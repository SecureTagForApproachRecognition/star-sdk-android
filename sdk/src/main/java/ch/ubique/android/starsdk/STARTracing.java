/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.core.content.ContextCompat;

import java.io.IOException;

import ch.ubique.android.starsdk.backend.CallbackListener;
import ch.ubique.android.starsdk.backend.ResponseException;
import ch.ubique.android.starsdk.backend.models.Exposee;
import ch.ubique.android.starsdk.crypto.STARModule;
import ch.ubique.android.starsdk.database.Database;
import ch.ubique.android.starsdk.util.ProcessUtil;

public class STARTracing {

	public static final String UPDATE_INTENT_ACTION = "ch.ubique.android.starsdk.UPDATE_ACTION";

	private static String appId;

	public static void init(Context context, String appId) {
		if (ProcessUtil.isMainProcess(context)) {
			STARTracing.appId = appId;
			AppConfigManager appConfigManager = AppConfigManager.getInstance(context);
			appConfigManager.setAppId(appId);
			appConfigManager.triggerLoad();

			STARModule.getInstance(context).init();

			if (appConfigManager.isTracingEnabled()) {
				start(context);
			}
		}
	}

	private static void checkInit() {
		if (appId == null) {
			throw new IllegalStateException("You have to call STARTracing.init() in your application onCreate()");
		}
	}

	public static void start(Context context) {
		checkInit();

		AppConfigManager.getInstance(context).setTracingEnabled(true);

		Intent intent = new Intent(context, TracingService.class).setAction(TracingService.ACTION_START);
		ContextCompat.startForegroundService(context, intent);
		SyncWorker.startSyncWorker(context);
	}

	public static boolean isStarted(Context context) {
		checkInit();
		return AppConfigManager.getInstance(context).isTracingEnabled();
	}

	public static void sync(Context context) throws IOException, ResponseException {
		checkInit();
		SyncWorker.doSync(context);
	}

	public static TracingStatus getStatus(Context context) {
		checkInit();
		Database database = new Database(context);
		AppConfigManager appConfigManager = AppConfigManager.getInstance(context);
		TracingStatus.ErrorState errorState = null;
		final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!bluetoothAdapter.isEnabled()) {
			errorState = TracingStatus.ErrorState.BLE_DISABLED;
		}
		return new TracingStatus(
				database.getHandshakes().size(),
				appConfigManager.isTracingEnabled(),
				database.wasContactExposed(),
				appConfigManager.getLastSyncDate(),
				appConfigManager.getAmIExposed(),
				errorState
		);
	}

	public static void sendIWasExposed(Context context, Object customData, CallbackListener<Void> callback) {
		checkInit();
		AppConfigManager appConfigManager = AppConfigManager.getInstance(context);
		appConfigManager.setAmIExposed(true);
		appConfigManager.getBackendRepository(context)
				.addExposee(new Exposee(STARModule.getInstance(context).getSecretKeyForBackend()), callback);
	}

	public static void sendIWasHealed(Context context, Object customData, CallbackListener<Void> callback) {
		checkInit();
		AppConfigManager appConfigManager = AppConfigManager.getInstance(context);
		appConfigManager.setAmIExposed(false);
		appConfigManager.getBackendRepository(context)
				.removeExposee(new Exposee(STARModule.getInstance(context).getSecretKeyForBackend()), callback);
		STARModule.getInstance(context).reset();
	}

	public static void stop(Context context) {
		checkInit();

		AppConfigManager.getInstance(context).setTracingEnabled(false);

		Intent intent = new Intent(context, TracingService.class).setAction(TracingService.ACTION_STOP);
		context.stopService(intent);
		SyncWorker.stopSyncWorker(context);
	}

	public static void reset(Context context) {
		checkInit();
		//TODO clear all data
		STARModule.getInstance(context).reset();
	}

	public static IntentFilter getUpdateIntentFilter() {
		IntentFilter intentFilter = new IntentFilter(STARTracing.UPDATE_INTENT_ACTION);
		return intentFilter;
	}

}
