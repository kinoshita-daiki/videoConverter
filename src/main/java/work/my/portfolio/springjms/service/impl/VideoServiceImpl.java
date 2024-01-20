package work.my.portfolio.springjms.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.NonNull;
import work.my.portfolio.springjms.messages.VideoMessage;
import work.my.portfolio.springjms.model.VideoMeta;
import work.my.portfolio.springjms.path.FFmpegPath;
import work.my.portfolio.springjms.path.VideoPath;
import work.my.portfolio.springjms.repository.VideoRepository;
import work.my.portfolio.springjms.service.VideoService;

@Service
class VideoServiceImpl implements VideoService {

	private final VideoRepository repository;

	private final VideoPath videoPath;

	private final FFmpegPath mpegPath;

	private final String mailFrom;

	private final String downloadUrl;

	private final String downloadTimeLimitTermSetting;

	@Autowired
	VideoServiceImpl(VideoRepository repository, VideoPath videoPath, FFmpegPath mpegPath,
			@Value("${mail.from}") String mailFrom,
			@Value("${video.download.url}") String downloadUrl,
			@Value("${video.download.time.limit.term.setting}") String downloadTimeLimit) {
		this.repository = repository;
		this.videoPath = videoPath;
		this.mpegPath = mpegPath;
		this.mailFrom = mailFrom;
		this.downloadUrl = downloadUrl;
		this.downloadTimeLimitTermSetting = downloadTimeLimit;
	}

	@Override
	public String convert(VideoMessage videoMessage, String originalPath) throws IOException {
		String outputFileName = "output_" + UUID.randomUUID().toString() + ".mp4";
		var encoder = VideoEncoder.builder()//
				.startMinutes(videoMessage.startMinutes())//
				.startSeconds(videoMessage.startSeconds())//
				.endMinutes(videoMessage.endMinutes())//
				.endSeconds(videoMessage.endSeconds())//
				.inputFileName(originalPath)//
				.outputFilePath(videoPath.getConverted() + outputFileName)//
				.pathToFFmpeg(mpegPath.getMpeg())
				.pathToFFprobe(mpegPath.getProbe())
				.build();
		encoder.encodeVideo();
		return outputFileName;
	}

	@Override
	public void postVideoMetaData(String fileNameFromClient, String outputFileName, LocalDateTime expiredDateTime) {
		repository.postVideoMetaData(fileNameFromClient, outputFileName, expiredDateTime);
	}

	@Override
	public VideoMeta findMetaData(String fileNameFromClient) {
		return repository.findMetaData(fileNameFromClient);
	}

	@Override
	public void sendMail(String to, String fileName, LocalDateTime expiredDateTime) {
		String subject = "動画変換が完了しました。(木下大暉ポートフォリオ)";
		String text = createTextForMail(fileName, expiredDateTime);
		repository.sendMail(to, mailFrom, subject, text);
	}

	private String createTextForMail(String fileName, LocalDateTime expiredDateTime) {
		String originalUrl = createDownloadPage(fileName);
		return String.join(System.lineSeparator(), //
				"ご利用ありがとうございます。動画変換サービスです。", //
				"動画変換が完了しました。", //
				"以下のURLから進み、ダウンロードを開始してください。", //
				"", //
				repository.getShortUrl(originalUrl), //
				"", //
				"ダウンロード期限は " + toDateTimeForMail(expiredDateTime) + "までです。", //
				"", //
				"※ 注意！このアプリはポートフォリオ用です。動作保証はいたしかねます。", //
				"※ またこのメールには返信できません。何かありましたら、Github等から連絡をお願いします。"//
		);
	}

	private String toDateTimeForMail(@NonNull LocalDateTime expiredDateTime) {
		return expiredDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH時mm分"));
	}

	private String createDownloadPage(String fileName) {
		return downloadUrl + fileName;
	}

	@Override
	public byte[] getVideo(String uri, String fileName) {
		return repository.getVideo(uri, fileName);
	}

	@Override
	public LocalDateTime getDownloadTimeLimit() {
		// 変換完了時刻を1時間単位で切り上げ、削除スケジュールは毎時間、2時間前の時刻の動画を削除する。DBのカラムに変換完了時刻を入れておく
		// 例：18:00 ＝＞ 20時にスケジュールで削除、18:59＝＞20時に削除、19:00＝＞21時にスケジュールで削除
		var now = LocalDateTime.now();
		int hour = now.plusHours(Long.parseLong(downloadTimeLimitTermSetting)).getHour();
		return now.withHour(hour).withMinute(0).withSecond(0).withNano(0);
	}

	@Override
	@Async
	@Scheduled(cron = "0 0 * * * *", zone = "Asia/Tokyo")
	public void deleteExpiredVideos() {
		// 2時間前の時刻の動画を削除する。
		LocalDateTime from = LocalDateTime.now().minusHours(2).withMinute(0).withSecond(0).withNano(0);
		LocalDateTime to = LocalDateTime.now().minusHours(2).withMinute(59).withSecond(59).withNano(999999999);
		repository.findExpiredVideoNameBetween(from, to)//
				.stream()//
				.map(n -> videoPath.getConverted() + n)//
				.map(Path::of)//
				.forEach(t -> {
					try {
						Files.deleteIfExists(t);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
	}

	@Override
	public void deleteClientVideo(String uri, String videoName) {
		repository.deleteClientVideo(uri, videoName);
	}
}
