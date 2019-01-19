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


/**
 * サブシステム間の通信を実現する抽象クラスです。
 * 他のサブシステムとの通信を確立し、データの受け渡しをサポートします。
 * @author 澤田 悠暉
 * @version 1.0
*/
public abstract class Communication {

	private DataInputStream dis;
	private DataOutputStream dos;

	static final int TIMEOUT = 0;
	static final int DELAY_TIME = 5000;
	private static final String DUMMY = "dummy";

	private static final String METHOD_SEPARATION = "%";
	private static final String PARAM_SEPARATION  = "&";
	private static final String LIST_SEPARATION   = "#";
	private static final String SET_SEPARATION    = "!";

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
				e.printStackTrace();
			}
		}

		public int getBytes() {
			return this.bytes;
		}
	}

	void setDis(DataInputStream dis){ this.dis = dis;}

	void setDos(DataOutputStream dos){ this.dos = dos;}


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
	 * データ入力ストリームから真偽値を読み込みます。
 	 * @return 読み込んだ真偽値
	*/
	public boolean readBoolean() {
		try {
			return dis.readBoolean();
		} catch(IOException e) {
			e.printStackTrace();
			Delay.msDelay(DELAY_TIME);
			return readBoolean();
		}
	}

	/**
	 * データ出力ストリームに真偽値を書き込みます。
	 * @param bool 真偽値
	*/
	public void writeBoolean(boolean bool) {
		try {
			Delay.msDelay(DELAY_TIME);
			dos.writeBoolean(bool);
			dos.flush();
		} catch(IOException e) {
			e.printStackTrace();
        	Delay.msDelay(DELAY_TIME);
			writeBoolean(bool);
		}
	}

	/**
	 * データ入力ストリームから文字列を読み込みます。
 	 * @return 読み込んだ文字列
	*/
	public String readString() {
		try {
			return dis.readUTF();
		} catch(IOException e) {
			e.printStackTrace();
			Delay.msDelay(DELAY_TIME);
			return readString();
		}
	}

	/**
	 * データ入力ストリームから文字列を読み込みます。
	 * ただし, 引数で渡された時間内にストリームに文字列の書き込みがなかった場合には読み込みを中断し、nullを返します。
 	 * @return 読み込んだ文字列 (時間を過ぎた場合はnull)
	*/
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
	 * データ出力ストリームに文字列を書き込みます。
	 * @param str 文字列
	*/
	public void writeString(String str) {
		try {
			Delay.msDelay(DELAY_TIME);
			dos.writeUTF(str);
			dos.flush();
		} catch(IOException e) {
			e.printStackTrace();
        	Delay.msDelay(DELAY_TIME);
			writeString(str);
		}
	}

	/**
	 * データ出力ストリームに他のサブシステムに実行させたいメソッド名を書き込みます。
	 * @param methodName メソッド名
	*/
	public void writeMethod(String methodName) {
		writeString(methodName + METHOD_SEPARATION);
	}

	/**
	 * データ出力ストリームに他のサブシステムに実行させたいメソッド名とパラメータを書き込みます。
	 * @param methodName メソッド名
	 * @param value 整数値パラメータ
	*/
	public void writeMethod(String methodName, int value) {
		writeString(methodName + METHOD_SEPARATION + value);
	}

	/**
	 * データ出力ストリームに他のサブシステムに実行させたいメソッド名とパラメータを書き込みます。
	 * @param methodName メソッド名
	 * @param str 文字列パラメータ
	*/
	public void writeMethod(String methodName, String str) {
		writeString(methodName + METHOD_SEPARATION + str);
	}

	/**
	 * データ出力ストリームに他のサブシステムに実行させたいメソッド名とパラメータを書き込みます。
	 * @param methodName メソッド名
	 * @param requestId 依頼ID
	 * @param personInfo 個人情報
	*/
	public void writeMethod(String methodName, int requestId, PersonInfo personInfo) {
		writeString(methodName + METHOD_SEPARATION + requestId + PARAM_SEPARATION + PersonInfo.encode(personInfo));
	}

	/**
	 * データ出力ストリームに他のサブシステムに実行させたいメソッド名とパラメータを書き込みます。
	 * @param methodName メソッド名
	 * @param parcel 荷物
	*/
	public void writeMethod(String methodName, Parcel parcel) {
		writeString(methodName + METHOD_SEPARATION + Parcel.encode(parcel));
	}

	/**
	 * データ出力ストリームに他のサブシステムに実行させたいメソッド名とパラメータを書き込みます。
	 * @param methodName メソッド名
	 * @param parcels 荷物のリスト
	*/
	public void writeMethodWithParcels(String methodName, List<Parcel> parcels) {
		writeString(methodName + METHOD_SEPARATION + encodeParcels(parcels));
	}

	/**
	 * データ出力ストリームに他のサブシステムに実行させたいメソッド名とパラメータを書き込みます。
	 * @param methodName メソッド名
	 * @param requestIds 依頼IDのリスト
	*/
	public void writeMethodWithIds(String methodName, List<Integer> requestIds) {
		writeString(methodName + METHOD_SEPARATION + encodeRequestIds(requestIds));
	}

	/**
	 * データ出力ストリームに他のサブシステムに実行させたいメソッド名とパラメータを書き込みます。
	 * @param methodName メソッド名
	 * @param records 配達記録のリスト
	 * @param requestIds 依頼IDのリスト
	*/
	public void writeMethod(String methodName, List<Record> records, List<Integer> requestIds) {
		writeString(methodName + METHOD_SEPARATION + encodeRecords(records) + PARAM_SEPARATION + encodeRequestIds(requestIds));
	}

	/**
	 * データ出力ストリームに他のサブシステムに実行させたいメソッド名とパラメータを書き込みます。
	 * @param methodName メソッド名
	 * @param receivingDateMap 受取時間表
	 * @param withoutRecipientParcels 受取人不在の荷物リスト
	 * @param wrongRecipientParcels 宛先間違いの荷物リスト
	*/
	public void writeMethod(String methodName, Map<Integer, Date> receivingDateMap, List<Parcel> withoutRecipientParcels, List<Parcel> wrongRecipientParcels) {
		writeString(methodName + METHOD_SEPARATION + encodeDateMap(receivingDateMap) + PARAM_SEPARATION + encodeParcels(withoutRecipientParcels) + PARAM_SEPARATION + encodeParcels(wrongRecipientParcels));
	}

	private String extractMethodName(String packet) {
		String[] packs = packet.split(METHOD_SEPARATION);
		return packs[0];
	}

	private String extractData(String packet) {
		String[] packs = packet.split(METHOD_SEPARATION);
		if(packs.length < 2)
			return DUMMY;
		else
			return packs[1];
	}

	protected List<Integer> decodeRequestIds(String str) {
		List<Integer> requestIds = new LinkedList<Integer>();

		if(!str.equals(DUMMY)) {
			for(String element : str.split(LIST_SEPARATION)) {
				requestIds.add(Integer.valueOf(element));
			}
		}

		return requestIds;
	}

	private String encodeRequestIds(List<Integer> requestIds) {
		StringBuilder result = new StringBuilder();

		for(Integer id : requestIds) {
			result.append(id);
			result.append(LIST_SEPARATION);
		}

		if(result.toString().isEmpty())
			return DUMMY;
		else
			return result.substring(0, result.length()-1);
	}

	protected List<Parcel> decodeParcels(String str) {
		List<Parcel> parcels = new LinkedList<Parcel>();

		if(!str.equals(DUMMY)) {
			for(String element : str.split(LIST_SEPARATION)) {
				parcels.add(Parcel.decode(element));
			}
		}

		return parcels;
	}

	private String encodeParcels(List<Parcel> parcels) {
		StringBuilder result = new StringBuilder();

		for(Parcel parcel : parcels) {
			result.append(Parcel.encode(parcel));
			result.append(LIST_SEPARATION);
		}

		if(result.toString().isEmpty())
			return DUMMY;
		else
			return result.substring(0, result.length()-1);
	}

	protected List<Record> decodeRecords(String str) {
		List<Record> records = new LinkedList<Record>();

		if(!str.equals(DUMMY)) {
			for(String element : str.split(LIST_SEPARATION)) {
				records.add(Record.decode(element));
			}
		}

		return records;
	}

	private String encodeRecords(List<Record> records) {
		StringBuilder result = new StringBuilder();


		for(Record record : records) {
			result.append(Record.encode(record));
			result.append(LIST_SEPARATION);
		}

		if(result.toString().isEmpty())
			return DUMMY;
		else
			return result.substring(0, result.length()-1);
	}

	protected Map<Integer, Date> decodeDateMap(String str) {
		Map<Integer, Date> map = new HashMap<Integer, Date>();

		if(!str.equals(DUMMY)) {
			for(String strMap : str.split(LIST_SEPARATION)) {
				String[] elements = strMap.split(SET_SEPARATION);
				map.put(Integer.valueOf(elements[0]), Date.decode(elements[1]));
			}
		}

		return map;
	}

	private String encodeDateMap(Map<Integer, Date> dateMap) {
		StringBuilder result = new StringBuilder();

		for(Map.Entry<Integer, Date> entry : dateMap.entrySet()) {
			result.append(entry.getKey());
			result.append(SET_SEPARATION);
			result.append(Date.encode(entry.getValue()));
			result.append(LIST_SEPARATION);
		}

		if(result.toString().isEmpty())
			return DUMMY;
		else
			return result.substring(0, result.length()-1);
	}

}
