package work.my.portfolio.springjms.messages;

public record VideoMessage(
		String originalFilename,
		String fileName,
		Integer startMinutes,
		Integer startSeconds,
		Integer endMinutes,
		Integer endSeconds,
		String email) {
}
