package saphion.services;

import saphion.batterycaster.R;
import saphion.fragments.TabNavigation;
import saphion.stencil.batterywidget.BatteryPreference;
import saphion.stencil.batterywidget.Battery_Actions;
import saphion.stencil.batterywidget.WidPrefHelper;
import saphion.utils.Functions;
import saphion.utils.PreferenceHelper;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class UpdateService extends Service {
	private static final boolean DEBUG = false;
	private static final String TAG = "UpdateService";

	private static boolean mRunning = false;

	// private int mLevel;
	PendingIntent mPendingIntent;
	boolean isconnected;
	int level;

	private static ComponentName mComponentName;
	private static AppWidgetManager mAppWidgetManager;

	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (DEBUG)
				Log.v(TAG, "onReceive");
			String action = intent.getAction();

			int rawlevel = intent.getIntExtra("level", -1);
			double scale = intent.getIntExtra("scale", -1);
			int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
			isconnected = (plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB);
			level = -1;
			if (rawlevel >= 0 && scale > 0) {
				level = (int) ((rawlevel * 100) / scale);
			}
			temperature = (float) ((float) (intent
					.getIntExtra("temperature", 0)) / 10);

			if (getBaseContext().getSharedPreferences("saphion.batterycaster_preferences",
					Context.MODE_MULTI_PROCESS)
					.getBoolean(PreferenceHelper.KEY_ONE_PERCENT_HACK, false)) {
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

			mComponentName = new ComponentName(UpdateService.this,
					Battery_Actions.class);
			mAppWidgetManager = AppWidgetManager
					.getInstance(UpdateService.this);

			ids = mAppWidgetManager.getAppWidgetIds(mComponentName);

			updateWidget(ids, action.equals(Intent.ACTION_BATTERY_CHANGED));
		}
	};

	private void disableOnePercentHack(String reason) {

		getBaseContext().getSharedPreferences("saphion.batterycaster_preferences",
				Context.MODE_MULTI_PROCESS).edit()
				.putBoolean(PreferenceHelper.KEY_ONE_PERCENT_HACK, false)
				.commit();

		saphion.logger.Log.d("Disabling one percent hack due to: " + reason);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	int[] ids;
	private float temperature;

	@Override
	public void onCreate() {
		if (DEBUG)
			Log.v(TAG, "onCreate");

		mRunning = true;

		mComponentName = new ComponentName(this, Battery_Actions.class);
		mAppWidgetManager = AppWidgetManager.getInstance(this);

		ids = mAppWidgetManager.getAppWidgetIds(mComponentName);

		// receive ACTION_BATTERY_CHANGED.
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		filter.addAction(Intent.ACTION_TIME_TICK);
		filter.addAction(Intent.ACTION_TIME_CHANGED);
		filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		filter.addAction(Intent.ACTION_DATE_CHANGED);
		filter.addAction("android.intent.action.TIME_SET");
		filter.addAction("android.intent.action.ALARM_CHANGED");

		registerReceiver(mBroadcastReceiver, filter);

		// set preference activity intent.
		// Intent clickIntent = new Intent(this,
		// BatteryStatusSquarePreference.class);
		// mPendingIntent = PendingIntent.getActivity(this, 0, clickIntent, 0);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if (DEBUG)
			Log.v(TAG, "onStart");

		if (intent.getExtras() != null) {
			final int[] widgetIds = intent
					.getIntArrayExtra(PreferenceHelper.EXTRA_WIDGET_IDS);
			if (widgetIds != null) {
				try {
					updateWidget(
							widgetIds,
							intent.getAction().equals(
									Intent.ACTION_BATTERY_CHANGED));
				} catch (NullPointerException c) {
					try {
						updateWidget(widgetIds, false);
					} catch (RuntimeException ex) {
					}
				}
			}
		} else {
			int[] widgetIds = mAppWidgetManager.getAppWidgetIds(mComponentName);
			try {
				updateWidget(widgetIds,
						intent.getAction()
								.equals(Intent.ACTION_BATTERY_CHANGED));
			} catch (NullPointerException c) {
				try {
					updateWidget(widgetIds, false);
				} catch (RuntimeException ex) {
				}
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (DEBUG)
			Log.v(TAG, "onStartCommand");

		if (intent != null) {
			final int[] widgetIds = intent
					.getIntArrayExtra(PreferenceHelper.EXTRA_WIDGET_IDS);
			if (widgetIds != null) {
				try {
					updateWidget(
							widgetIds,
							intent.getAction().equals(
									Intent.ACTION_BATTERY_CHANGED));
				} catch (NullPointerException c) {
					try {
						updateWidget(widgetIds, false);
					} catch (RuntimeException ex) {
					}
				}
			}
		} else {
			int[] widgetIds = mAppWidgetManager.getAppWidgetIds(mComponentName);
			try {
				updateWidget(widgetIds, false);
			} catch (NullPointerException c) {
				try {
					updateWidget(widgetIds, false);
				} catch (RuntimeException ex) {
				}
			}
		}

		PendingIntent sen2 = PendingIntent
				.getBroadcast(getBaseContext(), 0, new Intent(getBaseContext(),
						BatteryWidgetUpdateReceiver.class), 0);
		long firstTime = SystemClock.elapsedRealtime() + 300000;
		AlarmManager am = (AlarmManager) getBaseContext().getSystemService(
				Context.ALARM_SERVICE);
		am.cancel(sen2);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
				300000, sen2);

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		if (DEBUG)
			Log.v(TAG, "onDestroy");

		unregisterReceiver(mBroadcastReceiver);

		mRunning = false;
	}

	public static boolean isRunning() {
		return mRunning;
	}

	public void updateWidget(final int[] widgetIds, final boolean b) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (widgetIds == null) {
						mComponentName = new ComponentName(UpdateService.this,
								Battery_Actions.class);
						mAppWidgetManager = AppWidgetManager
								.getInstance(UpdateService.this);

						ids = mAppWidgetManager.getAppWidgetIds(mComponentName);
						myAsync(ids, b);
					} else
						myAsync(widgetIds, b);

				} catch (Exception ignore) {
				}
			}
		}).start();

		/*
		 * try { new myAsync().execute(b); } catch (RuntimeException ex) { }
		 */

	}

	public void myAsync(int[] ids, boolean b) {

		for (int awID : ids) {
			RemoteViews views;
			String file = PreferenceHelper.SETTINGS_WIDGET_FILE + awID;
			SharedPreferences getPrefs = getSharedPreferences(file,
					Context.MODE_PRIVATE);
			PendingIntent pendingIntent;
			Intent localIntent;
			views = new RemoteViews(getPackageName(), R.layout.main_widget);

			views.setViewVisibility(R.id.pbmainwidget, View.VISIBLE);
			views.setViewVisibility(R.id.ivmainwidget, View.INVISIBLE);

			localIntent = new Intent();
			localIntent.setFlags(346030080);
			localIntent.setAction("android.intent.action.POWER_USAGE_SUMMARY");
			WidPrefHelper wph = new WidPrefHelper(getBaseContext(), awID);
			if (wph.getWidgetClick() == 0) {
				localIntent = new Intent(getBaseContext(),
						BatteryPreference.class).putExtra(
						AppWidgetManager.EXTRA_APPWIDGET_ID, awID);
			} else if (wph.getWidgetClick() == 1) {
				localIntent = new Intent(getBaseContext(), TabNavigation.class);
			} else if (wph.getWidgetClick() == 2) {
				localIntent = new Intent();
				localIntent.setFlags(346030080);
				localIntent
						.setAction("android.intent.action.POWER_USAGE_SUMMARY");
			} else {
				localIntent = new Intent(getBaseContext(),
						BatteryPreference.class).putExtra(
						AppWidgetManager.EXTRA_APPWIDGET_ID, awID);
			}

			pendingIntent = PendingIntent.getActivity(getBaseContext(), awID,
					localIntent, 0);
			if (!getPrefs.getBoolean(PreferenceHelper.DISABLE_CLICKS, false))
				views.setOnClickPendingIntent(R.id.ivmainwidget, pendingIntent);
			else {
				try {
					views.setOnClickPendingIntent(R.id.ivmainwidget, null);
				} catch (RuntimeException ex) {
				}

			}

			// localIntent = new Intent(getBaseContext(),
			// BatteryPreference.class)
			// .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, awID);

			// pendingIntent = PendingIntent.getActivity(getBaseContext(), awID,
			// localIntent, 0);
			// views.setOnClickPendingIntent(R.id.ivmainwidget, pendingIntent);

			Bitmap b2;

			if (b)
				b2 = Functions.battery(level, temperature, isconnected,
						getBaseContext(), wph, awID);

			else {
				readbattery();
				b2 = Functions.battery(level, temperature, isconnected,
						getBaseContext(), wph, awID);
			}

			views.setViewVisibility(R.id.pbmainwidget, View.INVISIBLE);
			views.setViewVisibility(R.id.ivmainwidget, View.VISIBLE);

			views.setImageViewBitmap(R.id.ivmainwidget, b2);

			mAppWidgetManager.updateAppWidget(awID, views);// .updateAppWidget(mComponentName,
															// views);
		}

	}

	public int readbattery() {
		Intent batteryIntent = getApplicationContext().registerReceiver(null,
				new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		int rawlevel = batteryIntent.getIntExtra("level", -1);
		double scale = batteryIntent.getIntExtra("scale", -1);
		int plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED,
				-1);
		isconnected = (plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB);
		temperature = (float) ((float) (batteryIntent.getIntExtra(
				"temperature", 0)) / 10);
		level = -1;
		if (rawlevel >= 0 && scale > 0) {
			level = (int) ((rawlevel * 100) / scale);
		}
		return level;
	}

}
