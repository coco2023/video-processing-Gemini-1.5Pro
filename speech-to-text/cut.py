from moviepy.video.io.VideoFileClip import VideoFileClip
# video1v000_1_0:00:59.970000_0:00:00

def parse_filename(filename):
    # Assuming format is videoId_subVideoId_endtime_starttime.mp4
    parts = filename.split('_')
    video_id = parts[0]
    sub_video_id = parts[1]
    end_time = parts[2]
    start_time = parts[3]
    return video_id, sub_video_id, start_time, end_time

def cut_video(filename):
    video_id, sub_video_id, start_time, end_time = parse_filename(filename)
    videoname = video_id[:-4]
    input_file = f"{videoname}.mp4"
    print(input_file)
    output_file = f"{videoname}_{sub_video_id}_cut.mp4"

    # Load the video file
    clip = VideoFileClip(input_file)
    # Cut the clip according to the start and end times
    cut_clip = clip.subclip(start_time, end_time)
    # Write the resulting clip to a file
    cut_clip.write_videofile(output_file, codec='libx264')

# Example usage
cut_video('video1v000_1_0:00:59.970000_0:00:00')
