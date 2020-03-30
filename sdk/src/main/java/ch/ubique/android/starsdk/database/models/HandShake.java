/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk.database.models;

public class HandShake {

	private int id;
	private long timestamp;
	private byte[] star;
	private int associatedKnownCase;

	public HandShake(int id, long timstamp, byte[] star, int associatedKnownCase) {
		this.id = id;
		this.timestamp = timstamp;
		this.star = star;
		this.associatedKnownCase = associatedKnownCase;
	}

	public byte[] getStar() {
		return star;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public int getAssociatedKnownCase() {
		return associatedKnownCase;
	}

}
