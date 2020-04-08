/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */
package ch.ubique.android.starsdk.gatt;

import android.bluetooth.*;
import android.bluetooth.le.*;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.ParcelUuid;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import ch.ubique.android.starsdk.BroadcastHelper;
import ch.ubique.android.starsdk.TracingService;
import ch.ubique.android.starsdk.database.Database;
import ch.ubique.android.starsdk.util.LogHelper;

public class BleClient {

	private final Context context;
	HandlerThread gattConnectHandlerThread;
	Handler gattConnectHandler;
	private BluetoothLeScanner bleScanner;
	private ScanCallback bleScanCallback;
	private BluetoothGatt bluetoothGatt;
	private LinkedBlockingQueue<Pair<BluetoothDevice, ScanResult>> bluetoothDevicesToConnect = new LinkedBlockingQueue<>();

	private Runnable closeGattAndConnectToNextDevice = new Runnable() {
		@Override
		public void run() {
			gattConnectHandler.removeCallbacks(closeGattAndConnectToNextDevice);
			if (bluetoothGatt != null) {
				Log.d("BleClient", "closeGattAndConnectToNextDevice (disconnect() and then close())");
				LogHelper.append("Calling disconnect() and close(): " + bluetoothGatt.getDevice().getAddress());
				// Order matters! Call disconnect() before close() as the latter de-registers our client
				// and essentially makes disconnect a NOP.
				bluetoothGatt.disconnect();
				bluetoothGatt.close();
				bluetoothGatt = null;
			}
			LogHelper.append("Reset and wait for next BLE device");
			connectGattForNextQueueElement();
		}
	};

	public BleClient(Context context) {
		this.context = context;
		gattConnectHandlerThread = new HandlerThread("GattConnectHandlerThread");
		gattConnectHandlerThread.start();
		gattConnectHandler = new Handler(gattConnectHandlerThread.getLooper());
		gattConnectHandler.post(this::connectGattForNextQueueElement);
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

			connectGatt(bluetoothDevice, scanResult);
		} catch (Throwable t) {
			t.printStackTrace();
			LogHelper.append(t);
		}
	}

	private static double calculateDistance(int txPower, int rssi) {
		return Math.pow(10, (txPower - rssi) / 20.0) / 1000.0;
	}

	public void connectGatt(BluetoothDevice bluetoothDevice, ScanResult scanResult) {
		bluetoothDevicesToConnect.add(new Pair(bluetoothDevice, scanResult));
	}

	private void connectGattForNextQueueElement() {
		Pair<BluetoothDevice, ScanResult> bluetoothDeviceScanResultPair = null;
		try {
			bluetoothDeviceScanResultPair = bluetoothDevicesToConnect.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (bluetoothDeviceScanResultPair == null) {
			gattConnectHandler.post(this::connectGattForNextQueueElement);
			return;
		}

		BluetoothDevice bluetoothDevice = bluetoothDeviceScanResultPair.first;
		ScanResult scanResult = bluetoothDeviceScanResultPair.second;

		Log.d("BleClient", "connecting GATT...");
		LogHelper.append("Trying to connect to: " + bluetoothDevice.getAddress());

		final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
			@Override
			public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
				super.onConnectionStateChange(gatt, status, newState);
				if (newState == BluetoothProfile.STATE_CONNECTING) {
					Log.d("BluetoothGattCallback", "connecting... " + status);
				} else if (newState == BluetoothProfile.STATE_CONNECTED) {
					Log.d("BluetoothGattCallback", "connected " + status);
					Log.d("BluetoothGattCallback", "requesting mtu...");
					LogHelper.append("Gatt Connection established");
					gatt.requestMtu(512);
				} else if (newState == BluetoothProfile.STATE_DISCONNECTED || newState == BluetoothProfile.STATE_DISCONNECTING) {
					Log.d("BluetoothGattCallback", "disconnected " + status);
					LogHelper.append("Gatt Connection disconnected " + status);
					closeGattAndConnectToNextDevice.run();
				}
			}

			@Override
			public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
				Log.d("BluetoothGattCallback", "discovering services...");
				gatt.discoverServices();
			}

			@Override
			public void onServicesDiscovered(BluetoothGatt gatt, int status) {
				BluetoothGattService service = gatt.getService(BleServer.SERVICE_UUID);

				if (service == null) {
					Log.e("BluetoothGattCallback", "No GATT service for " + BleServer.SERVICE_UUID + " found, status=" + status);
					LogHelper.append("Could not find our GATT service");
					closeGattAndConnectToNextDevice.run();
					return;
				}

				Log.d("BluetoothGattCallback", "Service " + service.getUuid() + " found");

				BluetoothGattCharacteristic characteristic = service.getCharacteristic(BleServer.TOTP_CHARACTERISTIC_UUID);

				boolean initiatedRead = gatt.readCharacteristic(characteristic);
				if (!initiatedRead) {
					Log.e("BluetoothGattCallback", "Failed to initiate characteristic read");
					LogHelper.append("Failed to read");
				} else {
					LogHelper.append("Read initiated");
				}
			}

			@Override
			public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
				Log.d("onCharacteristicRead", "[status:" + status + "] " + characteristic.getUuid() + ": " +
						Arrays.toString(characteristic.getValue()));

				if (characteristic.getUuid().equals(BleServer.TOTP_CHARACTERISTIC_UUID)) {
					if (status == BluetoothGatt.GATT_SUCCESS) {
						addHandshakeToDatabase(characteristic.getValue(), gatt.getDevice().getAddress(),
								scanResult.getScanRecord().getTxPowerLevel(), scanResult.getRssi());
					} else {
						Log.e("BluetoothGattCallback", "Failed to read characteristic. Status: " + status);

						// TODO error
					}
				}
				closeGattAndConnectToNextDevice.run();
				LogHelper.append("Closed Gatt Connection");
			}
		};

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			bluetoothGatt = bluetoothDevice.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE);
		} else {
			bluetoothGatt = bluetoothDevice.connectGatt(context, false, gattCallback);
		}

		gattConnectHandler.postDelayed(closeGattAndConnectToNextDevice, 10 * 1000L);
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
		gattConnectHandlerThread.quit();
		stopScan();
		if (bluetoothGatt != null) {
			bluetoothGatt.close();
			bluetoothGatt = null;
		}
	}

	public void addHandshakeToDatabase(byte[] starValue, String macAddress, int rxPowerLevel, int rssi) {
		try {
			String base64String = new String(Base64.encode(starValue, Base64.NO_WRAP));
			Log.e("received", base64String);
			new Database(context).addHandshake(context, starValue, macAddress, rxPowerLevel, rssi, System.currentTimeMillis());
			LogHelper.append(base64String);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
