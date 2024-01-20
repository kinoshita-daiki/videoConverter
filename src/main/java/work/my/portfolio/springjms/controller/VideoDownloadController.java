package work.my.portfolio.springjms.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import work.my.portfolio.springjms.model.VideoMeta;
import work.my.portfolio.springjms.path.VideoPath;
import work.my.portfolio.springjms.service.VideoService;

@RequiredArgsConstructor(onConstructor_ = { @Autowired }, access = AccessLevel.PACKAGE)
@RestController
public class VideoDownloadController {

	private final VideoPath videoPath;

	private final VideoService service;

	@GetMapping("/videoDownload")
	public ResponseEntity<Resource> downloadVideo(@RequestParam String fileName) {
		File uploadedFile = new File(videoPath.getConverted() + fileName);
		if (StringUtils.isEmpty(fileName) || !uploadedFile.exists()) {
			return ResponseEntity.notFound().build();
		}
		Path path = uploadedFile.toPath();
		Resource resource = new PathResource(path);
		return ResponseEntity.ok()
				.contentType(getContentType(path))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
				.body(resource);
	}

	private MediaType getContentType(Path path) {
		try {
			return MediaType.parseMediaType(Files.probeContentType(path));
		} catch (IOException e) {
			return MediaType.APPLICATION_OCTET_STREAM;
		}
	}

	@GetMapping("/videoDownloadView")
	public DownloadViewMetaData getVideoDownloadView(@RequestParam String fileName) {
		VideoMeta downloadModel = service.findMetaData(fileName);
		String outputFileName = downloadModel.getOutputFileName();
		if (StringUtils.isEmpty(outputFileName)
				|| !new File(videoPath.getConverted() + outputFileName).exists()) {
			return new DownloadViewMetaData("expiredView", null, null);
		}
		return new DownloadViewMetaData("videoDownloadView", outputFileName, downloadModel.getExpiredDateTime());
	}

	private static record DownloadViewMetaData(String viewName, String fileName, LocalDateTime expiredDateTime) {
	}

	@ExceptionHandler(EmptyResultDataAccessException.class)
	public String displayNoDataErrorPage() {
		return "expiredView";
	}

	@ExceptionHandler(Throwable.class)
	public String displayCommonErrorPage() {
		return "commonErrorPage";
	}
}