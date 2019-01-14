package entity.inPC;

// TODO 削除
import boundary.cui.Boundary;

import comm.RecipientCommunication;
import entity.common.Date;
import entity.common.PersonInfo;
import entity.common.Parcel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * サブシステム「受取人宅」クラスです。
 * 他のサブシステム「配達担当ロボット」クラスと通信を行います。
 * 番地1～16の受取人宅の情報を保持しています。
 * @author 池田はるか
 * @version 1.0(2019/01/13)
 */
public class Recipient {

	private List<Parcel> parcels;

	private RecipientCommunication commToDeliverer;

	private PersonInfo[] recipientsInfo;

	private boolean[] isHome;

	private static final int ADDRESS_GAP = 1;

	private static final String DELIVERER_ADDRESS = "btspp://001653423DA3:1";

	/**
	 * 全て受取人宅が受け取った荷物を格納するためのParcelクラスの配列を生成します。
	 * 番地1～16の受取人宅の個人情報を格納する配列を生成します。
	 * 番地1～16の受取人宅が在宅しているかどうかを格納する配列を生成します。
	 * 受取人宅の個人情報と在宅しているかどうかを設定します。
	 */
	public Recipient() {
		this.parcels = new LinkedList<Parcel>();
		this.recipientsInfo = new PersonInfo[16];
		this.isHome = new boolean[16];
		autoSetInfos();
		autoSetIsHome();
	}

	/**
	 * 配達担当ロボットとの通信を確立します。
	 */
	public void execute() {
		this.commToDeliverer = new RecipientCommunication(this, DELIVERER_ADDRESS);
		new Thread(commToDeliverer).start();

		// TODO 削除
		Boundary io = new Boundary();
		io.printMessage("Recipient is started.");
	}

	/**
	 * 通信が正常に確立されたことを表示します。
	 */
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

	private void autoSetIsHome(){
		for(int i=0; i<this.isHome.length; i++) {
			this.isHome[i] = true;
		}
	}

	/**
	 * 番地1～16の受取人宅を全て故意的に"在宅"に設定します。
	 * @param isHome 在宅しているかどうかを表すboolean配列。
	 */
	public void setIsHome(boolean[] isHome) {
		this.isHome = isHome;
	}

	/**
	 * 配達担当ロボットから渡される個人情報をもとに本人確認を行います。
	 * @param address 配達担当ロボットから渡される番地。
	 * @param personalInfo 配達担当ロボットから渡される個人情報。
	 */
	public void verifyRecipientInfo(int address, PersonInfo personalInfo) {
		if(isHome[address-ADDRESS_GAP]){
			commToDeliverer.writeString(Boolean.toString(recipientsInfo[address-ADDRESS_GAP].equals(personalInfo)));
			System.out.println(recipientsInfo[address-ADDRESS_GAP].equals(personalInfo));
		}
	}

	/**
	 * 配達担当ロボットから渡される荷物を受け取ります。
	 * @param parcel 渡される荷物。
	 */
	public void receiveParcel(Parcel parcel) {
		parcels.add(parcel);

		commToDeliverer.writeString(Date.encode(Date.getCurrentDate()));
	}

	/**
	 * 引数で渡された個人情報と同じ受取人宅が存在するかどうかを判定します。
	 * @param info 渡される個人情報。
	 * @return 存在する場合はtrue,存在しない場合はfalse。
	 */
	public boolean contains(PersonInfo info) {
//		for(PersonInfo recipientInfo : this.recipientsInfo) {
//			if(recipientInfo.equals(info)) return true;
//		}
//		return false;
		return Arrays.asList(recipientsInfo).contains(info);
	}
}
