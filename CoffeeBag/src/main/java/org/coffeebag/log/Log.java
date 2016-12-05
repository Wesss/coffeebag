package org.coffeebag.log;

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
	 * If any log messages should be output
	 */
	private boolean enabled;
	
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
		}
		instance.enabled = true;
		return instance;
	}
	
	public void log(Level level, String tag, String message) {
		if (enabled) {
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
		this.enabled = enabled;
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
