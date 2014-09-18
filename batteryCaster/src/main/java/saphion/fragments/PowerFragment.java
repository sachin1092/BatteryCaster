package saphion.fragments;

import java.util.ArrayList;

import saphion.batterycaster.AboutClass;
import saphion.batterycaster.MainPreference;
import saphion.batterycaster.R;
import saphion.fragment.alarm.alert.Intents;
import saphion.fragment.powerfragment.EditPower;
import saphion.fragment.powerfragment.PowerPreference;
import saphion.fragment.powerfragment.PowerProfItems;
import saphion.fragment.powerfragment.PowerProfilesAdapter;
import saphion.fragment.powerfragment.ToggleAMode;
import saphion.fragment.powerfragment.ToggleBness;
import saphion.fragment.powerfragment.ToggleBtooth;
import saphion.fragment.powerfragment.ToggleData;
import saphion.fragment.powerfragment.ToggleSync;
import saphion.fragment.powerfragment.ToggleWifi;
import saphion.logger.Log;
import saphion.services.ForegroundService.Controller;
import saphion.utils.PreferenceHelper;
import saphion.utils.ToggleHelper;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

@SuppressLint("AlwaysShowAction")
public final class PowerFragment extends ListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		/* setHasOptionsMenu(true); */
		setListViewParams();
		super.onActivityCreated(savedInstanceState);
	}


	@Override
	public void onPause() {
		try {
			getBaseContext().unregisterReceiver(updateReceiver);
			//getBaseContext().unregisterReceiver(actionMode);
			getBaseContext().unregisterReceiver(selectionReceiver);

			//mod.finish();
		} catch (Exception e) {
			Log.d(e.toString());
		}
		super.onPause();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onResume() {
		try {
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
			filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			filter.addAction(ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED);
			filter.addAction(Intents.SWITCHER_INTENT);
			filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
			getBaseContext().registerReceiver(updateReceiver, filter);
			//IntentFilter actionfilter = new IntentFilter();
			//actionfilter.addAction(Intents.ActionModeIntent);
			//getBaseContext().registerReceiver(actionMode, actionfilter);
			IntentFilter ifs = new IntentFilter();
			ifs.addAction(Intents.SELECTOR_INTENT);
			getBaseContext().registerReceiver(selectionReceiver, ifs);
		} catch (Exception ex) {
			Log.d(ex.toString());
		}
		try {
			setListViewParams();
		} catch (OutOfMemoryError ex) {
		} catch (Exception ex) {
		}
		
/*		try {
			mAdapter.getMessageBar().hide();
		} catch (Exception ex) {
		}*/
		super.onResume();
	}

	/*
	 * @Override public void onCreateOptionsMenu(Menu menu, MenuInflater
	 * inflater) { MenuItem newAlarm = menu.add("New Alarm");
	 * newAlarm.setIcon(R.drawable.add);
	 * newAlarm.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
	 * newAlarm.setOnMenuItemClickListener(new OnMenuItemClickListener() {
	 * 
	 * @Override public boolean onMenuItemClick(MenuItem item) {
	 * 
	 * mAdapter.add(mAdapter.getCount(), new PowerProfItems());
	 * mAdapter.notifyDataSetChanged();
	 * PowerPreference.savePower(getBaseContext(), mAdapter.getAll());
	 * 
	 * return true; } }); super.onCreateOptionsMenu(menu, inflater); }
	 */
	private static final String KEY_CONTENT = "TestFragment:Content";

	public static PowerFragment newInstance(String content) {
		PowerFragment fragment = new PowerFragment();

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 20; i++) {
			builder.append(content).append(" ");
		}
		builder.deleteCharAt(builder.length() - 1);
		fragment.mContent = builder.toString();

		return fragment;
	}

	private String mContent = "???";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ((savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT)) {
			mContent = savedInstanceState.getString(KEY_CONTENT);
		}
	}

	public BroadcastReceiver updateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.hasExtra(EditPower.BNESS_SEEKBAR))
				switchtoBrightness(intent.getIntExtra(EditPower.BNESS_SEEKBAR,
						-99));
			setChecked();
			try {
				setListViewParams();
			} catch (Exception ex) {
			}
		}
	};

	public BroadcastReceiver selectionReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// Log.Toast(getBaseContext(), "Receiver", Toast.LENGTH_LONG);
			/*ArrayList<PowerProfItems> poweritems = PowerPreference
					.retPower(getBaseContext());
			mAdapter = new PowerProfilesAdapter(
					(ActionBarActivity) getActivity(), poweritems);
			setListAdapter(mAdapter);*/
		}
	};

	private void switchtoBrightness(int bness) {
		Window window = getActivity().getWindow();
		ToggleHelper.setBrightness(getBaseContext(), window, bness);
	}

	View k;
	ImageButton wifi, mdata, btooth, amode, sync, bness, arotate;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		k = inflater.inflate(R.layout.powerfrag, container, false);

		wifi = (ImageButton) k.findViewById(R.id.ibwifi);
		mdata = (ImageButton) k.findViewById(R.id.ibmdata);
		btooth = (ImageButton) k.findViewById(R.id.ibbtooth);
		amode = (ImageButton) k.findViewById(R.id.ibamode);
		sync = (ImageButton) k.findViewById(R.id.ibsync);
		bness = (ImageButton) k.findViewById(R.id.ibbrightness);
		arotate = (ImageButton) k.findViewById(R.id.ibarotate);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
			amode.setVisibility(View.GONE);
		else
			amode.setVisibility(View.VISIBLE);

		setChecked();
		listeners();

		ImageButton mAddAlarmButton = (ImageButton) k
				.findViewById(R.id.alarm_add_alarm);
		mAddAlarmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mAdapter.add(mAdapter.getCount(), new PowerProfItems(mAdapter.getCount()));
				mAdapter.notifyDataSetChanged();
				PowerPreference.savePower(getBaseContext(), mAdapter.getAll());
				/*try {
					mAdapter.getMessageBar().hide();
				} catch (Exception ex) {
				}*/

			}
		});

		FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mAddAlarmButton
				.getLayoutParams();

		boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
		if (isLandscape) {
			layoutParams.gravity = Gravity.RIGHT;
		} else {
			layoutParams.gravity = Gravity.CENTER;
		}
		mAddAlarmButton.setLayoutParams(layoutParams);

		if (!isLandscape)
			setupFakeOverflowMenuButton(k.findViewById(R.id.menu_button));
		else
			k.findViewById(R.id.menu_button).setVisibility(View.GONE);

		/*try {
			mAdapter.getMessageBar().hide();
		} catch (Exception ex) {
		}*/
		
		return k;
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

	public void setChecked() {

		if (ToggleHelper.isDataEnable(getBaseContext())) {
			mdata.setImageResource(R.drawable.mdata_on);
		} else {
			mdata.setImageResource(R.drawable.mdata_off);
		}

		if (ToggleHelper.isAModeEnabled(getBaseContext())) {
			amode.setImageResource(R.drawable.amode_on);
		} else {
			amode.setImageResource(R.drawable.amode_off);
		}

		if (ToggleHelper.isBluetoothEnabled(getBaseContext())) {
			btooth.setImageResource(R.drawable.btooth_on);
		} else {
			btooth.setImageResource(R.drawable.btooth_off);
		}

		if (ToggleHelper.isWifiEnabled(getBaseContext())) {
			wifi.setImageResource(R.drawable.wifi_on);
		} else {
			wifi.setImageResource(R.drawable.wifi_off);
		}

		if (ToggleHelper.isSyncEnabled()) {
			sync.setImageResource(R.drawable.sync_on);
		} else {
			sync.setImageResource(R.drawable.sync_off);
		}

		switch (ToggleHelper.getBrightnessMode(getBaseContext())) {
		case 1:
			bness.setImageResource(R.drawable.blow_on);
			break;
		case 2:
			bness.setImageResource(R.drawable.bhalf_on);
			break;
		case 3:
			bness.setImageResource(R.drawable.bfull_on);
			break;
		case 0:
			bness.setImageResource(R.drawable.bauto_on);
			break;
        default:
            bness.setImageResource(R.drawable.bauto_on);
            break;
		}

		if (ToggleHelper.isRotationEnabled(getBaseContext()))
			arotate.setImageResource(R.drawable.orientation_on);
		else
			arotate.setImageResource(R.drawable.orientation_off);

	}

	public void listeners() {

		wifi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToggleWifi tb = (new ToggleWifi(getBaseContext(), wifi));
				tb.execute();
			}
		});

		mdata.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToggleData tb = (new ToggleData(getBaseContext(), mdata));
				tb.execute();
			}
		});

		btooth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToggleBtooth tb = (new ToggleBtooth(getBaseContext(), btooth));
				tb.execute();
			}
		});

		amode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToggleAMode tb = (new ToggleAMode(getBaseContext(), amode));
				tb.execute();
			}
		});

		sync.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToggleSync tb = (new ToggleSync(getBaseContext(), sync));
				tb.execute();
			}
		});

		bness.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ToggleBness tb = (new ToggleBness(getBaseContext(), bness,
						getActivity().getWindow()));
				tb.toggle();
			}
		});

		arotate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToggleHelper.toggleRotation(getBaseContext());
				if (ToggleHelper.isRotationEnabled(getBaseContext()))
					arotate.setImageResource(R.drawable.orientation_on);
				else
					arotate.setImageResource(R.drawable.orientation_off);
			}
		});

	}

	public Context getBaseContext() {
		return k.getContext();
	}

    //ActionMode mod;
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }

	PowerProfilesAdapter mAdapter;

	// View myLayout;
	// LinearLayout ll;

	@SuppressLint("InlinedApi")
	public void setListViewParams() {

		/*
		 * myLayout = (View) k.findViewById(R.id.layoutpower);
		 * 
		 * ((TextView) myLayout.findViewById(R.id.description_text))
		 * .setText("Power Profile Deleted");
		 * 
		 * ll = ((LinearLayout) myLayout.findViewById(R.id.action_button));
		 */

		ArrayList<PowerProfItems> poweritems = PowerPreference
				.retPower(getBaseContext());
		mAdapter = new PowerProfilesAdapter(
				(ActionBarActivity) getActivity(), poweritems);
		boolean flag = false;
		for (int i = 0; i < mAdapter.getCount(); i++) {
			if (mAdapter.getItem(i).isPowerProfequal(
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
							.getScreenTimeOut(getBaseContext())))) {
				getActivity()
						.getSharedPreferences(
								"saphion.batterycaster_preferences"
										+ "_new_power",
								Context.MODE_MULTI_PROCESS).edit()
						.putInt(PreferenceHelper.POSITION, i).commit();
				flag = true;
			}
		}
		if (!flag) {
			int pos = getActivity().getSharedPreferences(
					"saphion.batterycaster_preferences" + "_new_power",
					Context.MODE_MULTI_PROCESS).getInt(
					PreferenceHelper.POSITIONS, mAdapter.getCount() - 1);
			if (mAdapter.getCount() <= pos)
				pos = 0;
            if(mAdapter.getCount() > 0) {
                PowerProfItems mItem = mAdapter.getItem(pos);
                mAdapter.add(
                        mAdapter.getCount(),
                        new PowerProfItems(ToggleHelper
                                .isWifiEnabled(getBaseContext()), ToggleHelper
                                .isDataEnable(getBaseContext()), ToggleHelper
                                .isBluetoothEnabled(getBaseContext()), ToggleHelper
                                .isAModeEnabled(getBaseContext()), ToggleHelper
                                .isSyncEnabled(), ToggleHelper
                                .getBrightness(getBaseContext()), "User Defined " + mAdapter.getCount(),
                                ToggleHelper.isHapticFback(getBaseContext()),
                                ToggleHelper.isRotationEnabled(getBaseContext()),
                                ToggleHelper.getRingerMode(getBaseContext()),
                                String.valueOf(ToggleHelper
                                        .getScreenTimeOutIndex(getBaseContext())),
                                false, 100, mItem.getS_Off_int_mdata(), mItem
                                .getS_Off_int_wifi(), mItem
                                .getS_Off_mdata(), mItem.getS_Off_wifi(), 2));
            } else {
                mAdapter.add(
                        mAdapter.getCount(),
                        new PowerProfItems(ToggleHelper
                                .isWifiEnabled(getBaseContext()), ToggleHelper
                                .isDataEnable(getBaseContext()), ToggleHelper
                                .isBluetoothEnabled(getBaseContext()), ToggleHelper
                                .isAModeEnabled(getBaseContext()), ToggleHelper
                                .isSyncEnabled(), ToggleHelper
                                .getBrightness(getBaseContext()), "User Defined " + mAdapter.getCount(),
                                ToggleHelper.isHapticFback(getBaseContext()),
                                ToggleHelper.isRotationEnabled(getBaseContext()),
                                ToggleHelper.getRingerMode(getBaseContext()),
                                String.valueOf(ToggleHelper
                                        .getScreenTimeOutIndex(getBaseContext())),
                                false, 100, 0, 0, false, false, 2));
            }
			getActivity()
					.getSharedPreferences(
							"saphion.batterycaster_preferences" + "_new_power",
							Context.MODE_MULTI_PROCESS).edit()
					.remove(PreferenceHelper.POSITION).commit();
		}
		setListAdapter(mAdapter);
		final ListView listView = getListView();

		listView.setBackgroundColor(0x00161616);


		/*
		 * ll.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { animate(myLayout).alpha(0);
		 * 
		 * (new Handler()).postDelayed(new Runnable() { public void run() {
		 * ((View) k.findViewById(R.id.layoutpower)) .setVisibility(View.GONE);
		 * }
		 * 
		 * }, 300); mAdapter = new PowerProfilesAdapter( (ActionBarActivity)
		 * getActivity(), PowerPreference .retPower(getBaseContext()));
		 * setListAdapter(mAdapter); getListView().setOnTouchListener(null);
		 * 
		 * } });
		 * 
		 * touchlister = new OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) {
		 * 
		 * if (((View) k.findViewById(R.id.layoutpower)).getVisibility() ==
		 * View.VISIBLE) { ((View) k.findViewById(R.id.layoutpower))
		 * .setVisibility(View.GONE);
		 * PowerPreference.savePower(getBaseContext(), mAdapter.getAll());
		 * getListView().setOnTouchListener(null); } return true; } };
		 * listView.setOnTouchListener(null);
		 */
		
		/*try {
			mAdapter.getMessageBar().hide();
		} catch (Exception ex) {
		}*/

	}

//	OnTouchListener touchlister;
}
