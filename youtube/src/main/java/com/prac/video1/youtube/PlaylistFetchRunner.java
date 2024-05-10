package com.prac.video1.youtube;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlaylistFetchRunner implements CommandLineRunner {
    private final PlaylistVideosFetcher videosFetcher;

    @Autowired
    private SingleVideoFetcher singleVideoFetcher;

    private final VideoDetailsService videoDetailsService;

    public PlaylistFetchRunner(PlaylistVideosFetcher videosFetcher, VideoDetailsService videoDetailsService) {
        this.videosFetcher = videosFetcher;
        this.videoDetailsService = videoDetailsService;
    }

    @Override
    public void run(String... args) throws Exception {
//        if (args.length < 2) {
//            System.out.println("Usage: java -jar yourapp.jar <API_KEY> <PLAYLIST_ID>");
//            return;
//        }
        List<String> videoIds = singleVideoFetcher.fetchSingleVideo();

//        String playlistId = "PLWTiuH5ft7ErAsTaeJMU31VI1JnnaTEy1"; // args[1];
//        List<String> videoIds = videosFetcher.fetchPlaylistVideos(apiKey, playlistId);
        System.out.println("Here is Fetched Video IDs:");
        videoIds.forEach(System.out::println);
    }
}
