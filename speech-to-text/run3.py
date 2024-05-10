from google.cloud import speech_v1
from google.oauth2 import service_account
import mysql.connector
from datetime import datetime, timezone
import math

def connect_to_database(host, user, password, database):
    return mysql.connector.connect(
        host=host,
        user=user,
        password=password,
        database=database
    )

def create_table(cursor):
    table_schema = """
    CREATE TABLE IF NOT EXISTS kv_storage (
        id INT AUTO_INCREMENT PRIMARY KEY,
        video_id VARCHAR(255),
        timestamp DATETIME,
        caption TEXT
    )
    """
    cursor.execute(table_schema)

def initialize_speech_client(credentials_path):
    credentials = service_account.Credentials.from_service_account_file(credentials_path)
    return speech_v1.SpeechClient(credentials=credentials)

def calculate_timestamp(start_time, word_count, sample_rate):
    # Calculate the duration of the caption in seconds
    duration_seconds = word_count / sample_rate
    # Increment the start time by the duration
    end_time = start_time + datetime.timedelta(seconds=duration_seconds)
    return end_time

def process_audio_uris(audio_uris, client):
    all_sentences_with_timestamps = {}
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

        sentences_with_timestamps = {}

        for result in response.results:
            for word_info in result.alternatives[0].words:
                word = word_info.word
                start_time = word_info.start_time.total_seconds()
                if not start_time:
                    continue
                end_time = start_time + (len(word.split()) / 16000)
                timestamp_str = datetime.fromtimestamp(start_time, tz=timezone.utc).strftime('%Y-%m-%dT%H:%M:%S')
                key = f"Video{index+1}_{timestamp_str}"
                if key in sentences_with_timestamps:
                    sentences_with_timestamps[key] += " " + word
                else:
                    sentences_with_timestamps[key] = word

        all_sentences_with_timestamps.update(sentences_with_timestamps)

    return all_sentences_with_timestamps

def save_to_database(data, cursor):
    for key, value in data.items():
        video_id = key
        timestamp = key.split("_")[1]  # Extract timestamp from the key
        insert_query = "INSERT INTO kv_storage (video_id, timestamp, caption) VALUES (%s, %s, %s)"
        insert_data = (video_id, timestamp, value)
        cursor.execute(insert_query, insert_data)

# Database connection details
host = "localhost"
user = "root"
password = "12345"
database = "youtube_data"

# Audio URIs
audio_uris = [
    "gs://video1-20240502/video1v000.wav",
    "gs://video1-20240502/video1v001.wav",
]

# Path to service account credentials file
credentials_path = './videos/woven-sequence-422021-4dd531c27e17.json'

# Connect to database
db_connection = connect_to_database(host, user, password, database)
cursor = db_connection.cursor()

# Create table if not exists
create_table(cursor)

# Initialize Speech client
client = initialize_speech_client(credentials_path)

# Process audio URIs
all_data = process_audio_uris(audio_uris, client)

# Save data to database
save_to_database(all_data, cursor)

# Commit changes and close connections
db_connection.commit()
cursor.close()
db_connection.close()
