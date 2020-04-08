/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import com.google.gson.Gson;

import ch.ubique.android.starsdk.backend.BackendRepository;
import ch.ubique.android.starsdk.backend.CallbackListener;
import ch.ubique.android.starsdk.backend.DiscoveryRepository;
import ch.ubique.android.starsdk.backend.models.Application;
import ch.ubique.android.starsdk.backend.models.ApplicationsList;

public class AppConfigManager {

	private static AppConfigManager instance;

	public static synchronized AppConfigManager getInstance(Context context) {
		if (instance == null) {
			instance = new AppConfigManager(context);
		}
		return instance;
	}


	private static final String PREF_NAME = "appConfigPreferences";
	private static final String PREF_APPLICATION_LIST = "applicationList";
	private static final String PREF_TRACING_ENABLED = "tracingEnabled";
	private static final String PREF_LAST_SYNC_DATE = "lastSyncDate";
	private static final String PREF_AM_I_EXPOSED = "amIExposed";

	private String appId;
	private SharedPreferences sharedPrefs;
	private DiscoveryRepository discoveryRepository;

	private AppConfigManager(Context context) {
		discoveryRepository = new DiscoveryRepository(context);
		sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void triggerLoad() {
		discoveryRepository.getDiscovery(new CallbackListener<ApplicationsList>() {
			@Override
			public void onSuccess(ApplicationsList response) {
				sharedPrefs.edit().putString(PREF_APPLICATION_LIST, new Gson().toJson(response)).commit();
			}

			@Override
			public void onError(Throwable throwable) {
				throwable.printStackTrace();
			}
		});
	}

	public void loadSynchronous() throws IOException {
		ApplicationsList response = discoveryRepository.getDiscoverySync();
		sharedPrefs.edit().putString(PREF_APPLICATION_LIST, new Gson().toJson(response)).commit();
	}

	public ApplicationsList getLoadedApplicationsList() {
		return new Gson().fromJson(sharedPrefs.getString(PREF_APPLICATION_LIST, ""), ApplicationsList.class);
	}

	public Application getAppConfig() {
		for (Application application : getLoadedApplicationsList().getApplications()) {
			if (application.getAppId().equals(appId)) {
				return application;
			}
		}
		throw new IllegalStateException("The provided appId is not found by the discovery service!");
	}

	public void setTracingEnabled(boolean enabled) {
		sharedPrefs.edit().putBoolean(PREF_TRACING_ENABLED, enabled).commit();
	}

	public boolean isTracingEnabled() {
		return sharedPrefs.getBoolean(PREF_TRACING_ENABLED, false);
	}

	public void setLastSyncDate(long lastSyncDate) {
		sharedPrefs.edit().putLong(PREF_LAST_SYNC_DATE, lastSyncDate).commit();
	}

	public long getLastSyncDate() {
		return sharedPrefs.getLong(PREF_LAST_SYNC_DATE, 0);
	}

	public boolean getAmIExposed() {
		return sharedPrefs.getBoolean(PREF_AM_I_EXPOSED, false);
	}

	public void setAmIExposed(boolean exposed) {
		sharedPrefs.edit().putBoolean(PREF_AM_I_EXPOSED, exposed).commit();
	}

	public BackendRepository getBackendRepository(Context context) {
		Application appConfig = getAppConfig();
		//TODO what if appConfig is not yet loaded?
		return new BackendRepository(context, appConfig.getBackendBaseUrl(), appConfig.getListBaseUrl());
	}

}
