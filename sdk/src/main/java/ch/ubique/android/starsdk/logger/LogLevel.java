package ch.ubique.android.starsdk.logger;

public enum LogLevel {

	DEBUG("d", 1),
	INFO("i", 2),
	ERROR("e", 3),
	OFF("-", Integer.MAX_VALUE);

	private final String key;
	private final int i;

	LogLevel(String key, int i) {
		this.key = key;
		this.i = i;
	}

	public static LogLevel byKey(String key) {
		for (LogLevel value : LogLevel.values()) {
			if (value.getKey().equals(key)) {
				return value;
			}
		}
		return null;
	}

	public String getKey() {
		return key;
	}

	public int getI() {
		return i;
	}
}
