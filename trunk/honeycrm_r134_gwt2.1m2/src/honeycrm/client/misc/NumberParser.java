package honeycrm.client.misc;

public class NumberParser {
	/**
	 * Convert a given object safely to a double value.
	 */
	public static double convertToDouble(final Object value) {
		if (null == value)
			return 0;

		if (value instanceof Double)
			return (Double) value;

		try {
			return Double.parseDouble(value.toString());
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public static int convertToInteger(final Object value) {
		if (null == value)
			return 0;

		if (value instanceof Integer)
			return (Integer) value;

		try {
			return Integer.parseInt(value.toString());
		} catch (NumberFormatException e) {
			return 0;
		}
	}	

	/**
	 * Convert a given object safely to a long value
	 */
	public static long convertToLong(final Object value) {
		if (null == value)
			return 0;

		if (value instanceof Long)
			return (Long) value;

		try {
			return Long.parseLong(value.toString().replace("\r", ""));
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}