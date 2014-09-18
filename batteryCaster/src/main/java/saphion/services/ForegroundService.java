/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package saphion.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;
//import org.jraf.android.backport.switchwidget.Switch;

import me.yugy.github.lswitchbackport.library.Switch;
import saphion.batterycaster.R;
import saphion.batterycaster.providers.Alarm;
import saphion.batterylib.DateUtils;
import saphion.fragment.alarm.alert.Intents;
import saphion.fragment.powerfragment.EditPower;
import saphion.fragment.powerfragment.PowerPreference;
import saphion.fragment.powerfragment.PowerProfItems;
import saphion.fragments.TabNavigation;
import saphion.logger.Log;
import saphion.togglehelper.hotspot.WifiApManager;
import saphion.togglehelpers.torch.CameraDevice;
import saphion.togglehelpers.torch.TorchService;
import saphion.togglercvrs.AmodeRcvr;
import saphion.togglercvrs.ArotateRcvr;
import saphion.togglercvrs.BrightnessRcvr;
import saphion.togglercvrs.BtoothRcvr;
import saphion.togglercvrs.HotspotRcvr;
import saphion.togglercvrs.MdataRcvr;
import saphion.togglercvrs.PowerProfileRcvr;
import saphion.togglercvrs.SyncRcvr;
import saphion.togglercvrs.ToggleDialog;
import saphion.togglercvrs.WifiRcvr;
import saphion.utils.Constants;
import saphion.utils.Functions;
import saphion.utils.NewFunctions;
import saphion.utils.PreferenceHelper;
import saphion.utils.SerialPreference;
import saphion.utils.TimeFuncs;
import saphion.utils.ToggleHelper;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ForegroundService extends Service {
	public static final String ACTION_FOREGROUND = "saphion.batterycaster.FOREGROUND";
	static final String ACTION_BACKGROUND = "saphion.batterycaster.BACKGROUND";

	private static final Class<?>[] mSetForegroundSignature = new Class[] { boolean.class };
	private static final Class<?>[] mStartForegroundSignature = new Class[] {
			int.class, Notification.class };
	private static final Class<?>[] mStopForegroundSignature = new Class[] { boolean.class };

	private NotificationManager mNM;
	private Method mSetForeground;
	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mSetForegroundArgs = new Object[1];
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];
	boolean isconnected = true;
	public static final int notiID = 1071992;
	int level = 0;
	public final String PREF_NAME = "saphion.batterycaster_preferences";
	SharedPreferences mPref;

	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			// String action = intent.getAction();

			if (intent.getAction() == Intents.TORCH_OFF) {
				// Toast.makeText(getBaseContext(), "Torch off",
				// Toast.LENGTH_LONG).show();
				updateTorchIcon(false);
				// return;
			}

			if (intent.getAction() == Intents.TORCH_ON) {
				// Toast.makeText(getBaseContext(), "Torch on",
				// Toast.LENGTH_LONG).show();
				updateTorchIcon(true);
				// return;
			}

			int rawlevel = intent.getIntExtra("level", -1);
			double scale = intent.getIntExtra("scale", -1);
			int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
			isconnected = (plugged == BatteryManager.BATTERY_PLUGGED_AC
					|| plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS);
			level = -1;
			if (rawlevel >= 0 && scale > 0) {
				level = (int) ((rawlevel * 100) / scale);
				Log.d("rawLevel: " + rawlevel);
				Log.d("scale: " + scale);
			}

			readbattery();
			// was commented before
			// TODO
			new UpdateEntries().execute(level);
			Intent intents = new Intent(ForegroundService.ACTION_FOREGROUND);
			intents.setClass(getBaseContext(), ForegroundService.class);
			// stopSelf();
			startService(intents);
			try {

				triggerFunc(intent.getAction());
			} catch (Exception ex) {
				Log.d("Trigger Error: " + ex.toString());
			}

			if (intent.getAction() == Intent.ACTION_SCREEN_OFF
					|| intent.getAction() == Intents.MYBAT_INTENT) {
				Log.d("Receiving Battery Intent and screen is off :)");
				handler.postDelayed(drawRunner, 500000);
			}

			if (intent.getAction() == Intent.ACTION_SCREEN_ON) {
				handler.removeCallbacks(drawRunner);
			}

		}
	};

	private final Handler handler = new Handler();
	private final Runnable drawRunner = new Runnable() {
		public void run() {
			Intent intent;
			intent = new Intent(Intents.MYBAT_INTENT);
			sendBroadcast(intent);

		}

	};

	void invokeMethod(Method method, Object[] args) {
		try {
			method.invoke(this, args);
		} catch (InvocationTargetException e) {
			// Should not happen.
			Log.d("Unable to invoke method " + e);
		} catch (IllegalAccessException e) {
			// Should not happen.
			Log.d("Unable to invoke method " + e);
		}
	}

	Runnable dataOffRunnable = new Runnable() {

		@Override
		public void run() {

			if (ToggleHelper.isDataEnable(getBaseContext())) {
				try {
					ToggleHelper.toggleMData(getBaseContext());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Log.d("dataOffRunnable with mVal: " + mVal + " and millies: "
					+ mVal * 60 * 1000);
			handler.removeCallbacks(dataOffRunnable);
			handler.removeCallbacks(dataOnRunnable);
			handler.postDelayed(dataOnRunnable, mVal * 60 * 1000);

		}
	};

	Runnable dataOnRunnable = new Runnable() {

		@Override
		public void run() {
			if (!ToggleHelper.isDataEnable(getBaseContext())) {
				try {
					ToggleHelper.toggleMData(getBaseContext());
				} catch (Exception e) {
					e.printStackTrace();
				}
				Log.d("dataOnRunnable with mVal: " + mVal);
				handler.removeCallbacks(dataOffRunnable);
				handler.removeCallbacks(dataOnRunnable);
				handler.postDelayed(dataOffRunnable, 60000);
			}

		}
	};

	Runnable wifiOffRunnable = new Runnable() {

		@Override
		public void run() {
			if (ToggleHelper.isWifiEnabled(getBaseContext())) {
				try {
					ToggleHelper.toggleWifi(getBaseContext());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			handler.removeCallbacks(wifiOffRunnable);
			handler.removeCallbacks(wifiOnRunnable);
			handler.postDelayed(wifiOnRunnable, wVal * 60 * 1000);

		}
	};

	Runnable wifiOnRunnable = new Runnable() {

		@Override
		public void run() {
			if (!ToggleHelper.isWifiEnabled(getBaseContext())) {
				try {
					ToggleHelper.toggleWifi(getBaseContext());
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.removeCallbacks(wifiOffRunnable);
				handler.removeCallbacks(wifiOnRunnable);
				handler.postDelayed(wifiOffRunnable, 20000);
			}
		}
	};

	int mVal = -99;
	int wVal = -99;

	protected void triggerFunc(String action) {
		if (action == Intent.ACTION_SCREEN_OFF) {
			PowerProfItems pItems = PowerPreference.retPower(getBaseContext())
					.get(mPref.getInt(PreferenceHelper.POSITIONS, 0));
			if (pItems.getData()) {
				if (pItems.getS_Off_mdata()) {
					mVal = pItems.getS_Off_int_mdata();

					if (ToggleHelper.isDataEnable(getBaseContext())) {
						try {
							ToggleHelper.toggleMData(getBaseContext());
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
					if (mVal != -99) {
						Log.d("Posting dataOnRunnable with mVal: " + mVal
								+ " and millies of " + (mVal * 60 * 1000));
						handler.postDelayed(dataOnRunnable, mVal * 60 * 1000);
					} else {
						Log.d("Won't post because mVal is: " + mVal);
					}
				}
			}
			if (pItems.getWifi()) {
				if (pItems.getS_Off_wifi()) {
					wVal = pItems.getS_Off_int_wifi();
					if (ToggleHelper.isWifiEnabled(getBaseContext())) {

						ToggleHelper.toggleWifi(getBaseContext());

					}
					if (wVal != -99)
						handler.postDelayed(wifiOnRunnable, wVal * 60 * 1000);
				}
			}
		} else if (action == Intent.ACTION_SCREEN_ON) {
			PowerProfItems pItems = PowerPreference.retPower(getBaseContext())
					.get(mPref.getInt(PreferenceHelper.POSITIONS, 0));
			handler.removeCallbacks(dataOffRunnable);
			handler.removeCallbacks(dataOnRunnable);
			handler.removeCallbacks(wifiOffRunnable);
			handler.removeCallbacks(wifiOnRunnable);
			if (pItems.getData()) {
				if (pItems.getS_Off_mdata()) {
					if (!ToggleHelper.isDataEnable(getBaseContext())) {
						try {
							ToggleHelper.toggleMData(getBaseContext());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			if (pItems.getWifi()) {
				if (pItems.getS_Off_wifi()) {
					if (!ToggleHelper.isWifiEnabled(getBaseContext())) {

						ToggleHelper.toggleWifi(getBaseContext());

					}
				}
			}
		}

		// Average stats processing

		// Log.Toast(getBaseContext(), "Recieved action", Toast.LENGTH_LONG);

		mPref = getSharedPreferences(PREF_NAME, MODE_MULTI_PROCESS);
		Editor mPrefEditor = mPref.edit();
		Status mStat = readBatteryStat();
		if ((mStat.getConnected() && mStat.getLevel() == 100)
				|| action == Intent.ACTION_POWER_DISCONNECTED) {

			// Log.Toast(getBaseContext(), "Disconnected", Toast.LENGTH_LONG);
			int diff;
			if ((diff = mStat.getLevel()
					- mPref.getInt(PreferenceHelper.STAT_CONNECTED_LAST_LEVEL,
							mStat.getLevel())) > 3) {
				calcStat(mStat, mPref, mPrefEditor, diff, true);
			}

			if (mPref
					.getInt(PreferenceHelper.STAT_DISCONNECTED_LAST_LEVEL, -99) == -99
					|| diff < 3) {
				mPrefEditor.putInt(
						PreferenceHelper.STAT_DISCONNECTED_LAST_LEVEL,
						mStat.getLevel());
				mPrefEditor.putString(
						PreferenceHelper.STAT_DISCONNECTED_LAST_TIME,
						TimeFuncs.getCurrentTimeStamp());
			}

		}

		if ((!mStat.getConnected() && mStat.getLevel() <= 1)
				|| action == Intent.ACTION_POWER_CONNECTED) {

			// Log.Toast(getBaseContext(), "Connected", Toast.LENGTH_LONG);

			int diff;
			if ((diff = mPref.getInt(
					PreferenceHelper.STAT_DISCONNECTED_LAST_LEVEL,
					mStat.getLevel())
					- mStat.getLevel()) > 3) {
				calcStat(mStat, mPref, mPrefEditor, diff, false);
			}

			if (mPref.getInt(PreferenceHelper.STAT_CONNECTED_LAST_LEVEL, -99) == -99
					|| diff < 3) {
				mPrefEditor.putInt(PreferenceHelper.STAT_CONNECTED_LAST_LEVEL,
						mStat.getLevel());
				mPrefEditor.putString(
						PreferenceHelper.STAT_CONNECTED_LAST_TIME,
						TimeFuncs.getCurrentTimeStamp());
			}
		}

		mPrefEditor.commit();

	}

	private void calcStat(Status mStat, SharedPreferences mPrefs,
			Editor mPrefEditor, int diff, boolean b) {

		// Step 1: retrieve previous time
		// Step 2: calculate time difference
		// Step 3: scale it to 1% by dividing it with #diff
		// Step 4: take the previous average and multiply with count
		// Step 5: add the new time difference with result from previous step
		// Step 6: increment the count
		// Step 7: divide the Step 5 result with new count
		// Step 8: save the values

		if (b) {

			long prevAvg = mPrefs.getLong(
					PreferenceHelper.STAT_CHARGING_AVGTIME, 90);
			long count = mPrefs.getLong(
					PreferenceHelper.STAT_DISCONNECTED_COUNT, 0);
			long newAvg = prevAvg;
			// retrieve previous time
			String prevTime = mPrefs.getString(
					PreferenceHelper.STAT_CONNECTED_LAST_TIME,
					TimeFuncs.getCurrentTimeStamp());
			// Calculate difference
			long timeDiff = TimeFuncs.newDiff(TimeFuncs.GetItemDate(prevTime),
					TimeFuncs.GetItemDate(TimeFuncs.getCurrentTimeStamp()));
			// scale to 1% by dividing by #diff
			if (diff != 0) {
				timeDiff = timeDiff / diff;

				// take the previous average and multiply with count
				prevAvg = prevAvg * count;
				// increment the count
				count = count + 1;
				// add the new time difference with result from previous
				// step
				// divide the Step 5 result with new count
				newAvg = (prevAvg + timeDiff) / (count);

			}

			mPrefEditor.putLong(PreferenceHelper.STAT_CHARGING_AVGTIME, newAvg);
			mPrefEditor
					.putLong(PreferenceHelper.STAT_DISCONNECTED_COUNT, count);
			mPrefEditor.putString(PreferenceHelper.STAT_DISCONNECTED_LAST_TIME,
					TimeFuncs.getCurrentTimeStamp());
			mPrefEditor.putInt(PreferenceHelper.STAT_DISCONNECTED_LAST_LEVEL,
					mStat.getLevel());

		} else {
			long prevAvg = mPrefs.getLong(
					PreferenceHelper.STAT_DISCHARGING_AVGTIME, 612);
			long count = mPrefs.getLong(PreferenceHelper.STAT_CONNECTED_COUNT,
					0);
			long newAvg = prevAvg;
			// retrieve previous time
			String prevTime = mPrefs.getString(
					PreferenceHelper.STAT_DISCONNECTED_LAST_TIME,
					TimeFuncs.getCurrentTimeStamp());
			// Calculate difference
			long timeDiff = TimeFuncs.newDiff(TimeFuncs.GetItemDate(prevTime),
					TimeFuncs.GetItemDate(TimeFuncs.getCurrentTimeStamp()));
			// scale to 1% by dividing by #diff
			if (diff != 0) {
				timeDiff = timeDiff / diff;

				// take the previous average and multiply with count
				prevAvg = prevAvg * count;
				// increment the count
				count = count + 1;
				// add the new time difference with result from previous
				// step
				// divide the Step 5 result with new count
				newAvg = (prevAvg + timeDiff) / (count);

			}

			mPrefEditor.putLong(PreferenceHelper.STAT_DISCHARGING_AVGTIME,
					newAvg);
			mPrefEditor.putLong(PreferenceHelper.STAT_CONNECTED_COUNT, count);
			mPrefEditor.putString(PreferenceHelper.STAT_CONNECTED_LAST_TIME,
					TimeFuncs.getCurrentTimeStamp());
			mPrefEditor.putInt(PreferenceHelper.STAT_CONNECTED_LAST_LEVEL,
					mStat.getLevel());
		}

		mPrefEditor.commit();

	}

	public class Status {
		private boolean isConnected;
		private int mLevel;

		public Status(int mLevel, boolean isConnected) {
			this.mLevel = mLevel;
			this.isConnected = isConnected;
		}

		public int getLevel() {
			return this.mLevel;
		}

		public boolean getConnected() {
			return this.isConnected;
		}
	}

	public Status readBatteryStat() {
		Intent batteryIntent = getBaseContext().registerReceiver(null,
				new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		int rawlevel = batteryIntent
				.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		double scale = batteryIntent
				.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		int plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED,
				-1);
		isconnected = (plugged == BatteryManager.BATTERY_PLUGGED_AC
				|| plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS);
		level = -1;
		if (rawlevel >= 0 && scale > 0) {
			level = (int) ((rawlevel * 100) / scale);
			Log.d("rawLevel: " + rawlevel);
			Log.d("scale: " + scale);
		}

		if (mPref.getBoolean(PreferenceHelper.KEY_ONE_PERCENT_HACK, false)) {
			try {
				java.io.FileReader fReader = new java.io.FileReader(
						"/sys/class/power_supply/battery/charge_counter");
				java.io.BufferedReader bReader = new java.io.BufferedReader(
						fReader);
				int charge_counter = Integer.valueOf(bReader.readLine());
				bReader.close();

				if (charge_counter > PreferenceHelper.CHARGE_COUNTER_LEGIT_MAX) {
					disableOnePercentHack("charge_counter is too big to be actual charge");
				} else {
					if (charge_counter > 100)
						charge_counter = 100;

					level = charge_counter;
				}
			} catch (java.io.FileNotFoundException e) {
				/*
				 * These error messages are only really useful to me and might
				 * as well be left hardwired here in English.
				 */
				disableOnePercentHack("charge_counter file doesn't exist");
			} catch (java.io.IOException e) {
				disableOnePercentHack("Error reading charge_counter file");
			}
		}

		return new Status(level, isconnected);
	}

	/**
	 * This is a wrapper around the new startForeground method, using the older
	 * APIs if it is not available.
	 */
	void startForegroundCompat(int id, Notification notification) {
		// If we have the new startForeground API, then use it.
		if (mStartForeground != null) {
			mStartForegroundArgs[0] = Integer.valueOf(id);
			mStartForegroundArgs[1] = notification;
			invokeMethod(mStartForeground, mStartForegroundArgs);
			return;
		}

		// Fall back on the old API.
		mSetForegroundArgs[0] = Boolean.TRUE;
		invokeMethod(mSetForeground, mSetForegroundArgs);
		if (mPref.getBoolean(PreferenceHelper.NOTIFICATION_ENABLE, true)) {
			mNM.notify(id, notification);
		} else {
			mNM.cancelAll();// .notify(id, notification);
			mNM.cancel(id);
		}

	}

	/**
	 * This is a wrapper around the new stopForeground method, using the older
	 * APIs if it is not available.
	 */
	void stopForegroundCompat(int id) {
		// If we have the new stopForeground API, then use it.
		if (mStopForeground != null) {
			mStopForegroundArgs[0] = Boolean.TRUE;
			invokeMethod(mStopForeground, mStopForegroundArgs);
			return;
		}

		// Fall back on the old API. Note to cancel BEFORE changing the
		// foreground state, since we could be killed at that point.
		mNM.cancel(id);
		mSetForegroundArgs[0] = Boolean.FALSE;
		invokeMethod(mSetForeground, mSetForegroundArgs);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {

		mPref = ForegroundService.this.getSharedPreferences(PREF_NAME,
				MODE_MULTI_PROCESS);

		// receive ACTION_BATTERY_CHANGED.
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		filter.addAction(Intent.ACTION_POWER_CONNECTED);
		filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intents.MYBAT_INTENT);
		filter.addAction(Intents.TORCH_ON);
		filter.addAction(Intents.TORCH_OFF);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
			filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			filter.addAction(ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED);
			filter.addAction(Intents.SWITCHER_INTENT);
			filter.addAction(Intents.SWITCHER_NOTI);
			filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
		}
		registerReceiver(mBroadcastReceiver, filter);

		readbattery();

		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		if (!mPref.getBoolean(PreferenceHelper.NOTIFICATION_ENABLE, true)) {
			mNM.cancelAll();// .notify(id, notification);

		}
		try {
			mStartForeground = getClass().getMethod("startForeground",
					mStartForegroundSignature);
			mStopForeground = getClass().getMethod("stopForeground",
					mStopForegroundSignature);
			return;
		} catch (NoSuchMethodException e) {
			// Running on an older platform.
			mStartForeground = mStopForeground = null;
		}
		try {
			mSetForeground = getClass().getMethod("setForeground",
					mSetForegroundSignature);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(
					"OS doesn't have Service.startForeground OR Service.setForeground!");
		}
	}

	@Override
	public void onDestroy() {
		// Make sure our notification is gone.
		unregisterReceiver(mBroadcastReceiver);

		stopForegroundCompat(R.string.foreground_service_started);
	}

	// This is the old onStart method that will be called on the pre-2.0
	// platform. On 2.0 or later we override onStartCommand() so this
	// method will not be called.
	@Override
	public void onStart(Intent intent, int startId) {
		new UpdateEntries().execute(level);
		// handleCommand(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new UpdateEntries().execute(level);
		// handleCommand(intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.

		return START_STICKY;
	}

	boolean isOn = false;

	void updateTorchIcon(boolean isON) {
		// Toast.makeText(getBaseContext(), "Torch is " + (isOn?"ON":"OFF"),
		// Toast.LENGTH_LONG).show();
		/*
		 * if (rvLargeNoti != null) {
		 * rvLargeNoti.setImageViewResource(R.id.ibtorch_large, isOn ?
		 * R.drawable.lightbulb_on : R.drawable.lightbulb_off); }
		 */
		ForegroundService.this.isOn = isON;
	}

	long reqTime = 0;
	RemoteViews rvLargeNoti;

	@SuppressWarnings("deprecation")
	void handleCommand() {

		mPref = getSharedPreferences(PREF_NAME, MODE_MULTI_PROCESS);

		// if (ACTION_FOREGROUND.equals(intent.getAction())) {
		// In this sample, we'll use the same text for the ticker and the
		// expanded notification
		// CharSequence text = getText(R.string.foreground_service_started);

		// Set the icon, scrolling text and timestamp
		try {
			handleTrigger(level);
		} catch (Exception ex) {
			Log.d(ex.toString());
		}

		RemoteViews rvNoti = new RemoteViews(getPackageName(),
				R.layout.smallnoti);

		NotificationCompat.Builder builder;/*
											 * = new NotificationCompat.Builder(
											 * getBaseContext
											 * ()).setContent(rvNoti);
											 * builder.build();
											 */

		Notification notification = new Notification(getId(level), null, 0);

		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent = null;
		Intent localIntent;
		switch (mPref.getInt(PreferenceHelper.NOTI_ONCLICK, 2)) {
		case 0:
			contentIntent = PendingIntent.getActivity(this, 0, new Intent(this,
					TabNavigation.class), 0);
			break;
		case 1:
			localIntent = new Intent();
			localIntent.setFlags(346030080);
			localIntent.setAction("android.intent.action.POWER_USAGE_SUMMARY");
			contentIntent = PendingIntent.getActivity(this, 0, localIntent, 0);
			break;
		case 2:
			contentIntent = PendingIntent.getActivity(this, 0, new Intent(this,
					Controller.class), 0);
			break;
		case 3:
			contentIntent = PendingIntent.getActivity(this, 0, new Intent(this,
					ToggleDialog.class), 0);
			break;
		}
		String prevTime = mPref.getString(PreferenceHelper.PREV_BAT_TIME,
				TimeFuncs.getCurrentTimeStamp());

		long diff = TimeFuncs.newDiff(TimeFuncs.GetItemDate(prevTime),
				TimeFuncs.GetItemDate(TimeFuncs.getCurrentTimeStamp()));

		Log.d("New diff " + diff);
		// TODO

		Log.d("Previous Date: " + TimeFuncs.GetItemDate(prevTime));
		Log.d("Current Date: "
				+ TimeFuncs.GetItemDate(TimeFuncs.getCurrentTimeStamp()));
		// Log.d( "Previous Timestamp " + level + "");
		// Log.d( "Current Timestamp " + level + "");

		String subtext;

		Log.d("Current Level " + level + "");
		Log.d("Previous Level "
				+ mPref.getInt(PreferenceHelper.PREV_BAT_LEVEL, level) + "");
		if (level < mPref.getInt(PreferenceHelper.PREV_BAT_LEVEL, level)) {

			diff = (long) (mPref.getLong(PreferenceHelper.BAT_DISCHARGE, diff));

			reqTime = diff * level;
			subtext = "Empty in " + TimeFuncs.convtohournminnday(reqTime);

			// mPref
			// .edit().putLong(PreferenceHelper.BAT_DISCHARGE, diff)
			// .commit();
			Log.d("Discharging with " + diff);

		} else {
			if (level > mPref.getInt(PreferenceHelper.PREV_BAT_LEVEL, level)) {
				if (level != 100
						&& TimeFuncs.convtohournminnday(diff * (100 - level))
								.equalsIgnoreCase("0 Minute(s)")) {
					reqTime = (long) (81 * (100 - level));
					subtext = "Full Charge in "
							+ TimeFuncs.convtohournminnday(reqTime);
				} else {
					reqTime = diff * (100 - level);
					subtext = "Full Charge in "
							+ TimeFuncs.convtohournminnday(reqTime);
					mPref.edit().putLong(PreferenceHelper.BAT_CHARGE, diff)
							.commit();
				}

				Log.d("Charging with " + diff);

			} else {

				if (isconnected) {
					reqTime = (long) (mPref.getLong(
							PreferenceHelper.BAT_CHARGE, 81) * (100 - level));
					subtext = "Full Charge in "
							+ TimeFuncs.convtohournminnday(reqTime);
					Log.d("Estimating Charging");
					// mPref
					// .edit().putLong("batcharge", diff).commit();
					Log.d("EST Charging with " + diff);

				} else {
					reqTime = (long) (mPref.getLong(
							PreferenceHelper.BAT_DISCHARGE, 792) * (level));
					subtext = "Empty in "
							+ TimeFuncs.convtohournminnday(reqTime);
					Log.d("Estimating Discharging with: "
							+ (long) (mPref.getLong(
									PreferenceHelper.BAT_DISCHARGE, 792)));
				}
			}
		}

		if (level == 100 && isconnected) {
			subtext = "Fully Charged";
			reqTime = 0;
		}

		String mainText = mPref.getString(PreferenceHelper.LAST_CHARGED,
				TimeFuncs.getCurrentTimeStamp());

		if (isconnected) {
			if (mPref.getBoolean("plugged?", true))
				mPref.edit()
						.putString(PreferenceHelper.LAST_CHARGED,
								TimeFuncs.getCurrentTimeStamp()).commit();
			String time = TimeFuncs.convtohournminnday(TimeFuncs.newDiff(
					TimeFuncs.GetItemDate(mainText),
					TimeFuncs.GetItemDate(TimeFuncs.getCurrentTimeStamp())));
			if (!time.equals("0 Minute(s)"))
				mainText = "Plugged " + time + " ago";
			else
				mainText = "Plugged " + "right now";
			mPref.edit().putBoolean("plugged?", false).commit();

		} else {
			if (!mPref.getBoolean("plugged?", true)) {
				mPref.edit().putBoolean("plugged?", true).commit();
				mPref.edit()
						.putString(PreferenceHelper.LAST_CHARGED,
								TimeFuncs.getCurrentTimeStamp()).commit();
			}

			mainText = mPref.getString(PreferenceHelper.LAST_CHARGED,
					TimeFuncs.getCurrentTimeStamp());

			String time = TimeFuncs.convtohournminnday(TimeFuncs.newDiff(
					TimeFuncs.GetItemDate(mainText),
					TimeFuncs.GetItemDate(TimeFuncs.getCurrentTimeStamp())));

			if (!time.equals("0 Minute(s)"))
				mainText = "Unplugged " + time + " ago";
			else
				mainText = "Unplugged " + "right now";
		}

		String tempsubtext = subtext;
		subtext = setNotText(subtext, mainText,
				mPref.getInt(PreferenceHelper.NOTI_SUBTEXT, 3));
		mainText = setNotText(tempsubtext, mainText,
				mPref.getInt(PreferenceHelper.NOTI_MAINTEXT, 6));

		// Set the info for the views that show in the notification panel
		int srcId = getId(level);
		builder = new NotificationCompat.Builder(this).setSmallIcon(srcId)
				.setAutoCancel(false).setOngoing(true)
				.setContentTitle(mainText).setContentText(subtext)
				.setTicker(null).setWhen(0);
		// modification

		rvNoti.setImageViewResource(R.id.notification_icon, srcId);
		rvNoti.setTextViewText(R.id.tvNotiPrevmainText, mainText);
		rvNoti.setTextViewText(R.id.tvNotiPrevsubText, subtext);

		rvLargeNoti = new RemoteViews(getPackageName(), R.layout.largenoti);

		/*if (Build.VERSION.SDK_INT != 20)*/
			builder.setContent(rvNoti);
		// Intent notificationIntent = new Intent(this,
		// ForegroundService.class);
		// contentIntent = PendingIntent.getActivity(this, 0,
		// notificationIntent,
		// PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIntent);
		switch (mPref.getInt(PreferenceHelper.NOTI_PRIORITY, 2)) {
		case 0:
			builder.setPriority(NotificationCompat.PRIORITY_HIGH);
			break;
		case 1:
			builder.setPriority(NotificationCompat.PRIORITY_MIN);
			break;
		case 2:
			builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
			break;
		case 3:
			builder.setPriority(NotificationCompat.PRIORITY_LOW);
			break;
		case 4:
			builder.setPriority(NotificationCompat.PRIORITY_MAX);
			break;
		}

		// builder.
		notification = builder.build();
		/*if (Build.VERSION.SDK_INT != 20)*/
			notification.contentView = rvNoti;

		/*if (Build.VERSION.SDK_INT == 20) {

			
			 * builder = new
			 * Notification.Builder(this).setContentTitle(mainText)
			 * .setContentText(subtext).setTicker(null).setWhen(0)
			 * .setOngoing(true).setSmallIcon(srcId)
			 * .setContentIntent(pendingIntent).addAction(call)
			 * .addAction(cancel).setAutoCancel(false);
			 

			setKitkatActions(builder);

			notification = builder.build();

		} else*/ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			rvLargeNoti.setImageViewResource(R.id.notification_icon_large,
					srcId);

			if (mPref.getBoolean(PreferenceHelper.SHOW_CHART, true)) {

				vals = new ArrayList<Double>();
				dates = new ArrayList<Double>();

				vals = SerialPreference.retPrefs(getBaseContext(),
						PreferenceHelper.BAT_VALS);
				dates = SerialPreference.retPrefs(getBaseContext(),
						PreferenceHelper.BAT_TIME);

				GraphicalView mChartView = ChartFactory.getTimeChartView(
						getBaseContext(), getDateDataset(), getRenderer(),
						"EEE, h:mm a");
				Bitmap outBitmap = Bitmap.createScaledBitmap(
						loadBitmapFromView(mChartView), getResources()
								.getDisplayMetrics().widthPixels, Functions
								.ReturnHeight(140, getBaseContext()), true);

				rvLargeNoti.setImageViewBitmap(R.id.ivchart_large, outBitmap);
				rvLargeNoti.setViewVisibility(R.id.ivchart_large, View.VISIBLE);
			} else {
				rvLargeNoti.setViewVisibility(R.id.ivchart_large, View.GONE);
			}
			rvLargeNoti
					.setTextViewText(R.id.tvNotiPrevmainText_large, mainText);
			rvLargeNoti.setTextViewText(R.id.tvNotiPrevsubText_large, subtext);
			setResourceImages(rvLargeNoti);
			setIntents(rvLargeNoti);
			notification.bigContentView = rvLargeNoti;

		}

		if (mPref.getBoolean(PreferenceHelper.NOTIFICATION_ENABLE, true)) {
			/*
			 * startForegroundCompat(R.string.foreground_service_started,
			 * notification);
			 */
			startForeground(notiID, notification);

		} else {
			notification.icon = R.drawable.abc_ab_bottom_transparent_dark_holo;
			try {
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
					notification.priority = Notification.PRIORITY_MIN;
			} catch (Exception ex) {
			}
			startForeground(notiID, notification);
			startService(new Intent(ForegroundService.this, FakeService.class));
		}

		/*
		 * if(DISPLAY != null){ if(DISPLAY){ startForeground(notiID,
		 * notification); Log.Toast(getBaseContext(), "displaying notification",
		 * Toast.LENGTH_LONG); }else{ Log.Toast(getBaseContext(),
		 * "not gonna display", Toast.LENGTH_LONG);
		 * //startForegroundCompat(R.string.foreground_service_started, null);
		 * notification.icon = R.drawable.abc_ab_bottom_transparent_dark_holo;
		 * notification.priority = Notification.PRIORITY_MIN;
		 * startForeground(notiID, notification); startService(new
		 * Intent(ForegroundService.this, FakeService.class)); } }
		 */

		if (level != mPref.getInt(PreferenceHelper.PREV_BAT_LEVEL, level))
			mPref.edit()
					.putString(PreferenceHelper.PREV_BAT_TIME,
							TimeFuncs.getCurrentTimeStamp()).commit();
		mPref.edit().putInt(PreferenceHelper.PREV_BAT_LEVEL, level).commit();

	}

	void setKitkatActions(NotificationCompat.Builder builder) {

		// wifi
		PendingIntent contentIntent = PendingIntent.getBroadcast(this, 34341,
				new Intent(this, WifiRcvr.class), 0);

		if (mPref.getBoolean(PreferenceHelper.SHOW_WIFI, true))
			builder.addAction(
					ToggleHelper.isWifiEnabled(getBaseContext()) ? R.drawable.wifi_on
							: R.drawable.wifi_off, null, contentIntent);

		// wifi hotspot
		contentIntent = PendingIntent.getBroadcast(this, 1765, new Intent(this,
				HotspotRcvr.class), 0);
		if (mPref.getBoolean(PreferenceHelper.SHOW_WIFIHOTSPOT, false))
			builder.addAction(new WifiApManager(getBaseContext())
					.isWifiApEnabled() ? R.drawable.wifi_hotspot_on
					: R.drawable.wifi_hotspot_off, null, contentIntent);

		// mobile data
		contentIntent = PendingIntent.getBroadcast(this, 323, new Intent(this,
				MdataRcvr.class), 0);
		if (mPref.getBoolean(PreferenceHelper.SHOW_MDATA, true))
			builder.addAction(
					ToggleHelper.isDataEnable(getBaseContext()) ? R.drawable.mdata_on
							: R.drawable.mdata_off, null, contentIntent);

		// bluetooth
		contentIntent = PendingIntent.getBroadcast(this, 546, new Intent(this,
				BtoothRcvr.class), 0);
		if (mPref.getBoolean(PreferenceHelper.SHOW_BTOOTH, true))
			builder.addAction(
					ToggleHelper.isBluetoothEnabled(getBaseContext()) ? R.drawable.btooth_on
							: R.drawable.btooth_off, null, contentIntent);

		// background sync
		contentIntent = PendingIntent.getBroadcast(this, 545, new Intent(this,
				SyncRcvr.class), 0);
		if (mPref.getBoolean(PreferenceHelper.SHOW_SYNC, true))
			builder.addAction(ToggleHelper.isSyncEnabled() ? R.drawable.sync_on
					: R.drawable.sync_off, null, contentIntent);

		// auto rotate
		contentIntent = PendingIntent.getBroadcast(this, 987, new Intent(this,
				ArotateRcvr.class), 0);
		if (mPref.getBoolean(PreferenceHelper.SHOW_AROTATE, false))
			builder.addAction(
					ToggleHelper.isRotationEnabled(getBaseContext()) ? R.drawable.orientation_on
							: R.drawable.orientation_off, null, contentIntent);

		// torch
		Intent intent = new Intent(this, TorchService.class);
		// intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent,
				0);
		// mCameraDevice.releaseCamera();
		if (mPref.getBoolean(PreferenceHelper.SHOW_TORCH, false))
			builder.addAction((isOn) ? R.drawable.lightbulb_on
					: R.drawable.lightbulb_off, null, pendingIntent);

		// brigthness
		contentIntent = PendingIntent.getActivity(this, 543, new Intent(this,
				BrightnessRcvr.class), 0);
		if (mPref.getBoolean(PreferenceHelper.SHOW_BNESS, true))

			switch (ToggleHelper.getBrightnessMode(getBaseContext())) {
			case 1:
				builder.addAction(R.drawable.blow_on, null, contentIntent);
				break;
			case 2:
				builder.addAction(R.drawable.bhalf_on, null, contentIntent);
				break;
			case 3:
				builder.addAction(R.drawable.bfull_on, null, contentIntent);
				break;
			case 0:
				builder.addAction(R.drawable.bauto_on, null, contentIntent);
				break;
			}

		// power profile
		contentIntent = PendingIntent.getBroadcast(this, 765, new Intent(this,
				PowerProfileRcvr.class), 0);
		if (mPref.getBoolean(PreferenceHelper.SHOW_PSWITCHER, false)) {

			ArrayList<PowerProfItems> poweritems = PowerPreference
					.retPower(getBaseContext());
			if (mPref.getInt(PreferenceHelper.PROF2, 0) < poweritems.size()
					&& mPref.getInt(PreferenceHelper.PROF1, 0) < poweritems
							.size()) {
				if (poweritems.get(mPref.getInt(PreferenceHelper.PROF2, 0))
						.isPowerProfequal(
								ToggleHelper.isWifiEnabled(getBaseContext()),
								ToggleHelper.isDataEnable(getBaseContext()),
								ToggleHelper
										.isBluetoothEnabled(getBaseContext()),
								ToggleHelper.isAModeEnabled(getBaseContext()),
								ToggleHelper.isSyncEnabled(),
								ToggleHelper.getBrightness(getBaseContext()),
								ToggleHelper.isHapticFback(getBaseContext()),
								ToggleHelper
										.isRotationEnabled(getBaseContext()),
								ToggleHelper.getRingerMode(getBaseContext()),
								String.valueOf(ToggleHelper
										.getScreenTimeOut(getBaseContext()))))
					builder.addAction(R.drawable.power_on, null, contentIntent);

				else if (poweritems
						.get(mPref.getInt(PreferenceHelper.PROF1, 0))
						.isPowerProfequal(
								ToggleHelper.isWifiEnabled(getBaseContext()),
								ToggleHelper.isDataEnable(getBaseContext()),
								ToggleHelper
										.isBluetoothEnabled(getBaseContext()),
								ToggleHelper.isAModeEnabled(getBaseContext()),
								ToggleHelper.isSyncEnabled(),
								ToggleHelper.getBrightness(getBaseContext()),
								ToggleHelper.isHapticFback(getBaseContext()),
								ToggleHelper
										.isRotationEnabled(getBaseContext()),
								ToggleHelper.getRingerMode(getBaseContext()),
								String.valueOf(ToggleHelper
										.getScreenTimeOut(getBaseContext()))))
					builder.addAction(R.drawable.power_off, null, contentIntent);
				else
					builder.addAction(R.drawable.power_custom, null,
							contentIntent);

			} else {
				builder.addAction(R.drawable.power_custom, null, contentIntent);
			}
		}

	}

	private void handleTrigger(int bLevel) {
		if (mPref.getInt(PreferenceHelper.PREV_BAT_LEVEL, level - 1) == level)
			return;
		ArrayList<Boolean> trigs = PowerPreference.retBooleans(
				getBaseContext(), PowerPreference.TRIGGER);
		ArrayList<Integer> t_mode = PowerPreference.retIntegers(
				getBaseContext(), PowerPreference.TRIGGER_MODE);
		if (trigs.contains(true)) {
			ArrayList<Integer> trig_vals = PowerPreference.retIntegers(
					getBaseContext(), PowerPreference.TRIGGER_VAL);
			int val = 0;
			boolean flag = false;
			if (trig_vals.contains(bLevel)) {
				for (int i = 0; i < trig_vals.size(); i++) {
					if (trigs.get(i) && trig_vals.get(i) == bLevel) {
						val = i;
						flag = true;
						break;
					}
				}
				if (flag) {
					if (((t_mode.get(val) == 0) && !isconnected)
							|| ((t_mode.get(val) == 1) && isconnected)) {
						return;
					}
					ArrayList<String> names = PowerPreference.retStrings(
							getBaseContext(), PowerPreference.PROFN);
					NotificationCompat.Builder builder = new NotificationCompat.Builder(
							this)
							.setSmallIcon(R.drawable.ic_launcher)
							.setContentTitle("Power Profile Switched")
							.setContentText(
									"Switched to Profile " + names.get(val))
							.setTicker("Power Profile Switched")
							.setWhen(System.currentTimeMillis());
					builder.setOngoing(false);
					builder.setAutoCancel(true);
					Notification notify = builder.build();
					mNM.notify(R.layout.about, notify);
					sendBroadcast(new Intent(Intents.SWITCHER_INTENT).putExtra(
							EditPower.BNESS_SEEKBAR,
							PowerPreference.retIntegers(getBaseContext(),
									PowerPreference.BNESS).get(val)).putExtra(
							PreferenceHelper.POSITION, val));
				}
			}
		}

	}

	public String setNotText(String subtext, String mainText, int choice) {
		switch (choice) {
		case 0:
			return level + "% remaining";
		case 1:
			return "Temperature: " + temperature;
		case 2:
			return health + ", " + "Temperature: " + temperature;
		case 3:
			return subtext;
		case 4:
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date(System.currentTimeMillis() + (reqTime * 1000)));
			String day1 = "";
			switch (cal.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				day1 = "Monday";
				break;
			case Calendar.TUESDAY:
				day1 = "Tuesday";
				break;
			case Calendar.WEDNESDAY:
				day1 = "Wednesday";
				break;
			case Calendar.THURSDAY:
				day1 = "Thursday";
				break;
			case Calendar.FRIDAY:
				day1 = "Friday";
				break;
			case Calendar.SATURDAY:
				day1 = "Saturday";
				break;
			case Calendar.SUNDAY:
				day1 = "Sunday";
				break;
			}
			String str = "";
			if (isconnected) {
				str = "Full Charge at ";
			} else {
				str = "Empty at ";
			}
			if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.getInstance().get(
					Calendar.DAY_OF_WEEK)) {
				return str
						+ ((cal.get(Calendar.HOUR) == 0) ? 12 : cal
								.get(Calendar.HOUR))
						+ ":"
						+ ((cal.get(Calendar.MINUTE) + "").length() == 1 ? ("0" + cal
								.get(Calendar.MINUTE)) : cal
								.get(Calendar.MINUTE))
						+ (cal.get(Calendar.AM_PM) == Calendar.AM ? " AM"
								: " PM");
			} else {
				int d = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 1;
				if (d == 8)
					d = 1;
				if (cal.get(Calendar.DAY_OF_WEEK) == d)
					day1 = "Tomorrow";
				return str
						+ ((cal.get(Calendar.HOUR) == 0) ? 12 : cal
								.get(Calendar.HOUR))
						+ ":"
						+ ((cal.get(Calendar.MINUTE) + "").length() == 1 ? ("0" + cal
								.get(Calendar.MINUTE)) : cal
								.get(Calendar.MINUTE))
						+ (cal.get(Calendar.AM_PM) == Calendar.AM ? " AM"
								: " PM") + ", " + day1;
			}
		case 5:
			Date mydate = TimeFuncs.GetItemDate(mPref.getString(
					PreferenceHelper.LAST_CHARGED,
					TimeFuncs.getCurrentTimeStamp()));
			Calendar c = Calendar.getInstance();
			c.setTime(mydate);
			String day = "";
			switch (c.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				day = "Monday";
				break;
			case Calendar.TUESDAY:
				day = "Tuesday";
				break;
			case Calendar.WEDNESDAY:
				day = "Wednesday";
				break;
			case Calendar.THURSDAY:
				day = "Thursday";
				break;
			case Calendar.FRIDAY:
				day = "Friday";
				break;
			case Calendar.SATURDAY:
				day = "Saturday";
				break;
			case Calendar.SUNDAY:
				day = "Sunday";
				break;
			}

			if (c.get(Calendar.DAY_OF_WEEK) == Calendar.getInstance().get(
					Calendar.DAY_OF_WEEK)) {
				if (isconnected)
					return "Plugged at "
							+ ((c.get(Calendar.HOUR) == 0) ? 12 : c
									.get(Calendar.HOUR))
							+ ":"
							+ ((c.get(Calendar.MINUTE) + "").length() == 1 ? ("0" + c
									.get(Calendar.MINUTE)) : c
									.get(Calendar.MINUTE))
							+ (c.get(Calendar.AM_PM) == Calendar.AM ? " AM"
									: " PM");
				else
					return "Unplugged at "
							+ ((c.get(Calendar.HOUR) == 0) ? 12 : c
									.get(Calendar.HOUR))
							+ ":"
							+ ((c.get(Calendar.MINUTE) + "").length() == 1 ? ("0" + c
									.get(Calendar.MINUTE)) : c
									.get(Calendar.MINUTE))
							+ (c.get(Calendar.AM_PM) == Calendar.AM ? " AM"
									: " PM");
			} else {
				int d = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
				if (d == 0)
					d = 7;
				if (c.get(Calendar.DAY_OF_WEEK) == d)
					day = "Yesterday";
				if (isconnected)
					return "Plugged at "
							+ ((c.get(Calendar.HOUR) == 0) ? 12 : c
									.get(Calendar.HOUR))
							+ ":"
							+ ((c.get(Calendar.MINUTE) + "").length() == 1 ? ("0" + c
									.get(Calendar.MINUTE)) : c
									.get(Calendar.MINUTE))
							+ (c.get(Calendar.AM_PM) == Calendar.AM ? " AM"
									: " PM") + ", " + day;
				else
					return "Unplugged at "
							+ ((c.get(Calendar.HOUR) == 0) ? 12 : c
									.get(Calendar.HOUR))
							+ ":"
							+ ((c.get(Calendar.MINUTE) + "").length() == 1 ? ("0" + c
									.get(Calendar.MINUTE)) : c
									.get(Calendar.MINUTE))
							+ (c.get(Calendar.AM_PM) == Calendar.AM ? " AM"
									: " PM") + ", " + day;
			}

		case 6:
			return mainText;
		}
		return mainText;

	}

	private void setIntents(RemoteViews rv) {
		PendingIntent contentIntent = PendingIntent.getBroadcast(this, 34341,
				new Intent(this, WifiRcvr.class), 0);
		rv.setOnClickPendingIntent(R.id.ibwifi_large, contentIntent);

		contentIntent = PendingIntent.getBroadcast(this, 323, new Intent(this,
				MdataRcvr.class), 0);
		rv.setOnClickPendingIntent(R.id.ibmdata_large, contentIntent);

		contentIntent = PendingIntent.getBroadcast(this, 546, new Intent(this,
				BtoothRcvr.class), 0);
		rv.setOnClickPendingIntent(R.id.ibbtooth_large, contentIntent);

		contentIntent = PendingIntent.getBroadcast(this, 324, new Intent(this,
				AmodeRcvr.class), 0);
		rv.setOnClickPendingIntent(R.id.ibamode_large, contentIntent);

		contentIntent = PendingIntent.getBroadcast(this, 545, new Intent(this,
				SyncRcvr.class), 0);
		rv.setOnClickPendingIntent(R.id.ibsync_large, contentIntent);

		contentIntent = PendingIntent.getActivity(this, 543, new Intent(this,
				BrightnessRcvr.class), 0);
		rv.setOnClickPendingIntent(R.id.ibbrightness_large, contentIntent);

		contentIntent = PendingIntent.getBroadcast(this, 765, new Intent(this,
				PowerProfileRcvr.class), 0);
		rv.setOnClickPendingIntent(R.id.ibpowersaver_toggle_large,
				contentIntent);

		contentIntent = PendingIntent.getBroadcast(this, 1765, new Intent(this,
				HotspotRcvr.class), 0);
		rv.setOnClickPendingIntent(R.id.ibwifihotspot_large, contentIntent);

		contentIntent = PendingIntent.getBroadcast(this, 987, new Intent(this,
				ArotateRcvr.class), 0);
		rv.setOnClickPendingIntent(R.id.ibarotate_large, contentIntent);

		Intent intent = new Intent(this, TorchService.class);
		// intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent,
				0);
		rv.setOnClickPendingIntent(R.id.ibtorch_large, pendingIntent);

	}

	private void setResourceImages(RemoteViews rv) {

		// Keeping this...even though it doesn't matter..value will always be
		// false
		// CameraDevice mCameraDevice = new CameraDevice();
		// mCameraDevice.acquireCamera();

		if (mPref.getBoolean(PreferenceHelper.SHOW_WIFIHOTSPOT, false))
			rv.setViewVisibility(R.id.ibwifihotspot_large, View.VISIBLE);
		else
			rv.setViewVisibility(R.id.ibwifihotspot_large, View.GONE);

		rv.setImageViewResource(
				R.id.ibwifihotspot_large,
				new WifiApManager(getBaseContext()).isWifiApEnabled() ? R.drawable.wifi_hotspot_on
						: R.drawable.wifi_hotspot_off);

		rv.setImageViewResource(R.id.ibtorch_large,
				(isOn) ? R.drawable.lightbulb_on : R.drawable.lightbulb_off);
		// mCameraDevice.releaseCamera();
		if (mPref.getBoolean(PreferenceHelper.SHOW_TORCH, false))
			rv.setViewVisibility(R.id.ibtorch_large, View.VISIBLE);
		else
			rv.setViewVisibility(R.id.ibtorch_large, View.GONE);

		if (mPref.getBoolean(PreferenceHelper.SHOW_AROTATE, false))
			rv.setViewVisibility(R.id.ibarotate_large, View.VISIBLE);
		else
			rv.setViewVisibility(R.id.ibarotate_large, View.GONE);

		rv.setImageViewResource(
				R.id.ibarotate_large,
				ToggleHelper.isRotationEnabled(getBaseContext()) ? R.drawable.orientation_on
						: R.drawable.orientation_off);

		if (mPref.getBoolean(PreferenceHelper.SHOW_WIFI, true))
			rv.setViewVisibility(R.id.ibwifi_large, View.VISIBLE);
		else
			rv.setViewVisibility(R.id.ibwifi_large, View.GONE);
		rv.setImageViewResource(R.id.ibwifi_large, ToggleHelper
				.isWifiEnabled(getBaseContext()) ? R.drawable.wifi_on
				: R.drawable.wifi_off);
		if (mPref.getBoolean(PreferenceHelper.SHOW_MDATA, true))
			rv.setViewVisibility(R.id.ibmdata_large, View.VISIBLE);
		else
			rv.setViewVisibility(R.id.ibmdata_large, View.GONE);
		rv.setImageViewResource(R.id.ibmdata_large, ToggleHelper
				.isDataEnable(getBaseContext()) ? R.drawable.mdata_on
				: R.drawable.mdata_off);
		if (mPref.getBoolean(PreferenceHelper.SHOW_BTOOTH, true))
			rv.setViewVisibility(R.id.ibbtooth_large, View.VISIBLE);
		else
			rv.setViewVisibility(R.id.ibbtooth_large, View.GONE);
		rv.setImageViewResource(R.id.ibbtooth_large, ToggleHelper
				.isBluetoothEnabled(getBaseContext()) ? R.drawable.btooth_on
				: R.drawable.btooth_off);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
			rv.setViewVisibility(R.id.ibamode_large, View.GONE);
		else {
			rv.setImageViewResource(R.id.ibamode_large, ToggleHelper
					.isAModeEnabled(getBaseContext()) ? R.drawable.amode_on
					: R.drawable.amode_off);
			if (mPref.getBoolean(PreferenceHelper.SHOW_AMODE, true))
				rv.setViewVisibility(R.id.ibamode_large, View.VISIBLE);
			else
				rv.setViewVisibility(R.id.ibamode_large, View.GONE);
		}

		if (mPref.getBoolean(PreferenceHelper.SHOW_SYNC, true))
			rv.setViewVisibility(R.id.ibsync_large, View.VISIBLE);
		else
			rv.setViewVisibility(R.id.ibsync_large, View.GONE);
		rv.setImageViewResource(R.id.ibsync_large,
				ToggleHelper.isSyncEnabled() ? R.drawable.sync_on
						: R.drawable.sync_off);
		if (mPref.getBoolean(PreferenceHelper.SHOW_BNESS, true))
			rv.setViewVisibility(R.id.ibbrightness_large, View.VISIBLE);
		else
			rv.setViewVisibility(R.id.ibbrightness_large, View.GONE);
		switch (ToggleHelper.getBrightnessMode(getBaseContext())) {
		case 1:
			rv.setImageViewResource(R.id.ibbrightness_large, R.drawable.blow_on);
			break;
		case 2:
			rv.setImageViewResource(R.id.ibbrightness_large,
					R.drawable.bhalf_on);
			break;
		case 3:
			rv.setImageViewResource(R.id.ibbrightness_large,
					R.drawable.bfull_on);
			break;
		case 0:
			rv.setImageViewResource(R.id.ibbrightness_large,
					R.drawable.bauto_on);
			break;
		}
		if (mPref.getBoolean(PreferenceHelper.SHOW_PSWITCHER, false))
			rv.setViewVisibility(R.id.ibpowersaver_toggle_large, View.VISIBLE);
		else
			rv.setViewVisibility(R.id.ibpowersaver_toggle_large, View.GONE);
		ArrayList<PowerProfItems> poweritems = PowerPreference
				.retPower(getBaseContext());
		if (mPref.getInt(PreferenceHelper.PROF2, 0) < poweritems.size()
				&& mPref.getInt(PreferenceHelper.PROF1, 0) < poweritems.size()) {
			if (poweritems.get(mPref.getInt(PreferenceHelper.PROF2, 0))
					.isPowerProfequal(
							ToggleHelper.isWifiEnabled(getBaseContext()),
							ToggleHelper.isDataEnable(getBaseContext()),
							ToggleHelper.isBluetoothEnabled(getBaseContext()),
							ToggleHelper.isAModeEnabled(getBaseContext()),
							ToggleHelper.isSyncEnabled(),
							ToggleHelper.getBrightness(getBaseContext()),
							ToggleHelper.isHapticFback(getBaseContext()),
							ToggleHelper.isRotationEnabled(getBaseContext()),
							ToggleHelper.getRingerMode(getBaseContext()),
							String.valueOf(ToggleHelper
									.getScreenTimeOut(getBaseContext()))))
				rv.setImageViewResource(R.id.ibpowersaver_toggle_large,
						R.drawable.power_on);
			else if (poweritems.get(mPref.getInt(PreferenceHelper.PROF1, 0))
					.isPowerProfequal(
							ToggleHelper.isWifiEnabled(getBaseContext()),
							ToggleHelper.isDataEnable(getBaseContext()),
							ToggleHelper.isBluetoothEnabled(getBaseContext()),
							ToggleHelper.isAModeEnabled(getBaseContext()),
							ToggleHelper.isSyncEnabled(),
							ToggleHelper.getBrightness(getBaseContext()),
							ToggleHelper.isHapticFback(getBaseContext()),
							ToggleHelper.isRotationEnabled(getBaseContext()),
							ToggleHelper.getRingerMode(getBaseContext()),
							String.valueOf(ToggleHelper
									.getScreenTimeOut(getBaseContext()))))
				rv.setImageViewResource(R.id.ibpowersaver_toggle_large,
						R.drawable.power_off);
			else
				rv.setImageViewResource(R.id.ibpowersaver_toggle_large,
						R.drawable.power_custom);
		} else {
			rv.setImageViewResource(R.id.ibpowersaver_toggle_large,
					R.drawable.power_custom);
		}

	}

	/**
	 * Functions For Graph
	 */

	public Bitmap loadBitmapFromView(View v) {
		Bitmap b = Bitmap
				.createBitmap(getResources().getDisplayMetrics().widthPixels,
						Functions.ReturnHeight(160, getBaseContext()),
						Config.ARGB_8888);
		Canvas c = new Canvas(b);
		v.layout(0, 0, getResources().getDisplayMetrics().widthPixels,
				Functions.ReturnHeight(160, getBaseContext()));
		v.draw(c);
		return b;
	}

	double lastentry;
	ArrayList<Double> vals;
	ArrayList<Double> dates;

	private XYMultipleSeriesDataset getDateDataset() {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		TimeSeries series = new TimeSeries("Battery History2");
		TimeSeries series2 = new TimeSeries("Battery History1");

		for (int i = 0; i < vals.size(); i++) {
			// Log.d("Stencil", "x: " + new Date((long) series.getX(i)));
			// Log.d("Stencil", "y: " + series.getY(i));
			series2.add(new Date((long) (double) dates.get(i)), vals.get(i));
		}

		Log.d("series2 size: " + series2.getItemCount());
		dataset.addSeries(series2);

		if (isconnected) {
			series = new TimeSeries("Battery History");

			series.add(
					new Date((long) series2.getX(series2.getItemCount() - 1)),
					series2.getY(series2.getItemCount() - 1));

			lastentry = series2.getX(series2.getItemCount() - 1)
					+ (long) (1000 * ((mPref.getLong(
							PreferenceHelper.BAT_CHARGE, 81)) * (100 - series2
							.getY(series2.getItemCount() - 1))));

			series.add(new Date((long) lastentry), 100);
			dataset.addSeries(series);

		} else {
			series = new TimeSeries("Battery History");
			series.add(new Date((long) series2.getMaxX()),
					series2.getY(series2.getItemCount() - 1));

			lastentry = series2.getX(series2.getItemCount() - 1)
					+ (long) (1000 * (mPref.getLong(
							PreferenceHelper.BAT_DISCHARGE, 792)) * (series2
							.getY(series2.getItemCount() - 1)));

			series.add(new Date((long) lastentry), 0);
			dataset.addSeries(series);
		}

		return dataset;
	}

	public float getDimension(float i) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		float ScreenDensity = dm.density;
		return i * ScreenDensity;
	}

	private XYMultipleSeriesRenderer getRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(getDimension(10.67f));
		renderer.setChartTitleTextSize(getDimension(13.34f));
		renderer.setPointSize(0f);
		renderer.setMargins(new int[] { (int) getDimension(20f),// 13.34
				(int) getDimension(33.34f), (int) getDimension(1f),// 10
				(int) getDimension(6.67f) });
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(0xff1e8bd4);

		renderer.setBackgroundColor(0x00000000);
		// renderer.setApplyBackgroundColor(true);
		/* r.setFillBelowLine(true); */
		FillOutsideLine fol = new FillOutsideLine(
				XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
		fol.setColor(0xa01e8bd4);
		r.addFillOutsideLine(fol);
		renderer.setYLabelsAlign(Align.RIGHT);

		// r.setFillBelowLineColor(0xff21637c);
		r.setLineWidth(getDimension(2.67f));
		renderer.setMarginsColor(0xff111111);
		renderer.setLabelsTextSize(getDimension(10f));

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			renderer.setXLabels(5);
		} else {
			renderer.setXLabels(6);
		}

		renderer.setYLabels(11);
		renderer.addSeriesRenderer(r);
		renderer.setShowGrid(true);
		renderer.setShowLegend(false);
		renderer.setInScroll(true);

		r = new XYSeriesRenderer();
		r.setPointStyle(PointStyle.POINT);
		r.setLineWidth(4);

		if (isconnected) {
			r.setColor(0xff17699f);
			FillOutsideLine fol1 = new FillOutsideLine(
					XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
			fol1.setColor(0xa017699f);
			r.addFillOutsideLine(fol1);

		} else {

			r.setColor(0xffff4444);
			FillOutsideLine fol2 = new FillOutsideLine(
					XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
			fol2.setColor(0xa0ff4444);
			r.addFillOutsideLine(fol2);
		}

		renderer.setPanEnabled(true, false);
		renderer.setShowAxes(true);
		renderer.setAntialiasing(true);
		renderer.setZoomEnabled(true, false);
		renderer.setYAxisMax(100);
		renderer.setYAxisMin(0);
		renderer.setTextTypeface(Typeface.createFromAsset(getAssets(),
				Constants.FONT_ROBOTO_COND));

		renderer.addSeriesRenderer(r);

		renderer.setXAxisMax(lastentry + 1800000);

		for (int i = 0; i <= 100; i = i + 10)
			renderer.addYTextLabel(i, i + " %");

		renderer.setAxesColor(Color.WHITE);
		renderer.setLabelsColor(Color.LTGRAY);
		renderer.setInScroll(false);
		return renderer;
	}

	/**
	 * 
	 * @param level2
	 * @return
	 */
	private int getId(int level2) {

		switch (mPref.getInt(PreferenceHelper.NOTI_THEME, 0)) {
		case 1:
			int ids[] = { R.drawable.ics_circle_0, R.drawable.ics_circle_1,
					R.drawable.ics_circle_2, R.drawable.ics_circle_3,
					R.drawable.ics_circle_4, R.drawable.ics_circle_5,
					R.drawable.ics_circle_6, R.drawable.ics_circle_7,
					R.drawable.ics_circle_8, R.drawable.ics_circle_9,
					R.drawable.ics_circle_10, R.drawable.ics_circle_11,
					R.drawable.ics_circle_12, R.drawable.ics_circle_13,
					R.drawable.ics_circle_14, R.drawable.ics_circle_15,
					R.drawable.ics_circle_16, R.drawable.ics_circle_17,
					R.drawable.ics_circle_18, R.drawable.ics_circle_19,
					R.drawable.ics_circle_20, R.drawable.ics_circle_21,
					R.drawable.ics_circle_22, R.drawable.ics_circle_23,
					R.drawable.ics_circle_24, R.drawable.ics_circle_25,
					R.drawable.ics_circle_26, R.drawable.ics_circle_27,
					R.drawable.ics_circle_28, R.drawable.ics_circle_29,
					R.drawable.ics_circle_30, R.drawable.ics_circle_31,
					R.drawable.ics_circle_32, R.drawable.ics_circle_33,
					R.drawable.ics_circle_34, R.drawable.ics_circle_35,
					R.drawable.ics_circle_36, R.drawable.ics_circle_37,
					R.drawable.ics_circle_38, R.drawable.ics_circle_39,
					R.drawable.ics_circle_40, R.drawable.ics_circle_41,
					R.drawable.ics_circle_42, R.drawable.ics_circle_43,
					R.drawable.ics_circle_44, R.drawable.ics_circle_45,
					R.drawable.ics_circle_46, R.drawable.ics_circle_47,
					R.drawable.ics_circle_48, R.drawable.ics_circle_49,
					R.drawable.ics_circle_50, R.drawable.ics_circle_51,
					R.drawable.ics_circle_52, R.drawable.ics_circle_53,
					R.drawable.ics_circle_54, R.drawable.ics_circle_55,
					R.drawable.ics_circle_56, R.drawable.ics_circle_57,
					R.drawable.ics_circle_58, R.drawable.ics_circle_59,
					R.drawable.ics_circle_60, R.drawable.ics_circle_61,
					R.drawable.ics_circle_62, R.drawable.ics_circle_63,
					R.drawable.ics_circle_64, R.drawable.ics_circle_65,
					R.drawable.ics_circle_66, R.drawable.ics_circle_67,
					R.drawable.ics_circle_68, R.drawable.ics_circle_69,
					R.drawable.ics_circle_70, R.drawable.ics_circle_71,
					R.drawable.ics_circle_72, R.drawable.ics_circle_73,
					R.drawable.ics_circle_74, R.drawable.ics_circle_75,
					R.drawable.ics_circle_76, R.drawable.ics_circle_77,
					R.drawable.ics_circle_78, R.drawable.ics_circle_79,
					R.drawable.ics_circle_80, R.drawable.ics_circle_81,
					R.drawable.ics_circle_82, R.drawable.ics_circle_83,
					R.drawable.ics_circle_84, R.drawable.ics_circle_85,
					R.drawable.ics_circle_86, R.drawable.ics_circle_87,
					R.drawable.ics_circle_88, R.drawable.ics_circle_89,
					R.drawable.ics_circle_90, R.drawable.ics_circle_91,
					R.drawable.ics_circle_92, R.drawable.ics_circle_93,
					R.drawable.ics_circle_94, R.drawable.ics_circle_95,
					R.drawable.ics_circle_96, R.drawable.ics_circle_97,
					R.drawable.ics_circle_98, R.drawable.ics_circle_99,
					R.drawable.ics_circle_100 };
			return ids[level2];
		case 0:
			int ids2[] = { R.drawable.stencil_0, R.drawable.stencil_1,
					R.drawable.stencil_2, R.drawable.stencil_3,
					R.drawable.stencil_4, R.drawable.stencil_5,
					R.drawable.stencil_6, R.drawable.stencil_7,
					R.drawable.stencil_8, R.drawable.stencil_9,
					R.drawable.stencil_10, R.drawable.stencil_11,
					R.drawable.stencil_12, R.drawable.stencil_13,
					R.drawable.stencil_14, R.drawable.stencil_15,
					R.drawable.stencil_16, R.drawable.stencil_17,
					R.drawable.stencil_18, R.drawable.stencil_19,
					R.drawable.stencil_20, R.drawable.stencil_21,
					R.drawable.stencil_22, R.drawable.stencil_23,
					R.drawable.stencil_24, R.drawable.stencil_25,
					R.drawable.stencil_26, R.drawable.stencil_27,
					R.drawable.stencil_28, R.drawable.stencil_29,
					R.drawable.stencil_30, R.drawable.stencil_31,
					R.drawable.stencil_32, R.drawable.stencil_33,
					R.drawable.stencil_34, R.drawable.stencil_35,
					R.drawable.stencil_36, R.drawable.stencil_37,
					R.drawable.stencil_38, R.drawable.stencil_39,
					R.drawable.stencil_40, R.drawable.stencil_41,
					R.drawable.stencil_42, R.drawable.stencil_43,
					R.drawable.stencil_44, R.drawable.stencil_45,
					R.drawable.stencil_46, R.drawable.stencil_47,
					R.drawable.stencil_48, R.drawable.stencil_49,
					R.drawable.stencil_50, R.drawable.stencil_51,
					R.drawable.stencil_52, R.drawable.stencil_53,
					R.drawable.stencil_54, R.drawable.stencil_55,
					R.drawable.stencil_56, R.drawable.stencil_57,
					R.drawable.stencil_58, R.drawable.stencil_59,
					R.drawable.stencil_60, R.drawable.stencil_61,
					R.drawable.stencil_62, R.drawable.stencil_63,
					R.drawable.stencil_64, R.drawable.stencil_65,
					R.drawable.stencil_66, R.drawable.stencil_67,
					R.drawable.stencil_68, R.drawable.stencil_69,
					R.drawable.stencil_70, R.drawable.stencil_71,
					R.drawable.stencil_72, R.drawable.stencil_73,
					R.drawable.stencil_74, R.drawable.stencil_75,
					R.drawable.stencil_76, R.drawable.stencil_77,
					R.drawable.stencil_78, R.drawable.stencil_79,
					R.drawable.stencil_80, R.drawable.stencil_81,
					R.drawable.stencil_82, R.drawable.stencil_83,
					R.drawable.stencil_84, R.drawable.stencil_85,
					R.drawable.stencil_86, R.drawable.stencil_87,
					R.drawable.stencil_88, R.drawable.stencil_89,
					R.drawable.stencil_90, R.drawable.stencil_91,
					R.drawable.stencil_92, R.drawable.stencil_93,
					R.drawable.stencil_94, R.drawable.stencil_95,
					R.drawable.stencil_96, R.drawable.stencil_97,
					R.drawable.stencil_98, R.drawable.stencil_99,
					R.drawable.stencil_100 };
			return ids2[level2];
		case 2:
			int ids3[] = { R.drawable.simple_0, R.drawable.simple_1,
					R.drawable.simple_2, R.drawable.simple_3,
					R.drawable.simple_4, R.drawable.simple_5,
					R.drawable.simple_6, R.drawable.simple_7,
					R.drawable.simple_8, R.drawable.simple_9,
					R.drawable.simple_10, R.drawable.simple_11,
					R.drawable.simple_12, R.drawable.simple_13,
					R.drawable.simple_14, R.drawable.simple_15,
					R.drawable.simple_16, R.drawable.simple_17,
					R.drawable.simple_18, R.drawable.simple_19,
					R.drawable.simple_20, R.drawable.simple_21,
					R.drawable.simple_22, R.drawable.simple_23,
					R.drawable.simple_24, R.drawable.simple_25,
					R.drawable.simple_26, R.drawable.simple_27,
					R.drawable.simple_28, R.drawable.simple_29,
					R.drawable.simple_30, R.drawable.simple_31,
					R.drawable.simple_32, R.drawable.simple_33,
					R.drawable.simple_34, R.drawable.simple_35,
					R.drawable.simple_36, R.drawable.simple_37,
					R.drawable.simple_38, R.drawable.simple_39,
					R.drawable.simple_40, R.drawable.simple_41,
					R.drawable.simple_42, R.drawable.simple_43,
					R.drawable.simple_44, R.drawable.simple_45,
					R.drawable.simple_46, R.drawable.simple_47,
					R.drawable.simple_48, R.drawable.simple_49,
					R.drawable.simple_50, R.drawable.simple_51,
					R.drawable.simple_52, R.drawable.simple_53,
					R.drawable.simple_54, R.drawable.simple_55,
					R.drawable.simple_56, R.drawable.simple_57,
					R.drawable.simple_58, R.drawable.simple_59,
					R.drawable.simple_60, R.drawable.simple_61,
					R.drawable.simple_62, R.drawable.simple_63,
					R.drawable.simple_64, R.drawable.simple_65,
					R.drawable.simple_66, R.drawable.simple_67,
					R.drawable.simple_68, R.drawable.simple_69,
					R.drawable.simple_70, R.drawable.simple_71,
					R.drawable.simple_72, R.drawable.simple_73,
					R.drawable.simple_74, R.drawable.simple_75,
					R.drawable.simple_76, R.drawable.simple_77,
					R.drawable.simple_78, R.drawable.simple_79,
					R.drawable.simple_80, R.drawable.simple_81,
					R.drawable.simple_82, R.drawable.simple_83,
					R.drawable.simple_84, R.drawable.simple_85,
					R.drawable.simple_86, R.drawable.simple_87,
					R.drawable.simple_88, R.drawable.simple_89,
					R.drawable.simple_90, R.drawable.simple_91,
					R.drawable.simple_92, R.drawable.simple_93,
					R.drawable.simple_94, R.drawable.simple_95,
					R.drawable.simple_96, R.drawable.simple_97,
					R.drawable.simple_98, R.drawable.simple_99,
					R.drawable.simple_100 };
			return ids3[level2];
		}
		return R.drawable.simple_0;
	}

	public class UpdateEntries extends AsyncTask<Integer, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Integer... arg0) {

			if (mPref.getInt(PreferenceHelper.PREV_BAT_LEVEL, level - 1) == level)
				return false;
			// TODO
			List<Alarm> mAlarms = Alarm.getAlarms(getContentResolver(),
					Alarm.BATTERY + " = ? ", new String[] { "" + level });

			for (int i = 0; i < mAlarms.size(); i++) {
				if (mAlarms.get(i).enabled
						&& NewFunctions.hasCharge(isconnected,
								mAlarms.get(i).charge)) {

					if ((mAlarms.get(i).daysOfWeek.getBitSet()) != 0) {
						if (NewFunctions.hasCurrDay(mAlarms.get(i).daysOfWeek
								.getBitSet())) {

							Intent intent;
							intent = new Intent(Intents.ALARM_ALERT_ACTION);
							intent.putExtra(PreferenceHelper.BAT_VALS,
									mAlarms.get(i).id);
							intent.putExtra(PreferenceHelper.CURR_RING, level);
							sendBroadcast(intent);
						}
					} else {

						Intent intent;
						intent = new Intent(Intents.ALARM_ALERT_ACTION);
						intent.putExtra(PreferenceHelper.BAT_VALS,
								mAlarms.get(i).id);
						intent.putExtra(PreferenceHelper.CURR_RING, level);
						sendBroadcast(intent);
					}
				}
			}

			Log.d("Updating Entries and level is " + level);

			ArrayList<Double> vals = new ArrayList<Double>();
			ArrayList<Double> dates = new ArrayList<Double>();

			try {

				vals = SerialPreference.retPrefs(getBaseContext(),
						PreferenceHelper.BAT_VALS);
				dates = SerialPreference.retPrefs(getBaseContext(),
						PreferenceHelper.BAT_TIME);

				if (vals.size() > 100) {
					vals.remove(0);
					dates.remove(0);
				}
				// return false;

				vals.add((double) level);

				dates.add((double) System.currentTimeMillis());

				SerialPreference.savePrefs(getBaseContext(), dates,
						PreferenceHelper.BAT_TIME);
				SerialPreference.savePrefs(getBaseContext(), vals,
						PreferenceHelper.BAT_VALS);

				ArrayList<Double> eDates = new ArrayList<Double>();
				int count = 0;
				for (int i = 20; i > 1; i--) {

					if (vals.get(vals.size() - 1 - i) > vals.get(vals.size()
							- i)) {
						Log.d("Val: "
								+ vals.get(vals.size() - 1 - i)
								+ "Time: "
								+ DateUtils.format(
										(long) (double) dates.get(vals.size()
												- 1 - i),
										DateUtils.DATE_FORMAT_NOW));
						Log.d("Val: "
								+ vals.get(vals.size() - i)
								+ "Time: "
								+ DateUtils.format(
										(long) (double) dates.get(vals.size()
												- i), DateUtils.DATE_FORMAT_NOW));
						count++;
						eDates.add(dates.get(vals.size() - i)
								- dates.get(vals.size() - 1 - i));
					}
				}
				long avgTime = (long) (mPref.getLong(
						PreferenceHelper.BAT_DISCHARGE, 792));
				long sum = 0;
				int flag = 0;
				if (count != 0) {
					for (int i = eDates.size() - 1; i >= 0; i--)
						if (flag < 7) {
							sum = (long) (sum + eDates.get(i));
							flag++;
						}
					if (count > 7)
						count = 7;

					avgTime = sum / count;
					avgTime = avgTime / 1000;
				}

				Log.d("AVGTIME: " + avgTime);

				mPref.edit().putLong(PreferenceHelper.BAT_DISCHARGE, avgTime)
						.commit();

			} catch (Exception ex) {
				Log.d("Exception in updating with error: " + ex.toString());
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			/*
			 * Intent intents = new Intent(ForegroundService.ACTION_FOREGROUND);
			 * intents.setClass(getBaseContext(), ForegroundService.class);
			 * startService(intents);
			 */
			handleCommand(/* intent */);
			super.onPostExecute(result);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	String temperature;
	String health;

	public int readbattery() {
		Intent batteryIntent = getApplicationContext().registerReceiver(null,
				new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		int rawlevel = batteryIntent.getIntExtra("level", -1);
		double scale = batteryIntent.getIntExtra("scale", -1);
		int plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED,
				-1);
		isconnected = (plugged == BatteryManager.BATTERY_PLUGGED_AC
				|| plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS);
		level = -1;
		if (rawlevel >= 0 && scale > 0) {
			level = (int) ((rawlevel * 100) / scale);
			Log.d("rawLevel: " + rawlevel);
			Log.d("scale: " + scale);
		}

		temperature = Functions
				.updateTemperature((float) ((float) (batteryIntent.getIntExtra(
						"temperature", 0)) / 10), mPref.getBoolean(
						PreferenceHelper.MAIN_TEMP, true), true);

		int inthealth = batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH,
				0);
		health = "";
		switch (inthealth) {
		case BatteryManager.BATTERY_HEALTH_COLD:
			health = "Cold";
			break;
		case BatteryManager.BATTERY_HEALTH_DEAD:
			health = "Dead";
			break;
		case BatteryManager.BATTERY_HEALTH_GOOD:
			health = "Good";
			break;
		case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
			health = "Over Voltage";
			break;
		case BatteryManager.BATTERY_HEALTH_OVERHEAT:
			health = "Overheat";
			break;
		case BatteryManager.BATTERY_HEALTH_UNKNOWN:
			health = "Unknown";
			break;
		case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
			health = "Unspecified failure";
			break;
		}

		if (mPref.getBoolean(PreferenceHelper.KEY_ONE_PERCENT_HACK, false)) {
			try {
				java.io.FileReader fReader = new java.io.FileReader(
						"/sys/class/power_supply/battery/charge_counter");
				java.io.BufferedReader bReader = new java.io.BufferedReader(
						fReader);
				int charge_counter = Integer.valueOf(bReader.readLine());
				bReader.close();

				if (charge_counter > PreferenceHelper.CHARGE_COUNTER_LEGIT_MAX) {
					disableOnePercentHack("charge_counter is too big to be actual charge");
				} else {
					if (charge_counter > 100)
						charge_counter = 100;

					level = charge_counter;
				}
			} catch (java.io.FileNotFoundException e) {
				/*
				 * These error messages are only really useful to me and might
				 * as well be left hardwired here in English.
				 */
				disableOnePercentHack("charge_counter file doesn't exist");
			} catch (java.io.IOException e) {
				disableOnePercentHack("Error reading charge_counter file");
			}
		}

		return level;
	}

	private void disableOnePercentHack(String reason) {

		mPref.edit().putBoolean(PreferenceHelper.KEY_ONE_PERCENT_HACK, false)
				.commit();

		saphion.logger.Log.d("Disabling one percent hack due to: " + reason);
	}

	// ----------------------------------------------------------------------

	/**
	 * <p>
	 * Example of explicitly starting and stopping the {@link ForegroundService}.
	 * 
	 * <p>
	 * Note that this is implemented as an inner class only keep the sample all
	 * together; typically this code would appear in some separate class.
	 */

	public static class Controller extends ActionBarActivity {

		SharedPreferences mPref;

		@Override
		public void onBackPressed() {
			finish();
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			super.onBackPressed();
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case android.R.id.home:
				startActivity(new Intent(this,
						saphion.fragments.TabNavigation.class));
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				finish();
				break;
			}
			return super.onOptionsItemSelected(item);
		}

		void saveIcon(ImageView iv, int i) {
			try {
				mPref.edit().putInt(PreferenceHelper.NOTI_THEME, i).commit();
				imgSwitch(iv, getBaseContext());
				if (mPref
						.getBoolean(PreferenceHelper.NOTIFICATION_ENABLE, true)) {
					sendBroadcast(new Intent(Intents.SWITCHER_NOTI));
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
		}

		/**
		 * Installs click and touch listeners on a fake overflow menu button.
		 * 
		 * @param menuButton
		 *            the fragment's fake overflow menu button
		 * @param imageView
		 */
		public void setupFakeOverflowMenuButton1(final View menuButton,
				final ImageView imageView) {
			final saphion.utils.PopupMenu fakeOverflow = new saphion.utils.PopupMenu(
					menuButton.getContext(), menuButton) {
				@Override
				public void show() {
					onPrepareOptionsMenu(getMenu());
					super.show();
				}
			};
			String[] mArray = new String[] { "Stencil Circle", "ICS Circle",
					"Simple Numbers" };
			((TextView) menuButton).setText(mArray[mPref.getInt(
					PreferenceHelper.NOTI_THEME, 0)]);

			Menu menu = fakeOverflow.getMenu();// .inflate(R.menu.bmenu);
			MenuItem noti = menu.add("Stencil Circle");
			noti.setIcon(R.drawable.stencil_72);
			noti.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					saveIcon(imageView, 0);
					String[] mArray2 = new String[] { "Stencil Circle",
							"ICS Circle", "Simple Numbers" };
					((TextView) menuButton).setText(mArray2[0]);
					return true;
				}
			});

			MenuItem prefs = menu.add("ICS Circle");
			prefs.setIcon(R.drawable.ics_circle_72);
			prefs.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					saveIcon(imageView, 1);
					String[] mArray2 = new String[] { "Stencil Circle",
							"ICS Circle", "Simple Numbers" };
					((TextView) menuButton).setText(mArray2[1]);
					return true;
				}
			});

			MenuItem share = menu.add("Simple Numbers");
			share.setIcon(R.drawable.simple_72);
			share.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					saveIcon(imageView, 2);
					String[] mArray2 = new String[] { "Stencil Circle",
							"ICS Circle", "Simple Numbers" };
					((TextView) menuButton).setText(mArray2[2]);
					return true;
				}
			});

			fakeOverflow
					.setOnMenuItemClickListener(new saphion.utils.PopupMenu.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							return onOptionsItemSelected(item);
						}
					});

			menuButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					fakeOverflow.show();
				}
			});

		}

		void savePriority(int position) {
			mPref.edit().putInt(PreferenceHelper.NOTI_PRIORITY, position)
					.commit();
			if (mPref.getBoolean(PreferenceHelper.NOTIFICATION_ENABLE, true)) {
				stopService(new Intent(Controller.this, ForegroundService.class));
				Intent intent = new Intent(ForegroundService.ACTION_FOREGROUND);
				intent.setClass(Controller.this, ForegroundService.class);
				startService(intent);
				sendBroadcast(new Intent(Intents.SWITCHER_NOTI));
			}
		}

		/**
		 * Installs click and touch listeners on a fake overflow menu button.
		 * 
		 * @param menuButton
		 *            the fragment's fake overflow menu button
		 */
		public void setupFakePrioritySpinner(View menuButton) {

			final TextView tv = (TextView) menuButton;
			final String[] mArray = getResources().getStringArray(
					R.array.notipriority);

			tv.setText(mArray[mPref.getInt(PreferenceHelper.NOTI_PRIORITY, 2)]);

			final PopupMenu fakeOverflow = new PopupMenu(
					menuButton.getContext(), menuButton) {
				@Override
				public void show() {
					onPrepareOptionsMenu(getMenu());
					super.show();
				}
			};
			Menu menu = fakeOverflow.getMenu();// .inflate(R.menu.bmenu);
			MenuItem noti = menu.add(mArray[0]);
			// noti.setIcon(R.drawable.noti);
			noti.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					savePriority(0);
					tv.setText(mArray[0]);
					return true;
				}
			});

			MenuItem prefs = menu.add(mArray[1]);
			// prefs.setIcon(R.drawable.prefs);
			prefs.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					savePriority(1);
					tv.setText(mArray[1]);
					return true;
				}
			});

			MenuItem share = menu.add(mArray[2]);
			// share.setIcon(R.drawable.share);
			share.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					savePriority(2);
					tv.setText(mArray[2]);
					return true;
				}
			});

			MenuItem more = menu.add(mArray[3]);
			// more.setIcon(R.drawable.morebydev);
			more.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					savePriority(3);
					tv.setText(mArray[3]);
					return true;
				}
			});

			MenuItem about = menu.add(mArray[4]);
			// about.setIcon(R.drawable.about);
			about.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					savePriority(4);
					tv.setText(mArray[4]);
					return true;
				}
			});
			fakeOverflow
					.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							return onOptionsItemSelected(item);
						}
					});

			menuButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					fakeOverflow.show();
				}
			});
		}

		public void saveOnClick(int position) {
			mPref.edit().putInt(PreferenceHelper.NOTI_ONCLICK, position)
					.commit();
			if (mPref.getBoolean(PreferenceHelper.NOTIFICATION_ENABLE, true)) {
				sendBroadcast(new Intent(Intents.SWITCHER_NOTI));
			}
		}

		/**
		 * Installs click and touch listeners on a fake overflow menu button.
		 * 
		 * @param menuButton
		 *            the fragment's fake overflow menu button
		 */
		public void setupFakeOnClickSpinner(View menuButton) {

			final TextView tv = (TextView) menuButton;
			final String[] mArray = getResources().getStringArray(
					R.array.onclickactions);

			tv.setText(mArray[mPref.getInt(PreferenceHelper.NOTI_ONCLICK, 2)]);

			final PopupMenu fakeOverflow = new PopupMenu(
					menuButton.getContext(), menuButton) {
				@Override
				public void show() {
					onPrepareOptionsMenu(getMenu());
					super.show();
				}
			};
			Menu menu = fakeOverflow.getMenu();// .inflate(R.menu.bmenu);
			MenuItem noti = menu.add(mArray[0]);
			// noti.setIcon(R.drawable.noti);
			noti.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					saveOnClick(0);
					tv.setText(mArray[0]);
					return true;
				}
			});

			MenuItem prefs = menu.add(mArray[1]);
			// prefs.setIcon(R.drawable.prefs);
			prefs.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					saveOnClick(1);
					tv.setText(mArray[1]);
					return true;
				}
			});

			MenuItem share = menu.add(mArray[2]);
			// share.setIcon(R.drawable.share);
			share.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					saveOnClick(2);
					tv.setText(mArray[2]);
					return true;
				}
			});
			MenuItem shares = menu.add(mArray[3]);
			// share.setIcon(R.drawable.share);
			shares.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					saveOnClick(3);
					tv.setText(mArray[3]);
					return true;
				}
			});

			fakeOverflow
					.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							return onOptionsItemSelected(item);
						}
					});

			menuButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					fakeOverflow.show();
				}
			});
		}

		public class Status {
			private boolean isConnected;
			private int mLevel;
			private String temperature;
			private String health;

			public Status(int mLevel, boolean isConnected, String temperature,
					String health) {
				this.mLevel = mLevel;
				this.isConnected = isConnected;
				this.temperature = temperature;
				this.health = health;
			}

			public int getLevel() {
				return this.mLevel;
			}

			public boolean getConnected() {
				return this.isConnected;
			}

			public String getTemperature() {
				return this.temperature;
			}

			public String getHealth() {
				return this.health;
			}
		}

		public Status readBatteryStat() {
			Intent batteryIntent = getBaseContext().registerReceiver(null,
					new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

			int rawlevel = batteryIntent.getIntExtra(
					BatteryManager.EXTRA_LEVEL, -1);
			double scale = batteryIntent.getIntExtra(
					BatteryManager.EXTRA_SCALE, -1);
			int plugged = batteryIntent.getIntExtra(
					BatteryManager.EXTRA_PLUGGED, -1);
			boolean isconnected = (plugged == BatteryManager.BATTERY_PLUGGED_AC
					|| plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS);
			int level = -1;
			if (rawlevel >= 0 && scale > 0) {
				level = (int) ((rawlevel * 100) / scale);
				Log.d("rawLevel: " + rawlevel);
				Log.d("scale: " + scale);
			}

			String temperature = Functions.updateTemperature(
					(float) ((float) (batteryIntent.getIntExtra("temperature",
							0)) / 10), mPref.getBoolean(
							PreferenceHelper.MAIN_TEMP, true), true);

			int inthealth = batteryIntent.getIntExtra(
					BatteryManager.EXTRA_HEALTH, 0);
			String health = "";
			switch (inthealth) {
			case BatteryManager.BATTERY_HEALTH_COLD:
				health = "Cold";
				break;
			case BatteryManager.BATTERY_HEALTH_DEAD:
				health = "Dead";
				break;
			case BatteryManager.BATTERY_HEALTH_GOOD:
				health = "Good";
				break;
			case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
				health = "Over Voltage";
				break;
			case BatteryManager.BATTERY_HEALTH_OVERHEAT:
				health = "Overheat";
				break;
			case BatteryManager.BATTERY_HEALTH_UNKNOWN:
				health = "Unknown";
				break;
			case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
				health = "Unspecified failure";
				break;
			}

			if (mPref.getBoolean(PreferenceHelper.KEY_ONE_PERCENT_HACK, false)) {
				try {
					java.io.FileReader fReader = new java.io.FileReader(
							"/sys/class/power_supply/battery/charge_counter");
					java.io.BufferedReader bReader = new java.io.BufferedReader(
							fReader);
					int charge_counter = Integer.valueOf(bReader.readLine());
					bReader.close();

					if (charge_counter > PreferenceHelper.CHARGE_COUNTER_LEGIT_MAX) {
						disableOnePercentHack("charge_counter is too big to be actual charge");
					} else {
						if (charge_counter > 100)
							charge_counter = 100;

						level = charge_counter;
					}
				} catch (java.io.FileNotFoundException e) {
					/*
					 * These error messages are only really useful to me and
					 * might as well be left hardwired here in English.
					 */
					disableOnePercentHack("charge_counter file doesn't exist");
				} catch (java.io.IOException e) {
					disableOnePercentHack("Error reading charge_counter file");
				}
			}

			return new Status(level, isconnected, temperature, health);
		}

		public void setBothTexts(TextView tv, int ch) {
			String prevTime = mPref.getString(PreferenceHelper.PREV_BAT_TIME,
					TimeFuncs.getCurrentTimeStamp());

			long diff = TimeFuncs.newDiff(TimeFuncs.GetItemDate(prevTime),
					TimeFuncs.GetItemDate(TimeFuncs.getCurrentTimeStamp()));

			Log.d("New diff " + diff);
			long reqTime = 0;
			String subtext;
			Status mStat = readBatteryStat();
			String temperature = mStat.getTemperature();
			String health = mStat.getHealth();
			int level = mStat.getLevel();
			boolean isconnected = mStat.getConnected();
			Log.d("Current Level " + level + "");
			Log.d("Previous Level "
					+ mPref.getInt(PreferenceHelper.PREV_BAT_LEVEL, level) + "");
			if (level < mPref.getInt(PreferenceHelper.PREV_BAT_LEVEL, level)) {

				diff = (long) (mPref.getLong(PreferenceHelper.BAT_DISCHARGE,
						diff));

				reqTime = diff * level;
				subtext = "Empty in " + TimeFuncs.convtohournminnday(reqTime);

				Log.d("Discharging with " + diff);

			} else {
				if (level > mPref
						.getInt(PreferenceHelper.PREV_BAT_LEVEL, level)) {
					if (level != 100
							&& TimeFuncs.convtohournminnday(
									diff * (100 - level)).equalsIgnoreCase(
									"0 Minute(s)")) {
						reqTime = (long) (81 * (100 - level));
						subtext = "Full Charge in "
								+ TimeFuncs.convtohournminnday(reqTime);
					} else {
						reqTime = diff * (100 - level);
						subtext = "Full Charge in "
								+ TimeFuncs.convtohournminnday(reqTime);

					}

					Log.d("Charging with " + diff);

				} else {

					if (isconnected) {
						reqTime = (long) (mPref.getLong(
								PreferenceHelper.BAT_CHARGE, 81) * (100 - level));
						subtext = "Full Charge in "
								+ TimeFuncs.convtohournminnday(reqTime);
						Log.d("Estimating Charging");
						// mPref
						// .putLong("batcharge", diff).commit();
						Log.d("EST Charging with " + diff);

					} else {
						reqTime = (long) (mPref.getLong(
								PreferenceHelper.BAT_DISCHARGE, 792) * (level));
						subtext = "Empty in "
								+ TimeFuncs.convtohournminnday(reqTime);
						Log.d("Estimating Discharging with: "
								+ (long) (mPref.getLong(
										PreferenceHelper.BAT_DISCHARGE, 792)));
					}
				}
			}

			if (level == 100 && isconnected) {
				subtext = "Fully Charged";
				reqTime = 0;
			}

			String mainText = mPref.getString(PreferenceHelper.LAST_CHARGED,
					TimeFuncs.getCurrentTimeStamp());

			if (isconnected) {
				if (mPref.getBoolean("plugged?", true))
					mPref.edit()
							.putString(PreferenceHelper.LAST_CHARGED,
									TimeFuncs.getCurrentTimeStamp()).commit();
				String time = TimeFuncs
						.convtohournminnday(TimeFuncs.newDiff(TimeFuncs
								.GetItemDate(mainText), TimeFuncs
								.GetItemDate(TimeFuncs.getCurrentTimeStamp())));
				if (!time.equals("0 Minute(s)"))
					mainText = "Plugged " + time + " ago";
				else
					mainText = "Plugged " + "right now";

			} else {
				if (!mPref.getBoolean("plugged?", true)) {

					mPref.edit()
							.putString(PreferenceHelper.LAST_CHARGED,
									TimeFuncs.getCurrentTimeStamp()).commit();
				}

				mainText = mPref.getString(PreferenceHelper.LAST_CHARGED,
						TimeFuncs.getCurrentTimeStamp());

				String time = TimeFuncs
						.convtohournminnday(TimeFuncs.newDiff(TimeFuncs
								.GetItemDate(mainText), TimeFuncs
								.GetItemDate(TimeFuncs.getCurrentTimeStamp())));

				if (!time.equals("0 Minute(s)"))
					mainText = "Unplugged " + time + " ago";
				else
					mainText = "Unplugged " + "right now";
			}

			String tempsubtext = subtext;
			if (ch == 1) {
				subtext = setNotText(subtext, mainText,
						mPref.getInt(PreferenceHelper.NOTI_SUBTEXT, 3), level,
						temperature, health, reqTime, isconnected);
				tv.setText(subtext);
			}
			if (ch == 0) {
				mainText = setNotText(tempsubtext, mainText,
						mPref.getInt(PreferenceHelper.NOTI_MAINTEXT, 6), level,
						temperature, health, reqTime, isconnected);
				tv.setText(mainText);
			}
		}

		private void disableOnePercentHack(String reason) {

			mPref.edit()
					.putBoolean(PreferenceHelper.KEY_ONE_PERCENT_HACK, false)
					.commit();

			saphion.logger.Log
					.d("Disabling one percent hack due to: " + reason);
		}

		private String setNotText(String subtext, String mainText, int choice,
				int level, String temperature, String health, long reqTime,
				boolean isconnected) {
			switch (choice) {
			case 0:
				return level + "% remaining";
			case 1:
				return "Temperature: " + temperature;
			case 2:
				return health + ", " + "Temperature: " + temperature;
			case 3:
				return subtext;
			case 4:
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date(System.currentTimeMillis()
						+ (reqTime * 1000)));
				String day1 = "";
				switch (cal.get(Calendar.DAY_OF_WEEK)) {
				case Calendar.MONDAY:
					day1 = "Monday";
					break;
				case Calendar.TUESDAY:
					day1 = "Tuesday";
					break;
				case Calendar.WEDNESDAY:
					day1 = "Wednesday";
					break;
				case Calendar.THURSDAY:
					day1 = "Thursday";
					break;
				case Calendar.FRIDAY:
					day1 = "Friday";
					break;
				case Calendar.SATURDAY:
					day1 = "Saturday";
					break;
				case Calendar.SUNDAY:
					day1 = "Sunday";
					break;
				}
				String str = "";
				if (isconnected) {
					str = "Full Charge at ";
				} else {
					str = "Empty at ";
				}
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.getInstance()
						.get(Calendar.DAY_OF_WEEK)) {
					return str
							+ ((cal.get(Calendar.HOUR) == 0) ? 12 : cal
									.get(Calendar.HOUR))
							+ ":"
							+ ((cal.get(Calendar.MINUTE) + "").length() == 1 ? ("0" + cal
									.get(Calendar.MINUTE)) : cal
									.get(Calendar.MINUTE))
							+ (cal.get(Calendar.AM_PM) == Calendar.AM ? " AM"
									: " PM");
				} else {
					int d = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 1;
					if (d == 8)
						d = 1;
					if (cal.get(Calendar.DAY_OF_WEEK) == d)
						day1 = "Tomorrow";
					return str
							+ ((cal.get(Calendar.HOUR) == 0) ? 12 : cal
									.get(Calendar.HOUR))
							+ ":"
							+ ((cal.get(Calendar.MINUTE) + "").length() == 1 ? ("0" + cal
									.get(Calendar.MINUTE)) : cal
									.get(Calendar.MINUTE))
							+ (cal.get(Calendar.AM_PM) == Calendar.AM ? " AM"
									: " PM") + ", " + day1;
				}
			case 5:
				Date mydate = TimeFuncs.GetItemDate(mPref.getString(
						PreferenceHelper.LAST_CHARGED,
						TimeFuncs.getCurrentTimeStamp()));
				Calendar c = Calendar.getInstance();
				c.setTime(mydate);
				String day = "";
				switch (c.get(Calendar.DAY_OF_WEEK)) {
				case Calendar.MONDAY:
					day = "Monday";
					break;
				case Calendar.TUESDAY:
					day = "Tuesday";
					break;
				case Calendar.WEDNESDAY:
					day = "Wednesday";
					break;
				case Calendar.THURSDAY:
					day = "Thursday";
					break;
				case Calendar.FRIDAY:
					day = "Friday";
					break;
				case Calendar.SATURDAY:
					day = "Saturday";
					break;
				case Calendar.SUNDAY:
					day = "Sunday";
					break;
				}

				if (c.get(Calendar.DAY_OF_WEEK) == Calendar.getInstance().get(
						Calendar.DAY_OF_WEEK)) {
					if (isconnected)
						return "Plugged at "
								+ ((c.get(Calendar.HOUR) == 0) ? 12 : c
										.get(Calendar.HOUR))
								+ ":"
								+ ((c.get(Calendar.MINUTE) + "").length() == 1 ? ("0" + c
										.get(Calendar.MINUTE)) : c
										.get(Calendar.MINUTE))
								+ (c.get(Calendar.AM_PM) == Calendar.AM ? " AM"
										: " PM");
					else
						return "Unplugged at "
								+ ((c.get(Calendar.HOUR) == 0) ? 12 : c
										.get(Calendar.HOUR))
								+ ":"
								+ ((c.get(Calendar.MINUTE) + "").length() == 1 ? ("0" + c
										.get(Calendar.MINUTE)) : c
										.get(Calendar.MINUTE))
								+ (c.get(Calendar.AM_PM) == Calendar.AM ? " AM"
										: " PM");
				} else {
					int d = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
					if (d == 0)
						d = 7;
					if (c.get(Calendar.DAY_OF_WEEK) == d)
						day = "Yesterday";
					if (isconnected)
						return "Plugged at "
								+ ((c.get(Calendar.HOUR) == 0) ? 12 : c
										.get(Calendar.HOUR))
								+ ":"
								+ ((c.get(Calendar.MINUTE) + "").length() == 1 ? ("0" + c
										.get(Calendar.MINUTE)) : c
										.get(Calendar.MINUTE))
								+ (c.get(Calendar.AM_PM) == Calendar.AM ? " AM"
										: " PM") + ", " + day;
					else
						return "Unplugged at "
								+ ((c.get(Calendar.HOUR) == 0) ? 12 : c
										.get(Calendar.HOUR))
								+ ":"
								+ ((c.get(Calendar.MINUTE) + "").length() == 1 ? ("0" + c
										.get(Calendar.MINUTE)) : c
										.get(Calendar.MINUTE))
								+ (c.get(Calendar.AM_PM) == Calendar.AM ? " AM"
										: " PM") + ", " + day;
				}

			case 6:
				return mainText;
			}
			return mainText;

		}

		/**
		 * Installs click and touch listeners on a fake overflow menu button.
		 * 
		 * @param menuButton
		 *            the fragment's fake overflow menu button
		 */
		public void setupFakeOverflowMenuButton2(View menuButton,
				final int choice, final TextView tvt) {
			// TODO
			final String spin;
			final TextView tv = (TextView) menuButton;
			final String[] mArray = getResources().getStringArray(
					R.array.batterynoti);
			if (choice == 0) {
				spin = PreferenceHelper.NOTI_MAINTEXT;
				tv.setText(croppedText(mArray[mPref.getInt(
						PreferenceHelper.NOTI_MAINTEXT, 6)]));

			} else {
				spin = PreferenceHelper.NOTI_SUBTEXT;
				tv.setText(croppedText(mArray[mPref.getInt(
						PreferenceHelper.NOTI_SUBTEXT, 3)]));
			}

			setBothTexts(tvt, choice);

			final PopupMenu fakeOverflow = new PopupMenu(
					menuButton.getContext(), menuButton) {
				@Override
				public void show() {
					onPrepareOptionsMenu(getMenu());
					super.show();
				}
			};
			Menu menu = fakeOverflow.getMenu();// .inflate(R.menu.bmenu);
			MenuItem noti = menu.add("Battery Remaining");
			// noti.setIcon(R.drawable.noti);
			noti.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					mPref.edit().putInt(spin, 0).commit();
					if (mPref.getBoolean(PreferenceHelper.NOTIFICATION_ENABLE,
							true)) {
						sendBroadcast(new Intent(Intents.SWITCHER_NOTI));
					}
					setBothTexts(tvt, choice);
					tv.setText(croppedText(mArray[0]));
					return true;
				}
			});

			MenuItem prefs = menu.add("Temperature");
			// prefs.setIcon(R.drawable.prefs);
			prefs.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					mPref.edit().putInt(spin, 1).commit();
					if (mPref.getBoolean(PreferenceHelper.NOTIFICATION_ENABLE,
							true)) {
						sendBroadcast(new Intent(Intents.SWITCHER_NOTI));
					}
					setBothTexts(tvt, choice);
					tv.setText(croppedText(mArray[1]));
					return true;
				}
			});

			MenuItem share = menu.add("Health, Temperature");
			// share.setIcon(R.drawable.share);
			share.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					mPref.edit().putInt(spin, 2).commit();
					if (mPref.getBoolean(PreferenceHelper.NOTIFICATION_ENABLE,
							true)) {
						sendBroadcast(new Intent(Intents.SWITCHER_NOTI));
					}
					setBothTexts(tvt, choice);
					tv.setText(croppedText(mArray[2]));
					return true;
				}
			});

			MenuItem more = menu
					.add("Duration in which phone will get Charged/Discharged");
			// more.setIcon(R.drawable.morebydev);
			more.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					mPref.edit().putInt(spin, 3).commit();
					if (mPref.getBoolean(PreferenceHelper.NOTIFICATION_ENABLE,
							true)) {
						sendBroadcast(new Intent(Intents.SWITCHER_NOTI));
					}
					setBothTexts(tvt, choice);
					tv.setText(croppedText(mArray[3]));
					return true;
				}
			});

			MenuItem about = menu
					.add("Time at which phone will get Charged/Discharged");
			// about.setIcon(R.drawable.about);
			about.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					mPref.edit().putInt(spin, 4).commit();
					if (mPref.getBoolean(PreferenceHelper.NOTIFICATION_ENABLE,
							true)) {
						sendBroadcast(new Intent(Intents.SWITCHER_NOTI));
					}
					setBothTexts(tvt, choice);
					tv.setText(croppedText(mArray[4]));
					return true;
				}
			});

			MenuItem about2 = menu.add("Time when Charged/Discharged started");
			// about.setIcon(R.drawable.about);
			about2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					mPref.edit().putInt(spin, 5).commit();
					if (mPref.getBoolean(PreferenceHelper.NOTIFICATION_ENABLE,
							true)) {
						sendBroadcast(new Intent(Intents.SWITCHER_NOTI));
					}
					setBothTexts(tvt, choice);
					tv.setText(croppedText(mArray[5]));
					return true;
				}
			});

			MenuItem more2 = menu.add("Duration since last plugged/Unplugged");
			// more.setIcon(R.drawable.morebydev);
			more2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					mPref.edit().putInt(spin, 6).commit();
					if (mPref.getBoolean(PreferenceHelper.NOTIFICATION_ENABLE,
							true)) {
						sendBroadcast(new Intent(Intents.SWITCHER_NOTI));
					}
					setBothTexts(tvt, choice);
					tv.setText(croppedText(mArray[6]));
					return true;
				}
			});

			fakeOverflow
					.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							return onOptionsItemSelected(item);
						}
					});

			menuButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					fakeOverflow.show();
				}
			});
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mPref = getSharedPreferences(getPackageName() + "_preferences",
					MODE_MULTI_PROCESS);
			getSupportActionBar().setBackgroundDrawable(
					getResources().getDrawable(R.drawable.actionbar_back));
			setContentView(R.layout.foreground_service_controller);
			getSupportActionBar().setDisplayShowTitleEnabled(true);
			getSupportActionBar().setTitle("Notification Settings");
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);

			jbSpecificFuncs();

			View mView = findViewById(R.id.layoutnoti);
			setupFakeOverflowMenuButton1(mView.findViewById(R.id.textView1),
					(ImageView) mView.findViewById(R.id.notification_icon));

			setupFakeOverflowMenuButton2(mView.findViewById(R.id.textView2), 0,
					(TextView) mView.findViewById(R.id.tvNotiPrevmainText));
			setupFakeOverflowMenuButton2(mView.findViewById(R.id.textView3), 1,
					(TextView) mView.findViewById(R.id.tvNotiPrevsubText));

			imgSwitch((ImageView) mView.findViewById(R.id.notification_icon),
					getBaseContext());

			TextView onClick = (TextView) findViewById(R.id.tvnotionclick);

			setupFakeOnClickSpinner(onClick);

		}

		public CharSequence croppedText(String string) {

			if (string.length() > 15) {
				string = string.substring(0, 14);
				string = string + "..";
			}

			return string;
		}

		/**
		 * Installs click and touch listeners on a fake overflow menu button.
		 * 
		 * @param menuButton
		 *            the fragment's fake overflow menu button
		 */
		public void setupFakeProfileSpinners(View menuButton, int choice,
				final ArrayList<String> profnames) {
			final String spin;
			final TextView tv = (TextView) menuButton;
			if (choice == 0) {
				spin = PreferenceHelper.PROF1;
				String prof = profnames.get(0);
				if (profnames.size() > mPref.getInt(PreferenceHelper.PROF1, 0)) {
					prof = profnames.get(mPref
							.getInt(PreferenceHelper.PROF1, 0));
				}
				tv.setText(prof);

			} else {
				spin = PreferenceHelper.PROF2;
				String prof = profnames.get(0);
				if (profnames.size() > mPref.getInt(PreferenceHelper.PROF2, 0)) {
					prof = profnames.get(mPref
							.getInt(PreferenceHelper.PROF2, 0));
				}
				tv.setText(prof);
			}

			final PopupMenu fakeOverflow = new PopupMenu(
					menuButton.getContext(), menuButton) {
				@Override
				public void show() {
					onPrepareOptionsMenu(getMenu());
					super.show();
				}
			};

			Menu menu = fakeOverflow.getMenu();// .inflate(R.menu.bmenu);
			for (int i = 0; i < profnames.size(); i++) {
				menu.add(profnames.get(i));
			}

			fakeOverflow
					.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							mPref.edit()
									.putInt(spin,
											profnames.indexOf(item.getTitle()))
									.commit();
							tv.setText(item.getTitle());
							if (mPref.getBoolean(
									PreferenceHelper.NOTIFICATION_ENABLE, true)) {

								sendBroadcast(new Intent(Intents.SWITCHER_NOTI));
							}
							return true;
						}
					});

			menuButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					fakeOverflow.show();
				}
			});
		}

		public void jbSpecificFuncs() {

			final View extView = (View) findViewById(R.id.layoutToggles);
			CheckBox showChart = (CheckBox) extView
					.findViewById(R.id.cbShowGraph);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				showChart.setVisibility(View.VISIBLE);
				extView.setVisibility(View.VISIBLE);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
					((CheckBox) extView.findViewById(R.id.cbnbamode))
							.setVisibility(View.GONE);
				else
					((CheckBox) extView.findViewById(R.id.cbnbamode))
							.setVisibility(View.VISIBLE);

				CheckBox amode, mdata, bness, sync, ps, btooth, sChart, wifi, arotate, hotspot;
				amode = ((CheckBox) extView.findViewById(R.id.cbnbamode));
				mdata = ((CheckBox) extView.findViewById(R.id.cbnbmdata));
				bness = ((CheckBox) extView.findViewById(R.id.cbnbbness));
				sync = ((CheckBox) extView.findViewById(R.id.cbnbsync));
				ps = ((CheckBox) extView.findViewById(R.id.cbnbprofileswitcher));
				btooth = ((CheckBox) extView.findViewById(R.id.cbnbbtooth));
				sChart = showChart;
				wifi = ((CheckBox) extView.findViewById(R.id.cbnbwifi));
				arotate = ((CheckBox) extView.findViewById(R.id.cbnbarotate));
				final CheckBox torch = ((CheckBox) extView
						.findViewById(R.id.cbnbtorch));
				hotspot = ((CheckBox) extView
						.findViewById(R.id.cbnbwifihotspot));

				hotspot.setChecked(mPref.getBoolean(
						PreferenceHelper.SHOW_WIFIHOTSPOT, false));

				new AsyncTask<Void, Void, Void>() {
					boolean hasFlash;

					@Override
					protected void onPreExecute() {

						if (mPref.getBoolean(PreferenceHelper.HAS_FLASH, true)) {
							torch.setVisibility(View.VISIBLE);
						} else {
							torch.setVisibility(View.GONE);
						}
						super.onPreExecute();
					}

					@Override
					protected Void doInBackground(Void... params) {
						try {
							Looper.prepare();
						} catch (Exception ex) {
						}
						CameraDevice cd = new CameraDevice();
						cd.acquireCamera();
						hasFlash = cd.supportsTorchMode();
						cd.releaseCamera();
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						if (hasFlash)
							torch.setVisibility(View.VISIBLE);
						else
							torch.setVisibility(View.GONE);
						mPref.edit()
								.putBoolean(PreferenceHelper.HAS_FLASH,
										hasFlash).commit();
						super.onPostExecute(result);
					}

				}.execute();
				/*
				 * try { CameraDevice cd = new CameraDevice();
				 * cd.acquireCamera(); boolean hasFlash =
				 * cd.supportsTorchMode(); cd.releaseCamera();
				 * 
				 * if (hasFlash) torch.setVisibility(View.VISIBLE); else
				 * torch.setVisibility(View.GONE); } catch (Exception e) {
				 * e.printStackTrace(); }
				 */
				torch.setChecked(mPref.getBoolean(PreferenceHelper.SHOW_TORCH,
						false));

				arotate.setChecked(mPref.getBoolean(
						PreferenceHelper.SHOW_AROTATE, false));

				amode.setChecked(mPref.getBoolean(PreferenceHelper.SHOW_AMODE,
						true));
				mdata.setChecked(mPref.getBoolean(PreferenceHelper.SHOW_MDATA,
						true));
				bness.setChecked(mPref.getBoolean(PreferenceHelper.SHOW_BNESS,
						true));
				sync.setChecked(mPref.getBoolean(PreferenceHelper.SHOW_SYNC,
						true));
				ps.setChecked(mPref.getBoolean(PreferenceHelper.SHOW_PSWITCHER,
						false));
				btooth.setChecked(mPref.getBoolean(
						PreferenceHelper.SHOW_BTOOTH, true));
				sChart.setChecked(mPref.getBoolean(PreferenceHelper.SHOW_CHART,
						true));
				wifi.setChecked(mPref.getBoolean(PreferenceHelper.SHOW_WIFI,
						true));
				final LinearLayout ll = (LinearLayout) extView
						.findViewById(R.id.llpowerswitcher);
				if (ps.isChecked()) {
					ll.setVisibility(View.VISIBLE);
				} else {
					ll.setVisibility(View.GONE);
				}

				TextView ps1 = (TextView) extView
						.findViewById(R.id.tvpowerprof1);
				TextView ps2 = (TextView) extView
						.findViewById(R.id.tvpowerprof2);

				ArrayList<String> profnames = null;
				try {
					profnames = PowerPreference.getProfileName(PowerPreference
							.retPower(getBaseContext()));
				} catch (Exception ex) {

				}

				if (profnames != null) {
					if (profnames.size() > 0) {
						setupFakeProfileSpinners(ps1, 0, profnames);
						setupFakeProfileSpinners(ps2, 1, profnames);
					}
				} else {
					ps.setChecked(false);
					mPref.edit()
							.putBoolean(PreferenceHelper.SHOW_PSWITCHER, false)
							.commit();
				}

				OnCheckedChangeListener listener = new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						String toSave = "";
						switch (buttonView.getId()) {
						case R.id.cbnbwifihotspot:
							toSave = PreferenceHelper.SHOW_WIFIHOTSPOT;
							break;
						case R.id.cbnbtorch:
							toSave = PreferenceHelper.SHOW_TORCH;
							break;
						case R.id.cbnbamode:
							toSave = PreferenceHelper.SHOW_AMODE;
							break;
						case (R.id.cbnbmdata):
							toSave = PreferenceHelper.SHOW_MDATA;
							break;
						case (R.id.cbnbbness):
							toSave = PreferenceHelper.SHOW_BNESS;
							break;
						case (R.id.cbnbsync):
							toSave = PreferenceHelper.SHOW_SYNC;
							break;
						case (R.id.cbnbprofileswitcher):
							toSave = PreferenceHelper.SHOW_PSWITCHER;
							if (isChecked) {
								ll.setVisibility(View.VISIBLE);
								((ScrollView) findViewById(R.id.svMain))
										.post(new Runnable() {

											@Override
											public void run() {
												((ScrollView) findViewById(R.id.svMain))
														.fullScroll(ScrollView.FOCUS_DOWN);
											}
										});
							} else
								ll.setVisibility(View.GONE);
							break;
						case (R.id.cbnbbtooth):
							toSave = PreferenceHelper.SHOW_BTOOTH;
							break;
						case (R.id.cbShowGraph):
							toSave = PreferenceHelper.SHOW_CHART;
							break;
						case (R.id.cbnbwifi):
							toSave = PreferenceHelper.SHOW_WIFI;
							break;
						case R.id.cbnbarotate:
							toSave = PreferenceHelper.SHOW_AROTATE;
						}

						mPref.edit().putBoolean(toSave, isChecked).commit();
						/*
						 * Intent intent = new Intent(
						 * ForegroundService.ACTION_FOREGROUND);
						 * intent.setClass(Controller.this,
						 * ForegroundService.class); startService(intent);
						 */sendBroadcast(new Intent(Intents.SWITCHER_NOTI));

					}
				};
				amode.setOnCheckedChangeListener(listener);
				mdata.setOnCheckedChangeListener(listener);
				bness.setOnCheckedChangeListener(listener);
				sync.setOnCheckedChangeListener(listener);
				ps.setOnCheckedChangeListener(listener);
				btooth.setOnCheckedChangeListener(listener);
				sChart.setOnCheckedChangeListener(listener);
				wifi.setOnCheckedChangeListener(listener);
				arotate.setOnCheckedChangeListener(listener);
				torch.setOnCheckedChangeListener(listener);
				hotspot.setOnCheckedChangeListener(listener);

				TextView notiP = (TextView) extView
						.findViewById(R.id.tvnotipriority);
				setupFakePrioritySpinner(notiP);
				/*
				 * notiP.setSelection(mPref.getInt(PreferenceHelper.NOTI_PRIORITY
				 * , 2)); notiP.setOnItemSelectedListener(new
				 * OnItemSelectedListener() {
				 * 
				 * @Override public void onItemSelected(AdapterView<?> parent,
				 * View view, int position, long id) { mPref.edit()
				 * .putInt(PreferenceHelper.NOTI_PRIORITY, position).commit();
				 * if (mPref.getBoolean( PreferenceHelper.NOTIFICATION_ENABLE,
				 * true)) { stopService(new Intent(Controller.this,
				 * ForegroundService.class)); Intent intent = new Intent(
				 * ForegroundService.ACTION_FOREGROUND);
				 * intent.setClass(Controller.this, ForegroundService.class);
				 * startService(intent); sendBroadcast(new
				 * Intent(Intents.SWITCHER_NOTI)); }
				 * 
				 * }
				 * 
				 * @Override public void onNothingSelected(AdapterView<?>
				 * parent) { } });
				 */
			} else {
				showChart.setVisibility(View.GONE);
				extView.setVisibility(View.GONE);
			}

		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// new MenuInflater(this).inflate(R.menu.actions, menu);
			// getMenuInflater().inflate(R.menu.actions, menu);
			MenuItem mSwitch = menu.add("Switch");
			// mSwitch.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			MenuItemCompat.setShowAsAction(mSwitch,
					MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
			configureActionItem(menu, mSwitch);

			return (super.onCreateOptionsMenu(menu));
		}

		private void configureActionItem(Menu menu, MenuItem mItem) {

			MenuItemCompat.setActionView(mItem, R.layout.myswitch);

			Switch switcher = (Switch) MenuItemCompat.getActionView(mItem)
					.findViewById(R.id.notiSwitch);
			switcher.setChecked(mPref.getBoolean(
					PreferenceHelper.NOTIFICATION_ENABLE, true));

			if (switcher.isChecked()) {
                (findViewById(R.id.rltotoggle))
						.setVisibility(View.VISIBLE);
				(findViewById(R.id.tvnotificationDisable))
						.setVisibility(View.GONE);
			} else {
				(findViewById(R.id.tvnotificationDisable))
						.setVisibility(View.VISIBLE);
				(findViewById(R.id.rltotoggle))
						.setVisibility(View.GONE);
			}
			switcher.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						((TextView) findViewById(R.id.tvnotificationDisable))
								.setVisibility(View.GONE);
						((LinearLayout) findViewById(R.id.rltotoggle))
								.setVisibility(View.VISIBLE);
						mPref.edit()
								.putBoolean(
										PreferenceHelper.NOTIFICATION_ENABLE,
										true).commit();

						// sendBroadcast(new Intent(Intents.SWITCHER_NOTI));

						Intent intent = new Intent(
								ForegroundService.ACTION_FOREGROUND);
						intent.setClass(Controller.this,
								ForegroundService.class);
						stopService(intent);
						startService(intent);
						sendBroadcast(new Intent(Intents.SWITCHER_NOTI));

					} else {
						((TextView) findViewById(R.id.tvnotificationDisable))
								.setVisibility(View.VISIBLE);
						((LinearLayout) findViewById(R.id.rltotoggle))
								.setVisibility(View.GONE);
						mPref.edit()
								.putBoolean(
										PreferenceHelper.NOTIFICATION_ENABLE,
										false).commit();
						/*
						 * Intent intent = new Intent(
						 * ForegroundService.ACTION_FOREGROUND);
						 * intent.setClass(Controller.this,
						 * ForegroundService.class); stopService(intent);
						 * startService(intent);
						 */
						sendBroadcast(new Intent(Intents.SWITCHER_NOTI));
					}

				}
			});
		}

	}

	public static void imgSwitch(ImageView iv, Context mContext) {
		switch (mContext.getSharedPreferences(
				mContext.getPackageName() + "_preferences", MODE_MULTI_PROCESS)
				.getInt(PreferenceHelper.NOTI_THEME, 0)) {
		case 0:
			iv.setImageResource(R.drawable.stencil_72);
			break;
		case 1:
			iv.setImageResource(R.drawable.ics_circle_72);
			break;
		case 2:
			iv.setImageResource(R.drawable.simple_72);
			break;

		}

	}

}
