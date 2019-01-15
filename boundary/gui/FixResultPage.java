package boundary.gui;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * 受取人個人情報を修正した配達記録を参照するページを作成するクラスです。
 * @author 池田はるか
 * @version 1.0
 */
public class FixResultPage extends BasePage {
    /*ラベルの文字の大きさ*/
    private final int LETTER_SIZE = 16;

    /*左側と右側のラベルのそれぞれの座標*/
    private final int LEFT_X = 30;
    private final int LEFT_Y = 90;
    private final int RIGHT_X = 340;
    private final int RIGHT_Y = LEFT_Y;

    /*すべてのラベルの幅・高さ*/
    private final int LABEL_W = 220;
    private final int LABEL_H = 40;

    /*ボタンの座標・幅・高さ*/
    private final int OK_BUTTON_X = 325;
    private final int OK_BUTTON_Y = 450;
    private final int OK_BUTTON_W = 160;
    private final int OK_BUTTON_H = 50;

    /*配列の要素数(一列に並べる配達記録の数)*/
    private final int OUTPUT_NUM = 7;

    /*ラベルのy座標の差*/
    private final int DIFFERENCE_Y = 50;

    private static final PageName NAME = PageName.FIX_RESULT;

    private JLabel[] leftOutput;
    private JLabel[] rightOutput;

    private static final int REQUEST_ID_ID        = 0;
    private static final int CLIENT_NAME_ID       = 1;
    private static final int CLIENT_ADDRESS_ID    = 2;
    private static final int CLIENT_PHONE_ID      = 3;
    private static final int RECIPIENT_NAME_ID    = 4;
    private static final int RECIPIENT_ADDRESS_ID = 5;
    private static final int RECIPIENT_PHONE_ID   = 6;

    private static final int STATE_ID                   = 0;
    private static final int RECEPTION_DATE_ID          = 1;
    private static final int TRANSPORT_STARTING_DATE_ID = 2;
    private static final int TRANSPORT_SUCCESS_DATE_ID  = 3;
    private static final int DELIVERY_STARTING_DATE_ID  = 4;
    private static final int RECEIVING_DATE_ID          = 5;
    private static final int DELIVERY_SUCCESS_DATE_ID   = 6;

    /**
     * 取人個人情報を修正した配達記録を参照するページを作成します。
     * @param frame MainFrameインスタンス
     */
    public FixResultPage(MainFrame frame){
        super(frame, NAME, new JLabel(NAME.toString()));

        JLabel[] leftHeading = new JLabel[OUTPUT_NUM];
        leftHeading[0] = new JLabel("依頼ID : ");
        leftHeading[1] = new JLabel("依頼人名前 : ");
        leftHeading[2] = new JLabel("依頼人番地 : ");
        leftHeading[3] = new JLabel("依頼人電話番号 : ");
        leftHeading[4] = new JLabel("受取人名前 : ");
        leftHeading[5] = new JLabel("受取人番地 : ");
        leftHeading[6] = new JLabel("受取人電話番号 : ");

        this.leftOutput = new JLabel[OUTPUT_NUM];
        leftOutput[0] = new JLabel();
        leftOutput[1] = new JLabel();
        leftOutput[2] = new JLabel();
        leftOutput[3] = new JLabel();
        leftOutput[4] = new JLabel();
        leftOutput[5] = new JLabel();
        leftOutput[6] = new JLabel();

        JLabel[] rightHeading = new JLabel[OUTPUT_NUM];
        rightHeading[0] = new JLabel("配達状況 : ");
        rightHeading[1] = new JLabel("受付時間 : ");
        rightHeading[2] = new JLabel("発送時間 : ");
        rightHeading[3] = new JLabel("中継所到着時間 : ");
        rightHeading[4] = new JLabel("配達開始時間 : ");
        rightHeading[5] = new JLabel("受取時間 : ");
        rightHeading[6] = new JLabel("配達完了時間 : ");

        this.rightOutput = new JLabel[OUTPUT_NUM];
        rightOutput[0] = new JLabel();
        rightOutput[1] = new JLabel();
        rightOutput[2] = new JLabel();
        rightOutput[3] = new JLabel();
        rightOutput[4] = new JLabel();
        rightOutput[5] = new JLabel();
        rightOutput[6] = new JLabel();

        int addY=0;

        for(int i = 0 ; i < OUTPUT_NUM ; i++){
            leftHeading[i].setHorizontalAlignment(JLabel.RIGHT);
            rightHeading[i].setHorizontalAlignment(JLabel.RIGHT);

            leftHeading[i].setFont(new Font("ＭＳ ゴシック", Font.BOLD, LETTER_SIZE));
            leftOutput[i].setFont(new Font("ＭＳ ゴシック", Font.BOLD, LETTER_SIZE));
            rightHeading[i].setFont(new Font("ＭＳ ゴシック", Font.BOLD, LETTER_SIZE));
            rightOutput[i].setFont(new Font("ＭＳ ゴシック", Font.BOLD, LETTER_SIZE));

            super.addComponent(leftHeading[i], LEFT_X, LEFT_Y + addY, LABEL_W, LABEL_H);
            super.addComponent(leftOutput[i], LEFT_X+LABEL_W, LEFT_Y + addY, LABEL_W, LABEL_H);
            super.addComponent(rightHeading[i], RIGHT_X, RIGHT_Y + addY, LABEL_W, LABEL_H);
            super.addComponent(rightOutput[i], RIGHT_X + LABEL_W, RIGHT_Y + addY, LABEL_W, LABEL_H);

            addY += DIFFERENCE_Y;
        }


        JButton moveSelectPageButton = new JButton("トップページに戻る");
        moveSelectPageButton.addActionListener(frame.new MovePageActionListener(frame, NAME, PageName.USER_TOP));
        super.addComponent(moveSelectPageButton, OK_BUTTON_X, OK_BUTTON_Y, OK_BUTTON_W, OK_BUTTON_H);

    }

    /**
     * {@inheritDoc}
    */
    @Override
    public void refresh(){
        for(JLabel output : leftOutput) {
            output.setText("");
        }
        for(JLabel output : rightOutput) {
            output.setText("");
        }
    }

    /**
     * {@inheritDoc}
    */
    @Override
    public boolean canChangePage(PageName page) {
        return true;
    }

    /**
     * 宛先を修正した配達記録の情報を設定します。
     * @param data 出力したい配達記録のデータ
    */
    public void setOutputs(ParamData data) {
        leftOutput[REQUEST_ID_ID].setText("" + data.getRequestId());
        leftOutput[CLIENT_NAME_ID].setText(data.getClientName());
        leftOutput[CLIENT_ADDRESS_ID].setText("" + data.getClientAddress());
        leftOutput[CLIENT_PHONE_ID].setText(data.getClientPhoneNumber());
        leftOutput[RECIPIENT_NAME_ID].setText(data.getRecipientName());
        leftOutput[RECIPIENT_ADDRESS_ID].setText("" + data.getRecipientAddress());
        leftOutput[RECIPIENT_PHONE_ID].setText(data.getRecipientPhoneNumber());

        rightOutput[STATE_ID].setText(data.getStateStr());
        rightOutput[RECEPTION_DATE_ID].setText(data.getReceptionDateStr());
        rightOutput[TRANSPORT_STARTING_DATE_ID].setText(data.getTransportStartingDateStr());
        rightOutput[TRANSPORT_SUCCESS_DATE_ID].setText(data.getTransportSuccessDateStr());
        rightOutput[DELIVERY_STARTING_DATE_ID].setText(data.getDeliveryStartingDateStr());
        rightOutput[RECEIVING_DATE_ID].setText(data.getReceivingDateStr());
        rightOutput[DELIVERY_SUCCESS_DATE_ID].setText(data.getDeliverySuccessDateStr());
    }

}
