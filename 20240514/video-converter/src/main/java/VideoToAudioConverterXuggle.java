// import com.xuggle.mediatool.IMediaReader;
// import com.xuggle.mediatool.IMediaTool;
// import com.xuggle.mediatool.IMediaWriter;
// import com.xuggle.mediatool.ToolFactory;
// import com.xuggle.xuggler.IContainer;
// import com.xuggle.xuggler.IPacket;
// import com.xuggle.xuggler.IPixelFormat;
// import com.xuggle.xuggler.IVideoPicture;

// public class VideoToAudioConverterXuggle {
    
//     public static void main(String[] args) {
//         String inputFilePath = "videos/_4t62i_OUZ8#1#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f.mp4"; // path_to_your_mp4_file.mp4
//         String outputFilePath = "videos/_4t62i_OUZ8#1#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f.wav";
        
//         IMediaReader mediaReader = ToolFactory.makeReader(inputFilePath);
//         IMediaWriter mediaWriter = ToolFactory.makeWriter(outputFilePath, mediaReader);

//         mediaReader.addListener(mediaWriter);
        
//         while (mediaReader.readPacket() == null) {
//             // Keep reading packets until done
//         }

//         System.out.println("Conversion completed!");
//     }
// }