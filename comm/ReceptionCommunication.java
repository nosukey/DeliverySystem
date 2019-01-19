package comm;

import entity.inPC.Reception;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;
import lejos.utility.Delay;

/**
 * 宅配受付所専用の通信クラスです。
 * 他のサブシステムとの通信を確立し、データの受け渡しをサポートします。
 * @author 澤田 悠暉
 * @version 1.0
*/
public class ReceptionCommunication extends Communication implements Runnable {

	private Reception reception;
	private String target;
	private int port;
	private Socket socket;
	private Connection connection;

	private static final int DEFAULT_PORT = -1;

	/**
	 * 通信の確立に必要なインスタンスを生成します。
	 * このコンストラクタを用いて生成されたインスタンスは対EV3の通信を実現します。
	 * @param parent 宅配受付所
	 * @param target デバイスの名前やアドレス
	*/
	public ReceptionCommunication(Reception parent, String target) {
		this(parent, target, DEFAULT_PORT);
	}

	/**
	 * 通信の確立に必要なインスタンスを生成します。
	 * このコンストラクタを用いて生成されたインスタンスは対PCの通信を実現します。
	 * @param parent 宅配受付所
	 * @param target デバイスの名前やアドレス
	 * @param port ポート番号
	*/
	public ReceptionCommunication(Reception parent, String target, int port) {
		this.reception  = parent;
		this.target     = target;
		this.port       = port;
		this.socket     = null;
		this.connection = null;
	}

	/**
	 * 通信を確立し、他のサブシステムからの命令待ち状態に入ります。
	*/
	public void run() {
		try {
			connect();
		} catch(IOException e) {
			System.out.println("Exception: Reception's connection failed.");
			Delay.msDelay(DELAY_TIME);
			System.exit(1);
		}

		reception.connected();

		waitForInvoke();
	}

	@Override
	protected void selectMethod(String methodName, String data) {
		switch(methodName) {
			case "receiveSuccessNotification":
				reception.receiveSuccessNotification();
				break;
			case "receiveFailureNotification":
				reception.receiveFailureNotification(decodeParcels(data));
				break;
			default:
				break;
		}
	}

	@Override
	protected void connect() throws IOException {
		if(port == DEFAULT_PORT)
			connectToEV3();
		else
			connectToPC();
	}

	@Override
	protected void waitForConnection() throws IOException {

	}

	private void connectToPC() throws IOException {
		socket = new Socket(target, port);

		setDis(new DataInputStream(socket.getInputStream()));
		setDos(new DataOutputStream(socket.getOutputStream()));
	}

	private void connectToEV3() throws IOException {
		connection = Connector.open(target);

		setDis(new DataInputStream(((InputConnection)connection).openInputStream()));
		setDos(new DataOutputStream(((OutputConnection)connection).openOutputStream()));
	}

}
