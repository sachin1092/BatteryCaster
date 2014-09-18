package saphion.fragments;

import java.util.ArrayList;
import java.util.Date;

import org.achartengine.model.TimeSeries;

import saphion.batterycaster.AboutClass;
import saphion.batterycaster.BatteryStats;
import saphion.batterycaster.ChangeLog;
import saphion.batterycaster.MainPreference;
import saphion.batterycaster.R;
import saphion.batterylib.DateUtils;
import saphion.batterylib.HistoryItem;
import saphion.fragment.powerfragment.PowerPreference;
import saphion.fragment.powerfragment.PowerProfItems;
import saphion.fragments.alarm.AlarmFragment;
import saphion.fragments.alarm.AlarmItems;
import saphion.fragments.alarm.AlarmPreference;
import saphion.help.HelpActivity;
import saphion.logger.Log;
import saphion.pageindicators.FixedIconTabsAdapter;
import saphion.pageindicators.FixedTabsView;
import saphion.pageindicators.TabsAdapter;
import saphion.services.ForegroundService.Controller;
import saphion.utils.ActivityFuncs;
import saphion.utils.PreferenceHelper;
import saphion.utils.SerialPreference;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InlinedApi")
public class TabNavigation extends ActionBarActivity implements
		ActionBar.TabListener {

	ExtendedViewPager mPager;

	BroadcastReceiver mReceiver;
	ActionBar actionBar;

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mPager.setCurrentItem(tab.getPosition());
		switch (tab.getPosition()) {
		case 0:
			tab.setIcon(R.drawable.power);
			break;
		case 1:
			tab.setIcon(R.drawable.battery_icon_activated);
			break;
		case 2:
			tab.setIcon(R.drawable.graph_icon_activated);
		case 3:
			tab.setIcon(R.drawable.alarm);
			break;
		}
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		switch (tab.getPosition()) {
		case 0:
			tab.setIcon(R.drawable.power_normal);
			break;
		case 1:
			tab.setIcon(R.drawable.battery_icon_normal1);
			break;
		case 2:
			tab.setIcon(R.drawable.graph_icon_normal);
		case 3:
			tab.setIcon(R.drawable.alarm_normal);
			break;
		}
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// mSelected = (TextView)findViewById(R.id.text);

		new FirstTime().execute();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("position", mFixedTabs.getPosition());
		/*Toast.makeText(getBaseContext(),
				"saved pos: " + mFixedTabs.getPosition(), Toast.LENGTH_LONG)
				.show();*/
	}

	int setPos = 1;

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null) {
			setPos = savedInstanceState.getInt("position", 1);
			//Toast.makeText(getBaseContext(), "restored pos: " + setPos,
				//	Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onPause() {
		try {
			this.unregisterReceiver(mReceiver);
			super.onPause();
		} catch (Exception ex) {
			Log.d(ex.toString());
		}

	}

	ArrayList<Double> vals;
	ArrayList<Double> dates;
	ArrayList<HistoryItem> mList;

	private static Animation createHintSwitchAnimation() {
		Animation animation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation.setStartOffset(0);
		animation.setDuration(1300);
		animation.setRepeatCount(Animation.INFINITE);
		animation.setRepeatMode(Animation.RESTART);
		animation.setInterpolator(new DecelerateInterpolator());
		animation.setFillAfter(true);

		return animation;
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
				onPrepareOptionsMenu(getMenu());
				super.show();
			}
		};
		Menu menu = fakeOverflow.getMenu();// .inflate(R.menu.bmenu);
		MenuItem noti = menu.add("Notification Settings");
		noti.setIcon(R.drawable.noti);
		noti.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				startActivity(new Intent(getBaseContext(), Controller.class));
				overridePendingTransition(R.anim.slide_in_left,
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
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
				return true;
			}
		});

		MenuItem share = menu.add("Share");
		share.setIcon(R.drawable.share);
		share.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				startActivity(createShareIntent());
				return true;
			}
		});

		MenuItem more = menu.add("More By Developer");
		more.setIcon(R.drawable.morebydev);
		more.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				startActivity(new Intent(
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
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
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

	private Intent createShareIntent() {
		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");

		intent.putExtra(
				Intent.EXTRA_TEXT,
				"Checkout this Amazing App\nBattery Caster\nGet it now from Playstore\n"
						+ Uri.parse("http://play.google.com/store/apps/details?id="
								+ getPackageName()));

		return Intent.createChooser(intent, "Share");
	}

	public class FirstTime extends AsyncTask<Void, Boolean, Void> {

		@Override
		protected void onPostExecute(Void result) {

			((RelativeLayout) findViewById(R.id.rlsplash))
					.setVisibility(View.GONE);
			((ExtendedViewPager) findViewById(R.id.pager))
					.setVisibility(View.VISIBLE);

			initialisePaging();
			
			boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
			if (isLandscape) {
				setupFakeOverflowMenuButton(findViewById(R.id.ibOverflow));
			}

			if (getSharedPreferences("saphion.batterycaster_preferences",
					Context.MODE_MULTI_PROCESS).getBoolean(
					PreferenceHelper.FIRST_TIME_HELP, true)) {
				
				startActivity(new Intent(TabNavigation.this, HelpActivity.class));
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
				
				super.onPostExecute(result);
				return;
				
			}
			ChangeLog cl = new ChangeLog(TabNavigation.this);

			if (cl.isFirstRun()) {
				cl.getFullLogDialog().show();
			}

			

			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {

			setContentView(R.layout.icons_pager);

			ActionBar actionBar = getSupportActionBar();
			actionBar.setBackgroundDrawable(new ColorDrawable());
			getSupportActionBar().setDisplayShowHomeEnabled(false);
			getSupportActionBar().setDisplayShowTitleEnabled(false);

			actionBar.setDisplayOptions(0);
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

			actionBar.setStackedBackgroundDrawable(new ColorDrawable());
			actionBar.setSplitBackgroundDrawable(new ColorDrawable());
			actionBar.setBackgroundDrawable(new ColorDrawable());

			((ExtendedViewPager) findViewById(R.id.pager))
					.setVisibility(View.INVISIBLE);
			try {
				getSupportActionBar().hide();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			((RelativeLayout) findViewById(R.id.rlsplash))
					.setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.tvBattery)).setTypeface(Typeface
					.createFromAsset(getAssets(), "cnlbold.ttf"));
			((TextView) findViewById(R.id.tvCaster)).setTypeface(Typeface
					.createFromAsset(getAssets(), "cnlbold.ttf"));
			if (getSharedPreferences("saphion.batterycaster_preferences",
					Context.MODE_MULTI_PROCESS).getBoolean(
					PreferenceHelper.FIRST_TIME, true)) {
				((TextView) findViewById(R.id.tvinfo)).setTypeface(Typeface
						.createFromAsset(getAssets(), "cnlbold.ttf"));
			} else {
				((TextView) findViewById(R.id.tvinfo))
						.setVisibility(View.INVISIBLE);
			}
			((ImageView) findViewById(R.id.ivsplashedit))
					.setImageBitmap(ActivityFuncs.loader(getBaseContext()));
			((ImageView) findViewById(R.id.ivsplashedit))
					.startAnimation(createHintSwitchAnimation());

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			vals = new ArrayList<Double>();
			dates = new ArrayList<Double>();

			if (getSharedPreferences("saphion.batterycaster_preferences",
					Context.MODE_MULTI_PROCESS).getBoolean(
					PreferenceHelper.FIRST_TIME, true)) {
				getSharedPreferences("saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS).edit()
						.putBoolean(PreferenceHelper.FIRST_TIME, false)
						.commit();
				ArrayList<String> al = new ArrayList<String>();
				al.add("mon");
				al.add("tue");
				al.add("wed");
				al.add("thu");
				al.add("fri");
				al.add("sat");
				al.add("sun");

				AlarmItems a1 = new AlarmItems(100, false, "Full Charged", al,
						"Default", true, true);

				ArrayList<AlarmItems> atoSave = new ArrayList<AlarmItems>();
				atoSave.add(a1);

				ArrayList<String> al2 = new ArrayList<String>();
				al2.add("mon");
				al2.add("wed");
				al2.add("fri");
				al2.add("sun");

				AlarmItems a2 = new AlarmItems(20, false, "Low Battery", al2,
						"Default", true, true);

				atoSave.add(a2);

				AlarmPreference.saveAlarms(getBaseContext(), atoSave);

				ArrayList<PowerProfItems> poweritems = new ArrayList<PowerProfItems>();
				poweritems.add(new PowerProfItems(false, false, false, false,
						false, 20, "Power Saving", false, false, 0, "0", false,
						100, -99, -99, false, false, 2));// 20 instead of -99
				poweritems.add(new PowerProfItems(false, true, false, false,
						true, 177, "Normal", false, false, 2, "1", false, 100,
						-99, -99, false, false, 2));

				PowerPreference.savePower(getBaseContext(), poweritems);

				mList = BatteryStats.getHistList(getBaseContext());

				TimeSeries series = new TimeSeries("Battery History2");
				TimeSeries exceptionSeries = new TimeSeries("Battery HistoryE");

				Intent batteryIntent = getApplicationContext()
						.registerReceiver(null,
								new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

				int plugged = batteryIntent.getIntExtra(
						BatteryManager.EXTRA_PLUGGED, -1);
				boolean isConnected = (plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB);

				for (int i = 0; i < mList.size(); i++) {

					int zx = 0;
					if (isConnected) {
						getSharedPreferences(
								"saphion.batterycaster_preferences",
								Context.MODE_MULTI_PROCESS).edit()
								.putBoolean("plugged?", false).commit();
						zx = 0;
					} else {
						zx = 1;
					}
					if (mList.get(i).getChargingInt() == zx) {
						getSharedPreferences(
								"saphion.batterycaster_preferences",
								Context.MODE_MULTI_PROCESS)
								.edit()
								.putString(
										PreferenceHelper.LAST_CHARGED,
										DateUtils.format(mList.get(i)
												.getNormalizedTimeLong(),
												"MM/dd/yyyy hh:mm:ss a"))
								.commit();
					}

					if (i != 0) {

						if (mList.get(i - 1).getBatteryLevelInt() != mList.get(
								i).getBatteryLevelInt()) {

							Log.d("Entry number: " + i + " is time "
									+ mList.get(i).getNormalizedTimeLong()
									+ " level "
									+ mList.get(i).getBatteryLevelInt());

							series.add(new Date(mList.get(i)
									.getNormalizedTimeLong()), mList.get(i)
									.getBatteryLevelInt());
						} else {
							exceptionSeries.add(new Date(mList.get(i)
									.getNormalizedTimeLong()), mList.get(i)
									.getBatteryLevelInt());
						}
					}

				}

				if (series.getItemCount() == 0) {
					series = exceptionSeries;
				}

				int z = 0;
				if (series.getItemCount() > 100) {
					z = series.getItemCount() - 100;
				}

				Log.d(z + "");

				for (int i = z; i < series.getItemCount(); i++) {
					Log.d("x: " + new Date((long) series.getX(i)));
					Log.d("y: " + series.getY(i));
					// series2.add(new Date((long) series.getX(i)),
					// series.getY(i));
					vals.add(series.getY(i));

					dates.add(series.getX(i));
				}

				SerialPreference.savePrefs(getBaseContext(), dates,
						PreferenceHelper.BAT_TIME);
				SerialPreference.savePrefs(getBaseContext(), vals,
						PreferenceHelper.BAT_VALS);
			}

			return null;
		}

	}

	public void initialisePaging() {

		Log.d("Inside Initialising page ");
		// Toast.makeText(getBaseContext(), "Initialising pos: " + setPos,
		// Toast.LENGTH_LONG).show();
		try {
			// For each of the sections in the app, add a tab to the action bar.

			CONTENT = new String[] {
					TabNavigation.this.getResources().getString(R.string.power),
					TabNavigation.this.getResources().getString(R.string.info),
					TabNavigation.this.getResources().getString(R.string.graph),
					TabNavigation.this.getResources().getString(R.string.alarm) };
			FragmentPagerAdapter adapter = new IconsAdapter(
					getSupportFragmentManager());

			mPager = (ExtendedViewPager) findViewById(R.id.pager);

			mPager.setAdapter(adapter);

			try {
				mPager.setCurrentItem(setPos);
			} catch (Exception ex) {
				Log.d("1. Error in Initialising page: " + ex.toString());
			}
			mPager.setOffscreenPageLimit(adapter.getCount());

			mFixedTabs = (FixedTabsView) findViewById(R.id.fixed_icon_tabs);
			mFixedTabsAdapter = new FixedIconTabsAdapter(this);
			mFixedTabs.setAdapter(mFixedTabsAdapter);
			mFixedTabs.setViewPager(mPager);
			mFixedTabs.setPosition(mPager.getCurrentItem());

		} catch (Exception ex) {
			Log.Toast(getBaseContext(), ex.toString() + "  " + ex.getMessage()
					+ " " + ex.getCause() + " " + toString(ex.getStackTrace()),
					Toast.LENGTH_LONG);
			Log.d("Execption in Initialising!! " + ex.toString());
		}

	}

	private FixedTabsView mFixedTabs;
	private TabsAdapter mFixedTabsAdapter;

	public String toString(StackTraceElement[] stackTrace) {
		String s = "";
		for (int i = 0; i < stackTrace.length; i++) {
			s = s + stackTrace[i];
		}
		return s;
	}

	@Override
	protected void onResume() {
		Log.d("Resuming");

		try {
			mPager.setPagingEnabled(true);
		} catch (Exception ex) {
			Log.d("Exception in resuming: " + ex.toString());
		}

		try {

			IntentFilter intentFilter = new IntentFilter(
					"android.intent.action.MAIN");
			mReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					if ((Integer) (intent.getExtras().get("msg")) == 0) {
						mPager.setPagingEnabled(false);
					} else {
						mPager.setPagingEnabled(true);
					}
				}
			};
			this.registerReceiver(mReceiver, intentFilter);


		} catch (Exception ex) {
			Log.d(ex.toString());
		}
		// registering our receiver

		SharedPreferences getPrefs = getSharedPreferences(
				"saphion.batterycaster_preferences", Context.MODE_MULTI_PROCESS);
		getPrefs.edit().putInt("count", getPrefs.getInt("count", 0) + 1)
				.commit();

		if (getSharedPreferences("saphion.batterycaster_preferences",
				Context.MODE_MULTI_PROCESS).getInt("count", 0) == getSharedPreferences(
				"saphion.batterycaster_preferences", Context.MODE_MULTI_PROCESS)
				.getInt("showAt", 7)) {

			showRateDialog();
		}

		super.onResume();
	}

	private void showRateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				TabNavigation.this);
		builder.setTitle("Rate");
		builder.setIcon(R.drawable.rate);
		View view = LayoutInflater.from(TabNavigation.this).inflate(
				R.layout.ratedialog, null);
		Button rateNow = (Button) view.findViewById(R.id.bRatenow);
		Button rateLater = (Button) view.findViewById(R.id.bRateLater);
		Button rateNever = (Button) view.findViewById(R.id.bRateNever);
		TextView tv = (TextView) view.findViewById(R.id.tvRate);
		Typeface tf = Typeface.createFromAsset(getAssets(), "cnlbold.ttf");
		rateLater.setTypeface(tf);
		rateNow.setTypeface(tf);
		rateNever.setTypeface(tf);
		tv.setTypeface(tf);
		tv.setTextColor(Color.WHITE);
		builder.setView(view);
		final AlertDialog dialog = builder.create();
		rateNow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("market://details?id=" + getPackageName());
				Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);

				try {
					startActivity(myAppLinkToMarket);

				} catch (ActivityNotFoundException e) {
					startActivity(new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("http://play.google.com/store/apps/details?id="
									+ getPackageName())));
				} finally {
					dialog.dismiss();
				}
			}
		});
		rateNever.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}
		});
		rateLater.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences getPrefs = getSharedPreferences(
						"saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS);
				getPrefs.edit()
						.putInt("showAt", 2 * getPrefs.getInt("showAt", 10))
						.commit();
				dialog.dismiss();
			}
		});
		dialog.show();

	}

	public String[] CONTENT;

	protected static final int[] ICONS = new int[] { R.drawable.powersave,
			R.drawable.battery, R.drawable.graph_icon_activated,
			R.drawable.clock };

	public class IconsAdapter extends FragmentPagerAdapter /*
															 * implements
															 * IconPagerAdapter
															 */{

		private int mCount = CONTENT.length;

		public IconsAdapter(android.support.v4.app.Fragment fragment) {
			super(fragment.getChildFragmentManager());

			// write your code here
		}

		public IconsAdapter(FragmentManager fm) {

			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0)
				return PowerFragment.newInstance(CONTENT[position
						% CONTENT.length]);
			else if (position == 1)
				return BatteryFragment.newInstance(CONTENT[position
						% CONTENT.length]);
			else if (position == 2)
				return GraphFragment.newInstance(CONTENT[position
						% CONTENT.length]);
			else
				return AlarmFragment.newInstance(CONTENT[position
						% CONTENT.length]);
		}

		@Override
		public int getCount() {
			return mCount;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length];
		}

		/*
		 * @Override public int getIconResId(int index) { return ICONS[index %
		 * ICONS.length]; }
		 */

		public void setCount(int count) {
			if (count > 0 && count <= 10) {
				mCount = count;
				notifyDataSetChanged();
			}
		}
	}
}