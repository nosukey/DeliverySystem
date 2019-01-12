package entity.inPC;

// TODO 削除
import boundary.cui.Boundary;

import comm.ReceptionCommunication;
import controller.ReceptionObserver;
import entity.common.*;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

public class Reception {

	private List<Parcel> undeliveredParcels;

	private List<Parcel> redeliveryParcels;

	private List<Record> recordsForReporting;

	private ReceptionCommunication commToHeadquarter;

	private ReceptionCommunication commToCollector;

	private ReceptionObserver observer;

	private static final int MAX_STORAGE = 10;

	/**
	 * 新規の依頼IDを割り当てるために保持している
	 * 依頼IDを割り当てた直後に+1する
	 */
	private int newId;

	private static final String HEADQUARTER_ADDRESS = "localhost";
	private static final int HEADQUARTER_PORT       = 10000;
	private static final String COLLECTOR_ADDRESS   = "btspp://001653420416:1";

	public Reception() {
		this.undeliveredParcels  = new LinkedList<Parcel>();
		this.redeliveryParcels   = new LinkedList<Parcel>();
		this.recordsForReporting = new ArrayList<Record>();
		this.observer = new ReceptionObserver(this);
		this.newId = 0;
	}

	/**
	 * 本部との通信を確立する
	 * 収集担当ロボットとの通信を確立する
	 * 宅配受付所オブザーバ－をたてる
	 */
	public void execute() {
		this.commToHeadquarter = new ReceptionCommunication(this, HEADQUARTER_ADDRESS, HEADQUARTER_PORT);
		new Thread(commToHeadquarter).start();

		this.commToCollector   = new ReceptionCommunication(this, COLLECTOR_ADDRESS);
		new Thread(commToCollector).start();

		// TODO 削除
		Boundary io = new Boundary();
		io.printMessage("Reception is started.");
	}

	// TODO 削除
	public void connected() {
		Boundary io = new Boundary();
		io.printMessage("Reception is connected.");
	}

	public synchronized void receiveRequest(PersonInfo clientInfo, PersonInfo recipientInfo) {
		Parcel parcel = new Parcel(newId, clientInfo, recipientInfo);
		undeliveredParcels.add(parcel);

		Date receptionDate = Date.getCurrentDate();

		Record record = new Record(newId, clientInfo, recipientInfo, receptionDate);
		recordsForReporting.add(record);

		System.out.println("receiveRequest: " + PersonInfo.encode(clientInfo) + ", " + PersonInfo.encode(recipientInfo));

		newId++;

		// オブザーバー更新するを追加する箇所
		observer.update(undeliveredParcels.size() + redeliveryParcels.size());
	}

	/**
	 * ユースケース「発送させる」を包含しているメソッド
	 * 統合テストで確認してください
	 *
	 */
	public synchronized void promptToTransport() {
 		List<Parcel>  deliveryParcels     = new LinkedList<Parcel>();
 		List<Record>  recordsForTransport = new LinkedList<Record>();
 		List<Integer> redeliveryIdList   = new LinkedList<Integer>();
 		int firstElementNumber = 0;

 		while(MAX_STORAGE > deliveryParcels.size() && (!redeliveryParcels.isEmpty() || !undeliveredParcels.isEmpty())){
 			if(!redeliveryParcels.isEmpty()){
 				Parcel parcel = redeliveryParcels.remove(firstElementNumber);
 				redeliveryIdList.add(parcel.getRequestId());
 				deliveryParcels.add(parcel);
 			}else{
 				Parcel parcel = undeliveredParcels.remove(firstElementNumber);
 				Record record = removeRecordForReport(parcel.getRequestId());
 				recordsForTransport.add(record);
 				deliveryParcels.add(parcel);
 			}
 		}
 		// 収集担当ロボットとの通信
 		commToCollector.writeMethodWithParcels("transportParcels", deliveryParcels);
 		// 操作を書き込む
 		reportTransportStarting(recordsForTransport, redeliveryIdList);
 		// オブザーバー初期化する
 		observer.init(undeliveredParcels.size() + redeliveryParcels.size());
 	}

	/**
	 * ユースケース「中継所引き渡し成功の連絡を受け取る」を包含しているメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveSuccessNotification() {
		observer.update(true);
	}

	/**
	 * ユースケース「中継所引き渡し失敗の連絡を受け取る」を包含しているメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveFailureNotification(List<Parcel> parcels) {
 		reportTransportFailure(parcels);
 		redeliveryParcels.addAll(parcels);

 		observer.update(true);
 	}

	/**
	 * ユースケース「発送を報告する」を包含しているメソッド
	 *
	 *
	 */
	private void reportTransportStarting(List<Record> records, List<Integer> requestIds) {
 		Date transportStartingDate = Date.getCurrentDate();

 		for(Record record : records){
 			record.setTransportStartingDate(transportStartingDate);
 			record.setState(State.ON_DELIVERY);
 		}
 		commToHeadquarter.writeMethod("receiveTransportStartingReport", records, requestIds);
 	}

	/**
	 * ユースケース「中継所引き渡し失敗を報告する」を包含しているメソッド
	 */
	private void reportTransportFailure(List<Parcel> failureParcels) {
		List<Integer> reportTransportFailureIds = newRequestIdList(failureParcels);

		commToHeadquarter.writeMethodWithIds("receiveTransportFailureReport", reportTransportFailureIds);
	}

	/**
	 * 荷物リストの荷物から依頼IDを取得し, 依頼IDリストに追加する
	 * これを繰り返すことで報告用の依頼IDリストを作成する
	 */
	private List<Integer> newRequestIdList(List<Parcel> parcels) {
		List<Integer> requestIds = new LinkedList<Integer>();

		for(Parcel parcel : parcels){
			requestIds.add(parcel.getRequestId());
		}
		return requestIds;
	}

	/**
	 * 受付完了後の配達記録リストから引数で渡された依頼IDの配達記録を取り除く
	 * ループで該当する配達記録を見つけなければならないためメソッドにした
	 */
	private Record removeRecordForReport(int requestId) {
		for(Record record : recordsForReporting){
			if(requestId == record.getRequestId()){
				recordsForReporting.remove(record);
				return record;
			}
		}
		return null;
	}

	public boolean isEmpty() {
		return undeliveredParcels.isEmpty() && redeliveryParcels.isEmpty();
	}

}
