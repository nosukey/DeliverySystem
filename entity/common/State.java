package entity.common;

public enum State {
	ready("未配達"),

	onDelivery("配達中"),

	deliverySuccess("配達済み"),

	transportFailure("中継所引き渡し失敗"),

	redelivery("再配達"),

	wrongRecipient("宛先間違い");

	private final String strState;

	private State(final String str) {
		this.strState = str;
	}

	public String toString() {
		return this.strState;
	}
}
