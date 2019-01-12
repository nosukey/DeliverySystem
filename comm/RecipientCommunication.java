package comm;

import entity.common.Parcel;
import entity.common.PersonInfo;
import entity.inPC.Recipient;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;
import lejos.utility.Delay;

public class RecipientCommunication extends Communication implements Runnable {

	private Recipient recipient;
	private String target;
	private Connection connection;

	public RecipientCommunication(Recipient parent, String target) {
		this.recipient  = parent;
		this.target     = target;
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
			System.out.println("Exception: Recipient's connection failed.");
			Delay.msDelay(DELAY_TIME);
			System.exit(1);
		}

		// TODO 削除
		recipient.connected();

		waitForInvoke();
	}

	/**
	 * 第1引数
	 * 操作名には受取人宅のpublicメソッド名
	 * 第2引数
	 * データにはそのメソッドの引数の文字列データ
	 *
	 * から受取人宅のpublicメソッドを呼び出す
	 *
	 * TODO あとで
	 */
	protected void selectMethod(String methodName, String data) {
		switch(methodName) {
			case "verifyRecipientInfo":
				String[] params = data.split("&");
				recipient.verifyRecipientInfo(
					Integer.parseInt(params[0]),
					PersonInfo.decode(params[1])
				);
				break;
			case "receiveParcel":
				recipient.receiveParcel(Parcel.decode(data));
				break;
			default:
				break;
		}
	}

	/**
	 * 他の通信クラスとの通信を確立する
	 */
	protected void connect() throws IOException {
		System.out.println("Recip-Deli connecting is started.");
		connection = Connector.open(target);

		dis = new DataInputStream(((InputConnection)connection).openInputStream());
		dos = new DataOutputStream(((OutputConnection)connection).openOutputStream());
		System.out.println("Recip-Deli connecting is finished.");
	}

	protected void waitForConnection() throws IOException {

	}

}
