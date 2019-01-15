package entity.common;

/**
 * 配達記録クラスです。
 * @author 大久保美涼
 * @version 1.0
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
	 * 依頼IDを取得します。
	 * @return 依頼ID
	 */
	public int getRequestId() {
		return this.requestId;
	}

	/**
	 * 依頼人個人情報を取得します。
	 * @return 依頼人個人情報
	 */
	public PersonInfo getClientInfo() {
		return this.clientInfo;
	}

	/**
	 * 受取人個人情報に値を代入します。
	 * @param info 受取人個人情報
	 */
	public void setRecipientInfo(PersonInfo info) {
		this.recipientInfo = info;
	}

	/**
	 * 依頼人個人情報を取得します。
	 * @return 依頼人個人情報
	 */
	public PersonInfo getRecipientInfo() { return this.recipientInfo; }

	/**
	 * 受付時間を取得します。
	 * @return 受付時間
	 */
	public Date getReceptionDate() { return this.receptionDate; }

	/**
	 * 発送時間に値を代入します。
	 * @param date 発送時間
	 */
	public void setTransportStartingDate(Date date) {
		this.transportStartingDate = date;
	}

	/**
	 * 発送時間を取得します。
	 * @return 発送時間
	 */
	public Date getTransportStartingDate() {
		return this.transportStartingDate;
	}

	/**
	 * 中継所到着時間に値を代入します。
	 * @param date 中継所到着時間
	 */
	public void setTransportSuccessDate(Date date) {
		this.transportSuccessDate = date;
	}

	/**
	 * 中継所到着時間を取得します。
	 * @return 中継所到着時間
	 */
	public Date getTransportSuccessDate() {
		return this.transportSuccessDate;
	}

	/**
	 * 中継所到着時間に値を代入します。
	 * @param date 中継所到着時間
	 */
	public void setDeliveryStartingDate(Date date) {
		this.deliveryStartingDate = date;
	}

	/**
	 * 配達開始時間を取得します。
	 * @return 配達開始時間
	 */
	public Date getDeliveryStartingDate() {
		return this.deliveryStartingDate;
	}

	/**
	 * 受取時間に値を代入します。
	 * @param date 受取時間
	 */
	public void setReceivingDate(Date date) {
		this.receivingDate = date;
	}

	/**
	 * 受取時間を取得します。
	 * @return 受取時間
	 */
	public Date getReceivingDate() {
		return this.receivingDate;
	}
	/**
	 * 配達完了時間に値を代入します。
	 * @param date 配達完了時間
	 */
	public void setDeliverySuccessDate(Date date) {
		this.deliverySuccessDate = date;
	}

	/**
	 * 配達完了時間を取得します。
	 * @return 配達完了時間
	 */
	public Date getDeliverySuccessDate() {
		return this.deliverySuccessDate;
	}

	/**
	 * 配達状況に値を代入します。
	 * @param state 配達状況
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * 配達状況を取得します。
	 * @return 配達状況
	 */
	public State getState() {
		return this.state;
	}

	/**
	 * 配達状況が「宛先間違い」であるか
	 * 判定する。
	 * @return 宛先間違いならtrueを返す
	 */
	public boolean isWrongRecipient() {
		return this.state == State.WRONG_RECIPIENT;
	}

	/**
	 * 文字列できた情報から配達記録クラスのインスタンスを返します。
	 * @param str 通信フォーマットに従った文字列
	 * @return Recordオブジェクト
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
	 * @param record Recordオブジェクト
	 * @return 通信フォーマットに従った文字列
	 */
	public static String encode(Record record) {
		String result = record.requestId + COMMA + PersonInfo.encode(record.clientInfo) + COMMA + PersonInfo.encode(record.recipientInfo) + COMMA;
		result += Date.encode(record.receptionDate) + COMMA + Date.encode(record.transportStartingDate) + COMMA + Date.encode(record.transportSuccessDate) + COMMA;
		result += Date.encode(record.deliveryStartingDate) + COMMA + Date.encode(record.receivingDate) + COMMA + Date.encode(record.deliverySuccessDate) + COMMA;
		return result + State.encode(record.state);
	}

	/**
	 * RecordオブジェクトをStringに変換し、返します。
	 * @return 配達記録の文字列表現
	 */
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
