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
 * @author 大場貴斗 大竹知幸
 * @version 1.0
 */
public class Deliverer extends Robot {

	private List<Parcel> deliveredParcels;

	private Map<Integer, Date> receivingDateMap;

	private List<Parcel> withoutRecipientParcels;

	private List<Parcel> wrongRecipientParcels;

	private DelivererCommunication commToRelayStation;

	private DelivererCommunication commToRecipient;

	private final int SECOND_GEAR_SPEED = 175;

	private final int RIGHT_ANGLE = 90;

	private final int ADJAST_ANGLE = 6;

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

	/**
	 * 配達担当ロボットを起動します。
	 * 中継所、受取人宅からの接続待ち状態に入ります。
	 * @param args コマンドライン引数
	 */
	public static void main(String[] args) {

		final int CONNECTION_INTERVAL = 10000;

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
		final String REFRESH_DISPLAY = "\n\n\n\n\n\n\n";
		System.out.println(REFRESH_DISPLAY);

		LCD.drawString("Connected.", 0, 1);
	}

	/**
	 * 中継所から待機所に移動し、待機所で待機します。
	 */
	public void waitInStandbyStation() {

		final int WAITING_TIME = 20000;
		final int ADJAST_ANGLE = 3;

		setIsRightSide(false);

		moveFromRelayStaToIntersection();
        rotate(RIGHT_ANGLE);

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
        setIsRightSide(false);

        moveFromRelayStaToIntersection();
        rotate(90-ADJAST_ANGLE);

        moveFromIntersectionToEntryPoint();
        rotate(90-ADJAST_ANGLE);

        moveFromEntryPointToStartingPoint();

        currentAddress = STARTING_ADDRESS;
        for(Parcel parcel : this.deliveredParcels){
            if(parcel.getAddress() != currentAddress){
                targetAddress = parcel.getAddress();
                moveNextRecipient(currentAddress,targetAddress);
                currentAddress = targetAddress;
            }

			rotate(HAND_OVER_ANGLE);

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

			rotate(-HAND_OVER_ANGLE);
        }

        moveNextRecipient(currentAddress,STARTING_ADDRESS);

        notifyDeliveryResults();
	}

	private void goCheck() {
		setIsRightSide(true);
		moveFromStandbyStaToEntryPoint();
		moveFromEntryPointToRelaySta();

		commToRelayStation.writeMethod("sendParcels");
	}


	private void notifyDeliveryResults() {
        setIsRightSide(true);
        moveFromStartingPointToEntryPoint();
        rotate(-RIGHT_ANGLE+ADJAST_ANGLE);
        moveFromEntryPointToRelaySta();
        this.commToRelayStation.writeMethod("receiveFinishDeliveryNotification", receivingDateMap, withoutRecipientParcels, wrongRecipientParcels);

        init();
	}

	private void moveFromStandbyStaToEntryPoint() {
		final float DISTANCE_FROM_STANDBYSTA_TO_ENTRYPOINT = 77.5f;

        lineTrace(DISTANCE_FROM_STANDBYSTA_TO_ENTRYPOINT ,THIRD_GEAR_SPEED);
	}

	private void moveFromIntersectionToStandbySta() {
		final float DISTANCE_INTERSECTION_TO_STANDBYSTA = 118.5f;

        lineTrace(DISTANCE_INTERSECTION_TO_STANDBYSTA ,THIRD_GEAR_SPEED);
	}

	private void moveFromEntryPointToRelaySta() {
		final int ENTRY_WAITING_TIME = 1000;

		while(!checkCanEntry()) {
			Delay.msDelay(ENTRY_WAITING_TIME);
		}

		moveFromEntryPointToIntersection();
		rotate(-RIGHT_ANGLE + ADJAST_ANGLE);

		moveFromIntersectionToRelaySta();
		rotate(-STRAIGHT_ANGLE);
	}

	private void moveFromEntryPointToIntersection() {
		final float DISTANCE_ENTRYPOINT_TO_INTERSECTION = 38.0f;

        lineTrace(DISTANCE_ENTRYPOINT_TO_INTERSECTION,SECOND_GEAR_SPEED);
	}

	private void moveFromIntersectionToEntryPoint() {
		final float DISTANCE_INTERSECTION_TO_ENTRYPOINT = 38.0f;

        lineTrace(DISTANCE_INTERSECTION_TO_ENTRYPOINT,SECOND_GEAR_SPEED);
	}

	private void moveFromIntersectionToRelaySta() {
		final float DISTANCE_INTERSECTION_TO_RELAYSTA = 60f;

        lineTrace(DISTANCE_INTERSECTION_TO_RELAYSTA,SECOND_GEAR_SPEED);
	}

	private void moveFromRelayStaToIntersection() {
		final float DISTANCE_RELAYSTA_TO_INTERSECTION = 58.5f;

        lineTrace(DISTANCE_RELAYSTA_TO_INTERSECTION,SECOND_GEAR_SPEED);
	}

	private void moveFromEntryPointToStartingPoint() {
		final float DISTANCE_ENTRYPOINT_TO_CURVE = 50f;
		final float DISTANCE_CURVE_TO_STARTINGPOINT = 124.0f;

		lineTrace(DISTANCE_ENTRYPOINT_TO_CURVE, SECOND_GEAR_SPEED);
		lineTrace(DISTANCE_CURVE_TO_STARTINGPOINT, THIRD_GEAR_SPEED);
	}

	private void moveFromStartingPointToEntryPoint() {
		final float DISTANCE_STARTINGPOINT_TO_CURVE = 121f;
		final float DISTANCE_CURVE_TO_ENTRYPOINT = 51.5f;

		lineTrace(DISTANCE_STARTINGPOINT_TO_CURVE, THIRD_GEAR_SPEED);
		lineTrace(DISTANCE_CURVE_TO_ENTRYPOINT, SECOND_GEAR_SPEED);
	}

	private void moveNextRecipient(int from, int to) {
		int currentAddress 	= from-1;
		int nextAddress 		= to-1;

		final int RECIPIENT_BLOCK 		= 4;
		final int CURRENT_ROW 			= currentAddress%RECIPIENT_BLOCK;
		final int CURRENT_LINE 			= currentAddress/RECIPIENT_BLOCK;
		final int NEXT_ROW 				= nextAddress%RECIPIENT_BLOCK;
		final int NEXT_LINE 			= nextAddress/RECIPIENT_BLOCK;
		final int ROW_STANDARD			= 0;
		final int ADJAST_MINUTE_ANGLE 	= 2;

		if(currentAddress > nextAddress) {
			goBackToStartingPoint(from);
		} else if(currentAddress == nextAddress) {
			rotate(STRAIGHT_ANGLE-ADJAST_ANGLE);
			initRotate();
			setIsRightSide(true);
		} else {
			if(CURRENT_LINE == NEXT_LINE) {
				for(int i=CURRENT_ROW; i<NEXT_ROW; i++) {
					moveRecipientSide();
				}
			}else {
				if(CURRENT_ROW == ROW_STANDARD) {
					rotate(RIGHT_ANGLE);
					initRotate();
				}else {
					rotate(STRAIGHT_ANGLE-ADJAST_ANGLE);
					initRotate();
					setIsRightSide(true);

					for(int i=0;i<CURRENT_ROW;i++) {
						moveRecipientSide();
					}
					rotate(-RIGHT_ANGLE-ADJAST_MINUTE_ANGLE);
					initRotate();
				}

				setIsRightSide(false);

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
		setIsRightSide(true);

    	if(CURRENT_ROW != ROW_STANDARD) {
    		for(int i=0;i<CURRENT_ROW;i++) {
    			moveRecipientSide();
    		}
    	}
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
    	final float HEIGHT_BETWEEN_RECIPIENT = 44.2f;
		lineTrace(HEIGHT_BETWEEN_RECIPIENT,THIRD_GEAR_SPEED);
    }

	private void init() {
        this.deliveredParcels.clear();
        this.withoutRecipientParcels.clear();
        this.wrongRecipientParcels.clear();
        this.receivingDateMap.clear();

	}
     private boolean checkCanEntry(){
		 commToRelayStation.writeMethod("canEntry");
		 return commToRelayStation.readBoolean();
    }

}
