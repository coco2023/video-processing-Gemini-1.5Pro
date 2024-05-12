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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.databind.ObjectMapper;

public class YouTubePlaylistScraper3 {
    public static void main(String[] args) {
        // Set the location of the ChromeDriver
        System.setProperty("webdriver.chrome.driver", "D:\\Application\\WorkApp\\ChromeDriver\\chromedriver-win64\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200");
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get("https://www.youtube.com/playlist?list=PL159CD41EB36CFE86");
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            HashMap<String, String> videoLinks = new HashMap<>();
            
            WebDriverWait wait = new WebDriverWait(driver, 30);

            // Better scrolling logic that checks for the presence of the "Load more" button or similar triggers
            while (true) {
                List<WebElement> loadedVideos = driver.findElements(By.cssSelector("a#video-title"));
                WebElement body = driver.findElement(By.tagName("body"));
                body.sendKeys(Keys.END); // Scroll to the end of the page
                Thread.sleep(2000); // Wait for new content to load

                // Check if any new videos are loaded
                if (driver.findElements(By.cssSelector("a#video-title")).size() == loadedVideos.size()) {
                    break; // Exit loop if no new videos are found after scrolling
                }
            }

            // Ensure all videos are loaded
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a#video-title")));

            // Get all the video elements after full load
            List<WebElement> videoElements = driver.findElements(By.cssSelector("a#video-title"));
            for (WebElement videoElement : videoElements) {
                String videoUrl = videoElement.getAttribute("href");
                String videoTitle = videoElement.getAttribute("title");
                videoLinks.put(videoUrl, videoTitle);
            }

            // Print the results
            videoLinks.forEach((url, title) -> System.out.println("URL: " + url + ", Title: " + title));

            // Serialize HashMap to JSON file
            ObjectMapper mapper = new ObjectMapper();
            File directory = new File("outputs");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            mapper.writeValue(new File(directory, "videoLinks3.json"), videoLinks);
            System.out.println("JSON file has been written successfully");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
