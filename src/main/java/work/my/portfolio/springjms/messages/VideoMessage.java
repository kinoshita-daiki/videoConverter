package work.my.portfolio.springjms.messages;

/**
 * メッセージング用モデル
 * 
 * @author kinoshita daiki
 * @since 2024/03/02
 */
public record VideoMessage(
		String originalFilename,
		String fileName,
		Integer startMinutes,
		Integer startSeconds,
		Integer endMinutes,
		Integer endSeconds,
		String email) {
}
