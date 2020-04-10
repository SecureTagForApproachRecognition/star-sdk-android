/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */
package ch.ubique.android.starsdk;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import ch.ubique.android.starsdk.backend.CallbackListener;
import ch.ubique.android.starsdk.backend.ResponseException;
import ch.ubique.android.starsdk.backend.models.ExposeeAuthData;
import ch.ubique.android.starsdk.backend.models.ExposeeRequest;
import ch.ubique.android.starsdk.crypto.STARModule;
import ch.ubique.android.starsdk.database.Database;
import ch.ubique.android.starsdk.logger.Logger;
import ch.ubique.android.starsdk.util.ProcessUtil;

public class STARTracing {

	public static final String UPDATE_INTENT_ACTION = "ch.ubique.android.starsdk.UPDATE_ACTION";

	private static String appId;

	public static void init(Context context, String appId) {
		init(context, appId, false);
	}

	public static void init(Context context, String appId, boolean enableDevMode) {
		if (ProcessUtil.isMainProcess(context)) {
			STARTracing.appId = appId;
			AppConfigManager appConfigManager = AppConfigManager.getInstance(context);
			appConfigManager.setAppId(appId);
			appConfigManager.setDevModeEnabled(enableDevMode);
			appConfigManager.triggerLoad();

			STARModule.getInstance(context).init();

			boolean advertising = appConfigManager.isAdvertisingEnabled();
			boolean receiving = appConfigManager.isReceivingEnabled();
			if (advertising || receiving) {
				start(context, advertising, receiving);
			}
		}
	}

	private static void checkInit() {
		if (appId == null) {
			throw new IllegalStateException("You have to call STARTracing.init() in your application onCreate()");
		}
	}

	public static void start(Context context) {
		start(context, true, true);
	}

	public static void start(Context context, boolean advertise, boolean receive) {
		checkInit();
		AppConfigManager appConfigManager = AppConfigManager.getInstance(context);
		appConfigManager.setAdvertisingEnabled(advertise);
		appConfigManager.setReceivingEnabled(receive);
		long scanInterval = appConfigManager.getScanInterval();
		long scanDuration = appConfigManager.getScanDuration();
		Intent intent = new Intent(context, TracingService.class).setAction(TracingService.ACTION_START);
		intent.putExtra(TracingService.EXTRA_ADVERTISE, advertise);
		intent.putExtra(TracingService.EXTRA_RECEIVE, receive);
		intent.putExtra(TracingService.EXTRA_SCAN_INTERVAL, scanInterval);
		intent.putExtra(TracingService.EXTRA_SCAN_DURATION, scanDuration);
		ContextCompat.startForegroundService(context, intent);
		SyncWorker.startSyncWorker(context);
	}

	public static boolean isStarted(Context context) {
		checkInit();
		AppConfigManager appConfigManager = AppConfigManager.getInstance(context);
		return appConfigManager.isAdvertisingEnabled() || appConfigManager.isReceivingEnabled();
	}

	public static void sync(Context context) {
		checkInit();
		try {
			SyncWorker.doSync(context);
			AppConfigManager.getInstance(context).setLastSyncNetworkSuccess(true);
		} catch (IOException | ResponseException e) {
			e.printStackTrace();
			AppConfigManager.getInstance(context).setLastSyncNetworkSuccess(false);
		}
	}

	public static TracingStatus getStatus(Context context) {
		checkInit();
		Database database = new Database(context);
		AppConfigManager appConfigManager = AppConfigManager.getInstance(context);
		ArrayList<TracingStatus.ErrorState> errorStates = checkTracingStatus(context);
		return new TracingStatus(
				database.getHandshakes().size(),
				appConfigManager.isAdvertisingEnabled(),
				appConfigManager.isReceivingEnabled(),
				database.wasContactExposed(),
				appConfigManager.getLastSyncDate(),
				appConfigManager.getAmIExposed(),
				errorStates
		);
	}

	private static ArrayList<TracingStatus.ErrorState> checkTracingStatus(Context context) {
		ArrayList<TracingStatus.ErrorState> errors = new ArrayList<>();

		final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!bluetoothAdapter.isEnabled()) {
			errors.add(TracingStatus.ErrorState.BLE_DISABLED);
		}

		PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		boolean batteryOptimizationsDeactivated = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
		if (!batteryOptimizationsDeactivated) {
			errors.add(TracingStatus.ErrorState.BATTERY_OPTIMIZER_ENABLED);
		}

		boolean locationPermissionGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
				PackageManager.PERMISSION_GRANTED;
		if (!locationPermissionGranted) {
			errors.add(TracingStatus.ErrorState.MISSING_LOCATION_PERMISSION);
		}

		if (!AppConfigManager.getInstance(context).getLastSyncNetworkSuccess()) {
			errors.add(TracingStatus.ErrorState.NETWORK_ERROR_WHILE_SYNCING);
		}

		return errors;
	}

	public static void sendIWasExposed(Context context, Date date, ExposeeAuthData exposeeAuthData,
			CallbackListener<Void> callback) {
		checkInit();
		AppConfigManager appConfigManager = AppConfigManager.getInstance(context);
		appConfigManager.setAmIExposed(true);
		appConfigManager.getBackendRepository(context)
				.addExposee(new ExposeeRequest(STARModule.getInstance(context).getSecretKeyForBackend(date),
								date,
								exposeeAuthData),
						callback);
	}

	public static void stop(Context context) {
		checkInit();

		AppConfigManager appConfigManager = AppConfigManager.getInstance(context);
		appConfigManager.setAdvertisingEnabled(false);
		appConfigManager.setReceivingEnabled(false);

		Intent intent = new Intent(context, TracingService.class).setAction(TracingService.ACTION_STOP);
		context.stopService(intent);
		SyncWorker.stopSyncWorker(context);
	}

	public static IntentFilter getUpdateIntentFilter() {
		IntentFilter intentFilter = new IntentFilter(STARTracing.UPDATE_INTENT_ACTION);
		return intentFilter;
	}

	public static boolean isDevModeEnabled(Context context) {
		checkInit();
		return AppConfigManager.getInstance(context).isDevModeEnabled();
	}

	public static void setCalibrationTestDeviceName(Context context, String name) {
		checkInit();
		AppConfigManager.getInstance(context).setCalibrationTestDeviceName(name);
	}

	public static String getCalibrationTestDeviceName(Context context) {
		checkInit();
		return AppConfigManager.getInstance(context).getCalibrationTestDeviceName();
	}

	public static void disableCalibrationTestDeviceName(Context context) {
		checkInit();
		AppConfigManager.getInstance(context).setCalibrationTestDeviceName(null);
	}

	public static void clearData(Context context, Runnable onDeleteListener) {
		checkInit();
		AppConfigManager appConfigManager = AppConfigManager.getInstance(context);
		if (appConfigManager.isAdvertisingEnabled() || appConfigManager.isReceivingEnabled()) {
			throw new IllegalStateException("Tracking must be stopped for clearing the local data");
		}

		STARModule.getInstance(context).reset();
		appConfigManager.clearPreferences();
		Logger.clear();
		Database db = new Database(context);
		db.recreateTables(response -> onDeleteListener.run());
	}

	public static void exportDb(Context context, OutputStream targetOut, Runnable onExportedListener) {
		Database db = new Database(context);
		db.exportTo(context, targetOut, response -> onExportedListener.run());
	}

}
