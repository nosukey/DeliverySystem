package comm;

public class CollectorCommunicatioin extends Communication {

	/**
	 * 「接続する」を呼び出す
	 * 「文字列を読み込む->操作を選択する」ループに入る
	 */
	public void run() {

	}

	/**
	 * 第1引数
	 * 操作名には収集担当ロボットのpublicメソッド名
	 * 第2引数
	 * データにはそのメソッドの引数の文字列データ
	 *
	 * から収集担当ロボットのpublicメソッドを呼び出す
	 */
	protected void selectMethod(String methodName, String data) {

	}

	/**
	 * 収集担当ロボットからは接続しにいかないので未実装
	 */
	protected void connect(String connectionName) {

	}

}
