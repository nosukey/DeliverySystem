package boundary.cui;

import entity.common.Record;

/**
 * システムに合わせた標準入出力を実現するクラスです。
 * @author 澤田 悠暉
 * @version 1.0 (2019/01/13)
 */
public class Boundary {

	private Input input;

	private static final int MIN_ADDRESS = 1;
	private static final int MAX_ADDRESS = 16;

	/**
	 * 標準入出力のためのインスタンスを生成します。
	*/
	public Boundary() {
		this.input = new Input();
	}

	/**
	 * 入力を促すメッセージを出力され、標準入力を行えます。
	 * その後、入力された名前を返します。
	 * @param msg 名前の入力を促すメッセージ
	 * @return 入力された名前
	 */
	public String inputName(String msg) {
		return input.inputString(msg);
	}

	/**
	 * 入力を促すメッセージを出力され、標準入力を行えます。
	 * その後、入力された番地を返します。
	 * @param msg 番地の入力を促すメッセージ
	 * @return 入力された番地
	 */
	public int inputAddress(String msg) {
		return input.inputInt(msg);
	}

	/**
	 * 入力を促すメッセージを出力され、標準入力を行えます。
	 * その後、入力された電話番号を返します。
	 * @param msg 電話番号の入力を促すメッセージ
	 * @return 入力された電話番号
	 */
	public String inputPhoneNumber(String msg) {
		return input.inputString(msg);
	}

	/**
	 * 入力を促すメッセージを出力され、標準入力を行えます。
	 * その後、入力された依頼IDを返します。
	 * @return 入力された依頼ID
	 */
	public int inputRequestId() {
		final String MESSAGE = "参照したい依頼ID :";

		return input.inputInt(MESSAGE);
	}

	/**
	 * 入力を促すメッセージを出力され、0・1の選択肢を選ぶ形式での標準入力を行えます。
	 * 0を選択した場合にはtrueを返します。
	 * @param msg0 0番の選択補助メッセージ
	 * @param msg1 1番の選択補助メッセージ
	 * @return 0が選択された場合はtrue
	 */
	public boolean select(String msg0, String msg1) {
		final String BRANCH_ZERO = "(0)";
		final String BRANCH_ONE  = "(1)";
		final String SEPARATION  = ", ";
		final String BR          = "\n";

		if(input.inputInt(BRANCH_ZERO + msg0 + SEPARATION + BRANCH_ONE + msg1 + BR) == 0)
			return true;
		else
			return false;
	}

	/**
	 * 引数として与えられた文字列を標準出力します。
	 * @param msg 標準出力したい文字列
	 */
	public void printMessage(String msg) {
		System.out.println(msg);
	}

	/**
     * 配達記録インスタンスを特定のフォーマットに従って標準出力します。
	 * @param record 標準出力したい配達記録
	 */
	public synchronized void printRecord(Record record) {
		final String SEPARATION = "-----------------------------------------------------------------------------";
		System.out.println(SEPARATION);
		System.out.println(record.toString());
		System.out.println(SEPARATION);
	}

	/**
	 * 名前・番地・電話番号が正しいかを判定し、正当である場合はtrueを返します。
	 * @param name 判定する名前
	 * @param address 判定する番地
	 * @param phoneNumber 判定する電話番号
	 * @return 名前・番地・電話番号がすべて正当である場合はtrue
	 */
	public boolean isCorrectPersonInfo(String name, int address, String phoneNumber) {
		return isCorrectName(name) && isCorrectAddress(address) && isCorrectPhoneNumber(phoneNumber);
	}


	private boolean isCorrectName(String name) {
		return !name.isEmpty();
	}

	private boolean isCorrectAddress(int address) {
		return address >= MIN_ADDRESS && address <= MAX_ADDRESS;
	}

	private boolean isCorrectPhoneNumber(String phoneNumber) {
		final String PHONE_REGEX = "^0(\\d[-(]\\d{4}|\\d{2}[-(]\\d{3}|\\d{3}[-(]\\d{2}|\\d{4}[-(]\\d{1})[-)]\\d{4}$|^0\\d{9}$|^(0[5789]0)[-(]\\d{4}[-)]\\d{4}$|^(0[5789]0)\\d{8}$";
		return phoneNumber.matches(PHONE_REGEX);
	}

}
