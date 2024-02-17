package work.my.portfolio.springjms.repository.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import work.my.portfolio.springjms.model.VideoMeta;
import work.my.portfolio.springjms.repository.VideoRepository;

@Repository
public class VideoRepositoryImpl implements VideoRepository {

	private final JdbcTemplate jdbcTemplate;

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private final MailSender mailSender;

	private final String urlShortnerUri;

	@Autowired
	public VideoRepositoryImpl(JdbcTemplate jdbcTemplate,
			NamedParameterJdbcTemplate namedParameterJdbcTemplate, MailSender mailSender,
			@Value("${my.url.shortner}") String urlShortnerUri) {
		this.jdbcTemplate = jdbcTemplate;
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
		this.mailSender = mailSender;
		this.urlShortnerUri = urlShortnerUri;
	}

	@Override
	public VideoMeta findMetaData(String fileNameFromClient) {
		String sql = "select * from video_meta where file_name_from_client = ?";
		return jdbcTemplate.queryForObject(sql, new DataClassRowMapper<>(VideoMeta.class), fileNameFromClient);
	}

	@Override
	public void sendMail(String to, String from, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setFrom(from);
		message.setSubject(subject);
		message.setText(text);
		mailSender.send(message);
	}

	@Override
	public String getShortUrl(String originalUrl) {
		return new RestTemplate().postForObject(urlShortnerUri, originalUrl,
				String.class);
	}

	@Override
	public byte[] getVideo(String uri, String fileName) {
		var template = new RestTemplate();
		ResponseEntity<byte[]> response = template
				.getForEntity(uri, byte[].class, fileName);
		return response.getBody();
	}

	@Override
	public void postVideoMetaData(String fileNameFromClient, String outputFileName, LocalDateTime expiredDateTime) {
		String sql = "INSERT INTO VIDEO_META (FILE_NAME_FROM_CLIENT,OUTPUT_FILE_NAME,EXPIRED_DATE_TIME)" //
				+ "VALUES(:FILE_NAME_FROM_CLIENT,:OUTPUT_FILE_NAME,:EXPIRED_DATE_TIME)";
		var mapper = new MapSqlParameterSource();
		mapper.addValue("FILE_NAME_FROM_CLIENT", fileNameFromClient);
		mapper.addValue("OUTPUT_FILE_NAME", outputFileName);
		mapper.addValue("EXPIRED_DATE_TIME", expiredDateTime);
		namedParameterJdbcTemplate.update(sql, mapper);
	}

	@Override
	public List<String> findExpiredVideoNameBetween(LocalDateTime from, LocalDateTime to) {
		if (from == null || to == null) {
			return Collections.emptyList();
		}
		String sql = "select OUTPUT_FILE_NAME from video_meta where EXPIRED_DATE_TIME between ? and ?";
		return jdbcTemplate.queryForList(sql, String.class, from, to);
	}

	@Override
	public void deleteClientVideo(String uri, String videoName) {
		new RestTemplate()
				.delete(uri, videoName);
	}
}
