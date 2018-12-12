package entity.inEV3;

import comm.CollectorCommunicatioin;
import entity.common.Parcel;
import java.util.List;

public class Collector extends Robot {

	private List<Parcel> transportedParcels;

	private CollectorCommunicatioin commToReception;

	private CollectorCommunicatioin commToRelayStation;

	/**
	 * ユースケース「荷物を搬送する」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void transportParcels(List<Parcel> parcels) {

	}

	/**
	 * ユースケース「中継所引き渡し成功を連絡する」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void notifySuccess() {

	}

	/**
	 * ユースケース「中継所引き渡し失敗を連絡する」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void notifyFailure() {

	}

	/**
	 * ユースケース「荷物を渡す」を包含するメソッド
	 */
	public void sendParcels() {

	}

	/**
	 * 現段階では,
	 * 宅配受付所から中継所までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromReceptionToRelaySta() {

	}

	/**
	 * 現段階では,
	 * 中継所から宅配受付所までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromRelayStaToReception() {

	}

	/**
	 * 現段階では,
	 * 宅配受付所から中継所進入点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromReceptionToEntryPoint() {

	}

	/**
	 * 現段階では,
	 * 中継所進入点から中継所までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromEntryPointToRelaySta() {

	}

	/**
	 * 現段階では宅配受付所で次の出発に向けて方向転換するための処理を記述する予定である
	 */
	private void turn() {

	}

}
