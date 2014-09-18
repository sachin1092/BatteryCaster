package saphion.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import saphion.logger.Log;

public class TimeFuncs {
	public static String getCurrentTimeStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat(
				"MM/dd/yyyy hh:mm:ss a", Locale.US);// dd/MM/yyyy
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
	}

	public static Date GetItemDate(final String date) {
		final Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		final SimpleDateFormat format = new SimpleDateFormat(
				"MM/dd/yyyy hh:mm:ss a", Locale.US);
		format.setCalendar(cal);

		try {
			return format.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Getting Difference in Minutes between two Time-Stamps
	 */
	public static float minutesDiff(Date earlierDate, Date laterDate) {
		if (earlierDate == null || laterDate == null)
			return 0;

		return (float) ((laterDate.getTime() / 60000) - (earlierDate.getTime() / 60000));
	}

	/**
	 * Getting Difference in Seconds between two Time-Stamps
	 */
	public static float secondsDiff(Date earlierDate, Date laterDate) {
		if (earlierDate == null || laterDate == null)
			return 0;

		return (float) (((laterDate.getTime() % 60000) - (earlierDate.getTime() % 60000)) / 1000);
	}

	/**
	 * Converts a Given Minute to Hour and Minute String
	 */
	public static String convmintohournmin(float level) {
		int hour = (int) (level / 60);
		int minute = (int) (level % 60);
		return hour + "h " + minute + "m";

	}

	/**
	 * Converts a Given Seconds to Hour and Minute String
	 */
	public static String convtohournmin(long sec) {
		int hour = (int) (sec / 3600);
		int minute = (int) (sec % 3600);
		minute = (int) (minute / 60);
		return hour + " h " + minute + " m";

	}

	/**
	 * Converts a Given Seconds to Days, Hours and Minutes String
	 */
	public static String convtohournminnday(long sec) {
		int hour = (int) (sec / 3600);
		int minute = (int) (sec % 3600);
		minute = (int) (minute / 60);
		int day = 0;
		if (hour >= 24) {
			day = hour / 24;
			hour = hour % 24;
		}
		if (day != 0)
			return day + " Day(s) " + hour + " Hour(s) " + minute
					+ " Minute(s)";
		else {
			if (hour != 0)
				return hour + " Hour(s) " + minute + " Minute(s)";
			else
				return minute + " Minute(s)";
		}
	}

	/**
	 * Getting Difference in Seconds between two Time-Stamps
	 */
	public static long newDiff(Date date1, Date date2) {
		long milliSec1 = date1.getTime();
		long milliSec2 = date2.getTime();

		long timeDifInMilliSec;
		if (milliSec1 >= milliSec2) {
			timeDifInMilliSec = milliSec1 - milliSec2;
		} else {
			timeDifInMilliSec = milliSec2 - milliSec1;
		}

		long timeDifSeconds = timeDifInMilliSec / 1000;
		/*
		 * long timeDifMinutes = timeDifInMilliSec / (60 * 1000); long
		 * timeDifHours = timeDifInMilliSec / (60 * 60 * 1000); long timeDifDays
		 * = timeDifInMilliSec / (24 * 60 * 60 * 1000);
		 * 
		 * System.out.println(
		 * "Time differences expressed in various units are given below");
		 * System.out.println(timeDifInMilliSec + " Milliseconds");
		 * System.out.println(timeDifSeconds + " Seconds");
		 * System.out.println(timeDifMinutes + " Minutes");
		 * System.out.println(timeDifHours + " Hours");
		 * System.out.println(timeDifDays + " Days");
		 */
		return timeDifSeconds;
	}

	/**
	 * Converts a Given Seconds to Days, Hours, Minutes and seconds String
	 */
	public static String convtohournminndaynsec(long sec) {
		int hour = (int) (sec / 3600);
		int minute = (int) (sec % 3600);
		sec = (int) minute % 60;
		minute = (int) (minute / 60);

		int day = 0;
		if (hour >= 24) {
			day = hour / 24;
			hour = hour % 24;
		}
		if (day != 0)
			return day + " Day(s) " + hour + " Hour(s) " + minute
					+ " Minute(s)";
		else {
			if (hour != 0)
				return hour + " Hour(s) " + minute + " Minute(s)";
			else {
				if (minute != 0)
					return minute + " Minute(s) " + sec + " Second(s)";
				else
					return sec + " Second(s)";

			}
		}
	}

	public static String convtohwidget(long sec) {

		Log.d("Seconds " + sec);
		int hour = (int) (sec / 3600);

		return hour + "";

	}

}
