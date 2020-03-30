/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk;

import android.content.Context;
import android.content.Intent;

public class BroadcastHelper {

	public static void sendUpdateBroadcast(Context context) {
		Intent intent = new Intent();
		intent.setAction(STARTracing.UPDATE_INTENT_ACTION);
		context.sendBroadcast(intent);
	}

}
