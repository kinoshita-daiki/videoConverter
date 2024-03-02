package work.my.portfolio.springjms;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import work.my.portfolio.springjms.messages.VideoMessage;
import work.my.portfolio.springjms.path.VideoPath;
import work.my.portfolio.springjms.service.VideoService;

/**
 * レシーバー
 * 
 * @author kinoshita daiki
 * @since 2024/03/02
 */
@Log4j2
@RequiredArgsConstructor(onConstructor_ = { @Autowired }, access = AccessLevel.PACKAGE)
@Component
class Receiver {

	private final ObjectMapper mapper;

	private final VideoService service;

	private final VideoPath videoPath;

	/**
	 * メッセージを受け取る
	 * 
	 * @param messasge メッセージ
	 */
	@JmsListener(destination = "${jms.destination}", containerFactory = "getFactory", concurrency = "2")
	void receiveMessage(Message messasge) {
		if (messasge instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) messasge;
			Path originalPath = null;
			String fileName = null;
			try {
				VideoMessage videoMessage = mapper.readValue(textMessage.getText(), VideoMessage.class);

				fileName = videoMessage.fileName();
				Path fileFromClientPath = new File(videoPath.getOrigin() + fileName)
						.toPath();
				originalPath = Files.write(fileFromClientPath, service.getVideo(videoPath.getUri(), fileName));

				String outputFileName = service.convert(videoMessage, originalPath.toString());
				LocalDateTime expiredDateTime = service.getDownloadTimeLimit();

				// 整合性を保つ必要がないため、トランザクション不要
				service.postVideoMetaData(videoMessage.fileName(), outputFileName, expiredDateTime);
				service.sendMail(videoMessage.email(),
						videoMessage.fileName(),
						expiredDateTime);
			} catch (Exception e) {
				log.error("jms error", e);
			} finally {
				try {
					deleteClientVideo(originalPath, fileName);
				} catch (IOException e) {
					log.error("client video delete error", e);
				}
			}

		}
		try {
			messasge.acknowledge();
		} catch (JMSException e) {
			log.error("message acknowledge fails", e.getCause());
		}
	}

	private void deleteClientVideo(Path originalPath, String fileName) throws IOException {
		Files.deleteIfExists(originalPath);
		service.deleteClientVideo(videoPath.getUri(), fileName);
	}
}