package work.my.portfolio.springjms;

import java.io.File;
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
import work.my.portfolio.springjms.messages.VideoMessage;
import work.my.portfolio.springjms.path.VideoPath;
import work.my.portfolio.springjms.service.VideoService;

@RequiredArgsConstructor(onConstructor_ = { @Autowired }, access = AccessLevel.PACKAGE)
@Component
class Receiver {

	private final ObjectMapper mapper;

	private final VideoService service;

	private final VideoPath videoPath;

	@JmsListener(destination = "${jms.destination}", containerFactory = "getFactory", concurrency = "2")
	void receiveMessage(Message messasge) {
		if (messasge instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) messasge;
			try {
				VideoMessage videoMessage = mapper.readValue(textMessage.getText(), VideoMessage.class);

				String fileName = videoMessage.fileName();
				Path fileFromClientPath = new File(videoPath.getOrigin() + fileName)
						.toPath();
				Path originalPath = Files.write(fileFromClientPath, service.getVideo(videoPath.getUri(), fileName));

				String outputFileName = service.convert(videoMessage, originalPath.toString());
				LocalDateTime expiredDateTime = service.getDownloadTimeLimit();

				// 整合性を保つ必要がないため、トランザクション不要
				service.postVideoMetaData(videoMessage.fileName(), outputFileName, expiredDateTime);
				service.sendMail(videoMessage.email(),
						videoMessage.fileName(),
						expiredDateTime);
				Files.deleteIfExists(originalPath);
				service.deleteClientVideo(videoPath.getUri(), fileName);
			} catch (Exception e) {
				e.printStackTrace();
				try {
					// メッセージの再配信を防ぐため確認させる(配信保障が不要)
					messasge.acknowledge();
				} catch (JMSException e1) {
					e1.printStackTrace();
					return;
				}
			}
		}
		try {
			messasge.acknowledge();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}