import subprocess

def download_video(video_url, output_format='mp4'):
    command = ['yt-dlp', '-f', f'bestvideo[ext={output_format}]+bestaudio', '-o', '%(title)s.%(ext)s', video_url]
    subprocess.run(command)

download_video('https://www.youtube.com/watch?v=QQF0f9wfyhA')
