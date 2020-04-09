/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import androidx.core.content.ContextCompat;

public class TracingServiceBroadcastReceiver extends BroadcastReceiver {

	PowerManager.WakeLock screenWakeLock;

	@Override
	public void onReceive(Context context, Intent i) {
		Log.d("TracingServiceBdcRecv", "received broadcast to start service");
		AppConfigManager appConfigManager = AppConfigManager.getInstance(context);
		boolean advertising = appConfigManager.isAdvertisingEnabled();
		boolean receiving = appConfigManager.isReceivingEnabled();
		long scanInterval = appConfigManager.getScanInterval();
		long scanDuration = appConfigManager.getScanDuration();
		if (advertising || receiving) {
			Intent intent = new Intent(context, TracingService.class).setAction(TracingService.ACTION_START);
			intent.putExtra(TracingService.EXTRA_ADVERTISE, advertising);
			intent.putExtra(TracingService.EXTRA_RECEIVE, receiving);
			intent.putExtra(TracingService.EXTRA_SCAN_INTERVAL, scanInterval);
			intent.putExtra(TracingService.EXTRA_SCAN_DURATION, scanDuration);
			ContextCompat.startForegroundService(context, intent);
		}
	}

}
