package boundary.gui;

/**
 * ページの名前を持つ列挙型クラスです。
 * @author 澤田悠暉
 * @version 1.0(2019/01/13)
 */
enum PageName {
    /**
	 * ログインページのシングルトン・インスタンスです。
	 */
    LOGIN("   ログインしてください(Login Page)"),
    /**
     * ユーザートップページのシングルトン・インスタンスです。
     */
    USER_TOP("   宅配サービスを選択してください(User Top Page)"),
    /**
  	 * 受取人個人情報入力ページのシングルトン・インスタンスです。
     */
    REQUESTING("   宛先情報を入力してください(Requesting Page)"),
    /**
     * 依頼情報表示ページのシングルトン・インスタンスです。
     */
    REQUEST_RESULT("   受付が完了しました(Request Result Page)"),
    /**
     * 依頼ID入力ページのシングルトン・インスタンスです。
     */
    CONFIRMING("   確認したい依頼IDを選択してください(Confirming Page)"),
    /**
     * 配達記録確認ページのシングルトン・インスタンスです。
     */
    CONFIRM_RESULT("   配達状況を確認してください(Confirm Result Page)"),
    /**
     * 宛先修正ページのシングルトン・インスタンスです。
     */
    FIXING("   宛先情報を修正してください(Fixing Page)"),
    /**
     * 宛先修正した配達記録のシングルトン・インスタンスです。
     */
    FIX_RESULT("   修正が完了しました(Fix Result Page)"),
    /**
     * 受取人宅の設定ページのシングルトン・インスタンスです。
     */
    SETTING("  在宅の受取人番地にチェックをいれてください(Setting Page)");

    private final String name;

    private PageName(final String name) {
        this.name = name;
    }
    
    /**
     * PageNameオブジェクトをStringに変換します。
     * {@inheritDoc}
     * @return ページ名の文字列表現
     */
     @Override
    public String toString() {
        return this.name;
    }
}
