package com.example;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameFilter;
import org.bytedeco.javacv.FFmpegLogCallback;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MP4ToWAVConverter {

    private static String inputDirPath = "outputs/videos/mp4/";
    private static String outputDirPath = "outputs/videos/wav/";

    public static void main(String[] args) {
        FFmpegLogCallback.set();


        ArrayList<String> videoFileNames = getVideoFiles(inputDirPath);

        if (videoFileNames.isEmpty()) {
            System.out.println("No video files found in the directory: " + inputDirPath);
            return;
        }

        createOutputFolder(outputDirPath);

        for (String inputFileName : videoFileNames) {
            processVideoFile(inputFileName);
        }
    }

    private static ArrayList<String> getVideoFiles(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp4"));
        ArrayList<String> fileNames = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName().replace(".mp4", ""));
            }
        }
        return fileNames;
    }

    private static void createOutputFolder(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) folder.mkdirs();
    }

    private static void processVideoFile(String videoFileName) {
        String inputVideoName = inputDirPath + videoFileName + ".mp4";
        String outputVideoName = outputDirPath + videoFileName + ".wav";

        convertMp4ToWav(inputVideoName, outputVideoName);
    }

    public static void convertMp4ToWav(String inputFilePath, String outputFilePath) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFilePath);
             FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFilePath, 2)) {

            grabber.start();
            recorder.setFormat("wav");
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_PCM_S16LE);
            recorder.setAudioBitrate(grabber.getAudioBitrate());
            recorder.setAudioChannels(1);  // Ensure output is mono
            recorder.start();
            System.out.println("Recorder started");

            Frame frame;
            while ((frame = grabber.grabSamples()) != null) {
                if (frame.samples != null) {
                    recorder.record(frame);
                } else {
                    System.out.println("No audio samples in this frame");
                }
            }

            recorder.stop();
            grabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
