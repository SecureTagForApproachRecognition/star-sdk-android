/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */
package ch.ubique.android.starsdk.gatt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ubique.android.starsdk.BroadcastHelper;
import ch.ubique.android.starsdk.TracingService;
import ch.ubique.android.starsdk.util.LogHelper;

public class BleClient {

	private final Context context;
	private BluetoothLeScanner bleScanner;
	private ScanCallback bleScanCallback;
	private GattConnectionThread gattConnectionThread;

	public BleClient(Context context) {
		this.context = context;
		gattConnectionThread = new GattConnectionThread();
		gattConnectionThread.start();
	}

	public void start() {
		final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!bluetoothAdapter.isEnabled()) {
			BroadcastHelper.sendUpdateBroadcast(context);
			return;
		}
		bleScanner = bluetoothAdapter.getBluetoothLeScanner();

		List<ScanFilter> scanFilters = new ArrayList<>();
		scanFilters.add(new ScanFilter.Builder()
				.setServiceUuid(new ParcelUuid(BleServer.SERVICE_UUID))
				.build());

		// Scan for Apple devices as iOS does not advertise service uuid when in background,
		// but instead pushes it to the "overflow" area (manufacturer data). For now let's
		// connect to all Apple devices until we find the algorithm used to put the service uuid
		// into the manufacturer data
		scanFilters.add(new ScanFilter.Builder()
				.setManufacturerData(0x004c, new byte[0])
				.build());

		ScanSettings scanSettings = new ScanSettings.Builder()
				.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
				.build();

		bleScanCallback = new ScanCallback() {
			public void onScanResult(int callbackType, ScanResult result) {
				if (result.getScanRecord() != null) {
					onDeviceFound(result);
				}
			}

			@Override
			public void onBatchScanResults(List<ScanResult> results) {
				LogHelper.append("Batch size " + results.size());
				for (ScanResult result : results) {
					onScanResult(0, result);
				}
			}

			public void onScanFailed(int errorCode) {
				Log.e("ScanCallback", "error: " + errorCode);
			}
		};

		bleScanner.startScan(scanFilters, scanSettings, bleScanCallback);
		LogHelper.append("bleScanner started");
	}

	public static final long MINIMUM_TIME_TO_RECONNECT_TO_SAME_DEVICE = TracingService.SCAN_INTERVAL;
	public static final Map<String, Long> deviceLastConnected = new HashMap<>();

	public void onDeviceFound(ScanResult scanResult) {
		try {
			BluetoothDevice bluetoothDevice = scanResult.getDevice();
			//LogHelper.append("scanned: " + bluetoothDevice.getAddress());
			Log.d("BleClient", bluetoothDevice.getAddress() + "; " + scanResult.getScanRecord().getDeviceName());

			if (deviceLastConnected.get(bluetoothDevice.getAddress()) != null &&
					deviceLastConnected.get(bluetoothDevice.getAddress()) > System.currentTimeMillis() -
							MINIMUM_TIME_TO_RECONNECT_TO_SAME_DEVICE) {
				Log.d("BleClient", "skipped");
				return;
			}

			int power = scanResult.getScanRecord().getTxPowerLevel();
			if (power == Integer.MIN_VALUE) {
				LogHelper.append("No power levels found for (" + scanResult.getDevice().getName() + "), use default of 12dbm");
				power = 12;
			}
			double distance = calculateDistance(power, scanResult.getRssi());
			LogHelper.append("Distance to device (" + scanResult.getDevice().getAddress() + "): " + String.valueOf(distance) +
					"m");

			deviceLastConnected.put(bluetoothDevice.getAddress(), System.currentTimeMillis());

			gattConnectionThread.addTask(new GattConnectionTask(context, bluetoothDevice, scanResult));
		} catch (Throwable t) {
			t.printStackTrace();
			LogHelper.append(t);
		}
	}

	private static double calculateDistance(int txPower, int rssi) {
		return Math.pow(10, (txPower - rssi) / 20.0) / 1000.0;
	}

	public synchronized void stopScan() {
		if (bleScanner == null) {
			final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			bleScanner = bluetoothAdapter.getBluetoothLeScanner();
		}
		LogHelper.append("bleScanner stopped");
		Log.d("BleClient", "stopping BLE scanner");
		bleScanner.stopScan(bleScanCallback);
		bleScanner = null;
	}

	public synchronized void stop() {
		gattConnectionThread.terminate();
		stopScan();
	}

}
