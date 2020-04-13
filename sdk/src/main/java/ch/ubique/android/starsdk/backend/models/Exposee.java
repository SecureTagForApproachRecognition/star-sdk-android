/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */
package ch.ubique.android.starsdk.backend.models;

import ch.ubique.android.starsdk.util.DayDate;

public class Exposee {

	private String key;

	private DayDate onset;

	public Exposee(String key, DayDate onset) {
		this.key = key;
		this.onset = onset;
	}

	public String getKey() {
		return key;
	}

	public DayDate getOnset() {
		return onset;
	}

}
