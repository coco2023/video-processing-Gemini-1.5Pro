package com.prac.video1.youtube;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistItem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class PlaylistVideosFetcher2 {
    private static final String APPLICATION_NAME = "My First Project";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String DB_URL = "jdbc:mysql://localhost:3306/youtube_data";
    private static final String USER = "root";
    private static final String PASS = "12345";

    private static Connection connect() throws Exception {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

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
            for (PlaylistItem item : response.getItems()) {
                videoIds.add(item.getSnippet().getResourceId().getVideoId());
                storeVideoDetails(item.getSnippet().getResourceId().getVideoId(), item.getSnippet().getTitle(), item.getSnippet().getDescription());
            }
            nextPageToken = response.getNextPageToken();
        } while (nextPageToken != null && !nextPageToken.equals(""));

        return videoIds;
    }

    private static void storeVideoDetails(String videoId, String title, String description) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = connect();
            String sql = "INSERT INTO videos (video_id, title, description) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE title = VALUES(title), description = VALUES(description)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, videoId);
            pstmt.setString(2, title);
            pstmt.setString(3, description);
            pstmt.executeUpdate();
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java PlaylistVideosFetcher <API_KEY> <PLAYLIST_ID>");
            return;
        }
        String apiKey = args[0];
        String playlistId = args[1];
        try {
            List<String> videoIds = fetchPlaylistVideos(apiKey, playlistId);
            System.out.println("Video IDs fetched: " + videoIds);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to fetch video details due to an error: " + e.getMessage());
        }
    }
}
