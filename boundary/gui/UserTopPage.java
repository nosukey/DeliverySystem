package boundary.gui;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * ユーザーが受けたいサービスを選択することができるページを作成するクラスです。
 * @author 池田はるか
 * @version 1.0(2019/01/13)
 */
public class UserTopPage extends BasePage {
    private final int LETTER_SIZE = 24;

    private final int REQUEST_BUTTON_X = 80;
    private final int RECORD_BUTTON_X = 420;
    private final int BUTTON_Y = 200;
    private final int BUTTON_W = 300;
    private final int BUTTON_H = 150;

    private final int BACK_BUTTON_X = 640;
    private final int BACK_BUTTON_Y = 75;
    private final int BACK_BUTTON_W = 120;
    private final int BACK_BUTTON_H = 40;

    private static final PageName NAME = PageName.USER_TOP;

    /**
     * ユーザーが受けたいサービスを選択することができるページを作成します。
     * @param frame メインフレーム
    */
    public UserTopPage(MainFrame frame){
        super(frame, NAME, new JLabel(NAME.toString()));

        JButton moveInputRecipientButton = new JButton("配達を依頼する");
        moveInputRecipientButton.setFont(new Font("ＭＳ ゴシック", Font.PLAIN, LETTER_SIZE));
        moveInputRecipientButton.addActionListener(frame.new MovePageActionListener(frame, NAME, PageName.REQUESTING));
        super.addComponent(moveInputRecipientButton, REQUEST_BUTTON_X, BUTTON_Y, BUTTON_W, BUTTON_H);

        JButton moveInputIDButton = new JButton("配達状況を確認する");
        moveInputIDButton.setFont(new Font("ＭＳ ゴシック", Font.PLAIN, LETTER_SIZE));
        moveInputIDButton.addActionListener(frame.new MovePageActionListener(frame, NAME, PageName.CONFIRMING));
        super.addComponent(moveInputIDButton, RECORD_BUTTON_X, BUTTON_Y, BUTTON_W, BUTTON_H);

        JButton moveLoginButton = new JButton("サインアウト");
        moveLoginButton.addActionListener(frame.new MovePageActionListener(frame, NAME, PageName.LOGIN));
        super.addComponent(moveLoginButton, BACK_BUTTON_X, BACK_BUTTON_Y, BACK_BUTTON_W, BACK_BUTTON_H);

    }

    /**
     * {@inheritDoc}
    */
    @Override
    public void refresh(){

    }

    /**
     * {@inheritDoc}
    */
    @Override
    public boolean canChangePage(PageName page) {
        return true;
    }

}
