package com.example;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.PartMaker;
import java.io.IOException;

public class Quickstart {

          public static void main(String[] args) throws IOException {
                    // TODO(developer): Replace these variables before running the sample.
                    String projectId = "delta-coil-423603-j2";
                    String location = "us-central1";
                    String modelName = "gemini-1.5-pro-preview-0409"; // gemini-1.5-pro-preview-0409 //
                                                                      // gemini-1.0-pro-002

                    String output = quickstart(projectId, location, modelName);
                    System.out.println("output: " + output);
          }

          // Analyzes the provided Multimodal input.
          public static String quickstart(String projectId, String location, String modelName)
                              throws IOException {
                    // Initialize client that will be used to send requests. This client only needs
                    // to be created once, and can be reused for multiple requests.
                    try (VertexAI vertexAI = new VertexAI(projectId, location)) {
                              String imageUri = "gs://cloud-samples-data/vertex-ai/llm/prompts/landmark1.png";

                              GenerativeModel model = new GenerativeModel(modelName, vertexAI);
                              GenerateContentResponse response = model.generateContent(ContentMaker.fromMultiModalData(
                                                  PartMaker.fromMimeTypeAndData("image/png", imageUri),
                                                  "What's in this photo"));

                              return response.toString();
                    }
          }
}