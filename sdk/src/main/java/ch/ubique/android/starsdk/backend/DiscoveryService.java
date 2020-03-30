/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk.backend;

import ch.ubique.android.starsdk.backend.models.ApplicationsList;
import retrofit2.Call;
import retrofit2.http.GET;

interface DiscoveryService {

	@GET("discovery.json")
	Call<ApplicationsList> getDiscovery();

}
