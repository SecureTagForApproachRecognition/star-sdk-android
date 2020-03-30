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

import java.io.IOException;

import ch.ubique.android.starsdk.backend.models.ApplicationsList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DiscoveryRepository implements Repository {

	private DiscoveryService discoveryService;

	public DiscoveryRepository(@NonNull Context context) {

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://discovery.next-step.io/")
				.client(getClientBuilder(context).build())
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		discoveryService = retrofit.create(DiscoveryService.class);
	}

	public void getDiscovery(@NonNull CallbackListener<ApplicationsList> callbackListener) {
		//TODO caching for no network connection

		discoveryService.getDiscovery().enqueue(new Callback<ApplicationsList>() {

			@Override
			public void onResponse(@NonNull Call<ApplicationsList> call, @NonNull Response<ApplicationsList> response) {
				if (response.isSuccessful()) {
					callbackListener.onSuccess(response.body());
				} else {
					onFailure(call, new ResponseException(response.raw()));
				}
			}

			@Override
			public void onFailure(@NonNull Call<ApplicationsList> call, @NonNull Throwable throwable) {
				callbackListener.onError(throwable);
			}
		});
	}

	public ApplicationsList getDiscoverySync() throws IOException {
		return discoveryService.getDiscovery().execute().body();
	}

}
