/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */
package ch.ubique.android.starsdk.crypto;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Base64;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class STARModule implements STARInterface {
	private static final String TAG = "STARModule";
	private static String KEY_ALIAS;
	private static final String KEY_ENTRY = "STAR_KEY";
	private static final int interval = 60;
	private static final int KEY_LENGTH = 26;
	private static STARModule instance;
	private KeyStore ks;
	private SharedPreferences esp;

	private byte[] debugKey = "key".getBytes();

	public static STARModule getInstance(Context context) {
		if (instance == null) {
			instance = new STARModule();
			try {
				KEY_ALIAS = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
				instance.esp = EncryptedSharedPreferences.create("star_store",
						KEY_ALIAS,
						context,
						EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
						EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return instance;
	}

	@Override
	public boolean init() {
		try {
			String stringKey = esp.getString(KEY_ENTRY, null);
			if (stringKey != null) return true; //key already exists

			KeyGenParameterSpec spec = new KeyGenParameterSpec
					.Builder(KEY_ALIAS, 0)
					.build();
			KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
			SecretKey secretKey = keyGenerator.generateKey();
			SharedPreferences.Editor editor = esp.edit();
			editor.putString(KEY_ENTRY, Base64.encodeToString(secretKey.getEncoded(), Base64.NO_WRAP));
			editor.commit();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public byte[] newTOTP() {
		try {
			Integer counter = (int) Math.floor(System.currentTimeMillis() / 1000 / interval);
			byte[] timestamp = intToByteArray(counter);
			byte[] hmac = hmac(getSecretKey(), timestamp);
			byte[] star = new byte[Math.min(KEY_LENGTH, timestamp.length + hmac.length)];

			int lenTimestamp = Math.min(KEY_LENGTH, timestamp.length);
			System.arraycopy(timestamp, 0, star, 0, lenTimestamp);
			System.arraycopy(hmac, 0, star, lenTimestamp, KEY_LENGTH - lenTimestamp);
			return star;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new byte[0];
	}

	@Override
	public boolean validate(byte[] key, byte[] star) {
		try {
			ByteBuffer buffer = ByteBuffer.wrap(star);
			buffer.order(ByteOrder.BIG_ENDIAN);
			byte[] counter = new byte[4];
			buffer.get(counter);
			byte[] hash = new byte[32];
			buffer.get(hash);

			byte[] hmac = hmac(key, counter);
			return Arrays.equals(hmac, hash);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public String getSecretKeyForBackend() {
		return esp.getString(KEY_ENTRY, null);
	}

	public byte[] getSecretKey() {
		try {
			String secretKey = esp.getString(KEY_ENTRY, null);
			if (secretKey == null) {
				return new byte[0];
			}

			return Base64.decode(secretKey, Base64.NO_WRAP);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new byte[0];
	}

	@Override
	public void reset() {
		try {
			SharedPreferences.Editor editor = esp.edit();
			editor.remove(KEY_ENTRY);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private byte[] hmac(byte[] key, byte[] msg) throws NoSuchAlgorithmException, InvalidKeyException {
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(key, "HmacSHA256"));
		mac.update(msg);
		return mac.doFinal();
	}

	private static byte[] intToByteArray(int a) {
		byte[] ret = new byte[4];
		ret[3] = (byte) (a & 0xFF);
		ret[2] = (byte) ((a >> 8) & 0xFF);
		ret[1] = (byte) ((a >> 16) & 0xFF);
		ret[0] = (byte) ((a >> 24) & 0xFF);
		return ret;
	}

	private static int byteArrayToInt(byte[] b) {
		return (b[3] & 0xFF) + ((b[2] & 0xFF) << 8) + ((b[1] & 0xFF) << 16) + ((b[0] & 0xFF) << 24);
	}

}
