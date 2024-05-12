package com.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class YouTubePlaylistScraper {
    public static void main(String[] args) {
        // Set the location of the ChromeDriver
        // download: https://googlechromelabs.github.io/chrome-for-testing/#stable
        System.setProperty("webdriver.chrome.driver", "D:/Application/WorkApp/ChromeDriver/chromedriver-win64/chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200");
        WebDriver driver = new ChromeDriver(options);

        try {
            // Replace with your YouTube playlist URL
            driver.get("https://www.youtube.com/playlist?list=PL159CD41EB36CFE86");
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);            // Configures the WebDriver to wait up to 10 seconds for elements to appear before throwing an error, providing time for dynamic content to load.

            HashMap<String, String> videoLinks = new HashMap<>();

            // Scroll to ensure all dynamic content loads (might need adjustment)
            // This code simulates pressing the "End" key on the keyboard up to ten times to scroll to the bottom of the page
            // Each key press is followed by a one-second pause (Thread.sleep(1000)), allowing time for any lazy-loaded content (such as additional videos in the playlist) to load as you scroll.
            WebDriverWait wait = new WebDriverWait(driver, 30);
            WebElement element = driver.findElement(By.tagName("body"));
            for (int i = 0; i < 10; i++) {
                element.sendKeys(org.openqa.selenium.Keys.END);
                Thread.sleep(1000); // Increase if necessary
            }
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("a#video-title"), 20));

            // Extract video details
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
                directory.mkdirs(); // Make the directory if it doesn't exist
            }
            mapper.writeValue(new File(directory, "videoLinks.json"), videoLinks);
            System.out.println("JSON file has been written successfully");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();  // Ensure the driver is closed
            }
        }
    }
}
