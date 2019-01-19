package entity.inPC;

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
 *本部の役割を担うクラスです。
 *他のサブシステムを担うクラスと通信を行います。
 *@author 大場 貴斗
 *@version 1.0
 */
public class Headquarter {

	private List<Record> records;

	private HeadquarterCommunication commToReception;

	private HeadquarterCommunication commToRelayStation;

	private static final int RECEPTION_PORT = 10000;

	private static final String RELAY_STA_ADDRESS = "btspp://0016535DEB1C:1";

    /**
    *
    *配達記録を保管するlistを作成します。
    *
    */
	public Headquarter() {
		this.records = new ArrayList<Record>();
	}

    /**
     * 中継所と宅配受付所の通信を確立します
     * またスレッドを実行し標準出力のためのBoundaryクラスのインスタンスを生成します。
     *
     */
	public void execute() {
		this.commToReception = new HeadquarterCommunication(this, RECEPTION_PORT);
		new Thread(commToReception).start();

		this.commToRelayStation = new HeadquarterCommunication(this, RELAY_STA_ADDRESS);
		new Thread(commToRelayStation).start();

		Boundary io = new Boundary();
		io.printMessage("Headquarter is started.");
	}

	public void connected() {
		Boundary io = new Boundary();
		io.printMessage("Headquarter is connected.");
	}

    /**
     * 発送報告用の配達記録と再配達依頼IDをリストとして受け取ります
     * またその再配達依頼IDから発送状況を「配達中」に変更します。
     * @param records 宅配受付所から受け取る配送報告用の配達記録リスト
     * @param requestIds 宅配受付所から受け取る再配達用の依頼IDリスト
     *
     */
	public synchronized void receiveTransportStartingReport(List<Record> records, List<Integer> requestIds) {
        this.records.addAll(records);
        for(Integer requestId : requestIds){
            this.records.get(requestId.intValue()).setState(State.ON_DELIVERY);
        }
	}

    /**
     * 宅配受付所から中継所引き渡し失敗した依頼IDを受け取り配達状況を「中継所引き渡し失敗」に更新します。
     * @param requestIds 宅配受付所から受け取る中継所引き渡し失敗報告用の依頼IDリスト
     *
     */
	public synchronized void receiveTransportFailureReport(List<Integer> requestIds) {
        for(Integer requestId : requestIds){
            this.records.get(requestId.intValue()).setState(State.TRANSPORT_FAILURE);
        }
	}

    /**
     * 中継所から中継所到着報告をした依頼IDを受け取り中継所到着時間を更新します。
     * @param requestIds 中継所から受け取る中継所到着報告用の依頼IDリスト
     *
     */
	public synchronized void receiveTransportSuccessReport(List<Integer> requestIds) {
        for(Integer requestId : requestIds){
            this.records.get(requestId.intValue()).setTransportSuccessDate(Date.getCurrentDate());
        }
	}

    /**
     * 中継所から配達記録報告用の依頼IDを受け取り配達開始時間を更新します。
     * @param requestIds 中継所から受け取る配達報告用の依頼IDリスト
     *
     */
	public synchronized void receiveDeliveryStartingReport(List<Integer> requestIds) {
		for(Integer requestId : requestIds){
            this.records.get(requestId.intValue()).setDeliveryStartingDate(Date.getCurrentDate());
        }
	}

    /**
     * 中継所から配達完了報告用の受取時間表を受け取り配達記録に受取時間と配達完了時間を更新し配達状況を「配達済み」に更新します。
     * @param receivingDateMap 中継所から受け取る配達完了報告用の受取時間表
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
     * 中継所から受取人不在報告用の依頼IDを受け取り配達記録の配達状況を「再配達待ち」に更新します。
     * @param requestIds 中継所から受け取る受取人不在報告用の依頼IDリスト
     *
     */
	public synchronized void receiveWithoutRecipientReport(List<Integer> requestIds) {
	    for(Integer requestId : requestIds){
            this.records.get(requestId.intValue()).setState(State.RE_DELIVERY);
        }
	}

    /**
     * 中継所から宛先間違いの依頼IDを受け取り配達記録の配達状況を「宛先間違い」に更新します。
     * @param requestIds 中継所から受け取る宛先間違い報告用の依頼IDリスト
     *
     */
	public synchronized void receiveWrongRecipientReport(List<Integer> requestIds) {
		 for(Integer requestId : requestIds){
            this.records.get(requestId.intValue()).setState(State.WRONG_RECIPIENT);
        }
	}

    /**
     * 入力された依頼IDから配達記録を返します。
     * @param id 参照したい入力された依頼ID
     * @return 依頼IDから一致した配達記録
     */
	public synchronized Record referRecord(int id) {
		Record record = null;
		if(contains(id)) {
			record = records.get(id);
		}
		return record;
	}

    /**
     * 配達記録の中から引数で受け取った依頼人個人情報と一致する依頼ID全てを配列として渡します。
     * @param info 依頼人個人情報
     * @return 依頼人個人情報と一致した依頼IDの配列
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
     * 宛先間違いだった場合の配達記録に新しい受取人個人情報を書き換えます
     * @param record 宛先間違いが含まれている配達記録
     * @param recipientInfo 新しい入力された受取人個人情報
     * @return 受取人個人情報が更新された配達記録
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
     * @return 比較した結果
     */
	public boolean contains(int requestId) {
		return requestId <= this.records.size();
	}

}
