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
                    // if (args.length == 0) {
                    //     System.out.println("Please provide a question as an argument.");
                    //     return;
                    // }
            
                    // String question = args[0];

                    // TODO(developer): Replace these variables before running the sample.
                    String projectId = "delta-coil-423603-j2";
                    String location = "us-central1";
                    String modelName = "gemini-1.5-pro-preview-0409"; // gemini-1.5-pro-preview-0409 //
                                                                      // gemini-1.0-pro-002
                    String filePath = "train/transcriptions.txt";
                    // String defaultReply = " please Return the referred informatoion from origin data you used to generate the answer. the return should be in a list format which begin with the prefix: referList=";
                    // String defaultReply = "You must: 1. Return the referred sentence from the original data; 2. Return the original sentence in a list format named `referList`. 3. The `referList` should have a prefix: `referList=`";
                    String defaultReply = "1. the origin text format is sentenceId|sentence. 2. You MUST return ALL the referred sentences you use to generate the answer based on the original speech text data. The referred sentences should be return in a list format named referList, prefixed with `referList` 3. you should also return the sentenceId in the list format of the referList, named idList";
                    String textPrompt = readFromTxt(filePath);
                    String question = "what does the speech text tell us? ";
                    String output = textInput(projectId, location, modelName, question + defaultReply + textPrompt);
                    // System.out.println("this is the answer: " + output);

                    // process output
                    List<List<String>> result = AnswerUtil.extractAllLists(output);
                    System.out.println("Extracted Lists:");
                    for (List<String>  list : result) {
                        System.out.println(list);
                    }

                    // List<String> referList = FindSentences.extractReferList(output);   
                    // System.out.println(referList);                
                    // // match transcript
                    // List<String> resultKey = FindSentences.matchTranscript(referList);
                    // System.out.println(resultKey);
                    // // extract the video info
                    // List<com.example.FindSentences.KeyComponents> videoInfo = FindSentences.extraKeyComponents(resultKey);
                    // videoInfo.forEach((n) -> System.out.println(n));
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