package ch.ubique.android.starsdk;

import android.bluetooth.le.AdvertiseSettings;

public enum BluetoothAdvertiseMode {
	ADVERTISE_MODE_LOW_POWER(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER),
	ADVERTISE_MODE_BALANCED(AdvertiseSettings.ADVERTISE_MODE_BALANCED),
	ADVERTISE_MODE_LOW_LATENCY(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);

	private final int value;

	BluetoothAdvertiseMode(final int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
