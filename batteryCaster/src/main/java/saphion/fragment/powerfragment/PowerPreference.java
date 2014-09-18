package saphion.fragment.powerfragment;

import java.io.IOException;
import java.util.ArrayList;

import saphion.utils.ObjectSerializer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PowerPreference {

	public final static String SHARED_PREFS_FILE = "powerprefs";
	public final static String WIFI = "wi";
	public final static String BTOOTH = "bt";
	public final static String DATA = "d";
	public final static String AMODE = "amd";
	public final static String SYNC = "snc";
	public static final String BNESS = "bns";
	public static final String PROFN = "pnm";
	public static final String HFBACK = "hfbk";
	public static final String AROTATE = "aRt";
	public static final String RINGMODE = "rMd";
	public static final String SCREENTIMEOUT = "sTt";
	public static final String TRIGGER = "tgr";
	public static final String TRIGGER_VAL = "tgrv";
	public static final String S_OFF_MDATA = "somd";
	public static final String S_OFF_WIFI = "somw";
	public static final String S_OFF_INT_MDATA = "soimd";
	public static final String S_OFF_INT_WIFI = "soimw";
	public static final String TRIGGER_MODE = "tmode";

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

	public static ArrayList<PowerProfItems> retPower(Context mContext) {

		ArrayList<String> pname = retStrings(mContext, PROFN);
		ArrayList<Integer> bness = retIntegers(mContext, BNESS);
		ArrayList<Boolean> wifi = retBooleans(mContext, WIFI);
		ArrayList<Boolean> data = retBooleans(mContext, DATA);
		ArrayList<Boolean> btooth = retBooleans(mContext, BTOOTH);
		ArrayList<Boolean> amode = retBooleans(mContext, AMODE);
		ArrayList<Boolean> sync = retBooleans(mContext, SYNC);
		ArrayList<Boolean> hfback = retBooleans(mContext, HFBACK);
		ArrayList<Boolean> aRotate = retBooleans(mContext, AROTATE);
		ArrayList<Integer> ringMode = retIntegers(mContext, RINGMODE);
		ArrayList<String> screenTimeout = retStrings(mContext, SCREENTIMEOUT);

		ArrayList<Boolean> trigger = retBooleans(mContext, TRIGGER);
		ArrayList<Boolean> s_off_mdata = retBooleans(mContext, S_OFF_MDATA);
		ArrayList<Boolean> s_off_wifi = retBooleans(mContext, S_OFF_WIFI);

		ArrayList<Integer> trigger_val = retIntegers(mContext, TRIGGER_VAL);
		ArrayList<Integer> s_off_int_mdata = retIntegers(mContext,
				S_OFF_INT_MDATA);
		ArrayList<Integer> s_off_int_wifi = retIntegers(mContext,
				S_OFF_INT_WIFI);
		
		ArrayList<Integer> t_mode = retIntegers(mContext,
				TRIGGER_MODE);

		ArrayList<PowerProfItems> ai = new ArrayList<PowerProfItems>();

		for (int i = 0; i < sync.size(); i++) {
			ai.add(new PowerProfItems(wifi.get(i), data.get(i), btooth.get(i),
					amode.get(i), sync.get(i), bness.get(i), pname.get(i),
					hfback.get(i), aRotate.get(i), ringMode.get(i),
					screenTimeout.get(i), trigger.get(i), trigger_val.get(i),
					s_off_int_mdata.get(i), s_off_int_wifi.get(i), s_off_mdata
							.get(i), s_off_wifi.get(i), t_mode.get(i)));
		}

		return ai;

	}

	public static void savePower(Context mContext,
			ArrayList<PowerProfItems> power) {
		ArrayList<Integer> bness = new ArrayList<Integer>();
		ArrayList<Boolean> wifi = new ArrayList<Boolean>();
		ArrayList<Boolean> data = new ArrayList<Boolean>();
		ArrayList<Boolean> btooth = new ArrayList<Boolean>();
		ArrayList<Boolean> amode = new ArrayList<Boolean>();
		ArrayList<Boolean> sync = new ArrayList<Boolean>();
		ArrayList<String> pname = new ArrayList<String>();
		ArrayList<Boolean> hfback = new ArrayList<Boolean>();
		ArrayList<Boolean> aRotate = new ArrayList<Boolean>();
		ArrayList<Integer> ringMode = new ArrayList<Integer>();
		ArrayList<String> screenTimeout = new ArrayList<String>();

		ArrayList<Boolean> trigger = new ArrayList<Boolean>();
		ArrayList<Boolean> s_off_mdata = new ArrayList<Boolean>();
		ArrayList<Boolean> s_off_wifi = new ArrayList<Boolean>();

		ArrayList<Integer> trigger_val = new ArrayList<Integer>();
		ArrayList<Integer> s_off_int_mdata = new ArrayList<Integer>();
		ArrayList<Integer> s_off_int_wifi = new ArrayList<Integer>();
		ArrayList<Integer> t_mode = new ArrayList<Integer>();

		for (int i = 0; i < power.size(); i++) {
			bness.add(power.get(i).getBrightness());
			wifi.add(power.get(i).getWifi());
			data.add(power.get(i).getData());
			btooth.add(power.get(i).getBluetooth());
			amode.add(power.get(i).getAmode());
			sync.add(power.get(i).getSync());
			pname.add(power.get(i).getProfileName());
			hfback.add(power.get(i).getHapticFeedback());
			aRotate.add(power.get(i).getAutoRotate());
			ringMode.add(power.get(i).getRingMode());
			screenTimeout.add(power.get(i).getScreenTimeout());
			trigger.add(power.get(i).getTrigger());
			trigger_val.add(power.get(i).getTiggerLevel());
			s_off_mdata.add(power.get(i).getS_Off_mdata());
			s_off_wifi.add(power.get(i).getS_Off_wifi());
			s_off_int_mdata.add(power.get(i).getS_Off_int_mdata());
			s_off_int_wifi.add(power.get(i).getS_Off_int_wifi());
			t_mode.add(power.get(i).getTriggerMode());
		}

		saveIntegers(mContext, bness, BNESS);
		saveIntegers(mContext, ringMode, RINGMODE);

		saveStrings(mContext, pname, PROFN);
		saveStrings(mContext, screenTimeout, SCREENTIMEOUT);

		saveBooleans(mContext, hfback, HFBACK);
		saveBooleans(mContext, aRotate, AROTATE);
		saveBooleans(mContext, wifi, WIFI);
		saveBooleans(mContext, data, DATA);
		saveBooleans(mContext, amode, AMODE);
		saveBooleans(mContext, btooth, BTOOTH);
		saveBooleans(mContext, sync, SYNC);

		saveBooleans(mContext, s_off_wifi, S_OFF_WIFI);
		saveBooleans(mContext, s_off_mdata, S_OFF_MDATA);
		saveBooleans(mContext, trigger, TRIGGER);

		saveIntegers(mContext, s_off_int_mdata, S_OFF_INT_MDATA);
		saveIntegers(mContext, s_off_int_wifi, S_OFF_INT_WIFI);
		saveIntegers(mContext, trigger_val, TRIGGER_VAL);
		saveIntegers(mContext, t_mode, TRIGGER_MODE);

	}

	public static ArrayList<String> getProfileName(
			ArrayList<PowerProfItems> alarms) {
		ArrayList<String> objs = new ArrayList<String>();

		for (int i = 0; i < alarms.size(); i++) {
			objs.add(alarms.get(i).getProfileName());
		}

		return objs;
	}

	public static ArrayList<String> getScreenTimeout(
			ArrayList<PowerProfItems> alarms) {
		ArrayList<String> objs = new ArrayList<String>();

		for (int i = 0; i < alarms.size(); i++) {
			objs.add(alarms.get(i).getScreenTimeout());
		}

		return objs;
	}

	public static ArrayList<Boolean> getARotate(ArrayList<PowerProfItems> alarms) {
		ArrayList<Boolean> objs = new ArrayList<Boolean>();

		for (int i = 0; i < alarms.size(); i++) {
			objs.add(alarms.get(i).getAutoRotate());
		}

		return objs;
	}

	public static ArrayList<Boolean> getHFBack(ArrayList<PowerProfItems> alarms) {
		ArrayList<Boolean> objs = new ArrayList<Boolean>();

		for (int i = 0; i < alarms.size(); i++) {
			objs.add(alarms.get(i).getHapticFeedback());
		}

		return objs;
	}

	public static ArrayList<Boolean> getWifi(ArrayList<PowerProfItems> alarms) {
		ArrayList<Boolean> objs = new ArrayList<Boolean>();

		for (int i = 0; i < alarms.size(); i++) {
			objs.add(alarms.get(i).getWifi());
		}

		return objs;
	}

	public static ArrayList<Boolean> getData(ArrayList<PowerProfItems> alarms) {
		ArrayList<Boolean> objs = new ArrayList<Boolean>();

		for (int i = 0; i < alarms.size(); i++) {
			objs.add(alarms.get(i).getData());
		}

		return objs;
	}

	public static ArrayList<Integer> getBrightness(
			ArrayList<PowerProfItems> alarms) {
		ArrayList<Integer> objs = new ArrayList<Integer>();

		for (int i = 0; i < alarms.size(); i++) {
			objs.add(alarms.get(i).getBrightness());
		}

		return objs;
	}

	public static ArrayList<Integer> getRingMode(
			ArrayList<PowerProfItems> alarms) {
		ArrayList<Integer> objs = new ArrayList<Integer>();

		for (int i = 0; i < alarms.size(); i++) {
			objs.add(alarms.get(i).getRingMode());
		}

		return objs;
	}

	public static ArrayList<Boolean> getBtooth(ArrayList<PowerProfItems> alarms) {
		ArrayList<Boolean> objs = new ArrayList<Boolean>();

		for (int i = 0; i < alarms.size(); i++) {
			objs.add(alarms.get(i).getBluetooth());
		}

		return objs;
	}

	public static ArrayList<Boolean> getAmode(ArrayList<PowerProfItems> alarms) {
		ArrayList<Boolean> objs = new ArrayList<Boolean>();

		for (int i = 0; i < alarms.size(); i++) {
			objs.add(alarms.get(i).getAmode());
		}

		return objs;
	}

	public static ArrayList<Boolean> getSync(ArrayList<PowerProfItems> alarms) {
		ArrayList<Boolean> objs = new ArrayList<Boolean>();

		for (int i = 0; i < alarms.size(); i++) {
			objs.add(alarms.get(i).getSync());
		}

		return objs;
	}

	public static void saveWifi(ArrayList<PowerProfItems> alarms,
			Context mContext) {
		ArrayList<Boolean> enabled = new ArrayList<Boolean>();
		for (int i = 0; i < alarms.size(); i++) {
			enabled.add(alarms.get(i).getWifi());
		}
		saveBooleans(mContext, enabled, WIFI);
	}

	public static void saveHFBack(ArrayList<PowerProfItems> alarms,
			Context mContext) {
		ArrayList<Boolean> enabled = new ArrayList<Boolean>();
		for (int i = 0; i < alarms.size(); i++) {
			enabled.add(alarms.get(i).getHapticFeedback());
		}
		saveBooleans(mContext, enabled, HFBACK);
	}

	public static void saveARotate(ArrayList<PowerProfItems> alarms,
			Context mContext) {
		ArrayList<Boolean> enabled = new ArrayList<Boolean>();
		for (int i = 0; i < alarms.size(); i++) {
			enabled.add(alarms.get(i).getAutoRotate());
		}
		saveBooleans(mContext, enabled, AROTATE);
	}

	public static void saveData(ArrayList<PowerProfItems> alarms,
			Context mContext) {
		ArrayList<Boolean> enabled = new ArrayList<Boolean>();
		for (int i = 0; i < alarms.size(); i++) {
			enabled.add(alarms.get(i).getData());
		}
		saveBooleans(mContext, enabled, DATA);
	}

	public static void saveBtooth(ArrayList<PowerProfItems> alarms,
			Context mContext) {
		ArrayList<Boolean> enabled = new ArrayList<Boolean>();
		for (int i = 0; i < alarms.size(); i++) {
			enabled.add(alarms.get(i).getBluetooth());
		}
		saveBooleans(mContext, enabled, BTOOTH);
	}

	public static void saveAmode(ArrayList<PowerProfItems> alarms,
			Context mContext) {
		ArrayList<Boolean> enabled = new ArrayList<Boolean>();
		for (int i = 0; i < alarms.size(); i++) {
			enabled.add(alarms.get(i).getAmode());
		}
		saveBooleans(mContext, enabled, AMODE);
	}

	public static void saveSync(ArrayList<PowerProfItems> alarms,
			Context mContext) {
		ArrayList<Boolean> enabled = new ArrayList<Boolean>();
		for (int i = 0; i < alarms.size(); i++) {
			enabled.add(alarms.get(i).getSync());
		}
		saveBooleans(mContext, enabled, SYNC);
	}

	public static void saveBrightness(ArrayList<PowerProfItems> alarms,
			Context mContext) {
		ArrayList<Integer> enabled = new ArrayList<Integer>();
		for (int i = 0; i < alarms.size(); i++) {
			enabled.add(alarms.get(i).getBrightness());
		}
		saveIntegers(mContext, enabled, BNESS);
	}

	public static void saveRingMode(ArrayList<PowerProfItems> alarms,
			Context mContext) {
		ArrayList<Integer> enabled = new ArrayList<Integer>();
		for (int i = 0; i < alarms.size(); i++) {
			enabled.add(alarms.get(i).getRingMode());
		}
		saveIntegers(mContext, enabled, RINGMODE);
	}

	public static void saveProfName(ArrayList<PowerProfItems> alarms,
			Context mContext) {
		ArrayList<String> enabled = new ArrayList<String>();
		for (int i = 0; i < alarms.size(); i++) {
			enabled.add(alarms.get(i).getProfileName());
		}
		saveStrings(mContext, enabled, PROFN);
	}

	public static void saveScreenTimeout(ArrayList<PowerProfItems> alarms,
			Context mContext) {
		ArrayList<String> enabled = new ArrayList<String>();
		for (int i = 0; i < alarms.size(); i++) {
			enabled.add(alarms.get(i).getScreenTimeout());
		}
		saveStrings(mContext, enabled, SCREENTIMEOUT);
	}

}
