package boundary.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * GUIページのベースとなる抽象クラスです。
 * @author 澤田 悠暉
 * @version 1.0
*/
public abstract class BasePage extends JPanel {
    protected MainFrame parent;
    private PageName name;

    private static final int MIN_ADDRESS = 1;
    private static final int MAX_ADDRESS = 16;

    private static final int PAGE_X = 0;
    private static final int PAGE_Y = 0;
    private static final int PAGE_W = 800;
    private static final int PAGE_H = 600;
    private static final int LABEL_H = 65;
    private static final int LABEL_FONT = 30;

    /**
     * ページを更新します。
    */
    public abstract void refresh();

    /**
     * ページを遷移して良いかを判定します。
     * @param page 遷移先ページの名前
     * @return 遷移しても良い場合はtrue
    */
    public abstract boolean canChangePage(PageName page);

    /**
     * ページを構成するための共通部分を初期化、生成します。
     * このクラスを継承する場合には必ずこのコンストラクタを呼び出すようにしてください。
    */
    public BasePage(MainFrame frame, PageName name, JLabel label) {
        this.parent = frame;
        this.name   = name;

        setLayout(null);
        setSize(PAGE_W, PAGE_H);
        setOpaque(true);
        setBackground(new Color(153, 204, 255));
        label.setBounds(PAGE_X, PAGE_Y, PAGE_W, LABEL_H);
        label.setOpaque(true);
        label.setBackground(new Color(102, 153, 255));
        label.setFont(new Font("ＭＳ ゴシック", Font.BOLD, LABEL_FONT));
        add(label);
    }

    /**
    * コンポーネントを指定の位置に追加します。
    * @param component 追加するコンポーネント
    * @param x コンポーネントのx座標
    * @param y コンポーネントのy座標
    * @param w コンポーネントの幅
    * @param h コンポーネントの高さ
    */
    public void addComponent(JComponent component, int x, int y, int w, int h) {
        component.setBounds(x, y, w, h);
        this.add(component);
    }

    /**
     * ページの名前を返します。
     * @return ページ名
    */
    public PageName getPageName() {
        return name;
    }

    protected boolean isCorrectPersonInfo(String name, int address, String phoneNumber) {
		return isCorrectName(name) && isCorrectAddress(address) && isCorrectPhoneNumber(phoneNumber);
	}

	private boolean isCorrectName(String name) {
		return !name.isEmpty();
	}

	private boolean isCorrectAddress(int address) {
		return address >= MIN_ADDRESS && address <= MAX_ADDRESS;
	}

	private boolean isCorrectPhoneNumber(String phoneNumber) {
        final String PHONE_REGEX = "^0(\\d[-(]\\d{4}|\\d{2}[-(]\\d{3}|\\d{3}[-(]\\d{2}|\\d{4}[-(]\\d)[-)]\\d{4}$|^0\\d{9}$|^(0[5789]0)[-(]\\d{4}[-)]\\d{4}$|^(0[5789]0)\\d{8}$";
		return phoneNumber.matches(PHONE_REGEX);
	}

}
