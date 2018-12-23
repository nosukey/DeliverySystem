package entity.inPC;

// TODO 削除
import boundary.Boundary;

import comm.HeadquarterCommunication;
import entity.common.Date;
import entity.common.Record;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import entity.common.State;
import entity.common.PersonInfo;

public class Headquarter {

	private List<Record> records;

	private HeadquarterCommunication commToReception;

	private HeadquarterCommunication commToRelayStation;

	private static final int RECEPTION_PORT = 10000;
	private static final String RELAY_STA_ADDRESS = "btspp://0016535DEB1C:1";

	public Headquarter() {
		this.records = new ArrayList<Record>();
	}

	/**
	 * 中継所との通信を確立する
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

	// TODO 削除
	public void connected() {
		Boundary io = new Boundary();
		io.printMessage("Headquarter is connected.");
	}

	// TODO 削除
	public void dummy(HeadquarterCommunication comm, String str) {
		if(comm == commToReception)
			commToRelayStation.writeString(str + " -> Headquarter");
		else
			commToReception.writeString(str + " -> Headquarter");
	}

	/**
	 * ユースケース「発送報告を受け取る」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveTransportStartingReport(List<Record> records, List<Integer> requestIds) {
        this.records.addAll(records);
        for(Integer requestId : requestIds){
            this.records.get(requestId.intValue()).setState(State.ON_DELIVERY);
        }
	}

	/**
	 * ユースケース「中継所引き渡し失敗報告を受け取る」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveTransportFailureReport(List<Integer> requestIds) {
        for(Integer requestId : requestIds){
            this.records.get(requestId.intValue()).setState(State.TRANSPORT_FAILURE);
        }
	}

	/**
	 * ユースケース「中継所到着報告を受け取る」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveTransportSuccessReport(List<Integer> requestIds) {
        for(Integer requestId : requestIds){
            this.records.get(requestId.intValue()).setTransportSuccessDate(Date.getCurrentDate());
        }
	}

	/**
	 * ユースケース「配達開始報告を受け取る」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveDeliveryStartingReport(List<Integer> requestIds) {
        for(Integer requestId : requestIds){
            this.records.get(requestId.intValue()).setDeliveryStartingDate(Date.getCurrentDate());
        }
	}

	/**
	 * ユースケース「配達完了報告を受け取る」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveDeliverySuccessReport(Map<Integer, Date> receivingDateMap) {
        for(Map.Entry<Integer , Date > entry : receivingDateMap.entrySet()){
            Record record = this.records.get(entry.getKey().intValue());
            record.setReceivingDate(entry.getValue());
            record.setDeliverySuccessDate(Date.getCurrentDate());
            record.setState(State.DELIVERY_SUCCESS);
        }
	}

	/**
	 * ユースケース「受取人不在報告を受け取る」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveWithoutRecipientReport(List<Integer> requestIds) {
        for(Integer requestId : requestIds){
            this.records.get(requestId.intValue()).setState(State.RE_DELIVERY);
        }
	}

	/**
	 * ユースケース「宛先間違い報告を受け取る」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveWrongRecipientReport(List<Integer> requestIds) {
         for(Integer requestId : requestIds){
            this.records.get(requestId.intValue()).setState(State.WRONG_RECIPIENT);
        }
	}

	/**
	 * ユースケース「配達記録を参照する」を包含するメソッド
	 */
	public void referRecord() {
        int id;
        Boundary boundary = new Boundary();
        /*
         *依頼IDの入力を要求する
         *入力情報を修正するかの選択を要求する
         */
        do{
            id = boundary.inputRequestId();
        }while(boundary.select("修正する","修正しない"));
        /*
         *修正する場合は最初に戻る
         *修正しない場合は入力されたidが配達記録にあるか確認する
         */
        if(contains(id)){
            Record record;
            String name;
            int address;
            String phoneNumber;

            record= this.records.get(id);

            do{
                do{
                    name = boundary.inputName("依頼人名前: ");
                    address = boundary.inputAddress("依頼人番地: ");
                    phoneNumber = boundary.inputPhoneNumber("依頼人電話番号: ");
                }while(boundary.select("修正する","修正しない"));

                if(record.getClientInfo().equals(new PersonInfo(name, address, phoneNumber))){
                    boundary.printRecord(record);
                    if(record.isWrongRecipient()){
                        boundary.printMessage("参照した配達記録は宛先間違いでした\n");
                        if(boundary.select("修正する","修正しない")){
                            fixWrongRecipient(record);
                        }
                        break;
                    }
                }
                else{
                    boundary.printMessage("入力されたIDの配達記録を参照する権限がありません\n");

                }

            }while(boundary.select("修正する","修正しない"));


        }
        else{
            boundary.printMessage("該当の依頼IDは存在しませんでした\n");
            if(boundary.select("再入力する","再入力しない"))
                referRecord();
        }
	}

	/**
	 * ユースケース「宛先間違いを修正する」を包含するメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void fixWrongRecipient(Record record) {
        Boundary boundary = new Boundary();
        String name;
        int address;
        String phoneNumber;
        PersonInfo recipientInfo;

        do{
            name = boundary.inputName("受取人名前: ");
            address = boundary.inputAddress("受取人番地: ");
            phoneNumber = boundary.inputPhoneNumber("受取人電話番号: ");
        }while(boundary.select("修正する","修正しない"));


        if(boundary.isCorrectPersonInfo(name,address,phoneNumber)){
            recipientInfo = new PersonInfo(name,address,phoneNumber);
            record.setRecipientInfo(recipientInfo);
            record.setState(State.RE_DELIVERY);
            boundary.printRecord(record);
			commToRelayStation.writeMethod("fixWrongRecipient", record.getRequestId(), recipientInfo);
        }
        else{
            boundary.printMessage("入力された個人情報は不正です\n");
            if(boundary.select("修正する","修正しない")){
                fixWrongRecipient(record);
            }
        }
	}

	/**
	 * 引数の依頼IDと配達記録リストの要素数を比較し
	 * 依頼ID <= 要素数 ならばtrue
	 * 依頼ID > 要素数 ならばfalse
	 * を返す
	 */
	private boolean contains(int requestId) {
		if(requestId <= this.records.size())
            return true;
        else
            return false;
	}

}
