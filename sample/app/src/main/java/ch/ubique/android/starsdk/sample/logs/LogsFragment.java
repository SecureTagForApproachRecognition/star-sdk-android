package ch.ubique.android.starsdk.sample.logs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ch.ubique.android.starsdk.sample.R;
import ch.ubique.android.starsdk.util.LogHelper;

public class LogsFragment extends Fragment {

	TextView logTextView;

	public static LogsFragment newInstance() {
		return new LogsFragment();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_logs, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		logTextView = view.findViewById(R.id.logs);
		LogHelper.init(getActivity().getApplicationContext());
		updateLogsRunnable.run();
	}

	private Runnable updateLogsRunnable = new Runnable() {
		@Override
		public void run() {
			logTextView.setText(LogHelper.getLog());
			logTextView.postDelayed(updateLogsRunnable, 10 * 1000L);
		}
	};

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		logTextView.removeCallbacks(updateLogsRunnable);
	}

}
