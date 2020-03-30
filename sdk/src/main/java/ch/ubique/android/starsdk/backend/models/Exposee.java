/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk.backend.models;

import androidx.annotation.Nullable;

public class Exposee {

	private String key;

	@Nullable
	private Action action;

	@Nullable
	private int id;

	public Exposee(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	@Nullable
	public Action getAction() {
		return action;
	}

	@Nullable
	public int getId() {
		return id;
	}

}
