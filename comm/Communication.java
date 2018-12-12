package comm;

import entity.common.*;
import java.util.List;
import java.util.Map;

public abstract class Communication {

	protected abstract void selectMethod(String methodName, String data);

	protected abstract void connect(String connectionName);

	/**
	 * ストリームから真偽値を読み込む
	 */
	public boolean readBoolean() {
		return false;
	}

	/**
	 * ストリームに真偽値を書き込む
	 */
	public void writeBoolean(boolean bool) {

	}

	/**
	 * ストリームから文字列を読み込む
	 */
	public String readString() {
		return null;
	}

	/**
	 * ストリームに文字列を書き込む
	 */
	public void writeString(String str) {

	}

	/**
	 * 操作名を通信パケットとし, 文字列としてストリームに書き込む
	 *
	 * 単体テスト
	 * 通信パケットを表示し, フォーマットを確認する
	 */
	public void writeMethod(String methodName) {

	}

	/**
	 * 操作名とデータを文字列に変換したものを連結させたものを通信パケットとし, 文字列としてストリームに書き込む
	 *
	 * 単体テスト
	 * 通信パケットを表示し, フォーマットを確認する
	 */
	public void writeMethod(String methodName, int value) {

	}

	/**
	 * 操作名とデータを文字列に変換したものを連結させたものを通信パケットとし, 文字列としてストリームに書き込む
	 *
	 * 単体テスト
	 * 通信パケットを表示し, フォーマットを確認する
	 */
	public void writeMethod(String methodName, String str) {

	}

	/**
	 * 操作名とデータを文字列に変換したものを連結させたものを通信パケットとし, 文字列としてストリームに書き込む
	 *
	 * 単体テスト
	 * 通信パケットを表示し, フォーマットを確認する
	 */
	public void writeMethod(String methodName, int requestId, PersonInfo personInfo) {

	}

	/**
	 * 操作名とデータを文字列に変換したものを連結させたものを通信パケットとし, 文字列としてストリームに書き込む
	 *
	 * 単体テスト
	 * 通信パケットを表示し, フォーマットを確認する
	 */
	public void writeMethod(String methodName, Parcel parcel) {

	}

	/**
	 * 操作名とデータを文字列に変換したものを連結させたものを通信パケットとし, 文字列としてストリームに書き込む
	 *
	 * 単体テスト
	 * 通信パケットを表示し, フォーマットを確認する
	 */
	public void writeMethodWithParcels(String methodName, List<Parcel> parcels) {

	}

	/**
	 * 操作名とデータを文字列に変換したものを連結させたものを通信パケットとし, 文字列としてストリームに書き込む
	 *
	 * 単体テスト
	 * 通信パケットを表示し, フォーマットを確認する
	 */
	public void writeMethodWithIds(String methodName, List<Integer> requestIds) {

	}

	/**
	 * 操作名とデータを文字列に変換したものを連結させたものを通信パケットとし, 文字列としてストリームに書き込む
	 *
	 * 単体テスト
	 * 通信パケットを表示し, フォーマットを確認する
	 */
	public void writeMethod(String methodName, List<Record> records, List<Integer> requestIds) {

	}

	/**
	 * 操作名とデータを文字列に変換したものを連結させたものを通信パケットとし, 文字列としてストリームに書き込む
	 *
	 * 単体テスト
	 * 通信パケットを表示し, フォーマットを確認する
	 */
	public void writeMethod(String methodName, Map<Integer, Date> receivingDateMap, List<Parcel> withoutRecipientParcels, List<Parcel> wrongRecipientParcels) {

	}

	/**
	 * 引数として代入された文字列から操作名を抽出する
	 */
	private String extractMethodName(String packet) {
		return null;
	}

	/**
	 * 引数として代入された文字列からデータを抽出する
	 */
	private String extractData(String packet) {
		return null;
	}

	private List<Integer> decodeToRequestIds(String str) {
		return null;
	}

	private String encodeFromRequestIds(List<Integer> str) {
		return null;
	}

	private List<Parcel> decodeToParcels(String str) {
		return null;
	}

	private String encodeFromParcels(List<Parcel> parcels) {
		return null;
	}

	private List<Record> decodeToRecords(String str) {
		return null;
	}

	private String encodeFromRecords(List<Record> records) {
		return null;
	}

	private Map<Integer, Date> decodeToDateMap(String str) {
		return null;
	}

	private String encodeFromDateMap(Map<Integer, Date> dateMap) {
		return null;
	}

}
