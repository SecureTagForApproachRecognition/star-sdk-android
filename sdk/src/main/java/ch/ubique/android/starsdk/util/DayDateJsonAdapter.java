package ch.ubique.android.starsdk.util;

import java.io.IOException;
import java.text.ParseException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

class DayDateJsonAdapter extends TypeAdapter<DayDate> {

	@Override
	public void write(JsonWriter out, DayDate value) throws IOException {
		out.value(value.formatAsString());
	}

	@Override
	public DayDate read(JsonReader in) throws IOException {
		String value = in.nextString();
		try {
			return new DayDate(value);
		} catch (ParseException e) {
			throw new IOException("Unexpected DayDate format " + value, e);
		}
	}

}
