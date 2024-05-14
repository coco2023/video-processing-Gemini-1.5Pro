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

public class MP4ToWAVConverter {
    public static void main(String[] args) {
        FFmpegLogCallback.set();

        String inputFilePath = "videos/input.mp4";
        String outputFilePath = "videos/output.wav";

        System.out.println("Source file: " + new File(inputFilePath).getAbsolutePath());
        System.out.println("Target file: " + new File(outputFilePath).getAbsolutePath());

        convertMp4ToWav(inputFilePath, outputFilePath);

        if (Files.exists(Paths.get(outputFilePath))) {
            System.out.println("Conversion completed successfully! Output file size: " + new File(outputFilePath).length() + " bytes");
        } else {
            System.out.println("Conversion failed. Output file was not created.");
        }
    }

    public static void convertMp4ToWav(String inputFilePath, String outputFilePath) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFilePath);
             FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFilePath, 2)) {

            grabber.start();
            System.out.println("Grabber started");
            System.out.println("Audio Channels: " + grabber.getAudioChannels());
            System.out.println("Sample Rate: " + grabber.getSampleRate());
            System.out.println("Bitrate: " + grabber.getAudioBitrate());

            recorder.setFormat("wav");
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_PCM_S16LE);
            recorder.setAudioBitrate(grabber.getAudioBitrate());
            recorder.setAudioChannels(grabber.getAudioChannels());
            recorder.start();
            System.out.println("Recorder started");

            Frame frame;
            while ((frame = grabber.grabSamples()) != null) {
                if (frame.samples != null) {
                    recorder.record(frame);
                    System.out.println("Recording frame...");
                } else {
                    System.out.println("No audio samples in this frame");
                }
            }

            recorder.stop();
            grabber.stop();
            System.out.println("Grabber and Recorder stopped");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
