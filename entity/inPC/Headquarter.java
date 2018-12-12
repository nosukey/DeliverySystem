package entity.inPC;

import comm.HeadquarterCommunication;
import entity.common.Date;
import entity.common.Record;
import java.util.List;
import java.util.Map;

public class Headquarter {

	private List<Record> records;

	private HeadquarterCommunication commToReception;

	private HeadquarterCommunication commToRelayStation;

	/**
	 * 中継所との通信を確立する
	 */
	public void execute() {

	}

	/**
	 * ユースケース「発送報告を受け取る」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveTransportStartingReport(List<Record> records, List<Integer> requestIds) {

	}

	/**
	 * ユースケース「中継所引き渡し失敗報告を受け取る」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveTransportFailureReport(List<Integer> requestIds) {

	}

	/**
	 * ユースケース「中継所到着報告を受け取る」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveTransportSuccessReport(List<Integer> requestIds) {

	}

	/**
	 * ユースケース「配達開始報告を受け取る」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveDeliveryStartingReport(List<Integer> requestIds) {

	}

	/**
	 * ユースケース「配達完了報告を受け取る」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveDeliverySuccessReport(Map<Integer, Date> receivingDateMap) {

	}

	/**
	 * ユースケース「受取人不在報告を受け取る」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveWithoutRecipientReport(List<Integer> requestIds) {

	}

	/**
	 * ユースケース「宛先間違い報告を受け取る」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveWrongRecipientReport(List<Integer> requestIds) {

	}

	/**
	 * ユースケース「配達記録を参照する」を包含するメソッド
	 */
	public void referRecord() {

	}

	/**
	 * ユースケース「宛先間違いを修正する」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void fixWrongRecipient() {

	}

	/**
	 * 引数の依頼IDと配達記録リストの要素数を比較し
	 * 依頼ID <= 要素数 ならばtrue
	 * 依頼ID > 要素数 ならばfalse
	 * を返す
	 */
	private boolean contains(int requestId) {
		return false;
	}

}
