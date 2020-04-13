/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */
package ch.ubique.android.starsdk.database;

interface Contacts {

	String TABLE_NAME = "contacts";

	String ID = "id";
	String DATE = "date";
	String EPHID = "ephid";
	String ASSOCIATED_KNOWN_CASE = "associated_known_case";

	String[] PROJECTION = {
			ID,
			DATE,
			EPHID,
			ASSOCIATED_KNOWN_CASE
	};

	static String create() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				DATE + " INTEGER NOT NULL, " + EPHID + " BLOB NOT NULL, " +
				ASSOCIATED_KNOWN_CASE + " INTEGER, FOREIGN KEY (" + ASSOCIATED_KNOWN_CASE + ") REFERENCES " +
				KnownCases.TABLE_NAME + " (" + KnownCases.ID + ") ON DELETE SET NULL)";
	}

	static String drop() {
		return "DROP TABLE IF EXISTS " + TABLE_NAME;
	}

}
