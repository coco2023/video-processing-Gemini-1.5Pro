package com.example;

import be.tarsos.dsp.io.jvm.WaveFileWriter;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegProbeResult;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.io.File;
import java.io.IOException;

public class MP4ToWAVConverterTarSos {

    public static void main(String[] args) throws IOException {
//         // Path to the FFmpeg executable
//         String ffmpegPath = "/path/to/ffmpeg";

//         // Source video file
//         File source = new File("path/to/your/video.mp4");
//         // Intermediate extracted audio file
//         File tempAudio = new File("path/to/your/temp_audio.wav");
//         // Target audio file
//         File target = new File("path/to/your/output.wav");

//         // Initialize FFmpeg
//         FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
//         FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);

//         // Extract audio using FFmpeg
//         FFmpegBuilder builder = new FFmpegBuilder()
//                 .setInput(source.getAbsolutePath())
//                 .addOutput(tempAudio.getAbsolutePath())
//                 .setAudioCodec("pcm_s16le")
//                 .setFormat("wav")
//                 .done();

//         // Execute the extraction
//         executor.createJob(builder).run();

//         // Use TarsosDSP to process the extracted audio (if needed)
//         // Here, simply copying the extracted file to the target location
//         WaveFileWriter writer = new WaveFileWriter(tempAudio.getAbsolutePath(), target.getAbsolutePath());
//         writer.write();

        System.out.println("Conversion completed successfully.");
    }
}
