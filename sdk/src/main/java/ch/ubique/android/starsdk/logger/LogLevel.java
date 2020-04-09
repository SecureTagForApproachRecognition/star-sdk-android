package ch.ubique.android.starsdk.logger;

public enum LogLevel {

	DEBUG("d", 1),
	INFO("i", 2),
	ERROR("e", 3),
	OFF(null, Integer.MAX_VALUE);

	private final String key;
	private final int i;

	LogLevel(String key, int i) {
		this.key = key;
		this.i = i;
	}

	public String getKey() {
		return key;
	}

	public int getI() {
		return i;
	}
}
