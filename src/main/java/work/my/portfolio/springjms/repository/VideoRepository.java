package work.my.portfolio.springjms.repository;

import java.time.LocalDateTime;
import java.util.List;

import work.my.portfolio.springjms.model.VideoMeta;

public interface VideoRepository {

	/**
	 * 変換後動画ファイルのメタデータを取得する
	 * 
	 * @param fileNameFromClient クライアントから取得した動画のファイル名
	 * @return メタデータ
	 */
	VideoMeta findMetaData(String fileNameFromClient);

	/**
	 * メールを送信する
	 * 
	 * @param to      宛先
	 * @param from    送信元
	 * @param subject 件名
	 * @param text    本文
	 */
	void sendMail(String to, String from, String subject, String text);

	/**
	 * 短縮URLを取得する
	 * 
	 * @param originalUrl 元となるURL
	 * @return 短縮URL
	 */
	String getShortUrl(String originalUrl);

	/**
	 * 動画を取得する
	 * 
	 * @param uri      取得先
	 * @param fileName ファイル名
	 * @return 動画
	 * 
	 */
	byte[] getVideo(String uri, String fileName);

	/**
	 * メタデータを保存する
	 * 
	 * @param fileNameFromClient クライアントから取得した動画のファイル名
	 * @param outputFileName     出力先
	 * @param expiredDateTime    ダウンロード期限
	 */
	void postVideoMetaData(String fileNameFromClient, String outputFileName, LocalDateTime expiredDateTime);

	/**
	 * 指定した期間内に期限がある動画のファイル名を取得する。
	 * 
	 * @param from 開始期間
	 * @param to   終了期間
	 * @return 動画ファイル名
	 */
	List<String> findExpiredVideoNameBetween(LocalDateTime from, LocalDateTime to);

	/**
	 * クライアント側の動画を削除する
	 * 
	 * @param uri       操作先
	 * @param videoName 動画ファイル名
	 */
	void deleteClientVideo(String uri, String videoName);
}
