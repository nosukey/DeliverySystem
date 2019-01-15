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

/**
 * 受取人宅専用の通信クラスです。
 * 他のサブシステムとの通信を確立し、データの受け渡しをサポートします。
 * @author 澤田 悠暉
 * @version 1.0
*/
public class RecipientCommunication extends Communication implements Runnable {

	private Recipient recipient;
	private String target;
	private Connection connection;

	private static final String PARAM_SEPARATION  = "&";

	/**
	 * 通信の確立に必要なインスタンスを生成します。
	 * @param parent 受取人宅
	 * @param target デバイスの名前やアドレス
	*/
	public RecipientCommunication(Recipient parent, String target) {
		this.recipient  = parent;
		this.target     = target;
		this.connection = null;
	}

	/**
	 * 通信を確立し、他のサブシステムからの命令待ち状態に入ります。
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
	 * 受取人宅のメソッドを呼び出します。
	 * @param methodName メソッド名
	 * @param data パラメータの文字列データ
	 * {@inheritDoc}
	*/
	@Override
	protected void selectMethod(String methodName, String data) {
		switch(methodName) {
			case "verifyRecipientInfo":
				String[] params = data.split(PARAM_SEPARATION);
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
	 * {@inheritDoc}
	*/
	@Override
	protected void connect() throws IOException {
		connection = Connector.open(target);

		setDis(new DataInputStream(((InputConnection)connection).openInputStream()));
		setDos(new DataOutputStream(((OutputConnection)connection).openOutputStream()));
	}

	/**
	 * {@inheritDoc}
	*/
	@Override
	protected void waitForConnection() throws IOException {

	}

}
