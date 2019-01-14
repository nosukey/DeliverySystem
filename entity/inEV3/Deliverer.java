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

	/*
	 * ロボットの方向転換時に限定角（0,90,-90,180)から搬送路の片側の適切な位置に調整するための角度を設定
	 */
	private final int ADJAST_ANGLE = 6;

	private final int RECIPIENTS_PER_LINE = 4;

	private final int RECIPIENTS_PER_ROW = 4;


	private class ButtonEventListener implements KeyListener {
		public void keyPressed(Key key) {
			switch(key.getId()) {
				case Button.ID_UP:
					openSensor();
					setParkingDistance();
					goCheck();
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

		final int CONNECTION_INTERVAL = 60000;

		Deliverer myself = new Deliverer();

		myself.commToRelayStation = new DelivererCommunication(myself);
		myself.commToRecipient    = new DelivererCommunication(myself);

		LCD.clear();
		LCD.drawString("Started.", 0, 0);

		new Thread(myself.commToRelayStation).start();

		Delay.msDelay(CONNECTION_INTERVAL);
		new Thread(myself.commToRecipient).start();

		ButtonEventListener listener = myself.new ButtonEventListener();
		Button.UP.addKeyListener(listener);

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

		final int WAITING_TIME = 20000;
		final int ADJAST_ANGLE = 3;

		isRightSide = false;

		/*
		 * 中継所から合流点まで移動する
		 */
        moveFromRelayStaToIntersection();
        rotate(RIGHT_ANGLE);

        /*
         * 合流点から待機所まで移動する
         */
        moveFromIntersectionToStandbySta();
        rotate(STRAIGHT_ANGLE-ADJAST_ANGLE);

        parking();

        Delay.msDelay(WAITING_TIME);

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

		final int HAND_OVER_ANGLE = 45;
		final int WAIT_RECEIVE_TIME = 10000;
		int STARTING_ADDRESS = 1;
		int currentAddress;
        int targetAddress;

        this.deliveredParcels.addAll(parcels);
        super.isRightSide = false;

        /*
         * 中継所から合流点へ移動する
         */
        moveFromRelayStaToIntersection();
        rotate(90-ADJAST_ANGLE);

        /*
         * 合流点から中継所進入点へ移動する
         */
        moveFromIntersectionToEntryPoint();
        rotate(90-ADJAST_ANGLE);

        /*
         * 中継所進入点から受取人宅基準点へ移動する
         */
        moveFromEntryPointToStartingPoint();

        currentAddress = STARTING_ADDRESS;
        for(Parcel parcel : this.deliveredParcels){
            if(parcel.getAddress() != currentAddress){
                targetAddress = parcel.getAddress();
                moveNextRecipient(currentAddress,targetAddress);
                currentAddress = targetAddress;
            }

			rotate(HAND_OVER_ANGLE);

             //TODO 受取人宅に取得した受取人個人情報をを送る
            this.commToRecipient.writeMethod("verifyRecipientInfo", currentAddress, parcel.getRecipientInfo());

			String result = commToRecipient.readString(WAIT_RECEIVE_TIME);
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
			rotate(-HAND_OVER_ANGLE);
        }

        moveNextRecipient(currentAddress,STARTING_ADDRESS);

        notifyDeliveryResults();
	}

	/**
	 * ユースケース「配達の有無を確認する」を包含するメソッド
	 */
	public void goCheck() {
		isRightSide = true;
		/*
         *待機所から中継所進入点へ移動する
         */
        moveFromStandbyStaToEntryPoint();

        /*
         * 中継所進入点から中継所へ移動する
         */
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
        rotate(-RIGHT_ANGLE+ADJAST_ANGLE);
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
		final float DISTANCE_FROM_STANDBYSTA_TO_ENTRYPOINT = 76f;

        lineTrace(DISTANCE_FROM_STANDBYSTA_TO_ENTRYPOINT ,THIRD_GEAR_SPEED);
	}

	/**
	 * 現段階では,
	 * 合流点から待機所までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromIntersectionToStandbySta() {
		final float DISTANCE_INTERSECTION_TO_STANDBYSTA = 118.5f;

        lineTrace(DISTANCE_INTERSECTION_TO_STANDBYSTA ,THIRD_GEAR_SPEED);
	}

	/**
	 * 現段階では,
	 * 中継所進入点から中継所までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromEntryPointToRelaySta() {

		final int ENTRY_WAITING_TIME = 1000;

		while(!checkCanEntry()) {
			Delay.msDelay(ENTRY_WAITING_TIME);
		}

		moveFromEntryPointToIntersection();
		super.rotate(-RIGHT_ANGLE + ADJAST_ANGLE);

		moveFromIntersectionToRelaySta();
		super.rotate(-STRAIGHT_ANGLE);
	}

	/**
	 * 現段階では,
	 * 中継所進入点から合流点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromEntryPointToIntersection() {
		final float DISTANCE_ENTRYPOINT_TO_INTERSECTION = 39.5f;

        lineTrace(DISTANCE_ENTRYPOINT_TO_INTERSECTION,SECOND_GEAR_SPEED);
	}

	/**
	 * 現段階では,
	 * 合流点から中継所進入点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromIntersectionToEntryPoint() {
		final float DISTANCE_INTERSECTION_TO_ENTRYPOINT = 38.0f;

        lineTrace(DISTANCE_INTERSECTION_TO_ENTRYPOINT,SECOND_GEAR_SPEED);
	}

	/**
	 * 現段階では,
	 * 合流点から中継所までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromIntersectionToRelaySta() {
		final float DISTANCE_INTERSECTION_TO_RELAYSTA = 60f;

        lineTrace(DISTANCE_INTERSECTION_TO_RELAYSTA,SECOND_GEAR_SPEED);
	}

	/**
	 * 現段階では,
	 * 中継所から合流点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromRelayStaToIntersection() {
		final float DISTANCE_RELAYSTA_TO_INTERSECTION = 58.5f;

        lineTrace(DISTANCE_RELAYSTA_TO_INTERSECTION,SECOND_GEAR_SPEED);
	}

	/**
	 * 現段階では,
	 * 中継所進入点から受取人宅基準点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromEntryPointToStartingPoint() {
		final float DISTANCE_ENTRYPOINT_TO_CURVE = 50f;
		final float DISTANCE_CURVE_TO_STARTINGPOINT = 124.0f;

		lineTrace(DISTANCE_ENTRYPOINT_TO_CURVE, SECOND_GEAR_SPEED);
		lineTrace(DISTANCE_CURVE_TO_STARTINGPOINT, THIRD_GEAR_SPEED);
	}

	/**
	 * 現段階では,
	 * 受取人宅基準点から中継所進入点までの距離をローカル定数として保持している
	 * その距離を用いて「ライントレースする」を呼び出す
	 * 形をとる予定である
	 */
	private void moveFromStartingPointToEntryPoint() {
		final float DISTANCE_STARTINGPOINT_TO_CURVE = 121f;
		final float DISTANCE_CURVE_TO_ENTRYPOINT = 53f;

		lineTrace(DISTANCE_STARTINGPOINT_TO_CURVE, THIRD_GEAR_SPEED);
		lineTrace(DISTANCE_CURVE_TO_ENTRYPOINT, SECOND_GEAR_SPEED);
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
		int currentAddress 	= from-1;
		int nextAddress 		= to-1;

		final int RECIPIENT_BLOCK 		= 4;
		final int CURRENT_ROW 			= currentAddress%RECIPIENT_BLOCK;
		final int CURRENT_LINE 			= currentAddress/RECIPIENT_BLOCK;
		final int NEXT_ROW 				= nextAddress%RECIPIENT_BLOCK;
		final int NEXT_LINE 			= nextAddress/RECIPIENT_BLOCK;
		final int LINE_STANDARD			= 0;
		final int ROW_STANDARD			= 0;
		final int ADJAST_MINUTE_ANGLE 	= 2;

		System.out.println("move "+from+" --> "+to);

        //基準点に戻る場合
		if(currentAddress > nextAddress) {
			goBackToStartingPoint(from);
		} else if(currentAddress == nextAddress) {
			rotate(STRAIGHT_ANGLE-ADJAST_ANGLE);
			initRotate();
			isRightSide = true;

		} else {
			/*
			 * 現在の番地と次の番地が同じ行にある場合
			 */
			if(CURRENT_LINE == NEXT_LINE) {
				moveRecipientSide();
			}else {
				/*
				 * 現在の列が基準となる列（受取人基準点のある列）にいる場合
				 */
				if(CURRENT_ROW == ROW_STANDARD) {
					rotate(RIGHT_ANGLE);
					initRotate();
				}else {
					rotate(STRAIGHT_ANGLE-ADJAST_ANGLE);
					initRotate();
					isRightSide = true;

					/*
					 * 受取人基準点まで行方向に移動
					 */
					for(int i=0;i<CURRENT_ROW;i++) {
						moveRecipientSide();
					}
					rotate(-RIGHT_ANGLE-ADJAST_MINUTE_ANGLE);
					initRotate();
				}

				isRightSide = false;

				for(int i=CURRENT_LINE;i<NEXT_LINE;i++) {
					moveRecipientHeight();
				}
				rotate(-RIGHT_ANGLE);
				initRotate();

				for(int i=0;i<NEXT_ROW;i++) {
					moveRecipientSide();
				}

			}
		}
	}

    private void goBackToStartingPoint(int from) {
		int currentAddress = from-1;
    	final int RECIPIENT_BLOCK 		= 4;
    	final int CURRENT_ROW 			= currentAddress%RECIPIENT_BLOCK;
		final int CURRENT_LINE 			= currentAddress/RECIPIENT_BLOCK;
		final int LINE_STANDARD			= 0;
		final int ROW_STANDARD				= 0;

    	rotate(STRAIGHT_ANGLE-ADJAST_ANGLE);
    	initRotate();
    	isRightSide = true;

    	/*
    	 * 現在いる列が受取人基準点の列でない場合
    	 */
    	if(CURRENT_ROW != ROW_STANDARD) {
    		for(int i=0;i<CURRENT_ROW;i++) {
    			moveRecipientSide();
    		}
    	}
    	/*
    	 * 現在いる行が受取人基準点の行でない場合
    	 */
    	if(CURRENT_LINE != LINE_STANDARD){
    		rotate(RIGHT_ANGLE);
    		for(int i=0;i<CURRENT_LINE;i++) {
    			moveRecipientHeight();
    		}
    		rotate(-RIGHT_ANGLE);
    		initRotate();
    	}
   }


    private void moveRecipientSide() {
    	final float WIDTH_BETWEEN_RECIPIENT = 44.8f;

    	lineTrace(WIDTH_BETWEEN_RECIPIENT,THIRD_GEAR_SPEED);
		initRotate();
    }

    private void moveRecipientHeight() {
    	final float HEIGHT_BETWEEN_RECIPIENT = 44.5f;
		lineTrace(HEIGHT_BETWEEN_RECIPIENT,THIRD_GEAR_SPEED);
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
