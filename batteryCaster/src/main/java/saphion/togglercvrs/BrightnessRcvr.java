package saphion.togglercvrs;

import saphion.batterycaster.R;
import saphion.fragment.alarm.alert.Intents;
import saphion.logger.Log;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.System;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class BrightnessRcvr extends ActionBarActivity {
	AlertDialog dialog;
	private Handler handler = new Handler();
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			handler.removeCallbacks(runnable);
			if (dialog != null)
				dialog.dismiss();

		}
	};

	public final static String FROM_SWITCHER = "from_switcher";

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
			//	WindowManager.LayoutParams.DIM_AMOUNT_CHANGED);
		
		/*ImageView iv = new ImageView(BrightnessRcvr.this);
		iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		iv.setBackgroundColor(0x00ffffff);
		iv.setFocusable(false);
		iv.setFocusableInTouchMode(false);
		setContentView(iv);*/
		
		if (getIntent().hasExtra(FROM_SWITCHER)) {
			if (getIntent().getIntExtra(FROM_SWITCHER, -99) == -99)
				toggleToAuto();
			else
				toggletoManual(getIntent().getIntExtra(FROM_SWITCHER, -99));
			finish();
			return;
		}

		showDialog();
		finish();

	}
	
	private void setEnabled(boolean b, SeekBar sb) {
		if(b){
			sb.setThumb(getResources().getDrawable(R.drawable.apptheme_scrubber_control_selector_holo_dark));
			sb.setProgressDrawable(getResources().getDrawable(R.drawable.apptheme_scrubber_progress_horizontal_holo_dark));
		}else{
			sb.setThumb(getResources().getDrawable(R.drawable.apptheme_scrubber_control_disabled_holo));
			sb.setProgressDrawable(getResources().getDrawable(R.drawable.apptheme_scrubber_track_holo_dark));
		}
		
	}

	public void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
		final View view = LayoutInflater.from(getBaseContext()).inflate(
				R.layout.bslider, null);
		final SeekBar sb = (SeekBar) view
				.findViewById(R.id.sbBrightnessSliderNB);
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
				sendBroadcast(new Intent(Intents.SWITCHER_NOTI));
				handler.postDelayed(runnable, 5000);

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				cb.setChecked(false);
				handler.removeCallbacks(runnable);

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
					handler.removeCallbacks(runnable);
					handler.postDelayed(runnable, 5000);
				} else {
					((ImageView) view.findViewById(R.id.ivbrightnessImageNB))
							.setImageResource(R.drawable.bhalf_on);
					toggletoManual(System.getInt(getContentResolver(),
							System.SCREEN_BRIGHTNESS, -1));
					handler.removeCallbacks(runnable);
					handler.postDelayed(runnable, 5000);
				}
			}

			
		});
		builder.setView(view);

		dialog = builder.create();
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		WindowManager.LayoutParams WMLP = dialog.getWindow().getAttributes();

		WMLP.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		WMLP.y = 100;

		dialog.getWindow().setAttributes(WMLP);
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				sendBroadcast(new Intent(Intents.SWITCHER_NOTI));
				finish();
				overridePendingTransition(R.anim.abc_fade_in,
						R.anim.abc_fade_out);
			}
		});

		dialog.show();
		handler.removeCallbacks(runnable);
		handler.postDelayed(runnable, 5000);

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
