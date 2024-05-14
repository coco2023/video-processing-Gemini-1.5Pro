import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.AudioAttributes;
import ws.schild.jave.EncodingAttributes;

import java.io.File;

public class VideoToAudioConverterJAVA {
    
    public static void main(String[] args) {

        String inputFilePath = "videos/_4t62i_OUZ8#1#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f.mp4"; // path_to_your_mp4_file.mp4
        String outputFilePath = "videos/_4t62i_OUZ8#1#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f.wav";

        File source = new File(inputFilePath);
        File target = new File(outputFilePath);

        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("pcm_s16le");
        audio.setBitRate(16000);
        audio.setChannels(2);
        audio.setSamplingRate(44100);

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("wav");
        attrs.setAudioAttributes(audio);

        Encoder encoder = new Encoder();
        try {
            encoder.encode(new MultimediaObject(source), target, attrs);
            System.out.println("Conversion completed!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
