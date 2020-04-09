/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk.crypto;

import java.util.Date;

public interface STARInterface {
	boolean init();

	byte[] getCurrentEphId();

	boolean isKeyMatchingEphId(byte[] key, byte[] ephId);

	String getSecretKeyForBackend(Date date);

	void reset();

}
