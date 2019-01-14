package boundary.gui;

import controller.DeliverySystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * GUIのフレームのクラスです。
 * ページ遷移の管理をし、コントローラーと情報のやりとりをします。
 * @author 澤田 悠暉
 * @version 1.0(2019/01/13)
 */
public class MainFrame extends JFrame {
    private DeliverySystem system;
    private Map<PageName, BasePage> pageMap;

    private final String ERROR_MESSAGE = "入力が正しくありません.";

    /**
     * ページ遷移するためのアクションリスナーです。
    */
    public class MovePageActionListener implements ActionListener {
        private JFrame parent;
        private PageName from, to;

        /**
         * フレームと現在表示してるページと次に表示されるページを設定します。
         * @param frame フレーム
         * @param from 現在のページ名
         * @param to 次のページ名
         */
        public MovePageActionListener(JFrame frame, PageName from, PageName to) {
            this.parent = frame;
            this.from   = from;
            this.to     = to;
        }

        /**
         * {@inheritDoc}
        */
        @Override
        public void actionPerformed(ActionEvent e) {
            BasePage page = pageMap.get(this.from);
            if(page.canChangePage(this.to)) {
                ParamData data = newDataFormat(this.from, this.to);
                if(system.canExecuteSubSystem(data)) {
                    changePage(this.to);
                    system.executeSubSystem(data);
                } else {
                    JOptionPane.showMessageDialog(this.parent, ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this.parent, ERROR_MESSAGE);
            }
        }
    }

    /**
     * ダイアログを表示するためのアクションリスナーです。
    */
    public class MoveDialogActionListener implements ActionListener {
        private JFrame parent;
        private String title;
        private String msg;
        private PageName from, to;

        /**
         * 表示するダイアログを設定します。
         * @param frame フレーム
         * @param msg ダイアログに表示されるメッセージ
         * @param title 表示されるダイアログのタイトル
         * @param from 現在のページ
         * @param to 次のページ
         */
        public MoveDialogActionListener(JFrame frame, String msg, String title, PageName from, PageName to) {
            this.parent = frame;
            this.title  = title;
            this.msg    = msg;
            this.from   = from;
            this.to     = to;
        }

        /**
         * {@inheritDoc}
        */
        @Override
        public void actionPerformed(ActionEvent e) {
            BasePage page = pageMap.get(this.from);
            if(page.canChangePage(this.to)) {
                ParamData data = newDataFormat(this.from, this.to);
                if(system.canExecuteSubSystem(data)) {
                    int option = JOptionPane.showConfirmDialog(this.parent, this.msg, this.title, JOptionPane.YES_NO_OPTION);
                    if(option == JOptionPane.YES_OPTION) {
                        changePage(to);
                        system.executeSubSystem(data);
                    }
                } else {
                    JOptionPane.showMessageDialog(this.parent, ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this.parent, ERROR_MESSAGE);
            }

        }
    }

    /**
     * フレームを作成します。
     * 各ページを追加します。
     * メニューバーを設置します。
     * @param system システムのコントローラ
     * @param x フレームのx座標
     * @param y フレームのy座標
     * @param w フレームの幅
     * @param h フレームの高さ
     */
    public MainFrame(DeliverySystem system, int x, int y, int w, int h) {
        this.system  = system;
        this.pageMap = new HashMap<PageName, BasePage>();

        setBounds(x, y, w, h);
        setTitle("宅配システム");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        addPage(new LoginPage(this), true);
        addPage(new UserTopPage(this), false);
        addPage(new RequestingPage(this), false);
        addPage(new RequestResultPage(this), false);
        addPage(new ConfirmingPage(this), false);
        addPage(new ConfirmResultPage(this), false);
        addPage(new FixingPage(this), false);
        addPage(new FixResultPage(this), false);
        addPage(new SettingPage(this), false);

        JMenuBar bar = new JMenuBar();
        JMenu   menu = new JMenu("File");

        JMenuItem home = new JMenuItem("Home");
        home.addActionListener(new MovePageActionListener(this, PageName.SETTING, PageName.LOGIN));
        menu.add(home);

        JMenuItem setting = new JMenuItem("Setting");
        setting.addActionListener(new MovePageActionListener(this, PageName.LOGIN, PageName.SETTING));
        menu.add(setting);

        bar.add(menu);
        setJMenuBar(bar);

        setVisible(true);
    }

    private void addPage(BasePage page, boolean isVisible) {
        this.add(page);
        this.pageMap.put(page.getPageName(), page);
        page.setVisible(isVisible);
    }

    private void changePage(PageName to) {
        for(PageName name : this.pageMap.keySet()) {
            BasePage page = this.pageMap.get(name);
            if(name == to) {
                page.refresh();
                page.setVisible(true);
            } else {
                page.setVisible(false);
            }
        }
    }

    private ParamData newDataFormat(PageName from, PageName to) {
        ParamData data = null;

        BasePage page = this.pageMap.get(from);
        if(from == PageName.LOGIN) {
            if(to == PageName.USER_TOP) {
                data = new ParamData(
                    DeliverySystem.LOGIN,
                    ((LoginPage)page).getName(),
                    ((LoginPage)page).getAddress(),
                    ((LoginPage)page).getPhoneNumber()
                );
            }
        } else if(from == PageName.REQUESTING) {
            if(to == PageName.REQUEST_RESULT) {
                data = new ParamData(
                    DeliverySystem.REQUEST,
                    ((RequestingPage)page).getName(),
                    ((RequestingPage)page).getAddress(),
                    ((RequestingPage)page).getPhoneNumber()
                );
            }
        } else if(from == PageName.CONFIRMING) {
            if(to == PageName.CONFIRM_RESULT) {
                data = new ParamData(
                    DeliverySystem.REFER,
                    ((ConfirmingPage)page).getRequestId()
                );
            }
        } else if(from == PageName.FIXING) {
            if(to == PageName.FIX_RESULT) {
                data = new ParamData(
                    DeliverySystem.FIX,
                    ((FixingPage)page).getName(),
                    ((FixingPage)page).getAddress(),
                    ((FixingPage)page).getPhoneNumber()
                );
            }
        } else if(to == PageName.SETTING) {
            data = new ParamData(DeliverySystem.SETTING, ((SettingPage)page).getBools());
        } else if(to == PageName.LOGIN) {
            data = new ParamData(DeliverySystem.LOGOUT);
        } else if(to == PageName.CONFIRMING) {
            data = new ParamData(DeliverySystem.LOGGING);
        }

        return data;
    }

    /**
     * 依頼情報の確認ページに依頼情報を設定します。
     * @param data 依頼情報
    */
    public void setRequestResults(ParamData data) {
        ((RequestResultPage)this.pageMap.get(PageName.REQUEST_RESULT)).setOutputs(data);
    }

    /**
     * 依頼ID選択ページにユーザの保有している配達記録の依頼IDを設定します。
     * @param ids 配達記録の依頼IDの配列
    */
    public void setComfirmSelection(Integer[] ids) {
        ((ConfirmingPage)this.pageMap.get(PageName.CONFIRMING)).setRequestIds(ids);
    }

    /**
     * 設定された配達記録の依頼IDを削除します。
    */
    //これ現在は使われていない。けどでバックとか何かに使う？？
    public void resetComfirmSelection() {
        ((ConfirmingPage)this.pageMap.get(PageName.CONFIRMING)).removeRequestIds();
    }

    /**
     * 参照された配達記録の表示ページに参照結果を設定します。
     * @param data 参照結果
     * @param bool 参照した配達記録が宛先間違いであればtrue、そうでなければfalse
    */
    public void setComfirmResults(ParamData data, boolean bool) {
        ConfirmResultPage page = (ConfirmResultPage)this.pageMap.get(PageName.CONFIRM_RESULT);
        page.setButtonVisible(bool);
        page.setOutputs(data);
    }

    /**
     * 宛先修正結果の確認ページに宛先修正の結果を設定します。
     * @param data 宛先が修正された配達記録の情報
    */
    public void setFixResults(ParamData data) {
        ((FixResultPage)this.pageMap.get(PageName.FIX_RESULT)).setOutputs(data);
    }
}
