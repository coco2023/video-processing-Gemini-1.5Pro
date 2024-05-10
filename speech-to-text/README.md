# 视频预处理
- 使用4K Video将视频下载到本地
- 使用ffmpeg将mp4 转成 .wav 和 .aac 形式
- 对视频进行切片
- 将切片的.wav视频上传至google cloud storag
```
D:\Application\WorkApp\ffmpeg\ffmpeg-2024\bin\ffmpeg -i video1.aac -acodec pcm_s16le -ac 1 -ar 16000 video1.wav
set GOOGLE_APPLICATION_CREDENTIALS=D:\1Learn\2022CVProject\20230222CogentTrain\20230612Java_Interview\videos-processing\speech-to-text\videos\woven-sequence-422021-4dd531c27e17.json
setx GOOGLE_APPLICATION_CREDENTIALS "D:\1Learn\2022CVProject\20230222CogentTrain\20230612Java_Interview\videos-processing\speech-to-text\videos\woven-sequence-422021-4dd531c27e17.json"
python transcribe.py
D:\Application\WorkApp\ffmpeg\ffmpeg-2024\bin\ffmpeg -i video1.wav -f segment -segment_time 300 -c copy video1v%03d.wav
D:\Application\WorkApp\ffmpeg\ffmpeg-2024\bin\ffmpeg -i video2.aac -acodec pcm_s16le -ac 1 -ar 16000 video2.wav
D:\Application\WorkApp\ffmpeg\ffmpeg-2024\bin\ffmpeg -i video2.wav -f segment -segment_time 300 -c copy video2v%03d.wav

python run.py
```

# 使用Google Cloud and Speech-to-Text API对视频-文本进行转换
Setting up and using the Google Speech-to-Text API to save captions with timestamps from a video involves several steps. I'll guide you through each one, including setting up the API and modifying the Python script to save the output to a file instead of just printing it.

### Step 1: Set Up Google Cloud and Speech-to-Text API

1. **Create a Google Cloud Account**:
   - Go to the [Google Cloud website](https://cloud.google.com/) and sign up or log in.
   - You may need to enter billing information, as Google Cloud's API services are typically pay-as-you-go.

2. **Create a Project**:
   - In your Google Cloud Console, create a new project.

3. **Enable the Speech-to-Text API**:
   - Navigate to the "API & Services" dashboard.
   - Click on "Enable APIs and Services".
   - Search for "Speech-to-Text API", select it, and click "Enable".

4. **Set Up Authentication**:
   - In the API & Services dashboard, go to the "Credentials" section.
   - Click "Create Credentials" and select "Service account".
   - Follow the steps to create a service account. Download the JSON key file at the end of this process.
   - Set the environment variable `GOOGLE_APPLICATION_CREDENTIALS` to the path of the JSON key file. This allows your script to authenticate with the Google Cloud Services. On Windows, you can set this environment variable via Command Prompt:
     ```bash
     set GOOGLE_APPLICATION_CREDENTIALS=[PATH_TO_YOUR_JSON_FILE]
     ```

### Step 2: Modify the Python Script to Save Output

Modify the existing script to write the transcript and timestamps to a file:

```python
from google.cloud import speech

# Initialize the Google speech client
client = speech.SpeechClient()

# Read the audio file
with open("output_audio.wav", "rb") as audio_file:
    content = audio_file.read()

audio = speech.RecognitionAudio(content=content)
config = speech.RecognitionConfig(
    encoding=speech.RecognitionConfig.AudioEncoding.LINEAR16,
    sample_rate_hertz=16000,
    language_code="en-US",
    enable_word_time_offsets=True
)

response = client.recognize(config=config, audio=audio)

# Open a file to save the output
with open("transcript_with_timestamps.txt", "w") as file:
    for result in response.results:
        for alternative in result.alternatives:
            file.write("Transcript: {}\n".format(alternative.transcript))
            for word_info in alternative.words:
                start_time = word_info.start_time
                file.write(f'Word: {word_info.word}, start time: {start_time.seconds}.{start_time.nanos}\n')

print("Transcription and timestamps have been saved to transcript_with_timestamps.txt")
```

This script extracts the audio, sends it to the Google Speech-to-Text API, and saves the transcribed text along with timestamps to a file named `transcript_with_timestamps.txt`.

### Running the Script
To run the script:
- Make sure you have `ffmpeg`, `python`, and `google-cloud-speech` installed and configured.
- Place your video file in the same directory as your script, or modify the script paths accordingly.
- Execute the script from your command line.

If you need help at any step, feel free to ask!