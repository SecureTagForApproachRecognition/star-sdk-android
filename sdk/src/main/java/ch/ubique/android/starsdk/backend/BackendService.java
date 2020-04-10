/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk.backend;

import ch.ubique.android.starsdk.backend.models.Exposee;
import ch.ubique.android.starsdk.backend.models.ExposedList;
import ch.ubique.android.starsdk.backend.models.ExposeeRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

interface BackendService {

	@GET
	Call<ExposedList> getExposees(@Url String url);

	@POST("v1/exposed")
	Call<Void> addExposee(@Body ExposeeRequest exposeeRequest);

	@POST("v1/removeexposed")
	Call<Void> removeExposee(@Body Exposee exposee);
}
