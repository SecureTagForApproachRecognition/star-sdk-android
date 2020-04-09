package ch.ubique.android.starsdk.sample.logs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ch.ubique.android.starsdk.logger.LogEntry;
import ch.ubique.android.starsdk.sample.R;

class LogsAdapter extends RecyclerView.Adapter<LogsViewHolder> {

	private final LayoutInflater inflater;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);

	private final List<LogEntry> logs = new ArrayList<>();

	public LogsAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}

	@NonNull
	@Override
	public LogsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new LogsViewHolder(inflater.inflate(R.layout.view_log_entry, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull LogsViewHolder holder, int position) {
		LogEntry logEntry = logs.get(position);
		holder.timeView.setText(dateFormat.format(new Date(logEntry.getTime())));
		holder.levelView.setText(logEntry.getLevel().getKey());
		holder.tagView.setText(logEntry.getTag());
		holder.messageView.setText(logEntry.getMessage());

		int color = 0x22000000;
		switch (logEntry.getLevel()) {
			case DEBUG:
				color |= 0x0000FF;
				break;
			case INFO:
				color |= 0x00AA00;
				break;
			case ERROR:
				color |= 0xFF0000;
				break;
		}
		holder.itemView.setBackgroundColor(color);
	}

	@Override
	public int getItemCount() {
		return logs.size();
	}

	public long getLastLogTime() {
		if (logs.isEmpty()) {
			return -1;
		} else {
			return logs.get(logs.size() - 1).getTime();
		}
	}

	public void appendLogs(List<LogEntry> logEntries) {
		int startIndex = logs.size();
		logs.addAll(logEntries);
		notifyItemRangeInserted(startIndex, logEntries.size());
	}
}

class LogsViewHolder extends RecyclerView.ViewHolder {

	final TextView timeView;
	final TextView levelView;
	final TextView tagView;
	final TextView messageView;

	LogsViewHolder(@NonNull View itemView) {
		super(itemView);
		timeView = itemView.findViewById(R.id.log_entry_time);
		levelView = itemView.findViewById(R.id.log_entry_level);
		tagView = itemView.findViewById(R.id.log_entry_tag);
		messageView = itemView.findViewById(R.id.log_entry_msg);
	}

}
