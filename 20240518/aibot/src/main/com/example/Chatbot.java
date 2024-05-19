package com.example;

import com.example.AnswerUtil;
import com.example.FileUtil;
import com.example.FindSentences;
import com.example.VideoSplitter;
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
                // System.out.println("Please provide a question as an argument.");
                // return;
                // }

                // String question = args[0];

                String projectId = "delta-coil-423603-j2";
                String location = "us-central1";
                String modelName = "gemini-1.5-pro-preview-0409"; // gemini-1.5-pro-preview-0409 //
                                                                  // gemini-1.0-pro-002
                String filePath = "train/transcriptions.txt";
                String defaultReply = "1. the speech text data format is sentenceId|sentence. 2. You MUST return ALL the referred sentences you used to generate the answer based on the original speech text data. The referred sentences should be return in a list format named referList, prefixed with `referList` 3. you should also return the sentenceId in the list format of the referList, named idList";
                String textPrompt = FileUtil.readFromTxt(filePath);
                String question = "what does the speech text tell us? ";
                String output = textInput(projectId, location, modelName, question + defaultReply + textPrompt);
                // System.out.println("this is the answer: " + output);

                // process output
                List<List<String>> result = AnswerUtil.extractAllLists(output);
                System.out.println("Extracted Lists:" + result.isEmpty());
                for (List<String> list : result) {
                        List<String> resultList = AnswerUtil.extractSentenceIds(list);
                        System.out.println(resultList);
                }
                System.out.println("finish!");
        }

        // Passes the provided text input to the Gemini model and returns the text-only
        // response.
        public static String textInput(
                        String projectId, String location, String modelName, String textPrompt)
                        throws IOException {
                // This client only needs to be created once, and can be reused for multiple
                // requests.
                try (VertexAI vertexAI = new VertexAI(projectId, location)) {
                        String output;

                        GenerationConfig generationConfig = GenerationConfig.newBuilder()
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
                                                        .build());

                        GenerativeModel model = new GenerativeModel(modelName, vertexAI)
                                        .withGenerationConfig(generationConfig)
                                        .withSafetySettings(safetySettings);

                        GenerateContentResponse response = model.generateContent(textPrompt);
                        output = ResponseHandler.getText(response);

                        return output;
                }
        }

}