package boundary.cui;

// test

import entity.common.Record;

/**
 * 標準入力クラスのラップクラス
 */
public class Boundary {

	private Input input;

	public Boundary() {
		this.input = new Input();
	}

	/**
	 * メソッド内容
	 * メッセージを出力し, 入力された名前を返す
	 *
	 * 単体テスト
	 * 引数に名前の入力を促すメッセージを代入し、入力要求をする
	 * ->入力された名前を出力し確認する
	 */
	public String inputName(String msg) {
		return input.inputString(msg);
	}

	/**
	 * メソッド内容
	 * メッセージを出力し, 入力された番地を返す
	 *
	 * 単体テスト
	 * 引数に番地の入力を促すメッセージを代入し、入力要求をする
	 * ->入力された番地を出力し確認する
	 */
	public int inputAddress(String msg) {
		return input.inputInt(msg);
	}

	/**
	 * メソッド内容
	 * メッセージを出力し, 入力された電話番号を返す
	 *
	 * 単体テスト
	 * 引数に電話番号の入力を促すメッセージを代入し、入力を要求する
	 * ->入力された電話番号を出力し確認する
	 */
	public String inputPhoneNumber(String msg) {
		return input.inputString(msg);
	}

	/**
	 * メソッド内容
	 * メッセージを出力し, 入力された依頼IDを返す
	 *
	 * 単体テスト
	 * 引数に依頼IDの入力を促すメッセージを代入し、入力を要求する
	 * ->入力された依頼IDを出力し確認する
	 */
	public int inputRequestId() {
		return input.inputInt("参照したい依頼ID: ");
	}

	/**
	 * メソッド内容
	 * 選択内容を表示し、次の動作の選択をさせる
	 *
	 * 単体テスト
	 * 引数に選択させたい選択肢1と選択肢2を代入し、入力を要求する(選択内容->修正する/しない・再入力をする/しない・宛先修正する/しない)
	 * ->選択肢1が入力されたらtrueを返すことを確認する
	 * ->選択肢2が入力されたらfalseを返すことを確認する
	 */
	public boolean select(String msg1, String msg2) {
		// TODO 置換
		// int in = input.inputInt("(0)" + msg1 + ", (1)" + msg2 + "\n");
		// if(in == 0) {
		// 	return true;
		// } else if(in == 1) {
		// 	return false;
		// } else {
		// 	System.out.println("\n0, 1のどちらかを入力してください.\n");
		// 	return select(msg1, msg2);
		// }
		if(input.inputInt("(0)" + msg1 + ", (1)" + msg2 + "\n") == 0)
			return true;
		else
			return false;
	}

	/**
	 * メソッド内容
	 * ユーザーに状況がわかるようにのメッセージ表示する
	 *
	 * 単体テスト
	 * 引数にメッセージを代入する
	 * -.>引数に渡したメッセージが表示されているか確認する
	 */
	public void printMessage(String msg) {
		System.out.println(msg);
	}

	/**
	 * 単体テスト
	 * 引数に表示させたい配達記録を代入する
	 * ->引数に代入した配達記録が表示されるかを確認する
	 */
	public void printRecord(Record record) {
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println(record.toString());
		System.out.println("-----------------------------------------------------------------------------");
	}

	/**
	 * メソッド内容
	 * 名前が正当であるかを判定する && 番地が正当であるかを判定する && 電話番号が正当であるかを判定する
	 *
	 * 単体テスト
	 * 引数に名前・番地・電話番号を代入する
	 * ->名前・番地・電話番号が全て正しい入力ならtrueを返すことを確認する
	 * ->名前・番地・電話番号でどれか一つでも不正な入力があればfalseを返すことを確認する
	 */
	public boolean isCorrectPersonInfo(String name, int address, String phoneNumber) {
		return isCorrectName(name) && isCorrectAddress(address) && isCorrectPhoneNumber(phoneNumber);
	}

	/**
	 * 空文字でないかを判定する
	 */
	private boolean isCorrectName(String name) {
		return !name.isEmpty();
	}

	/**
	 * 1-16の数値であるかを判定する
	 */
	private boolean isCorrectAddress(int address) {
		return address >= 1 && address <= 16;
	}

	/**
	 * 桁数が正当であるかを判定する
	 */
	private boolean isCorrectPhoneNumber(String phoneNumber) {
		// TODO phoneNumberをString型に変更してから考える
		return true;
	}

}
