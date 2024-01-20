package work.my.portfolio.springjms.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class VideoMeta {

	private String id;

	private String fileNameFromClient;

	private String outputFileName;

	private LocalDateTime expiredDateTime;
}
