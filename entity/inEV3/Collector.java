package entity.inEV3;

import java.util.LinkedList;
import java.util.List;

import comm.CollectorCommunication;
import entity.common.Parcel;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

/**
 *サブシステム「収集担当ロボット」クラスです。
 *他のサブシステム「宅配受付所」クラス、「中継所」クラスと通信を行います。
 *@author 池田はるか 吉野鷹
 *@version 1.0
 */
public class Collector extends Robot {

	private List<Parcel> transportedParcels;

	private CollectorCommunication commToReception;

	private CollectorCommunication commToRelayStation;

	private class ButtonEventListener implements KeyListener {
		public void keyPressed(Key key) {
			switch(key.getId()) {
				case Button.ID_UP:
					openSensor();
					setParkingDistance();
					break;
				default:
					break;
			}
		}

		public void keyReleased(Key key) {}
	}

	private Collector() {
		this.transportedParcels = new LinkedList<Parcel>();
		this.commToReception = null;
		this.commToRelayStation = null;
	}

	/**
	 * 収集担当ロボットを起動します。
	 * 中継所、宅配受付所からの接続待ち状態に入ります。
	 * @param args コマンドライン引数
	*/
	public static void main(String[] args) {

		final int CONNECTION_INTERVAL = 60000;

		Collector myself = new Collector();

		myself.commToRelayStation = new CollectorCommunication(myself);
		myself.commToReception    = new CollectorCommunication(myself);

		LCD.clear();
		LCD.drawString("Started.", 0, 0);

		new Thread(myself.commToRelayStation).start();

		Delay.msDelay(CONNECTION_INTERVAL);
		new Thread(myself.commToReception).start();

		ButtonEventListener listener = myself.new ButtonEventListener();
		Button.UP.addKeyListener(listener);

		LCD.drawString("Ready.", 0, 2);
	}

	/**
	 * 通信が正常に確立されたことを表示します。
	 */
	public void connected() {
		final String REFRESH_DISPLAY = "\n\n\n\n\n\n\n";
		System.out.println(REFRESH_DISPLAY);

		LCD.drawString("Connected.", 0, 1);
	}

	/**
	 * 宅配受付所で渡された荷物を宅配受付所から中継所まで搬送します。
	 * @param parcels 宅配受付所で渡される荷物リスト
	 */
	public void transportParcels(List<Parcel> parcels) {
		this.transportedParcels.addAll(parcels);
		setIsRightSide(true);

		moveFromReceptionToRelaySta();

		commToRelayStation.writeMethod("canSendParcels", this.transportedParcels.size());
	}

	/**
	 * 宅配受付所に中継所引き渡し成功を連絡します。
	 */
	public void notifySuccess() {
		moveFromRelayStaToReception();

		turn();

		parking();

		setIsRightSide(true);

		commToReception.writeMethod("receiveSuccessNotification");

		this.transportedParcels.clear();
	}

	/**
	 * 宅配受付所に中継所引き渡し失敗を連絡します。
	 */
	public void notifyFailure() {
		moveFromRelayStaToReception();

		turn();

		parking();

		setIsRightSide(true);

		commToReception.writeMethodWithParcels("receiveFailureNotification",this.transportedParcels);

		this.transportedParcels.clear();
	}

	/**
	 * 中継所に荷物を渡します。
	 */
	public void sendParcels() {
		commToRelayStation.writeMethodWithParcels("receiveParcels",this.transportedParcels);

		notifySuccess();
	}

	/**
	 * 現段階では,
	 * 宅配受付所から中継所までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromReceptionToRelaySta() {
		final int ENTRY_WAITING_TIME = 1000;
		final int ADJAST_MINUTE_ANGLE = 3;
		setIsRightSide(true);

		moveFromReceptionToEntryPoint();

		//何秒か待つ
		while(!checkCanEntry()){
			Delay.msDelay(ENTRY_WAITING_TIME);
		}

		moveFromEntryPointToRelaySta();

		rotate(STRAIGHT_ANGLE-ADJAST_MINUTE_ANGLE);
	}

	/**
	 * 現段階では,
	 * 中継所から宅配受付所までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromRelayStaToReception() {
		final float DISTANCE_RELAYSTA_TO_ENTRYPOINT = 80f;
		final float DISTANCE_ENTRYPOINT_TO_RECEPTION = 455.5f;
		final int FOURTH_GEAR_SPEED = 200;

		setIsRightSide(true);

		lineTrace(DISTANCE_RELAYSTA_TO_ENTRYPOINT,FOURTH_GEAR_SPEED);
		lineTrace(DISTANCE_ENTRYPOINT_TO_RECEPTION,THIRD_GEAR_SPEED);
	}

	/**
	 * 現段階では,
	 * 宅配受付所から中継所進入点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromReceptionToEntryPoint() {
		final float DISTANCE_RECEPTION_TO_ENTRYPOINT = 203f;

		lineTrace(DISTANCE_RECEPTION_TO_ENTRYPOINT,THIRD_GEAR_SPEED);
	}

	/**
	 * 現段階では,
	 * 中継所進入点から中継所までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromEntryPointToRelaySta() {

		final float DISTANCE_MINUTE = 8f;
		final float DISTANCE_ENTRYPOINT_TO_RELAYSTA = 43f;
		final int FOURTH_GEAR_SPEED = 200;
		final int FIFTH_GEAR_SPEED = 365;

		lineTrace(DISTANCE_MINUTE, FIFTH_GEAR_SPEED);

		rotate(6);
		setIsRightSide(false);

		lineTrace(DISTANCE_ENTRYPOINT_TO_RELAYSTA,FOURTH_GEAR_SPEED);
	}

	private boolean checkCanEntry(){
		commToRelayStation.writeMethod("canEntry");
		return commToRelayStation.readBoolean();
	}

}
