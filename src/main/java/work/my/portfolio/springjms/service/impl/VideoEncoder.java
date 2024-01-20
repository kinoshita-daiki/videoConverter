package work.my.portfolio.springjms.service.impl;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import lombok.Builder;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

@Builder
class VideoEncoder {

	private final String inputFileName;

	private final Integer startMinutes;

	private final Integer startSeconds;

	private final Integer endMinutes;

	private final Integer endSeconds;

	private final String outputFilePath;

	private final String pathToFFmpeg;

	private final String pathToFFprobe;

	public void encodeVideo() throws IOException {
		FFmpeg ffmpeg = new FFmpeg(pathToFFmpeg);
		FFprobe ffprobe = new FFprobe(pathToFFprobe);

		long startOffset = toSeconds(startMinutes, startSeconds);
		long duration = toSeconds(endMinutes, endSeconds) - startOffset;
		FFmpegBuilder builder = new FFmpegBuilder()//
				.setInput(inputFileName) //
				.overrideOutputFiles(false) //
				.addOutput(outputFilePath) //
				.setFormat("mp4") //
				.setStartOffset(startOffset, TimeUnit.SECONDS)
				.setDuration(duration, TimeUnit.SECONDS)//
				.done();

		var executor = new FFmpegExecutor(ffmpeg, ffprobe);
		executor.createJob(builder).run();
	}

	private long toSeconds(int minutes, int seconds) {
		return minutes * 60L + seconds;
	}

}
