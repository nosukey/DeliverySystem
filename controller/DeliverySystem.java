package controller;

import boundary.cui.Boundary;
import boundary.gui.MainFrame;
import boundary.gui.ParamData;
import entity.common.PersonInfo;
import entity.common.Record;
import entity.inPC.Reception;
import entity.inPC.Headquarter;
import entity.inPC.Recipient;

public class DeliverySystem {

	private Reception reception;

	private Headquarter headquarter;

	private Recipient recipient;

	private MainFrame view;

	private PersonInfo userInfo;
	private Record record;

	public static final int LOGIN   = 0;
	public static final int LOGOUT  = 1;
	public static final int REQUEST = 2;
	public static final int REFER   = 3;
	public static final int FIX     = 4;
	public static final int SETTING = 5;

	private static final int FRAME_X = 100;
	private static final int FRAME_Y = 100;
	private static final int FRAME_W = 800;
	private static final int FRAME_H = 600;

	/**
	 * システムを起動する
	 * PCの各サブシステムを起動し, 各通信を確立する
	 * 詳細はシーケンス図「システムを起動する」に記載している
	 */
	public static void main(String[] args) {
		DeliverySystem system = new DeliverySystem();

		system.view = new MainFrame(system, FRAME_X, FRAME_Y, FRAME_W, FRAME_H);

		system.headquarter = new Headquarter();
		system.reception   = new Reception();
		system.recipient   = new Recipient();

		Boundary io = new Boundary();
		io.printMessage("DeliverySystem is started.");

		system.headquarter.execute();
		system.reception.execute();
		system.recipient.execute();

		try {
			Thread.sleep(20000);
		} catch(InterruptedException e) {
			System.out.println("Exception: Interrupted.");
			System.exit(1);
		}


		// while(true) {
		// 	if(io.select("配達を依頼する", "配達記録を参照する"))
		// 		system.reception.receiveRequest();
		// 	else
		// 		system.headquarter.printAfter();
		// 		// system.headquarter.referRecord();
		//
		// 	// if(io.select("本部を確認する", "本部を確認しない"))
		// 	// 	system.headquarter.printAfter();
		// }
	}

	public void executeSubSystem(ParamData data) {
		if(data == null) return;

		switch(data.getMethod()) {
			case LOGIN:
				login(data.getName(), data.getAddress(), data.getPhoneNumber());
				view.setComfirmSelection(headquarter.getIds(userInfo));
				break;
			case LOGOUT:
				logout();
				break;
			case REQUEST:
				PersonInfo recipientInfo = new PersonInfo(data.getName(), data.getAddress(), data.getPhoneNumber());
				this.reception.receiveRequest(this.userInfo, recipientInfo);
				view.setRequestResults(new ParamData(0, this.userInfo, recipientInfo));
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
			default:
				break;
		}
	}

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
