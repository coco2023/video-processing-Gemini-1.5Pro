package com.prac.video1.youtube;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.youtube.model.Caption;
import com.google.api.services.youtube.model.CaptionListResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
@Log4j2
public class PlaylistVideosFetcher3 {
    private static final String APPLICATION_NAME = "My First Project";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String VIDEO_ID = "ggpXwiKNiU8"; // The YouTube video ID
    private static final String API_KEY = "AIzaSyAW0etjJys7vi3A9ckID4q0X-B-KciNvA4"; // Replace with your actual API key

    private YouTube youtubeService;

    @Autowired
    private VideoDetailsService videoDetailsService;

    @PostConstruct
    public void init() throws Exception {
        youtubeService = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, request -> {})
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public List<String> fetchPlaylistVideos(String apiKey, String playlistId) throws Exception {
        YouTube.PlaylistItems.List request = youtubeService.playlistItems()
                .list("snippet")
                .setMaxResults(50L)
                .setPlaylistId(playlistId)
                .setKey(apiKey);

        List<String> videoIds = new ArrayList<>();
        String nextPageToken = "";
        do {
            PlaylistItemListResponse response = request.setPageToken(nextPageToken).execute();
            for (PlaylistItem item : response.getItems()) {
                String videoId = item.getSnippet().getResourceId().getVideoId();
                String title = item.getSnippet().getTitle();
                String description = item.getSnippet().getDescription();

                // Fetch and download captions
                List<String> captionIds = fetchCaptionIds(videoId);
//                String captions = captionIds.isEmpty() ? "" : downloadCaptions(captionIds.get(0));

                List<String> captionsList = captionIds.stream().map(captionId -> {
                    try {
                        return downloadCaptions(captionId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null; // or use Optional<String> to handle possibly missing values
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
//                captionsList.forEach(System.out::println);

                videoIds.add(videoId);
                videoDetailsService.storeVideoDetails(videoId, title, description, captionsList.toString());
            }
            nextPageToken = response.getNextPageToken();
        } while (nextPageToken != null && !nextPageToken.equals(""));
        return videoIds;
    }

    public List<String> fetchCaptionIds(String videoId) throws Exception {
        List<String> captionIds = new ArrayList<>();
        YouTube.Captions.List request = youtubeService.captions().list("snippet", videoId);
        request.setKey(API_KEY);
        CaptionListResponse response = request.execute();

        if (response.getItems() != null) {
            for (Caption caption : response.getItems()) {
                String language = caption.getSnippet().getLanguage();
                String captionId = caption.getId();
                captionIds.add(captionId);
                System.out.printf("Downloading caption: %s (%s)\n", language, captionId);
            }
        }
        return captionIds;
    }

    public String downloadCaptions(String captionId) throws Exception {
        YouTube.Captions.Download request = youtubeService.captions().download(captionId);
        request.setTfmt("srt");  // or "vtt" for WebVTT format
        request.setKey(API_KEY);

        HttpResponse response = request.executeMedia();
        InputStream content = response.getContent();
        try (Scanner scanner = new Scanner(content)) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}

//        InputStream is = request.executeMediaAsInputStream();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//        StringBuilder stringBuilder = new StringBuilder();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            stringBuilder.append(line).append("\n");
//        }
//        return stringBuilder.toString();


//                // Download Captions
//                YouTube.Captions.Download downloadRequest = youtubeService.captions().download(captionId);
//                downloadRequest.setTfmt("srt");  // Set format to SRT (you could also choose VTT)
//                downloadRequest.setKey(API_KEY);
//
//                // Prepare file output stream
//                try (OutputStream outputStream = new FileOutputStream(captionId + ".srt")) {
//                    downloadRequest.executeMediaAndDownloadTo(outputStream);
//                }
//                System.out.println("Caption downloaded successfully to " + captionId + ".srt");

//    public List<String> fetchCaptionIds(String apiKey, String videoId) throws Exception {
//    YouTube.Captions.List request = youtubeService.captions().list("snippet", videoId);
//    request.setKey(apiKey);
//    CaptionListResponse response = request.execute();
//
//    return response.getItems().stream()
//            .map(caption -> caption.getId())
//            .collect(Collectors.toList());
//}
//
//    public String downloadCaptions(String apiKey, String captionId) throws Exception {
//        YouTube.Captions.Download request = youtubeService.captions().download(captionId);
//        request.setTfmt("srt");  // or "vtt" for WebVTT format
//        request.setKey(apiKey);
//
//        HttpResponse response = request.executeMedia();
//        try (Scanner scanner = new Scanner(response.getContent())) {
//            scanner.useDelimiter("\\A");
//            return scanner.hasNext() ? scanner.next() : "";
//        }
//    }

//    @Autowired
//    private OAuthService oauthService;
//        Credential credential = oauthService.authorize();
