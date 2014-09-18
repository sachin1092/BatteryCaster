package saphion.togglercvrs;

import saphion.batterycaster.R;
import saphion.fragment.alarm.alert.Intents;
import saphion.fragment.powerfragment.ToggleAMode;
import saphion.fragment.powerfragment.ToggleBtooth;
import saphion.fragment.powerfragment.ToggleData;
import saphion.fragment.powerfragment.ToggleSync;
import saphion.fragment.powerfragment.ToggleWifi;
import saphion.fragments.TabNavigation;
import saphion.logger.Log;
import saphion.togglehelper.hotspot.WifiApManager;
import saphion.togglehelpers.torch.CameraDevice;
import saphion.togglehelpers.torch.TorchService;
import saphion.utils.Constants;
import saphion.utils.PreferenceHelper;
import saphion.utils.ToggleHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ToggleDialog extends Activity {
	AlertDialog dialog;

	/*
	 * private Handler handler = new Handler(); Runnable runnable = new
	 * Runnable() {
	 * 
	 * @Override public void run() { handler.removeCallbacks(runnable); if
	 * (dialog != null) dialog.dismiss();
	 * 
	 * } };
	 */
	protected boolean m_ignoreChange;

	@Override
	public void onPause() {
		try {
			getBaseContext().unregisterReceiver(updateReceiver);

			dialog.dismiss();
		} catch (Exception e) {
			Log.d(e.toString());
		}
		try {
			if (ringtone != null)
				ringtone.stop();
		} catch (Exception ex) {
		}
		super.onPause();
	}

	public BroadcastReceiver updateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction() == AudioManager.RINGER_MODE_CHANGED_ACTION) {
				updateRadioGroup();
				return;
			}
			if (torch != null)
				if (intent.getAction() == Intents.TORCH_OFF) {
					torch.setImageResource(R.drawable.lightbulb_off);
					return;
				} else if (intent.getAction() == Intents.TORCH_ON) {
					torch.setImageResource(R.drawable.lightbulb_on);
					return;
				}
			setChecked();
			setVolumeSeekBars();
			// Toast.makeText(context, intent.getAction(),
			// Toast.LENGTH_LONG).show();

		}
	};

	protected void updateRadioGroup() {
		if (view != null) {
			int checkedId = current();
			RadioButton checked = (RadioButton) view.findViewById(checkedId);
			m_ignoreChange = true;
			checked.setChecked(true);
			m_ignoreChange = false;
		}
	}

	@SuppressWarnings("deprecation")
	protected int current() {
		AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		switch (audio.getRingerMode()) {
		case AudioManager.RINGER_MODE_SILENT:
			return R.id.rbVolumeOff;
		case AudioManager.RINGER_MODE_VIBRATE:
			return R.id.rbVibrate;
		}

		try {
			if (audio.shouldVibrate(AudioManager.VIBRATE_TYPE_RINGER)
					|| audio.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE
					|| (Settings.System.getInt(getContentResolver(),
							"vibrate_when_ringing") == 1 ? true : false))
				return R.id.rbVolumeOnVib;
		} catch (SettingNotFoundException e) {
			if (audio.shouldVibrate(AudioManager.VIBRATE_TYPE_RINGER))
				return R.id.rbVolumeOnVib;
			e.printStackTrace();
		}

		return R.id.rbVolumeOn;
	}

	@SuppressWarnings("deprecation")
	protected void ringAndVibrate() {
		broadcastVolumeUpdate(AudioManager.RINGER_MODE_NORMAL);

		AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
				AudioManager.VIBRATE_SETTING_ON);
		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,
				AudioManager.VIBRATE_SETTING_ON);
		Settings.System.putInt(getContentResolver(), "vibrate_when_ringing", 1);

	}

	@SuppressWarnings("deprecation")
	protected void ring() {
		broadcastVolumeUpdate(AudioManager.RINGER_MODE_NORMAL);

		AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
				AudioManager.VIBRATE_SETTING_OFF);
		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,
				AudioManager.VIBRATE_SETTING_OFF);
		Settings.System.putInt(getContentResolver(), "vibrate_when_ringing", 0);
	}

	@SuppressWarnings("deprecation")
	protected void vibrate() {
		broadcastVolumeUpdate(AudioManager.RINGER_MODE_VIBRATE);

		AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
				AudioManager.VIBRATE_SETTING_ON);
		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,
				AudioManager.VIBRATE_SETTING_ON);
		Settings.System.putInt(getContentResolver(), "vibrate_when_ringing", 1);
	}

	@SuppressWarnings("deprecation")
	protected void silent() {
		broadcastVolumeUpdate(AudioManager.RINGER_MODE_SILENT);

		AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
				AudioManager.VIBRATE_SETTING_OFF);
		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,
				AudioManager.VIBRATE_SETTING_OFF);
		Settings.System.putInt(getContentResolver(), "vibrate_when_ringing", 0);
	}

	protected void broadcastVolumeUpdate(int ringerMode) {
		// see http://www.openintents.org/en/node/380
		Intent intent = new Intent("org.openintents.audio.action_volume_update");
		intent.putExtra("org.openintents.audio.extra_stream_type",
				AudioManager.STREAM_RING);
		intent.putExtra("org.openintents.audio.extra_volume_index", -9999);
		intent.putExtra("org.openintents.audio.extra_ringer_mode", ringerMode);
		getApplicationContext().sendOrderedBroadcast(intent, null);
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
			filter.addAction(Intents.SWITCHER_NOTI);
			filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
			filter.addAction(Intents.TORCH_ON);
			filter.addAction(Intents.TORCH_OFF);
			filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);

			getBaseContext().registerReceiver(updateReceiver, filter);

		} catch (Exception ex) {
			// Toast.makeText(getBaseContext(), "" + ex.toString(),
			// Toast.LENGTH_LONG).show();
			Log.d(ex.toString());
		}

		super.onResume();
	}

	public final static String FROM_SWITCHER = "from_switcher";

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
				WindowManager.LayoutParams.DIM_AMOUNT_CHANGED);
		ImageView iv = new ImageView(ToggleDialog.this);
		iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		iv.setBackgroundColor(0x00ffffff);
		iv.setFocusable(false);
		iv.setFocusableInTouchMode(false);
		setContentView(iv);
		/*
		 * if (getIntent().hasExtra(FROM_SWITCHER)) { if
		 * (getIntent().getIntExtra(FROM_SWITCHER, -99) == -99) toggleToAuto();
		 * else toggletoManual(getIntent().getIntExtra(FROM_SWITCHER, -99));
		 * finish(); return; }
		 */

		showDialog();
		// finish();

	}

	private void setEnabled(boolean b, SeekBar sb) {
		if (b) {
			sb.setThumb(getResources().getDrawable(
					R.drawable.apptheme_scrubber_control_selector_holo_dark));
			sb.setProgressDrawable(getResources().getDrawable(
					R.drawable.apptheme_scrubber_progress_horizontal_holo_dark));
		} else {
			sb.setThumb(getResources().getDrawable(
					R.drawable.apptheme_scrubber_control_disabled_holo));
			sb.setProgressDrawable(getResources().getDrawable(
					R.drawable.apptheme_scrubber_track_holo_dark));
		}

	}

	ImageButton wifi, mdata, btooth, amode, sync, arotate, torch, wifihotpot;
	SeekBar sb;
	View view;

	public void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
		view = LayoutInflater.from(getBaseContext()).inflate(R.layout.popup,
				null);
		view.setFocusable(true);
		view.setFocusableInTouchMode(true);
		view.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_VOLUME_UP
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					// change the seek bar progress indicator position
					// AudioManager am = (AudioManager)
					// getSystemService(Context.AUDIO_SERVICE);
					try {
						if (ringtone != null)
							ringtone.stop();
					} catch (Exception ex) {
					}
					SeekBar sb = ((SeekBar) view.findViewById(R.id.sbAlarm));
					switch (getVolumeControlStream()) {
					case AudioManager.STREAM_ALARM:
						sb = ((SeekBar) view.findViewById(R.id.sbAlarm));
						if (sb.getProgress() != sb.getMax())
							sb.setProgress(sb.getProgress() + 1);
						break;
					case AudioManager.STREAM_MUSIC:
						sb = ((SeekBar) view.findViewById(R.id.sbMusic));
						if (sb.getProgress() != sb.getMax())
							sb.setProgress(sb.getProgress() + 1);
						break;
					case AudioManager.STREAM_RING:
						sb = ((SeekBar) view.findViewById(R.id.sbRing));
						if (sb.getProgress() != sb.getMax())
							sb.setProgress(sb.getProgress() + 1);
						break;
					}
					switch (getVolumeControlStream()) {
					case AudioManager.STREAM_ALARM:
						ringtone = RingtoneManager.getRingtone(
								getBaseContext(),
								RingtoneManager
										.getDefaultUri(RingtoneManager.TYPE_ALARM));
						break;
					case AudioManager.STREAM_RING:
					case AudioManager.STREAM_MUSIC:
						ringtone = RingtoneManager.getRingtone(
								getBaseContext(),
								RingtoneManager
										.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
						break;
					}
					
					try {
						ringtone.setStreamType(getVolumeControlStream());
						ringtone.stop();
						ringtone.play();
					} catch (Exception ex) {
					}
					return true;
				}

				if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					// change the seek bar progress indicator position
					// AudioManager am = (AudioManager)
					// getSystemService(Context.AUDIO_SERVICE);
					try {
						if (ringtone != null)
							ringtone.stop();
					} catch (Exception ex) {
					}
					switch (getVolumeControlStream()) {
					case AudioManager.STREAM_ALARM:
						SeekBar sb = ((SeekBar) view.findViewById(R.id.sbAlarm));
						if (sb.getProgress() != 0)
							sb.setProgress(sb.getProgress() - 1);

						break;
					case AudioManager.STREAM_MUSIC:
						sb = ((SeekBar) view.findViewById(R.id.sbMusic));
						if (sb.getProgress() != 0)
							sb.setProgress(sb.getProgress() - 1);
						break;
					case AudioManager.STREAM_RING:
						sb = ((SeekBar) view.findViewById(R.id.sbRing));
						if (sb.getProgress() != 0)
							sb.setProgress(sb.getProgress() - 1);
						break;
					}
					switch (getVolumeControlStream()) {
					case AudioManager.STREAM_ALARM:
						ringtone = RingtoneManager.getRingtone(
								getBaseContext(),
								RingtoneManager
										.getDefaultUri(RingtoneManager.TYPE_ALARM));
						break;
					case AudioManager.STREAM_RING:
					case AudioManager.STREAM_MUSIC:
						ringtone = RingtoneManager.getRingtone(
								getBaseContext(),
								RingtoneManager
										.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
						break;
					}

					
					try {
						ringtone.setStreamType(getVolumeControlStream());
						ringtone.stop();
						ringtone.play();
					} catch (Exception ex) {
					}
					return true;
				}

				return false;
			}
		});
		wifi = (ImageButton) view.findViewById(R.id.ibwifi);
		mdata = (ImageButton) view.findViewById(R.id.ibmdata);
		btooth = (ImageButton) view.findViewById(R.id.ibbtooth);
		amode = (ImageButton) view.findViewById(R.id.ibamode);
		sync = (ImageButton) view.findViewById(R.id.ibsync);
		torch = (ImageButton) view.findViewById(R.id.ibtorch);
		arotate = (ImageButton) view.findViewById(R.id.ibarotate);
		wifihotpot = (ImageButton) view.findViewById(R.id.ibwifispot);
		new AsyncTask<Void, Void, Void>() {
			boolean hasFlash;
			SharedPreferences mPref;

			@Override
			protected void onPreExecute() {
				mPref = getSharedPreferences(
						"saphion.batterycaster_preferences", MODE_MULTI_PROCESS);
				if (mPref.getBoolean(PreferenceHelper.HAS_FLASH, true)) {
					torch.setVisibility(View.VISIBLE);
					view.findViewById(R.id.torchview).setVisibility(
							View.VISIBLE);
				} else {
					torch.setVisibility(View.GONE);
					view.findViewById(R.id.torchview).setVisibility(View.GONE);
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
				if (hasFlash) {
					torch.setVisibility(View.VISIBLE);
					view.findViewById(R.id.torchview).setVisibility(
							View.VISIBLE);
				} else {
					torch.setVisibility(View.GONE);
					view.findViewById(R.id.torchview).setVisibility(View.GONE);
				}
				mPref.edit().putBoolean(PreferenceHelper.HAS_FLASH, hasFlash)
						.commit();
				super.onPostExecute(result);
			}

		}.execute();
		
		new AsyncTask<Void, Void, Void>() {
			boolean hasHotspot;
			SharedPreferences mPref;

			@Override
			protected void onPreExecute() {
				mPref = getSharedPreferences(
						"saphion.batterycaster_preferences", MODE_MULTI_PROCESS);
				if (mPref.getBoolean(PreferenceHelper.HAS_HOTSPOT, true)) {
					wifihotpot.setVisibility(View.VISIBLE);
					view.findViewById(R.id.viewwifihotspot).setVisibility(
							View.VISIBLE);
				} else {
					wifihotpot.setVisibility(View.GONE);
					view.findViewById(R.id.viewwifihotspot).setVisibility(View.GONE);
				}
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					Looper.prepare();
				} catch (Exception ex) {
				}
				WifiApManager wam = new WifiApManager(getApplicationContext());
				hasHotspot = wam.hasWifiApSupport();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (hasHotspot) {
					wifihotpot.setVisibility(View.VISIBLE);
					view.findViewById(R.id.viewwifihotspot).setVisibility(
							View.VISIBLE);
				} else {
					wifihotpot.setVisibility(View.GONE);
					view.findViewById(R.id.viewwifihotspot).setVisibility(View.GONE);
				}
				mPref.edit().putBoolean(PreferenceHelper.HAS_FLASH, hasHotspot)
						.commit();
				super.onPostExecute(result);
			}

		}.execute();
		

		/*
		 * try{ CameraDevice cd = new CameraDevice(); cd.acquireCamera();
		 * boolean hasFlash = cd.supportsTorchMode(); cd.releaseCamera();
		 * 
		 * if (hasFlash) torch.setVisibility(View.VISIBLE); else
		 * torch.setVisibility(View.GONE); }catch(Exception ex){}
		 */
		view.findViewById(R.id.ibbattery).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(ToggleDialog.this,
								TabNavigation.class));
						dialog.dismiss();
					}
				});

		view.findViewById(R.id.ibsettings).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent dialogIntent = new Intent(
								android.provider.Settings.ACTION_SETTINGS);
						dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(dialogIntent);
						dialog.dismiss();
					}
				});

		setRadioGroup((RadioGroup) view.findViewById(R.id.rgtoggles));
		setVolumeSeekBars();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			amode.setVisibility(View.GONE);
			view.findViewById(R.id.amodeView).setVisibility(View.GONE);
		} else {
			amode.setVisibility(View.VISIBLE);
			view.findViewById(R.id.amodeView).setVisibility(View.VISIBLE);
		}

		((TextView) view.findViewById(R.id.tvHeader)).setTypeface(Typeface
				.createFromAsset(getAssets(), Constants.FONT_DOSIS));

		setChecked();
		Listeners();
		sb = (SeekBar) view.findViewById(R.id.sbBrightnessSliderNB);
		final CheckBox cb = (CheckBox) view
				.findViewById(R.id.cbBrightnessAutoNB);

		sb.setProgress(System.getInt(getContentResolver(),
				System.SCREEN_BRIGHTNESS, -1));
		cb.setChecked(System.getInt(getContentResolver(),
				System.SCREEN_BRIGHTNESS_MODE, -1) == System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
		if (cb.isChecked())
			((ImageView) view.findViewById(R.id.ivbrightnessImageNB))
					.setImageResource(R.drawable.bauto_on);
		else
			((ImageView) view.findViewById(R.id.ivbrightnessImageNB))
					.setImageResource(R.drawable.bhalf_on);
		setEnabled(!cb.isChecked(), sb);

		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// handler.postDelayed(runnable, 5000);
				sendBroadcast(new Intent(Intents.SWITCHER_NOTI));

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// handler.removeCallbacks(runnable);
				cb.setChecked(false);

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				toggletoManual(progress);

			}
		});

		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				setEnabled(!isChecked, sb);
				if (isChecked) {
					((ImageView) view.findViewById(R.id.ivbrightnessImageNB))
							.setImageResource(R.drawable.bauto_on);
					toggleToAuto();

				} else {
					((ImageView) view.findViewById(R.id.ivbrightnessImageNB))
							.setImageResource(R.drawable.bhalf_on);
					toggletoManual(System.getInt(getContentResolver(),
							System.SCREEN_BRIGHTNESS, -1));

				}
				sendBroadcast(new Intent(Intents.SWITCHER_NOTI));
			}
		});

		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		/*
		 * WindowManager.LayoutParams WMLP = dialog.getWindow().getAttributes();
		 * 
		 * WMLP.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL; WMLP.y = 100;
		 * 
		 * dialog.getWindow().setAttributes(WMLP);
		 */
		dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				try {
					if (ringtone != null)
						ringtone.stop();
				} catch (Exception ex) {
				}
				sendBroadcast(new Intent(Intents.SWITCHER_NOTI));
				finish();
				overridePendingTransition(R.anim.abc_fade_in,
						R.anim.abc_fade_out);
			}
		});

		dialog.show();
		// handler.removeCallbacks(runnable);
		// handler.postDelayed(runnable, 5000);

	}

	private void setVolumeSeekBars() {
		try {
			final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			SeekBar ringSeekbar = (SeekBar) view.findViewById(R.id.sbRing);
			SeekBar musicSeekbar = (SeekBar) view.findViewById(R.id.sbMusic);
			SeekBar alarmSeekbar = (SeekBar) view.findViewById(R.id.sbAlarm);
			setVolumeControlStream(AudioManager.STREAM_RING);
			setStreams(audioManager, alarmSeekbar, AudioManager.STREAM_ALARM);
			setStreams(audioManager, musicSeekbar, AudioManager.STREAM_MUSIC);
			setStreams(audioManager, ringSeekbar, AudioManager.STREAM_RING);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	Ringtone ringtone;

	public void setStreams(final AudioManager audioManager, SeekBar seekbar,
			final int streamType) {
		try {
			seekbar.setKeyProgressIncrement(1);
			seekbar.setMax(audioManager.getStreamMaxVolume(streamType));
			seekbar.setProgress(audioManager.getStreamVolume(streamType));

			seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onStopTrackingTouch(SeekBar arg0) {
					switch (streamType) {
					case AudioManager.STREAM_ALARM:
						ringtone = RingtoneManager.getRingtone(
								getBaseContext(),
								RingtoneManager
										.getDefaultUri(RingtoneManager.TYPE_ALARM)); 
						break;
					case AudioManager.STREAM_RING:
					case AudioManager.STREAM_MUSIC:
						ringtone = RingtoneManager.getRingtone(
								getBaseContext(),
								RingtoneManager
										.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
						break;
					}

					audioManager.setStreamVolume(streamType,
							arg0.getProgress(),
							AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

					
					try {
						ringtone.setStreamType(streamType);
						ringtone.stop();
						ringtone.play();
					} catch (Exception ex) {
					}
				}

				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
					if (ringtone != null)
						ringtone.stop();
					setVolumeControlStream(streamType);
				}

				@Override
				public void onProgressChanged(SeekBar arg0, int progress,
						boolean arg2) {
					audioManager.setStreamVolume(streamType, progress,
							AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

				}

			});
			seekbar.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					setVolumeControlStream(streamType);
					return false;
				}
			});
			seekbar.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						setVolumeControlStream(streamType);
					} else {

					}
					try {
						if (ringtone != null)
							ringtone.stop();
					} catch (Exception ex) {
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setRadioGroup(RadioGroup group) {
		updateRadioGroup();
		group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				if (m_ignoreChange)
					return;

				switch (checkedId) {
				case R.id.rbVolumeOnVib:
					ringAndVibrate();
					break;
				case R.id.rbVolumeOn:
					ring();
					break;
				case R.id.rbVibrate:
					vibrate();
					break;
				case R.id.rbVolumeOff:
					silent();
					break;
				}

				/*
				 * RadioButton radio = (RadioButton) findViewById(checkedId); if
				 * (radio != null) radio.setTextSize(30);
				 */

				// RingToggle.this.close();
			}
		});

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

		/*
		 * switch (ToggleHelper.getBrightnessMode(getBaseContext())) { case 1:
		 * bness.setImageResource(R.drawable.blow_on); break; case 2:
		 * bness.setImageResource(R.drawable.bhalf_on); break; case 3:
		 * bness.setImageResource(R.drawable.bfull_on); break; case 0:
		 * bness.setImageResource(R.drawable.bauto_on); break; }
		 */

		torch.setImageResource(R.drawable.lightbulb_off);

		if (ToggleHelper.isRotationEnabled(getBaseContext()))
			arotate.setImageResource(R.drawable.orientation_on);
		else
			arotate.setImageResource(R.drawable.orientation_off);
		WifiApManager wam = new WifiApManager(getApplicationContext());
		if (wam.isWifiApEnabled())
			wifihotpot.setImageResource(R.drawable.wifi_hotspot_on);
		else
			wifihotpot.setImageResource(R.drawable.wifi_hotspot_off);
	}

	public void Listeners() {

		wifi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToggleWifi tb = (new ToggleWifi(getBaseContext(), wifi));
				tb.execute();
			}
		});

		wifi.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Intent dialogIntent = new Intent(
						android.provider.Settings.ACTION_WIFI_SETTINGS);
				dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(dialogIntent);
				dialog.dismiss();
				return true;
			}
		});

		mdata.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToggleData tb = (new ToggleData(getBaseContext(), mdata));
				tb.execute();
			}
		});

		mdata.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Intent dialogIntent = new Intent(
						android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
				dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(dialogIntent);
				dialog.dismiss();
				return true;
			}
		});

		btooth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToggleBtooth tb = (new ToggleBtooth(getBaseContext(), btooth));
				tb.execute();
			}
		});

		btooth.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Intent dialogIntent = new Intent(
						android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
				dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(dialogIntent);
				dialog.dismiss();
				return true;
			}
		});

		amode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToggleAMode tb = (new ToggleAMode(getBaseContext(), amode));
				tb.execute();
			}
		});

		amode.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Intent dialogIntent = new Intent(
						android.provider.Settings.ACTION_AIRPLANE_MODE_SETTINGS);
				dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(dialogIntent);
				dialog.dismiss();
				return true;
			}
		});

		sync.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToggleSync tb = (new ToggleSync(getBaseContext(), sync));
				tb.execute();
			}
		});

		sync.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Intent dialogIntent = new Intent(
						android.provider.Settings.ACTION_SYNC_SETTINGS);
				dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(dialogIntent);
				dialog.dismiss();
				return true;
			}
		});

		torch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				ToggleDialog.this.startService(new Intent(ToggleDialog.this,
						TorchService.class));

			}
		});

		wifihotpot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				WifiApManager wam = new WifiApManager(getApplicationContext());
				wam.toggleHotspot(getBaseContext());
			}
		});

		wifihotpot.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Intent dialogIntent = new Intent(
						android.provider.Settings.ACTION_WIRELESS_SETTINGS);
				dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(dialogIntent);
				dialog.dismiss();
				return true;
			}
		});

		/*
		 * bness.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * ToggleBness tb = (new ToggleBness(getBaseContext(), bness,
		 * getWindow())); tb.toggle(); } });
		 */

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

		arotate.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Intent dialogIntent = new Intent(
						android.provider.Settings.ACTION_DISPLAY_SETTINGS);
				dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(dialogIntent);
				dialog.dismiss();
				return true;
			}
		});

	}

	protected void toggletoManual(int bness) {
		final ContentResolver resolver = getContentResolver();

		// Get the current brightness mode.
		int mode = System.getInt(resolver, System.SCREEN_BRIGHTNESS_MODE, -1);
		// Figure out which mode to switch to and pop up a toast.
		Window window = getWindow();
		WindowManager.LayoutParams lp;
		mode = System.SCREEN_BRIGHTNESS_MODE_MANUAL;
		System.putInt(resolver, System.SCREEN_BRIGHTNESS_MODE, mode);
		System.putInt(resolver, System.SCREEN_BRIGHTNESS, bness);
		// window.getAttributes().screenBrightness = 0.1f;
		Log.d("Switching to manual mode with brightness "
				+ window.getAttributes().screenBrightness + " & "
				+ System.getInt(resolver, System.SCREEN_BRIGHTNESS, -1));
		lp = window.getAttributes();
		lp.screenBrightness = (float) (bness / 255);
		window.setAttributes(lp);

	}

	protected void toggleToAuto() {
		final ContentResolver resolver = getContentResolver();

		// Get the current brightness mode.
		int mode = System.getInt(resolver, System.SCREEN_BRIGHTNESS_MODE, -1);
		// Figure out which mode to switch to and pop up a toast.
		Window window = getWindow();
		WindowManager.LayoutParams lp;
		mode = System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
		System.putInt(resolver, System.SCREEN_BRIGHTNESS_MODE, mode);
		Log.d("Switching to automatic mode with brightness "
				+ window.getAttributes().screenBrightness + " & "
				+ System.getInt(resolver, System.SCREEN_BRIGHTNESS, -1));
		lp = window.getAttributes();
		lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
		window.setAttributes(lp);
	}

}
