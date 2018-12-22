package comm;

import entity.inEV3.Collector;
import java.io.IOException;
import lejos.remote.nxt.BTConnection;
import lejos.remote.nxt.BTConnector;
import lejos.utility.Delay;

public class CollectorCommunication extends Communication implements Runnable {

	private Collector collector;
	private BTConnector connector;
	private BTConnection connection;

	public CollectorCommunication(Collector parent) {
		this.collector  = parent;
		this.connector  = new BTConnector();
		this.connection = null;
	}

	/**
	 * 「接続する」を呼び出す
	 * 「文字列を読み込む->操作を選択する」ループに入る
	 */
	public void run() {
		try {
			waitForConnection();
			this.collector.connected();
		} catch(IOException e) {
			System.out.println("Exception: Connection failed.");
			Delay.msDelay(DELAY_TIME);
			System.exit(1);
		}

		waitForInvoke();
	}

	/**
	 * 第1引数
	 * 操作名には収集担当ロボットのpublicメソッド名
	 * 第2引数
	 * データにはそのメソッドの引数の文字列データ
	 *
	 * から収集担当ロボットのpublicメソッドを呼び出す
	 *
	 * TODO あとで
	 */
	protected void selectMethod(String methodName, String data) {
		switch(methodName) {
			case "transportParcels":
				collector.transportParcels(decodeParcels(data));
				break;
			case "notifySuccess":
				collector.notifySuccess();
				break;
			case "notifyFailure":
				collector.notifyFailure();
				break;
			case "sendParcels":
				collector.sendParcels();
				break;
			default:
				break;
		}
	}

	/**
	 * 収集担当ロボットからは接続しにいかないので未実装
	 */
	protected void connect() throws IOException {

	}

	protected void waitForConnection() throws IOException {
		connection = connector.waitForConnection(TIMEOUT, BTConnection.RAW);

		dis = connection.openDataInputStream();
		dos = connection.openDataOutputStream();

		connector.close();
	}

	// TODO 削除
	protected void dummy(String str) {
		this.collector.dummy(this, str);
	}

}
