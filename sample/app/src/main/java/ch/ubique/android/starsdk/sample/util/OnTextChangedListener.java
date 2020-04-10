package ch.ubique.android.starsdk.sample.util;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class OnTextChangedListener implements TextWatcher {
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

}
