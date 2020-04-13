/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */
package ch.ubique.android.starsdk.database;

interface Handshakes {

	String TABLE_NAME = "handshakes";

	String ID = "id";
	String TIMESTAMP = "timestamp";
	String EPHID = "ephid";
	String TX_POWER_LEVEL = "tx_power_level";
	String RSSI = "rssi";

	String[] PROJECTION = {
			ID,
			TIMESTAMP,
			EPHID,
			TX_POWER_LEVEL,
			RSSI
	};

	static String create() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				TIMESTAMP + " INTEGER NOT NULL, " + EPHID + " BLOB NOT NULL, " +
				TX_POWER_LEVEL + " INTEGER, " + RSSI + " INTEGER)";
	}

	static String drop() {
		return "DROP TABLE IF EXISTS " + TABLE_NAME;
	}

}
