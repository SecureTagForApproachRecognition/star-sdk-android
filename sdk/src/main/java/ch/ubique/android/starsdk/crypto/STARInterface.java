/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk.crypto;

public interface STARInterface {
	boolean init();

	byte[] newTOTP();

	boolean validate(byte[] key, byte[] star);

	String getSecretKeyForBackend();

	void reset();

}
