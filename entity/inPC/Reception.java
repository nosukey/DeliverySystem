package entity.inPC;

import boundary.Boundary; //追加
import comm.ReceptionCommunication;
import controller.ReceptionObserver;
import entity.common.*;

import java.util.ArrayList; //追加
import java.util.List;
import java.util.LinkedList; //追加

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

	/*--追加---------*/
	private Boundary boundary;

	public Reception(){
		this.undeliveredParcels  = new LinkedList<Parcel>();
		this.redeliveryParcels   = new LinkedList<Parcel>();
		this.recordsForReporting = new ArrayList<Record>();
		this.boundary = new Boundary();
	}

	// テスト用getnimotu
	public List<Parcel> getnimotu(){
		return this.undeliveredParcels;
	}

	/**
	 * 起動する
	 * 本部との通信を確立する
	 * 収集担当ロボットとの通信を確立する
	 * 宅配受付所オブザーバ－をたてる
	 */
	public void execute() {
		// 通信はあとで
		//observer = new ReceptionObserver(this);
	}

	/**
	 * ユースケース「依頼を受ける」を包含しているメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveRequest() {
		boolean isReenter = true;

		while(isReenter){
			PersonInfo clientInfo = createPersonInfo("依頼人");

			if(!boundary.isCorrectPersonInfo(clientInfo.getName(), clientInfo.getAddress(), clientInfo.getPhoneNumber())){
				isReenter = checkReenter();
			}else{
				while(isReenter){
					PersonInfo recipientInfo = createPersonInfo("受取人");

					if(!boundary.isCorrectPersonInfo(recipientInfo.getName(), recipientInfo.getAddress(), recipientInfo.getPhoneNumber())){
						isReenter = checkReenter();
					}else{
						newId++;
						Parcel parcel = new Parcel(newId, clientInfo, recipientInfo);
						undeliveredParcels.add(parcel);
						Date receptionDate = Date.getCurrentDate();
						Record record = new Record(newId, clientInfo, recipientInfo, receptionDate);
						recordsForReporting.add(record);
						boundary.printRecord(record);

						// オブザーバー更新するを追加する箇所
						isReenter = false;
					}
				}
			}
		}
	}

	private PersonInfo createPersonInfo(String person){
		String name     = boundary.inputName(person + "名前を入力してください-> ");
		int address     = boundary.inputAddress(person + "番地を入力してください-> ");
		// phoneNumberをStringに変更するよ！
		int phoneNumber = boundary.inputPhoneNumber(person +"依頼人電話番号を入力してください-> ");
		if(boundary.select("修正する", "修正しない"))
			createPersonInfo(person);
		return new PersonInfo(name, address, phoneNumber);
	}

	private boolean checkReenter(){
		boundary.printMessage("不正な入力があります");
		return boundary.select("再入力する", "再入力しない");
	}

	/**
	 * ユースケース「発送させる」を包含しているメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void promptToTransport() {
		List<Parcel>  deliveryParcels     = new LinkedList<>();
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
		// 操作を書き込む
		reportTransportStarting(recordsForTransport, redeliveryIdList);
		// オブザーバー初期化する
	}

	/**
	 * ユースケース「中継所引き渡し成功の連絡を受け取る」を包含しているメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveSuccessNotification() {
		// オブザーバー更新する(true)
	}

	/**
	 * ユースケース「中継所引き渡し失敗の連絡を受け取る」を包含しているメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveFailureNotification(List<Parcel> parcels) {
		reportTransportFailure(parcels);
		redeliveryParcels.addAll(parcels);

		// オブザーバー更新する
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
			record.setState(State.onDelivery);
		}
		//本部との通信
	}

	/**
	 * ユースケース「中継所引き渡し失敗を報告する」を包含しているメソッド
	 */
	private void reportTransportFailure(List<Parcel> failureParcels) {
		List<Integer> reportTransportFailureIds = newRequestIdList(failureParcels);

		// 本部との通信
		// 操作を書き込む
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
				return record;
			}
		}
		return null;
	}

}
