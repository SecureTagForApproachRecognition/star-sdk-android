/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk.backend.models;

public class Application {

	private String appId;
	private String description;
	private String backendBaseUrl;
	private String listBaseUrl;
	private String bleGattGuid;
	private String contact;

	public String getAppId() {
		return appId;
	}

	public String getDescription() {
		return description;
	}

	public String getBackendBaseUrl() {
		return backendBaseUrl;
	}

	public String getListBaseUrl() {
		return listBaseUrl;
	}

	public String getBleGattGuid() {
		return bleGattGuid;
	}

	public String getContact() {
		return contact;
	}

}
