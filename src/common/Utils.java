package common;

import java.util.LinkedList;

public class Utils {
	// common functionality

	public static final String dash = " - ";
	public static final String sep = ", ";

	// function to collate intervals in a list
	public static String concatListInterval(LinkedList<Integer>
	                                        list) {
		if (list == null || list.size() == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder();

		while (list.size() > 0) {
			int start = list.poll();
			int end = start;
			while (list.size() > 0 && list.peek() == end + 1) {
				end = list.poll();
			}
			if (start != end) {
				sb.append(String.valueOf(start));
				sb.append(dash);
				sb.append(String.valueOf(end));
			} else {
				sb.append(String.valueOf(start));
			}
			if (list.size() > 0) {
				sb.append(sep);
			}
		}

		return sb.toString();
	}

	// functions for handling error
	public static void handleError(String err) {
		System.err.println(err);
		System.exit(-1);
	}

	public static void handleError(String err, Exception e) {
		System.err.println(err);
		e.printStackTrace();
		System.exit(-1);
	}

	// function to parse integer
	public static int getInt(String s, String err) {
		int result = -1;
		try {
			result = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			handleError(err, e);
		}
		return result;
	}
}
