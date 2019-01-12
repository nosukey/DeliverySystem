package entity.inEV3;

import comm.CollectorCommunication;
import entity.common.Parcel;
import java.util.LinkedList;
import java.util.List;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class Collector extends Robot {

	private List<Parcel> transportedParcels;

	private CollectorCommunication commToReception;

	private CollectorCommunication commToRelayStation;

	private class ButtonEventListener implements KeyListener {
		public void keyPressed(Key key) {
			switch(key.getId()) {
				case Button.ID_UP:
					openSensor();
					break;
				case Button.ID_DOWN:
					LCD.drawString("checkCanEntry(): " + checkCanEntry(), 0, 5);
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

	public static void main(String[] args) {
		Collector myself = new Collector();

		myself.commToRelayStation = new CollectorCommunication(myself);
		myself.commToReception    = new CollectorCommunication(myself);

		LCD.clear();
		LCD.drawString("Started.", 0, 0);

		new Thread(myself.commToRelayStation).start();

		Delay.msDelay(60000);
		new Thread(myself.commToReception).start();

		ButtonEventListener listener = myself.new ButtonEventListener();
		Button.UP.addKeyListener(listener);
		Button.DOWN.addKeyListener(listener);

		LCD.drawString("Ready.", 0, 2);
	}

	public void connected() {
		LCD.drawString("Connected.", 0, 1);

		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
	}

	/**
	 * ユースケース「荷物を搬送する」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void transportParcels(List<Parcel> parcels) {
		System.out.println("transportParcels()");

		this.transportedParcels.addAll(parcels);
		isRightSide=true;

		moveFromReceptionToRelaySta();

		//TODO 消す
		System.out.println("parcels size: " + this.transportedParcels.size());

		commToRelayStation.writeMethod("canSendParcels", this.transportedParcels.size());
	}

	/**
	 * ユースケース「中継所引き渡し成功を連絡する」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void notifySuccess() {
		System.out.println("notifySuccess()");

		moveFromRelayStaToReception();

		turn();
		isRightSide=true;

		//TODO 消す
		System.out.println("isRightSide(true) = "+isRightSide);

		System.out.println("parcels size(0): " + this.transportedParcels.size());
		commToReception.writeMethod("receiveSuccessNotification");

		this.transportedParcels.clear();
		System.out.println("parcels size(0): " + this.transportedParcels.size());
	}

	/**
	 * ユースケース「中継所引き渡し失敗を連絡する」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void notifyFailure() {
		System.out.println("notifyFailure()");

		moveFromRelayStaToReception();

		turn();
		isRightSide=true;

		//TODO 消す
		System.out.println("isRightSide(true) = "+isRightSide);

		System.out.println("parcels size(0): " + this.transportedParcels.size());
		commToReception.writeMethodWithParcels("receiveFailureNotification",this.transportedParcels);

		this.transportedParcels.clear();
		System.out.println("parcels size(0): " + this.transportedParcels.size());
	}

	/**
	 * ユースケース「荷物を渡す」を包含するメソッド
	 */
	public void sendParcels() {
		System.out.println("sendParcels()");

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
		System.out.println("moveFromReceptionToRelaySta()");

		isRightSide = true;

		moveFromReceptionToEntryPoint();

		//決まり次第追加する
		//何秒か待つ
		while(!checkCanEntry()){
			Delay.msDelay(10000);
		}

		moveFromEntryPointToRelaySta();

		rotate(177);
	}

	/**
	 * 現段階では,
	 * 中継所から宅配受付所までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromRelayStaToReception() {
		System.out.println("moveFromRelayStaToReception()");

		isRightSide = true;

		lineTrace(80f,200);
		lineTrace(468.5f,315);
	}

	/**
	 * 現段階では,
	 * 宅配受付所から中継所進入点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromReceptionToEntryPoint() {
		System.out.println("moveFromReceptionToEntryPoint()");

		lineTrace(203f,315);
	}

	/**
	 * 現段階では,
	 * 中継所進入点から中継所までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromEntryPointToRelaySta() {
		System.out.println("moveFromEntryPointToRelaySta()");

		lineTrace(8f, 365);

		isRightSide = false;

		lineTrace(50f,200);
	}

	private boolean checkCanEntry(){
		commToRelayStation.writeMethod("canEntry");
		return commToRelayStation.readBoolean();
	}

}
