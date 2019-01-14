package entity.inPC;

// TODO 削除
import boundary.cui.Boundary;

import comm.ReceptionCommunication;
import controller.ReceptionObserver;
import entity.common.*;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

/**
 * サブシステム「宅配受付所」クラスです。
 * 他のサブシステム「本部」クラス、「収集担当ロボット」クラスと通信を行います。
 * 荷物の個数や収集担当ロボットの在否を監視するobserverクラスを持っています。
 * @author 大久保美涼
 * @version 1.0(2019/01/13)
*/
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

	/**
	 * 未配達の荷物を保管する荷物リストを初期化します。
	 * 再配達の荷物を保管する荷物リストを初期化します。
	 * 報告用の配達記録を保管する配達記録リストを初期化します。
	 * 宅配受付所オブザーバーのインスタンスを生成します。
	 */
	public Reception() {
		this.undeliveredParcels  = new LinkedList<Parcel>();
		this.redeliveryParcels   = new LinkedList<Parcel>();
		this.recordsForReporting = new ArrayList<Record>();
		this.observer = new ReceptionObserver(this);
		this.newId = 0;
	}

	/**
	 * 本部と収集担当ロボットとの通信を確立します。
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

	/**
	 * 通信を確立された場合に呼び出され、状況を確認することができます。
	*/
	public void connected() {
		Boundary io = new Boundary();
		io.printMessage("Reception is connected.");
	}

	/**
	 * 依頼された情報で荷物と配達記録を作成します。
	 * 荷物を作成します。
	 * 発送時刻を取得します。
	 * 配達記録を作成します。
	 * 宅配受付所オブザーバーをアップデートします。
	 * @param clientInfo 依頼人個人情報。
	 * @param recipientInfo 受取人個人情報。
	 * @return 配達記録
	 */
	public synchronized Record receiveRequest(PersonInfo clientInfo, PersonInfo recipientInfo) {
		Parcel parcel = new Parcel(newId, clientInfo, recipientInfo);
		undeliveredParcels.add(parcel);

		Date receptionDate = Date.getCurrentDate();

		Record record = new Record(newId, clientInfo, recipientInfo, receptionDate);
		recordsForReporting.add(record);

		newId++;

		// オブザーバー更新するを追加する箇所
		observer.update(undeliveredParcels.size() + redeliveryParcels.size());

		return record;
	}

	/**
	 * 保管している再配達の荷物、未配達の荷物を再配達の荷物から順番に最大10個まで荷物を発送します。
	 * 収集担当ロボットと通信し、荷物リストを渡します。
	 * 宅配受付所オブザーバーを初期化します。
	 */
	public synchronized void promptToTransport() {
 		List<Parcel>  deliveryParcels     = new LinkedList<Parcel>();
 		List<Record>  recordsForTransport = new LinkedList<Record>();
 		List<Integer> redeliveryIdList   = new LinkedList<Integer>();
 		int firstElementNumber = 0;

		if(!this.observer.hasCollector()) return;

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
	 * 宅配受付所オブザーバーのアップデートをします。
	 */
	public void receiveSuccessNotification() {
		observer.update(true);
	}

	/**
	 * 中継所引き渡し失敗の連絡を受け取ります。
	 * 中継所引き渡し失敗をした荷物の報告をします。
	 * 引き渡し失敗した荷物を再配達の荷物リストに追加します。
	 * 宅配受付所オブザーバーのアップデートをします。
	 * @param parcels 中継所引き渡し失敗の荷物リスト
	 */
	public void receiveFailureNotification(List<Parcel> parcels) {
 		reportTransportFailure(parcels);
 		redeliveryParcels.addAll(parcels);

 		observer.update(parcels.size(), true);
 	}

	private void reportTransportStarting(List<Record> records, List<Integer> requestIds) {
 		Date transportStartingDate = Date.getCurrentDate();

 		for(Record record : records){
 			record.setTransportStartingDate(transportStartingDate);
 			record.setState(State.ON_DELIVERY);
 		}
 		commToHeadquarter.writeMethod("receiveTransportStartingReport", records, requestIds);
 	}

	private void reportTransportFailure(List<Parcel> failureParcels) {
		List<Integer> reportTransportFailureIds = newRequestIdList(failureParcels);

		commToHeadquarter.writeMethodWithIds("receiveTransportFailureReport", reportTransportFailureIds);
	}

	private List<Integer> newRequestIdList(List<Parcel> parcels) {
		List<Integer> requestIds = new LinkedList<Integer>();

		for(Parcel parcel : parcels){
			requestIds.add(parcel.getRequestId());
		}
		return requestIds;
	}

	private Record removeRecordForReport(int requestId) {
		for(Record record : recordsForReporting){
			if(requestId == record.getRequestId()){
				recordsForReporting.remove(record);
				return record;
			}
		}
		return null;
	}

	/**
	 * 保管している荷物があるか判定します。
	 */
	public boolean isEmpty() {
		return undeliveredParcels.isEmpty() && redeliveryParcels.isEmpty();
	}

}
