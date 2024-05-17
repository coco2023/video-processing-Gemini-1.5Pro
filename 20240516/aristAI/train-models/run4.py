import pandas as pd
from langchain.llms import Gemini
from langchain.chains import RetrievalQAWithSources
from langchain.embeddings.openai import OpenAIEmbeddings
from langchain.vectorstores import Chroma  # No change here
from langchain.document_loaders import JSONLoader

# 1. Data Loading and Preprocessing

import json

def read_json_data(json_file_path):
    """Reads data from a JSON file with the format [{'key1': 'value1', 'key2': 'value2'}, ...]

    Args:
        json_file_path (str): The path to the JSON file.

    Returns:
        list: A list of dictionaries, where each dictionary represents a sentence and has the keys "sentenceId" and "sentence".
    """

    with open(json_file_path, 'r', encoding='utf-8') as f:
        data = json.load(f)

    # Extract sentenceId and sentence from each dictionary in the list
    sentences = []
    for item in data:
        for key, value in item.items():  # Iterate through key-value pairs in each dictionary
            sentence_id, sentence = key, value
            sentences.append({"sentenceId": sentence_id, "sentence": sentence})

    return sentences

# Example usage:
json_file_path = 'data.json'  # Replace with the actual path to your JSON file
sentences = read_json_data(json_file_path)

# Print the sentences (optional)
for sentence in sentences:
    print(f"Sentence ID: {sentence['sentenceId']}, Sentence: {sentence['sentence']}")

# 2. Vector Database Creation

embeddings = OpenAIEmbeddings(api_key='OPENAI_KEY')  # Replace with your actual API key
vectorstore = Chroma.from_documents(processed_data, embeddings, persist_directory='your_data_directory')

# 3. Retrieval QA Chain with Source Tracking

llm = Gemini(model_name='gemini', temperature=0.2)

chain = RetrievalQAWithSources.from_chain_type(
    llm=llm,
    chain_type="stuff",
    retriever=vectorstore.as_retriever(),
)

# 4. Querying and Result Display

query = "How can Federal Reserve help people?"
result = chain.run(query)

print(result)
print("Sources:")
for source in result['sources']:
    print(source)

# 5. Example: Correlation Analysis

query = "How can Federal Reserve help people?"
result = chain.run(query)
print(result)
print("Sources:")
for source in result['sources']:
    print(source)
