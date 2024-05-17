# This Folder includes the process of Youtube Video processing and  Gemini 1.5 Training

# Aim PlayList
1. https://www.youtube.com/playlist?list=PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f 
2. https://www.youtube.com/watch?v=RePqojqhR2k&list=PL159CD41EB36CFE86
## Video process
1. Workflow: scraper -> download -> convert -> split -> upload -> transcript -> save
2. code path: [**Java + Python**](./aristAI/)

## Gemini Model Training
1. code Path: [**Colab**](https://colab.research.google.com/drive/1RnmL32MBv7s5ysyUOeBqlHvgM4JnAwwu?usp=sharing)

# Refer
1. https://chatgpt.com/g/g-WKIaLGGem-tech-support-advisor/c/4b5adde7-e7b9-4eac-aa5d-32db9ca6fdc5
2. https://chatgpt.com/g/g-WKIaLGGem-tech-support-advisor/c/8ab6a12b-25f1-4bfc-b05c-0ef7daa8c553
## Gemini
1. File API Quickstart: https://github.com/google-gemini/cookbook/blob/main/quickstarts/File_API.ipynb
2. Gemini 1.5 Pro: https://github.com/google-gemini/cookbook/blob/main/examples/Apollo_11.ipynb
code: [Pyhton: Apollo 11 transcript](./aristAI/train-models/)
3. GenerativeModel: https://ai.google.dev/api/python/google/generativeai/GenerativeModel
4. Vertex AI: https://cloud.google.com/vertex-ai/generative-ai/docs/multimodal/overview
5. Access Gemini Pro via the Gemini API in Google Cloud Vertex AI: https://cloud.google.com/vertex-ai?hl=en#extract-summarize-and-classify-data
6. Send requests to the Vertex AI API for Gemini: https://cloud.google.com/vertex-ai/generative-ai/docs/start/quickstarts/quickstart-multimodal#gemini-text-and-image-samples-python_vertex_ai_sdk
7. Overview of Generative AI on Vertex AI: https://cloud.google.com/vertex-ai/generative-ai/docs/learn/overview
8. Prompting with media files: https://ai.google.dev/gemini-api/docs/prompting_with_media?lang=python
9.  feedbackTutorial: Get started with the Gemini API: https://ai.google.dev/gemini-api/docs/get-started/tutorial?lang=python#prerequisites
10. Gemini API Overview: https://ai.google.dev/gemini-api/docs/api-overview

11. https://ai.google.dev/api/python/google/generativeai/GenerativeModel
12. https://github.com/google-gemini/generative-ai-python/blob/v0.5.3/google/generativeai/generative_models.py#L24-L400
13. https://cloud.google.com/vertex-ai?hl=en#extract-summarize-and-classify-data
14. Java, Vertex AI API: https://cloud.google.com/vertex-ai/generative-ai/docs/start/quickstarts/quickstart-multimodal#gemini-setup-environment-java
15. Prompting: https://cloud.google.com/vertex-ai/generative-ai/docs/learn/prompts/introduction-prompt-design
16. File API: https://github.com/google-gemini/cookbook/blob/main/quickstarts/File_API.ipynb
17. Prompting with media files: https://ai.google.dev/gemini-api/docs/prompting_with_media?lang=python
18. Custom training overview : https://cloud.google.com/vertex-ai/docs/training/overview
19. Usage: https://cloud.google.com/bigquery?hl=en#data-warehouse-migration

# sequence diagram of Video Processing
refer: https://chatgpt.com/g/g-5QhhdsfDj-diagrams-show-me-charts-presentations-code/c/ee1353ef-ddab-44c6-bb0d-2cb6dc99e1e0
1. https://diagrams.helpful.dev/s/s:63UoLUpE
![](./img/sequence_diagram1.png)
2. https://diagrams.helpful.dev/s/s:M9uyyYp0
![](./img/sequence_diagram2.png)

# JavaCV to convert the mp4 to wav
refer: https://github.com/bytedeco/javacv
> mvn exec:java -Dexec.mainClass="com.example.VideoToAudioConverterJAVA"
