package boundary.gui;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

/**
 * 受取人宅の在否をチェックボックスで設定するページのクラスです。
 * @author 大場貴斗
 * @version 1.0
 */
public class SettingPage extends BasePage{
    /*配列ckboxの行と列の大きさ*/
    private final int LINE_SIZE = 4;
    private final int COLUMN_SIZE = 4;

    private JCheckBox[][] ckbox;
    private static final PageName NAME = PageName.SETTING;

    /**
     * 受取人宅の在否をチェックボックスで設定するページを作成します。
     * @param frame MainFrameインスタンス
    */
    public SettingPage(MainFrame frame) {
        super(frame, NAME, new JLabel(NAME.toString()));
        /*ckboxを作成するための座標・幅・高さ*/
        final int CKBOX_X = 60;
        final int CKBOX_Y = 90;
        final int CKBOX_W = 90;
        final int CKBOX_H = 90;

        /*ckboxのx座標の差とy座標の差*/
        final int DIFFERENCE_X = 150;
        final int DIFFERENCE_Y = 110;

        /*決定ボタンの座標・幅・高さ*/
        final int BUTTON_X = 650;
        final int BUTTON_Y = 430;
        final int BUTTON_W = 100;
        final int BUTTON_H = 70;

        this.ckbox = new JCheckBox[LINE_SIZE][COLUMN_SIZE];

        int block=1;  //番地の値(1から16まで)
        for(int i = 0 ; i < LINE_SIZE ; i++){
            for(int j = 0 ; j < COLUMN_SIZE ; j++){
                ckbox[i][j] = new JCheckBox( block + "番地", true);
                ckbox[i][j].setBorderPainted(true);
                ckbox[i][j].setBackground(new Color(0, 102, 255));
                block++;
            }
        }


        int addY=0;
        for(int i = 0 ; i < LINE_SIZE ; i++){
            for(int j = 0 ; j < COLUMN_SIZE ; j++){
                super.addComponent(ckbox[i][j], CKBOX_X + ( DIFFERENCE_X * j ), CKBOX_Y + addY, CKBOX_W, CKBOX_H);
            }
            addY+=DIFFERENCE_Y;
        }

        // ボタンを作成
        JButton button = new JButton("決定");
        button.addActionListener(parent.new MovePageActionListener(parent, NAME, PageName.SETTING));
        super.addComponent(button, BUTTON_X, BUTTON_Y, BUTTON_W, BUTTON_H);
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

    /**
     * 受取人宅の在否の配列を返します。
     *  @return 受取人宅の在否の配列
     */
    public boolean[] getBools() {
        boolean[] bools = new boolean[LINE_SIZE*COLUMN_SIZE];
        for(int i=0; i<LINE_SIZE; i++) {
            for(int j=0; j<COLUMN_SIZE; j++) {
                bools[i*LINE_SIZE+j] = ckbox[i][j].isSelected();
                if(bools[i*LINE_SIZE+j]) {
                  ckbox[i][j].setBackground(new Color(0, 102, 255));
                } else {
                  ckbox[i][j].setBackground(new Color(102, 102, 102));
                }
            }
        }
        return bools;
    }

}
