package com.example;

import com.example.FindSentences;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.api.HarmCategory;
import com.google.cloud.vertexai.api.GenerationConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Chatbot {

          public static void main(String[] args) throws IOException {
                    if (args.length == 0) {
                        System.out.println("Please provide a question as an argument.");
                        return;
                    }
            
                    String question = args[0];

                    // TODO(developer): Replace these variables before running the sample.
                    String projectId = "delta-coil-423603-j2";
                    String location = "us-central1";
                    String modelName = "gemini-1.5-pro-preview-0409"; // gemini-1.5-pro-preview-0409 //
                                                                      // gemini-1.0-pro-002
                    String filePath = "train/transcriptions.txt";
                    String defaultReply = " please Return the referred sentence in origin data in a list format and begin with the name: referList.";
                    String textPrompt = readFromTxt(filePath);
                    // String question = "what does the txt tell us? ";
                    String output = textInput(projectId, location, modelName, question + textPrompt);
                    // System.out.println("this is the answer: " + output);

                    // process output
                    List<String> referList = FindSentences.extractReferList(output);   
                    System.out.println(referList);                
                    // match transcript
                    List<String> resultKey = FindSentences.matchTranscript(referList);
                    System.out.println(resultKey);
                    // extract the video info
                    List<com.example.FindSentences.KeyComponents> videoInfo = FindSentences.extraKeyComponents(resultKey);
                    videoInfo.forEach((n) -> System.out.println(n));
          }

          // Passes the provided text input to the Gemini model and returns the text-only
          // response.
          // For the specified textPrompt, the model returns a list of possible store
          // names.
          public static String textInput(
                              String projectId, String location, String modelName, String textPrompt)
                              throws IOException {
                    // Initialize client that will be used to send requests. This client only needs
                    // to be created once, and can be reused for multiple requests.
                    try (VertexAI vertexAI = new VertexAI(projectId, location)) {
                              // StringBuilder output = new StringBuilder();
                              String output;

                              GenerationConfig generationConfig =
                              GenerationConfig.newBuilder()
                                  .setMaxOutputTokens(2048)
                                  .setTemperature(0.4F)
                                  .setTopK(32)
                                  .setTopP(1)
                                  .build();
                    
                                    List<SafetySetting> safetySettings = Arrays.asList(
          SafetySetting.newBuilder()
              .setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
              .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
              .build(),
              SafetySetting.newBuilder()
              .setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
              .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
              .build(),
              SafetySetting.newBuilder()
              .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
              .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
              .build(),
              SafetySetting.newBuilder()
              .setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
              .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
              .build()           
              );

              GenerativeModel model = new GenerativeModel(modelName, vertexAI)
              .withGenerationConfig(generationConfig)
              .withSafetySettings(safetySettings);
                                  // Set safety settings
                              
                              // model.setSafetySettings(Collections.singletonList(
                              //                     SafetySetting.newBuilder() // Use the correct class name
                              //                                         .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
                              //                                         .build()));

                              GenerateContentResponse response = model.generateContent(textPrompt);
                              output = ResponseHandler.getText(response);
                              // output.append(response).append("\n");

                              return output;
                    }
          }

          public static String readFromTxt(String filePath) throws IOException {
                    return FileUtil.readFileAsString(filePath);
          }
            
}