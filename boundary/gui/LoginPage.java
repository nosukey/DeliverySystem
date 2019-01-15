package boundary.gui;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * 依頼人個人情報を入力してログインするページクラスです。
 * @author 大久保美涼
 * @version 1.0
 */
public class LoginPage extends BasePage {
  /************************************************************************/
    private final int INPUT_NUM = 3; // textとlabelの配列の数
  /************************************************************************/

    private JTextField[] text;
    private static final PageName NAME = PageName.LOGIN;

    private final int NAME_ID    = 0;
    private final int ADDRESS_ID = 1;
    private final int PHONE_ID   = 2;

    /**
     * 依頼人個人情報を入力してログインするページを作成します。
     * @param frame MainFrameインスタンス
     */
    public LoginPage(MainFrame frame) {
        super(frame, NAME, new JLabel(NAME.toString()));
        /* ラベルの位置と文字のサイズ */
        final int LABEL_X = 170;
        final int LABEL_Y = 150;
        final int LABEL_W = 160;
        final int LABEL_H = 30;
        final int LETTER_SIZE = 16;

        /* テキストフィールドの位置と文字の入る長さ */
        final int TEXT_X = LABEL_X + LABEL_W;
        final int TEXT_Y = LABEL_Y;
        final int TEXT_W = 300;
        final int TEXT_H = LABEL_H;
        final int LETTER_LENGTH = 20;

        /* 確認ボタンの位置*/
        final int CFM_BUTTON_X = 320;
        final int CFM_BUTTON_Y = 370;
        final int CFM_BUTTON_W = 160;
        final int CFM_BUTTON_H = 50;

        final int DISTANCE = 50; // なんかいい名前（テキストとテキストの幅）

        JLabel[] label    = new JLabel[INPUT_NUM];
        label[NAME_ID]    = new JLabel("依頼人 名前");
        label[ADDRESS_ID] = new JLabel("依頼人 番地");
        label[PHONE_ID]   = new JLabel("依頼人 電話番号");

        this.text = new JTextField[INPUT_NUM];
        text[NAME_ID]    = new JTextField(LETTER_LENGTH);
        text[ADDRESS_ID] = new JTextField(LETTER_LENGTH);
        text[PHONE_ID]   = new JTextField(LETTER_LENGTH);


        int addY = 0;
        for(int i=0; i<INPUT_NUM; i++) {
          label[i].setFont(new Font("ＭＳ ゴシック", Font.BOLD, LETTER_SIZE));

          super.addComponent(label[i], LABEL_X, LABEL_Y + addY, LABEL_W, LABEL_H);
          super.addComponent(text[i], TEXT_X, TEXT_Y + addY, TEXT_W, TEXT_H);
          addY += DISTANCE;
        }


        JButton moveSelectButton = new JButton("ログイン");
        moveSelectButton.addActionListener(frame.new MoveDialogActionListener(frame, "この入力で決定しますか？", "メッセージ", NAME, PageName.USER_TOP));
        super.addComponent(moveSelectButton, CFM_BUTTON_X, CFM_BUTTON_Y, CFM_BUTTON_W, CFM_BUTTON_H);

    }

    /**
     * {@inheritDoc}
    */
    @Override
    public void refresh() {
        for(int i=0; i<INPUT_NUM; i++) {
          text[i].setText("");
        }
    }

    /**
     * {@inheritDoc}
    */
    @Override
    public boolean canChangePage(PageName page) {
        if(page == PageName.SETTING) return true;

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

    /**
     * テキストフィールドに入力された名前を取得します。
     * @return 名前
     */
    public String getName() {
        return text[NAME_ID].getText();
    }

    /**
     * テキストフィールドに入力された番地を取得します。
     * @return 番地
     */
    public int getAddress() {
        return Integer.parseInt(text[ADDRESS_ID].getText());
    }

    /**
     * テキストフィールドに入力された電話番号を取得します。
     * @return 電話番号
     */
    public String getPhoneNumber() {
        return text[PHONE_ID].getText();
    }
}
