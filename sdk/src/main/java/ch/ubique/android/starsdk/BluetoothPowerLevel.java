package ch.ubique.android.starsdk;

public enum BluetoothPowerLevel {
	ADVERTISE_MODE_BALANCED(1),
	ADVERTISE_MODE_LOW_LATENCY(2),
	ADVERTISE_MODE_LOW_POWER(0),
	ADVERTISE_TX_POWER_HIGH(3),
	ADVERTISE_TX_POWER_LOW(1),
	ADVERTISE_TX_POWER_MEDIUM(2),
	ADVERTISE_TX_POWER_ULTRA_LOW(0);

	private final int value;

	BluetoothPowerLevel(final int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
