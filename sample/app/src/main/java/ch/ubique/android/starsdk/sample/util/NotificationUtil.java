package ch.ubique.android.starsdk.sample.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;

import ch.ubique.android.starsdk.sample.R;

public class NotificationUtil {

	private static final String NOTIFICATION_CHANNEL_ID = "star_sdk_sample_channel";
	private static final int NOTIFICATION_ID = 1;

	@RequiresApi(api = Build.VERSION_CODES.O)
	public static void createNotificationChannel(Context context) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		String channelName = context.getString(R.string.app_name);
		NotificationChannel channel =
				new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
		channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
		notificationManager.createNotificationChannel(channel);
	}

	public static void showNotification(Context context, @StringRes int title, @StringRes int message, @DrawableRes int icon) {
		Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
		PendingIntent contentIntent = null;
		if (launchIntent != null) {
			contentIntent = PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		}
		Notification notification =
				new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
						.setContentTitle(context.getString(title))
						.setContentText(context.getString(message))
						.setPriority(NotificationCompat.PRIORITY_MAX)
						.setSmallIcon(icon)
						.setContentIntent(contentIntent)
						.build();

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NOTIFICATION_ID, notification);
	}

}
