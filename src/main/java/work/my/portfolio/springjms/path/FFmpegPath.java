package work.my.portfolio.springjms.path;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@ConfigurationProperties(prefix = "path.to.ff")
public class FFmpegPath {

	FFmpegPath() {
	}

	private String mpeg;

	private String probe;
}
