package entity.inPC;

import comm.RecipientCommunication;
import entity.common.PersonInfo;
import entity.common.Parcel;
import java.util.List;

public class Recipient {

	private List<Parcel> parcels;

	private RecipientCommunication commToDelivery;

	private PersonInfo[] recipientsInfo;

	/**
	 * 配達担当ロボットとの通信を確立する
	 */
	public void execute() {

	}

	/**
	 * ユースケース「本人確認を行う」を包含しているメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void verifyRecipientInfo(int address, PersonInfo personalInfo) {

	}

	/**
	 * ユースケース「荷物を受け取る」を包含しているメソッド
	 * 統合テストで確認してください
	 *
	 */
	public void receiveParcel(Parcel parcel) {

	}

}
