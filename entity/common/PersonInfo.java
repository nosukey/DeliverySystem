package entity.common;

public class PersonInfo {

	private String name;

	private int address;

	private int phoneNumber;

	public PersonInfo(String name, int address, int phoneNumber) {
		this.name = name;
		this.address = address;
		this.phoneNumber = phoneNumber;
	}

	/**
	 * メソッド内容
	 * フィールドの名前, 引数の名前
	 * フィールドの番地, 引数の番地
	 * フィールドの電話番号, 引数の電話番号
	 * をそれぞれ比較し, すべて一致すればtrue, それ以外はfalseを返す
	 *
	 * 単体テスト
	 * 引数に比較したい個人情報を代入する
	 * ->それぞれ比較して一致していればtrueを返すことを確認する
	 * ->一致していなければfalseを返すことを確認する
	 */
	public boolean equals(PersonInfo info) {
		if(this.name.equals(info.getName()) && this.address == info.getAddress() && this.phoneNumber == info.getPhoneNumber()) {
			return true;
		} else {
			return false;
		}
	}

	public String getName() {
		return this.name;
	}

	/**
	 * 単体テスト
	 * 呼び出されたら個人情報フィールドの番地が返ることを確認する
	 *
	 */
	public int getAddress() {
		return this.address;
	}

	public int getPhoneNumber() {
		return this.phoneNumber;
	}

	/**
	 * メソッド内容
	 * 文字列できた情報から個人情報クラスのインスタンスを返す
	 *
	 * 単体テスト
	 * 引数に個人情報に変換したい文字列を代入する
	 * ->文字列の情報と同じ個人情報を返すことを確認する
	 *
	 */
	public static PersonInfo decode(String str) {
		return null;
	}

	/**
	 * メソッド内容
	 * 個人情報を文字列に変換し、その文字列を返す
	 *
	 * 単体テスト
	 * 引数に文字列に変換したい個人情報を代入する
	 * ->個人情報と同じ情報の文字列を返すことを確認する
	 *
	 */
	public static String encode(PersonInfo info) {
		return null;
	}

}
