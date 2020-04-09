package ch.ubique.android.starsdk.logger;

public class LogEntry {

	private final long time;
	private final LogLevel level;
	private final String tag;
	private final String message;

	public LogEntry(long time, LogLevel level, String tag, String message) {
		this.time = time;
		this.level = level;
		this.tag = tag;
		this.message = message;
	}

	public long getTime() {
		return time;
	}

	public LogLevel getLevel() {
		return level;
	}

	public String getTag() {
		return tag;
	}

	public String getMessage() {
		return message;
	}

}
