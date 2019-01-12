package boundary.gui;

//import java.awt.Color;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class BasePage extends JPanel {
    protected MainFrame parent;
    private PageName name;
    private JLabel label;

    public abstract void refresh();

    public abstract boolean canChangePage(PageName page);

    public BasePage(MainFrame frame, PageName name, JLabel label) {
        this.parent = frame;
        this.name   = name;
        this.label  = label;

        setLayout(null);
        setSize(800, 600);
//        setOpaque(true);
//        setBackground(new Color(153,204,255));

        this.label.setBounds(0, 0, 800, 65);
//        this.label.setOpaque(true);
//        this.label.setBackground(new Color(102,153,255));
        this.label.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 30));
        add(this.label);
    }

    public void addComponent(JComponent component, int x, int y, int w, int h) {
        component.setBounds(x, y, w, h);
        this.add(component);
    }

    public PageName getPageName() {
        return name;
    }

    protected boolean isCorrectPersonInfo(String name, int address, String phoneNumber) {
		return isCorrectName(name) && isCorrectAddress(address) && isCorrectPhoneNumber(phoneNumber);
	}

	/**
	 * 空文字でないかを判定する
	 */
	private boolean isCorrectName(String name) {
		return !name.isEmpty();
	}

	/**
	 * 1-16の数値であるかを判定する
	 */
	private boolean isCorrectAddress(int address) {
		return address >= 1 && address <= 16;
	}

	/**
	 * 桁数が正当であるかを判定する
	 */
	private boolean isCorrectPhoneNumber(String phoneNumber) {
		return phoneNumber.matches("^0(\\d[-(]\\d{4}|\\d{2}[-(]\\d{3}|\\d{3}[-(]\\d{2}|\\d{4}[-(]\\d{1})[-)]\\d{4}$|^0\\d{9}$|^(0[5789]0)[-(]\\d{4}[-)]\\d{4}$|^(0[5789]0)\\d{8}$");
	}

}
