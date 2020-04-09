package ch.ubique.android.starsdk.sample.logs;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.ubique.android.starsdk.logger.LogEntry;
import ch.ubique.android.starsdk.logger.Logger;
import ch.ubique.android.starsdk.sample.R;

public class LogsFragment extends Fragment {

	private Handler handler = new Handler();
	private Runnable updateLogsRunnable;

	public static LogsFragment newInstance() {
		return new LogsFragment();
	}

	public LogsFragment() {
		super(R.layout.fragment_logs);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		RecyclerView logsList = view.findViewById(R.id.logs_list);

		LinearLayoutManager layoutManager = (LinearLayoutManager) logsList.getLayoutManager();

		LogsAdapter logsAdapter = new LogsAdapter(getContext());
		logsList.setAdapter(logsAdapter);

		updateLogsRunnable = () -> {
			boolean isAtBottom = layoutManager.findLastCompletelyVisibleItemPosition() == logsAdapter.getItemCount() - 1;

			List<LogEntry> logs = Logger.getLogs(logsAdapter.getLastLogTime() + 1);
			logsAdapter.appendLogs(logs);

			if (isAtBottom) {
				logsList.smoothScrollToPosition(logsAdapter.getItemCount() - 1);
			}

			handler.postDelayed(updateLogsRunnable, 2 * 1000L);
		};
		updateLogsRunnable.run();
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();

		handler.removeCallbacks(updateLogsRunnable);
	}

}
