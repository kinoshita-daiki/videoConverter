package work.my.portfolio.springjms.path;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * FFmpegへのパス
 * 
 * @author kinoshita daiki
 * @since 2024/03/02
 */
@Component
@Data
@ConfigurationProperties(prefix = "path.to.ff")
public class FFmpegPath {

	FFmpegPath() {
	}

	private String mpeg;

	private String probe;
}
