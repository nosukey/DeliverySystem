package comm;

import entity.common.PersonInfo;
import entity.inEV3.RelayStation;
import java.io.IOException;
import lejos.remote.nxt.BTConnection;
import lejos.remote.nxt.BTConnector;
import lejos.utility.Delay;

import lejos.hardware.lcd.LCD;

/**
 * 中継所専用の通信クラスです。
 * 他のサブシステムとの通信を確立し、データの受け渡しをサポートします。
 * @author 澤田 悠暉
 * @version 1.0 (2019/01/14)
*/
public class RelayStationCommunication extends Communication implements Runnable {

	private RelayStation relayStation;
	private String target;
	private BTConnector connector;
	private BTConnection connection;
	
	private static final String PARAM_SEPARATION  = "&";

	/**
	 * 通信の確立に必要なインスタンスを生成します。
	 * このコンストラクタを用いて生成されたインスタンスは対PCの通信を実現します。
	 * @param parent 中継所
	*/
	public RelayStationCommunication(RelayStation parent) {
		this.relayStation = parent;
		this.target       = null;
		this.connector    = new BTConnector();
		this.connection   = null;
	}

	/**
	 * 通信の確立に必要なインスタンスを生成します。
	 * このコンストラクタを用いて生成されたインスタンスは対EV3の通信を実現します。
	 * @param parent 中継所
	 * @param target デバイスの名前やアドレス
	*/
	public RelayStationCommunication(RelayStation parent, String target) {
		this(parent);
		this.target = target;
	}

	/**
	 * 通信を確立し、他のサブシステムからの命令待ち状態に入ります。
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
	 * 中継所のメソッドを呼び出します。
	 * @param methodName メソッド名
	 * @param data パラメータの文字列データ
	 * {@inheritDoc}
	*/
	@Override
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
				params = data.split(PARAM_SEPARATION);
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
				params = data.split(PARAM_SEPARATION);
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
	 * {@inheritDoc}
	*/
	@Override
	protected void connect() throws IOException {
		connection = connector.connect(target, BTConnection.RAW);
		if(connection == null)
			throw new IOException("Can't connect.");

		dis = connection.openDataInputStream();
		dos = connection.openDataOutputStream();
	}

	/**
	 * {@inheritDoc}
	*/
	@Override
	protected void waitForConnection() throws IOException {
		LCD.drawString("Ready", 0, 2);
		connection = connector.waitForConnection(TIMEOUT, BTConnection.RAW);

		dis = connection.openDataInputStream();
		dos = connection.openDataOutputStream();

	}

}
