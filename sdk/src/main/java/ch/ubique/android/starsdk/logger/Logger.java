package ch.ubique.android.starsdk.logger;

import android.content.Context;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {

	private static Logger instance = null;

	private final LogLevel activeLevel;
	private final LogDatabase database;

	public static void init(Context context, LogLevel level) {
		instance = new Logger(context, level);
	}

	private Logger(Context context, LogLevel level) {
		this.activeLevel = level;
		this.database = new LogDatabase(context);
	}

	public static void d(String tag, String message) {
		if (instance != null) {
			instance.log(LogLevel.DEBUG, tag, message);
		}
	}

	public static void i(String tag, String message) {
		if (instance != null) {
			instance.log(LogLevel.INFO, tag, message);
		}
	}

	public static void e(String tag, String message) {
		if (instance != null) {
			instance.log(LogLevel.ERROR, tag, message);
		}
	}

	public static void e(String tag, Throwable throwable) {
		StringWriter sw = new StringWriter();
		throwable.printStackTrace(new PrintWriter(sw));
		e(tag, sw.toString());
	}

	private void log(LogLevel level, String tag, String message) {
		if (level.getI() < activeLevel.getI()) {
			return;
		}

		database.log(level.getKey(), tag, message, System.currentTimeMillis());
	}

}
