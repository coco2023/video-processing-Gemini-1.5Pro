// package com.example;

// import com.google.cloud.speech.v1.RecognitionAudio;
// import com.google.cloud.speech.v1.RecognitionConfig;
// import com.google.cloud.speech.v1.RecognizeResponse;
// import com.google.cloud.speech.v1.SpeechClient;
// import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
// import com.google.cloud.speech.v1.SpeechRecognitionResult;
// import com.google.protobuf.ByteString;
// import org.json.JSONObject;
// import ws.schild.jave.encode.AudioAttributes;
// import ws.schild.jave.encode.EncodingAttributes;

// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// // refer: https://github.com/a-schild/jave2/blob/master/jave-core-test-java11/src/test/java/ws/schild/jave/EncoderTest.java
// public class GoogleAPITranscriptionService {
//           public static void main(String[] args) {
//                     String filePath = "outputs/videos/_4t62i_OUZ8#1#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f.mp4"; // path_to_your_mp4_file.mp4
//                     String outputFilePath = "captions.json";
//                     try {
//                               String wavFilePath = extractAudioFromMp4(filePath);
//                               Map<String, String> captions = getCaptionsWithTimestamps(wavFilePath);
//                               saveCaptionsToJsonFile(captions, outputFilePath);
//                               // captions.forEach((timestamp, caption) -> {
//                               // System.out.println("[" + timestamp + "] " + caption);
//                               // });
//                     } catch (Exception e) {
//                               e.printStackTrace();
//                     }
//           }

//           public static String extractAudioFromMp4(String videoPath) throws IOException {
//                     String audioPath = videoPath.replace(".mp4", ".wav");

//                     File source = new File(videoPath);
//                     File target = new File(audioPath);

//                     AudioAttributes audio = new AudioAttributes();
//                     audio.setCodec("pcm_s16le");
//                     audio.setChannels(1);
//                     audio.setSamplingRate(16000);

//                     EncodingAttributes attrs = new EncodingAttributes();
//                     attrs.setFormat("wav");
//                     attrs.setAudioAttributes(audio);

//                     Encoder encoder = new Encoder();
//                     encoder.encode(source, target, attrs);

//                     return audioPath;

//                     // FFmpeg ffmpeg = new FFmpeg("ffmpeg");
//                     // FFprobe ffprobe = new FFprobe("ffprobe");

//                     // FFmpegResult result = ffmpeg.addInput(FFmpeg.input(videoPath))
//                     // .addOutput(audioPath)
//                     // .setAudioCodec("pcm_s16le")
//                     // .setAudioChannels(1)
//                     // .setAudioSampleRate(16000)
//                     // .execute();

//                     // return audioPath;
//           }

//           public static Map<String, String> getCaptionsWithTimestamps(String filePath) throws Exception {
//                     Map<String, String> captions = new HashMap<>();

//                     // Speech-to-Text
//                     try (SpeechClient speechClient = SpeechClient.create()) {
//                               ByteString audioBytes = ByteString.readFrom(new FileInputStream(filePath));

//                               RecognitionConfig config = RecognitionConfig.newBuilder()
//                                                   .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
//                                                   .setSampleRateHertz(16000)
//                                                   .setLanguageCode("en-US")
//                                                   .enableWordTimeOffsets(true)
//                                                   .build();

//                               RecognitionAudio audio = RecognitionAudio.newBuilder()
//                                                   .setContent(audioBytes)
//                                                   .build();

//                               RecognizeResponse response = speechClient.recognize(config, audio);
//                               List<SpeechRecognitionResult> results = response.getResultsList();

//                               for (SpeechRecognitionResult result : results) {
//                                         for (SpeechRecognitionAlternative alternative : result.getAlternativesList()) {
//                                                   for (com.google.cloud.speech.v1.WordInfo wordInfo : alternative
//                                                                       .getWordsList()) {
//                                                             // double start = wordInfo.getStartTime().getNanos() / 1e9;
//                                                             double start = wordInfo.getStartTime().getSeconds()
//                                                             + wordInfo.getStartTime().getNanos() / 1e9;         
//                                                             String text = wordInfo.getWord();       

//                                                             String timestamp = String.format("%02d:%02d:%02d",
//                                                                                 (int) (start / 3600),
//                                                                                 (int) (start % 3600) / 60,
//                                                                                 (int) (start % 60));
//                                                             captions.put(timestamp, text);
//                                                   }
//                                         }
//                               }
//                     }

//                     return captions;
//           }

//           private static void saveCaptionsToJsonFile(Map<String, String> captions, String filePath) throws IOException {
//                     JSONObject jsonObject = new JSONObject(captions);
//                     try (FileWriter fileWriter = new FileWriter(filePath)) {
//                               fileWriter.write(jsonObject.toString(4)); // Indent with 4 spaces for readability
//                     }
//           }
// }
