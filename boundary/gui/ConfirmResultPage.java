package boundary.gui;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * 参照を希望した依頼IDの配達記録を参照するページを作成するクラスです。
 * @author 池田はるか
 * @version 1.0
 */
public class ConfirmResultPage extends BasePage {
    private static final PageName NAME = PageName.CONFIRM_RESULT;

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

    private JLabel[] leftOutput;
    private JLabel[] rightOutput;
    private JButton button;

    /**
     * 参照を希望した依頼IDの配達記録を参照するページを作成します。
     * @param frame メインフレーム
     */
    public ConfirmResultPage(MainFrame frame) {
        super(frame, NAME, new JLabel(NAME.toString()));
        /*ラベルの文字の大きさ*/
        final int LETTER_SIZE = 16;

        /*左側と右側のラベルのそれぞれの座標*/
        final int LEFT_X = 30;
        final int LEFT_Y = 90;
        final int RIGHT_X = 340;
        final int RIGHT_Y = LEFT_Y;

        /*すべてのラベルの幅・高さ*/
        final int LABEL_W = 220;
        final int LABEL_H = 40;

        /*ラベルのy座標の差*/
        final int DIFFERENCE_Y = 50;


        JLabel[] leftHeading = {
            new JLabel("依頼ID : "),
            new JLabel("依頼人名前 : "),
            new JLabel("依頼人番地 : "),
            new JLabel("依頼人電話番号 : "),
            new JLabel("受取人名前 : "),
            new JLabel("受取人番地 : "),
            new JLabel("受取人電話番号 : ")
        };

        this.leftOutput = new JLabel[leftHeading.length];
        for(int i=0; i<this.leftOutput.length; i++) {
            leftOutput[i] = new JLabel();
        }

        JLabel[] rightHeading = {
            new JLabel("配達状況 : "),
            new JLabel("受付時間 : "),
            new JLabel("発送時間 : "),
            new JLabel("中継所到着時間 : "),
            new JLabel("配達開始時間 : "),
            new JLabel("受取時間 : "),
            new JLabel("配達完了時間 : ")
        };

        this.rightOutput = new JLabel[rightHeading.length];
        for(int i=0; i<this.rightOutput.length; i++) {
            rightOutput[i] = new JLabel();
        }

        int addY=0;

        for(int i = 0 ; i < this.leftOutput.length ; i++){
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
    }

    /**
     * {@inheritDoc}
    */
    @Override
    public void refresh() {

    }

    /**
     * {@inheritDoc}
    */
    @Override
    public boolean canChangePage(PageName page) {
        return true;
    }

    /**
     * ボタンの可視性を設定し, ボタンを設置します。
     * @param bool 宛先を修正した方が良い場合はtrue、その他の場合はfalse
     */
    public void setButtonVisible(boolean bool) {
        /*ボタンの座標・幅・高さ*/
        final int OK_BUTTON_X = 325;
        final int OK_BUTTON_Y = 450;
        final int OK_BUTTON_W = 160;
        final int OK_BUTTON_H = 50;

        if(this.button != null) {
            remove(this.button);
            this.button = null;
        }

        if(bool) {
            this.button = new JButton("宛先を修正する");
            this.button.addActionListener(parent.new MovePageActionListener(parent, NAME, PageName.FIXING));
        } else {
            this.button = new JButton("トップページに戻る");
            this.button.addActionListener(parent.new MovePageActionListener(parent, NAME, PageName.USER_TOP));
        }

        super.addComponent(this.button, OK_BUTTON_X, OK_BUTTON_Y, OK_BUTTON_W, OK_BUTTON_H);
    }

    /**
     * 参照した配達記録の情報を設定します。
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
