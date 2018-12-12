package controller;

import entity.inPC.Reception;

public class ReceptionObserver {

	private Reception reception;

	private int numOfStocks;

	private boolean hasCollector;

	/**
	 * 事前条件
	 * 未配達の荷物リストの要素数と再配達の荷物リストの要素数の合計が引数として渡されることとする
	 * 
	 * メソッド内容
	 * 在庫数 = 引数の要素数 に更新する
	 * 収集担当ロボットがいるかをfalseにする
	 * 
	 * 単体テスト
	 * 在庫数に適当な数値を入れてみて
	 */
	public void init(int numOfStocks) {

	}

	/**
	 * 事前条件
	 * 未配達の荷物リストの要素数と再配達の荷物リストの要素数の合計が引数として渡されることとする
	 * 
	 * メソッド内容
	 * 在庫数 = 引数の要素数 に更新する
	 * 発送開始条件を判定する
	 * trueならば宅配受付所の「発送させる」を呼び出す
	 * falseならば何もしない
	 * 
	 * 単体テスト
	 * 「収集担当ロボットがいるか」がtrueの状況で引数に2を代入する
	 * -> 在庫数を出力し, 2であることを確認する
	 * -> 発送開始条件の判定結果を出力し, falseであることを確認する
	 * 「収集担当ロボットがいるか」がtrueの状況で引数に3を代入する
	 * -> 在庫数を出力し, 3であることを確認する
	 * -> 発送開始条件の判定結果を出力し, trueであることを確認する
	 * 「収集担当ロボットがいるか」がfalseの状況で引数に2を代入する
	 * -> 在庫数を出力し, 2であることを確認する
	 * -> 発送開始条件の判定結果を出力し, falseであることを確認する
	 * 「収集担当ロボットがいるか」がfalseの状況で引数に3を代入する
	 * -> 在庫数を出力し, 3であることを確認する
	 * -> 発送開始条件の判定結果を出力し, falseであることを確認する
	 */
	public void update(int numOfStocks) {

	}

	/**
	 * 引数には基本trueしか入らないため「収集担当ロボットがいるか」をtrueに更新するメソッドだと考えてもらえればよい
	 * 
	 * メソッド内容
	 * 「収集担当ロボットがいるか」 = true
	 * 発送開始条件を判定する
	 * trueならばすぐに, falseならば90秒後に, 在庫数を参照し, 
	 * 0ならば何もしない
	 * 1以上ならば宅配受付所の「発送させる」を呼ぶ
	 * 
	 * 単体テスト
	 * 在庫数が0である場合
	 * 在庫数が2であり, 90秒以内に在庫数が3個になる場合
	 * 在庫数が2であり, 90秒以内に在庫数が変わらない場合
	 * を判定結果を判定し, 確認する
	 */
	public void update(boolean hasCollector) {

	}

	/**
	 * 事前条件
	 * 未配達の荷物リストの要素数と再配達の荷物リストの要素数の合計が引数として渡されることとする
	 * 
	 * メソッド内容
	 * 在庫数 = 引数の要素数
	 * 「収集担当ロボットがいるか」 = true
	 * 発送開始条件を判定する
	 * trueならばすぐに, falseならば90秒後に, 在庫数を参照し, 
	 * 0ならば何もしない
	 * 1以上ならば宅配受付所の「発送させる」を呼ぶ
	 * 
	 * 単体テスト
	 * 在庫数が0である場合
	 * 在庫数が2であり, 90秒以内に在庫数が3個になる場合
	 * 在庫数が2であり, 90秒以内に在庫数が変わらない場合
	 * を判定結果を判定し, 確認する
	 */
	public void update(int numOfStocks, boolean hasCollector) {

	}

	/**
	 * メソッド内容
	 * 「収集担当ロボットがいるか」 && 在庫数が3個以上である
	 * の判定結果を返す
	 */
	private boolean canStartTransport() {
		return false;
	}

}
