package com.example;

import java.util.HashMap;
import java.util.Map;

class VideoInfo {
    String videoName;
    String videoTime;
    String videoUrl;

    VideoInfo(String name, String time, String url) {
        this.videoName = name;
        this.videoTime = time;
        this.videoUrl = url;
    }
}

public class VideoStore {
    private Map<String, VideoInfo> videos = new HashMap<>();

    public void addVideo(String videoId, VideoInfo videoInfo) {
        videos.put(videoId, videoInfo);
    }

    public VideoInfo getVideo(String videoId) {
        return videos.get(videoId);
    }

    public static void main(String[] args) {
        VideoStore store = new VideoStore();
        store.addVideo("vid123", new VideoInfo("Example Video", "12:00", "http://example.com/video"));

        VideoInfo info = store.getVideo("vid123");
        System.out.println("Video: " + info.videoName + ", Time: " + info.videoTime + ", URL: " + info.videoUrl);
    }
}
