package entity.common;

import java.util.Calendar;

/**
 * APIの日時クラスのラップクラス
 * 日時クラスをnewすることはない
 * 現在時刻を取得するによって日時インスタンスを取得することができる
 */
public class Date {

	private int year;

	private int month;

	private int day;

	private int hour;

	private int minute;

	private Date(int year, int month, int day, int hour, int min) {
		this.year   = year;
		this.month  = month;
		this.day    = day;
		this.hour   = hour;
		this.minute = min;
	}

	/**
	 * APIの日時クラスで現在時刻を取得し, 現在時刻の日時クラスのインスタンスを返す
	 */
	public static Date getCurrentDate() {
		Calendar calender = Calendar.getInstance();
		return new Date(
			calender.get(Calendar.YEAR),
			calender.get(Calendar.MONTH),
			calender.get(Calendar.DATE),
			calender.get(Calendar.HOUR),
			calender.get(Calendar.MINUTE)
		);
	}

	/**
	 * メソッド内容
	 * 文字列できた情報から日時クラスのインスタンスを返す
	 *
	 * 単体テスト
	 * 引数に日時に変換したい文字列を代入する
	 * ->文字列の情報と同じ日時を返すことを確認する
	 *
	 */
	public static Date decode(String str) {
		return null;
	}

	/**
	 * メソッド内容
	 * 日時を文字列に変換し、その文字列を返す
	 *
	 * 単体テスト
	 * 引数に文字列に変換したい日時を代入する
	 * ->日時の情報と同じ情報の文字列を返すことを確認する
	 *
	 */
	public static String encode(Date date) {
		return null;
	}

	public String toString() {
		return this.year + "/" + this.month + "/" + this.day + "/ " + this.hour + ":" + this.minute;
	}

}
