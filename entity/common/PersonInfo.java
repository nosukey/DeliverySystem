package entity.common;

public class PersonInfo {

	private String name;

	private int address;

	private String phoneNumber;

	private static final String COMMA = ",";

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
	 * @params info PersonInfoオブジェクト
	 * @return boolean
	 */
	public boolean equals(PersonInfo info) {
		if(this.name.equals(info.name) && this.address == info.address && this.phoneNumber.replaceAll("[-()]","").equals(info.phoneNumber.replaceAll("[-()]",""))) {
			return true;
		} else {
			return false;
		}
		// refact
		// return (this.name.equals(info.name) && this.address == info.address && this.phoneNumber.replaceAll("[-()]","").equals(info.phoneNumber.replaceAll("[-()]","")))
	}

	/**
	 * nameを取得します。
	 * @return {@link #name}
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * addressを取得します。
	 * @return {@link #address}
	 */
	public int getAddress() {
		return this.address;
	}

	/**
	 * phoneNumberを取得します。
	 * @return {@link #phoneNumber}
	 */
	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	/**
	 * 文字列できた情報から個人情報クラスのインスタンスを返します。
	 * @param str 通信フォーマットに従った文字列。
	 * @return PersonInfoオブジェクト。
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
	 * @param info PersonInfoオブジェクト。
	 * @return String 通信フォーマットに従った文字列。
	 */
	public static String encode(PersonInfo info) {
		return info.name + COMMA + info.address + COMMA + info.phoneNumber;
	}

}
