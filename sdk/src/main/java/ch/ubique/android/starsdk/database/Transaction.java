/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk.database;

import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;

public class Transaction implements Runnable {

	private SQLiteDatabase db;
	private String[] queries;

	Transaction(@NonNull SQLiteDatabase db, String... queries) {
		this.db = db;
		this.queries = queries;
	}

	@Override
	public void run() {
		db.beginTransaction();
		try {
			for (String query : queries) {
				db.execSQL(query);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// anything
		} finally {
			db.endTransaction();
		}
	}

}
