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

import java.io.IOException;

import javax.management.RuntimeErrorException;

public class VideoSplitter {

    public static String filePath = "outputs/";

    public static void main(String[] args) {
        String infoString = "1ESOfxO78B8#9#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f#segment#1#00:00:42.500#00:00:57.100";
        try {
            parseAndSplitVideo(infoString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseAndSplitVideo(String infoString) throws IOException, InterruptedException {
        String[] parts = infoString.split("#");
        String videoName = parts[0];
        String videoId = parts[1];
        String startTime = parts[5];
        String endTime = parts[6];
        String outputName = videoName + "_segment.mp4";

        try { 
            splitVideo(videoName, startTime, endTime, outputName);   
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void splitVideo(String videoName, String startTime, String endTime, String outputName) throws Exception {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePath + videoName + ".mp4");
        grabber.start();

        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(filePath + outputName, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
        recorder.setFormat("mp4");
        recorder.setFrameRate(grabber.getFrameRate());
        recorder.start();

        double startSeconds = timeStringToSeconds(startTime);
        double endSeconds = timeStringToSeconds(endTime);

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
}
