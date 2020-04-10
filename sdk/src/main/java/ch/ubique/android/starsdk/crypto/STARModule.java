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
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;

import ch.ubique.android.starsdk.database.models.Contact;
import ch.ubique.android.starsdk.util.DayDate;

public class STARModule {

	public static final int KEY_LENGTH = 26;

	public static final int NUMBER_OF_DAYS_TO_KEEP_DATA = 21;
	private static final int NUMBER_OF_EPOCHS_PER_DAY = 24 * 12;
	private static final int MILLISECONDS_PER_EPOCH = 24 * 60 * 60 * 1000 / NUMBER_OF_EPOCHS_PER_DAY;
	//TODO set correct broadcast key
	private static final byte[] BROADCAST_KEY = "TODOTODOTODOTODOTODOTODOTODOTODOTODO".getBytes();

	private static final String KEY_SK_LIST_JSON = "SK_LIST_JSON";
	private static STARModule instance;
	private SharedPreferences esp;

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

	public boolean init() {
		try {
			String stringKey = esp.getString(KEY_SK_LIST_JSON, null);
			if (stringKey != null) return true; //key already exists

			KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
			SecretKey secretKey = keyGenerator.generateKey();
			SKList skList = new SKList();
			skList.add(new Pair(new DayDate(System.currentTimeMillis()), secretKey.getEncoded()));
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
		DayDate nextDay = skList.get(0).first.getNextDay();
		byte[] SKt1 = getSKt1(skList.get(0).second);
		skList.add(0, new Pair(nextDay, SKt1));
		List subList = skList.subList(0, Math.min(NUMBER_OF_DAYS_TO_KEEP_DATA, skList.size()));
		skList = new SKList();
		skList.addAll(subList);
		storeSKList(skList);
	}

	private byte[] getCurrentSK(DayDate day) {
		SKList SKList = getSKList();
		while (SKList.get(0).first.isBefore(day)) {
			rotateSK();
			SKList = getSKList();
		}
		assert SKList.get(0).first.equals(day);
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

	public byte[] getCurrentEphId() {
		long now = System.currentTimeMillis();
		DayDate currentDay = new DayDate(now);
		byte[] SK = getCurrentSK(currentDay);
		int counter = (int) (now - currentDay.getStartOfDayTimestamp()) / MILLISECONDS_PER_EPOCH;
		return createEphIds(SK).get(counter);
	}

	public void checkContacts(byte[] sk, DayDate onsetDate, DayDate bucketDate, GetContactsCallback contactCallback,
			MatchCallback matchCallback) {

		DayDate dayToTest = onsetDate;
		byte[] skForDay = sk;
		while (dayToTest.isBeforeOrEquals(bucketDate)) {

			List<Contact> contactsOnDay = contactCallback.getContacts(dayToTest);
			if (contactsOnDay.size() > 0) {

				//generate all ephIds for day
				List<byte[]> ephIds = createEphIds(skForDay);

				//check all contacts if they match any of the ephIds
				for (Contact contact : contactsOnDay) {
					for (byte[] ephId : ephIds) {
						if (Arrays.equals(ephId, contact.getEphId())) {
							matchCallback.contactMatched(contact);
							break;
						}
					}
				}
			}

			//update day to next day and rotate sk accordingly
			dayToTest = dayToTest.getNextDay();
			skForDay = getSKt1(skForDay);
		}
	}

	public String getSecretKeyForPublishing(DayDate date) {
		for (Pair<DayDate, byte[]> daySKPair : getSKList()) {
			if (daySKPair.first.equals(date)) {
				return new String(Base64.encode(daySKPair.second, Base64.NO_WRAP));
			}
		}
		return null;
	}

	public void reset() {
		try {
			SharedPreferences.Editor editor = esp.edit();
			editor.clear();
			editor.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public interface GetContactsCallback {

		List<Contact> getContacts(DayDate date);

	}


	public interface MatchCallback {

		void contactMatched(Contact contact);

	}

}
