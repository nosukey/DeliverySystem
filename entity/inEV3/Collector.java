package entity.inEV3;

import comm.CollectorCommunication;
import entity.common.Parcel;
import java.util.LinkedList;
import java.util.List;

import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class Collector extends Robot {

	private List<Parcel> transportedParcels;

	private CollectorCommunication commToReception;

	private CollectorCommunication commToRelayStation;

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

	// TODO 削除
	public void dummy(CollectorCommunication comm, String str) {
		if(comm == commToReception)
			commToRelayStation.writeString(str + " -> Collector");
		else
			commToReception.writeString(str + " -> Collector");
	}

	/**
	 * ユースケース「荷物を搬送する」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void transportParcels(List<Parcel> parcels) {
		//TODO EV3確かめ用　消すこと
		//writer.open("testCollector3.txt");

		this.transportedParcels.addAll(parcels);
		isRightSide=true;
		moveFromReceptionToRelaySta();

		//TODO テスト終わったら変数削除しても良い
		int passingPlanNum=this.transportedParcels.size();

		//TODO 消す
		System.out.println("passingPlanNum = "+passingPlanNum);

		//TODO EV3確かめ用　消すこと
		//writer.write("passingPlanNum = "+passingPlanNum);

		commToRelayStation.writeMethod("canSendParcels",passingPlanNum);
	}

	/**
	 * ユースケース「中継所引き渡し成功を連絡する」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void notifySuccess() {
		moveFromRelayStaToReception();
		turn();
		isRightSide=true;

		//TODO 消す
		System.out.println("Did notifySuccess");
		System.out.println("isRightSide(true) = "+isRightSide);

		//TODO EV3確かめ用　消すこと
		//writer.write("Did notifySuccess");
		//writer.write("isRightSide(true) = "+isRightSide);

		//TODO 通信
		commToReception.writeMethod("receiveSuccessNotification");

		this.transportedParcels.clear();

		//TODO 消す
		System.out.println("elementsCount(0)　=　"+this.transportedParcels.size());

		//TODO EV3確かめ用　消すこと
		//writer.write("elementsCount(0)　=　"+this.transportedParcels.size());
	}

	/**
	 * ユースケース「中継所引き渡し失敗を連絡する」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void notifyFailure() {
		moveFromRelayStaToReception();
		turn();
		isRightSide=true;

		//TODO 消す
		System.out.println("Did notifyFailure");
		System.out.println("isRightSide(true) = "+isRightSide);

		//TODO EV3確かめ用　消すこと
		//writer.write("Did notifyFailure");
		//writer.write("isRightSide(true) = "+isRightSide);


		//TODO 通信
		commToReception.writeMethodWithParcels("receiveFailureNotification",this.transportedParcels);

		this.transportedParcels.clear();

		//TODO 消す
		System.out.println("elementsCount(0)　=　"+this.transportedParcels.size());

		//TODO EV3確かめ用　消すこと
		//writer.write("elementsCount(0)　=　"+this.transportedParcels.size());
		//TODO EV3確かめ用　消すこと
		//writer.close();
	}

	/**
	 * ユースケース「荷物を渡す」を包含するメソッド
	 */
	public void sendParcels() {
		//TODO 消す
		System.out.println("Did sendParcels");

		//TODO EV3確かめ用　消すこと
		//writer.write("Did sendParcels");

		commToRelayStation.writeMethodWithParcels("receiveParcels",this.transportedParcels);

		notifySuccess();

		//TODO EV3確かめ用　消すこと
		//writer.close();
	}

	/**
	 * 現段階では,
	 * 宅配受付所から中継所までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromReceptionToRelaySta() {
		moveFromReceptionToEntryPoint();

		while(!checkCanEntry()){
		//決まり次第追加する
		//何秒か待つ
		Delay.msDelay(10000);
	}

		moveFromEntryPointToRelaySta();
		rotate(180);
		isRightSide=false;

		//TODO 消す
		System.out.println("Did moveFromReceptionToRelaySta");
		System.out.println("isRightSide(false) = "+isRightSide);

		//TODO EV3確かめ用　消すこと
		//writer.write("Did moveFromReceptionToRelaySta");
		//writer.write("isRightSide(false) = "+isRightSide);
	}

	/**
	 * 現段階では,
	 * 中継所から宅配受付所までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromRelayStaToReception() {
		lineTrace(80,200);
		lineTrace(465,315);


		//TODO 消す
		System.out.println("Did moveFromRelayStaToReception");

		//TODO EV3確かめ用　消すこと
		//writer.write("Did moveFromRelayStaToReception");

	}

	/**
	 * 現段階では,
	 * 宅配受付所から中継所進入点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromReceptionToEntryPoint() {
		lineTrace(200,315);
		lineTrace(8,365);

	 	//TODO 消す
		System.out.println("Did moveFromReceptionToEntryPoint");

		//TODO EV3確かめ用　消すこと
		//writer.write("Did moveFromReceptionToEntryPoint");
	}

	/**
	 * 現段階では,
	 * 中継所進入点から中継所までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromEntryPointToRelaySta() {
		lineTrace(50,200);


		//TODO 消す
		System.out.println("Did moveFromEntryPointToRelaySta");

		//TODO EV3確かめ用　消すこと
		//writer.write("Did moveFromEntryPointToRelaySta");
	}

	private boolean checkCanEntry(){
		commToRelayStation.writeMethod("checkCanEntry");		// TODO checkCanEntryって名前を変更するかも
		return commToRelayStation.readBoolean();
	}

}
