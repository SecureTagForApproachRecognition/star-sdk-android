package ch.ubique.android.starsdk.sample.handshakes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import ch.ubique.android.starsdk.database.Database;
import ch.ubique.android.starsdk.database.models.HandShake;
import ch.ubique.android.starsdk.sample.R;

public class HandshakesFragment extends Fragment {

	TextView handshakeList;

	public static HandshakesFragment newInstance() {
		return new HandshakesFragment();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_handshakes, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		handshakeList = view.findViewById(R.id.handshake_list);

		new Database(getContext()).getHandshakes(response -> {
			StringBuilder stringBuilder = new StringBuilder();
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM HH:mm:ss");
			for (HandShake handShake : response) {
				stringBuilder.append(sdf.format(new Date(handShake.getTimestamp())));
				stringBuilder.append(" ");
				stringBuilder.append(handShake.getMacAddress());
				stringBuilder.append(" ");
				stringBuilder.append(new String(handShake.getStar()));
				stringBuilder.append(" TxPowerLevel: ");
				stringBuilder.append(handShake.getTxPowerLevel());
				stringBuilder.append(" RSSI:");
				stringBuilder.append(handShake.getRssi());
				stringBuilder.append("\n");
			}
			handshakeList.setText(stringBuilder.toString());
		});
	}

}
