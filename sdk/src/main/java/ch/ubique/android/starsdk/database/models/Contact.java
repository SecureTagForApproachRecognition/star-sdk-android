/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */
package ch.ubique.android.starsdk.database.models;

import ch.ubique.android.starsdk.util.DayDate;

public class Contact {

	private int id;
	private DayDate date;
	private byte[] ephId;
	private int associatedKnownCase;

	public Contact(int id, DayDate date, byte[] ephId, int associatedKnownCase) {
		this.id = id;
		this.date = date;
		this.ephId = ephId;
		this.associatedKnownCase = associatedKnownCase;
	}

	public byte[] getEphId() {
		return ephId;
	}

	public DayDate getDate() {
		return date;
	}

	public int getAssociatedKnownCase() {
		return associatedKnownCase;
	}

	public int getId() {
		return id;
	}

}
