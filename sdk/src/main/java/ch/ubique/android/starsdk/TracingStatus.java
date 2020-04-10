/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk;

import java.util.ArrayList;

public class TracingStatus {

	private int number_of_handshakes;
	private boolean is_advertising;
	private boolean is_receiving;
	private boolean was_contact_exposed;
	private long last_sync_date;
	private boolean am_i_exposed;
	private ArrayList<ErrorState> errors;

	public TracingStatus(int number_of_handshakes, boolean is_advertising, boolean is_receiving, boolean was_contact_exposed, long last_sync_date,
			boolean am_i_exposed, ArrayList<ErrorState> errors) {
		this.number_of_handshakes = number_of_handshakes;
		this.is_advertising = is_advertising;
		this.is_receiving = is_receiving;
		this.was_contact_exposed = was_contact_exposed;
		this.last_sync_date = last_sync_date;
		this.am_i_exposed = am_i_exposed;
		this.errors = errors;
	}

	public int getNumber_of_handshakes() {
		return number_of_handshakes;
	}

	public boolean isAdvertising() {
		return is_advertising;
	}

	public boolean isReceiving() {
		return is_receiving;
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

	public ArrayList<ErrorState> getErrors() {
		return errors;
	}

	public enum ErrorState {
		NETWORK_ERROR_WHILE_SYNCING,
		MISSING_LOCATION_PERMISSION,
		BLE_DISABLED,
		BATTERY_OPTIMIZER_ENABLED,
		UNKNOWN
	}

}
