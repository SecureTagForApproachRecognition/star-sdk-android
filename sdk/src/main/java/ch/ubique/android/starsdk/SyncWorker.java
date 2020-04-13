/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */
package ch.ubique.android.starsdk;

import android.content.Context;
import android.util.Base64;
import androidx.annotation.NonNull;
import androidx.work.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ch.ubique.android.starsdk.backend.BackendRepository;
import ch.ubique.android.starsdk.backend.ResponseException;
import ch.ubique.android.starsdk.backend.models.Application;
import ch.ubique.android.starsdk.backend.models.ExposedList;
import ch.ubique.android.starsdk.backend.models.Exposee;
import ch.ubique.android.starsdk.database.Database;
import ch.ubique.android.starsdk.util.DayDate;

import static ch.ubique.android.starsdk.util.Base64Util.fromBase64;

public class SyncWorker extends Worker {

	private static final String TAG = "ch.ubique.android.starsdk.SyncWorker";

	public static void startSyncWorker(Context context) {
		Constraints constraints = new Constraints.Builder()
				.setRequiredNetworkType(NetworkType.CONNECTED)
				.build();

		PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(SyncWorker.class, 15, TimeUnit.MINUTES)
				.setConstraints(constraints)
				.build();

		WorkManager workManager = WorkManager.getInstance(context);
		workManager.enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
	}

	public static void stopSyncWorker(Context context) {
		WorkManager workManager = WorkManager.getInstance(context);
		workManager.cancelAllWorkByTag(TAG);
	}

	public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
		super(context, workerParams);
	}

	@NonNull
	@Override
	public Result doWork() {
		long scanInterval = AppConfigManager.getInstance(getApplicationContext()).getScanInterval();
		TracingService.scheduleNextRun(getApplicationContext(), scanInterval);

		try {
			doSync(getApplicationContext());
			AppConfigManager.getInstance(getApplicationContext()).setLastSyncNetworkSuccess(true);
		} catch (IOException | ResponseException e) {
			AppConfigManager.getInstance(getApplicationContext()).setLastSyncNetworkSuccess(false);
			return Result.retry();
		}

		return Result.success();
	}

	public static void doSync(Context context) throws IOException, ResponseException {
		AppConfigManager appConfigManager = AppConfigManager.getInstance(context);
		appConfigManager.loadSynchronous();
		Application appConfig = appConfigManager.getAppConfig();

		Database database = new Database(context);
		database.generateContactsFromHandshakes();

		BackendRepository backendRepository =
				new BackendRepository(context, appConfig.getBackendBaseUrl(), appConfig.getListBaseUrl());

		DayDate dateToLoad = new DayDate();
		dateToLoad = dateToLoad.subtractDays(14);

		for (int i = 0; i <= 14; i++) {

			ExposedList exposedList = backendRepository.getExposees(dateToLoad);
			for (Exposee exposee : exposedList.getExposed()) {
				database.addKnownCase(
						context,
						exposee.getKey(),
						exposee.getOnset(),
						dateToLoad
				);
			}

			dateToLoad = dateToLoad.getNextDay();
		}

		appConfigManager.setLastSyncDate(System.currentTimeMillis());
	}

}
