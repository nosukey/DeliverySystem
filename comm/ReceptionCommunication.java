package comm;

public class ReceptionCommunication extends Communication {

	/**
	 * 「接続する」を呼び出す
	 * 「文字列を読み込む->操作を選択する」ループに入る
	 */
	public void run() {

	}

	/**
	 * 第1引数
	 * 操作名には宅配受付所のpublicメソッド名
	 * 第2引数
	 * データにはそのメソッドの引数の文字列データ
	 *
	 * から宅配受付所のpublicメソッドを呼び出す
	 */
	protected void selectMethod(String methodName, String data) {

	}

	/**
	 * 他の通信クラスとの通信を確立する
	 */
	protected void connect(String connectionName) {

	}

	/**
	 * 他のPCの通信クラスとの通信を確立する
	 */
	private void connectToPC(String connectionName) {

	}

}
