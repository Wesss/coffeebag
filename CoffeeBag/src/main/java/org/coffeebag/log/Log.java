package org.coffeebag.log;

import java.util.function.Predicate;

/**
 * A simple logging tool with basic configuration support
 */
public class Log {

	/**
	 * Log levels
	 */
	public enum Level {
		VERBOSE,
		DEBUG,
		INFO,;
		
		public String toShortString() {
			switch (this) {
			case DEBUG:
				return "D";
			case INFO:
				return "I";
			case VERBOSE:
				return "V";
			default:
				return "<?>";	
			}
		}
	}
	/**
	 * Filters log messages based on their tags
	 */
	private Predicate<String> tagFilter;
	
	/**
	 * The log instance
	 */
	private static Log instance;
	
	/**
	 * @return the global Log instance
	 */
	public static Log getInstance() {
		if (instance == null) {
			instance = new Log();
			// Enable by default
			instance.tagFilter = (tag) -> true;
		}
		return instance;
	}
	
	public void log(Level level, String tag, String message) {
		if (tagFilter.test(tag)) {
			final StringBuilder builder = new StringBuilder();
			builder.append('[')
				.append(level.toShortString())
				.append('/')
				.append(tag)
				.append("] ")
				.append(message);
			
			System.out.println(builder.toString());
		}
	}
	
	/**
	 * Enables or disables the logger
	 * @param enabled if the logger should be enabled
	 */
	public void setEnabled(boolean enabled) {
		tagFilter = (tag) -> enabled;
	}
	
	/**
	 * Sets a predicate to use for filtering messages by tag
	 * @param filter
	 */
	public void setTagFilter(Predicate<String> filter) {
		tagFilter = filter;
	}

	/**
	 * Logs a verbose message
	 * @param tag the component that generated the message
	 * @param message the message
	 */
	public static void v(String tag, String message) {
		getInstance().log(Level.VERBOSE, tag, message);
	}
	
	/**
	 * Logs a debug message
	 * @param tag the component that generated the message
	 * @param message the message
	 */
	public static void d(String tag, String message) {
		getInstance().log(Level.DEBUG, tag, message);
	}
	
	/**
	 * Logs an information message
	 * @param tag the component that generated the message
	 * @param message the message
	 */
	public static void i(String tag, String message) {
		getInstance().log(Level.INFO, tag, message);
	}
}
