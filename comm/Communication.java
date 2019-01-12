package comm;

import entity.common.Date;
import entity.common.Parcel;
import entity.common.PersonInfo;
import entity.common.Record;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lejos.utility.Delay;

public abstract class Communication {

	protected DataInputStream dis;
	protected DataOutputStream dos;

	protected static final int TIMEOUT = 0;
	protected static final int DELAY_TIME = 5000;
	private final String DUMMY = "dummy";

	private static class InterruptSystem extends Thread {
		private int bytes;
		private DataInputStream dis;

		public InterruptSystem(DataInputStream dis) {
			this.bytes = 0;
			this.dis   = dis;
		}

		public void run() {
			try {
				this.bytes = dis.available();
			} catch(IOException e) {
				System.out.println("error");
				e.printStackTrace();
			}
		}

		public int getBytes() {
			return this.bytes;
		}
	}

	protected abstract void selectMethod(String methodName, String data);

 	protected abstract void connect() throws IOException;

	protected abstract void waitForConnection() throws IOException;

	protected void waitForInvoke() {
		while(true) {
			String buffer = readString();
			selectMethod(extractMethodName(buffer), extractData(buffer));
		}
	}

	/**
	 * ストリームから真偽値を読み込む
	 */
	public boolean readBoolean() {
		try {
			return dis.readBoolean();
		} catch(IOException e) {
			System.out.println("Exception: Stream is closed.");
			Delay.msDelay(DELAY_TIME);
			return readBoolean();
		}
	}

	/**
	 * ストリームに真偽値を書き込む
	 */
	public void writeBoolean(boolean bool) {
		try {
			Delay.msDelay(DELAY_TIME);
			dos.writeBoolean(bool);
			dos.flush();
		} catch(IOException e) {
    		System.out.println("Exception: I/O error.");
        	Delay.msDelay(DELAY_TIME);
			// System.exit(1);
			writeBoolean(bool);
		}
	}

	/**
	 * ストリームから文字列を読み込む
	 */
	public String readString() {
		try {
			return dis.readUTF();
		} catch(IOException e) {
			System.out.println("Exception: Stream is closed.");
			Delay.msDelay(DELAY_TIME);
			// System.exit(1);
			return readString();
		}
	}

	public String readString(int millis) {
		final int MSEC_INTERVAL = 1000;

		InterruptSystem system = new InterruptSystem(dis);
		system.start();
		for(int i=0; i<(millis/MSEC_INTERVAL); i++) {
			if(system.getBytes() > 0) {
				return readString();
			} else {
				system.interrupt();
				Delay.msDelay(MSEC_INTERVAL);
			}
		}

		return "";
	}

	/**
	 * ストリームに文字列を書き込む
	 */
	public void writeString(String str) {
		try {
			Delay.msDelay(DELAY_TIME);
			dos.writeUTF(str);
			dos.flush();
		} catch(IOException e) {
    		System.out.println("Exception: I/O error.");
        	Delay.msDelay(DELAY_TIME);
			// System.exit(1);
			writeString(str);
		}
	}

	/**
	 * 操作名を通信パケットとし, 文字列としてストリームに書き込む
	 *
	 * 単体テスト
	 * 通信パケットを表示し, フォーマットを確認する
	 */
	public void writeMethod(String methodName) {
		writeString(methodName + "%");
	}

	/**
	 * 操作名とデータを文字列に変換したものを連結させたものを通信パケットとし, 文字列としてストリームに書き込む
	 *
	 * 単体テスト
	 * 通信パケットを表示し, フォーマットを確認する
	 */
	public void writeMethod(String methodName, int value) {
		writeString(methodName + "%" + value);
	}

	/**
	 * 操作名とデータを文字列に変換したものを連結させたものを通信パケットとし, 文字列としてストリームに書き込む
	 *
	 * 単体テスト
	 * 通信パケットを表示し, フォーマットを確認する
	 */
	public void writeMethod(String methodName, String str) {
		writeString(methodName + "%" + str);
	}

	/**
	 * 操作名とデータを文字列に変換したものを連結させたものを通信パケットとし, 文字列としてストリームに書き込む
	 *
	 * 単体テスト
	 * 通信パケットを表示し, フォーマットを確認する
	 */
	public void writeMethod(String methodName, int requestId, PersonInfo personInfo) {
		writeString(methodName + "%" + requestId + "&" + PersonInfo.encode(personInfo));
	}

	/**
	 * 操作名とデータを文字列に変換したものを連結させたものを通信パケットとし, 文字列としてストリームに書き込む
	 *
	 * 単体テスト
	 * 通信パケットを表示し, フォーマットを確認する
	 */
	public void writeMethod(String methodName, Parcel parcel) {
		writeString(methodName + "%" + Parcel.encode(parcel));
	}

	/**
	 * 操作名とデータを文字列に変換したものを連結させたものを通信パケットとし, 文字列としてストリームに書き込む
	 *
	 * 単体テスト
	 * 通信パケットを表示し, フォーマットを確認する
	 */
	public void writeMethodWithParcels(String methodName, List<Parcel> parcels) {
		writeString(methodName + "%" + encodeParcels(parcels));
	}

	/**
	 * 操作名とデータを文字列に変換したものを連結させたものを通信パケットとし, 文字列としてストリームに書き込む
	 *
	 * 単体テスト
	 * 通信パケットを表示し, フォーマットを確認する
	 */
	public void writeMethodWithIds(String methodName, List<Integer> requestIds) {
		writeString(methodName + "%" + encodeRequestIds(requestIds));
	}

	/**
	 * 操作名とデータを文字列に変換したものを連結させたものを通信パケットとし, 文字列としてストリームに書き込む
	 *
	 * 単体テスト
	 * 通信パケットを表示し, フォーマットを確認する
	 */
	public void writeMethod(String methodName, List<Record> records, List<Integer> requestIds) {
		writeString(methodName + "%" + encodeRecords(records) + "&" + encodeRequestIds(requestIds));
	}

	/**
	 * 操作名とデータを文字列に変換したものを連結させたものを通信パケットとし, 文字列としてストリームに書き込む
	 *
	 * 単体テスト
	 * 通信パケットを表示し, フォーマットを確認する
	 */
	public void writeMethod(String methodName, Map<Integer, Date> receivingDateMap, List<Parcel> withoutRecipientParcels, List<Parcel> wrongRecipientParcels) {
		writeString(methodName + "%" + encodeDateMap(receivingDateMap) + "&" + encodeParcels(withoutRecipientParcels) + "&" + encodeParcels(wrongRecipientParcels));
	}

	/**
	 * 引数として代入された文字列から操作名を抽出する
	 */
	private String extractMethodName(String packet) {
		String[] packs = packet.split("%");
		return packs[0];
	}

	/**
	 * 引数として代入された文字列からデータを抽出する
	 */
	private String extractData(String packet) {
		String[] packs = packet.split("%");
		if(packs.length < 2)
			return DUMMY;
		else
			return packs[1];
	}

	protected List<Integer> decodeRequestIds(String str) {
		List<Integer> requestIds = new LinkedList<Integer>();

		if(!str.equals(DUMMY)) {
			for(String element : str.split("#")) {
				requestIds.add(Integer.valueOf(element));
			}
		}

		return requestIds;
	}

	private String encodeRequestIds(List<Integer> requestIds) {
		String result = "";

		for(Integer id : requestIds) {
			result += (id.toString() + "#");
		}

		if(result.isEmpty())
			return DUMMY;
		else
			return result.substring(0, result.length()-1);
	}

	protected List<Parcel> decodeParcels(String str) {
		List<Parcel> parcels = new LinkedList<Parcel>();

		if(!str.equals(DUMMY)) {
			for(String element : str.split("#")) {
				parcels.add(Parcel.decode(element));
			}
		}

		return parcels;
	}

	private String encodeParcels(List<Parcel> parcels) {
		String result = "";

		for(Parcel parcel : parcels) {
			result += (Parcel.encode(parcel) + "#");
		}

		if(result.isEmpty())
			return DUMMY;
		else
			return result.substring(0, result.length()-1);
	}

	protected List<Record> decodeRecords(String str) {
		List<Record> records = new LinkedList<Record>();

		if(!str.equals(DUMMY)) {
			for(String element : str.split("#")) {
				records.add(Record.decode(element));
			}
		}

		return records;
	}

	private String encodeRecords(List<Record> records) {
		String result = "";

		for(Record record : records) {
			result += (Record.encode(record) + "#");
		}

		if(result.isEmpty())
			return DUMMY;
		else
			return result.substring(0, result.length()-1);
	}

	protected Map<Integer, Date> decodeDateMap(String str) {
		Map<Integer, Date> map = new HashMap<Integer, Date>();

		if(!str.equals(DUMMY)) {
			for(String strMap : str.split("#")) {
				String[] elements = strMap.split("!");
				map.put(Integer.valueOf(elements[0]), Date.decode(elements[1]));
			}
		}

		return map;
	}

	private String encodeDateMap(Map<Integer, Date> dateMap) {
		String result = "";

		for(Map.Entry<Integer, Date> entry : dateMap.entrySet()) {
			result += (entry.getKey().toString() + "!" + Date.encode(entry.getValue()) + "#");
		}

		if(result.isEmpty())
			return DUMMY;
		else
			return result.substring(0, result.length()-1);
	}

}
