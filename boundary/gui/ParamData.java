package boundary.gui;

import entity.common.Date;
import entity.common.PersonInfo;
import entity.common.Record;
import entity.common.State;

/**
 * システムのコントローラとメインフレーム間の情報の受け渡しのデータクラスです。
 * @author 澤田悠暉
 * @version 1.0 (2019/01/14)
*/
public class ParamData {
    private int method;
    private int id;
    private PersonInfo info;
    private PersonInfo recipientInfo;
    private Record record;
    private boolean[] bools;

    /**
     * 受け渡しデータを生成します。
     * @param method メソッド名
    */
    public ParamData(int method) {
        this.method = method;
    }

    /**
     * 受け渡しデータを生成します。
     * @param method メソッド名
     * @param name 名前
     * @param address 番地
     * @param phone 電話番号
    */
    public ParamData(int method, String name, int address, String phone) {
        this.method = method;
        this.info   = new PersonInfo(name, address, phone);
    }

    /**
     * 受け渡しデータを生成します。
     * @param id 依頼ID
     * @param client 依頼人個人情報
     * @param recipient 受取人個人情報
    */
    public ParamData(int id, PersonInfo client, PersonInfo recipient) {
        this.id            = id;
        this.info          = client;
        this.recipientInfo = recipient;
    }

    /**
     * 受け渡しデータを生成します。
     * @param record 配達記録
    */
    public ParamData(Record record) {
        this(record.getRequestId(), record.getClientInfo(), record.getRecipientInfo());
        this.record = record;
    }

    /**
     * 受け渡しデータを生成します。
     * @param method メソッド名
     * @param id 依頼ID
    */
    public ParamData(int method, int id) {
        this.method = method;
        this.id     = id;
    }

    /**
     * 受け渡しデータを生成します。
     * @param method メソッド名
     * @param bools 受取人宅の在否の配列
    */
    public ParamData(int method, boolean[] bools) {
        this.method = method;
        this.bools  = bools;
    }

    /**
     * メソッドのIDを返します。
     * @return メソッドID
    */
    public int getMethod() {
        return this.method;
    }

    /**
     * 名前を返します。
     * @return 名前
    */
    public String getName() {
        return this.info.getName();
    }

    /**
     * 番地を返します。
     * @return 番地
    */
    public int getAddress() {
        return this.info.getAddress();
    }

    /**
     * 電話番号を返します。
     * @return 電話番号
    */
    public String getPhoneNumber() {
        return this.info.getPhoneNumber();
    }

    /**
     * 依頼IDを返します。
     * @return 依頼ID
    */
    public int getRequestId() {
        return this.id;
    }

    /**
     * 依頼人名前を返します。
     * @return 依頼人名前
    */
    public String getClientName() {
        return this.info.getName();
    }

    /**
     * 依頼人番地を返します。
     * @return 依頼人番地
    */
    public int getClientAddress() {
        return this.info.getAddress();
    }

    /**
     * 依頼人電話番号を返します。
     * @return 依頼人電話番号
    */
    public String getClientPhoneNumber() {
        return this.info.getPhoneNumber();
    }

    /**
     * 受取人名前を返します。
     * @return 受取人名前
    */
    public String getRecipientName() {
        return this.recipientInfo.getName();
    }

    /**
     * 受取人番地を返します。
     * @return 受取人番地
    */
    public int getRecipientAddress() {
        return this.recipientInfo.getAddress();
    }

    /**
     * 受取人電話番号を返します。
     * @return 受取人電話番号
    */
    public String getRecipientPhoneNumber() {
        return this.recipientInfo.getPhoneNumber();
    }

    /**
     * 受付時間を文字列に変換し、返します。
     * @return 受付時間の文字列データ
    */
    public String getReceptionDateStr() {
        Date date = this.record.getReceptionDate();
        if(date == null) return null;
        else             return date.toString();
    }

    /**
     * 発送時間を文字列に変換し、返します。
     * @return 発送時間の文字列データ
    */
    public String getTransportStartingDateStr() {
        Date date = this.record.getTransportStartingDate();
        if(date == null) return null;
        else             return date.toString();
    }

    /**
     * 中継所到着時間を文字列に変換し、返します。
     * @return 中継所到着時間の文字列データ
    */
    public String getTransportSuccessDateStr() {
        Date date = this.record.getTransportSuccessDate();
        if(date == null) return null;
        else             return date.toString();
    }

    /**
     * 配達開始時間を文字列に変換し、返します。
     * @return 配達開始時間の文字列データ
    */
    public String getDeliveryStartingDateStr() {
        Date date = this.record.getDeliveryStartingDate();
        if(date == null) return null;
        else             return date.toString();
    }

    /**
     * 受取時間を文字列に変換し、返します。
     * @return 受取時間の文字列データ
    */
    public String getReceivingDateStr() {
        Date date = this.record.getReceivingDate();
        if(date == null) return null;
        else             return date.toString();
    }

    /**
     * 配達完了時間を文字列に変換し、返します。
     * @return 配達完了時間の文字列データ
    */
    public String getDeliverySuccessDateStr() {
        Date date = this.record.getDeliverySuccessDate();
        if(date == null) return null;
        else             return date.toString();
    }

    /**
     * 配達状況を文字列に変換し、返します。
     * @return 配達状況の文字列データ
    */
    public String getStateStr() {
        State state = this.record.getState();
        if(state == null) return null;
        else              return state.toString();
    }

    /**
     * 受取人宅の在否の配列を返します。
     * @return 受取人宅の在否の配列
    */
    public boolean[] getBools() {
        return this.bools;
    }
}
