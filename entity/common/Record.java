package entity.common;

/**
 * 配達記録クラスです。
 * @author 山下京之介
 * @version 1.0 (2019/01/14)
*/
public class Record {

	private int requestId;

	private PersonInfo clientInfo;

	private PersonInfo recipientInfo;

	private Date receptionDate;

	private Date transportStartingDate;

	private Date transportSuccessDate;

	private Date deliveryStartingDate;

	private Date receivingDate;

	private Date deliverySuccessDate;

	private State state;

	private static final String COMMA = ",";

	private static final String BR = "\n";

	/**
	 * 配達記録インスタンスを生成します。
	 * @param id 依頼ID
	 * @param clientInfo 依頼人個人情報
	 * @param recipientInfo 受取人個人情報
	 * @param receptionDate 受付時間
	*/
	public Record(int id, PersonInfo clientInfo, PersonInfo recipientInfo, Date receptionDate) {
		this.requestId = id;
		this.clientInfo = clientInfo;
		this.recipientInfo = recipientInfo;
		this.receptionDate = receptionDate;
		this.state = State.READY;
	}

	/**
	 * requestIdを取得します。
	 * @return {@link #requestId}
	 */
	public int getRequestId() {
		return this.requestId;
	}

	/**
	 * clientInfoを取得します。
	 * @return {@link #clientInfo}
	 */
	public PersonInfo getClientInfo() {
		return this.clientInfo;
	}

	/**
	 * recipientInfoに値を代入します。
	 * @param info {@link #recipientInfo}
	 */
	public void setRecipientInfo(PersonInfo info) {
		this.recipientInfo = info;
	}

	/**
	 * recipientInfoを取得します。
	 * @return {@link #recipientInfo}
	 */
	public PersonInfo getRecipientInfo() { return this.recipientInfo; }

	/**
	 * receptionDateを取得します。
	 * @return {@link #receptionDate}
	 */
	public Date getReceptionDate() { return this.receptionDate; }

	/**
	 * transportStartingDateに値を代入します。
	 * @param date {@link #transportStartingDate}
	 */
	public void setTransportStartingDate(Date date) {
		this.transportStartingDate = date;
	}

	/**
	 * transportStartingDateを取得します。
	 * @return {@link #transportStartingDate}
	 */
	public Date getTransportStartingDate() {
		return this.transportStartingDate;
	}

	/**
	 * transportSuccessDateに値を代入します。
	 * @param date {@link #transportSuccessDate}
	 */
	public void setTransportSuccessDate(Date date) {
		this.transportSuccessDate = date;
	}

	/**
	 * transportSuccessDateを取得します。
	 * @return {@link #transportSuccessDate}
	 */
	public Date getTransportSuccessDate() {
		return this.transportSuccessDate;
	}

	/**
	 * deliveryStartingDateに値を代入します。
	 * @param date {@link #deliveryStartingDate}
	 */
	public void setDeliveryStartingDate(Date date) {
		this.deliveryStartingDate = date;
	}

	/**
	 * deliveryStartingDateを取得します。
	 * @return {@link #deliveryStartingDate}
	 */
	public Date getDeliveryStartingDate() {
		return this.deliveryStartingDate;
	}

	/**
	 * receivingDateに値を代入します。
	 * @param date {@link #receivingDate}
	 */
	public void setReceivingDate(Date date) {
		this.receivingDate = date;
	}

	/**
	 * receivingDateを取得します。
	 * @return {@link #receivingDate}
	 */
	public Date getReceivingDate() {
		return this.receivingDate;
	}
	/**
	 * deliverySuccessDateに値を代入します。
	 * @param date {@link #deliverySuccessDate}
	 */
	public void setDeliverySuccessDate(Date date) {
		this.deliverySuccessDate = date;
	}

	/**
	 * deliverySuccessDateを取得します。
	 * @return {@link #deliverySuccessDate}
	 */
	public Date getDeliverySuccessDate() {
		return this.deliverySuccessDate;
	}

	/**
	 * stateに値を代入します。
	 * @param state {@link #state}
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * stateを取得します。
	 * @return {@link #state}
	 */
	public State getState() {
		return this.state;
	}

	/**
	 * 配達状況 == 宛先間違い
	 * を判定する。
	 * @return boolean
	 */
	public boolean isWrongRecipient() {
		return this.state == State.WRONG_RECIPIENT;
	}

	/**
	 * 文字列できた情報から配達記録クラスのインスタンスを返します。
	 *
	 * TODO もっと良いアルゴリズムに変更したい
	 * @param str 通信フォーマットに従った文字列。
	 * @return Recordオブジェクト。
	 */
	public static Record decode(String str) {
		String[] parameters = str.split(COMMA);
		Record record =  new Record(
			Integer.parseInt(parameters[0]),
			PersonInfo.decode(parameters[1] + COMMA + parameters[2] + COMMA + parameters[3]),
			PersonInfo.decode(parameters[4] + COMMA + parameters[5] + COMMA + parameters[6]),
			Date.decode(parameters[7] + COMMA + parameters[8] + COMMA + parameters[9] + COMMA + parameters[10] + COMMA + parameters[11])
		);
		record.setTransportStartingDate(Date.decode(parameters[12] + COMMA + parameters[13] + COMMA + parameters[14] + COMMA + parameters[15] + COMMA + parameters[16]));
		record.setTransportSuccessDate(Date.decode(parameters[17] + COMMA + parameters[18] + COMMA + parameters[19] + COMMA + parameters[20] + COMMA + parameters[21]));
		record.setDeliveryStartingDate(Date.decode(parameters[22] + COMMA + parameters[23] + COMMA + parameters[24] + COMMA + parameters[25] + COMMA + parameters[26]));
		record.setReceivingDate(Date.decode(parameters[27] + COMMA + parameters[28] + COMMA + parameters[29] + COMMA + parameters[30] + COMMA + parameters[31]));
		record.setDeliverySuccessDate(Date.decode(parameters[32] + COMMA + parameters[33] + COMMA + parameters[34] + COMMA + parameters[35] + COMMA + parameters[36]));
		record.setState(State.decode(parameters[37]));
		return record;
	}

	/**
	 * 配達記録を文字列に変換し、その文字列を返します。
	 * @param record Recordオブジェクト。
	 * @return String 通信フォーマットに従った文字列。
	 */
	public static String encode(Record record) {
		String result = record.requestId + COMMA + PersonInfo.encode(record.clientInfo) + COMMA + PersonInfo.encode(record.recipientInfo) + COMMA;
		result += Date.encode(record.receptionDate) + COMMA + Date.encode(record.transportStartingDate) + COMMA + Date.encode(record.transportSuccessDate) + COMMA;
		result += Date.encode(record.deliveryStartingDate) + COMMA + Date.encode(record.receivingDate) + COMMA + Date.encode(record.deliverySuccessDate) + COMMA;
		return result + State.encode(record.state);
	}

	/**
	 * RecordオブジェクトをStringに変換し、返します。
	 * @return String 配達記録の文字列表現。
	 */
	@Override
	public String toString() {
		final String NOTHING = "--/--/--/ --:--";

		String result = "依頼ID: " + this.requestId + BR
						+ "依頼人: " + this.clientInfo.getName() + BR
						+ "受取人: " + this.recipientInfo.getName() + BR
						+ "受付時間: " + this.receptionDate.toString() + BR;

		if(transportStartingDate != null)
			result += "発送時間: " + this.transportStartingDate.toString() + BR;
		else
			result += "発送時間: " + NOTHING + BR;

		if(transportSuccessDate != null)
			result += "中継所到着時間: " + this.transportSuccessDate.toString() + BR;
		else
			result += "中継所到着時間: " + NOTHING + BR;

		if(deliveryStartingDate != null)
			result += "配達開始時間: " + this.deliveryStartingDate.toString() + BR;
		else
			result += "配達開始時間: " + NOTHING + BR;

		if(receivingDate != null)
			result += "受取時間: " + this.receivingDate.toString() + BR;
		else
			result += "受取時間: " + NOTHING + BR;

		if(deliverySuccessDate != null)
			result += "配達完了時間: " + this.deliverySuccessDate.toString() + BR;
		else
			result += "配達完了時間: " + NOTHING + BR;

		result += "配達状況: " + this.state.toString();

		return result;
	}

}
