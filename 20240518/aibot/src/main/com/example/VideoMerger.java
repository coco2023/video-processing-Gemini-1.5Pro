package com.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import com.example.FileUtil;

public class VideoMerger {

    public static String inputDirectoryPath = "outputs/mp4split/";
    public static String outputFilePath = "outputs/mp4merge/";
    public static String outputFileName = "merged_video.mp4";

    public static void main(String[] args) {
        try {
            FileUtil.makeDir(inputDirectoryPath);
            FileUtil.makeDir(outputFilePath);
            List<String> inputFiles = getMp4Files(inputDirectoryPath);
            mergeVideos(inputFiles.toArray(new String[0]), outputFilePath + outputFileName);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getMp4Files(String directoryPath) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            return paths
                    .filter(Files::isRegularFile)        // Filter to include only files
                    .map(Path::getFileName)              // Convert Path to FileName
                    .map(Path::toString)                 // Convert FileName to String
                    .filter(filename -> filename.endsWith(".mp4")) // Filter to include only .mp4 files
                    .collect(Collectors.toList());       // Collect as a List
        }
    }

    public static void mergeVideos(String[] inputFiles, String outputFile) throws IOException, InterruptedException {
        FFmpegFrameRecorder recorder = null;

        try {
            for (String inputFile : inputFiles) {
                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputDirectoryPath + inputFile);
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
