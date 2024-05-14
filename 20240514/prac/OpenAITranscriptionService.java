// package com.example;

// import io.github.cdimascio.dotenv.Dotenv;
// import org.apache.http.HttpEntity;
// import org.apache.http.client.methods.CloseableHttpResponse;
// import org.apache.http.client.methods.HttpPost;
// import org.apache.http.entity.ContentType;
// import org.apache.http.entity.mime.MultipartEntityBuilder;
// import org.apache.http.entity.mime.content.FileBody;
// import org.apache.http.entity.mime.content.StringBody;
// import org.apache.http.impl.client.CloseableHttpClient;
// import org.apache.http.impl.client.HttpClients;
// import org.apache.http.util.EntityUtils;
// import org.json.JSONArray;
// import org.json.JSONObject;

// import java.io.File;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.nio.charset.StandardCharsets;
// import java.util.HashMap;
// import java.util.Map;

// public class OpenAITranscriptionService {
//           private static final Dotenv dotenv = Dotenv.load();
//           private static final String OPENAI_API_KEY = dotenv.get("OPENAI_API_KEY");

//           public static void main(String[] args) {
//                     String filePath = "outputs/videos/_4t62i_OUZ8#1#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f.mp4";
//                     String outputFilePath = "captions.json";
//                     try {
//                               Map<String, String> captions = getCaptionsWithTimestamps(filePath);
//                               saveCaptionsToJsonFile(captions, outputFilePath);
//                               captions.forEach((timestamp, caption) -> {
//                                         System.out.println("[" + timestamp + "] " + caption);
//                               });
//                     } catch (IOException e) {
//                               e.printStackTrace();
//                     }
//           }

//           public static Map<String, String> getCaptionsWithTimestamps(String filePath) throws IOException {
//                     File file = new File(filePath);

//                     try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//                               HttpPost uploadFile = new HttpPost("https://api.openai.com/v1/audio/transcriptions");
//                               uploadFile.setHeader("Authorization", "Bearer " + OPENAI_API_KEY);

//                               FileBody fileBody = new FileBody(file);
//                               StringBody model = new StringBody("whisper-1", ContentType.TEXT_PLAIN);
//                               StringBody responseFormat = new StringBody("verbose_json", ContentType.TEXT_PLAIN);
//                               StringBody timestampGranularities = new StringBody("segment", ContentType.TEXT_PLAIN);

//                               HttpEntity entity = MultipartEntityBuilder.create()
//                                                   .addPart("file", fileBody)
//                                                   .addPart("model", model)
//                                                   .addPart("response_format", responseFormat)
//                                                   .addPart("timestamp_granularities[]", timestampGranularities)
//                                                   .build();

//                               uploadFile.setEntity(entity);

//                               try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
//                                         HttpEntity responseEntity = response.getEntity();
//                                         if (responseEntity != null) {
//                                                   String responseBody = EntityUtils.toString(responseEntity,
//                                                                       StandardCharsets.UTF_8);
//                                                   return parseTranscriptionResponse(responseBody);
//                                         }
//                               }
//                     }
//                     return new HashMap<>();
//           }

//           private static Map<String, String> parseTranscriptionResponse(String responseBody) {
//                     Map<String, String> captions = new HashMap<>();

//                     JSONObject jsonObject = new JSONObject(responseBody);
                    
//                     // Log the response structure
//                     System.out.println("JSON Response: " + jsonObject.toString(4));
//                     if (!jsonObject.has("segments")) {
//                               System.out.println("No 'segments' field found in the response.");
//                               return captions;
//                     }
                  
//                     JSONArray segments = jsonObject.getJSONArray("segments");

//                     for (int i = 0; i < segments.length(); i++) {
//                               JSONObject segment = segments.getJSONObject(i);
//                               double start = segment.getDouble("start");
//                               String text = segment.getString("text");

//                               String timestamp = String.format("%02d:%02d:%02d",
//                                                   (int) (start / 3600),
//                                                   (int) (start % 3600) / 60,
//                                                   (int) (start % 60));

//                               captions.put(timestamp, text);
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
