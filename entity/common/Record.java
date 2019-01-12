package entity.common;

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

	public Record(int id, PersonInfo clientInfo, PersonInfo recipientInfo, Date receptionDate) {
		this.requestId = id;
		this.clientInfo = clientInfo;
		this.recipientInfo = recipientInfo;
		this.receptionDate = receptionDate;
		this.state = State.READY;
	}

	public int getRequestId() {
		return this.requestId;
	}

	/**
	 * 単体テスト
	 * 呼び出されたら依頼人個人情報を返すことを確認する
	 *
	 */
	public PersonInfo getClientInfo() {
		return this.clientInfo;
	}

	/**
	 * 単体テスト
	 * 引数に設定したい受取人個人情報を代入する
	 * ->配達記録の受取人個人情報が引数で与えた受取人個人情報になっているか確認する
	 *
	 */
	public void setRecipientInfo(PersonInfo info) {
		this.recipientInfo = info;
	}

	public PersonInfo getRecipientInfo() {
		return this.recipientInfo;
	}

	public Date getReceptionDate() {
		return this.receptionDate;
	}

	/**
	 * 単体テスト
	 * 引数に設定したい発送時間を代入する
	 * ->配達記録の発送時間が引数で与えた発送時間になっているか確認する
	 *
	 */
	public void setTransportStartingDate(Date date) {
		this.transportStartingDate = date;
	}

	public Date getTransportStartingDate() {
		return this.transportStartingDate;
	}

	/**
	 * 単体テスト
	 * 引数に設定したい中継所到着時間を代入する
	 * ->配達記録の中継所到着時間が引数で与えた中継所到着時間になっているか確認する
	 *
	 */
	public void setTransportSuccessDate(Date date) {
		this.transportSuccessDate = date;
	}

	public Date getTransportSuccessDate() {
		return this.transportSuccessDate;
	}

	/**
	 * 単体テスト
	 * 引数に設定したい配達開始時間を代入する
	 * -.>配達記録の配達開始時間が引数で与えた配達開始時間になっているか確認する
	 *
	 */
	public void setDeliveryStartingDate(Date date) {
		this.deliveryStartingDate = date;
	}

	public Date getDeliveryStartingDate() {
		return this.deliveryStartingDate;
	}

	/**
	 * 単体テスト
	 * 引数に設定したい受取時間を代入する
	 * ->配達記録の受取時間が引数で与えた受取時間になっているか確認する
	 *
	 */
	public void setReceivingDate(Date date) {
		this.receivingDate = date;
	}

	public Date getReceivingDate() {
		return this.receivingDate;
	}

	/**
	 * 単体テスト
	 * 引数に設定したい配達完了時間を代入する
	 * -.>配達記録の配達完了時間が引数で与えた配達完了時間になっているか確認する
	 *
	 */
	public void setDeliverySuccessDate(Date date) {
		this.deliverySuccessDate = date;
	}

	public Date getDeliverySuccessDate() {
		return this.deliverySuccessDate;
	}

	/**
	 * 引数に設定したい配達状況を代入する
	 * 配達記録の配達状況が引数で与えた配達状況になっているか確認する
	 *
	 */
	public void setState(State state) {
		this.state = state;
	}

	public State getState() {
		return this.state;
	}

	/**
	 * メソッド内容
	 * 配達状況 == 宛先間違い
	 * を判定する
	 *
	 * 単体テスト
	 * 配達記録の配達状況を判定する
	 * ->「宛先間違い」であればtrueを返すことを確認する
	 * ->「宛先間違い」でなけれfalseを返すことを確認する
	 *
	 */
	public boolean isWrongRecipient() {
		if(this.state == State.WRONG_RECIPIENT)
			return true;
		else
			return false;
	}

	/**
	 * メソッド内容
	 * 文字列できた情報から配達記録クラスのインスタンスを返す
	 *
	 * 単体テスト
	 * 引数に配達記録に変換したい文字列を代入する
	 * ->文字列の情報と同じ配達記録を返すことを確認する
	 *
	 * TODO もっと良いアルゴリズムに変更したい
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
	 * メソッド内容
	 * 配達記録を文字列に変換し、その文字列を返す
	 *
	 * 単体テスト
	 * 引数に文字列に変換したい配達記録を代入する
	 * ->配達記録の情報と同じ情報の文字列を返すことを確認する
	 *
	 */
	public static String encode(Record record) {
		String result = record.requestId + COMMA + PersonInfo.encode(record.clientInfo) + COMMA + PersonInfo.encode(record.recipientInfo) + COMMA;
		result += Date.encode(record.receptionDate) + COMMA + Date.encode(record.transportStartingDate) + COMMA + Date.encode(record.transportSuccessDate) + COMMA;
		result += Date.encode(record.deliveryStartingDate) + COMMA + Date.encode(record.receivingDate) + COMMA + Date.encode(record.deliverySuccessDate) + COMMA;
		return result + State.encode(record.state);
	}

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
