/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk.database;

interface HandShakes {

	String TABLE_NAME = "handshakes";

	String ID = "id";
	String TIMESTAMP = "timestamp";
	String STAR = "star";
	String ASSOCIATED_KNOWN_CASE = "associated_known_case";

	String[] PROJECTION = {
			ID,
			TIMESTAMP,
			STAR,
			ASSOCIATED_KNOWN_CASE
	};

	static String create() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				TIMESTAMP + " INTEGER NOT NULL, " + STAR + " BLOB NOT NULL, " +
				ASSOCIATED_KNOWN_CASE + " INTEGER, FOREIGN KEY (" + ASSOCIATED_KNOWN_CASE + ") REFERENCES " +
				KnownCases.TABLE_NAME + " (" + KnownCases.ID + ") ON DELETE SET NULL)";
	}

	static String drop() {
		return "DROP TABLE IF EXISTS " + TABLE_NAME;
	}
}
