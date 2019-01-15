package boundary.gui;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * 依頼情報の確認ページを作成するクラスです。
 * @author 大久保美涼
 * @version 1.0
 */
public class RequestResultPage extends BasePage {
    private static final PageName NAME = PageName.REQUEST_RESULT;

    private JLabel[] leftOutput;
    private JLabel[] rightOutput;

    private static final int ID_ID      = 0;
    private static final int NAME_ID    = 1;
    private static final int ADDRESS_ID = 2;
    private static final int PHONE_ID   = 3;

    /**
     * 依頼情報の確認ページを作成します。
     * @param frame MainFrameインスタンス
    */
    public RequestResultPage(MainFrame frame) {
        super(frame, NAME, new JLabel(NAME.toString()));
        /* ラベルの位置と文字のサイズ */
        final int LABEL_W = 220;
        final int LABEL_H = 40;
        final int LETTER_SIZE = 16;
        /* 左側 */
        final int LEFT_LABEL_X = 30;
        final int LEFT_LABEL_Y = 150;
        /* 右側 */
        final int RIGHT_LABEL_X = 340;
        final int RIGHT_LABEL_Y = LEFT_LABEL_Y;

        /* OKボタンの位置 */
        final int OK_BUTTON_X = 325;
        final int OK_BUTTON_Y = 450;
        final int OK_BUTTON_W = 160;
        final int OK_BUTTON_H = 50;

        final int OUTPUT_NUM  = 4; // 配列の数
        final int DISTANCE = 50;   // テキストとテキストの幅

        JLabel leftHading[] = new JLabel[OUTPUT_NUM];
        leftHading[0] = new JLabel("依頼ID : ");
        leftHading[1] = new JLabel("依頼人名前 : ");
        leftHading[2] = new JLabel("依頼人番地 : ");
        leftHading[3] = new JLabel("依頼人電話番号 : ");

        leftOutput    = new JLabel[OUTPUT_NUM];
        leftOutput[0] = new JLabel();
        leftOutput[1] = new JLabel();
        leftOutput[2] = new JLabel();
        leftOutput[3] = new JLabel();

        JLabel rightHading[] = new JLabel[OUTPUT_NUM];
        rightHading[0] = new JLabel("");
        rightHading[1] = new JLabel("受取人名前 : ");
        rightHading[2] = new JLabel("受取人番地 : ");
        rightHading[3] = new JLabel("受取人電話番号 : ");

        rightOutput    = new JLabel[OUTPUT_NUM];
        rightOutput[0] = new JLabel();
        rightOutput[1] = new JLabel();
        rightOutput[2] = new JLabel();
        rightOutput[3] = new JLabel();


        int addY = 0;
        for(int i=0; i<OUTPUT_NUM; i++) {
          leftHading[i].setFont(new Font("ＭＳ ゴシック", Font.BOLD, LETTER_SIZE));
          leftOutput[i].setFont(new Font("ＭＳ ゴシック", Font.BOLD, LETTER_SIZE));
          rightHading[i].setFont(new Font("ＭＳ ゴシック", Font.BOLD, LETTER_SIZE));
          rightOutput[i].setFont(new Font("ＭＳ ゴシック", Font.BOLD, LETTER_SIZE));

          leftHading[i].setHorizontalAlignment(JLabel.RIGHT);
          rightHading[i].setHorizontalAlignment(JLabel.RIGHT);

          super.addComponent(leftHading[i], LEFT_LABEL_X, LEFT_LABEL_Y + addY, LABEL_W, LABEL_H);
          super.addComponent(leftOutput[i], LEFT_LABEL_X + LABEL_W, LEFT_LABEL_Y + addY, LABEL_W, LABEL_H);
          super.addComponent(rightHading[i], RIGHT_LABEL_X, RIGHT_LABEL_Y + addY, LABEL_W, LABEL_H);
          super.addComponent(rightOutput[i], RIGHT_LABEL_X + LABEL_W, RIGHT_LABEL_Y + addY, LABEL_W, LABEL_H);

          addY += DISTANCE;
        }


        JButton moveSelectButton = new JButton("トップページに戻る");
        moveSelectButton.addActionListener(frame.new MovePageActionListener(frame, NAME, PageName.USER_TOP));
        super.addComponent(moveSelectButton, OK_BUTTON_X, OK_BUTTON_Y, OK_BUTTON_W, OK_BUTTON_H);
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
     * 依頼結果を設定します。
     * @param data 依頼結果
    */
    public void setOutputs(ParamData data) {
        leftOutput[ID_ID].setText("" + data.getRequestId());
        leftOutput[NAME_ID].setText(data.getClientName());
        leftOutput[ADDRESS_ID].setText("" + data.getClientAddress());
        leftOutput[PHONE_ID].setText(data.getClientPhoneNumber());
        rightOutput[NAME_ID].setText(data.getRecipientName());
        rightOutput[ADDRESS_ID].setText("" + data.getRecipientAddress());
        rightOutput[PHONE_ID].setText(data.getRecipientPhoneNumber());
    }

}
