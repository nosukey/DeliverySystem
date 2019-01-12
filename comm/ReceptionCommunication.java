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

public class ReceptionCommunication extends Communication implements Runnable {

	private Reception reception;
	private String target;
	private int port;
	private Socket socket;
	private Connection connection;

	private static final int DEFAULT_PORT = -1;

	public ReceptionCommunication(Reception parent, String target) {
		this(parent, target, DEFAULT_PORT);
	}

	public ReceptionCommunication(Reception parent, String target, int port) {
		this.reception  = parent;
		this.target     = target;
		this.port       = port;
		this.socket     = null;
		this.connection = null;
	}

	/**
	 * 「接続する」を呼び出す
	 * 「文字列を読み込む->操作を選択する」ループに入る
	 */
	public void run() {
		try {
			connect();
		} catch(IOException e) {
			System.out.println("Exception: Reception's connection failed.");
			Delay.msDelay(DELAY_TIME);
			System.exit(1);
		}

		// TODO 削除
		reception.connected();

		waitForInvoke();
	}

	/**
	 * 第1引数
	 * 操作名には宅配受付所のpublicメソッド名
	 * 第2引数
	 * データにはそのメソッドの引数の文字列データ
	 *
	 * から宅配受付所のpublicメソッドを呼び出す
	 *
	 * TODO あとで
	 */
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

	/**
	 * 他の通信クラスとの通信を確立する
	 */
	protected void connect() throws IOException {
		if(port == DEFAULT_PORT)
			connectToEV3();
		else
			connectToPC();
	}

	protected void waitForConnection() throws IOException {

	}

	private void connectToPC() throws IOException {
		System.out.println("Recep-Head connecting is started.");
		socket = new Socket(target, port);

		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
		System.out.println("Recep-Head connecting is finished.");
	}

	private void connectToEV3() throws IOException {
		System.out.println("Recep-Coll connecting is started.");
		connection = Connector.open(target);

		dis = new DataInputStream(((InputConnection)connection).openInputStream());
		dos = new DataOutputStream(((OutputConnection)connection).openOutputStream());
		System.out.println("Recep-Coll connecting is finished.");
	}

}
