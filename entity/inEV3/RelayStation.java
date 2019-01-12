package entity.inEV3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import comm.RelayStationCommunication;
import entity.common.Parcel;
import entity.common.PersonInfo;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class RelayStation {

	private List<Parcel> storedParcels;

	private List<Parcel> wrongRecipientParcels;

	private RelayStationCommunication commToHeadquarter;

	private RelayStationCommunication commToCollector;

	private RelayStationCommunication commToDeliverer;

	private boolean canEntry;

	private static final int MAX_STORAGE = 30;

	private int numOfDeliveredParcels;

	private int timesCameDeliveryRobot = 0;


	public static void main(String[] args) {
		RelayStation myself = new RelayStation();
		myself.commToCollector   = new RelayStationCommunication(myself, "Collector1");
		myself.commToDeliverer   = new RelayStationCommunication(myself, "Delivery1");
		myself.commToHeadquarter = new RelayStationCommunication(myself);

		LCD.clear();
		LCD.drawString("Started.", 0, 0);

		new Thread(myself.commToCollector).start();
		new Thread(myself.commToDeliverer).start();

		Delay.msDelay(60000);
		new Thread(myself.commToHeadquarter).start();
	}

	public RelayStation(){
		this.storedParcels         = new LinkedList<Parcel>();
		this.wrongRecipientParcels = new ArrayList<Parcel>();
		this.canEntry              = true;
		this.numOfDeliveredParcels = 0;

		autoSetParcels();
		printBefore();
	}

	private synchronized void printBefore() {
		// System.out.println("Relay before:");
		// System.out.println("stored: " + storedParcels.size());
		// for(Parcel parcel : storedParcels) {
		// 	System.out.println(Parcel.encode(parcel));
		// }
		// System.out.println("wrong: " + wrongRecipientParcels.size());
		// for(Parcel parcel : wrongRecipientParcels) {
		// 	System.out.println(Parcel.encode(parcel));
		// }
	}

	private synchronized void printAfter() {
		// System.out.println("Relay after:");
		// for(Parcel parcel : storedParcels) {
		// 	System.out.println(Parcel.encode(parcel));
		// }
		// System.out.println("wrong: " + wrongRecipientParcels.size());
		// for(Parcel parcel : wrongRecipientParcels) {
		// 	System.out.println(Parcel.encode(parcel));
		// }
	}

	private void autoSetParcels() {
		// this.storedParcels.add(
		// 	new Parcel(
		// 		0,
		// 		new PersonInfo("j", 10, "09010101010"),
		// 		new PersonInfo("a", 1, "09001010101")
		// 	)
		// );
		//
		// // 不在
		// this.storedParcels.add(
		// 	new Parcel(
		// 		1,
		// 		new PersonInfo("j", 10, "09010101010"),
		// 		new PersonInfo("c", 3, "09003030303")
		// 	)
		// );
		//
		// // 電話番号間違い
		// this.storedParcels.add(
		// 	new Parcel(
		// 		2,
		// 		new PersonInfo("j", 10, "09010101010"),
		// 		new PersonInfo("e", 10, "09005050605")
		// 	)
		// );
		//
		// // 名前間違い
		// this.storedParcels.add(
		// 	new Parcel(
		// 		3,
		// 		new PersonInfo("j", 10, "09010101010"),
		// 		new PersonInfo("wrong", 12, "09015151515")
		// 	)
		// );
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
	* ユースケース「荷物を受け取る」を包含するメソッド
	*
	* 統合テストで確認してください
	*
	*/
	public void receiveParcels(List<Parcel> parcels) {
		System.out.println("receiveParcels()");

		// TODO これいらない
		// commToCollector.writeMethod("notifySuccess");

		storedParcels.addAll(parcels);
		reportTransportSuccess(parcels);
		enableEntry();
	}

	/**
	 * ユースケース「荷物を渡す」を包含するメソッド
	 *
	 * 統合テストで確認してください
	 *
	 */
	public void sendParcels() {

		System.out.println("sendParcels()");

		if(!canStartDelivery()){
			commToDeliverer.writeMethod("waitInStandbyStation");

			enableEntry();
		}else{
			commToDeliverer.writeMethodWithParcels("deliverParcels", sortingParcels());

	    	numOfDeliveredParcels = storedParcels.size();
			reportDeliveryStarting();
			enableEntry();
	    	storedParcels.clear();
		}
	}

	private List<Parcel> sortingParcels(){
		List<Parcel> deliveryParcels = new LinkedList<Parcel>();

		for(int i=1; i<16; i++){
			for(Parcel parcel : storedParcels){
				if(i == parcel.getAddress()){
					deliveryParcels.add(parcel);
				}
			}
		}
		return deliveryParcels;
	}

	/**
	 * ユースケース「配達完了連絡を受け取る」を包含するメソッド
	 *
	 * 統合テストで確認してください
	 *
	 */
	public void receiveFinishDeliveryNotification(String strOfReceivingDateMap, List<Parcel> withoutRecipientParcels, List<Parcel> wrongRecipientParcels) {
		reportDeliveryResults(strOfReceivingDateMap, withoutRecipientParcels, wrongRecipientParcels);

		System.out.println("receiveFinishDeliveryNotification()");

		this.storedParcels.addAll(withoutRecipientParcels);
		this.wrongRecipientParcels.addAll(wrongRecipientParcels);
		this.numOfDeliveredParcels = 0;
		sendParcels();

		printAfter();
	}

	/**
	 * ユースケース「ロボットの進入の可否を判定する」を包含するメソッド
	 *
	 * 統合テストで確認してください
	 *
	 */
	public synchronized boolean canEntry() {
 		System.out.println("canEntry(): " + canEntry);
 		boolean returnValue = canEntry;
 		canEntry = false;
 		return returnValue;
 		// 通信を行わないように変更しました。　12/12
 	}

	/**
	 * ユースケース「荷物の宛先を修正する」を包含するメソッド
	 *
	 * 統合テストで確認してください
	 *
	 */
	public void fixWrongRecipient(int requestId, PersonInfo recipientInfo) {
		Parcel parcel = removeWrongRecipientParcel(requestId);
		if(parcel != null) {
			parcel.setRecipientInfo(recipientInfo);
      		storedParcels.add(parcel);
		}
	}

	/**
	 * 引数の引き渡し予定の荷物数 < (貯蔵限度 - 配達中の荷物数 - 配達待ちの荷物リストの要素数 - 宛先間違いの荷物リストの要素数) を判定する
	 * その結果によって, 収集担当ロボットのメソッドを切り替え, 通信によって呼び出す
	 *
	 * 引数に引き渡し予定の荷物数を代入する
	 * 引き渡し可能の場合と引き渡し不可の場合で分岐しているか確認する
	 *
	 */
	public void canSendParcels(int numOfParcelsToSend) {
		System.out.println("canSendParcels()");

		if(numOfParcelsToSend <= (MAX_STORAGE - numOfDeliveredParcels - storedParcels.size() - wrongRecipientParcels.size())){
			commToCollector.writeMethod("sendParcels");
		}else{
			commToCollector.writeMethod("notifyFailure");
		}
	}

	/**
	 * ユースケース「中継所到着を報告する」を包含するメソッド
	 */
	private void reportTransportSuccess(List<Parcel> parcels) {
		List<Integer> ids = newRequestIdList(parcels);
		commToHeadquarter.writeMethodWithIds("receiveTransportSuccessReport", ids);
	}

	/**
	 * ユースケース「配達開始を報告する」を包含するメソッド
	 */
	 private void reportDeliveryStarting() {
 		List<Integer> ids = newRequestIdList(storedParcels);

 		commToHeadquarter.writeMethodWithIds("receiveDeliveryStartingReport", ids);
 	}

	/**
	 * ユースケース「配達結果を報告する」を包含するメソッド
	 */
	private void reportDeliveryResults(String strOfReceivingDateMap, List<Parcel> withoutRecipientParcels, List<Parcel> wrongRecipientParcels) {
		if(!strOfReceivingDateMap.isEmpty()){
			commToHeadquarter.writeMethod("receiveDeliverySuccessReport", strOfReceivingDateMap);
		}
		if(!withoutRecipientParcels.isEmpty()){
			List<Integer> withoutRecipientParcelsIds = newRequestIdList(withoutRecipientParcels);
			commToHeadquarter.writeMethodWithIds("receiveWithoutRecipientReport", withoutRecipientParcelsIds);
		}
		if(!wrongRecipientParcels.isEmpty()){
			List<Integer> wrongRecipientParcelsIds = newRequestIdList(wrongRecipientParcels);
			commToHeadquarter.writeMethodWithIds("receiveWrongRecipientReport", wrongRecipientParcelsIds);
		}
	}

	/**
	 * ユースケース「進入制限を解除する」を包含するメソッド
	 */
	private void enableEntry() {
		// 一定時間待機する処理（EV3で可能ならばなんでも良い）
		Delay.msDelay(10000);
		// ここまで
		canEntry = true;

		System.out.println("Finish enableEntry()");
	}

	/**
	 * (配達待ちの荷物リストの要素数 + 宛先間違いの荷物リストの要素数 >= 3) && (有無の確認回数 >= 有無の確認限度)を判定し,
	 * trueならば, static変数を0にしてtrueを返す
	 * falseならば, そのままfalseを返す
	 *
	 * ローカルなstatic変数
	 * 配達担当ロボットが配達の有無を確認しに来た回数
	 * ローカルな定数
	 * 配達担当ロボットが配達の有無を確認しに来る限度 = 3回
	 *
	 */
	private boolean canStartDelivery() {
		timesCameDeliveryRobot++;

		System.out.println("times: "+timesCameDeliveryRobot);

		// 前半：3つ以上の荷物があるときGo　　　　　　　　　　　　　　　　　　　　　　後半３回以上の仕事確認 && 荷物が１こ以上ある。
		if((storedParcels.size() >= 3) || (timesCameDeliveryRobot >= 3 && (!storedParcels.isEmpty()))){
			timesCameDeliveryRobot = 0;
			return true;
		}
		return false;
	}

	/**
	 * 荷物リストの荷物から依頼IDを取得し, 依頼IDリストに追加する
	 * これを繰り返すことで報告用の依頼IDリストを作成する
	 */
	private List<Integer> newRequestIdList(List<Parcel> parcels) {
		//System.out.println("newRequestIdList");
		List<Integer> requestIds = new LinkedList<Integer>();

		for(Parcel parcel : parcels){
			requestIds.add(parcel.getRequestId());
		}
		//System.out.println(requestIds);
		return requestIds;
	}

	/**
	 * 宛先間違いの荷物リストから引数で渡された依頼IDの荷物を取り除く
	 * ループで該当する荷物を見つけなければならないためメソッドにした
	 */
	 private Parcel removeWrongRecipientParcel(int requestId) {
 		for(Parcel parcel : wrongRecipientParcels){
 			if(requestId == parcel.getRequestId()){
				wrongRecipientParcels.remove(parcel);
 				return parcel;
 			}
 		}
 		return null;
 	}

}
