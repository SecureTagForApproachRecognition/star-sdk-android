/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk.backend;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import okhttp3.Response;

public class ResponseException extends Exception {

	private Response response;

	public ResponseException(@NonNull Response response) {
		this.response = response;
	}

	@Nullable
	@Override
	public String getMessage() {
		return "Code: " + response.code() + " Message: " + response.message();
	}
}
