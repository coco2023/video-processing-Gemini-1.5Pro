package com.prac.video1.youtube;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Caption;
import com.google.api.services.youtube.model.CaptionListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.auth.http.HttpCredentialsAdapter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class PlaylistVideosFetcher {
    private static final String APPLICATION_NAME = "My First Project";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
//    private static final String VIDEO_ID = "ggpXwiKNiU8"; // The YouTube video ID
//    private static final String API_KEY = "AIzaSyAW0etjJys7vi3A9ckID4q0X-B-KciNvA4"; // Replace with your actual API key

    private YouTube youtubeService;
    private Credential credential;

    @Autowired
    private OAuthService oauthService;

    @Autowired
    private VideoDetailsService videoDetailsService;

    @PostConstruct
    public void init() throws Exception {
        credential = oauthService.authorize();
        youtubeService = new YouTube.Builder(
//                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, request -> {})
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public List<String> fetchPlaylistVideos(String apiKey, String playlistId) throws Exception {
        log.info("start fetchPlaylistVideos");
        YouTube.PlaylistItems.List request = youtubeService.playlistItems()
                .list("snippet")
                .setMaxResults(50L)
                .setPlaylistId(playlistId)
                .setKey(apiKey); // Ensure the API key is correctly set for requests

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
        Map<String, Map<String, Objects>> captionMap = new HashMap<>();
        YouTube.Captions.List request = youtubeService.captions().list("snippet", videoId);

        CaptionListResponse response = request.execute();

        if (response.getItems() != null) {
            for (Caption caption : response.getItems()) {
                String language = caption.getSnippet().getLanguage();
                String captionId = caption.getId();
                String timestamp = caption.getSnippet().getLastUpdated().toString();
                captionIds.add(captionId);
                // KV
                Map<String, Objects> timeCaption = new HashMap<>();
                timeCaption.put(timestamp, null);
                captionMap.put("captionId", timeCaption);

                System.out.printf("Downloading caption: %s (%s)\n", language, captionId);
            }
        }
        return captionIds;
    }

    public String downloadCaptions(String captionId) throws Exception {
        YouTube.Captions.Download request = youtubeService.captions().download(captionId);
        request.setTfmt("srt");  // or "vtt" for WebVTT format
//        request.setKey(API_KEY);

        HttpResponse response = request.executeMedia();
        InputStream content = response.getContent();
        try (Scanner scanner = new Scanner(content)) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}
