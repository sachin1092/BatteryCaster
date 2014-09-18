package saphion.batterycaster;

import saphion.services.ForegroundService;
import saphion.utils.ActivityFuncs;
import saphion.utils.PreferenceHelper;
import saphion.utils.TimeFuncs;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;



/**
 * @author Sachin
 * 
 */
public class MainBatteryPage extends ActionBarActivity {

	private IntentFilter mIntentFilter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.WazaBe.HoloEverywhere.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.setContentView(R.layout.main_page);
		super.onCreate(savedInstanceState);

		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		mIntentFilter.addAction(Intent.ACTION_TIME_TICK);

		LayoutParams params = ((LinearLayout) findViewById(R.id.llmainpage))
				.getLayoutParams();

		if (getResources().getDisplayMetrics().widthPixels > getResources()
				.getDisplayMetrics().heightPixels) {
			params.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.82);
			params.width = (int) (getResources().getDisplayMetrics().widthPixels / 2);
		} else {
			params.height = (int) (getResources().getDisplayMetrics().widthPixels * 0.70);
			params.width = (int) (getResources().getDisplayMetrics().widthPixels);
		}

		((LinearLayout) findViewById(R.id.llmainpage)).setLayoutParams(params);
		

		Intent intent = new Intent(ForegroundService.ACTION_FOREGROUND);
		intent.setClass(this, ForegroundService.class);
		startService(intent);

	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(batteryReceiver);
	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(batteryReceiver, mIntentFilter);
		// buildChart();
	}

	private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
		private String mStatus;
		private String mHealth;
		private String mLastCharged;
		private boolean isconnected;

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				int rawlevel = intent.getIntExtra("level", -1);
				double scale = intent.getIntExtra("scale", -1);
				int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,
						-1);
				isconnected = (plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB);
				int mLevel = -1;
				if (rawlevel >= 0 && scale > 0) {
					mLevel = (int) ((rawlevel * 100) / scale);
				}
				// int mScale = intent.getIntExtra("scale", 0);
				int voltage = intent.getIntExtra("voltage", 0);
				String voltageRes = voltage > 1000 ? "mV" : "V";
				String mVoltage = voltage + " " + voltageRes;

				String temperature = updateTemperature(intent.getIntExtra(
						"temperature", 0), getSharedPreferences("saphion.batterycaster_preferences",
								Context.MODE_MULTI_PROCESS)
						.getBoolean(PreferenceHelper.MAIN_TEMP, false));
				String mTechnology = intent.getStringExtra("technology");

				int status = intent.getIntExtra("status",
						BatteryManager.BATTERY_STATUS_UNKNOWN);
				switch (status) {
				case BatteryManager.BATTERY_STATUS_CHARGING:
					String statusString = context
							.getString(R.string.battery_info_status_charging);
					int plugType = intent.getIntExtra(
							BatteryManager.EXTRA_PLUGGED, 0);
					if (plugType > 0) {
						statusString = statusString
								+ " "
								+ getBaseContext()
										.getString(
												(plugType == BatteryManager.BATTERY_PLUGGED_AC) ? R.string.battery_info_status_charging_ac
														: R.string.battery_info_status_charging_usb);
					}
					mStatus = (statusString);
					break;
				case BatteryManager.BATTERY_STATUS_DISCHARGING:

					mStatus = getResources().getString(
							R.string.battery_info_status_discharging);
					break;
				case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
					mStatus = getResources().getString(
							R.string.battery_info_status_not_charging);
					break;
				case BatteryManager.BATTERY_STATUS_FULL:
					mStatus = getResources().getString(
							R.string.battery_info_status_full);
					break;
				default:
					mStatus = "Unknown";
					break;
				}

				switch (intent.getIntExtra("health",
						BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
				case BatteryManager.BATTERY_HEALTH_GOOD:
					mHealth = getResources().getString(
							R.string.battery_info_health_good);
					break;
				case BatteryManager.BATTERY_HEALTH_OVERHEAT:
					mHealth = getResources().getString(
							R.string.battery_info_health_overheat);
					break;
				case BatteryManager.BATTERY_HEALTH_DEAD:
					mHealth = getResources().getString(
							R.string.battery_info_health_dead);
					break;
				case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
					mHealth = getResources().getString(
							R.string.battery_info_health_over_voltage);
					break;
				case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
					mHealth = getResources().getString(
							R.string.battery_info_health_unspecified_failure);
					break;
				default:
					mHealth = "N/A";
					break;
				}

				String lastCharged;
				if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
					lastCharged = "Connected";
				} else {
					lastCharged = TimeFuncs
							.convtohournminnday(TimeFuncs.newDiff(
									TimeFuncs.GetItemDate(TimeFuncs
											.getCurrentTimeStamp()),
									TimeFuncs
											.GetItemDate(getSharedPreferences("saphion.batterycaster_preferences",
													Context.MODE_MULTI_PROCESS)
													.getString(
															PreferenceHelper.LAST_CHARGED,
															TimeFuncs
																	.getCurrentTimeStamp()))));// DateFormat.getDateTimeInstance().format(
					// BatteryLevel.lastCharged);
				}
				mLastCharged = lastCharged;

				((ImageView) findViewById(R.id.ivMainPage))
						.setImageBitmap(ActivityFuncs.battery(mLevel,
								getBaseContext(),100));/*
													 * ActivityFuncs.
													 * CreateImageBitmap(
													 * getBaseContext(), mLevel,
													 * mVoltage, temperature,
													 * mTechnology, mStatus,
													 * mHealth, mLastCharged));
													 */
				((ImageView) findViewById(R.id.ivMainPage))
						.setImageBitmap(ActivityFuncs.CreateImageBitmap(
								getBaseContext(), mLevel, mVoltage,
								temperature, mTechnology, mStatus, mHealth,
								mLastCharged, isconnected));

			}
		}
	};

	public String updateTemperature(int temperature, boolean bool) {
		if (!bool) {
			return temperature + "�C";

		} else {
			return adjustLenght(String.valueOf(((int) (Integer
					.valueOf((temperature)) / 0.56) + 32))) + "�F";
		}
	}

	public String adjustLenght(String s) {
		if (s.length() > 4) {
			s = s.substring(0, 4);
		}
		return s;
	}

}
