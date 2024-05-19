package com.example;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avformat;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avformat.AVStream;
import org.bytedeco.ffmpeg.avutil.AVRational;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import com.example.MP4ToWAVConverter;
import com.example.VideoMerger;

import org.bytedeco.javacv.FFmpegLogCallback;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.management.RuntimeErrorException;

public class VideoSplitter {

    public static String filePath = "outputs/videos/mp4/";
    public static String outputPath = "outputs/mp4split/";

    public static void beginSplitVideo(List<String> videoNameLists) {
        try {
            for (String videName : videoNameLists) {
                parseAndSplitVideo(videName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseAndSplitVideo(String videoNameString) throws IOException, InterruptedException {
        String[] parts = videoNameString.split("#");
        String videoName = parts[0];
        String videoId = parts[1];
        String playList = parts[2];
        String segmentNum = parts[4];
        String startTime = parts[5];
        String endTime = parts[6];

        String mp4VideName = videoName + "#" + videoId + "#" + playList;

        // convert time format: need to add previous time based on segment number
        double previousTime = Double.valueOf(segmentNum) * 59;
        double startSeconds = timeStringToSeconds(startTime) + previousTime;
        double endSeconds = timeStringToSeconds(endTime) + previousTime;
        String outputName = videoName + "#" + startSeconds + "#" + endSeconds + "#segment";

        try {
            splitVideo(mp4VideName, segmentNum, startSeconds, endSeconds, outputName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void splitVideo(String mp4VideName, String segmentNum, double startSeconds, double endSeconds,
            String outputName) throws Exception {
        FFmpegLogCallback.set();

        makeDir(filePath);
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePath + mp4VideName + ".mp4");
        grabber.start();

        makeDir(outputPath);
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath + outputName + ".mp4",
                grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
        recorder.setFormat("mp4");
        recorder.setFrameRate(grabber.getFrameRate());
        recorder.start();

        grabber.setTimestamp((long) (startSeconds * 1000000));
        Frame frame;

        while ((frame = grabber.grabFrame()) != null) {
            if (grabber.getTimestamp() > endSeconds * 1000000) {
                break;
            }
            recorder.record(frame);
        }

        grabber.stop();
        recorder.stop();
    }

    public static double timeStringToSeconds(String timeString) {
        String[] parts = timeString.split(":");
        double hours = Double.parseDouble(parts[0]);
        double minutes = Double.parseDouble(parts[1]);
        double seconds = Double.parseDouble(parts[2]);
        return hours * 3600 + minutes * 60 + seconds;
    }

    public static void makeDir(String filePathName) {
        File folder = new File(filePathName);
        if (!folder.exists())
            folder.mkdirs();
    }
}
