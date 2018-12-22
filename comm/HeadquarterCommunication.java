package comm;

import entity.inPC.Headquarter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;
import lejos.utility.Delay;

public class HeadquarterCommunication extends Communication implements Runnable {

	private Headquarter headquarter;
	private String target;
	private int port;
	private ServerSocket srvSocket;
	private Socket socket;
	private Connection connection;

	private static final int DEFAULT_PORT = -1;

	public HeadquarterCommunication(Headquarter parent, String target) {
		this(parent, target, DEFAULT_PORT);
	}

	public HeadquarterCommunication(Headquarter parent, int port) {
		this(parent, null, port);
	}

	private HeadquarterCommunication(Headquarter parent, String target, int port) {
		this.headquarter = parent;
		this.target      = target;
		this.port        = port;
		this.srvSocket   = null;
		this.socket      = null;
		this.connection  = null;
	}


	/**
	 * 「接続する」を呼び出す
	 * 「文字列を読み込む->操作を選択する」ループに入る
	 */
	public void run() {
		try {
			if(target == null)
				waitForConnection();
			else
				connect();
		} catch(IOException e) {
			System.out.println("Exception: Headquarter's connection failed.");
			Delay.msDelay(DELAY_TIME);
			System.exit(1);
		}

		// TODO 削除
		headquarter.connected();

		waitForInvoke();
	}

	/**
	 * 第1引数
	 * 操作名には本部のpublicメソッド名
	 * 第2引数
	 * データにはそのメソッドの引数の文字列データ
	 *
	 * から本部のpublicメソッドを呼び出す
	 *
	 * TODO あとで
	 */
	protected void selectMethod(String methodName, String data) {
		switch(methodName) {
			case "receiveTransportStartingReport":
				String[] params = data.split("&");
				headquarter.receiveTransportStartingReport(
					decodeRecords(params[0]),
					decodeRequestIds(params[1])
				);
				break;
			case "receiveTransportFailureReport":
				headquarter.receiveTransportFailureReport(decodeRequestIds(data));
				break;
			case "receiveTransportSuccessReport":
				headquarter.receiveTransportSuccessReport(decodeRequestIds(data));
				break;
			case "receiveDeliveryStartingReport":
				headquarter.receiveDeliveryStartingReport(decodeRequestIds(data));
				break;
			case "receiveDeliverySuccessReport":
				headquarter.receiveDeliverySuccessReport(decodeDateMap(data));
				break;
			case "receiveWithoutRecipientReport":
				headquarter.receiveWithoutRecipientReport(decodeRequestIds(data));
				break;
			case "receiveWrongRecipientReport":
				headquarter.receiveWrongRecipientReport(decodeRequestIds(data));
				break;
			default:
				break;
		}
	}

	/**
	 * 他の通信クラスとの通信を確立する
	 */
	protected void connect() throws IOException {
		System.out.println("Head-Relay connecting is started.");

		connection = Connector.open(target);

		dis = new DataInputStream(((InputConnection)connection).openInputStream());
		dos = new DataOutputStream(((OutputConnection)connection).openOutputStream());

		System.out.println("Head-Relay connecting is finished.");
	}

	protected void waitForConnection() throws IOException {
		System.out.println("Head-Recep connecting is started.");

		srvSocket = new ServerSocket(port);
		socket    = srvSocket.accept();

		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());

		System.out.println("Head-Recep connecting is finished.");
	}

	// TODO 削除
	protected void dummy(String str) {
		headquarter.dummy(this, str);
	}

}
