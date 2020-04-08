/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.NonNull;

class DatabaseOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "star_sdk.db";

	private static DatabaseOpenHelper instance;

	static DatabaseOpenHelper getInstance(@NonNull Context context) {
		if (instance == null) {
			instance = new DatabaseOpenHelper(context);
		}
		return instance;
	}

	private DatabaseOpenHelper(@NonNull Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		recreateTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//empty for now
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db); //do as we create a new table
	}

	public void recreateTables(@NonNull SQLiteDatabase db) {
		new Transaction(db,
				KnownCases.drop(),
				HandShakes.drop(),
				KnownCases.create(),
				HandShakes.create()
		).run();

	}

}
