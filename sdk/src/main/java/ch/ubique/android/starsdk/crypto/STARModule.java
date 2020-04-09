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
import android.util.Base64;
import android.util.Pair;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;

import static ch.ubique.android.starsdk.crypto.TimeUtils.convertToDay;
import static ch.ubique.android.starsdk.crypto.TimeUtils.getNextDay;

public class STARModule implements STARInterface {

	public static final int KEY_LENGTH = 26;

	private static final int NUMBER_OF_DAYS_TO_KEEP_SK = 21;
	private static final int NUMBER_OF_EPOCHS_PER_DAY = 24 * 12;
	private static final int MILLISECONDS_PER_EPOCH = 24 * 60 * 60 * 1000 / NUMBER_OF_EPOCHS_PER_DAY;
	//TODO set correct broadcast key
	private static final byte[] BROADCAST_KEY = "TODOTODOTODOTODOTODOTODOTODOTODOTODO".getBytes();

	private static final String KEY_SK_LIST_JSON = "SK_LIST_JSON";
	private static STARModule instance;
	private SharedPreferences esp;

	private byte[] debugKey = "key".getBytes();

	public static STARModule getInstance(Context context) {
		if (instance == null) {
			instance = new STARModule();
			try {
				String KEY_ALIAS = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
				instance.esp = EncryptedSharedPreferences.create("star_store",
						KEY_ALIAS,
						context,
						EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
						EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
			} catch (GeneralSecurityException | IOException ex) {
				ex.printStackTrace();
			}
		}
		return instance;
	}

	@Override
	public boolean init() {
		try {
			String stringKey = esp.getString(KEY_SK_LIST_JSON, null);
			if (stringKey != null) return true; //key already exists

			KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
			SecretKey secretKey = keyGenerator.generateKey();
			SKList skList = new SKList();
			skList.add(new Pair(convertToDay(System.currentTimeMillis()), secretKey.getEncoded()));
			storeSKList(skList);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private SKList getSKList() {
		String skListJson = esp.getString(KEY_SK_LIST_JSON, null);
		return new Gson().fromJson(skListJson, SKList.class);
	}

	private void storeSKList(SKList skList) {
		esp.edit().putString(KEY_SK_LIST_JSON, new Gson().toJson(skList)).commit();
	}

	private byte[] getSKt1(byte[] SKt0) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] SKt1 = digest.digest(SKt0);
			return SKt1;
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 algorithm must be present!");
		}
	}

	private void rotateSK() {
		SKList skList = getSKList();
		long nextDay = getNextDay(skList.get(0).first);
		byte[] SKt1 = getSKt1(skList.get(0).second);
		skList.add(0, new Pair(nextDay, SKt1));
		List subList = skList.subList(0, Math.min(NUMBER_OF_DAYS_TO_KEEP_SK, skList.size()));
		skList = new SKList();
		skList.addAll(subList);
		storeSKList(skList);
	}

	private byte[] getCurrentSK(long day) {
		List<Pair<Long, byte[]>> SKList = getSKList();
		while (SKList.get(0).first < day) {
			rotateSK();
			SKList = getSKList();
		}
		assert SKList.get(0).first == day;
		return SKList.get(0).second;
	}

	private List<byte[]> createEphIds(byte[] SK) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(SK, "HmacSHA256"));
			mac.update(BROADCAST_KEY);
			byte[] keyBytes = mac.doFinal();

			byte[] emptyArray = new byte[KEY_LENGTH];

			//generate EphIDs
			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
			Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
			byte[] counter = new byte[16];
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(counter));
			ArrayList<byte[]> ephIds = new ArrayList<>();
			for (int i = 0; i < NUMBER_OF_EPOCHS_PER_DAY; i++) {
				ephIds.add(cipher.update(emptyArray));
			}
			return ephIds;
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
			throw new IllegalStateException("HmacSHA256 and AES algorithms must be present!", e);
		}
	}

	@Override
	public byte[] getCurrentEphId() {
		long now = System.currentTimeMillis();
		long currentDay = convertToDay(now);
		byte[] SK = getCurrentSK(currentDay);
		int counter = (int) (now - currentDay) / MILLISECONDS_PER_EPOCH;
		return createEphIds(SK).get(counter);
	}

	@Override
	public boolean isKeyMatchingEphId(byte[] key, byte[] ephId) {
		for (byte[] id : createEphIds(key)) {
			if (Arrays.equals(id, ephId)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getSecretKeyForBackend(Date date) {
		long day = convertToDay(date.getTime());
		for (Pair<Long, byte[]> daySKPair : getSKList()) {
			if (daySKPair.first == day) {
				return new String(Base64.encode(daySKPair.second, Base64.NO_WRAP));
			}
		}
		return null;
	}

	@Override
	public void reset() {
		try {
			SharedPreferences.Editor editor = esp.edit();
			editor.clear();
			editor.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
