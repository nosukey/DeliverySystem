package boundary.gui;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

/**
 * 依頼した依頼IDを選択するページクラスです。
 * @author 澤田悠輝
 * @version 1.0
 */
public class ConfirmingPage extends BasePage {
    private static final PageName NAME = PageName.CONFIRMING;

    private Integer[] ids = null;
    private JComboBox<Integer> comboBox;
    private JLabel msg;
    private JButton moveShowRecordButton;

    /**
     * 依頼した依頼IDを選択するページを作成します。
     * @param frame メインフレーム
     */
    public ConfirmingPage(MainFrame frame){
        super(frame, NAME, new JLabel(NAME.toString()));

        final int BACK_BUTTON_X = 640;
        final int BACK_BUTTON_Y = 75;
        final int BACK_BUTTON_W = 120;
        final int BACK_BUTTON_H = 40;
        JButton moveSelectButton = new JButton("戻る");
        moveSelectButton.addActionListener(frame.new MovePageActionListener(frame, NAME, PageName.USER_TOP));
        super.addComponent(moveSelectButton, BACK_BUTTON_X, BACK_BUTTON_Y, BACK_BUTTON_W, BACK_BUTTON_H);
    }

    /**
     * {@inheritDoc}
    */
    @Override
    public void refresh(){
        final int COMBO_X = 200;
        final int COMBO_Y = 200;
        final int COMBO_W = 400;
        final int COMBO_H = 30;

        final int LABEL_X = 170;
        final int LABEL_Y = 250;
        final int LABEL_W = 500;
        final int LABEL_H = 30;
        final int LETTER_SIZE = 30;

        final int OK_BUTTON_X = 325;
        final int OK_BUTTON_Y = 450;
        final int OK_BUTTON_W = 160;
        final int OK_BUTTON_H = 50;

        removeComponents();

        if(ids == null) {
            this.msg = new JLabel("配達記録がありませんでした。");
            this.msg.setFont(new Font("ＭＳ ゴシック", Font.BOLD, LETTER_SIZE));
            super.addComponent(msg, LABEL_X, LABEL_Y, LABEL_W, LABEL_H);
        } else {
            this.comboBox = new JComboBox<Integer>(ids);
            super.addComponent(comboBox, COMBO_X, COMBO_Y, COMBO_W, COMBO_H);

            this.moveShowRecordButton = new JButton("決定");
            moveShowRecordButton.addActionListener(parent.new MovePageActionListener(parent, NAME, PageName.CONFIRM_RESULT));
            super.addComponent(moveShowRecordButton, OK_BUTTON_X, OK_BUTTON_Y, OK_BUTTON_W, OK_BUTTON_H);
        }
    }

    private void removeComponents() {
        if(this.comboBox != null) {
            remove(this.comboBox);
            this.comboBox = null;
        }

        if(this.msg != null) {
            remove(this.msg);
            this.msg = null;
        }

        if(this.moveShowRecordButton != null) {
            remove(this.moveShowRecordButton);
            this.moveShowRecordButton = null;
        }
    }

    /**
     * {@inheritDoc}
    */
    @Override
    public boolean canChangePage(PageName page) {
        return !(comboBox == null && page == PageName.CONFIRM_RESULT);
    }

    /**
     * 選択した依頼IDを取得します。
     * @return 選択した依頼ID
     */
    public int getRequestId() {
        return this.comboBox.getItemAt(this.comboBox.getSelectedIndex()).intValue();
    }

    /**
     * 配達記録に依頼IDを登録します。
     * @param ids 依頼IDの配列
     */
    public void setRequestIds(Integer[] ids) {
        this.ids = ids;
    }

    /**
     * 登録されていた依頼IDを全て削除します。
    */
    public void removeRequestIds() {
        this.ids = null;
    }

}
