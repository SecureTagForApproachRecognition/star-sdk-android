package ch.ubique.android.starsdk.sample.handshakes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ch.ubique.android.starsdk.sample.R;

public class HandshakesFragment extends Fragment {

	public static HandshakesFragment newInstance() {
		return new HandshakesFragment();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_handshakes, container, false);
	}

}
