package boundary.gui;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class FixingPage extends BasePage {
  /************************************************************************/
    /* ラベルの位置と文字のサイズ */
    private final int LABEL_X = 170;
    private final int LABEL_Y = 150;
    private final int LABEL_W = 160;
    private final int LABEL_H = 30;
    private final int LETTER_SIZE = 16;

    /* テキストフィールドの位置と文字の入る長さ */
    private final int TEXT_X = LABEL_X + LABEL_W;
    private final int TEXT_Y = LABEL_Y;
    private final int TEXT_W = 300;
    private final int TEXT_H = LABEL_H;
    private final int LETTER_LENGTH = 20;

    /* 確認ボタンの位置*/
    private final int CFM_BUTTON_X = 320;
    private final int CFM_BUTTON_Y = 370;
    private final int CFM_BUTTON_W = 160;
    private final int CFM_BUTTON_H = 50;

    private final int INPUT_NUM = 3; // textとlabelの配列の数
    private final int DISTANCE = 50; // なんかいい名前（テキストとテキストの幅）
  /************************************************************************/

    private JTextField[] text;
    private static final PageName NAME = PageName.FIXING;

    private static final int NAME_ID    = 0;
    private static final int ADDRESS_ID = 1;
    private static final int PHONE_ID   = 2;

    public FixingPage(MainFrame frame) {
        super(frame, NAME, new JLabel(NAME.toString()));

        JLabel[] label = new JLabel[INPUT_NUM];
        label[0] = new JLabel("受取人 名前");
        label[1] = new JLabel("受取人 番地");
        label[2] = new JLabel("受取人 電話番号");

        this.text = new JTextField[INPUT_NUM];
        text[0] = new JTextField(LETTER_LENGTH);
        text[1] = new JTextField(LETTER_LENGTH);
        text[2] = new JTextField(LETTER_LENGTH);

        int addY = 0;
        for(int i=0; i<INPUT_NUM; i++) {
          label[i].setFont(new Font("ＭＳ ゴシック", Font.BOLD, LETTER_SIZE));

          super.addComponent(label[i], LABEL_X, LABEL_Y + addY, LABEL_W, LABEL_H);
          super.addComponent(text[i], TEXT_X, TEXT_Y + addY, TEXT_W, TEXT_H);
          addY += DISTANCE;
        }


        JButton moveFixResultButton = new JButton("決定");
        moveFixResultButton.addActionListener(frame.new MoveDialogActionListener(frame, "この入力で決定しますか？", "Move Page Dialog", NAME, PageName.FIX_RESULT));
        super.addComponent(moveFixResultButton, CFM_BUTTON_X, CFM_BUTTON_Y, CFM_BUTTON_W, CFM_BUTTON_H);

    }

    public void refresh() {
      for(int i=0; i<INPUT_NUM; i++) {
        text[i].setText("");
      }
    }

    public boolean canChangePage(PageName page) {
        if(page == PageName.USER_TOP) return true;

        if(isNullOrEmpty()) {
            return false;
        } else {
            try{
                if(isCorrectPersonInfo(
                    text[NAME_ID].getText(),
                    Integer.parseInt(text[ADDRESS_ID].getText()),
                    text[PHONE_ID].getText()
                )) {
                    return true;
                } else {
                    return false;
                }
            } catch(NumberFormatException e) {
                return false;
            }
        }
    }

    private boolean isNullOrEmpty() {
        try{
            for(int i=0; i<INPUT_NUM; i++) {
                if(text[i].getText().isEmpty()) {
                    return true;
                }
            }
        } catch(NullPointerException e) {
            return true;
        }

        return false;
    }

    public String getName() {
        return text[NAME_ID].getText();
    }

    public int getAddress() {
        return Integer.parseInt(text[ADDRESS_ID].getText());
    }

    public String getPhoneNumber() {
        return text[PHONE_ID].getText();
    }

}
