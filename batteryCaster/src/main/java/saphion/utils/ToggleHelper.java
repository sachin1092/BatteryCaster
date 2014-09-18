package saphion.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import saphion.fragment.alarm.alert.Intents;
import saphion.logger.Log;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.view.Window;
import android.view.WindowManager;

public class ToggleHelper {

	public static boolean isWifiEnabled(Context mContext) {
		WifiManager wifi_manager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		if (wifi_manager != null)
			return (WifiManager.WIFI_STATE_ENABLED == wifi_manager
					.getWifiState())
					|| (WifiManager.WIFI_STATE_ENABLING == wifi_manager
							.getWifiState());
		return false;
	}

	public static void toggleWifi(Context context) {
		WifiManager wifi_manager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wifi_manager != null) {
			switch (wifi_manager.getWifiState()) {
			case WifiManager.WIFI_STATE_DISABLED:
				wifi_manager.setWifiEnabled(true);
				break;
			case WifiManager.WIFI_STATE_ENABLED:
				wifi_manager.setWifiEnabled(false);
				break;
			}
		}
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static boolean isAModeEnabled(Context context) {
		int state = 0;
		try {
			if (isJBean())
				state = Settings.System.getInt(context.getContentResolver(),
						Settings.Global.AIRPLANE_MODE_ON);
			else
				state = Settings.System.getInt(context.getContentResolver(),
						Settings.System.AIRPLANE_MODE_ON);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}

		if (state == 0)
			return false;
		return true;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void toggleAirplane(Context context) {
		
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
			return;
		
		int state = 0;
		try {
			if (isJBean())
				state = Settings.System.getInt(context.getContentResolver(),
						Settings.Global.AIRPLANE_MODE_ON);
			else
				state = Settings.System.getInt(context.getContentResolver(),
						Settings.System.AIRPLANE_MODE_ON);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}

		if (state == 0) {
			if (isJBean())
				Settings.System.putInt(context.getContentResolver(),
						Settings.Global.AIRPLANE_MODE_ON, 1);
			else
				Settings.System.putInt(context.getContentResolver(),
						Settings.System.AIRPLANE_MODE_ON, 1);
			Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			intent.putExtra("state", true);
			context.sendBroadcast(intent);
		} else {
			if (isJBean())
				Settings.System.putInt(context.getContentResolver(),
						Settings.Global.AIRPLANE_MODE_ON, 0);
			else
				Settings.System.putInt(context.getContentResolver(),
						Settings.System.AIRPLANE_MODE_ON, 0);
			Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			intent.putExtra("state", false);
			context.sendBroadcast(intent);
		}
	}

	public static boolean isBluetoothEnabled(Context mContext) {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter != null)
			return adapter.getState() == BluetoothAdapter.STATE_ON;
		return false;
	}

	public static void toggleBlueTooth(Context context) {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter != null) {
			switch (adapter.getState()) {
			case BluetoothAdapter.STATE_OFF:
				adapter.enable();
				break;
			case BluetoothAdapter.STATE_ON:
				adapter.disable();
				break;
			}
		}
	}

	public static void toggleMData(Context context) throws Exception {

		final ConnectivityManager conman = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final Class<?> conmanClass = Class.forName(conman.getClass().getName());
		final Field iConnectivityManagerField = conmanClass
				.getDeclaredField("mService");
		iConnectivityManagerField.setAccessible(true);
		final Object iConnectivityManager = iConnectivityManagerField
				.get(conman);
		final Class<?> iConnectivityManagerClass = Class
				.forName(iConnectivityManager.getClass().getName());
		final Method setMobileDataEnabledMethod = iConnectivityManagerClass
				.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
		setMobileDataEnabledMethod.setAccessible(true);

		if (isDataEnable(context))
			setMobileDataEnabledMethod.invoke(iConnectivityManager, false);
		else
			setMobileDataEnabledMethod.invoke(iConnectivityManager, true);

	}

	public static boolean isDataEnable(Context context) {

		/**
		 * @author Sachin
		 * 
		 *         Latest Working one
		 * @return false if unconfirmed
		 */
		Object connectivityService = context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		ConnectivityManager cm = (ConnectivityManager) connectivityService;

		try {
			Class<?> c = Class.forName(cm.getClass().getName());
			Method m = c.getDeclaredMethod("getMobileDataEnabled");
			m.setAccessible(true);
			return (Boolean) m.invoke(cm);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		/**
		 * Second one final ConnectivityManager connMgr = (ConnectivityManager)
		 * context .getSystemService(Context.CONNECTIVITY_SERVICE); final
		 * android.net.NetworkInfo wifi = connMgr
		 * .getNetworkInfo(ConnectivityManager.TYPE_WIFI); final
		 * android.net.NetworkInfo mobile = connMgr
		 * .getNetworkInfo(ConnectivityManager.TYPE_MOBILE); return
		 * mobile.isConnected();
		 */

		/**
		 * Oldest TelephonyManager tmanager = (TelephonyManager) context
		 * .getSystemService(Context.TELEPHONY_SERVICE); return
		 * (tmanager.getDataState() == TelephonyManager.DATA_CONNECTED);
		 */
	}

	public static void toggleSync() {
		ContentResolver.setMasterSyncAutomatically(!isSyncEnabled());

	}

	public static boolean isSyncEnabled() {
		return ContentResolver.getMasterSyncAutomatically();// .setSyncAutomatically(account,
															// authority,
															// true/false);
	}

	public static boolean isJBean() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			return true;
		return false;
	}

	public static final int MINIMUM_BACKLIGHT = 18;
	public static final int MAXIMUM_BACKLIGHT = 255;

	public static int getBrightnessMode(Context mContext) {
		final ContentResolver resolver = mContext.getContentResolver();
		int mode = System.getInt(resolver, System.SCREEN_BRIGHTNESS_MODE, -1);
		if (mode == System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
			return 0;
		} else if (mode == System.SCREEN_BRIGHTNESS_MODE_MANUAL) {
			int brightness = System.getInt(resolver, System.SCREEN_BRIGHTNESS,
					-1);
			if (brightness <= MINIMUM_BACKLIGHT) {
				return 1;
			} else if (brightness == MAXIMUM_BACKLIGHT) {
				return 3;
			} else {
				return 2;
			}
		} else {
			return 2;
		}
	}

	public static int getBrightness(Context mContext) {
		final ContentResolver resolver = mContext.getContentResolver();
		int mode = System.getInt(resolver, System.SCREEN_BRIGHTNESS_MODE, -1);
		if (mode == System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
			return -99;
		} else {
			return System.getInt(resolver, System.SCREEN_BRIGHTNESS, -1);
		}
	}

	public static void toggleAutoBrightness(Context mContext, Window window) {
		int mode;
		final ContentResolver resolver = mContext.getContentResolver();
		mode = System.getInt(resolver, System.SCREEN_BRIGHTNESS_MODE, -1);
		WindowManager.LayoutParams lp;
		switch (getBrightnessMode(mContext)) {
		case 0:
			mode = System.SCREEN_BRIGHTNESS_MODE_MANUAL;
			System.putInt(resolver, System.SCREEN_BRIGHTNESS_MODE, mode);
			System.putInt(resolver, System.SCREEN_BRIGHTNESS, 1);
			// window.getAttributes().screenBrightness = 0.1f;
			Log.d("Switching to manual mode with brightness "
					+ window.getAttributes().screenBrightness + " & "
					+ System.getInt(resolver, System.SCREEN_BRIGHTNESS, -1));
			lp = window.getAttributes();
			lp.screenBrightness = 0.1f;
			window.setAttributes(lp);
			break;
		case 1:
			mode = System.SCREEN_BRIGHTNESS_MODE_MANUAL;
			System.putInt(resolver, System.SCREEN_BRIGHTNESS_MODE, mode);
			System.putInt(resolver, System.SCREEN_BRIGHTNESS,
					MAXIMUM_BACKLIGHT / 2);
			window.getAttributes().screenBrightness = 0.5f;
			Log.d("Switching to manual mode with brightness "
					+ window.getAttributes().screenBrightness + " & "
					+ System.getInt(resolver, System.SCREEN_BRIGHTNESS, -1));
			lp = window.getAttributes();
			lp.screenBrightness = 0.5f;
			window.setAttributes(lp);
			break;
		case 2:
			mode = System.SCREEN_BRIGHTNESS_MODE_MANUAL;
			System.putInt(resolver, System.SCREEN_BRIGHTNESS_MODE, mode);
			window.getAttributes().screenBrightness = 1f;
			System.putInt(resolver, System.SCREEN_BRIGHTNESS, MAXIMUM_BACKLIGHT);
			Log.d("Switching to manual mode with brightness "
					+ window.getAttributes().screenBrightness + " & "
					+ System.getInt(resolver, System.SCREEN_BRIGHTNESS, -1));
			lp = window.getAttributes();
			lp.screenBrightness = 1f;
			window.setAttributes(lp);
			break;
		case 3:
			mode = System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
			System.putInt(resolver, System.SCREEN_BRIGHTNESS_MODE, mode);
			Log.d("Switching to automatic mode with brightness "
					+ window.getAttributes().screenBrightness + " & "
					+ System.getInt(resolver, System.SCREEN_BRIGHTNESS, -1));
			lp = window.getAttributes();
			lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
			window.setAttributes(lp);
			break;
		}

	}

	public static void setBrightness(Context mContext, Window window, int bness) {
		final ContentResolver resolver = mContext.getContentResolver();
		int mode = System.getInt(resolver, System.SCREEN_BRIGHTNESS_MODE, -1);
		WindowManager.LayoutParams lp;
		if (bness == -99) {
			mode = System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
			System.putInt(resolver, System.SCREEN_BRIGHTNESS_MODE, mode);
			lp = window.getAttributes();
			lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
			window.setAttributes(lp);
		} else {
			mode = System.SCREEN_BRIGHTNESS_MODE_MANUAL;
			System.putInt(resolver, System.SCREEN_BRIGHTNESS_MODE, mode);
			window.getAttributes().screenBrightness = (float) ((float) (bness) / 255f);
			System.putInt(resolver, System.SCREEN_BRIGHTNESS, bness);
			lp = window.getAttributes();
			lp.screenBrightness = (float) ((float) (bness) / 255f);
			window.setAttributes(lp);
		}
	}

	/**
	 * set screen off timeout
	 * 
	 * @param screenOffTimeout
	 * @param int 0~6
	 * @param 0 - 15 sec
	 * @param 1 - 30 sec
	 * @param 2 - 1 min
	 * @param 3 - 2 min
	 * @param 4 - 10 min
	 * @param 5 - 30 min
	 * @param -1 - Never
	 * 
	 */
	public static void setScreenTimeout(Context mContext, int screenOffTimeout) {
		int time;
		switch (screenOffTimeout) {
		case 0:
			time = 15000;
			break;
		case 1:
			time = 30000;
			break;
		case 2:
			time = 60000;
			break;
		case 3:
			time = 120000;
			break;
		case 4:
			time = 600000;
			break;
		case 5:
			time = 1800000;
			break;
		case -1:
			time = -1;
		default:
			time = -1;
		}
		// Log.Toast(mContext, "timeout to " + , length)
		android.provider.Settings.System.putInt(mContext.getContentResolver(),
				Settings.System.SCREEN_OFF_TIMEOUT, time);
	}
	
	public static String getScreenTimeOutIndex(Context baseContext) {
		switch (getScreenTimeOut(baseContext)) {
		case 15000:
			return "0";
		case 30000:
			return "1";
		case 60000:
			return "2";
		case 120000:
			return "3";
		case 600000:
			return "4";
		case 1800000:
			return "5";
		case -1:
			return "-1";
		default:
			return "-1"; 
		}
	}

	public static int getScreenTimeOut(Context mContext) {
		return android.provider.Settings.System.getInt(
				mContext.getContentResolver(),
				Settings.System.SCREEN_OFF_TIMEOUT, -1);
	}

	public static boolean isHapticFback(Context mContext) {
		try {
			return Settings.System.getInt(mContext.getContentResolver(),
					Settings.System.HAPTIC_FEEDBACK_ENABLED) == 1;
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void toggleHapticFeedback(Context mContext) {
		Settings.System.putInt(mContext.getContentResolver(),
				Settings.System.HAPTIC_FEEDBACK_ENABLED,
				!isHapticFback(mContext) ? 1 : 0);
	}

	public static boolean isRotationEnabled(Context mContext) {
		return Settings.System.getInt(mContext.getContentResolver(),
				Settings.System.ACCELEROMETER_ROTATION, 0) != 0;
	}

	public static void toggleRotation(Context mContext) {
		Settings.System.putInt(mContext.getContentResolver(),
				Settings.System.ACCELEROMETER_ROTATION,
				!isRotationEnabled(mContext) ? 1 : 0);
		mContext.sendBroadcast(new Intent(Intents.SWITCHER_NOTI));
	}

	public static int getRingerMode(Context mContext) {
		AudioManager am = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
		return am.getRingerMode();
	}

	public static void setRingerMode(Context mContext, int x) {
		AudioManager am = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
		switch (x) {
		case 0:
			am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			break;
		case 1:
			am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			break;
		case 2:
			am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			break;
		/*case 3:
			am.setRingerMode(AudioManager.RINGER_MODE_);
			break;*/
		}
	}

	

}
