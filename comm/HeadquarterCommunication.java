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

/**
 * 本部専用の通信クラスです。
 * 他のサブシステムとの通信を確立し、データの受け渡しをサポートします。
 * @author 澤田 悠暉
 * @version 1.0 (2019/01/14)
*/
public class HeadquarterCommunication extends Communication implements Runnable {

	private Headquarter headquarter;
	private String target;
	private int port;
	private ServerSocket srvSocket;
	private Socket socket;
	private Connection connection;

	private static final int DEFAULT_PORT = -1;
	private static final String PARAM_SEPARATION = "&";

	/**
	 * 通信の確立に必要なインスタンスを生成します。
	 * このコンストラクタを用いて生成されたインスタンスは対EV3の通信を実現します。
	 * @param parent 本部
	 * @param target デバイスの名前やアドレス
	*/
	public HeadquarterCommunication(Headquarter parent, String target) {
		this(parent, target, DEFAULT_PORT);
	}

	/**
	 * 通信の確立に必要なインスタンスを生成します。
	 * このコンストラクタを用いて生成されたインスタンスは対PCの通信を実現します。
	 * @param parent 本部
	 * @param port ポート番号
	*/
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
	 * 通信を確立し、他のサブシステムからの命令待ち状態に入ります。
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
	 * 本部のメソッドを呼び出します。
	 * @param methodName メソッド名
	 * @param data パラメータの文字列データ
	 * {@inheritDoc}
	*/
	@Override
	protected void selectMethod(String methodName, String data) {
		switch(methodName) {
			case "receiveTransportStartingReport":
				String[] params = data.split(PARAM_SEPARATION);
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
	 * {@inheritDoc}
	*/
	@Override
	protected void connect() throws IOException {
		System.out.println("Head-Relay connecting is started.");

		connection = Connector.open(target);

		dis = new DataInputStream(((InputConnection)connection).openInputStream());
		dos = new DataOutputStream(((OutputConnection)connection).openOutputStream());

		System.out.println("Head-Relay connecting is finished.");
	}

	/**
	 * {@inheritDoc}
	*/
	@Override
	protected void waitForConnection() throws IOException {
		System.out.println("Head-Recep connecting is started.");

		srvSocket = new ServerSocket(port);
		socket    = srvSocket.accept();

		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());

		System.out.println("Head-Recep connecting is finished.");
	}

}
