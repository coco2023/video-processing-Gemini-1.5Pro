import os
import pandas as pd
import nltk
from sqlalchemy import create_engine
from transformers import GPT2Tokenizer, GPT2LMHeadModel, Trainer, TrainingArguments, TextDataset, DataCollatorForLanguageModeling
from torch.utils.data import Dataset
import torch

# Ensure you have the necessary environment variables set
# Load environment variables from .env file
# from dotenv import load_dotenv
# load_dotenv()

# MySQL Database connection details
DB_HOST = 'localhost'
DB_USER = 'root'
DB_PASSWORD = '12345'
DB_NAME = 'youtube_data'

# Step 1: Connect to the MySQL database and retrieve data
def get_data_from_db():
    db_connection_str = f'mysql+mysqlconnector://{DB_USER}:{DB_PASSWORD}@{DB_HOST}/{DB_NAME}'
    db_connection = create_engine(db_connection_str)
    query = "SELECT id, sentence FROM transcriptions"
    df = pd.read_sql(query, db_connection)
    return df

# Step 2: Preprocess the data (Tokenization)
def preprocess_data(df):
    nltk.download('punkt')
    df['tokens'] = df['sentence'].apply(nltk.word_tokenize)
    return df

df = get_data_from_db()
df = preprocess_data(df)

# Prepare the training data
training_data = [{'id': row['id'], 'sentence': row['sentence']} for _, row in df.iterrows()]

# Save the sentences to a text file for training
with open("training_data.txt", "w", encoding="utf-8") as f:
    for data in training_data:
        f.write(f"{data['sentence']}\n")

# Custom Dataset class to keep track of sentence IDs
class CustomDataset(Dataset):
    def __init__(self, tokenizer, file_path, block_size=512):
        assert os.path.isfile(file_path)
        with open(file_path, encoding="utf-8") as f:
            lines = [line.strip() for line in f.readlines()]
        self.examples = tokenizer(lines, add_special_tokens=True, truncation=True, padding="max_length", max_length=block_size)["input_ids"]
        self.sentence_ids = [i for i in range(len(lines))]

    def __len__(self):
        return len(self.examples)

    def __getitem__(self, i):
        return torch.tensor(self.examples[i], dtype=torch.long), self.sentence_ids[i]

# Load the tokenizer and model
tokenizer = GPT2Tokenizer.from_pretrained('gpt2')
model = GPT2LMHeadModel.from_pretrained('gpt2')

# Create a dataset and data collator
dataset = CustomDataset(tokenizer, "training_data.txt")
data_collator = DataCollatorForLanguageModeling(tokenizer=tokenizer, mlm=False)

# Step 3: Fine-tune the model
training_args = TrainingArguments(
    output_dir="./results",
    overwrite_output_dir=True,
    num_train_epochs=1,
    per_device_train_batch_size=2,
    save_steps=10_000,
    save_total_limit=2,
)

trainer = Trainer(
    model=model,
    args=training_args,
    data_collator=data_collator,
    train_dataset=dataset,
)

trainer.train()

# Save the fine-tuned model
model.save_pretrained("./fine-tuned-gpt2")
tokenizer.save_pretrained("./fine-tuned-gpt2")

# Step 4: Generate responses and retrieve referred sentence IDs
class ResponseGenerator:
    def __init__(self, model, tokenizer, dataset):
        self.model = model
        self.tokenizer = tokenizer
        self.dataset = dataset

    def generate(self, prompt):
        inputs = self.tokenizer(prompt, return_tensors="pt")
        outputs = self.model.generate(**inputs, max_length=50, num_return_sequences=1)
        generated_text = self.tokenizer.decode(outputs[0], skip_special_tokens=True)

        referred_ids = self.match_generated_text_to_db(generated_text)
        return generated_text, referred_ids

    def match_generated_text_to_db(self, generated_text):
        referred_ids = []
        for example, sentence_id in zip(self.dataset.examples, self.dataset.sentence_ids):
            example_text = self.tokenizer.decode(example, skip_special_tokens=True)
            if example_text in generated_text:
                referred_ids.append(sentence_id)
        return referred_ids

# Load the fine-tuned model
model = GPT2LMHeadModel.from_pretrained("./fine-tuned-gpt2")
tokenizer = GPT2Tokenizer.from_pretrained("./fine-tuned-gpt2")

# Initialize the response generator
response_generator = ResponseGenerator(model, tokenizer, dataset)

# Generate a response
response, referred_ids = response_generator.generate("How can the Federal Reserve help people?")
print("Response:", response)
print("Referred Sentence IDs:", referred_ids)
