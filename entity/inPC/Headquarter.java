package entity.inPC;

// TODO 削除
import boundary.cui.Boundary;

import comm.HeadquarterCommunication;
import entity.common.Date;
import entity.common.Record;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import entity.common.State;
import entity.common.PersonInfo;

/**
 * 本部の役割を担うクラスです。
 * 他のサブシステムを担うクラスと通信を行います。
 * @author 大場 貴斗
 * @version 1.0(2019/01/12)
 */
public class Headquarter {

	private List<Record> records;

	private HeadquarterCommunication commToReception;

	private HeadquarterCommunication commToRelayStation;

	private static final int RECEPTION_PORT = 10000;

	private static final String RELAY_STA_ADDRESS = "btspp://0016535DEB1C:1";

	/**
     * 配達記録を保管するための配達記録リストを初期化します。
     */
	public Headquarter() {
		this.records = new ArrayList<Record>();
	}

	/**
	 * 中継所と宅配受付所の通信を確立します。
	 */
	public void execute() {
		this.commToReception = new HeadquarterCommunication(this, RECEPTION_PORT);
		new Thread(commToReception).start();

		this.commToRelayStation = new HeadquarterCommunication(this, RELAY_STA_ADDRESS);
		new Thread(commToRelayStation).start();

		// TODO 削除
		Boundary io = new Boundary();
		io.printMessage("Headquarter is started.");
	}

	public void connected() {
		Boundary io = new Boundary();
		io.printMessage("Headquarter is connected.");
	}

	/**
	 * 発送報告を受け取るを包含するメソッドです。
	 * @param records 発送報告です。
     * @param requestIds 依頼IDの入っているリストです。
	 *
	 */
	public synchronized void receiveTransportStartingReport(List<Record> records, List<Integer> requestIds) {
        this.records.addAll(records);
        for(Integer requestId : requestIds){
            this.records.get(requestId.intValue()).setState(State.ON_DELIVERY);
        }
	}

	/**
	 * 中継所引き渡し失敗報告を受け取るを包含するメソッドです。
	 * @param requestIds 依頼IDの入っているリストです。
	 *
	 */
	public synchronized void receiveTransportFailureReport(List<Integer> requestIds) {
        for(Integer requestId : requestIds){
            this.records.get(requestId.intValue()).setState(State.TRANSPORT_FAILURE);
        }
	}

	/**
	 * 中継所到着報告を受け取るを包含するメソッドです。
	 * @param requestIds 依頼IDの入っているリストです。
	 *
	 */
	public synchronized void receiveTransportSuccessReport(List<Integer> requestIds) {
        for(Integer requestId : requestIds){
            this.records.get(requestId.intValue()).setTransportSuccessDate(Date.getCurrentDate());
        }
	}

	/**
	 * 配達開始報告を受け取るを包含するメソッドです。
	 * @param requestIds 依頼IDの入っているリストです。
	 *
	 */
	public synchronized void receiveDeliveryStartingReport(List<Integer> requestIds) {
		for(Integer requestId : requestIds){
            this.records.get(requestId.intValue()).setDeliveryStartingDate(Date.getCurrentDate());
        }
	}

	/**
	 * 配達完了報告を受け取るを包含するメソッドです。
	 * @param receivingDateMap 依頼IDと配達完了時間が入っているMapです。
	 *
	 */
	public synchronized void receiveDeliverySuccessReport(Map<Integer, Date> receivingDateMap) {
        for(Map.Entry<Integer , Date > entry : receivingDateMap.entrySet()){
            Record record = this.records.get(entry.getKey().intValue());
            record.setReceivingDate(entry.getValue());
            record.setDeliverySuccessDate(Date.getCurrentDate());
            record.setState(State.DELIVERY_SUCCESS);
        }
	}

	/**
	 * 受取人不在報告を受け取るを包含するメソッドです。
	 * @param requestIds 依頼IDの入っているリストです。
	 *
	 */
	public synchronized void receiveWithoutRecipientReport(List<Integer> requestIds) {
	    for(Integer requestId : requestIds){
            this.records.get(requestId.intValue()).setState(State.RE_DELIVERY);
        }
	}

	/**
	 * 宛先間違い報告を受け取るを包含するメソッドです。
	 * @param requestIds 依頼IDの入っているリストです。
	 *
	 */
	public synchronized void receiveWrongRecipientReport(List<Integer> requestIds) {
		 for(Integer requestId : requestIds){
            this.records.get(requestId.intValue()).setState(State.WRONG_RECIPIENT);
        }
	}

	/**
	 * 配達記録を参照するを包含するメソッドです。
     * @param id 参照したい配達記録のIDです。
	 */
	public synchronized Record referRecord(int id) {
		Record record = null;
		if(contains(id)) {
			record = records.get(id);
		}
		return record;
	}

	/**
     * 配達記録の中から引数で受け取ったpersonInfoと一致する依頼ID全てを配列として渡すメソッドです。
     * @param info 依頼人の個人情報です。
     * @return 依頼IDのint型配列です。
     */
	public synchronized Integer[] getIds(PersonInfo info) {
		List<Integer> ids = new LinkedList<Integer>();
		for(Record record : records) {
			if(record.getClientInfo().equals(info)) {
				ids.add(record.getRequestId());
			}
		}

		if(ids.size()>0)
			return ids.toArray(new Integer[ids.size()]);
		else
			return null;
	}

    /**
     * 宛先間違いだった場合の受取人個人情報を書き換えるメソッドです。
     * @param record 宛先間違いが含まれているあ配達記録です。
     * @param recipientInfo 新しい受取人情報です。
     * @return record 新しい配達記録です。
     */
	public synchronized Record fixWrongRecipient(Record record, PersonInfo recipientInfo) {
        record.setRecipientInfo(recipientInfo);
        record.setState(State.RE_DELIVERY);
		commToRelayStation.writeMethod("fixWrongRecipient", record.getRequestId(), recipientInfo);
		return record;
	}

	/**
	 * 引数の依頼IDと配達記録リストの要素数を比較し
	 * 依頼ID <= 要素数 ならばtrue
	 * 依頼ID > 要素数 ならばfalse
	 * を返すメソッドです。
     * @param requestId 依頼ID
     * @return 比較した結果をboolean型で返します。
	 */
	public boolean contains(int requestId) {
		if(requestId <= this.records.size())
            return true;
        else
            return false;
	}

}
