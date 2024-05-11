import java.io.BufferedReader;
import java.io.InputStreamReader;

public class YouTubeVideoDownloader {

    public static void downloadVideo(String videoUrl) {
        try {
            ProcessBuilder builder = new ProcessBuilder("yt-dlp", "-o", "downloaded_video.%(ext)s", videoUrl);
            builder.redirectErrorStream(true); // Redirect error stream to the output stream

            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);  // Print the output of yt-dlp to the console
            }

            int exitCode = process.waitFor();  // Wait for the process to complete
            System.out.println("Download completed with exit code " + exitCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // compile: javac YouTubeVideoDownloader.java
    // run: java YouTubeVideoDownloader "https://www.youtube.com/watch?v=RePqojqhR2k"  
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java https://www.youtube.com/watch?v=RePqojqhR2k");
            System.exit(1);
        }
        downloadVideo(args[0]);
    }
}
