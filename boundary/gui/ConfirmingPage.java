package boundary.gui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

public class ConfirmingPage extends BasePage {
    private final int COMBO_X = 200;
    private final int COMBO_Y = 200;
    private final int COMBO_W = 400;
    private final int COMBO_H = 30;

    private final int OK_BUTTON_X = 325;
    private final int OK_BUTTON_Y = 450;
    private final int OK_BUTTON_W = 160;
    private final int OK_BUTTON_H = 50;

    private final int BACK_BUTTON_X = 640;
    private final int BACK_BUTTON_Y = 75;
    private final int BACK_BUTTON_W = 120;
    private final int BACK_BUTTON_H = 40;

    private static final PageName NAME = PageName.CONFIRMING;

    private Integer[] ids = null;
    private JComboBox<Integer> comboBox;
    private JLabel msg;
    private JButton moveShowRecordButton;

    public ConfirmingPage(MainFrame frame){
        super(frame, NAME, new JLabel(NAME.toString()));

        this.moveShowRecordButton = new JButton("決定");
        moveShowRecordButton.addActionListener(frame.new MovePageActionListener(frame, NAME, PageName.CONFIRM_RESULT));
        super.addComponent(moveShowRecordButton, OK_BUTTON_X, OK_BUTTON_Y, OK_BUTTON_W, OK_BUTTON_H);

        JButton moveSelectButton = new JButton("戻る");
        moveSelectButton.addActionListener(frame.new MovePageActionListener(frame, NAME, PageName.USER_TOP));
        super.addComponent(moveSelectButton, BACK_BUTTON_X, BACK_BUTTON_Y, BACK_BUTTON_W, BACK_BUTTON_H);
    }

    public void refresh(){
        if(ids == null) {
            // ちゃんとレイアウト決めてもらう
            this.msg = new JLabel("配達記録がありませんでした");
            super.addComponent(msg, COMBO_X, COMBO_Y, COMBO_W, COMBO_H);
            this.moveShowRecordButton.setVisible(false);
        } else {
            if(this.comboBox != null) {
                remove(this.comboBox);
                this.comboBox = null;
            }
            this.comboBox = new JComboBox<Integer>(ids);
            super.addComponent(comboBox, COMBO_X, COMBO_Y, COMBO_W, COMBO_H);
            this.moveShowRecordButton.setVisible(true);
        }
        // this.ids = null;
    }

    public boolean canChangePage(PageName page) {
        if(comboBox == null && page == PageName.CONFIRM_RESULT)
            return false;
        else
            return true;
    }

    public int getRequestId() {
        System.out.println("" + this.comboBox.getSelectedIndex());
        return this.comboBox.getItemAt(this.comboBox.getSelectedIndex()).intValue();
    }

    public void setRequestIds(Integer[] ids) {
        this.ids = ids;
    }

    public void resetRequestIds() {
        if(this.ids != null) {
            this.ids = null;
            remove(this.comboBox);
            this.comboBox = null;
        } else {
            if(this.msg == null) return;
            remove(this.comboBox);
            this.msg = null;
        }
    }

}
