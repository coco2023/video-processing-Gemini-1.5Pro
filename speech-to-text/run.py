from google.cloud import speech_v1
from google.cloud import speech
from google.oauth2 import service_account
from datetime import datetime
from datetime import timedelta

# Provide the path to the service account key file
credentials = service_account.Credentials.from_service_account_file(
    './videos/woven-sequence-422021-4dd531c27e17.json'
)

# Initialize the Google speech client
client = speech_v1.SpeechClient(credentials=credentials)

# Provide the GCS URI of your audio file
audio_uri = "gs://video1-20240502/out001.wav"
audio_uris = {
    "gs://video1-20240502/video1v000.wav",
    "gs://video1-20240502/video1v001.wav",
 }

audio = {"uri": audio_uri}
config = {
    "encoding": speech_v1.RecognitionConfig.AudioEncoding.LINEAR16,
    "sample_rate_hertz": 16000,
    "language_code": "en-US",
    "enable_word_time_offsets": True
}

operation = client.long_running_recognize(config=config, audio=audio)
response = operation.result()

# Initialize a dictionary to store sentences with their timestamps
sentences_with_timestamps = {}
# Initialize a dictionary to store sentences with their timestamps for each video
all_sentences_with_timestamps = {}

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

    # Initialize a dictionary to store sentences with their timestamps for the current video
    sentences_with_timestamps = {}

    # Initialize variables to store the current sentence and its start time
    current_sentence = ""
    start_time = None

    # Initialize current time for timestamp generation
    current_time = datetime(2014, 4, 6, 0, 0, 0)

    # Process the response
    for result in response.results:
        for alternative in result.alternatives:
            sentence = alternative.transcript.strip()
            duration = len(sentence.split()) * 0.01  # Estimate duration based on the number of words
            timestamp_str = current_time.strftime("%Y-%m-%dT%H:%M:%S")  # Format the timestamp
            current_time += timedelta(seconds=duration)  # Update current time
            sentences_with_timestamps[timestamp_str] = sentence

    # Store the sentences and timestamps for the current video
    all_sentences_with_timestamps[f"Video {index+1}"] = sentences_with_timestamps

# Save the output to a file
with open("transcript_with_timestamps7.txt", "w") as file:
    for video, sentences_with_timestamps in all_sentences_with_timestamps.items():
        for timestamp, sentence in sentences_with_timestamps.items():
            file.write(f"Video: {video}, Timestamp: {timestamp}, Caption: {sentence}\n")

# # useful for getting paraph timestamp
# # Initialize variables to store the current sentence and its start time
# current_sentence = ""
# start_time = None
# 
# # Open a file to save the output
# with open("transcript_with_timestamps_video1v000.txt", "w") as file:
#     current_time = datetime(2014, 4, 6, 0, 0, 0)  # Initialize current time as April 6, 2014, 00:00:00
#     for result in response.results:
#         for alternative in result.alternatives:
#             sentence = alternative.transcript.strip()
#             duration = len(sentence.split()) * 0.01  # Estimate duration based on the number of words
#             timestamp_str = current_time.strftime("%Y-%m-%dT%H:%M:%S")  # Format the timestamp
#             current_time += timedelta(seconds=duration)  # Update current time
#             file.write(f"Timestamp: {timestamp_str}, Caption: {sentence}\n")
#             # start_time = alternative.words[0].start_time  # Get the start time of the sentence
#             # timestamp = f"{start_time.seconds}.{start_time.microseconds // 1000}"  # Format the timestamp
#             # file.write(f"Timestamp: {timestamp}, Caption: {sentence}\n")

# # Iterate through the results and alternatives
# for result in response.results:
#     for alternative in result.alternatives:
#         for word_info in alternative.words:
#             word = word_info.word
#             if word.endswith((".", "!", "?")):  # Check if the word ends with a punctuation mark
#                 # If a punctuation mark is encountered, add the word to the current sentence
#                 current_sentence += word + " "
#                 # Store the current sentence along with its start time in the dictionary
#                 if start_time is not None:
#                     sentences_with_timestamps[f"{start_time.seconds}.{start_time.microseconds // 1000}"] = current_sentence.strip()
#                 # Reset the current sentence and start time for the next sentence
#                 current_sentence = ""
#                 start_time = None
#             else:
#                 # Add the word to the current sentence
#                 if current_sentence == "":
#                     # Store the start time of the sentence
#                     start_time = word_info.start_time
#                 current_sentence += word + " "

# # Write the sentences with timestamps to a file
# with open("transcript_with_timestamps3.txt", "w") as file:
#     for timestamp, sentence in sentences_with_timestamps.items():
#         file.write(f"Timestamp: {timestamp}, Caption: {sentence}\n")

# # Open a file to save the output
# with open("transcript_with_timestamps_sentence2.txt", "w") as file:
#     sentence = ""  # Initialize an empty string to accumulate words into a sentence
#     start_time = None  # Initialize start time for the current sentence
#     for result in response.results:
#         for alternative in result.alternatives:
#             for word_info in alternative.words:
#                 word = word_info.word
#                 start_time = word_info.start_time
#                 if word.endswith((".", "!", "?")):  # Check if the word ends with a punctuation mark
#                     # If a punctuation mark is encountered, add the word to the sentence
#                     sentence += word + " "
#                     # Write the sentence along with its start time to the file
#                     # file.write(f"Sentence: {sentence.strip()}, start time: {start_time.seconds}.{start_time.microseconds // 1000}\n")
#                     file.write(f'Sentence: {sentence}, start time: {start_time.seconds}.{start_time.microseconds // 1000}\n')
#                     # Reset the sentence and start time for the next sentence
#                     sentence = ""
#                     start_time = None
#                 else:
#                     # Add the word to the sentence
#                     if sentence == "":
#                         # Store the start time of the sentence
#                         start_time = word_info.start_time
#                     sentence += word + " "

# # Open a file to save the output
# with open("transcript_with_timestamps_sentence1.txt", "w") as file:
#     sentence = ""  # Initialize an empty string to accumulate words into a sentence
#     for result in response.results:
#         for alternative in result.alternatives:
#             for word_info in alternative.words:
#                 word = word_info.word
#                 start_time = word_info.start_time
#                 if word.endswith((".", "!", "?")):  # Check if the word ends with a punctuation mark
#                     # If a punctuation mark is encountered, add the word to the sentence and write it to the file
#                     sentence += word + " "
#                     # file.write("Sentence: {}\n".format(sentence))
#                     file.write(f'Sentence: {sentence}, start time: {start_time.seconds}.{start_time.microseconds // 1000}\n')
#                     sentence = ""  # Reset the sentence accumulator
#                 else:
#                     sentence += word + " "  # Add the word to the sentence

# # Open a file to save the output
# with open("transcript_with_timestamps.txt", "w") as file:
#     for result in response.results:
#         for alternative in result.alternatives:
#             file.write("Transcript: {}\n".format(alternative.transcript))
#             for word_info in alternative.words:
#                 start_time = word_info.start_time
#                 file.write(f'Word: {word_info.word}, start time: {start_time.seconds}.{start_time.microseconds // 1000}\n')

# print("Transcription and timestamps have been saved to transcript_with_timestamps.txt")

# from google.cloud import speech_v1
# from google.cloud import speech
# from google.oauth2 import service_account

# # Provide the path to the service account key file
# credentials = service_account.Credentials.from_service_account_file(
#     './videos/woven-sequence-422021-4dd531c27e17.json'
# )

# # Initialize the Google speech client
# client = speech_v1.SpeechClient(credentials=credentials)

# # Read the audio file from your local machine
# audio_file_path = "./videos/out001.wav"
# with open(audio_file_path, "rb") as audio_file:
#     content = audio_file.read()

# audio = {"content": content}
# config = {
#     "encoding": speech_v1.RecognitionConfig.AudioEncoding.LINEAR16,
#     "sample_rate_hertz": 16000,
#     "language_code": "en-US",
#     "enable_word_time_offsets": True
# }

# operation = client.long_running_recognize(config=config, audio=audio)
# print('Waiting for operation to complete...')
# response = operation.result()

## useful for getting single words' timestamp
## Open a file to save the output
# with open("transcript_with_timestamps1.txt", "w") as file:
#     for result in response.results:
#         for alternative in result.alternatives:
#             file.write("Transcript: {}\n".format(alternative.transcript))
#             for word_info in alternative.words:
#                 start_time = word_info.start_time
#                 file.write(f'Word: {word_info.word}, start time: {start_time.seconds}.{start_time.nanos}\n')

# print("Transcription and timestamps have been saved to transcript_with_timestamps1.txt")


# from google.cloud import speech
# from google.oauth2 import service_account

# # Provide the path to the service account key file
# credentials = service_account.Credentials.from_service_account_file(
#     './videos/woven-sequence-422021-4dd531c27e17.json'
# )

# # Initialize the Google speech client
# client = speech.SpeechClient(credentials=credentials)

# with open("./videos/out001.wav", "rb") as audio_file:
#     content = audio_file.read()

# audio = speech.RecognitionAudio(content=content)
# config = speech.RecognitionConfig(
#     encoding=speech.RecognitionConfig.AudioEncoding.LINEAR16,
#     sample_rate_hertz=16000,
#     language_code="en-US",
#     enable_word_time_offsets=True
# )

# operation = client.long_running_recognize(config=config, audio=audio)
# print('Waiting for operation to complete...')
# response = operation.result()

# # Open a file to save the output
# with open("transcript_with_timestamps.txt", "w") as file:
#     for result in response.results:
#         for alternative in result.alternatives:
#             file.write("Transcript: {}\n".format(alternative.transcript))
#             for word_info in alternative.words:
#                 start_time = word_info.start_time
#                 file.write(f'Word: {word_info.word}, start time: {start_time.seconds}.{start_time.nanos}\n')

# print("Transcription and timestamps have been saved to transcript_with_timestamps.txt")

# # Read the audio file
# with open("./videos/out001.wav", "rb") as audio_file:
#     content = audio_file.read()

# audio = speech.RecognitionAudio(content=content)
# config = speech.RecognitionConfig(
#     encoding=speech.RecognitionConfig.AudioEncoding.LINEAR16,
#     sample_rate_hertz=16000,
#     language_code="en-US",
#     enable_word_time_offsets=True
# )

# response = client.recognize(config=config, audio=audio)

# # Open a file to save the output
# with open("transcript_with_timestamps.txt", "w") as file:
#     for result in response.results:
#         for alternative in result.alternatives:
#             file.write("Transcript: {}\n".format(alternative.transcript))
#             for word_info in alternative.words:
#                 start_time = word_info.start_time
#                 file.write(f'Word: {word_info.word}, start time: {start_time.seconds}.{start_time.nanos}\n')

# print("Transcription and timestamps have been saved to transcript_with_timestamps.txt")
