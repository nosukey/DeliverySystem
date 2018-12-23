package comm;

import entity.common.PersonInfo;
import entity.inEV3.RelayStation;
import java.io.IOException;
import lejos.remote.nxt.BTConnection;
import lejos.remote.nxt.BTConnector;
import lejos.utility.Delay;

import lejos.hardware.lcd.LCD;

public class RelayStationCommunication extends Communication implements Runnable {

	private RelayStation relayStation;
	private String target;
	private BTConnector connector;
	private BTConnection connection;

	public RelayStationCommunication(RelayStation parent) {
		this.relayStation = parent;
		this.target       = null;
		this.connector    = new BTConnector();
		this.connection   = null;
	}

	public RelayStationCommunication(RelayStation parent, String target) {
		this(parent);
		this.target = target;
	}

	/**
	 * 「接続する」を呼び出す
	 * 「文字列を読み込む->操作を選択する」ループに入る
	 */
	public void run() {
		try {
			if(target == null) {
				waitForConnection();
				relayStation.connected();
			} else {
				connect();
			}
		} catch(IOException e) {
			System.out.println("Exception: Connection failed.");
			Delay.msDelay(DELAY_TIME);
			System.exit(1);
		}

		waitForInvoke();
	}

	/**
	 * 第1引数
	 * 操作名には中継所のpublicメソッド名
	 * 第2引数
	 * データにはそのメソッドの引数の文字列データ
	 *
	 * から中継所のpublicメソッドを呼び出す
	 *
	 * TODO あとで
	 */
	protected void selectMethod(String methodName, String data) {
		String[] params = null;

		switch(methodName) {
			case "receiveParcels":
				relayStation.receiveParcels(decodeParcels(data));
				break;
			case "sendParcels":
				relayStation.sendParcels();
				break;
			case "receiveFinishDeliveryNotification":
				params = data.split("&");
				relayStation.receiveFinishDeliveryNotification(
					params[0],
					decodeParcels(params[1]),
					decodeParcels(params[2])
				);
				break;
			case "canEntry":
				writeBoolean(relayStation.canEntry());
				break;
			case "fixWrongRecipient":
				params = data.split("&");
				relayStation.fixWrongRecipient(
					Integer.parseInt(params[0]),
					PersonInfo.decode(params[1])
				);
				break;
			case "canSendParcels":
				relayStation.canSendParcels(Integer.parseInt(data));
				break;
			default:
				break;
		}
	}

	/**
	 * 他の通信クラスとの通信を確立する
	 */
	protected void connect() throws IOException {
		connection = connector.connect(target, BTConnection.RAW);
		if(connection == null)
			throw new IOException("Can't connect.");

		dis = connection.openDataInputStream();
		dos = connection.openDataOutputStream();
	}

	protected void waitForConnection() throws IOException {
		LCD.drawString("Ready", 0, 1);
		connection = connector.waitForConnection(TIMEOUT, BTConnection.RAW);

		dis = connection.openDataInputStream();
		dos = connection.openDataOutputStream();

	}

	// TODO 削除
	protected void dummy(String str) {
		relayStation.dummy(this, str);
	}

}
