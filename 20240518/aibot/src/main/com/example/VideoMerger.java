package com.example;

import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avformat.AVStream;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avformat;
import org.bytedeco.ffmpeg.global.avutil;

import static org.bytedeco.ffmpeg.global.avcodec.av_packet_unref;
import static org.bytedeco.ffmpeg.global.avformat.*;
import static org.bytedeco.ffmpeg.global.avutil.*;

public class VideoMerger {
    public static void main(String[] args) {
        String[] inputFiles = {
            "outputs/1ESOfxO78B8_segment1.mp4",
            "outputs/1ESOfxO78B8_segment11.mp4",
            "outputs/3dYIOvCEUpU_segment4.mp4"
        };
        String outputFilePath = "outputs/merged_video.mp4";

        AVFormatContext outputContext = avformat_alloc_context();
        if (avformat_alloc_output_context2(outputContext, null, null, outputFilePath) < 0) {
            System.err.println("Could not create output context");
            return;
        }

        for (String inputFile : inputFiles) {
            AVFormatContext inputContext = avformat_alloc_context();
            if (avformat_open_input(inputContext, inputFile, null, null) < 0) {
                System.err.println("Could not open input file: " + inputFile);
                continue;
            }

            if (avformat_find_stream_info(inputContext, (org.bytedeco.ffmpeg.avutil.AVDictionary) null) < 0) {
                System.err.println("Failed to retrieve input stream information");
                avformat_close_input(inputContext);
                continue;
            }

            for (int i = 0; i < inputContext.nb_streams(); i++) {
                AVStream inStream = inputContext.streams(i);
                AVStream outStream = avformat_new_stream(outputContext, null);
                if (outStream == null) {
                    System.err.println("Failed allocating output stream");
                    avformat_close_input(inputContext);
                    continue;
                }

                if (avcodec.avcodec_parameters_copy(outStream.codecpar(), inStream.codecpar()) < 0) {
                    System.err.println("Failed to copy codec parameters");
                    avformat_close_input(inputContext);
                    continue;
                }

                outStream.time_base(inStream.time_base());
            }

            AVPacket packet = new AVPacket();
            while (av_read_frame(inputContext, packet) >= 0) {
                AVStream inStream = inputContext.streams(packet.stream_index());
                AVStream outStream = outputContext.streams(packet.stream_index());

                packet.pts(av_rescale_q_rnd(packet.pts(), inStream.time_base(), outStream.time_base(), AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX));
                packet.dts(av_rescale_q_rnd(packet.dts(), inStream.time_base(), outStream.time_base(), AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX));
                packet.duration(av_rescale_q(packet.duration(), inStream.time_base(), outStream.time_base()));
                packet.pos(-1);

                if (av_interleaved_write_frame(outputContext, packet) < 0) {
                    System.err.println("Error muxing packet");
                    break;
                }
                av_packet_unref(packet);
            }

            avformat_close_input(inputContext);
        }

        av_write_trailer(outputContext);
        if (avio_closep(outputContext.pb()) < 0) {
            System.err.println("Error closing output file");
        }
        avformat_free_context(outputContext);

        System.out.println("Videos merged successfully!");
    }
}
