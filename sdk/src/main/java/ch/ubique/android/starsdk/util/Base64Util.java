package ch.ubique.android.starsdk.util;

import android.util.Base64;

public class Base64Util {

	public static String toBase64(byte[] data) {
		return new String(Base64.encode(data, Base64.NO_WRAP));
	}

	public static byte[] fromBase64(String data) {
		return Base64.decode(data, Base64.NO_WRAP);
	}

}
