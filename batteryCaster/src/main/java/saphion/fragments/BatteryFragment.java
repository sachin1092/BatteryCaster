package saphion.fragments;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

import java.util.ArrayList;

import saphion.batterycaster.AboutClass;
import saphion.batterycaster.MainPreference;
import saphion.batterycaster.R;
import saphion.batterylib.HistoryItem;
import saphion.logger.Log;
import saphion.services.ForegroundService;
import saphion.services.ForegroundService.Controller;
import saphion.utils.ActivityFuncs;
import saphion.utils.Constants;
import saphion.utils.Functions;
import saphion.utils.PreferenceHelper;
import saphion.utils.TimeFuncs;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("InlinedApi")
public final class BatteryFragment extends Fragment {
	private static final String KEY_CONTENT = "TestFragment:Content";
	public final String PREF_NAME = "saphion.batterycaster_preferences";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// setHasOptionsMenu(true);
		super.onActivityCreated(savedInstanceState);
	}

	public static BatteryFragment newInstance(String content) {
		BatteryFragment fragment = new BatteryFragment();

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 20; i++) {
			builder.append(content).append(" ");
		}
		builder.deleteCharAt(builder.length() - 1);
		fragment.mContent = builder.toString();

		return fragment;
	}

	Boolean prevConnected;

	private String mContent = "???";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ((savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT)) {
			mContent = savedInstanceState.getString(KEY_CONTENT);
		}

	}

	double lastentry;
	ArrayList<Double> vals;
	ArrayList<Double> dates;
	ArrayList<HistoryItem> mList;

	@Override
	public void onPause() {
		super.onPause();
		try {
			getBaseContext().unregisterReceiver(batteryReceiver);
		} catch (Exception ex) {
			Log.d(ex.toString());
		}
	}

	@Override
	public void onResume() {
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		mIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
		mIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
		getBaseContext().registerReceiver(batteryReceiver, mIntentFilter);

		// (new LoadStatData()).execute();
		Intent intent = new Intent(ForegroundService.ACTION_FOREGROUND);
		intent.setClass(k.getContext(), ForegroundService.class);
		k.getContext().startService(intent);
		super.onResume();
	}

	private boolean isconnected = false;
	private int level = 0;
	BroadcastReceiver batteryReceiver = new BroadcastReceiver() {

		@SuppressLint("InlinedApi")
		@Override
		public void onReceive(Context context, Intent intent) {

			try {

				if (intent.getAction() == Intent.ACTION_POWER_CONNECTED
						|| intent.getAction() == Intent.ACTION_POWER_DISCONNECTED) {

					animate(k.findViewById(R.id.ivHourGlass)).rotationBy(360)
							.setDuration(700);
					return;
				}

				// Log.Toast(getBaseContext(), "Recieved", Toast.LENGTH_LONG);

				int rawlevel = intent.getIntExtra("level", -1);
				double scale = intent.getIntExtra("scale", -1);
				int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,
						-1);
				isconnected = (plugged == BatteryManager.BATTERY_PLUGGED_AC
						|| plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS);

				level = -1;
				if (rawlevel >= 0 && scale > 0) {
					level = (int) ((rawlevel * 100) / scale);
					Log.d("rawLevel: " + rawlevel);
					Log.d("scale: " + scale);
				}

				String temperature = Functions
						.updateTemperature(
								(float) ((float) (intent.getIntExtra(
										"temperature", 0)) / 10),
								getActivity().getSharedPreferences(PREF_NAME,
										Context.MODE_MULTI_PROCESS).getBoolean(
										PreferenceHelper.MAIN_TEMP, true), true);

				if (getActivity().getSharedPreferences(PREF_NAME + "_new",
						Context.MODE_MULTI_PROCESS).getBoolean(
						PreferenceHelper.KEY_ONE_PERCENT_HACK, false)) {
					try {
						java.io.FileReader fReader = new java.io.FileReader(
								"/sys/class/power_supply/battery/charge_counter");
						java.io.BufferedReader bReader = new java.io.BufferedReader(
								fReader);
						int charge_counter = Integer
								.valueOf(bReader.readLine());
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

				/*
				 * ((ImageView) k.findViewById(R.id.ivbatmain))
				 * .setImageBitmap(ActivityFuncs.newbattery(level,
				 * getBaseContext(), getBaseContext().getResources()
				 * .getDisplayMetrics().widthPixels, isconnected));
				 */

				if (isconnected) {
					((TextView) k.findViewById(R.id.tvLeftStatTitle))
							.setText("Full Charged in");
					long diff;
					diff = getActivity().getSharedPreferences(PREF_NAME,
							Context.MODE_MULTI_PROCESS).getLong(
							PreferenceHelper.BAT_CHARGE, 81);
					String willlast = TimeFuncs.convtohournminnday(diff
							* (100 - level));
					((TextView) k.findViewById(R.id.tvLeftStatVal))
							.setText(willlast);
				} else {
					((TextView) k.findViewById(R.id.tvLeftStatTitle))
							.setText("Empty in");
					long diff;
					diff = (long) (getActivity().getSharedPreferences(
							PREF_NAME, Context.MODE_MULTI_PROCESS).getLong(
							PreferenceHelper.BAT_DISCHARGE, 792));
					String willlast = TimeFuncs.convtohournminnday(diff
							* (level));
					((TextView) k.findViewById(R.id.tvLeftStatVal))
							.setText(willlast);
				}
				/*
				 * ((ImageView) k.findViewById(R.id.ivbatmainStats))
				 * .setImageBitmap(ActivityFuncs.createStatStrip(
				 * getBaseContext(), temperature,
				 * ActivityFuncs.getBatHealth(intent)));
				 */

				LoadCircle(level);
				LoadStrip(temperature, intent);

			} catch (Exception ex) {

				ex.printStackTrace();
			}

		}

	};

	@SuppressLint("InlinedApi")
	private void disableOnePercentHack(String reason) {

		getActivity()
				.getSharedPreferences(PREF_NAME + "_new",
						Context.MODE_MULTI_PROCESS).edit()
				.putBoolean(PreferenceHelper.KEY_ONE_PERCENT_HACK, false)
				.commit();

		saphion.logger.Log.d("Disabling one percent hack due to: " + reason);
	}

	/**
	 * Installs click and touch listeners on a fake overflow menu button.
	 * 
	 * @param menuButton
	 *            the fragment's fake overflow menu button
	 */
	public void setupFakeOverflowMenuButton(View menuButton) {
		final PopupMenu fakeOverflow = new PopupMenu(menuButton.getContext(),
				menuButton) {
			@Override
			public void show() {
				getActivity().onPrepareOptionsMenu(getMenu());
				super.show();
			}
		};
		Menu menu = fakeOverflow.getMenu();// .inflate(R.menu.bmenu);
		MenuItem noti = menu.add("Notification Settings");
		noti.setIcon(R.drawable.noti);
		noti.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				getActivity().startActivity(
						new Intent(getBaseContext(), Controller.class));
				getActivity().overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
				return true;
			}
		});

		MenuItem prefs = menu.add("Preferences");
		prefs.setIcon(R.drawable.prefs);
		prefs.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				startActivity(new Intent(getBaseContext(), MainPreference.class));
				getActivity().overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
				return true;
			}
		});

		MenuItem share = menu.add("Share");
		share.setIcon(R.drawable.share);
		share.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				getActivity().startActivity(createShareIntent());
				return true;
			}
		});

		MenuItem more = menu.add("More By Developer");
		more.setIcon(R.drawable.morebydev);
		more.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				getActivity()
						.startActivity(
								new Intent(
										Intent.ACTION_VIEW,
										Uri.parse("http://play.google.com/store/apps/developer?id=sachin+shinde")));
				return true;
			}
		});

		MenuItem about = menu.add("About");
		about.setIcon(R.drawable.about);
		about.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				startActivity(new Intent(getBaseContext(), AboutClass.class));
				getActivity().overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
				return true;
			}
		});

		fakeOverflow
				.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						return getActivity().onOptionsItemSelected(item);
					}
				});

		menuButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fakeOverflow.show();
			}
		});
	}

	private Intent createShareIntent() {
		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");

		intent.putExtra(
				Intent.EXTRA_TEXT,
				"Checkout this Amazing App\nBattery Caster\nGet it now from Playstore\n"
						+ Uri.parse("http://play.google.com/store/apps/details?id="
								+ getActivity().getPackageName()));

		return Intent.createChooser(intent, "Share");
	}

	public Context getBaseContext() {
		return k.getContext();
	}

	View k;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		k = inflater.inflate(R.layout.batteryfraglayout, container, false);

		readbattery();

		tfBold = Typeface.createFromAsset(getActivity().getAssets(),
				Constants.FONT_USING_BOLD);
		tfNormal = Typeface.createFromAsset(getActivity().getAssets(),
				Constants.FONT_USING);
		((TextView) k.findViewById(R.id.tvLeftStatTitle)).setTypeface(tfBold);
		((TextView) k.findViewById(R.id.tvLeftStatVal)).setTypeface(tfNormal);

		((Button) k.findViewById(R.id.bShowBatteryUsage)).setTypeface(tfNormal);

		((TextView) k.findViewById(R.id.tvStatsTempTitle)).setTypeface(tfBold);
		((TextView) k.findViewById(R.id.tvStatsTempVal)).setTypeface(tfNormal);

		((TextView) k.findViewById(R.id.tvStatHealthTitle)).setTypeface(tfBold);
		((TextView) k.findViewById(R.id.tvStatHealthVal)).setTypeface(tfNormal);

		((Button) k.findViewById(R.id.bShowBatteryUsage))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setFlags(346030080);
						intent.setAction("android.intent.action.POWER_USAGE_SUMMARY");
						startActivity(intent);
					}
				});

		setupFakeOverflowMenuButton(k.findViewById(R.id.ibOverflow));

		return k;
	}

	Typeface tfBold, tfNormal;

	public int readbattery() {
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

		String temperature = Functions.updateTemperature(
				(float) ((float) (batteryIntent.getIntExtra(
						BatteryManager.EXTRA_TEMPERATURE, 0)) / 10),
				getActivity().getSharedPreferences(PREF_NAME,
						Context.MODE_MULTI_PROCESS).getBoolean(
						PreferenceHelper.MAIN_TEMP, true), true);

		if (getActivity().getSharedPreferences(PREF_NAME,
				Context.MODE_MULTI_PROCESS).getBoolean(
				PreferenceHelper.KEY_ONE_PERCENT_HACK, false)) {
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
		try {

			/*
			 * ((ImageView) k.findViewById(R.id.ivbatmain))
			 * .setImageBitmap(ActivityFuncs.newbattery(level, getBaseContext(),
			 * getBaseContext().getResources() .getDisplayMetrics().widthPixels,
			 * isconnected));
			 */

			if (isconnected) {
				((TextView) k.findViewById(R.id.tvLeftStatTitle))
						.setText("Full Charged in");
				long diff;
				diff = getActivity().getSharedPreferences(PREF_NAME,
						Context.MODE_MULTI_PROCESS).getLong(
						PreferenceHelper.BAT_CHARGE, 81);
				String willlast = TimeFuncs.convtohournminnday(diff
						* (100 - level));
				((TextView) k.findViewById(R.id.tvLeftStatVal))
						.setText(willlast);
			} else {
				((TextView) k.findViewById(R.id.tvLeftStatTitle))
						.setText("Empty in");
				long diff;
				diff = (long) (getActivity().getSharedPreferences(PREF_NAME,
						Context.MODE_MULTI_PROCESS).getLong(
						PreferenceHelper.BAT_DISCHARGE, 792));
				String willlast = TimeFuncs.convtohournminnday(diff * (level));
				((TextView) k.findViewById(R.id.tvLeftStatVal))
						.setText(willlast);
			}

			/*
			 * ((ImageView) k.findViewById(R.id.ivbatmainStats))
			 * .setImageBitmap(ActivityFuncs.createStatStrip( getBaseContext(),
			 * temperature, ActivityFuncs.getBatHealth(batteryIntent)));
			 */
			LoadCircle(level);
			LoadStrip(temperature, batteryIntent);
		} catch (Exception ex) {
			Log.d(ex.toString());
		}

		return level;
	}

	public void LoadCircle(int mLevel) {

		boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
		if (!isLandscape)
			((ImageView) k.findViewById(R.id.ivbatmain))
					.setImageBitmap(ActivityFuncs.newbattery(mLevel,
							getBaseContext(), (int) (getResources()
									.getDisplayMetrics().heightPixels / 1.7),
							isconnected));
		else
			((ImageView) k.findViewById(R.id.ivbatmain))
					.setImageBitmap(ActivityFuncs.newbattery(mLevel,
							getBaseContext(), (int) (getResources()
									.getDisplayMetrics().heightPixels / 1.1),
							isconnected));
	}

	public void LoadStrip(String temperature, Intent intent) {

		((TextView) k.findViewById(R.id.tvStatsTempVal)).setText(temperature);

		((TextView) k.findViewById(R.id.tvStatHealthVal)).setText(ActivityFuncs
				.getBatHealth(intent));
		// ((ImageView) k.findViewById(R.id.ivbatmainStats))
		// .setImageBitmap(ActivityFuncs.createStatStrip(getBaseContext(),
		// temperature, ActivityFuncs.getBatHealth(intent)));

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}

	public float getDimension(float i) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		float ScreenDensity = dm.density;
		return i * ScreenDensity;
	}

}
