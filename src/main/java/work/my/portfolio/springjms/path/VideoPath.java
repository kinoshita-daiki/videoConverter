package work.my.portfolio.springjms.path;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "file.directory")
@Component
public class VideoPath {

	VideoPath() {
	}

	private String origin;

	private String converted;

	private String uri;
}
