package com.example;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.io.File;
import java.util.*;

public class AudioSplitter {

    public static void main(String[] args) {
        String inputFolderPath = "outputs/videos/wav";
        String outputFolderPath = "outputs/videos/split";
        int segmentDuration = 59; // Segment duration in seconds

        List<String> audioFiles = getWavFileNames(inputFolderPath);
        for (String fileName : audioFiles) {
          String inputFilePath = inputFolderPath + "/" + fileName + ".wav";
          splitAudioFile(inputFilePath, outputFolderPath, segmentDuration, fileName);
        }
    }

    public static List<String> getWavFileNames(String folderPath) {
          List<String> fileNames = new ArrayList<>();
          File folder = new File(folderPath);
          File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));
          
          if (files != null) {
                    for (File file : files) {
                              fileNames.add(file.getName().replace(".wav", ""));
                    }
          } else {
                    System.out.println("No WAV files found in the directory: " + folderPath);
          }          
          return fileNames;
      }

      public static void splitAudioFile(String inputFilePath, String outputFolderPath, int segmentDuration, String fileName) {
          File outputFolder = new File(outputFolderPath);
          if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFilePath)) {
            grabber.start();

            int audioChannels = grabber.getAudioChannels();
            int sampleRate = grabber.getSampleRate();
            int totalDuration = (int) (grabber.getLengthInTime() / 1000000); // Convert to seconds
            int segmentCount = (int) Math.ceil((double) totalDuration / segmentDuration);

            for (int i = 0; i < segmentCount; i++) {
                    String outputFilePath = outputFolderPath + "/" + fileName + "#segment#" + i + ".wav";
                    try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFilePath, audioChannels)) {
                        recorder.setFormat("wav");
                        recorder.setSampleRate(sampleRate);
                        recorder.setAudioChannels(audioChannels);
                        recorder.start();
    
                        int startTime = i * segmentDuration;
                        int endTime = Math.min(startTime + segmentDuration, totalDuration);
    
                        grabber.setTimestamp(startTime * 1000000L);
    
                        while (grabber.getTimestamp() < endTime * 1000000L) {
                            Frame frame = grabber.grabSamples();
                            if (frame == null) {
                                break;
                            }
                            recorder.record(frame);
                        }
    
                        recorder.stop();
                    }
                }
        
            grabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
