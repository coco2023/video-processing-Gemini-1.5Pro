package com.example;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FFmpegLogCallback;

public class VideoCutter {

    public static String filePath = "outputs/";

    public static void main(String[] args) {
        List<String> keyList = List.of(
            "1ESOfxO78B8#9#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f#segment#1#00:00:42.500#00:00:57.100",
            "1ESOfxO78B8#9#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f#segment#11#00:00:01.200#00:00:10.100",
            "3dYIOvCEUpU#14#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f#segment#4#00:00:00.300#00:00:58.900"
        );

        List<KeyComponents> componentsList = extractKeyComponents(keyList);

        for (KeyComponents components : componentsList) {
            try {
                cutVideo(components);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static List<KeyComponents> extractKeyComponents(List<String> keyList) {
        List<KeyComponents> componentsList = new ArrayList<>();
        Pattern pattern = Pattern.compile("([^#]+)#\\d+#([^#]+)#segment#(\\d+)#([^#]+)#([^#]+)");

        for (String key : keyList) {
            Matcher matcher = pattern.matcher(key);
            if (matcher.find()) {
                String videoId = matcher.group(1);
                String playlistId = matcher.group(2);
                String segmentNum = matcher.group(3);
                String startTime = matcher.group(4);
                String endTime = matcher.group(5);

                componentsList.add(new KeyComponents(videoId, playlistId, segmentNum, startTime, endTime));
            }
        }

        return componentsList;
    }

    public static void cutVideo(KeyComponents components) throws Exception {
        FFmpegLogCallback.set();

        String videoFilePath = filePath + components.videoId + ".mp4"; // Assuming video file is named as videoId.mp4
        String outputFileName = filePath + components.videoId + "_segment" + components.segmentNum + ".mp4";

        File videoFile = new File(videoFilePath);
        if (!videoFile.exists()) {
            System.err.println("File not found: " + videoFilePath);
            return;
        }

        System.out.println("Processing file: " + videoFilePath);
        System.out.println("Output file: " + outputFileName);

        double startTime = convertTimeToSeconds(components.startTime);
        double endTime = convertTimeToSeconds(components.endTime);

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFilePath)) {
            grabber.setFormat("mp4");

            System.out.println("Starting grabber...");
            grabber.start();

            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFileName, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels())) {
                System.out.println("Starting recorder...");
                recorder.start();

                Frame frame;
                double frameRate = grabber.getFrameRate();
                int startFrame = (int) (startTime * frameRate);
                int endFrame = (int) (endTime * frameRate);

                int frameNumber = 0;
                while ((frame = grabber.grabFrame()) != null) {
                    if (frameNumber >= startFrame && frameNumber <= endFrame) {
                        recorder.record(frame);
                    }
                    frameNumber++;
                    if (frameNumber > endFrame) {
                        break;
                    }
                }

                System.out.println("Stopping recorder...");
                recorder.stop();
            }

            System.out.println("Stopping grabber...");
            grabber.stop();
        } catch (Exception e) {
            System.err.println("Error processing file: " + videoFilePath);
            e.printStackTrace();
        }
    }

    public static double convertTimeToSeconds(String time) {
        String[] parts = time.split(":");
        double minutes = Double.parseDouble(parts[0]);
        double seconds = Double.parseDouble(parts[1]);
        double milliseconds = Double.parseDouble(parts[2]);
        return minutes * 60 + seconds + milliseconds / 1000;
    }

    public static class KeyComponents {
        String videoId;
        String playlistId;
        String segmentNum;
        String startTime;
        String endTime;

        public KeyComponents(String videoId, String playlistId, String segmentNum, String startTime, String endTime) {
            this.videoId = videoId;
            this.playlistId = playlistId;
            this.segmentNum = segmentNum;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }
}
