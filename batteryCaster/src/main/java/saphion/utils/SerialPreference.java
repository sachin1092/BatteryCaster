package saphion.utils;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SerialPreference {

	public final static String SHARED_PREFS_FILE = "prefs";

	@SuppressWarnings("unchecked")
	public static ArrayList<Double> retPrefs(Context mContext, String TASKS) {
		ArrayList<Double> currentTasks = null;
		if (null == currentTasks) {
			currentTasks = new ArrayList<Double>();
		}

		// load tasks from preference
		SharedPreferences prefs = mContext.getSharedPreferences(
				SHARED_PREFS_FILE, Context.MODE_PRIVATE);

		try {
			currentTasks = (ArrayList<Double>) ObjectSerializer
					.deserialize(prefs.getString(TASKS,
							ObjectSerializer.serialize(new ArrayList<Double>())));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return currentTasks;
	}

	public static void savePrefs(Context mContext, ArrayList<Double> currentTasks, String TASKS) {
		assert (null != currentTasks);
		if (null == currentTasks) {
			currentTasks = new ArrayList<Double>();
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

}
