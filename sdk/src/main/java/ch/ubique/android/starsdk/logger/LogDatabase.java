package ch.ubique.android.starsdk.logger;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

class LogDatabase {

	private final LogDatabaseHelper dbHelper;

	LogDatabase(Context context) {
		dbHelper = new LogDatabaseHelper(context);
	}

	void log(String level, String tag, String message, long time) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LogEntry.COLUMN_NAME_LEVEL, level);
		values.put(LogEntry.COLUMN_NAME_TAG, tag);
		values.put(LogEntry.COLUMN_NAME_MESSAGE, message);
		values.put(LogEntry.COLUMN_NAME_TIME, time);
		db.insert(LogEntry.TABLE_NAME, null, values);
	}


	private static class LogEntry implements BaseColumns {
		static final String TABLE_NAME = "log";
		static final String INDEX_NAME_LEVEL = "i_lvl";
		static final String INDEX_NAME_TAG = "i_tag";
		static final String COLUMN_NAME_LEVEL = "lvl";
		static final String COLUMN_NAME_TAG = "tag";
		static final String COLUMN_NAME_MESSAGE = "msg";
		static final String COLUMN_NAME_TIME = "time";
	}


	private static class LogDatabaseHelper extends SQLiteOpenHelper {

		private static final int DATABASE_VERSION = 1;
		private static final String DATABASE_NAME = "log.db";

		private static final String SQL_CREATE_ENTRIES =
				"CREATE TABLE " + LogEntry.TABLE_NAME + " (" +
						LogEntry._ID + " INTEGER PRIMARY KEY," +
						LogEntry.COLUMN_NAME_LEVEL + " TEXT NOT NULL," +
						LogEntry.COLUMN_NAME_TAG + " TEXT NOT NULL," +
						LogEntry.COLUMN_NAME_MESSAGE + " TEXT NOT NULL," +
						LogEntry.COLUMN_NAME_TIME + " INTEGER NOT NULL)";

		private static final String SQL_CREATE_INDEX_LEVEL =
				"CREATE INDEX " + LogEntry.INDEX_NAME_LEVEL + " ON " + LogEntry.TABLE_NAME + "(" + LogEntry.COLUMN_NAME_LEVEL +
						")";
		private static final String SQL_CREATE_INDEX_TAG =
				"CREATE INDEX " + LogEntry.INDEX_NAME_TAG + " ON " + LogEntry.TABLE_NAME + "(" + LogEntry.COLUMN_NAME_TAG + ")";


		LogDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_ENTRIES);
			db.execSQL(SQL_CREATE_INDEX_LEVEL);
			db.execSQL(SQL_CREATE_INDEX_TAG);
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// nothing yet
		}

	}

}
