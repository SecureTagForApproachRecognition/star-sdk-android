package ch.ubique.android.starsdk;

import android.bluetooth.le.AdvertiseSettings;

public enum BluetoothTxPowerLevel {
	ADVERTISE_TX_POWER_ULTRA_LOW(AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW),
	ADVERTISE_TX_POWER_LOW(AdvertiseSettings.ADVERTISE_TX_POWER_LOW),
	ADVERTISE_TX_POWER_MEDIUM(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM),
	ADVERTISE_TX_POWER_HIGH(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);

	private final int value;

	BluetoothTxPowerLevel(final int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
