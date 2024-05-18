package com.example;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;

import java.io.FileInputStream;
import java.io.IOException;

public class TextInput {

  public static void main(String[] args) throws IOException {
    // TODO(developer): Replace these variables before running the sample.
    String projectId = "delta-coil-423603-j2";
    String location = "us-central1";
    String modelName = "gemini-1.0-pro-002";
    String textPrompt =
        "What's a good name for a flower shop that specializes in selling bouquets of"
            + " dried flowers?";

    String output = textInput(projectId, location, modelName, textPrompt);
    System.out.println(output);
  }

  // Passes the provided text input to the Gemini model and returns the text-only response.
  // For the specified textPrompt, the model returns a list of possible store names.
  public static String textInput(
      String projectId, String location, String modelName, String textPrompt) throws IOException {
    // Initialize client that will be used to send requests. This client only needs
    // to be created once, and can be reused for multiple requests.
    try (VertexAI vertexAI = new VertexAI(projectId, location)) {
          String output;
      GenerativeModel model = new GenerativeModel(modelName, vertexAI);

      GenerateContentResponse response = model.generateContent(textPrompt);
      output = ResponseHandler.getText(response);
      return output;
    }
  }
}