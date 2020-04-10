/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */
package ch.ubique.android.starsdk.database.models;

public class Handshake {

	private int id;
	private long timestamp;
	private byte[] star;
	private int txPowerLevel;
	private int rssi;

	public Handshake(int id, long timstamp, byte[] star, int txPowerLevel, int rssi) {
		this.id = id;
		this.timestamp = timstamp;
		this.star = star;
		this.txPowerLevel = txPowerLevel;
		this.rssi = rssi;
	}

	public byte[] getEphId() {
		return star;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public int getTxPowerLevel() {
		return txPowerLevel;
	}

	public int getRssi() {
		return rssi;
	}

}
