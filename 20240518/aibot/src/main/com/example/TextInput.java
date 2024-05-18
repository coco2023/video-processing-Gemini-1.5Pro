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
    String modelName = "gemini-1.5-pro-preview-0409"; // gemini-1.5-pro-preview-0409 // gemini-1.0-pro-002
    String defaultReply = " Return the referred sentence/info of origin data in a list named: referList.";
    String textPrompt = "can you make conclusion for below text? also list the core information in a list[]"
        + "In this tutorial, we’ll examine the details of LangChain, a framework for developing applications powered by language models. We’ll begin by gathering basic concepts around the language models that will help in this tutorial.\r\n"
        + //
        "\r\n" + //
        "Although LangChain is primarily available in Python and JavaScript/TypeScript versions, there are options to use LangChain in Java. We’ll discuss the building blocks of LangChain as a framework and then proceed to experiment with them in Java.\r\n"
        + //
        "\r\n" + //
        "2. Background\r\n" + //
        "Before we go deeper into why we need a framework for building applications powered by language models, it’s imperative that we first understand what language models are. We’ll also cover some of the typical complexities encountered when working with language models.\r\n"
        + //
        "\r\n" + //
        "2.1. Large Language Models\r\n" + //
        "A language model is a probabilistic model of a natural language that can generate probabilities of a series of words. A large language model (LLM) is a language model characterized by its large size. They’re artificial neural networks with possibly billions of parameters.\r\n"
        + //
        "\r\n" + //
        "An LLM is often pre-trained on a vast amount of unlabeled data using self-supervised and semi-supervised learning techniques. Then, the pre-trained model is adapted for specific tasks using various techniques like fine-tuning and prompt engineering:\r\n"
        + //
        "\r\n" + //
        "";

    String output = textInput(projectId, location, modelName, textPrompt + defaultReply);
    System.out.println("this is the answer: " + output);
  }

  // Passes the provided text input to the Gemini model and returns the text-only
  // response.
  // For the specified textPrompt, the model returns a list of possible store
  // names.
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