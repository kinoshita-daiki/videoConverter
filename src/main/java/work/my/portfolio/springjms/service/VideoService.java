package work.my.portfolio.springjms.service;

import java.io.IOException;
import java.time.LocalDateTime;

import work.my.portfolio.springjms.messages.VideoMessage;
import work.my.portfolio.springjms.model.VideoMeta;

public interface VideoService {

	/**
	 * 動画を変換する
	 * 
	 * @param videoMessage メッセージ
	 * @param originalPath 入力元動画のパス
	 * @throws IOException 変換エラー時に投げる
	 * @returm 変換動画出力先
	 */
	String convert(VideoMessage videoMessage, String originalPath) throws IOException;

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
	 * @param to              宛先
	 * @param fileName        ファイル名
	 * @param expiredDateTime ダウンロード期限
	 */
	void sendMail(String to, String fileName, LocalDateTime expiredDateTime);

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
	 * ダウンロード期限を取得する
	 * 
	 * @return ダウンロード期限
	 */
	LocalDateTime getDownloadTimeLimit();

	/**
	 * クライアント側の動画を削除する
	 * 
	 * @param uri       取得先
	 * @param videoName 動画ファイル名
	 */
	void deleteClientVideo(String uri, String videoName);

	/**
	 * 現時点で期限切れの動画を削除する
	 */
	void deleteExpiredVideos();
}
