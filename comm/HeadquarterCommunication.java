package comm;

public class HeadquarterCommunication extends Communication {

	/**
	 * 「接続する」を呼び出す
	 * 「文字列を読み込む->操作を選択する」ループに入る
	 */
	public void run() {

	}

	/**
	 * 第1引数
	 * 操作名には本部のpublicメソッド名
	 * 第2引数
	 * データにはそのメソッドの引数の文字列データ
	 *
	 * から本部のpublicメソッドを呼び出す
	 */
	protected void selectMethod(String methodName, String data) {

	}

	/**
	 * 他の通信クラスとの通信を確立する
	 */
	protected void connect(String connectionName) {

	}

}
