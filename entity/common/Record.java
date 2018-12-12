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

	public Record(int id, PersonInfo clientInfo, PersonInfo recipientInfo, Date receptionDate) {
		this.requestId = id;
		this.clientInfo = clientInfo;
		this.recipientInfo = recipientInfo;
		this.receptionDate = receptionDate;
		this.state = State.ready;
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
		this.clientInfo = info;
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

	/**
	 * 単体テスト
	 * 引数に設定したい中継所到着時間を代入する
	 * ->配達記録の中継所到着時間が引数で与えた中継所到着時間になっているか確認する
	 *
	 */
	public void setTransportSuccessDate(Date date) {
		this.transportSuccessDate = date;
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

	/**
	 * 単体テスト
	 * 引数に設定したい受取時間を代入する
	 * ->配達記録の受取時間が引数で与えた受取時間になっているか確認する
	 *
	 */
	public void setReceivingDate(Date date) {
		this.receivingDate = date;
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

	/**
	 * 引数に設定したい配達状況を代入する
	 * 配達記録の配達状況が引数で与えた配達状況になっているか確認する
	 *
	 */
	public void setState(State state) {
		this.state = state;
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
		if(this.state == State.wrongRecipient)
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
	 */
	public static Record decode(String str) {
		return null;
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
	public static String encode(Record Record) {
		return null;
	}

	public String toString() {
		String result = "依頼ID: " + requestId + "\n"
						+ "依頼人: " + clientInfo.getName() + "\n"
						+ "受取人: " + recipientInfo.getName() + "\n"
						+ "受付時間: " + receptionDate.toString() + "\n";

		if(transportStartingDate != null)
			result += "発送時間: " + transportStartingDate.toString() + "\n";
		else
			result += "発送時間: --/--/--/ --:--\n";

		if(transportSuccessDate != null)
			result += "中継所到着時間: " + transportSuccessDate.toString() + "\n";
		else
			result += "発送時間: --/--/--/ --:--\n";

		if(deliveryStartingDate != null)
			result += "配達開始時間: " + deliveryStartingDate.toString() + "\n";
		else
			result += "配達開始時間: --/--/--/ --:--\n";

		if(receivingDate != null)
			result += "受取時間: " + receivingDate.toString() + "\n";
		else
			result += "受取時間: --/--/--/ --:--\n";

		if(deliverySuccessDate != null)
			result += "配達完了時間: " + deliverySuccessDate.toString() + "\n";
		else
			result += "配達完了時間: --/--/--/ --:--\n";

		result += "配達状況: " + state.toString();

		return result;
	}

}
