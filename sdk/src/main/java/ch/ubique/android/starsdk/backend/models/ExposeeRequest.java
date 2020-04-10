package ch.ubique.android.starsdk.backend.models;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExposeeRequest {

	private static final SimpleDateFormat onsetDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private String key;
	private String onset;
	private ExposeeAuthData authData;

	public ExposeeRequest(String key, String onset, ExposeeAuthData authData) {
		this.key = key;
		this.onset = onset;
		this.authData = authData;
	}

	public ExposeeRequest(String key, Date onsetDate, ExposeeAuthData authData) {
		this.key = key;
		this.onset = onsetDateFormat.format(onsetDate);
		this.authData = authData;
	}

	public String getKey() {
		return key;
	}

	public String getOnset() {
		return onset;
	}

	public ExposeeAuthData getAuthData() {
		return authData;
	}

}
