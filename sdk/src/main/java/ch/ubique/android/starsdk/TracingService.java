/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import ch.ubique.android.starsdk.gatt.BleClient;
import ch.ubique.android.starsdk.gatt.BleServer;
import ch.ubique.android.starsdk.logger.Logger;

public class TracingService extends Service {

	private static final String TAG = "TracingService";

	public static final String ACTION_START = TracingService.class.getCanonicalName() + ".ACTION_START";
	public static final String ACTION_STOP = TracingService.class.getCanonicalName() + ".ACTION_STOP";

	public static final String EXTRA_ADVERTISE = TracingService.class.getCanonicalName() + ".EXTRA_ADVERTISE";
	public static final String EXTRA_RECEIVE = TracingService.class.getCanonicalName() + ".EXTRA_RECEIVE";
	public static final String EXTRA_SCAN_INTERVAL = TracingService.class.getCanonicalName() + ".EXTRA_SCAN_INTERVAL";
	public static final String EXTRA_SCAN_DURATION = TracingService.class.getCanonicalName() + ".EXTRA_SCAN_DURATION";

	private static String NOTIFICATION_CHANNEL_ID = "star_tracing_service";
	private static int NOTIFICATION_ID = 1827;
	private Handler handler;

	private PowerManager.WakeLock wl;

	private BleServer bleServer;
	private BleClient bleClient;

	private boolean startAdvertising;
	private boolean startReceiveing;
	private long scanInterval;
	private long scanDuration;

	public TracingService() { }

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null || intent.getAction() == null) {
			return START_STICKY;
		}

		if (wl == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					getPackageName() + ":TracingServiceWakeLock");
			wl.acquire();
		}

		Logger.i(TAG, "service started");
		Log.d(TAG, "onHandleIntent() with " + intent.getAction());

		scanInterval = intent.getLongExtra(EXTRA_SCAN_INTERVAL, 5 * 60 * 1000);
		scanDuration = intent.getLongExtra(EXTRA_SCAN_DURATION, 30 * 1000);

		startAdvertising = intent.getBooleanExtra(EXTRA_ADVERTISE, true);
		startReceiveing = intent.getBooleanExtra(EXTRA_RECEIVE, true);

		if (ACTION_START.equals(intent.getAction())) {
			startForeground(NOTIFICATION_ID, createForegroundNotification());
			start();
		} else if (ACTION_STOP.equals(intent.getAction())) {
			stopForegroundService();
		}

		return START_STICKY;
	}

	private Notification createForegroundNotification() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			createNotificationChannel();
		}

		Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
		PendingIntent contentIntent = null;
		if (launchIntent != null) {
			contentIntent = PendingIntent.getActivity(this, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		}

		return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
				.setOngoing(true)
				.setContentTitle(getString(R.string.star_sdk_service_notification_title))
				.setContentText(getString(R.string.star_sdk_service_notification_text))
				.setPriority(NotificationCompat.PRIORITY_LOW)
				.setSmallIcon(R.drawable.ic_begegnungen)
				.setContentIntent(contentIntent)
				.build();
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	private void createNotificationChannel() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		String channelName = getString(R.string.star_sdk_service_notification_channel);
		NotificationChannel channel =
				new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW);
		channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
		notificationManager.createNotificationChannel(channel);
	}

	private void start() {
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
		}
		handler = new Handler();

		startTracing();
	}

	private void startTracing() {
		Log.d(TAG, "startTracing()");

		try {
			Notification notification = createForegroundNotification();

			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(NOTIFICATION_ID, notification);

			startClient();
			startServer();
		} catch (Throwable t) {
			t.printStackTrace();
			Logger.e(TAG, t);
		}

		handler.postDelayed(() -> {scheduleNextRun(this, scanInterval);}, scanDuration);
	}

	public static void scheduleNextRun(Context context, long scanInterval) {
		long now = System.currentTimeMillis();
		long delay = scanInterval - (now % scanInterval);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, TracingServiceBroadcastReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, now + delay, pendingIntent);
	}

	private void stopForegroundService() {
		stopClient();
		stopServer();
		stopForeground(true);
		wl.release();
		stopSelf();
	}

	@Override
	public void onDestroy() {
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
		}
		Log.d(TAG, "onDestroy()");
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void startServer() {
		stopServer();
		if (startAdvertising) {
			bleServer = new BleServer(this);
			bleServer.start();
			bleServer.startAdvertising();
		}
	}

	private void stopServer() {
		if (bleServer != null) {
			bleServer.stop();
			bleServer = null;
		}
	}

	private void startClient() {
		stopClient();
		if (startReceiveing) {
			bleClient = new BleClient(this);
			bleClient.setMinTimeToReconnectToSameDevice(scanInterval);
			bleClient.start();
		}
	}

	private void stopClient() {
		if (bleClient != null) {
			bleClient.stop();
			bleClient = null;
		}
	}

}
