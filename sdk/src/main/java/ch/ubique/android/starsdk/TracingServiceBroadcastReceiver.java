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
		if (AppConfigManager.getInstance(context).isTracingEnabled()) {
			Intent intent = new Intent(context, TracingService.class).setAction(TracingService.ACTION_START);
			ContextCompat.startForegroundService(context, intent);
		}
	}

}
