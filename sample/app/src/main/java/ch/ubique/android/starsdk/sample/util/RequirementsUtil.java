package ch.ubique.android.starsdk.sample.util;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import androidx.core.content.ContextCompat;

public class RequirementsUtil {

	public static boolean isLocationPermissionGranted(Context context) {
		return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
				PackageManager.PERMISSION_GRANTED;
	}

	public static boolean isBatteryOptimizationDeactivated(Context context) {
		PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		return powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
	}

	public static boolean isBluetoothEnabled() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
			return false;
		}
		return true;
	}

}
