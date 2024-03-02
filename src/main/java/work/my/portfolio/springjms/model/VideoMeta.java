package work.my.portfolio.springjms.model;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 動画メタデータ
 * 
 * @author kinoshita daiki
 * @since 2024/03/02
 */
@Data
public class VideoMeta {

	private String id;

	private String fileNameFromClient;

	private String outputFileName;

	private LocalDateTime expiredDateTime;
}
