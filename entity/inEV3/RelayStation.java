package entity.inEV3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import comm.RelayStationCommunication;
import entity.common.Parcel;
import entity.common.PersonInfo;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

/**
 * サブシステム「中継所」クラスです。
 * 他のサブシステム「本部」クラス、「収集担当ロボット」クラス、「配達担当ロボット」クラスとの通信を行います。
 * @author 大久保美涼 山下京之介
 * @version 1.0
 */
public class RelayStation {

	private List<Parcel> storedParcels;

	private List<Parcel> wrongRecipientParcels;

	private RelayStationCommunication commToHeadquarter;

	private RelayStationCommunication commToCollector;

	private RelayStationCommunication commToDeliverer;

	private boolean canEntry;

	private int numOfDeliveredParcels;

	private int timesCameDeliveryRobot = 0;

	private static final int MAX_STORAGE = 30;

	private static final int THRESHOLD_AMOUNT = 3;

	private static final int THRESHOLD_COMING_COUNT = 3;

	private static final String COLLECTOR_ADDRESS = "00:16:53:42:04:16";

	private static final String DELIVERER_ADDRESS = "00:16:53:42:3D:A3";

	private static final int CONNECTION_DELAY = 10000;

	private static final int ENTRY_DELAY = 5000;

	/**
	 * 中継所を起動します。
	 * 収集担当ロボット、配達担当ロボットとの接続を行い、本部からの接続待ち状態に入ります。
	 * @param args コマンドライン引数
	*/
	public static void main(String[] args) {
		RelayStation myself = new RelayStation();
		myself.commToCollector   = new RelayStationCommunication(myself, COLLECTOR_ADDRESS);
		myself.commToDeliverer   = new RelayStationCommunication(myself, DELIVERER_ADDRESS);
		myself.commToHeadquarter = new RelayStationCommunication(myself);

		LCD.clear();
		LCD.drawString("Started.", 0, 0);

		new Thread(myself.commToCollector).start();
		new Thread(myself.commToDeliverer).start();

		Delay.msDelay(CONNECTION_DELAY);
		new Thread(myself.commToHeadquarter).start();
	}

	/**
	 * 配達待ちの荷物を保管する荷物リストを初期化します。
	 * 宛先間違いの荷物を保管する荷物リストを初期化します。
	 */
	public RelayStation(){
		this.storedParcels         = new LinkedList<Parcel>();
		this.wrongRecipientParcels = new ArrayList<Parcel>();
		this.canEntry              = true;
		this.numOfDeliveredParcels = 0;
	}

	/**
	 * 通信を確立された場合に呼び出され、状況を確認することができます。
	*/
	public void connected() {
		LCD.drawString("Connected.", 0, 1);
	}

	/**
	* 収集担当ロボットから荷物を受け取ります。
	* @param parcels 収集担当ロボットから受け取った荷物リスト
	*/
	public void receiveParcels(List<Parcel> parcels) {
		storedParcels.addAll(parcels);
		reportTransportSuccess(parcels);
		enableEntry();
	}

	/**
	 * 配達担当ロボットに配達待ちの荷物リストを渡します。
	 * 配達待ちの荷物リストにある荷物が運べる状態であれば、配達担当ロボットに渡しますが、
	 * 運べない状態であれば、渡しません。
	 */
	public void sendParcels() {
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
		final int MAX_ADDRESS = 16;

		List<Parcel> deliveryParcels = new LinkedList<Parcel>();

		for(int i=1; i<=MAX_ADDRESS; i++){
			for(Parcel parcel : storedParcels){
				if(i == parcel.getAddress()){
					deliveryParcels.add(parcel);
				}
			}
		}
		return deliveryParcels;
	}

	/**
	 * 配達担当ロボットが配達完了した結果を受け取ります。
	 * 配達できた荷物は受取時間と依頼IDの表を本部に報告します。
	 * 不在の荷物は配達待ちの荷物リストに追加します。
	 * 宛先間違いの荷物は宛先間違いの荷物リストに追加します。
	 * @param strOfReceivingDateMap 受取時間表
	 * @param withoutRecipientParcels 受取人不在の荷物リスト
	 * @param wrongRecipientParcels 宛先間違いの荷物リスト
	 */
	public void receiveFinishDeliveryNotification(String strOfReceivingDateMap, List<Parcel> withoutRecipientParcels, List<Parcel> wrongRecipientParcels) {
		reportDeliveryResults(strOfReceivingDateMap, withoutRecipientParcels, wrongRecipientParcels);

		this.storedParcels.addAll(withoutRecipientParcels);
		this.wrongRecipientParcels.addAll(wrongRecipientParcels);
		this.numOfDeliveredParcels = 0;
		sendParcels();
	}

	/**
	 * ロボットが中継所に進入できるか判定をします。
	 * @return 進入できるか
	 */
	public synchronized boolean canEntry() {
 		boolean returnValue = canEntry;
 		canEntry = false;
 		return returnValue;
 	}

	/**
	 * 宛先間違いの荷物リストにある荷物の宛先を修正します。
	 * @param requestId 修正したい荷物の依頼ID
	 * @param recipientInfo 修正したい荷物の受取人個人情報
	 */
	public void fixWrongRecipient(int requestId, PersonInfo recipientInfo) {
		Parcel parcel = removeWrongRecipientParcel(requestId);
		if(parcel != null) {
			parcel.setRecipientInfo(recipientInfo);
      		storedParcels.add(parcel);
		}
	}

	/**
	 * 収集担当ロボットから荷物を受け取るかの判定をします。
	 * 引き渡し予定の荷物数 < (貯蔵限度 - 配達中の荷物数 - 配達待ちの荷物リストの要素数 - 宛先間違いの荷物リストの要素数) の
	 * 条件を満たす場合は荷物を受け取る通信をします。
	 * 条件を満たさない場合は荷物を受け取りません。
	 * @param numOfParcelsToSend 引き渡し予定の荷物数
	 */
	public void canSendParcels(int numOfParcelsToSend) {
		if(numOfParcelsToSend <= (MAX_STORAGE - numOfDeliveredParcels - storedParcels.size() - wrongRecipientParcels.size())){
			commToCollector.writeMethod("sendParcels");
		}else{
			commToCollector.writeMethod("notifyFailure");
		}
	}

	private void reportTransportSuccess(List<Parcel> parcels) {
		List<Integer> ids = newRequestIdList(parcels);
		commToHeadquarter.writeMethodWithIds("receiveTransportSuccessReport", ids);
	}

	private void reportDeliveryStarting() {
 		List<Integer> ids = newRequestIdList(storedParcels);

 		commToHeadquarter.writeMethodWithIds("receiveDeliveryStartingReport", ids);
 	}

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

	private void enableEntry() {
		Delay.msDelay(ENTRY_DELAY);
		canEntry = true;
	}

	private boolean canStartDelivery() {
		timesCameDeliveryRobot++;

		if((storedParcels.size() >= THRESHOLD_AMOUNT) || (timesCameDeliveryRobot >= THRESHOLD_COMING_COUNT && (!storedParcels.isEmpty()))){
			timesCameDeliveryRobot = 0;
			return true;
		}
		return false;
	}

	private List<Integer> newRequestIdList(List<Parcel> parcels) {
		List<Integer> requestIds = new LinkedList<Integer>();

		for(Parcel parcel : parcels){
			requestIds.add(parcel.getRequestId());
		}
		return requestIds;
	}

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
