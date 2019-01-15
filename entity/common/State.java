package entity.common;

/**
 * 配達状況を持つ列挙型クラスです。
 * @author 澤田悠暉
 * @version 1.0
*/
public enum State {

	/**
	 * 「配達状況」未配達のシングルトン・インスタンスです。
	*/
	READY("未配達"),

	/**
	 * 「配達状況」配達中のシングルトン・インスタンスです。
	*/
	ON_DELIVERY("配達中"),

	/**
	 * 「配達状況」配達済みのシングルトン・インスタンスです。
	*/
	DELIVERY_SUCCESS("配達済み"),

	/**
	 * 「配達状況」中継所引き渡し失敗のシングルトン・インスタンスです。
	*/
	TRANSPORT_FAILURE("中継所引き渡し失敗"),

	/**
	 * 「配達状況」再配達のシングルトン・インスタンスです。
	*/
	RE_DELIVERY("再配達"),

	/**
	 * 「配達状況」宛先間違いのシングルトン・インスタンスです。
	*/
	WRONG_RECIPIENT("宛先間違い");

	private final String text;

	State(final String str) {
		this.text = str;
	}

	/**
	 * StateインスタンスをStringに変換します。
	 * {@inheritDoc}
	 * @return 配達状況の文字列表現
	*/
	@Override
	public String toString() {
		return this.text;
	}

	/**
	 * 通信フォーマットをStateインスタンスに変換します。通信のフォーマットに合わない文字列が与えられた場合にはnullを返します。
	 * @param str 通信フォーマットに従った文字列
	 * @return Stateインスタンス
	*/
	public static State decode(String str) {
		for(State state : State.values()) {
			if(state.ordinal() == Integer.parseInt(str))
				return state;
		}

		return null;
	}

	/**
	 * Stateインスタンスを通信フォーマットに従った文字列に変換します。
	 * @param state Stateインスタンス
	 * @return 通信フォーマットに従った文字列
	*/
	public static String encode(State state) {
		return "" + state.ordinal();
	}

}
