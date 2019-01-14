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
/**
 * サブシステム「配達担当ロボット」クラスです。
 * 他のサブシステム「中継所」クラス、「受取人宅」クラスと通信を行います。
 * @author 山下京之介
 * @version 1.0(2019/01/15)
 */
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


	/**
	 * イベントリスナークラスです。
	 * 配達担当ロボットのButtonが押されたことを認識するためのクラスです。
	 */
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

		/**
		 * 配達担当ロボットのボタンが離された時に何も行いません。
		 * @param key Keyクラス
		 */
		@Override
		public void keyReleased(Key key) {}
	}

	/**
	 * 配達担当ロボットインスタンスを生成します。
	 */
	private Deliverer() {
		this.deliveredParcels        = new LinkedList<Parcel>();
		this.receivingDateMap        = new HashMap<Integer, Date>();
		this.withoutRecipientParcels = new LinkedList<Parcel>();
		this.wrongRecipientParcels   = new LinkedList<Parcel>();
		this.commToRelayStation = null;
		this.commToRecipient    = null;
	}

	/**
	 * 配達担当ロボットを起動します。
	 * 中継所、受取人宅からの接続待ち状態に入ります。
	 * @param args コマンドライン引数
	 */
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

	/**
	 * 通信を確立された場合に呼び出され、状況を確認することができます。
	 */
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
	 * 中継所から待機所に移動し、待機所で待機します。
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
	 * 中継所から荷物を受け取り、受取人宅に配達します。
	 * @param parcels 中継所から受け取った荷物リスト
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
	 * 中継所に対して配達の有無の確認を行います。
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
	 * 中継所に対して配達結果の連絡を行います。
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
	 * 待機所から中継所進入点に移動します。
	 */
	private void moveFromStandbyStaToEntryPoint() {
		final float DISTANCE_FROM_STANDBYSTA_TO_ENTRYPOINT = 76f;

        lineTrace(DISTANCE_FROM_STANDBYSTA_TO_ENTRYPOINT ,THIRD_GEAR_SPEED);
	}

	/**
	 * 合流点から待機所に移動します。
	 */
	private void moveFromIntersectionToStandbySta() {
		final float DISTANCE_INTERSECTION_TO_STANDBYSTA = 118.5f;

        lineTrace(DISTANCE_INTERSECTION_TO_STANDBYSTA ,THIRD_GEAR_SPEED);
	}

	/**
	 * 中継所進入点から中継所に移動します。
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
	 * 中継所進入点から合流点に移動します。
	 */
	private void moveFromEntryPointToIntersection() {
		final float DISTANCE_ENTRYPOINT_TO_INTERSECTION = 39.5f;

        lineTrace(DISTANCE_ENTRYPOINT_TO_INTERSECTION,SECOND_GEAR_SPEED);
	}

	/**
	 * 合流点から中継所進入点に移動します。
	 */
	private void moveFromIntersectionToEntryPoint() {
		final float DISTANCE_INTERSECTION_TO_ENTRYPOINT = 38.0f;

        lineTrace(DISTANCE_INTERSECTION_TO_ENTRYPOINT,SECOND_GEAR_SPEED);
	}

	/**
	 * 合流点から中継所に移動します。
	 */
	private void moveFromIntersectionToRelaySta() {
		final float DISTANCE_INTERSECTION_TO_RELAYSTA = 60f;

        lineTrace(DISTANCE_INTERSECTION_TO_RELAYSTA,SECOND_GEAR_SPEED);
	}

	/**
	 * 中継所から合流点に移動します。
	 */
	private void moveFromRelayStaToIntersection() {
		final float DISTANCE_RELAYSTA_TO_INTERSECTION = 58.5f;

        lineTrace(DISTANCE_RELAYSTA_TO_INTERSECTION,SECOND_GEAR_SPEED);
	}

	/**
	 * 中継所進入点から受取人宅基準点移動します。
	 */
	private void moveFromEntryPointToStartingPoint() {
		final float DISTANCE_ENTRYPOINT_TO_CURVE = 50f;
		final float DISTANCE_CURVE_TO_STARTINGPOINT = 124.0f;

		lineTrace(DISTANCE_ENTRYPOINT_TO_CURVE, SECOND_GEAR_SPEED);
		lineTrace(DISTANCE_CURVE_TO_STARTINGPOINT, THIRD_GEAR_SPEED);
	}

	/**
	 * 受取人宅基準点から中継所進入点に移動します。
	 */
	private void moveFromStartingPointToEntryPoint() {
		final float DISTANCE_STARTINGPOINT_TO_CURVE = 121f;
		final float DISTANCE_CURVE_TO_ENTRYPOINT = 53f;

		lineTrace(DISTANCE_STARTINGPOINT_TO_CURVE, THIRD_GEAR_SPEED);
		lineTrace(DISTANCE_CURVE_TO_ENTRYPOINT, SECOND_GEAR_SPEED);
	}

	/**
	 * 現在いる番地から配達先の番地へ移動します。
	 * @param from 現在いる番地
	 * @param to 配達先の番地
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

	/**
	 * 現在いる番地から受取人宅基準点に移動する。
	 * @param from 現在いる番地
	 */
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

	/**
	 * 行方面に一区画分移動する。
	 */
    private void moveRecipientSide() {
    	final float WIDTH_BETWEEN_RECIPIENT = 44.8f;

    	lineTrace(WIDTH_BETWEEN_RECIPIENT,THIRD_GEAR_SPEED);
		initRotate();
    }
	/**
	 * 列方面に一区画分移動する。
	 */
    private void moveRecipientHeight() {
    	final float HEIGHT_BETWEEN_RECIPIENT = 44.5f;
		lineTrace(HEIGHT_BETWEEN_RECIPIENT,THIRD_GEAR_SPEED);
    }

	/**
	 * 配達用の荷物リスト, 受取時間表, 受取人不在の荷物リスト, 宛先間違いの荷物リストを空にする。
	 */
	private void init() {
         this.deliveredParcels.clear();
        this.withoutRecipientParcels.clear();
        this.wrongRecipientParcels.clear();
        this.receivingDateMap.clear();

	}
    /**
	 * 中継所へ侵入確認を行います。
	 * @return boolean
	 */
     private boolean checkCanEntry(){
		 commToRelayStation.writeMethod("canEntry");
		 return commToRelayStation.readBoolean();
    }

}
