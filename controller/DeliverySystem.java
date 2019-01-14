package controller;

import boundary.cui.Boundary;
import boundary.gui.MainFrame;
import boundary.gui.ParamData;
import entity.common.PersonInfo;
import entity.common.Record;
import entity.inPC.Reception;
import entity.inPC.Headquarter;
import entity.inPC.Recipient;

/**
 * 荷物宅配システムのコントローラクラスです。
 * CUI/GUIと各サブシステムを管理します。
 * @author 澤田 悠暉
 * @version 1.0 (2019/01/14)
*/
public class DeliverySystem {

	private Reception reception;
	private Headquarter headquarter;
	private Recipient recipient;

	private Boundary io;

	private MainFrame view;
	private PersonInfo userInfo;
	private Record record;

	/**
	 * ログインモードです。
	*/
	public static final int LOGIN = 0;

	/**
	 * ログアウトモードです。
	*/
	public static final int LOGOUT = 1;

	/**
	 * 依頼モードです。
	*/
	public static final int REQUEST = 2;

	/**
	 * 配達記録参照モードです。
	*/
	public static final int REFER = 3;

	/**
	 * 宛先修正モードです。
	*/
	public static final int FIX = 4;

	/**
	 * 受取人宅不在設定モードです。
	*/
	public static final int SETTING = 5;

	/**
	 * ログイン中モードです。
	*/
	public static final int LOGGING = 6;

	private static final int FRAME_X = 100;
	private static final int FRAME_Y = 100;
	private static final int FRAME_W = 800;
	private static final int FRAME_H = 600;

	private static final int DELAY = 20000;

	/**
	 * 各サブシステムを起動し, 通信を確立させます。
	 * CUI/GUIでの入力を受けられる状態になります。
	 * @param args コマンドライン引数
	*/
	public static void main(String[] args) {
		DeliverySystem myself = new DeliverySystem();

		myself.view = new MainFrame(myself, FRAME_X, FRAME_Y, FRAME_W, FRAME_H);

		myself.headquarter = new Headquarter();
		myself.reception   = new Reception();
		myself.recipient   = new Recipient();

		myself.io = new Boundary();
		myself.io.printMessage("DeliverySystem is started.");

		myself.headquarter.execute();
		myself.reception.execute();
		myself.recipient.execute();

		try {
			Thread.sleep(DELAY);
		} catch(InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}

		while(true) {
			if(myself.io.select("配達を依頼する", "配達記録を参照する"))
				myself.executeReceiveRequest();
			else
				myself.executeReferRecord();
		}
	}

	private synchronized void executeReceiveRequest() {
		PersonInfo clientInfo    = null;
		PersonInfo recipientInfo = null;;

		clientInfo = inputPersonInfo("依頼人");
		if(clientInfo == null) return;

		recipientInfo = inputPersonInfo("受取人");
		if(recipientInfo == null) return;

		io.printRecord(this.reception.receiveRequest(clientInfo, recipientInfo));
	}

	private PersonInfo inputPersonInfo(String target) {
		String name  = io.inputName(target + "名前 :");
		int address  = io.inputAddress(target + "番地 :");
		String phone = io.inputPhoneNumber(target + "電話番号 :");

		if(io.select("修正する", "修正しない"))
			return inputPersonInfo(target);

		if(!io.isCorrectPersonInfo(name, address, phone)) {
			io.printMessage("入力された個人情報は不正です");

			if(io.select("再入力する", "再入力しない"))
				return inputPersonInfo(target);
			else
				return null;
		}

		return new PersonInfo(name, address, phone);
	}

	private synchronized void executeReferRecord() {
		int id = inputRequestId();
		if(id == -1) return;

		Record targetRecord = this.headquarter.referRecord(id);

		PersonInfo clientInfo = null;

		while(true) {
			do {
				clientInfo = new PersonInfo(
				io.inputName("依頼人名前 :"),
				io.inputAddress("依頼人番地 :"),
				io.inputPhoneNumber("依頼人電話番号 :")
				);
			} while(io.select("修正する", "修正しない"));

			if(targetRecord.getClientInfo().equals(clientInfo)) {
				break;
			} else {
				if(!io.select("再入力する", "再入力しない"))
				return;
			}
		}

		if(targetRecord.isWrongRecipient())
			executeFixWrongRecipient(targetRecord);
		else
			io.printRecord(targetRecord);
	}

	private int inputRequestId() {
		int id = io.inputRequestId();

		if(io.select("修正する", "修正しない"))
			return inputRequestId();

		if(!this.headquarter.contains(id)) {
			if(io.select("再入力する", "再入力しない"))
				return inputRequestId();
			else
				return -1;
		}

		return id;
	}

	private void executeFixWrongRecipient(Record befRecord) {
		if(!io.select("宛先を修正する", "宛先を修正しない")) return;

		PersonInfo recipientInfo = inputPersonInfo("受取人");
		if(recipientInfo == null) return;

		Record altRecord = this.headquarter.fixWrongRecipient(befRecord, recipientInfo);
		io.printRecord(altRecord);
	}

	/**
	 * 特定のサブシステムのメソッドを実行します。
	 * @param data メソッドに渡すパラメータデータ
	*/
	public void executeSubSystem(ParamData data) {
		if(data == null) return;

		switch(data.getMethod()) {
			case LOGIN:
				login(data.getName(), data.getAddress(), data.getPhoneNumber());
				break;
			case LOGOUT:
				logout();
				break;
			case REQUEST:
				PersonInfo recipientInfo = new PersonInfo(data.getName(), data.getAddress(), data.getPhoneNumber());
				int requestId = this.reception.receiveRequest(this.userInfo, recipientInfo).getRequestId();
				view.setRequestResults(new ParamData(requestId, this.userInfo, recipientInfo));
				break;
			case REFER:
				this.record = this.headquarter.referRecord(data.getRequestId());
				view.setComfirmResults(new ParamData(record), this.record.isWrongRecipient());
				break;
			case FIX:
				PersonInfo altInfo = new PersonInfo(data.getName(), data.getAddress(), data.getPhoneNumber());
				Record altRecord = this.headquarter.fixWrongRecipient(this.record, altInfo);
				view.setFixResults(new ParamData(altRecord));
				break;
			case SETTING:
				this.recipient.setIsHome(data.getBools());
				break;
			case LOGGING:
				view.setComfirmSelection(headquarter.getIds(userInfo));
				break;
			default:
				break;
		}
	}

	/**
	 * 指定したサブシステムのメソッドを実行することが可能かを判定します。
	 * 実行可能な場合はtrueを返します。
	 * @param data メソッドに渡すパラメータデータ
	 * @return 実行可能な場合はtrue
	*/
	public boolean canExecuteSubSystem(ParamData data) {
		if(data == null) return true;

		boolean result = false;
		switch(data.getMethod()) {
			case LOGIN:
				result = canLogin(data.getName(), data.getAddress(), data.getPhoneNumber());
				break;
			case LOGOUT:
			case REQUEST:
			case REFER:
			case FIX:
			case SETTING:
			case LOGGING:
				result = true;
				break;
			default:
				break;
		}

		return result;
	}

	private void login(String name, int address, String phone) {
		this.userInfo = new PersonInfo(name, address, phone);
	}

	private void logout() {
		this.userInfo = null;
	}

	private boolean canLogin(String name, int address, String phone) {
		return this.recipient.contains(new PersonInfo(name, address, phone));
	}

}
