/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk;

public class TracingStatus {

	private int number_of_handshakes;
	private boolean tracking_active;
	private boolean was_contact_exposed;
	private long last_sync_date;
	private boolean am_i_exposed;
	private ErrorState error;

	public TracingStatus(int number_of_handshakes, boolean tracking_active, boolean was_contact_exposed, long last_sync_date,
			boolean am_i_exposed, ErrorState error) {
		this.number_of_handshakes = number_of_handshakes;
		this.tracking_active = tracking_active;
		this.was_contact_exposed = was_contact_exposed;
		this.last_sync_date = last_sync_date;
		this.am_i_exposed = am_i_exposed;
		this.error = error;
	}

	public int getNumber_of_handshakes() {
		return number_of_handshakes;
	}

	public boolean isTracking_active() {
		return tracking_active;
	}

	public boolean isWas_contact_exposed() {
		return was_contact_exposed;
	}

	public long getLast_sync_date() {
		return last_sync_date;
	}

	public boolean isAm_i_exposed() {
		return am_i_exposed;
	}

	public ErrorState getError() {
		return error;
	}

	public enum ErrorState {
		NEETWORK_ERROR_WHILE_SYNCING,
		MISSING_LOCATION_PERMISSION,
		BLE_DISABLED,
		BATTERY_OPTIMIZER_ENABLED,
		UNKNOWN
	}

}
