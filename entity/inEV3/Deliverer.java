package entity.inEV3;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import comm.DelivererCommunication;
import entity.common.Date;
import entity.common.Parcel;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class Deliverer extends Robot {

	private List<Parcel> deliveredParcels;

	private Map<Integer, Date> receivingDateMap;

	private List<Parcel> withoutRecipientParcels;

	private List<Parcel> wrongRecipientParcels;

	private DelivererCommunication commToRelayStation;

	private DelivererCommunication commToRecipient;

	private class ButtonEventListener implements KeyListener {
		public void keyPressed(Key key) {
			switch(key.getId()) {
				case Button.ID_UP:
					openSensor();
					setParkingDistance();
					goCheck();
					break;
				case Button.ID_DOWN:
					LCD.drawString("checkCanEntry(): " + checkCanEntry(), 0, 5);
					break;
				default:
					break;
			}
		}

		public void keyReleased(Key key) {}
	}

	private Deliverer() {
		this.deliveredParcels        = new LinkedList<Parcel>();
		this.receivingDateMap        = new HashMap<Integer, Date>();
		this.withoutRecipientParcels = new LinkedList<Parcel>();
		this.wrongRecipientParcels   = new LinkedList<Parcel>();
		this.commToRelayStation = null;
		this.commToRecipient    = null;
	}

	public static void main(String[] args) {
		Deliverer myself = new Deliverer();

		myself.commToRelayStation = new DelivererCommunication(myself);
		myself.commToRecipient    = new DelivererCommunication(myself);

		LCD.clear();
		LCD.drawString("Started.", 0, 0);

		new Thread(myself.commToRelayStation).start();

		Delay.msDelay(60000);
		new Thread(myself.commToRecipient).start();

		ButtonEventListener listener = myself.new ButtonEventListener();
		Button.UP.addKeyListener(listener);
		Button.DOWN.addKeyListener(listener);

		LCD.drawString("Ready.", 0, 2);
	}

	public void connected() {
		LCD.drawString("Connected.", 0, 1);

		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
	}

	/**
	 * ユースケース「待機所で待機する」を包含するメソッド
	 *
	 * ローカル定数
	 * 待機時間 = 20秒
	 *
	 * 統合テストで確認してください
	 *
	 */
	public void waitInStandbyStation() {
		System.out.println("waitInStandbyStation()");
		isRightSide = false;


		//中継所から合流点
        moveFromRelayStaToIntersection();
        rotate(90);

        //合流点から待機所
        moveFromIntersectionToStandbySta();
        rotate(177);

        parking();

        //sleep20秒待つ
        Delay.msDelay(20000);
        
        closeSensor();
        openSensor();
		goCheck();
	}

	/**
	 * ユースケース「荷物を配達する」を包含するメソッド
	 *
	 * 統合テストで確認してください
	 *
	 */
	public void deliverParcels(List<Parcel> parcels) {
		System.out.println("deliverParcels()");

		int currentAddress;//現在番地
        int targetAddress;//目的番地

        /*ここはlistからarraylistに変わっているが大丈夫なのか気になった*/
        this.deliveredParcels.addAll(parcels);
        /*上記のどっち*/
        super.isRightSide = false;
        //中継所から合流点
        moveFromRelayStaToIntersection();
        rotate(84);
        //合流点から中継所進入点
        moveFromIntersectionToEntryPoint();
        rotate(84);
        //中継所進入点から受取人宅基準点
        moveFromEntryPointToStartingPoint();
        currentAddress = 1;
        for(Parcel parcel : this.deliveredParcels){
            if(parcel.getAddress() != currentAddress){
                targetAddress = parcel.getAddress();
                moveNextRecipient(currentAddress,targetAddress);
                currentAddress = targetAddress;
            }

			rotate(45);


             //TODO 受取人宅に取得した受取人個人情報をを送る
            this.commToRecipient.writeMethod("verifyRecipientInfo", currentAddress, parcel.getRecipientInfo());

			String result = commToRecipient.readString(10000);
			if(result.isEmpty()) {
				this.withoutRecipientParcels.add(parcel);
			} else {
				if(Boolean.parseBoolean(result)) {
					this.commToRecipient.writeMethod("receiveParcel", parcel);
					this.receivingDateMap.put(parcel.getRequestId(), Date.decode(this.commToRecipient.readString()));
				} else {
					this.wrongRecipientParcels.add(parcel);
				}
			}

			// TODO ピープ音いれるかも
			rotate(-45);
        }

        moveNextRecipient(currentAddress,1);

        notifyDeliveryResults();
	}

	/**
	 * ユースケース「配達の有無を確認する」を包含するメソッド
	 */
	public void goCheck() {
		System.out.println("goCheck()");

         isRightSide = true;
        /*
         *待機所から中継所進入点へ移動する
         */
        moveFromStandbyStaToEntryPoint();

        moveFromEntryPointToRelaySta();

		//TODO 中継所に配達の有無の確認要求を送るメソッドが入る
		commToRelayStation.writeMethod("sendParcels");
	}

	/**
	 * ユースケース「配達結果を連絡する」を包含するメソッド
	 */
	private void notifyDeliveryResults() {
        isRightSide = true;
        /*
         *受取人宅基準点から中継所進入点まで移動する
         */
        moveFromStartingPointToEntryPoint();
        rotate(-84);
        /*
         *中継所進入点から中継所へ移動する
         */
        moveFromEntryPointToRelaySta();
        /*
         *中継所に受取時間表・受取人不在の荷物リスト・宛先間違いの荷物リストを送る
         */
        this.commToRelayStation.writeMethod("receiveFinishDeliveryNotification", receivingDateMap, withoutRecipientParcels, wrongRecipientParcels);

        init();
	}

	/**
	 * 現段階では,
	 * 待機所から中継所進入点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromStandbyStaToEntryPoint() {
		System.out.println("moveFromStandbyStaToEntryPoint()");
        lineTrace(76f,315);
	}

	/**
	 * 現段階では,
	 * 合流点から待機所までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromIntersectionToStandbySta() {
		System.out.println("moveFromIntersectionToStandbySta()");
        lineTrace(118.5f,315);
	}

	/**
	 * 現段階では,
	 * 中継所進入点から中継所までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromEntryPointToRelaySta() {

		System.out.println("moveFromEntryPointToRelaySta()");

		while(!checkCanEntry()) {
			System.out.println("loop");
			Delay.msDelay(10000);
		}

		moveFromEntryPointToIntersection();
		super.rotate(-84);

		moveFromIntersectionToRelaySta();
		super.rotate(-180);
	}

	/**
	 * 現段階では,
	 * 中継所進入点から合流点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromEntryPointToIntersection() {
		System.out.println("moveFromEntryPointToIntersection()");
        lineTrace(39.5f,175);
	}

	/**
	 * 現段階では,
	 * 合流点から中継所進入点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromIntersectionToEntryPoint() {
		System.out.println("moveFromIntersectionToEntryPoint()");
        lineTrace(38.0f,175);
	}

	/**
	 * 現段階では,
	 * 合流点から中継所までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromIntersectionToRelaySta() {
		System.out.println("moveFromIntersectionToRelaySta()");
        lineTrace(60f,175);
	}

	/**
	 * 現段階では,
	 * 中継所から合流点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromRelayStaToIntersection() {
		System.out.println("moveFromRelayStaToIntersection()");
        lineTrace(58.5f,175);
	}

	/**
	 * 現段階では,
	 * 中継所進入点から受取人宅基準点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromEntryPointToStartingPoint() {
        System.out.println("moveFromEntryPointToStartingPoint()");
		lineTrace(50f, 175);
		lineTrace(124.0f, 315);
	}

	/**
	 * 現段階では,
	 * 受取人宅基準点から中継所進入点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromStartingPointToEntryPoint() {
		System.out.println("moveFromStartingPointToEntryPoint()");
		lineTrace(121f, 315);
		lineTrace(53f, 175);
	}

	/**
	 * 現在行 = (現在番地 / 4)
	 * 目的行 = (目的番地 / 4)
	 * 現在行 == 目的行を判定し,
	 * trueならば, (1区間の距離 * (目的番地 - 現在番地)) だけ正方向に移動する
	 * falseならば, (1区間の距離 * 現在番地) だけ負方向に移動する -> 90度回転 -> (1区間の距離 * 目的番地) だけ移動する
	 *
	 * ローカル定数
	 * 1区間の距離
	 */
	private void moveNextRecipient(int from, int to) {

		System.out.println("move "+from+" --> "+to);

        //基準点に戻る場合
		if(from>to) {
			goBackToStartingPoint(from);
		} else if(from == to) {
			rotate(174);
			initRotate();
			isRightSide = true;
		} else {
			//同じ行移動？
			if((from-1)/4 != (to-1)/4) {

				//列基準にいる？
				if((from-1) % 4 != 0) {

					//列基準まで戻る
					rotate(174);
					initRotate();
					isRightSide = true;

					for(int i=0;i<(from-1)%4;i++) {
						lineTrace(44.8f,315);
						initRotate();
					}
					rotate(-92);
					initRotate();
				}else {
					rotate(90);
					initRotate();
				}

				isRightSide = false;
				for(int i=(from-1)/4;i<(to-1)/4;i++) {
					lineTrace(44.5f,315);
					initRotate();
				}
				rotate(-90);
				initRotate();

				for(int i=0;i<(to-1)%4;i++) {
					lineTrace(44.5f,315);
					initRotate();
				}
			}
			//同じ行の場合
			else {
				//列移動
				for(int i=(from-1)%4;i<(to-1)%4;i++) {
					lineTrace(44.5f,315);
					//initRotate(0);
					initRotate();
				}

			}
		}
	}

    private void goBackToStartingPoint(int from) {
		  rotate(174);
		  initRotate();
		  isRightSide = true;
		  //列が違ったら
		  if((from-1)%4 != 0) {
              for(int i=0;i<(from-1)%4;i++) {
                  lineTrace(44.8f,315);
                  initRotate();
			 }
		  }
		  if((from-1)/4 != 0){
              rotate(90);
              for(int i=0;i<(from-1)/4;i++) {
                  lineTrace(44.5f,315);
			 }
			 rotate(-90);
			 initRotate();
		  }
	   }

	/**
	 * 配達用の荷物リスト, 受取時間表, 受取人不在の荷物リスト, 宛先間違いの荷物リストを空にする
	 */
	private void init() {
         this.deliveredParcels.clear();
        this.withoutRecipientParcels.clear();
        this.wrongRecipientParcels.clear();
        this.receivingDateMap.clear();

	}
    /**
    *中継所へ侵入確認を行う
    */
     private boolean checkCanEntry(){
		 commToRelayStation.writeMethod("canEntry");
		 return commToRelayStation.readBoolean();
    }

}
