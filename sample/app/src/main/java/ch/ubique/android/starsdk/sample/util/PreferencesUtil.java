package ch.ubique.android.starsdk.sample.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtil {

	private static final String PREFS_STAR_SDK_SAMPLE = "preferences_star_sdk_sample";
	private static final String PREF_KEY_EXPOSED_NOTIFICATION = "pref_key_exposed_notification";

	public static boolean isExposedNotificationShown(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_STAR_SDK_SAMPLE, Context.MODE_PRIVATE);
		return prefs.getBoolean(PREF_KEY_EXPOSED_NOTIFICATION, false);
	}

	public static void setExposedNotificationShown(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_STAR_SDK_SAMPLE, Context.MODE_PRIVATE);
		prefs.edit().putBoolean(PREF_KEY_EXPOSED_NOTIFICATION, true).apply();
	}

}
