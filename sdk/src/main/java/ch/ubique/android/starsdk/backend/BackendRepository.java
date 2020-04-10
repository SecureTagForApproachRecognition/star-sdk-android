/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk.backend;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import ch.ubique.android.starsdk.backend.models.Exposee;
import ch.ubique.android.starsdk.backend.models.ExposedList;
import ch.ubique.android.starsdk.backend.models.ExposeeRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackendRepository implements Repository {

	private BackendService backendService;

	private String listBaseUrl;

	public BackendRepository(@NonNull Context context, @NonNull String backendBaseUrl, @NonNull String listBaseUrl) {

		this.listBaseUrl = listBaseUrl;

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(backendBaseUrl)
				.client(getClientBuilder(context).build())
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		backendService = retrofit.create(BackendService.class);
	}

	@Nullable
	public ExposedList getExposees(@NonNull String dayDate) throws IOException, ResponseException {
		String url = listBaseUrl + "v1/" + dayDate + ".json";
		Response<ExposedList> response = backendService.getExposees(url).execute();
		if(response.isSuccessful()){
			return response.body();
		}
		throw new ResponseException(response.raw());
	}

	public void getExposees(@NonNull String dayDate, @NonNull CallbackListener<ExposedList> callbackListener) {
		String url = listBaseUrl + "v1/" + dayDate + ".json";
		backendService.getExposees(url).enqueue(new Callback<ExposedList>() {

			@Override
			public void onResponse(@NonNull Call<ExposedList> call, @NonNull Response<ExposedList> response) {
				if (response.isSuccessful()) {
					callbackListener.onSuccess(response.body());
				} else {
					onFailure(call, new ResponseException(response.raw()));
				}
			}

			@Override
			public void onFailure(@NonNull Call<ExposedList> call, @NonNull Throwable throwable) {
				callbackListener.onError(throwable);
			}

		});
	}

	public void addExposee(@NonNull ExposeeRequest exposeeRequest, @NonNull CallbackListener<Void> callbackListener) {

		backendService.addExposee(exposeeRequest).enqueue(new Callback<Void>() {

			@Override
			public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
				if (response.isSuccessful()) {
					callbackListener.onSuccess(null);
				} else {
					onFailure(call, new ResponseException(response.raw()));
				}
			}

			@Override
			public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
				callbackListener.onError(throwable);
			}

		});
	}

	public void removeExposee(@NonNull Exposee exposee, @NonNull CallbackListener<Void> callbackListener) {

		backendService.removeExposee(exposee).enqueue(new Callback<Void>() {

			@Override
			public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
				if (response.isSuccessful()) {
					callbackListener.onSuccess(null);
				} else {
					onFailure(call, new ResponseException(response.raw()));
				}
			}

			@Override
			public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
				callbackListener.onError(throwable);
			}

		});
	}


}
