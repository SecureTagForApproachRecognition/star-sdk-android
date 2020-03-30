/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class ProcessUtil {

	public static boolean isMainProcess(Context context) {
		return context.getPackageName().equals(getProcessName(context));
	}

	private static String getProcessName(Context context) {
		int mypid = android.os.Process.myPid();
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo info : infos) {
			if (info.pid == mypid) {
				return info.processName;
			}
		}
		// may never return null
		return null;
	}

}
