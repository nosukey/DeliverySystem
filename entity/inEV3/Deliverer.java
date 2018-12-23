package entity.inEV3;

import comm.DelivererCommunication;
import entity.common.Date;
import entity.common.Parcel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import sun.awt.windows.ThemeReader;

public class Deliverer extends Robot {

    private List<Parcel> deliveredParcels;

    private Map<Integer, Date> receivingDateMap;

    private List<Parcel> withoutRecipientParcels;

    private List<Parcel> wrongRecipientParcels;

    private DelivererCommunication commToRelayStation;

    private DelivererCommunication commToRecipient;

    private class ButtonEvent implements KeyListener {
        private Deliverer deliverer;

        ButtonEvent(Deliverer parent) {
            this.deliverer = parent;
        }

        public void keyPressed(Key key) {
            switch (key.getId()) {
                case Button.ID_UP:
                    deliverer.goCheck();
                    break;
                default:
                    break;
            }
        }

        public void keyReleased(Key key) {
        }
    }

    private Deliverer() {
        this.deliveredParcels = new LinkedList<Parcel>();
        this.receivingDateMap = new HashMap<Integer, Date>();
        this.withoutRecipientParcels = new LinkedList<Parcel>();
        this.wrongRecipientParcels = new LinkedList<Parcel>();
        this.commToRelayStation = null;
        this.commToRecipient = null;
    }

    public static void main(String[] args) {
        Deliverer myself = new Deliverer();
        myself.commToRelayStation = new DelivererCommunication(myself);
        myself.commToRecipient = new DelivererCommunication(myself);

        LCD.clear();
        LCD.drawString("Started.", 0, 0);

        new Thread(myself.commToRelayStation).start();

        Delay.msDelay(60000);
        new Thread(myself.commToRecipient).start();

        Button.UP.addKeyListener(myself.new ButtonEvent(myself));
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

    // TODO 削除
    public void dummy(DelivererCommunication comm, String str) {
        if (comm == commToRecipient)
            commToRelayStation.writeString(str + " -> Deliverer");
        else
            commToRecipient.writeString(str + " -> Deliverer");
    }

    /**
     * ユースケース「待機所で待機する」を包含するメソッド
     * <p>
     * ローカル定数
     * 待機時間 = 20秒
     * <p>
     * 統合テストで確認してください
     */
    public void waitInStandbyStation() {
        //中継所から合流点
        moveFromRelayStaToIntersection();
        rotate(90);
        System.out.println("retate(90)");
        isRightSide = false;
        //合流点から待機所
        moveFromIntersectionToStandbySta();
        rotate(180);
        System.out.println("retate(180)");

        //sleep20秒待つ
        Delay.msDelay(20000);

        goCheck();
    }

    /**
     * ユースケース「荷物を配達する」を包含するメソッド
     * <p>
     * 統合テストで確認してください
     */
    public void deliverParcels(List<Parcel> parcels) {
        int currentAddress;//現在番地
        int targetAddress;//目的番地

        /*ここはlistからarraylistに変わっているが大丈夫なのか気になった*/
        this.deliveredParcels.addAll(parcels);
        /*上記のどっち*/
        isRightSide = false;
        //中継所から合流点
        moveFromRelayStaToIntersection();
        rotate(90);
        System.out.println("rotate(90)");
        //合流点から中継所進入点
        moveFromIntersectionToEntryPoint();
        rotate(-90);
        //中継所進入点から受取人宅基準点
        moveFromEntryPointToStartingPoint();
        currentAddress = 1;
        for (Parcel parcel : this.deliveredParcels) {
            if (parcel.getAddress() != currentAddress) {
                targetAddress = parcel.getAddress();
                moveNextRecipient(currentAddress, targetAddress);
                currentAddress = targetAddress;
            }

            //TODO 受取人宅に取得した受取人個人情報をを送る
            this.commToRecipient.writeMethod("verifyRecipientInfo", currentAddress, parcel.getRecipientInfo());

            // commToRecipint.readBooleanOrNull()　仕様詳細について
            // 現在はbooleanなのでintの返り血（State?）になる予定
            // int 0: :normal, 1: :missed, 2: :absence
            // 上記の保管の上で下記記します。
            // int returnValue = commToRecipient.readBooleanOrNull();
//            switch (returnValue) {
//                case 0:
//                    //TODO 受取人宅に取り出した荷物を送る
//                    this.commToRecipient.writeMethod("receiveParcel", parcel);
//
//                    //TODO 受取人宅から受取時間を受け取る
//                    this.receivingDateMap.put(parcel.getRequestId(), Date.decode(this.commToRecipient.readString()));
//                    break;
//                case 1:
//                    this.wrongRecipientParcels.add(parcel);
//                    break;
//                case 2:
//                    this.withoutRecipientParcels.add(parcel);
//                    break;
//            }



            //TODO 10秒経過した場合

            //TODO 10秒経過しなかった場合

            //TODO 受取人宅から荷物の個人情報確認結果を受け取る
            if (commToRecipient.readBoolean() == true) {

                //TODO 受取人宅に取り出した荷物を送る
                this.commToRecipient.writeMethod("receiveParcel", parcel);

                //TODO 受取人宅から受取時間を受け取る
                this.receivingDateMap.put(parcel.getRequestId(), Date.decode(this.commToRecipient.readString()));
            } else {
                this.wrongRecipientParcels.add(parcel);
            }
        }
        moveNextRecipient(currentAddress, 1);

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
        rotate(-90);
        System.out.println("rotate(-90 )");
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
        lineTrace(76, 315);
        System.out.println("moveFromStandbyStaToEntryPoint\n");
    }

    /**
     * 現段階では,
     * 合流点から待機所までの距離をローカル定数として保持している
     * その距離を用いて「ライントレースする」を呼び出す
     * 形をとる予定である
     */
    private void moveFromIntersectionToStandbySta() {
        lineTrace(130f, 315);
        System.out.println("moveFromIntersectionToStandbySta\n");
    }

    /**
     * 現段階では,
     * 中継所進入点から中継所までの距離をローカル定数として保持している
     * その距離を用いて「ライントレースする」を呼び出す
     * 形をとる予定である
     */
    private void moveFromEntryPointToRelaySta() {

        while (!checkCanEntry()) {
            Delay.msDelay(10000);
        }

        moveFromEntryPointToIntersection();
        super.rotate(-84);
        System.out.println("retate(-84)");
        moveFromIntersectionToRelaySta();
        super.rotate(-180);
        System.out.println("retate(180)");

    }

    /**
     * 現段階では,
     * 中継所進入点から合流点までの距離をローカル定数として保持している
     * その距離を用いて「ライントレースする」を呼び出す
     * 形をとる予定である
     */
    private void moveFromEntryPointToIntersection() {
        lineTrace(37.5f, 150);
        System.out.println("moveFromEntryPointToIntersection\n");
    }

    /**
     * 現段階では,
     * 合流点から中継所進入点までの距離をローカル定数として保持している
     * その距離を用いて「ライントレースする」を呼び出す
     * 形をとる予定である
     */
    private void moveFromIntersectionToEntryPoint() {
        lineTrace(37, 150);
        System.out.println("moveFromIntersectionToEntryPoint\n");
    }

    /**
     * 現段階では,
     * 合流点から中継所までの距離をローカル定数として保持している
     * その距離を用いて「ライントレースする」を呼び出す
     * 形をとる予定である
     */
    private void moveFromIntersectionToRelaySta() {
        lineTrace(60, 150);
        System.out.println("moveFromIntersectionToRelaySta\n");
    }

    /**
     * 現段階では,
     * 中継所から合流点までの距離をローカル定数として保持している
     * その距離を用いて「ライントレースする」を呼び出す
     * 形をとる予定である
     */
    private void moveFromRelayStaToIntersection() {
        lineTrace(57.5f, 150);
        System.out.println("moveFromRelayStaToIntersection\n");
    }

    /**
     * 現段階では,
     * 中継所進入点から受取人宅基準点までの距離をローカル定数として保持している
     * その距離を用いて「ライントレースする」を呼び出す
     * 形をとる予定である
     */
    private void moveFromEntryPointToStartingPoint() {
        System.out.println("moveFromEntryPointToStartingPoint\n");
    }

    /**
     * 現段階では,
     * 受取人宅基準点から中継所進入点までの距離をローカル定数として保持している
     * その距離を用いて「ライントレースする」を呼び出す
     * 形をとる予定である
     */
    private void moveFromStartingPointToEntryPoint() {

        System.out.println("moveFromStartingPointToEntryPoint\n");
    }

    /**
     * 現在行 = (現在番地 / 4)
     * 目的行 = (目的番地 / 4)
     * 現在行 == 目的行を判定し,
     * trueならば, (1区間の距離 * (目的番地 - 現在番地)) だけ正方向に移動する
     * falseならば, (1区間の距離 * 現在番地) だけ負方向に移動する -> 90度回転 -> (1区間の距離 * 目的番地) だけ移動する
     * <p>
     * ローカル定数
     * 1区間の距離
     */
    private void moveNextRecipient(int from, int to) {
        //基準点に戻る場合
        if (from > to) {
            goBackToStandardPoint(from, to);
        } else {
            //同じ行移動？
            if ((from - 1) / 4 != (to - 1) / 4) {

                //列基準にいる？
                if ((from - 1) % 4 != 0) {

                    //列基準まで戻る
                    rotate(174);

                    for (int i = 0; i < (from - 1) % 4; i++) {
                        lineTrace(43.5f, 315);
                    }
                    rotate(-92);
                } else {
                    rotate(90);
                }
                for (int i = (from - 1) / 4; i < (to - 1) / 4; i++) {
                    lineTrace(43.5f, 315);
                }
                rotate(-92);

                for (int i = 0; i < (to - 1) % 4; i++) {
                    lineTrace(43.5f, 315);
                }
            }
            //同じ行の場合
            else {
                //列移動
                for (int i = (from - 1) % 4; i < (to - 1) % 4; i++) {
                    lineTrace(43.5f, 315);
                }
            }
        }
    }

    private void goBackToStandardPoint(int from, int to) {
        rotate(180);
        //列が違ったら
        if ((from - 1) % 4 != 0) {
            for (int i = 0; i < (from - 1) % 4; i++) {
                lineTrace(43.8f, 315);
            }
        }
        if ((from - 1) / 4 != 0) {
            rotate(90);
            for (int i = 0; i < (from - 1) / 4; i++) {
                lineTrace(44f, 315);
            }
            rotate(-90);
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
     * 中継所へ侵入確認を行う
     */
    private boolean checkCanEntry() {
        commToRelayStation.writeMethod("canEntry");
        return commToRelayStation.readBoolean();
    }

}
