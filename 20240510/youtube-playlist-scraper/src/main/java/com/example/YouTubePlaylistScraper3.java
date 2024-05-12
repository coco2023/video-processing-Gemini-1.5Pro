package com.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.JavascriptExecutor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class YouTubePlaylistScraper {
    public static void main(String[] args) {
        // download: https://googlechromelabs.github.io/chrome-for-testing/#stable
        System.setProperty("webdriver.chrome.driver", "D:/Application/WorkApp/ChromeDriver/chromedriver-win64/chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200");
        WebDriver driver = new ChromeDriver(options);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            // Replace with your YouTube playlist URL
            driver.get("https://www.youtube.com/playlist?list=PL159CD41EB36CFE86");

            WebDriverWait wait = new WebDriverWait(driver, 30);
            HashMap<String, String> videoLinks = new HashMap<>();

            while (true) {
                List<WebElement> initialList = driver.findElements(By.cssSelector("a#video-title"));
                int oldSize = initialList.size();
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");  // Scroll to bottom of page
                Thread.sleep(5000); // Wait for content to load
                List<WebElement> newList = driver.findElements(By.cssSelector("a#video-title"));
                if (newList.size() == oldSize) {
                    break;  // Exit loop if no new videos were loaded
                }
            }

            List<WebElement> videoElements = driver.findElements(By.cssSelector("a#video-title"));
            for (WebElement videoElement : videoElements) {
                String videoUrl = videoElement.getAttribute("href");
                String videoTitle = videoElement.getAttribute("title");
                videoLinks.put(videoUrl, videoTitle);
            }

            // Serialize HashMap to JSON file
            ObjectMapper mapper = new ObjectMapper();
            File directory = new File("outputs");
            if (!directory.exists()) {
                directory.mkdirs(); // Make the directory if it doesn't exist
            }
            mapper.writeValue(new File(directory, "videoLinks.json"), videoLinks);
            System.out.println("JSON file has been written successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();  // Ensure the driver is closed
            }
        }
    }
}
