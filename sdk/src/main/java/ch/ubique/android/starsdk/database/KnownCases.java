/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */
package ch.ubique.android.starsdk.database;

interface KnownCases {

	String TABLE_NAME = "known_cases";

	String ID = "id";
	String ONSET = "onset";
	String BUCKET_DAY = "day";
	String KEY = "key";

	String[] PROJECTION = {
			ID,
			ONSET,
			BUCKET_DAY,
			KEY,
	};

	static String create() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY NOT NULL, " + ONSET +
				" INTEGER NOT NULL," + BUCKET_DAY + " INTEGER NOT NULL, " + KEY + " BLOB NOT NULL)";
	}

	static String drop() {
		return "DROP TABLE IF EXISTS " + TABLE_NAME;
	}

}
