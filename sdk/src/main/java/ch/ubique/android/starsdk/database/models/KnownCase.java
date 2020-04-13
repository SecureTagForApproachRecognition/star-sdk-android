/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */
package ch.ubique.android.starsdk.database.models;

import static ch.ubique.android.starsdk.util.Base64Util.fromBase64;

public class KnownCase {

	private int id;
	private String day;
	private String key;

	public KnownCase(int id, String day, String key) {
		this.id = id;
		this.day = day;
		this.key = key;
	}

	public int getId() {
		return id;
	}

	public String getDay() {
		return day;
	}

	public String getKey() {
		return key;
	}

	public byte[] getParsedKey() {
		return fromBase64(key);
	}

}
