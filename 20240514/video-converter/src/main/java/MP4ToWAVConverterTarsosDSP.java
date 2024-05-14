// import org.bytedeco.ffmpeg.global.avutil;
// import org.bytedeco.javacv.FFmpegFrameGrabber;
// import org.bytedeco.javacv.FrameGrabber;

// import javax.sound.sampled.*;
// import java.io.File;
// import java.io.IOException;
// import java.nio.ByteBuffer;
// import java.nio.ShortBuffer;

// public class MP4ToWAVConverterTarsosDSP {

//     public static void main(String[] args) {
//         String inputFilePath = "videos/_4t62i_OUZ8#1#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f.mp4"; // path_to_your_mp4_file.mp4
//         String outputFilePath = "videos/_4t62i_OUZ8#1#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f.wav";
//       try {
//             convertMp4ToWav(inputFilePath, outputFilePath);
//             System.out.println("Conversion completed successfully.");
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     public static void convertMp4ToWav(String inputFilePath, String outputFilePath) throws FrameGrabber.Exception, IOException {
//         FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFilePath);
//         grabber.start();

//         AudioFormat audioFormat = new AudioFormat(
//                 grabber.getSampleRate(),
//                 16,
//                 grabber.getAudioChannels(),
//                 true,
//                 false
//         );

//         DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
//         if (!AudioSystem.isLineSupported(info)) {
//             throw new LineUnavailableException("The line is not supported.");
//         }

//         SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
//         line.open(audioFormat);
//         line.start();

//         File outputFile = new File(outputFilePath);
//         try (AudioInputStream audioInputStream = new AudioInputStream(
//                 new JavaCVAudioStream(grabber),
//                 audioFormat,
//                 AudioSystem.NOT_SPECIFIED)) {

//             AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputFile);
//         }

//         grabber.stop();
//         grabber.release();
//     }

//     static class JavaCVAudioStream extends TargetDataLine {

//         private final FFmpegFrameGrabber grabber;
//         private ShortBuffer shortBuffer;

//         public JavaCVAudioStream(FFmpegFrameGrabber grabber) {
//             this.grabber = grabber;
//         }

//         @Override
//         public int read(byte[] b, int off, int len) {
//             try {
//                 while ((shortBuffer = grabber.grabSamples().samples[0]) == null) {
//                     // Continue grabbing until we get audio samples
//                 }
//                 ByteBuffer byteBuffer = ByteBuffer.allocate(shortBuffer.capacity() * 2);
//                 for (int i = 0; i < shortBuffer.capacity(); i++) {
//                     byteBuffer.putShort(shortBuffer.get(i));
//                 }
//                 byte[] audioBytes = byteBuffer.array();
//                 System.arraycopy(audioBytes, 0, b, off, Math.min(len, audioBytes.length));
//                 return Math.min(len, audioBytes.length);
//             } catch (Exception e) {
//                 e.printStackTrace();
//                 return -1;
//             }
//         }

//         @Override
//         public void open(AudioFormat format, int bufferSize) {
//             // No-op
//         }

//         @Override
//         public void open(AudioFormat format) {
//             // No-op
//         }

//         @Override
//         public void start() {
//             // No-op
//         }

//         @Override
//         public void stop() {
//             // No-op
//         }

//         @Override
//         public int available() {
//             return 0;
//         }

//         @Override
//         public void close() {
//             // No-op
//         }

//         @Override
//         public boolean isActive() {
//             return false;
//         }

//         @Override
//         public boolean isRunning() {
//             return false;
//         }

//         @Override
//         public AudioFormat getFormat() {
//             return new AudioFormat(
//                     grabber.getSampleRate(),
//                     16,
//                     grabber.getAudioChannels(),
//                     true,
//                     false
//             );
//         }

//         @Override
//         public int getBufferSize() {
//             return 0;
//         }

//         @Override
//         public void flush() {
//             // No-op
//         }

//         @Override
//         public void drain() {
//             // No-op
//         }

//         @Override
//         public void addLineListener(LineListener listener) {
//             // No-op
//         }

//         @Override
//         public void removeLineListener(LineListener listener) {
//             // No-op
//         }

//         @Override
//         public Line.Info getLineInfo() {
//             return new DataLine.Info(SourceDataLine.class, getFormat());
//         }

//         @Override
//         public void write(byte[] b, int off, int len) {
//             // No-op
//         }
//     }
// }
