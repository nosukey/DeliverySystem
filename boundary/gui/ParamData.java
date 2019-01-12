package boundary.gui;

import entity.common.Date;
import entity.common.PersonInfo;
import entity.common.Record;
import entity.common.State;

public class ParamData {
    private int method;
    private int id;
    private PersonInfo info;
    private PersonInfo recipientInfo;
    private Record record;
    private boolean[] bools;

    public ParamData(int method) {
        this.method = method;
    }

    public ParamData(int method, String name, int address, String phone) {
        this.method = method;
        this.info   = new PersonInfo(name, address, phone);
    }

    public ParamData(int id, PersonInfo client, PersonInfo recipient) {
        this.id            = id;
        this.info          = client;
        this.recipientInfo = recipient;
    }

    public ParamData(Record record) {
        this(record.getRequestId(), record.getClientInfo(), record.getRecipientInfo());
        this.record = record;
    }

    public ParamData(int method, int id) {
        this.method = method;
        this.id     = id;
    }

    public ParamData(int method, boolean[] bools) {
        this.method = method;
        this.bools  = bools;
    }

    public int getMethod() {
        return this.method;
    }

    public String getName() {
        return this.info.getName();
    }

    public int getAddress() {
        return this.info.getAddress();
    }

    public String getPhoneNumber() {
        return this.info.getPhoneNumber();
    }

    public int getRequestId() {
        return this.id;
    }

    public String getClientName() {
        return this.info.getName();
    }

    public int getClientAddress() {
        return this.info.getAddress();
    }

    public String getClientPhoneNumber() {
        return this.info.getPhoneNumber();
    }

    public String getRecipientName() {
        return this.recipientInfo.getName();
    }

    public int getRecipientAddress() {
        return this.recipientInfo.getAddress();
    }

    public String getRecipientPhoneNumber() {
        return this.recipientInfo.getPhoneNumber();
    }

    public String getReceptionDateStr() {
        Date date = this.record.getReceptionDate();
        if(date == null) return null;
        else             return date.toString();
    }

    public String getTransportStartingDateStr() {
        Date date = this.record.getTransportStartingDate();
        if(date == null) return null;
        else             return date.toString();
    }

    public String getTransportSuccessDateStr() {
        Date date = this.record.getTransportSuccessDate();
        if(date == null) return null;
        else             return date.toString();
    }

    public String getDeliveryStartingDateStr() {
        Date date = this.record.getDeliveryStartingDate();
        if(date == null) return null;
        else             return date.toString();
    }

    public String getReceivingDateStr() {
        Date date = this.record.getReceivingDate();
        if(date == null) return null;
        else             return date.toString();
    }

    public String getDeliverySuccessDateStr() {
        Date date = this.record.getDeliverySuccessDate();
        if(date == null) return null;
        else             return date.toString();
    }

    public String getStateStr() {
        State state = this.record.getState();
        if(state == null) return null;
        else              return state.toString();
    }

    public boolean[] getBools() {
        return this.bools;
    }
}
