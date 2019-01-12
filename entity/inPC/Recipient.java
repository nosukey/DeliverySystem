package entity.inPC;

// TODO 削除
import boundary.cui.Boundary;

import comm.RecipientCommunication;
import entity.common.Date;
import entity.common.PersonInfo;
import entity.common.Parcel;
import java.util.LinkedList;
import java.util.List;

public class Recipient {

	private List<Parcel> parcels;

	private RecipientCommunication commToDeliverer;

	private PersonInfo[] recipientsInfo;

	private boolean[] isHome;

	private static final String DELIVERER_ADDRESS = "btspp://001653423DA3:1";

	public Recipient() {
		this.parcels = new LinkedList<Parcel>();
		this.recipientsInfo = new PersonInfo[16];
		this.isHome = new boolean[16];
		autoSetInfos();
		// autoSetIsHome();
	}

	/**
	 * 配達担当ロボットとの通信を確立する
	 */
	public void execute() {
		this.commToDeliverer = new RecipientCommunication(this, DELIVERER_ADDRESS);
		new Thread(commToDeliverer).start();

		// TODO 削除
		Boundary io = new Boundary();
		io.printMessage("Recipient is started.");
	}

	// TODO 削除
	public void connected() {
		Boundary io = new Boundary();
		io.printMessage("Recipient is connected.");
	}

	private void autoSetInfos(){
	 	this.recipientsInfo[0]  = new PersonInfo("a",1,"09001010101");
		this.recipientsInfo[1]  = new PersonInfo("b",2,"09002020202");
		this.recipientsInfo[2]  = new PersonInfo("c",3,"09003030303");
		this.recipientsInfo[3]  = new PersonInfo("d",4,"09004040404");
		this.recipientsInfo[4]  = new PersonInfo("e",5,"09005050505");
		this.recipientsInfo[5]  = new PersonInfo("f",6,"09006060606");
		this.recipientsInfo[6]  = new PersonInfo("g",7,"09007070707");
		this.recipientsInfo[7]  = new PersonInfo("h",8,"09008080808");
		this.recipientsInfo[8]  = new PersonInfo("i",9,"09009090909");
		this.recipientsInfo[9]  = new PersonInfo("j",10,"09010101010");
		this.recipientsInfo[10] = new PersonInfo("k",11,"09011111111");
		this.recipientsInfo[11] = new PersonInfo("l",12,"09012121212");
		this.recipientsInfo[12] = new PersonInfo("m",13,"09013131313");
		this.recipientsInfo[13] = new PersonInfo("n",14,"09014141414");
		this.recipientsInfo[14] = new PersonInfo("o",15,"09015151515");
		this.recipientsInfo[15] = new PersonInfo("p",16,"09016161616");
	}

	// private void autoSetIsHome(){
	// 	this.isHome[0]=true;
	// 	this.isHome[1]=true;
	// 	this.isHome[2]=false;
	// 	this.isHome[3]=true;
	// 	this.isHome[4]=true;
	// 	this.isHome[5]=true;
	// 	this.isHome[6]=true;
	// 	this.isHome[7]=true;
	// 	this.isHome[8]=true;
	// 	this.isHome[9]=true;
	// 	this.isHome[10]=true;
	// 	this.isHome[11]=true;
	// 	this.isHome[12]=true;
	// 	this.isHome[13]=true;
	// 	this.isHome[14]=true;
	// 	this.isHome[15]=true;
	// }

	public void setIsHome(boolean[] isHome) {
		this.isHome = isHome;

		for(boolean bool : isHome) {
			System.out.println(bool);
		}
	}


	/**
	 * ユースケース「本人確認を行う」を包含しているメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void verifyRecipientInfo(int address, PersonInfo personalInfo) {

		System.out.println("verifyRecipientInfo()");

		if(isHome[address-1]){
			commToDeliverer.writeString(Boolean.toString(recipientsInfo[address-1].equals(personalInfo)));
			System.out.println(recipientsInfo[address-1].equals(personalInfo));
		}
	}

	/**
	 * ユースケース「荷物を受け取る」を包含しているメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveParcel(Parcel parcel) {

		System.out.println("receiveParcel()");

		parcels.add(parcel);

		//TODO テスト終わったら変数削除しても良い
		String receivingTimeData=Date.encode(Date.getCurrentDate());

		//TODO 消す
		System.out.println("receivingTimeData = "+receivingTimeData);

		//TODO 通信
		commToDeliverer.writeString(receivingTimeData);
	}

	public boolean contains(PersonInfo info) {
		for(PersonInfo recipientInfo : this.recipientsInfo) {
			if(recipientInfo.equals(info)) return true;
		}
		return false;
	}
}
