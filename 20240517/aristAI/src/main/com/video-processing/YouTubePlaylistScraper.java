package com.example;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.JavascriptExecutor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubePlaylistScraper {
    public static void main(String[] args) throws InterruptedException {

        String playListName = "Speeches"; // Speeches // FOMC Press Conferences
        String playListLink = "https://www.youtube.com/playlist?list=PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f"; // https://www.youtube.com/playlist?list=PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f // https://www.youtube.com/playlist?list=PL159CD41EB36CFE86

        System.setProperty("webdriver.chrome.driver",
                "D:\\Application\\WorkApp\\ChromeDriver\\chromedriver-win64\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200");
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(playListLink);
            WebDriverWait wait = new WebDriverWait(driver, 50);
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Implement more effective scrolling to ensure all elements are loaded
            int oldSize = 0;
            List<WebElement> elements;
            do {
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                // Wait for elements to become visible after scroll
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a#video-title")));
                elements = driver.findElements(By.cssSelector("a#video-title"));
                if (elements.size() == oldSize) {
                    System.out.println("no further elements");
                    break;
                }
                oldSize = elements.size();
                Thread.sleep(2000); // Short pause to allow DOM to update
            } while (true);

            // Process the links: save into hashmap
            HashMap<String, String> videoLinks = new HashMap<>();
            int videoCount = 0;

            // extract playlistId
            String playlistId = extractPlayListId(playListLink);

            for (WebElement element : elements) {
                String videoUrl = element.getAttribute("href");
                String videoTitle = element.getAttribute("title");

                // extract videoId
                String videoId = extractVideoId(videoUrl);

                videoLinks.put(videoId + "|" + videoCount + "|" + playlistId, videoUrl);
                videoCount++;
            }

            // Print results
            System.out.println("Total number of unique URLs collected: " + videoLinks.size());

            // Define a date formatter
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String formattedDate = LocalDateTime.now().format(dtf);

            // Serialize HashMap to JSON file
            ObjectMapper mapper = new ObjectMapper();
            File directory = new File("outputs");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            mapper.writeValue(new File(directory, formattedDate + "_" + playListName + "_videoLinks.json"), videoLinks);
            System.out.println("JSON file has been written successfully");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    private static String extractPlayListId(String url) {
        return extractByRegex(url, "list=([^&]*)");
    }

    private static String extractVideoId(String url) {
        return extractByRegex(url, "v=([^&]*)");
    }

    private static String extractByRegex(String url, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
