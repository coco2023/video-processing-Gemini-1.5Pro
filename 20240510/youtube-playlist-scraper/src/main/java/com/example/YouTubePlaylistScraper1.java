package com.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;

public class YouTubePlaylistScraper1 {
    public static void main(String[] args) {
        // YouTube playlist URL
        String url = "https://www.youtube.com/playlist?list=PL159CD41EB36CFE86";

        // HashMap to store video titles and their URLs
        HashMap<String, String> videoLinks = new HashMap<>();

        try {

            // Fetch the HTML content from the YouTube playlist URL
            Document doc = Jsoup.connect(url).get();  // Corrected here: use get() to fetch the content
            // System.out.println(doc.outerHtml()); // Prints the entire HTML

            // Select the video elements by their CSS selector
            // Use a more specific selector to accurately capture the playlist links
            Elements videoElements = doc.select("div#contents a[id=video-title]");
            System.out.println("Number of video elements found: " + videoElements.size());

            // Iterate over each element and extract the URL and title
            for (Element video : videoElements) {
                String videoUrl = video.absUrl("href"); // Gets absolute URL
                String videoTitle = video.attr("title"); // Gets the video title
                videoLinks.put(videoUrl, videoTitle);
            }

            // Output the HashMap
            videoLinks.forEach((videoUrl, videoTitle) -> System.out.println("URL: " + videoUrl + ", Title: " + videoTitle));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
