/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ch.ubique.android.starsdk.BroadcastHelper;
import ch.ubique.android.starsdk.crypto.STARModule;
import ch.ubique.android.starsdk.database.models.HandShake;
import ch.ubique.android.starsdk.database.models.KnownCase;

public class Database {

	private DatabaseOpenHelper databaseOpenHelper;
	private DatabaseThread databaseThread;

	public Database(@NonNull Context context) {
		databaseOpenHelper = DatabaseOpenHelper.getInstance(context);
		databaseThread = DatabaseThread.getInstance(context);
	}

	private String[] asArray(String... columns) {
		return columns;
	}

	public void addKnownCase(Context context, @NonNull byte[] key, @NonNull String day) {
		SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KnownCases.KEY, key);
		values.put(KnownCases.DAY, day);
		databaseThread.post(() -> {

			long idOfAddedCase = db.insert(KnownCases.TABLE_NAME, null, values);
			ContentValues updateValues = new ContentValues();
			updateValues.put(HandShakes.ASSOCIATED_KNOWN_CASE, idOfAddedCase);

			STARModule starModule = STARModule.getInstance(context);
			Cursor cursor = db
					.query(HandShakes.TABLE_NAME, HandShakes.PROJECTION, HandShakes.ASSOCIATED_KNOWN_CASE + " IS NULL", null, null,
							null, HandShakes.ID);
			while (cursor.moveToNext()) {
				byte[] star = cursor.getBlob(cursor.getColumnIndexOrThrow(HandShakes.STAR));
				if (starModule.validate(key, star)) {
					int id = cursor.getInt(cursor.getColumnIndexOrThrow(HandShakes.ID));
					db.update(HandShakes.TABLE_NAME, updateValues, HandShakes.ID + "=" + id, null);
					BroadcastHelper.sendUpdateBroadcast(context);
				}
			}
		});
	}

	public void getKnownCases(@NonNull ResultListener<List<KnownCase>> resultListener) {
		SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
		databaseThread.post(new Runnable() {
			List<KnownCase> knownCases = new ArrayList<>();

			@Override
			public void run() {
				try (Cursor cursor = db
						.query(KnownCases.TABLE_NAME, KnownCases.PROJECTION, null, null, null, null,
								KnownCases.ID)) {
					while (cursor.moveToNext()) {
						int id = cursor.getInt(cursor.getColumnIndexOrThrow(KnownCases.ID));
						byte[] key = cursor.getBlob(cursor.getColumnIndexOrThrow(KnownCases.KEY));
						String day = cursor.getString(cursor.getColumnIndexOrThrow(KnownCases.DAY));
						KnownCase knownCase = new KnownCase(id, day, key);
						knownCases.add(knownCase);
					}
				}

				databaseThread.onResult(() -> resultListener.onResult(knownCases));
			}
		});
	}

	public void addHandshake(Context context, byte[] star, long timestamp) {
		SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(HandShakes.STAR, star);
		values.put(HandShakes.TIMESTAMP, timestamp);
		STARModule starModule = STARModule.getInstance(context);
		getKnownCases(response -> {
			databaseThread.post(() -> {
				for (KnownCase knownCase : response) {
					if (starModule.validate(knownCase.getKey(), star)) {
						values.put(HandShakes.ASSOCIATED_KNOWN_CASE, knownCase.getId());
						break;
					}
				}
				db.insert(HandShakes.TABLE_NAME, null, values);
				BroadcastHelper.sendUpdateBroadcast(context);
			});
		});
	}

	public List<HandShake> getHandshakes() {
		SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
		List<HandShake> handShakes = new ArrayList<>();
		try (Cursor cursor = db
				.query(HandShakes.TABLE_NAME, HandShakes.PROJECTION, null, null, null, null, HandShakes.ID)) {
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndexOrThrow(HandShakes.ID));
				long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(HandShakes.TIMESTAMP));
				byte[] star = cursor.getBlob(cursor.getColumnIndexOrThrow(HandShakes.STAR));
				int associatedKnownCase = cursor.getInt(cursor.getColumnIndexOrThrow(HandShakes.ASSOCIATED_KNOWN_CASE));
				HandShake handShake = new HandShake(id, timestamp, star, associatedKnownCase);
				handShakes.add(handShake);
			}
		}
		return handShakes;
	}

	public void getHandshakes(@NonNull ResultListener<List<HandShake>> resultListener) {
		databaseThread.post(new Runnable() {
			List<HandShake> handShakes = new ArrayList<>();

			@Override
			public void run() {
				handShakes = getHandshakes();
				databaseThread.onResult(() -> resultListener.onResult(handShakes));
			}
		});
	}

	public boolean wasContactExposed() {
		for (HandShake handshake : getHandshakes()) {
			if (handshake.getAssociatedKnownCase() != 0) {
				return true;
			}
		}
		return false;
	}

}
