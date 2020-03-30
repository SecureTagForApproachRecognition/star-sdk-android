/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk.database;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import androidx.annotation.NonNull;

class DatabaseThread extends HandlerThread {

	private static DatabaseThread instance;

	private Looper looper;
	private Handler handler;
	private Handler mainHandler;

	static DatabaseThread getInstance(@NonNull Context context) {
		if (instance == null) {
			instance = new DatabaseThread(context);
		}
		return instance;
	}

	private DatabaseThread(Context context) {
		super("DatabaseThread");
		start();

		looper = getLooper();
		handler = new Handler(looper);
		mainHandler = new Handler(context.getMainLooper());
	}

	void post(@NonNull Runnable runnable) {
		handler.post(runnable);
	}

	void onResult(@NonNull Runnable runnable) {
		mainHandler.post(runnable);
	}

}
