package ch.ubique.android.starsdk.sample;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import ch.ubique.android.starsdk.STARTracing;
import ch.ubique.android.starsdk.sample.util.NotificationUtil;
import ch.ubique.android.starsdk.sample.util.PreferencesUtil;
import ch.ubique.android.starsdk.util.ProcessUtil;

public class MainApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		if (ProcessUtil.isMainProcess(this)) {
			registerReceiver(sdkReceiver, STARTracing.getUpdateIntentFilter());
			// TODO: Register appId in discovery service (run on dev backend)
			STARTracing.init(this, "ch.ubique.android.starsdk.sample");
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationUtil.createNotificationChannel(this);
		}
	}

	@Override
	public void onTerminate() {
		if (ProcessUtil.isMainProcess(this)) {
			unregisterReceiver(sdkReceiver);
		}
		super.onTerminate();
	}

	private BroadcastReceiver sdkReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (STARTracing.getStatus(context).isWas_contact_exposed() && PreferencesUtil.isExposedNotificationShown(context)) {
				NotificationUtil.showNotification(context, R.string.push_exposed_title,
						R.string.push_exposed_text, R.drawable.ic_begegnungen);
				PreferencesUtil.setExposedNotificationShown(context);
			}
		}
	};

}