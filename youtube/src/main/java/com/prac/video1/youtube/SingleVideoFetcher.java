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
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
@Log4j2
public class SingleVideoFetcher {

    private static final String APPLICATION_NAME = "My First Project";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String VIDEO_ID = "ggpXwiKNiU8"; // The YouTube video ID
    private static final String API_KEY = "AIzaSyAW0etjJys7vi3A9ckID4q0X-B-KciNvA4"; // Replace with your actual API key

    private YouTube youtubeService;

    @Autowired
    private OAuthService oauthService;

    @Autowired
    private VideoDetailsService videoDetailsService;

    private Credential credential;

    @PostConstruct
    public void init() throws Exception {
        credential = oauthService.authorize();
        youtubeService = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public List<String> fetchSingleVideo() throws Exception {
        List<String> captionIds = fetchCaptionIds(VIDEO_ID);

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

        return captionsList;
    }

    public List<String> fetchCaptionIds(String videoId) throws Exception {
        List<String> captionIds = new ArrayList<>();
        YouTube.Captions.List request = youtubeService.captions().list("snippet", videoId);
//        request.setKey(API_KEY);
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
//        request.setKey(API_KEY);
        HttpResponse response = request.executeMedia();
        InputStream content = response.getContent();
        try (Scanner scanner = new Scanner(content)) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}
