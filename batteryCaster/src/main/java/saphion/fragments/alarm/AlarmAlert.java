package saphion.fragments.alarm;

import java.util.LinkedList;
import java.util.List;

import net.frakbot.glowpadbackport.GlowPadView;
import net.frakbot.glowpadbackport.GlowPadView.OnTriggerListener;
import saphion.batterycaster.R;
import saphion.batterycaster.providers.Alarm;
import saphion.fragment.alarm.alert.Intents;
import saphion.logger.Log;
import saphion.utils.ActivityFuncs;
import saphion.utils.PreferenceHelper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class AlarmAlert extends Activity implements OnTriggerListener {

	SharedPreferences mPref;

	public int readbattery() {
		Intent batteryIntent = getApplicationContext().registerReceiver(null,
				new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		int rawlevel = batteryIntent.getIntExtra("level", -1);
		double scale = batteryIntent.getIntExtra("scale", -1);

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

		return level;
	}

	private void disableOnePercentHack(String reason) {

		mPref.edit().putBoolean(PreferenceHelper.KEY_ONE_PERCENT_HACK, false)
				.commit();

		saphion.logger.Log.d("Disabling one percent hack due to: " + reason);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		return;
		// super.onBackPressed();
	}

	boolean wasSilent = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		Log.d("onDestroy");
		// Log.Toast(getBaseContext(), "onDestroy", Toast.LENGTH_LONG);
		try {
			getBaseContext().unregisterReceiver(mReceiver);
			// mMediaPlayer.stop();
			if (ringtone.isPlaying())
				ringtone.stop();
			handler.removeCallbacks(runnable);
			if (wasSilent) {

				audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0,
						AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			}
			mTelephonyManager.listen(mPhoneStateListener, 0);
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.d("Error in destroying: " + ex.toString());
			// Log.Toast(getBaseContext(),
			// "Error in destroying: " + ex.toString(), Toast.LENGTH_LONG);
		}
		super.onDestroy();
	}

	private TelephonyManager mTelephonyManager;

	private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String ignored) {
			// The user might already be in a call when the alarm fires. When
			// we register onCallStateChanged, we get the initial in-call state
			// which kills the alarm. Check against the initial call state so
			// we don't kill the alarm during a call.
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {

		Log.d("onPause");
		// Log.Toast(getBaseContext(), "onPause", Toast.LENGTH_LONG);

		super.onPause();
	}

	private GlowPadView mGlowPadView;
	protected static final String SCREEN_OFF = "screen_off";

	int level = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// getSupportActionBar().hide();

		setContentView(R.layout.alarmalert);

		mPref = getSharedPreferences(getPackageName() + "_preferences",
				MODE_MULTI_PROCESS);

		mGlowPadView = (GlowPadView) findViewById(R.id.glow_pad_view);

		mGlowPadView.setOnTriggerListener(this);

		// Listen for incoming calls to kill the alarm.
		mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mTelephonyManager.listen(mPhoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);

		Log.d("onCreate");
		// Log.Toast(getBaseContext(), "onCreate", Toast.LENGTH_LONG);

		// uncomment this to make sure the glowpad doesn't vibrate on touch
		// mGlowPadView.setVibrateEnabled(false);

		// uncomment this to hide targets
		//mGlowPadView.setShowTargetsOnIdle(false);

		final Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		// Turn on the screen unless we are being launched from the AlarmAlert
		// subclass as a result of the screen turning off.

		if (!getIntent().getBooleanExtra(SCREEN_OFF, false)) {
			win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
					| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
					| WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
		}

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intents.ALARM_DISMISS_ACTION);
		registerReceiver(mReceiver, filter);
		pos = getIntent().getIntExtra(PreferenceHelper.BAT_VALS, 0);

		int level = getIntent().getIntExtra(PreferenceHelper.CURR_RING, 72);
		level = readbattery();

		loadAlarms();

		// Log.Toast(getBaseContext(), level + " in alert, id: " + pos +
		// " size: " + mAlarms.size(), Toast.LENGTH_LONG);

		try {
			playAlarm();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ImageView iv = (ImageView) findViewById(R.id.ivlockscreen);
		LayoutParams lp = iv.getLayoutParams();
		iv.setImageBitmap(ActivityFuncs.getLockScreen(level, lp.width,
				lp.height, getBaseContext(), mAlarms.get(0).label));

	}

	List<Alarm> mAlarms;

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// int id = intent.getIntExtra(Intents.EXTRA_ID, -1);
			if (action.equals(Intents.ALARM_DISMISS_ACTION)) {
				// if (mAlarm.getId() == id) {
				finish();
				// }
			}
		}
	};
	int pos = 0;
	private boolean isRinging = false;
	Ringtone ringtone;

	void loadAlarms() {
		List<Alarm> mnAlarms = Alarm.getAlarms(getContentResolver(),
				Alarm.BATTERY + " = ? ", new String[] { "" + level });
		mAlarms = new LinkedList<Alarm>();
		for (int i = 0; i < mnAlarms.size(); i++) {

			if (mnAlarms.get(i).enabled) {
				mAlarms.add(mnAlarms.get(i));

				if (mnAlarms.get(i).daysOfWeek.getBitSet() == 0) {
					mnAlarms.get(i).enabled = false;
					Alarm.updateAlarm(getContentResolver(), mnAlarms.get(i));
				}

			}
		}

		Log.d("Length is: " + mAlarms.size() + " NLength is: "
				+ mnAlarms.size());

		if (mAlarms.size() == 0)
			finish();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		// Logger.getDefaultLogger().d("AlarmAlert.OnNewIntent()");

		mPref = getSharedPreferences(getPackageName() + "_preferences",
				MODE_MULTI_PROCESS);

		pos = intent.getIntExtra(PreferenceHelper.BAT_VALS, 0);
		loadAlarms();

		// Log.Toast(getBaseContext(), level + " in alert, id: " + pos +
		// " size: " + mAlarms.size(), Toast.LENGTH_LONG);

		try {
			playAlarm();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("new Intent");
		// Log.Toast(getBaseContext(), "New Intent", Toast.LENGTH_LONG);
		// mAlarm = alarmsManager.getAlarm(id);

		// setTitle();
	}

	Vibrator vib;
	boolean vibrate;
	boolean repeat;
	AudioManager audioManager;
	// Volume suggested by media team for in-call alarms.
	private static final float IN_CALL_VOLUME = 0.125f;

	public void playAlarm() throws Exception {
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int volume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);

		// volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
		vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		// vib.vibrate(new long[] { 0, 500 }, 1);

		vibrate = mAlarms.get(0).vibrate;

		// vibrate = hasVibrator();

		if (vibrate && hasVibrator())
			vib.vibrate(200);
		// int vol = mPref.getInt(PreferenceHelper.ALARM_VOL, 5);

		if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) == 0) {
			wasSilent = true;
			if (!mPref.getBoolean(PreferenceHelper.PLAY_WHEN_SILENT, false)) {
				volume = 0;
				Log.d("Silencing the alarm");
				// Log.Toast(getBaseContext(), "Silencing the alarm",
				// Toast.LENGTH_LONG);
			} else {
				audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume,
						AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			}
		} else {
			audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume,
					AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		}

		// Check if we are in a call. If we are, use the in-call alarm
		// resource at a low volume to not disrupt the call.
		if (mTelephonyManager.getCallState() != TelephonyManager.CALL_STATE_IDLE) {
			Log.d("Using the in-call alarm");
			audioManager.setStreamVolume(AudioManager.STREAM_ALARM,
					(int) IN_CALL_VOLUME,
					AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			vibrate = false;
		}

		// audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume,
		// AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		/**
		 * My Modifications
		 */
		int silenceafter = Integer.parseInt(mPref.getString(
				PreferenceHelper.SILENCE, "-99"));

		if (silenceafter != -99) {
			fhandler.postDelayed(frunnable, silenceafter * 60 * 1000);
		}

		/*
		 * ArrayList<String> ringTone = AlarmPreference.retStrings(
		 * getBaseContext(), AlarmPreference.RINGTONE);
		 */
		String ringtoneToPlay = mAlarms.get(0).alert.toString();// ringTone.get(pos);
		/*
		 * if (ringTone.equals("Default")) ringtoneToPlay = "";
		 */

		Uri toPlay;
		try {
			Uri soundUri = TextUtils.isEmpty(ringtoneToPlay) ? null : Uri
					.parse(ringtoneToPlay);
			toPlay = soundUri == null ? RingtoneManager
					.getActualDefaultRingtoneUri(getBaseContext(),
							RingtoneManager.TYPE_ALARM) : soundUri;
			// Log.Toast(getBaseContext(), "Playing Ringtone",
			// Toast.LENGTH_LONG);
		} catch (Exception ex) {

			Log.d(ex.toString());
			// Log.Toast(getBaseContext(), "Exception in Playing Ringtone",
			// Toast.LENGTH_LONG);

			toPlay = RingtoneManager.getActualDefaultRingtoneUri(
					getBaseContext(), RingtoneManager.TYPE_ALARM);

			if (toPlay == null) {
				// alert is null, using backup
				toPlay = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				if (toPlay == null) { // I can't see this ever being null (as
										// always have a default notification)
										// but just incase
					// alert backup is null, using 2nd backup
					toPlay = RingtoneManager
							.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
				}
			}
		}
		ringtone = RingtoneManager.getRingtone(getBaseContext(), toPlay);
		// Log.Toast(getBaseContext(), ringtoneToPlay, Toast.LENGTH_LONG);

		if (ringtone != null) {
			ringtone.setStreamType(AudioManager.STREAM_ALARM);
			if (ringtone.isPlaying()) {
				ringtone.stop();
			}
			ringtone.play();
			isRinging = true;

		}
		handler.post(runnable);

		Log.d("play Alarm");
		// Log.Toast(getBaseContext(), "play Alarm", Toast.LENGTH_LONG);

		/*
		 * try {
		 * 
		 * mMediaPlayer = new MediaPlayer(); mMediaPlayer.setDataSource(this,
		 * toPlay); // if
		 * (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) // {
		 * mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
		 * mMediaPlayer.setLooping(true); mMediaPlayer.prepare();
		 * mMediaPlayer.start(); handler.post(runnable); // } } catch (Exception
		 * e) { Log.d(e.toString()); }
		 */
	}

	@SuppressLint("NewApi")
	private boolean hasVibrator() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return true;
		}
		return vib.hasVibrator();
	}

	public Handler handler = new Handler();
	public Runnable runnable = new Runnable() {

		public void run() {
			handler.removeCallbacks(runnable);
			handler.postDelayed(runnable, 1000);
			if (vibrate)
				vib.vibrate(300);
			mGlowPadView.ping();
			if (ringtone != null)
				if (!ringtone.isPlaying()) {
					ringtone.play();
				}

			Log.d("Handler");
			if (once) {
				// Log.Toast(getBaseContext(), "Handler", Toast.LENGTH_LONG);
				once = false;
			}

		}
	};

	public boolean once = true;

	public Handler fhandler = new Handler();
	public Runnable frunnable = new Runnable() {

		public void run() {

			try {
				onTrigger(mGlowPadView, 0);
			} catch (Exception ex) {
				Log.d(ex.toString());
				// Log.Toast(getBaseContext(), ex.toString(),
				// Toast.LENGTH_LONG);
			}
			try {
				onTrigger(mGlowPadView, 1);
			} catch (Exception ex) {
				Log.d(ex.toString());
				// Log.Toast(getBaseContext(), ex.toString(),
				// Toast.LENGTH_LONG);
			}
			try {
				onTrigger(mGlowPadView, 2);
			} catch (Exception ex) {
				Log.d(ex.toString());
				// Log.Toast(getBaseContext(), ex.toString(),
				// Toast.LENGTH_LONG);
			}
			try {
				onTrigger(mGlowPadView, 3);
			} catch (Exception ex) {
				Log.d(ex.toString());
				// Log.Toast(getBaseContext(), ex.toString(),
				// Toast.LENGTH_LONG);
			}

		}
	};

	@Override
	public void onGrabbed(View v, int handle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReleased(View v, int handle) {
		mGlowPadView.ping();

	}

	@Override
	public void onTrigger(View v, int target) {
		final int resId = mGlowPadView.getResourceIdForTarget(target);
		switch (resId) {
		case R.drawable.ic_item_dismiss:
			Intent intent;
			intent = new Intent(Intents.ALARM_DISMISS_ACTION);
			intent.putExtra(PreferenceHelper.BAT_VALS, pos);

			sendBroadcast(intent);

			if (isRinging)
				ringtone.stop();
			// mMediaPlayer.stop();
			handler.removeCallbacks(runnable);
			super.onBackPressed();
			break;
		default:

			// Code should never reach here.
		}

	}

	@Override
	public void onGrabbedStateChange(View v, int handle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinishFinalAnimation() {
		// TODO Auto-generated method stub

	}

}
