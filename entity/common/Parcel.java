package entity.common;

public class Parcel {

	private int requestId;

	private PersonInfo clientInfo;

	private PersonInfo recipientInfo;

	private static final String COMMA = ",";

	public Parcel(int requestId, PersonInfo clientInfo, PersonInfo recipientInfo) {
		this.requestId     = requestId;
		this.clientInfo    = clientInfo;
		this.recipientInfo = recipientInfo;
	}

	/**
	 * requestIdを取得します。
	 * @return {@link #requestId}
	 */
	public int getRequestId() {
		return this.requestId;
	}

	/**
	 * recipientInfoを取得します。
	 * @return {@link #recipientInfo}
	 */
	public PersonInfo getRecipientInfo() {
		return this.recipientInfo;
	}

	/**
	 * recipientInfoに値を代入します。
	 * @param recipientInfo {@link #recipientInfo}
	 */
	public void setRecipientInfo(PersonInfo recipientInfo) {
		this.recipientInfo = recipientInfo;
	}

	/**
	 * フィールドの受取人個人情報の番地を取得し, それを返します。
	 * @return int
	 */
	public int getAddress() {
		return this.recipientInfo.getAddress();
	}

	/**
	 * 文字列できた情報から荷物クラスのインスタンスを返します。
	 * @params str 荷物クラスを表現した文字列。
	 * @return Parcelオブジェクト。
	 */
	public static Parcel decode(String str) {
		String[] parameters = str.split(COMMA);
		return new Parcel(
			Integer.parseInt(parameters[0]),
			PersonInfo.decode(parameters[1] + COMMA + parameters[2] + COMMA + parameters[3]),
			PersonInfo.decode(parameters[4] + COMMA + parameters[5] + COMMA + parameters[6])
		);
	}

	/**
	 * 荷物を文字列に変換し、その文字列を返します。
	 * @params parcel Parcelオブジェクト。
	 * @return String 通信フォーマットに従った文字列。
	 */
	public static String encode(Parcel parcel) {
		return parcel.requestId + COMMA + PersonInfo.encode(parcel.clientInfo) + COMMA + PersonInfo.encode(parcel.recipientInfo);
	}

}
