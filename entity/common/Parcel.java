package entity.common;

public class Parcel {

	private int requestId;

	private PersonInfo clientInfo;

	private PersonInfo recipientInfo;

	public Parcel(int requestId, PersonInfo clientInfo, PersonInfo recipientInfo) {
		this.requestId     = requestId;
		this.clientInfo    = clientInfo;
		this.recipientInfo = recipientInfo;
	}

	/**
	 * 単体テスト
	 * 呼び出されたらフィールドの依頼IDが返ることを確認する
	 *
	 */
	public int getRequestId() {
		return this.requestId;
	}

	/**
	 * 単体テスト
	 * 呼び出されたらフィールドの受取人個人情報が返ることを確認する
	 *
	 */
	public PersonInfo getRecipientInfo() {
		return this.recipientInfo;
	}

	/**
	 * 単体テスト
	 * 引数に設定したい受取人個人情報を代入する
	 * ->荷物の受取人個人情報が引数で与えた受取人情報になっているか確認する
	 *
	 */
	public void setRecipientInfo(PersonInfo recipientInfo) {
		this.recipientInfo = recipientInfo;
	}

	/**
	 * フィールドの受取人個人情報の番地を取得し, それを返す
	 */
	public int getAddress() {
		return this.recipientInfo.getAddress();
	}

	/**
	 * メソッド内容
	 * 文字列できた情報から荷物クラスのインスタンスを返す
	 *
	 * 単体テスト
	 * 引数に荷物に変換したい文字列を代入する
	 * ->文字列の情報と同じ荷物を返すことを確認する
	 *
	 */
	public static Parcel decode(String str) {
		String[] parameters = str.split(",");
		return new Parcel(
			Integer.parseInt(parameters[0]),
			PersonInfo.decode(parameters[1] + "," + parameters[2] + "," + parameters[3]),
			PersonInfo.decode(parameters[4] + "," + parameters[5] + "," + parameters[6])
		);
	}

	/**
	 * メソッド内容
	 * 荷物を文字列に変換し、その文字列を返す
	 *
	 * 単体テスト
	 * 引数に文字列に変換したい荷物を代入する
	 * ->荷物と同じ情報の文字列を返すことを確認する
	 *
	 */
	public static String encode(Parcel parcel) {
		return parcel.requestId + "," + PersonInfo.encode(parcel.clientInfo) + "," + PersonInfo.encode(parcel.recipientInfo);
	}

}
