package com.example;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.PartMaker;
import com.google.cloud.vertexai.generativeai.ResponseStream;
import java.util.Arrays;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.AbstractDocument.Content;

public class ContentInput {

          private static final String IMAGE_URI = "gs://cloud-samples-data/vertex-ai/llm/prompts/landmark1.png";

          public static void main(String[] args) throws IOException {
                    // TODO(developer): Replace these variables before running the sample.
                    String projectId = "delta-coil-423603-j2";
                    String location = "us-central1";
                    String modelName = "gemini-1.5-pro-preview-0409"; // gemini-1.5-pro-preview-0409 //
                                                                      // gemini-1.0-pro-002
                    ContentInput(projectId, location, modelName);
          }

          // Passes the provided text input to the Gemini model and returns the text-only
          // response.
          // For the specified textPrompt, the model returns a list of possible store
          // names.
          public static void ContentInput(
                              String projectId, String location, String modelName)
                              throws IOException {
                    // Initialize client that will be used to send requests. This client only needs
                    // to be created once, and can be reused for multiple requests.
                    try (VertexAI vertexAI = new VertexAI(projectId, location)) {
                              String output;

                              GenerativeModel model = new GenerativeModel(modelName, vertexAI);
                              // List<Content> contents = new ArrayList<>();
                              // contents.add(ContentMaker
                              //                     .fromMultiModalData(
                              //                                         "What is this?",
                              //                                         PartMaker.fromMimeTypeAndData("image/jpeg",
                              //                                                             IMAGE_URI)));

                              // GenerateContentResponse response = model.generateContent(contents);
                              // output = ResponseHandler.getText(response);
                              // return output;
                              ResponseStream<GenerateContentResponse> stream =
                              model.generateContentStream(ContentMaker.fromMultiModalData(
                                "Please describe this image",
                                PartMaker.fromMimeTypeAndData("image/jpeg", IMAGE_URI)
                              ));
                    
                              System.out.println(stream);
                    }
          }
}