package comm;

import entity.inEV3.Collector;
import java.io.IOException;
import lejos.remote.nxt.BTConnection;
import lejos.remote.nxt.BTConnector;
import lejos.utility.Delay;

/**
 * 収集担当ロボット専用の通信クラスです。
 * 他のサブシステムとの通信を確立し、データの受け渡しをサポートします。
 * @author 澤田 悠暉
 * @version 1.0 (2019/01/14)
*/
public class CollectorCommunication extends Communication implements Runnable {

	private Collector collector;
	private BTConnector connector;
	private BTConnection connection;

	/**
	 * 通信の確立に必要なインスタンスを生成します。
	 * @param parent 収集担当ロボット
	*/
	public CollectorCommunication(Collector parent) {
		this.collector  = parent;
		this.connector  = new BTConnector();
		this.connection = null;
	}

	/**
	 * 通信を確立し、他のサブシステムからの命令待ち状態に入ります。
	*/
	public void run() {
		try {
			waitForConnection();
			this.collector.connected();
		} catch(IOException e) {
			e.printStackTrace();
			Delay.msDelay(DELAY_TIME);
			System.exit(1);
		}

		waitForInvoke();
	}

	/**
	 * 収集担当ロボットのメソッドを呼び出します。
	 * @param methodName メソッド名
	 * @param data パラメータの文字列データ
	 * {@inheritDoc}
	*/
	@Override
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
	 * {@inheritDoc}
	*/
	@Override
	protected void connect() throws IOException {

	}

	/**
	 * {@inheritDoc}
	*/
	@Override
	protected void waitForConnection() throws IOException {
		connection = connector.waitForConnection(TIMEOUT, BTConnection.RAW);

		dis = connection.openDataInputStream();
		dos = connection.openDataOutputStream();

		connector.close();
	}
}
