import os
import requests
import mysql.connector
import pandas as pd
import nltk
from dotenv import load_dotenv

# Ensure you have the necessary environment variables set
# You can set this in your environment or directly here for testing purposes (not recommended for production)
# Load environment variables from .env file
load_dotenv()

# Get the API key from environment variable
api_key = os.getenv('GEMINI_API_KEY')

if not api_key:
    raise ValueError("GEMINI_API_KEY not found in .env file")

# Step 1: Connect to the MySQL database and retrieve data
def get_data_from_db():
    db_connection = mysql.connector.connect(
        host="localhost",
        user="root",
        password="12345",
        database="youtube_data"
    )

    query = "SELECT id, sentence FROM transcriptions"
    df = pd.read_sql(query, db_connection)
    return df

# Step 2: Preprocess the data (Tokenization)
def preprocess_data(df):
    nltk.download('punkt')
    df['tokens'] = df['sentence'].apply(nltk.word_tokenize)
    return df

# Step 3: Fine-tune the model using the Gemini API
def fine_tune_model(api_key, training_data):
    url = "https://api.gemini.com/v1/fine-tune"  # Update this URL according to Gemini's API documentation
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json"
    }
    data = {
        "training_data": training_data,
        "model": "gemini-model",
        "num_epochs": 3
    }
    response = requests.post(url, headers=headers, json=data)
    return response.json()

# Step 4: Generate responses and retrieve referred sentence IDs
class GeminiTextGenerationPipeline:
    def __init__(self, api_key, database_df):
        self.api_key = api_key
        self.database_df = database_df

    def generate(self, prompt):
        url = "https://api.gemini.com/v1/generate"  # Update this URL according to Gemini's API documentation
        headers = {
            "Authorization": f"Bearer {self.api_key}",
            "Content-Type": "application/json"
        }
        data = {
            "model": "fine-tuned-gemini-model",
            "prompt": prompt
        }
        response = requests.post(url, headers=headers, json=data)
        generated_texts = response.json()["generated_text"]
        referred_ids = self.match_generated_texts_to_db(generated_texts)
        return generated_texts, referred_ids

    def match_generated_texts_to_db(self, generated_texts):
        referred_ids = []
        for text in generated_texts:
            for idx, sentence in enumerate(self.database_df['sentence']):
                if sentence in text:
                    referred_ids.append(self.database_df['id'][idx])
        return referred_ids

# Main function to run all steps
def main():
    # Step 1: Retrieve and preprocess data
    df = get_data_from_db()
    df = preprocess_data(df)

    # Step 2: Prepare training data
    training_data = [{'id': row['id'], 'sentence': row['sentence']} for _, row in df.iterrows()]

    # Step 3: Fine-tune the model
    fine_tune_response = fine_tune_model(api_key, training_data)
    print("Fine-tuning response:", fine_tune_response)

    # Step 4: Generate a response and retrieve referred sentence IDs
    pipeline = GeminiTextGenerationPipeline(api_key=api_key, database_df=df)
    response, referred_ids = pipeline.generate("How can Federal Reserve helo people?")
    print("Response:", response)
    print("Referred Sentence IDs:", referred_ids)

if __name__ == "__main__":
    main()
