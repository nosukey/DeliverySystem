package controller;

import boundary.Boundary;
import entity.inPC.Reception;
import entity.inPC.Headquarter;
import entity.inPC.Recipient;

public class DeliverySystem {

	private Reception reception;

	private Headquarter headquarter;

	private Recipient recipient;

	/**
	 * システムを起動する
	 * PCの各サブシステムを起動し, 各通信を確立する
	 * 詳細はシーケンス図「システムを起動する」に記載している
	 */
	public static void main(String[] args) {
		DeliverySystem system = new DeliverySystem();

		system.headquarter = new Headquarter();
		system.reception   = new Reception();
		system.recipient   = new Recipient();

		// TODO 削除
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

		if(io.select("配達を依頼する", "配達記録を参照する"))
			system.reception.receiveRequest();
		else
			system.headquarter.referRecord();
	}

}
