from google.cloud import speech_v1
from google.cloud import speech
from google.oauth2 import service_account
from datetime import datetime
from datetime import timedelta
import mysql.connector
import json

audio_uris = {
    "gs://video1-20240502/video1v000.wav",
    "gs://video1-20240502/video1v001.wav",
 }

# Initialize a dictionary to store sentences with their timestamps for each video
all_sentences_with_timestamps = {}

# Connect to MySQL database
db_connection = mysql.connector.connect(
    host="localhost",
    user="root",
    password="12345",
    database="youtube_data"
)

# Create cursor
cursor = db_connection.cursor()

# Define the table schema
table_schema = """
CREATE TABLE IF NOT EXISTS kv_storage (
    id INT AUTO_INCREMENT PRIMARY KEY,
    video_id VARCHAR(255),
    timestamp DATETIME,
    caption TEXT
)
"""
cursor.execute(table_schema)

# Provide the path to the service account key file
credentials = service_account.Credentials.from_service_account_file(
    './videos/woven-sequence-422021-4dd531c27e17.json'
)

# Initialize the Google speech client
client = speech_v1.SpeechClient(credentials=credentials)

# Process each audio URI
for index, audio_uri in enumerate(audio_uris):
    audio = {"uri": audio_uri}
    config = {
        "encoding": speech_v1.RecognitionConfig.AudioEncoding.LINEAR16,
        "sample_rate_hertz": 16000,
        "language_code": "en-US",
        "enable_word_time_offsets": True
    }

    operation = client.long_running_recognize(config=config, audio=audio)
    print(f'Waiting for operation for audio {index+1} to complete...')
    response = operation.result()

    # Initialize current time for timestamp generation
    current_time = datetime(2014, 4, 6, 0, 0, 0)

    # Process the response
    sentences_with_timestamps = {}

    # Process the response
    for result in response.results:
        last_caption_start_time = None  # Initialize the start time of the last caption
        for alternative in result.alternatives:
            sentence = alternative.transcript.strip()

            current_caption_start_time = alternative.words[0].start_time.seconds + alternative.words[0].start_time.microseconds / 1e6
            # Calculate the duration
            if last_caption_start_time is not None:
                duration = current_caption_start_time - last_caption_start_time
            else:
                duration = 0  # Set duration to 0 for the first caption

          #   duration = len(sentence.split()) * 0.01  # Estimate duration based on the number of words
            timestamp_str = current_time.strftime("%Y-%m-%dT%H:%M:%S")  # Format the timestamp
          #   current_time += timedelta(seconds=duration)  # Update current time
            key = f"Video{index+1}_{duration}_{timestamp_str}"
            sentences_with_timestamps[key] = sentence  # Store the sentence with its timestamp
            last_caption_start_time = current_caption_start_time

    # Update all sentences with timestamps
    all_sentences_with_timestamps.update(sentences_with_timestamps)

# Writing JSON data
with open('data_file_duration.json', 'w') as file:
    json.dump(all_sentences_with_timestamps, file)

# # Save the output to MySQL database
# for key, value in all_sentences_with_timestamps.items():
#     video_id, timestamp = key.split("_")
#     insert_query = "INSERT INTO kv_storage (video_id, timestamp, caption) VALUES (%s, %s, %s)"
#     insert_data = (key, timestamp, value)
#     cursor.execute(insert_query, insert_data)
#     db_connection.commit()

# # Close cursor and database connection
# cursor.close()
# db_connection.close()
