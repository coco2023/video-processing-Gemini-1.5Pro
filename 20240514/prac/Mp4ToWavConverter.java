package com.example;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.InputFormatException;
import it.sauronsoftware.jave.MultimediaObject;

import java.io.File;

// https://www.sauronsoftware.it/projects/jave/index.php
// https://central.sonatype.com/artifact/ws.schild/jave-core/3.1.1/overview
public class Mp4ToWavConverter {

    public static void main(String[] args) {
        // Source video file
        File source = new File("path/to/your/video.mp4");
        // Target audio file
        File target = new File("path/to/your/output.wav");

        // Audio attributes
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("pcm_s16le");
        audio.setBitRate(128000);  // You can adjust this
        audio.setChannels(2);
        audio.setSamplingRate(44100);  // You can adjust this

        // Encoding attributes
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("wav");
        attrs.setAudioAttributes(audio);

        // Encoder
        Encoder encoder = new Encoder();
        try {
            encoder.encode(new MultimediaObject(source), target, attrs);
            System.out.println("Conversion completed successfully.");
        } catch (InputFormatException e) {
            System.err.println("Invalid format.");
            e.printStackTrace();
        } catch (EncoderException e) {
            System.err.println("Encoding error.");
            e.printStackTrace();
        }
    }
}
