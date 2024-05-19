package com.example;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.io.IOException;

public class VideoMerger {
          
    public static String filePath = "outputs/";

    public static void main(String[] args) {
        String videoFile1 = "1ESOfxO78B8_segment1.mp4";
        String videoFile2 = "1ESOfxO78B8_segment2.mp4";
        String outputFile = "merged_video.mp4";

        try {
            mergeVideos(new String[]{filePath + videoFile1, filePath + videoFile2}, filePath + outputFile);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void mergeVideos(String[] inputFiles, String outputFile) throws IOException, InterruptedException {
        FFmpegFrameRecorder recorder = null;

        try {
            for (String inputFile : inputFiles) {
                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
                grabber.start();

                if (recorder == null) {
                    recorder = new FFmpegFrameRecorder(outputFile, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
                    recorder.setFormat("mp4");
                    recorder.setFrameRate(grabber.getFrameRate());
                    recorder.start();
                }

                Frame frame;
                while ((frame = grabber.grabFrame()) != null) {
                    recorder.record(frame);
                }

                grabber.stop();
            }
        } finally {
            if (recorder != null) {
                recorder.stop();
            }
        }
    }
}
