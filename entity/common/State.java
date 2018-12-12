package entity.common;

public enum State {
	READY("未配達"),

	ON_DELIVERY("配達中"),

	DELIVERY_SUCCESS("配達済み"),

	TRANSPORT_FAILURE("中継所引き渡し失敗"),

	RE_DELIVERY("再配達"),

	WRONG_RECIPIENT("宛先間違い");

	private final String text;

	private State(final String str) {
		this.text = str;
	}

	public String toString() {
		return this.text;
	}

	public static State decode(String str) {
		for(State state : State.values()) {
			if(state.ordinal() == Integer.parseInt(str))
				return state;
		}

		return null;
	}

	public static String encode(State state) {
		return "" + state.ordinal();
	}

}
