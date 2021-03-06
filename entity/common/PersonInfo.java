package entity.common;

/**
 * 個人情報クラスです。
 * @author 池田はるか
 * @version 1.0
*/
public class PersonInfo {

	private String name;

	private int address;

	private String phoneNumber;

	private static final String COMMA = ",";

	/**
	 * 個人情報インスタンスを生成します。
	 * @param name 名前
	 * @param address 番地
	 * @param phoneNumber 電話番号
	*/
	public PersonInfo(String name, int address, String phoneNumber) {
		this.name = name;
		this.address = address;
		this.phoneNumber = phoneNumber;
	}

	/**
	 * フィールドの名前, 引数の名前
	 * フィールドの番地, 引数の番地
	 * フィールドの電話番号, 引数の電話番号
	 * をそれぞれ比較し, すべて一致すればtrue, それ以外はfalseを返します。
	 * @param info PersonInfoオブジェクト
	 * @return PersonInfoを比較し一致すればtrueを返す
	 */
	public boolean equals(PersonInfo info) {
		return (this.name.equals(info.name) && this.address == info.address && this.phoneNumber.replaceAll("[-()]","").equals(info.phoneNumber.replaceAll("[-()]","")));
	}

	/**
	 * 名前を取得します。
	 * @return 名前
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 番地を取得します。
	 * @return 番地
	 */
	public int getAddress() {
		return this.address;
	}

	/**
	 * 電話番号を取得します。
	 * @return 電話番号
	 */
	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	/**
	 * 文字列できた情報から個人情報クラスのインスタンスを返します。
	 * @param str 通信フォーマットに従った文字列
	 * @return PersonInfoオブジェクト
	 */
	public static PersonInfo decode(String str) {
		String[] parameters = str.split(COMMA);
		return new PersonInfo(
			parameters[0],
			Integer.parseInt(parameters[1]),
			parameters[2]
		);
	}

	/**
	 * 個人情報を文字列に変換し、その文字列を返します。
	 * @param info PersonInfoオブジェクト
	 * @return 通信フォーマットに従った文字列
	 */
	public static String encode(PersonInfo info) {
		return info.name + COMMA + info.address + COMMA + info.phoneNumber;
	}

}
