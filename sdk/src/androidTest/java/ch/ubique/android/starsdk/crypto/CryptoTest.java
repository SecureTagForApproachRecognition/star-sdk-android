/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */
package ch.ubique.android.starsdk.crypto;

import android.util.Base64;
import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.ubique.android.starsdk.database.models.Contact;
import ch.ubique.android.starsdk.util.DayDate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CryptoTest {

	@Test
	public void generateEphIds() {
		STARModule module = STARModule.getInstance(InstrumentationRegistry.getInstrumentation().getContext());
		module.reset();
		module.init();
		List<byte[]> allEphIdsOfToday = module.createEphIds(module.getCurrentSK(new DayDate()));
		byte[] currentEphId = module.getCurrentEphId();
		int matchingCount = 0;
		for (byte[] ephId : allEphIdsOfToday) {
			if (Arrays.equals(ephId, currentEphId)) {
				matchingCount++;
			}
		}
		assertTrue(matchingCount == 1);
	}

	@Test
	public void testReset() {
		STARModule module = STARModule.getInstance(InstrumentationRegistry.getInstrumentation().getContext());
		module.reset();
		module.init();

		DayDate today = new DayDate();
		byte[] oldSecretKey = module.getCurrentSK(today);
		byte[] oldCurrentEphId = module.getCurrentEphId();

		module.reset();
		module.init();

		byte[] newSecretKey = module.getCurrentSK(today);
		byte[] mewCurrentEphId = module.getCurrentEphId();

		Log.d("crypto", "key: " + new String(Base64.encode(newSecretKey, Base64.NO_WRAP)));
		byte[] nextKey = module.getSKt1(newSecretKey);
		byte[] nextEphId = module.createEphIds(nextKey).get(23);
		Log.d("crypto", "ephid: " + new String(Base64.encode(nextEphId, Base64.NO_WRAP)));

		assertFalse(Arrays.equals(oldSecretKey, newSecretKey));
		assertFalse(Arrays.equals(oldCurrentEphId, mewCurrentEphId));
	}

	@Test
	public void testTokenToday() {
		String key = "lTSYc/ER08HD1/ucwBJOiDLDEYiJruKqTHCiOFavzwA=";
		String token = "yJNfwAP8UaF+BZKbUiVwhUghLz60SOqPE0I=";
		testKeyAndTokenToday(key, token, 1);
	}

	@Test
	public void testWrongTokenToday() {
		String key = "yJNfwAP8UaF+BZKbUiVwhUghLz60SOqPE0I=";
		String token = "lTSYc/ER08HD1/ucwBJOiDLDEYiJruKqTHCiOFavzwA=";
		testKeyAndTokenToday(key, token, 0);
	}

	private void testKeyAndTokenToday(String key, String token, int expectedCount) {
		STARModule module = STARModule.getInstance(InstrumentationRegistry.getInstrumentation().getContext());
		module.reset();
		module.init();

		byte[] ephId = Base64.decode(token, Base64.NO_WRAP);
		DayDate today = new DayDate();
		List<Contact> contacts = new ArrayList<>();
		contacts.add(new Contact(0, today, ephId, 0));
		byte[] keyByte = Base64.decode(key, Base64.NO_WRAP);

		HashSet<Contact> infectedContacts = new HashSet<>();
		module.checkContacts(keyByte, today, today,
				date -> contacts.stream().filter(c -> c.getDate().equals(date)).collect(Collectors.toList()),
				contact -> infectedContacts.add(contact));

		assertTrue(infectedContacts.size() == expectedCount);
	}

	@Test
	public void testTokenTodayWithYesterdaysKey() {
		String key = "fdf3S4/WMFSbCqowrJHYZ45EHurPlSmIdrjVdUvHJNg=";
		String token = "KZaA+cr21kV/vpx3CKOYPf631VS6GTy2QoU=";

		STARModule module = STARModule.getInstance(InstrumentationRegistry.getInstrumentation().getContext());
		module.reset();
		module.init();

		byte[] ephId = Base64.decode(token, Base64.NO_WRAP);
		DayDate today = new DayDate();
		DayDate yesterday = today.subtractDays(1);
		List<Contact> contacts = new ArrayList<>();
		contacts.add(new Contact(0, today, ephId, 0));
		byte[] keyByte = Base64.decode(key, Base64.NO_WRAP);

		HashSet<Contact> infectedContacts = new HashSet<>();
		module.checkContacts(keyByte, yesterday, today,
				date -> contacts.stream().filter(c -> c.getDate().equals(date)).collect(Collectors.toList()),
				contact -> infectedContacts.add(contact));

		assertTrue(infectedContacts.size() == 1);
	}

}
