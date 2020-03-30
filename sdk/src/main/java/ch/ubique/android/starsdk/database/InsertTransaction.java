/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;

public class InsertTransaction implements Runnable {

	private SQLiteDatabase db;
	private String tableName;
	private ContentValues values;

	InsertTransaction(@NonNull SQLiteDatabase db, @NonNull String tableName, @NonNull ContentValues values) {
		this.db = db;
		this.tableName = tableName;
		this.values = values;
	}

	@Override
	public void run() {
		db.beginTransaction();
		try {
			db.insert(tableName, null, values);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// anything
		} finally {
			db.endTransaction();
		}
	}

}
