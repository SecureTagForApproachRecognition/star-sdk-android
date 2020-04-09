/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */
package ch.ubique.android.starsdk;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CryptoTest {
	/*@Test
	public void hmac_match() {
		STARModule module = STARModule.getInstance(InstrumentationRegistry.getInstrumentation().getContext());
		module.reset();
		if (module.init()) {
			byte[] star = module.getCurrentEphId();
			boolean assertion = module.isKeyMatchingEphId(module.getSecretKey(), star);
			assertTrue(assertion);
		}
	}

	@Test
	public void test_ios_tokens() {
		String key = "yinR5ETITuzZbTnJ947yNALuaxWj5ZCkewpTd/KQef4=";
		String token = "dzGTAZOrJS3NbKiAEdxPHbzNb5sogu8oQOQeY6Z7zRIdn7at";
		STARModule module = STARModule.getInstance(InstrumentationRegistry.getInstrumentation().getContext());
		module.reset();
		if (module.init()) {
			byte[] star = Base64.decode(token, Base64.NO_WRAP);
			byte[] keyByte = Base64.decode(key, Base64.NO_WRAP);
			boolean assertion = module.isKeyMatchingEphId(keyByte, star);
			assertTrue(assertion);
		}
	}

	@Test
	public void test_wrong_ios_tokens() {

		String token = "dzGTAZOrJS3NbKiAEdxPHbzNb5sogu8oQOQeY6Z7zRIdn7at";
		STARModule module = STARModule.getInstance(InstrumentationRegistry.getInstrumentation().getContext());
		module.reset();
		if (module.init()) {
			String key = Base64.encodeToString(module.getSecretKey(), Base64.NO_WRAP);
			byte[] star = Base64.decode(token, Base64.NO_WRAP);
			byte[] keyByte = Base64.decode(key, Base64.NO_WRAP);
			boolean assertion = module.isKeyMatchingEphId(keyByte, star);
			assertFalse(assertion);
		}
	}

	@Test
	public void generate_token() {
		STARModule module = STARModule.getInstance(InstrumentationRegistry.getInstrumentation().getContext());
		module.reset();
		if (module.init()) {
			String token = Base64.encodeToString(module.getCurrentEphId(), Base64.NO_WRAP);
			String key = Base64.encodeToString(module.getSecretKey(), Base64.NO_WRAP);
			Log.d("TOKEN: ", token);
			Log.d("KEY: ", key);
		}
	}

	@Test
	public void hmac_not_match() {
		STARModule module = STARModule.getInstance(InstrumentationRegistry.getInstrumentation().getContext());
		module.reset();
		if (module.init()) {
			byte[] star = module.getCurrentEphId();
			boolean assertion = module.isKeyMatchingEphId("wrongkey".getBytes(), star);
			assertFalse(assertion);
		}
	}

	private void out(String str) {
		Bundle b = new Bundle();
		b.putString(Instrumentation.REPORT_KEY_STREAMRESULT, "\n" + str);
		InstrumentationRegistry.getInstrumentation().sendStatus(0, b);
	}*/
}
