package ch.ubique.android.starsdk.backend.models;

import ch.ubique.android.starsdk.util.DayDate;

public class ExposeeRequest {

	private String key;
	private DayDate onset;
	private ExposeeAuthData authData;

	public ExposeeRequest(String key, DayDate onset, ExposeeAuthData authData) {
		this.key = key;
		this.onset = onset;
		this.authData = authData;
	}

	public String getKey() {
		return key;
	}

	public DayDate getOnset() {
		return onset;
	}

	public ExposeeAuthData getAuthData() {
		return authData;
	}

}
