package work.my.portfolio.springjms.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.test.context.jdbc.Sql;

import jakarta.jms.ConnectionFactory;
import work.my.portfolio.springjms.model.VideoMeta;
import work.my.portfolio.springjms.repository.impl.VideoRepositoryImpl;

@JdbcTest
@Sql("VideoRepositoryTest.sql")
class VideoRepositoryTest {

	@MockBean
	private ConnectionFactory connectionFactory;

	@MockBean
	private DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private VideoRepository repository;

	@BeforeEach
	void setUp() {
		this.repository = new VideoRepositoryImpl(jdbcTemplate, namedParameterJdbcTemplate, null, "no op");
	}

	@Test
	void test_findMetaData() {
		VideoMeta meta = repository.findMetaData("fileName1.mp4");
		assertEquals("fileName1.mp4", meta.getFileNameFromClient());
	}

	@Test
	void test_postVideoMetaData() {
		repository.postVideoMetaData("postTest.avi", "postTestOutput.mp4", LocalDateTime.of(2024, 3, 2, 23, 59));
		VideoMeta meta = repository.findMetaData("postTest.avi");
		assertEquals("postTest.avi", meta.getFileNameFromClient());
		assertEquals("postTestOutput.mp4", meta.getOutputFileName());
		assertEquals(LocalDateTime.of(2024, 3, 2, 23, 59), meta.getExpiredDateTime());
	}

	@Test
	void test_findExpiredVideoNameBetween() {
		List<String> names = repository.findExpiredVideoNameBetween(LocalDateTime.of(2024, 3, 17, 23, 59),
				LocalDateTime.of(2024, 3, 18, 0, 30));
		assertIterableEquals(List.of("outputfile2.mp4", "outputfile3.mp4"), names);
	}
}
