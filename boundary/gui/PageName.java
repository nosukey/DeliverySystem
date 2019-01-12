package boundary.gui;

enum PageName {
    LOGIN("   ログインしてください(Login Page)"),
    USER_TOP("   宅配サービスを選択してください(User Top Page)"),
    REQUESTING("   宛先情報を入力してください(Requesting Page)"),
    REQUEST_RESULT("   受付が完了しました(Request Result Page)"),
    CONFIRMING("   確認したい依頼IDを選択してください(Confirming Page)"),
    CONFIRM_RESULT("   配達状況を確認してください(Confirm Result Page)"),
    FIXING("   宛先情報を修正してください(Fixing Page)"),
    FIX_RESULT("   修正が完了しました(Fix Result Page)"),
    SETTING("  在宅の受取人番地にチェックをいれてください(Setting Page)");

    private final String name;

    private PageName(final String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
