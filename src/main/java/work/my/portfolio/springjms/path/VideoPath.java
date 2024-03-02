package work.my.portfolio.springjms.path;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 動画へのパス
 * 
 * @author kinoshita daiki
 * @since 2024/03/02
 */
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
