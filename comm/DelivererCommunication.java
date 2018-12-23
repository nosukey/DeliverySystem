package comm;

import entity.inEV3.Deliverer;

import java.io.DataInputStream;
import java.io.IOException;
import lejos.remote.nxt.BTConnection;
import lejos.remote.nxt.BTConnector;
import lejos.utility.Delay;

public class DelivererCommunication extends Communication implements Runnable {

	private Deliverer deliverer;
	private BTConnector connector;
	private BTConnection connection;

	public DelivererCommunication(Deliverer parent) {
		this.deliverer  = parent;
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
			this.deliverer.connected();
		} catch(IOException e) {
			System.out.println("Exception: Connection failed.");
			Delay.msDelay(DELAY_TIME);
			System.exit(1);
		}

		waitForInvoke();
	}

	/**
	 * 第1引数
	 * 操作名には配達担当ロボットのpublicメソッド名
	 * 第2引数
	 * データにはそのメソッドの引数の文字列データ
	 *
	 * から配達担当ロボットのpublicメソッドを呼び出す
	 *
	 * TODO あとで
	 */
	protected void selectMethod(String methodName, String data) {
		switch(methodName) {
			case "waitInStandbyStation":
				deliverer.waitInStandbyStation();
				break;
			case "deliverParcels":
				deliverer.deliverParcels(decodeParcels(data));
				break;
			default:
				break;
		}
	}

	/**
	 * 配達担当ロボットからは接続しにいかないので未実装
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
		this.deliverer.dummy(this, str);
	}

	/**
	 * ストリームから真偽値を読み込む
	 */
	public boolean readBooleanOrNull() {
		Inner in = new Inner(dis);
		in.start();

		long start = System.currentTimeMillis();
		long now;
		while(true){
			now = System.currentTimeMillis();
			if(now - start > 10000 || in.getState().equals(Thread.State.TERMINATED)) {
				 in.stop();
				 break;
			}
		}
		return in.getReadValue();
	}

	class Inner extends Thread {
		private DataInputStream dis;
		private boolean readValue; // intで0,1,2が好ましい

		public Inner (DataInputStream dis){
			this.dis = dis;
		}

		public boolean getReadValue(){
			return this.readValue;
		}

		public void run() {
			try{
				this.readValue = dis.readBoolean();
			}catch (IOException e){
				System.out.println("Exception: Stream is closed.");
				Delay.msDelay(DELAY_TIME);
				this.start();
			}
		}
	}

}
