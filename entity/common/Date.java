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

	private static final String NOTHING = ",,,,";

	private static final String COMMA = ",";

	private Date(int year, int month, int day, int hour, int min) {
		this.year   = year;
		this.month  = month;
		this.day    = day;
		this.hour   = hour;
		this.minute = min;
	}

	/**
	 * APIの日時クラスで現在時刻を取得し, 現在時刻の日時クラスのインスタンスを返します。
	 * @return Dateオブジェクト。
	 */
	public static Date getCurrentDate() {
		Calendar calender = Calendar.getInstance();
		return new Date(
			calender.get(Calendar.YEAR),
			calender.get(Calendar.MONTH) + 1, 		// ずれ補正
			calender.get(Calendar.DATE),
			calender.get(Calendar.HOUR_OF_DAY),
			calender.get(Calendar.MINUTE)
		);
	}

	/**
	 * 文字列できた情報から日時クラスのインスタンスを返します。
	 * @param str 通信フォーマットに従った文字列。
	 * @return Dateオブジェクト。 Returns {@code null} if strが日付を表現する文字列でなかったとき。
	 */
	public static Date decode(String str) {
		if(str.equals(NOTHING)) {
			return null;
		} else {
			String[] parameters = str.split(COMMA);
			return new Date(
			Integer.parseInt(parameters[0]),
			Integer.parseInt(parameters[1]),
			Integer.parseInt(parameters[2]),
			Integer.parseInt(parameters[3]),
			Integer.parseInt(parameters[4])
			);
		}
	}

	/**
	 * 日時を文字列に変換し、その文字列を返します。
	 * @param date Dataオブジェクト。
	 * @return String　通信フォーマットに従った文字列。 Returns {@code null} if dateがnullであるとき。
	 */
	public static String encode(Date date) {
		if(date == null)
			return NOTHING;
		else
			return date.year + COMMA + date.month + COMMA + date.day + COMMA + date.hour + COMMA + date.minute;
	}

	/**
	 * DataオブジェクトをStringに変換し、返します。
	 * @return String 日時の文字列表現。
	 */
	public String toString() {
		return String.format("%d/%02d/%02d/ %02d:%02d", this.year, this.month, this.day, this.hour, this.minute);
	}

}
