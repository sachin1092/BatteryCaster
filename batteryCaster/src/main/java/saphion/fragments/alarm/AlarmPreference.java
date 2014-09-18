package saphion.fragments.alarm;

import java.io.IOException;
import java.util.ArrayList;

import saphion.utils.ObjectSerializer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AlarmPreference {

	public final static String SHARED_PREFS_FILE = "alarmprefs";
	public final static String LEVEL = "ml";
	public final static String ENABLED = "an";
	public final static String LABEL = "ll";
	public final static String DAYS = "d";
	public final static String RINGTONE = "rt";
	public final static String VIBRATE = "vbt";
	public static final String REPEAT = "rp";
	public static final String TRACK = "tr";

	@SuppressWarnings("unchecked")
	public static ArrayList<Integer> retIntegers(Context mContext, String TASKS) {
		ArrayList<Integer> currentTasks = null;
		if (null == currentTasks) {
			currentTasks = new ArrayList<Integer>();
		}

		// load tasks from preference
		SharedPreferences prefs = mContext.getSharedPreferences(
				SHARED_PREFS_FILE, Context.MODE_PRIVATE);

		try {
			currentTasks = (ArrayList<Integer>) ObjectSerializer
					.deserialize(prefs.getString(TASKS, ObjectSerializer
							.serialize(new ArrayList<Integer>())));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return currentTasks;
	}

	public static void removeInteger(Context mContext, String TASKS) {
		SharedPreferences prefs = mContext.getSharedPreferences(
				SHARED_PREFS_FILE, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.remove(TASKS);
		editor.commit();
	}

	public static void saveIntegers(Context mContext,
			ArrayList<Integer> currentTasks, String TASKS) {
		assert (null != currentTasks);
		if (null == currentTasks) {
			currentTasks = new ArrayList<Integer>();
		}
		SharedPreferences prefs = mContext.getSharedPreferences(
				SHARED_PREFS_FILE, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		try {
			editor.putString(TASKS, ObjectSerializer.serialize(currentTasks));
		} catch (IOException e) {
			e.printStackTrace();
		}
		editor.commit();
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Boolean> retBooleans(Context mContext, String TASKS) {
		ArrayList<Boolean> currentTasks = null;
		if (null == currentTasks) {
			currentTasks = new ArrayList<Boolean>();
		}

		// load tasks from preference
		SharedPreferences prefs = mContext.getSharedPreferences(
				SHARED_PREFS_FILE, Context.MODE_PRIVATE);

		try {
			currentTasks = (ArrayList<Boolean>) ObjectSerializer
					.deserialize(prefs.getString(TASKS, ObjectSerializer
							.serialize(new ArrayList<Boolean>())));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return currentTasks;
	}

	public static void saveBooleans(Context mContext,
			ArrayList<Boolean> currentTasks, String TASKS) {
		assert (null != currentTasks);
		if (null == currentTasks) {
			currentTasks = new ArrayList<Boolean>();
		}
		SharedPreferences prefs = mContext.getSharedPreferences(
				SHARED_PREFS_FILE, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		try {
			editor.putString(TASKS, ObjectSerializer.serialize(currentTasks));
		} catch (IOException e) {
			e.printStackTrace();
		}
		editor.commit();
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<ArrayList<String>> retSuperStrings(
			Context mContext, String TASKS) {
		ArrayList<ArrayList<String>> currentTasks = null;
		if (null == currentTasks) {
			currentTasks = new ArrayList<ArrayList<String>>();
		}

		// load tasks from preference
		SharedPreferences prefs = mContext.getSharedPreferences(
				SHARED_PREFS_FILE, Context.MODE_PRIVATE);

		try {
			currentTasks = (ArrayList<ArrayList<String>>) ObjectSerializer
					.deserialize(prefs.getString(TASKS, ObjectSerializer
							.serialize(new ArrayList<ArrayList<String>>())));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return currentTasks;
	}

	public static void saveSuperStrings(Context mContext,
			ArrayList<ArrayList<String>> currentTasks, String TASKS) {
		assert (null != currentTasks);
		if (null == currentTasks) {
			currentTasks = new ArrayList<ArrayList<String>>();
		}
		SharedPreferences prefs = mContext.getSharedPreferences(
				SHARED_PREFS_FILE, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		try {
			editor.putString(TASKS, ObjectSerializer.serialize(currentTasks));
		} catch (IOException e) {
			e.printStackTrace();
		}
		editor.commit();
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<String> retStrings(Context mContext, String TASKS) {
		ArrayList<String> currentTasks = null;
		if (null == currentTasks) {
			currentTasks = new ArrayList<String>();
		}

		// load tasks from preference
		SharedPreferences prefs = mContext.getSharedPreferences(
				SHARED_PREFS_FILE, Context.MODE_PRIVATE);

		try {
			currentTasks = (ArrayList<String>) ObjectSerializer
					.deserialize(prefs.getString(TASKS,
							ObjectSerializer.serialize(new ArrayList<String>())));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return currentTasks;
	}

	public static void saveStrings(Context mContext,
			ArrayList<String> currentTasks, String TASKS) {
		assert (null != currentTasks);
		if (null == currentTasks) {
			currentTasks = new ArrayList<String>();
		}
		SharedPreferences prefs = mContext.getSharedPreferences(
				SHARED_PREFS_FILE, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		try {
			editor.putString(TASKS, ObjectSerializer.serialize(currentTasks));
		} catch (IOException e) {
			e.printStackTrace();
		}
		editor.commit();
	}

	public static ArrayList<AlarmItems> retAlarms(Context mContext) {
		ArrayList<String> ringtone = retStrings(mContext, RINGTONE);
		ArrayList<String> label = retStrings(mContext, LABEL);
		ArrayList<ArrayList<String>> days = retSuperStrings(mContext, DAYS);
		ArrayList<Boolean> vibrate = retBooleans(mContext, VIBRATE);
		ArrayList<Boolean> enabled = retBooleans(mContext, ENABLED);
		ArrayList<Integer> level = retIntegers(mContext, LEVEL);
		ArrayList<Boolean> repeat = retBooleans(mContext, REPEAT);

		ArrayList<AlarmItems> ai = new ArrayList<AlarmItems>();

		for (int i = 0; i < ringtone.size(); i++) {
			ai.add(new AlarmItems(level.get(i), enabled.get(i), label.get(i),
					days.get(i), ringtone.get(i), vibrate.get(i), repeat.get(i)));
		}

		return ai;

	}

	public static void saveAlarms(Context mContext, ArrayList<AlarmItems> alarms) {
		ArrayList<String> ringtone = new ArrayList<String>();
		ArrayList<String> label = new ArrayList<String>();
		ArrayList<ArrayList<String>> days = new ArrayList<ArrayList<String>>();
		ArrayList<Boolean> vibrate = new ArrayList<Boolean>();
		ArrayList<Boolean> enabled = new ArrayList<Boolean>();
		ArrayList<Integer> level = new ArrayList<Integer>();
		ArrayList<Boolean> repeat = new ArrayList<Boolean>();

		for (int i = 0; i < alarms.size(); i++) {
			ringtone.add(alarms.get(i).getRingTone());
			label.add(alarms.get(i).getLabel());
			days.add(alarms.get(i).getDays());
			vibrate.add(alarms.get(i).getVibrate());
			enabled.add(alarms.get(i).getEnabled());
			level.add(alarms.get(i).getLevel());
			repeat.add(alarms.get(i).getRepeat());
		}

		saveBooleans(mContext, repeat, REPEAT);
		saveBooleans(mContext, enabled, ENABLED);
		saveBooleans(mContext, vibrate, VIBRATE);
		saveIntegers(mContext, level, LEVEL);
		saveStrings(mContext, label, LABEL);
		saveStrings(mContext, ringtone, RINGTONE);
		saveSuperStrings(mContext, days, DAYS);
	}

	public static ArrayList<String> getRingtones(ArrayList<AlarmItems> alarms) {
		ArrayList<String> objs = new ArrayList<String>();

		for (int i = 0; i < alarms.size(); i++) {
			objs.add(alarms.get(i).getRingTone());
		}

		return objs;
	}

	public static ArrayList<String> getLabels(ArrayList<AlarmItems> alarms) {
		ArrayList<String> objs = new ArrayList<String>();

		for (int i = 0; i < alarms.size(); i++) {
			objs.add(alarms.get(i).getLabel());
		}

		return objs;
	}

	public static ArrayList<Boolean> getEnabled(ArrayList<AlarmItems> alarms) {
		ArrayList<Boolean> objs = new ArrayList<Boolean>();

		for (int i = 0; i < alarms.size(); i++) {
			objs.add(alarms.get(i).getEnabled());
		}

		return objs;
	}

	public static ArrayList<Boolean> getVibrates(ArrayList<AlarmItems> alarms) {
		ArrayList<Boolean> objs = new ArrayList<Boolean>();

		for (int i = 0; i < alarms.size(); i++) {
			objs.add(alarms.get(i).getVibrate());
		}

		return objs;
	}

	public static ArrayList<ArrayList<String>> getDays(
			ArrayList<AlarmItems> alarms) {
		ArrayList<ArrayList<String>> objs = new ArrayList<ArrayList<String>>();

		for (int i = 0; i < alarms.size(); i++) {
			objs.add(alarms.get(i).getDays());
		}

		return objs;
	}

	public static ArrayList<Integer> getLevels(ArrayList<AlarmItems> alarms) {
		ArrayList<Integer> objs = new ArrayList<Integer>();

		for (int i = 0; i < alarms.size(); i++) {
			objs.add(alarms.get(i).getLevel());
		}

		return objs;
	}

	public static ArrayList<Boolean> getRepeat(ArrayList<AlarmItems> alarms) {
		ArrayList<Boolean> objs = new ArrayList<Boolean>();

		for (int i = 0; i < alarms.size(); i++) {
			objs.add(alarms.get(i).getRepeat());
		}

		return objs;
	}

	public static void saveEnabled(ArrayList<AlarmItems> alarms,
			Context mContext) {
		ArrayList<Boolean> enabled = new ArrayList<Boolean>();
		for (int i = 0; i < alarms.size(); i++) {
			enabled.add(alarms.get(i).getEnabled());
		}
		saveBooleans(mContext, enabled, ENABLED);
	}

	public static void saveRepeat(ArrayList<AlarmItems> alarms, Context mContext) {
		ArrayList<Boolean> enabled = new ArrayList<Boolean>();
		for (int i = 0; i < alarms.size(); i++) {
			enabled.add(alarms.get(i).getRepeat());
		}
		saveBooleans(mContext, enabled, REPEAT);
	}

	public static void saveVibrate(ArrayList<AlarmItems> alarms,
			Context mContext) {
		ArrayList<Boolean> enabled = new ArrayList<Boolean>();
		for (int i = 0; i < alarms.size(); i++) {
			enabled.add(alarms.get(i).getVibrate());
		}
		saveBooleans(mContext, enabled, VIBRATE);
	}

	public static void saveLevel(ArrayList<AlarmItems> alarms, Context mContext) {
		ArrayList<Integer> enabled = new ArrayList<Integer>();
		for (int i = 0; i < alarms.size(); i++) {
			enabled.add(alarms.get(i).getLevel());
		}
		saveIntegers(mContext, enabled, LEVEL);
	}

	public static void saveRingtone(ArrayList<AlarmItems> alarms,
			Context mContext) {
		ArrayList<String> enabled = new ArrayList<String>();
		for (int i = 0; i < alarms.size(); i++) {
			enabled.add(alarms.get(i).getRingTone());
		}
		saveStrings(mContext, enabled, RINGTONE);
	}

	public static void saveLabels(ArrayList<AlarmItems> alarms, Context mContext) {
		ArrayList<String> enabled = new ArrayList<String>();
		for (int i = 0; i < alarms.size(); i++) {
			enabled.add(alarms.get(i).getLabel());
		}
		saveStrings(mContext, enabled, LABEL);
	}

	public static void saveDays(ArrayList<AlarmItems> alarms, Context mContext) {
		ArrayList<ArrayList<String>> enabled = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < alarms.size(); i++) {
			enabled.add(alarms.get(i).getDays());
		}
		saveSuperStrings(mContext, enabled, DAYS);
	}

}
