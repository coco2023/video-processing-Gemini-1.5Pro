package com.prac.video1.youtube;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItemListResponse;

import java.util.ArrayList;
import java.util.List;

public class PlaylistVideosFetcher1 {
    private static final String APPLICATION_NAME = "API Sample";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public static List<String> fetchPlaylistVideos(String apiKey, String playlistId) throws Exception {
        YouTube youtubeService = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();

        YouTube.PlaylistItems.List request = youtubeService.playlistItems()
                .list("snippet")
                .setMaxResults(50L)
                .setPlaylistId(playlistId)
                .setKey(apiKey);

        List<String> videoIds = new ArrayList<>();
        String nextPageToken = "";
        do {
            PlaylistItemListResponse response = request.setPageToken(nextPageToken).execute();
            response.getItems().forEach(item -> videoIds.add(item.getSnippet().getResourceId().getVideoId()));
            nextPageToken = response.getNextPageToken();
        } while (nextPageToken != null && !nextPageToken.equals(""));

        return videoIds;
    }
}
