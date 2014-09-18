package saphion.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceImpl {

	String prefName;

	public PreferenceImpl() {
		prefName = "saphion.batterycaster_preferences";
	}

	public PreferenceImpl(String prefName) {
		this.prefName = prefName;
	}

	public synchronized SharedPreferences getSharedPreferences(Context mContext) {
		
		
		return mContext.getApplicationContext().getSharedPreferences(prefName,
				Context.MODE_MULTI_PROCESS);
	}

	public void set(Context mContext, String key, Object value) {
		if (key == null)
			return;
		SharedPreferences.Editor mPrefEditor = getSharedPreferences(mContext)
				.edit();
		if (value instanceof Integer) {
			mPrefEditor.putInt(key, (Integer) value);
		} else if (value instanceof String) {
			mPrefEditor.putString(key, (String) value);
		} else if (value instanceof Long) {
			mPrefEditor.putLong(key, (Long) value);
		} else if (value instanceof Boolean) {
			mPrefEditor.putBoolean(key, (Boolean) value);
		} else if (value instanceof Float) {
			mPrefEditor.putFloat(key, (Float) value);
		}
		synchronized (mPrefEditor) {
			mPrefEditor.commit();
		}
	}
	
	public Object get(Context mContext, String key, Object defVal){
		return null;
	}
}
