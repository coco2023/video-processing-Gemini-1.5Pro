import json
from transformers import BartTokenizer, BartForConditionalGeneration
import torch

# Reading JSON data
with open('data_file.json', 'r') as file:
    all_sentences_with_timestamps = json.load(file)

print("success!")

# Load pre-trained BART tokenizer and model
tokenizer = BartTokenizer.from_pretrained('facebook/bart-large-cnn')
model = BartForConditionalGeneration.from_pretrained('facebook/bart-large-cnn')

# Define a function to generate summaries
def generate_summary(text):
    inputs = tokenizer([text], max_length=1024, return_tensors='pt', truncation=True)
    summary_ids = model.generate(inputs['input_ids'], num_beams=4, min_length=30, max_length=200, early_stopping=True)
    summary = tokenizer.decode(summary_ids[0], skip_special_tokens=True)
    return summary

# Summarize each caption in all_sentences_with_timestamps
summarized_captions = {}
for timestamp, caption in all_sentences_with_timestamps.items():
    summarized_caption = generate_summary(caption)
    summarized_captions[timestamp] = summarized_caption

# Store the summarized captions with their corresponding timestamps
# You can store them in a database, write to a file, or use them as needed

# Writing JSON data
with open('summarized.json', 'w') as file:
    json.dump(summarized_captions, file)



import json

# Mount Google Drive (if necessary)
from google.colab import drive
drive.mount('/content/drive')

# Define the path to your JSON file
json_file_path = '/content/drive/My Drive/data.json'  # Update the path accordingly

# Read the JSON data from the file
with open(json_file_path, 'r') as file:
    data = json.load(file)

# Now you can access the data as a Python dictionary
# For example, if your JSON file looks like {"key": "value"}, you can access the value like this:
value = data['key']
print(value)
