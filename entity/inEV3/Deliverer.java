package entity.inEV3;

import comm.DelivererCommunication;
import entity.common.Date;
import entity.common.Parcel;
import java.util.List;
import java.util.Map;

public class Deliverer extends Robot {

	private List<Parcel> delivery;

	private Map<Integer, Date> receivingDateMap;

	private List<Parcel> withoutRecipientParcels;

	private List<Parcel> wrongRecipientParcels;

	private DelivererCommunication commToRelayStation;

	private DelivererCommunication commToRecipient;

	/**
	 * ユースケース「待機所で待機する」を包含するメソッド
	 *
	 * ローカル定数
	 * 待機時間 = 20秒
	 *
	 * 統合テストで確認してください
	 *
	 */
	public void waitInStandbyStation() {

	}

	/**
	 * ユースケース「荷物を配達する」を包含するメソッド
	 *
	 * 統合テストで確認してください
	 *
	 */
	public void deliverParcels(List<Parcel> parcels) {

	}

	/**
	 * ユースケース「配達の有無を確認する」を包含するメソッド
	 */
	private void goCheck() {

	}

	/**
	 * ユースケース「配達結果を連絡する」を包含するメソッド
	 */
	private void notifyDeliveryResults() {

	}

	/**
	 * 現段階では,
	 * 待機所から中継所進入点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromStandbyStaToEntryPoint() {

	}

	/**
	 * 現段階では,
	 * 合流点から待機所までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromIntersectionToStandbySta() {

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
	 * 現段階では,
	 * 中継所進入点から合流点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromEntryPointToIntersection() {

	}

	/**
	 * 現段階では,
	 * 合流点から中継所進入点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromIntersectionToEntryPoint() {

	}

	/**
	 * 現段階では,
	 * 合流点から中継所までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromIntersectionToRelaySta() {

	}

	/**
	 * 現段階では,
	 * 中継所から合流点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromRelayStaToIntersection() {

	}

	/**
	 * 現段階では,
	 * 中継所進入点から受取人宅基準点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromEntryPointToStartingPoint() {

	}

	/**
	 * 現段階では,
	 * 受取人宅基準点から中継所進入点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromStartingPointToEntryPoint() {

	}

	/**
	 * 現在行 = (現在番地 / 4)
	 * 目的行 = (目的番地 / 4)
	 * 現在行 == 目的行を判定し,
	 * trueならば, (1区間の距離 * (目的番地 - 現在番地)) だけ正方向に移動する
	 * falseならば, (1区間の距離 * 現在番地) だけ負方向に移動する -> 90度回転 -> (1区間の距離 * 目的番地) だけ移動する
	 *
	 * ローカル定数
	 * 1区間の距離
	 */
	private void moveNextRecipient(int from, int to) {

	}

	/**
	 * 配達用の荷物リスト, 受取時間表, 受取人不在の荷物リスト, 宛先間違いの荷物リストを空にする
	 */
	private void init() {

	}

}
