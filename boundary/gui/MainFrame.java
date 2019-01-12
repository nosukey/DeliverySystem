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

public class MainFrame extends JFrame {
    private DeliverySystem system;
    private Map<PageName, BasePage> pageMap;

    private final String ERROR_MESSAGE = "入力が正しくありません.";

    public class MovePageActionListener implements ActionListener {
        private JFrame parent;
        private PageName from, to;

        public MovePageActionListener(JFrame frame, PageName from, PageName to) {
            this.parent = frame;
            this.from   = from;
            this.to     = to;
        }

        public void actionPerformed(ActionEvent e) {
            BasePage page = pageMap.get(this.from);
            if(page.canChangePage(this.to)) {
                ParamData data = newDataFormat(this.from, this.to);
                if(system.canExecuteSubSystem(data)) {
                    changePage(this.from, this.to);
                    system.executeSubSystem(data);
                } else {
                    JOptionPane.showMessageDialog(this.parent, ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this.parent, ERROR_MESSAGE);
            }
        }
    }

    public class MoveDialogActionListener implements ActionListener {
        private JFrame parent;
        private String title;
        private String msg;
        private PageName from, to;

        private final String ERROR_MESSAGE = "入力が正しくありません.";

        public MoveDialogActionListener(JFrame frame, String msg, String title, PageName from, PageName to) {
            this.parent = frame;
            this.title  = title;
            this.msg    = msg;
            this.from   = from;
            this.to     = to;
        }

        public void actionPerformed(ActionEvent e) {
            BasePage page = pageMap.get(this.from);
            if(page.canChangePage(this.to)) {
                ParamData data = newDataFormat(this.from, this.to);
                if(system.canExecuteSubSystem(data)) {
                    int option = JOptionPane.showConfirmDialog(this.parent, this.msg, this.title, JOptionPane.YES_NO_OPTION);
                    if(option == JOptionPane.YES_OPTION) {
                        changePage(from, to);
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

    public MainFrame(DeliverySystem system, int x, int y, int w, int h) {
        this.system  = system;
        this.pageMap = new HashMap<PageName, BasePage>();

        setBounds(x, y, w, h);
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

    private void changePage(PageName from, PageName to) {
        if(to == PageName.LOGIN) {
            ((ConfirmingPage)this.pageMap.get(PageName.CONFIRMING)).resetRequestIds();
        }

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
        }

        return data;
    }

    public void setRequestResults(ParamData data) {
        ((RequestResultPage)this.pageMap.get(PageName.REQUEST_RESULT)).setOutputs(data);
    }

    public void setComfirmSelection(Integer[] ids) {
        ((ConfirmingPage)this.pageMap.get(PageName.CONFIRMING)).setRequestIds(ids);
    }

    public void setComfirmResults(ParamData data, boolean bool) {
        ConfirmResultPage page = (ConfirmResultPage)this.pageMap.get(PageName.CONFIRM_RESULT);
        page.setButton(bool);
        page.setOutputs(data);
    }

    public void setFixResults(ParamData data) {
        ((FixResultPage)this.pageMap.get(PageName.FIX_RESULT)).setOutputs(data);
    }
}
