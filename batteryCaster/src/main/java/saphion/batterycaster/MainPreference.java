package saphion.batterycaster;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

import java.util.HashMap;

//import org.jraf.android.backport.switchwidget.Switch;

import me.yugy.github.lswitchbackport.library.Switch;
import saphion.help.HelpActivity;
import saphion.logger.Log;
import saphion.utils.Constants;
import saphion.utils.PreferenceHelper;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainPreference extends ActionBarActivity implements
		OnClickListener, OnCheckedChangeListener {

	public String PREF_NAME;
	SharedPreferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.actionbar_back));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.mainpref);
		PREF_NAME = getPackageName() + "_preferences";
		mPref = getSharedPreferences(PREF_NAME, MODE_MULTI_PROCESS);
		initialiseAll();
		setParams();
		setListeners();
	}

	public void setParams() {
//		swUnit.setTextOff("F");
//		swUnit.setTextOn("C");
		if (mPref.getBoolean(PreferenceHelper.MAIN_TEMP, (Boolean) true)) {
			tvunit.setText(getResources().getString(
					R.string.using_celsius_metric_unit_));
			swUnit.setChecked(true);
		} else {
			tvunit.setText(getResources().getString(
					R.string.using_fahrenheit_metric_unit_));
			swUnit.setChecked(false);
		}
		String val = mPref.getString(PreferenceHelper.SILENCE, "-99");

		if (Integer.parseInt(val) == -99)
			tvsa.setText("Never");
		else
			tvsa.setText(Integer.parseInt(val) + " Minutes");

		enableOnePercentIfAppropriate();

		tbaism.setChecked(mPref.getBoolean(PreferenceHelper.PLAY_WHEN_SILENT,
				(Boolean) false));

		tbnot.setChecked(mPref.getBoolean(PreferenceHelper.ALERT_NOTIFICATION,
				(Boolean) false));

		tbsn.setChecked(mPref.getBoolean(PreferenceHelper.SHOW_NOTIFICATION,
				(Boolean) true));

		tbsp.setChecked(mPref.getBoolean(PreferenceHelper.SHOW_POPUP,
				(Boolean) false));

	}

	private void enableOnePercentIfAppropriate() {
		setEnabled(false);
		try {
			java.io.FileReader fReader = new java.io.FileReader(
					"/sys/class/power_supply/battery/charge_counter");
			java.io.BufferedReader bReader = new java.io.BufferedReader(fReader);
			if (Integer.valueOf(bReader.readLine()) <= PreferenceHelper.CHARGE_COUNTER_LEGIT_MAX)
				setEnabled(true);
			bReader.close();
		} catch (Exception e) {
			Log.d("Can't Enable One Percent Hack due to " + e.toString());
		}
	}

	void setListeners() {
		llunit.setOnClickListener(this);
		lloph.setOnClickListener(this);
		llsa.setOnClickListener(this);
		llaism.setOnClickListener(this);
		tvav.setOnClickListener(this);
		llnot.setOnClickListener(this);
		llsn.setOnClickListener(this);
		llsp.setOnClickListener(this);
		llcl.setOnClickListener(this);
		llqsg.setOnClickListener(this);
		llru.setOnClickListener(this);
		llsaf.setOnClickListener(this);
		llrab.setOnClickListener(this);
		swUnit.setOnCheckedChangeListener(this);
		tboph.setOnCheckedChangeListener(this);
		tbaism.setOnCheckedChangeListener(this);
		tbnot.setOnCheckedChangeListener(this);
		tbsn.setOnCheckedChangeListener(this);
		tbsp.setOnCheckedChangeListener(this);
	}

	public void setEnabled(boolean enabled) {
		lloph.setEnabled(enabled);
		if (!lloph.isEnabled()) {
			animate(lloph).alpha(0.4f).setDuration(0);
		} else {
			animate(lloph).alpha(1f).setDuration(0);
		}
	}

	public void initialiseAll() {
		llunit = (LinearLayout) findViewById(R.id.llUnitVal);
		lloph = (LinearLayout) findViewById(R.id.llOnePercent);
		llsa = (LinearLayout) findViewById(R.id.llSilenceAfter);
		llaism = (LinearLayout) findViewById(R.id.llAinSMode);
		tvav = (TextView) findViewById(R.id.tvAlarmVolume);
		llnot = (LinearLayout) findViewById(R.id.llAlarmNotification);
		llsn = (LinearLayout) findViewById(R.id.llShowNotification);
		llsp = (LinearLayout) findViewById(R.id.llShowPopUp);
		llcl = (LinearLayout) findViewById(R.id.llChangeLog);
		llqsg = (LinearLayout) findViewById(R.id.llQSG);
		llru = (LinearLayout) findViewById(R.id.llRateUs);
		llsaf = (LinearLayout) findViewById(R.id.llSF);
		llrab = (LinearLayout) findViewById(R.id.llRaBug);
		swUnit = (Switch) findViewById(R.id.swUnitVal);
		tvunit = (TextView) findViewById(R.id.tvUnitVal);
		tvsa = (TextView) findViewById(R.id.tvSilenceAfter);
		tboph = (ToggleButton) findViewById(R.id.tbOnePercent);
		tbaism = (ToggleButton) findViewById(R.id.tbAinSMode);
		tbnot = (ToggleButton) findViewById(R.id.tbAlarmNotification);
		tbsn = (ToggleButton) findViewById(R.id.tbShowNotification);
		tbsp = (ToggleButton) findViewById(R.id.tbShowPopUp);
	}

	LinearLayout llunit, lloph, llsa, llaism, llnot, llsn, llsp, llcl, llqsg,
			llru, llsaf, llrab;
	Switch swUnit;
	TextView tvunit, tvsa, tvav;
	ToggleButton tboph, tbaism, tbnot, tbsn, tbsp;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case (R.id.llUnitVal):
			swUnit.toggle();
			break;
		case (R.id.llOnePercent):
			tboph.toggle();
			break;
		case (R.id.llSilenceAfter):
			openSilenceAfterChooserDialog();
			break;
		case (R.id.llAinSMode):
			tbaism.toggle();
			break;
		case (R.id.tvAlarmVolume):
			openAlarmVolumeChooserDialog();
			break;
		case (R.id.llAlarmNotification):
			tbnot.toggle();
			break;
		case (R.id.llShowNotification):
			tbsn.toggle();
			break;
		case (R.id.llShowPopUp):
			tbsp.toggle();
			break;
		case (R.id.llChangeLog):
			ChangeLog cl = new ChangeLog(MainPreference.this);
			cl.getFullLogDialog().show();
			break;
		case (R.id.llQSG):
			startActivity(new Intent(getBaseContext(), HelpActivity.class));
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			break;
		case (R.id.llRateUs):
			AboutClass.launchMarket(getBaseContext(), getPackageName());
			break;
		case (R.id.llSF):
			sendMail("Feedback - Battery Caster");
			break;
		case (R.id.llRaBug):
			sendMail("Feedback - Battery Caster");
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {

		case (R.id.swUnitVal):
			mPref.edit()
					.putBoolean(PreferenceHelper.MAIN_TEMP, (Boolean) isChecked)
					.commit();
			if (isChecked) {
				tvunit.setText(getResources().getString(
						R.string.using_celsius_metric_unit_));
			} else {
				tvunit.setText(getResources().getString(
						R.string.using_fahrenheit_metric_unit_));
			}
			break;
		case (R.id.tbOnePercent):
			mPref.edit()
					.putBoolean(PreferenceHelper.KEY_ONE_PERCENT_HACK,
							(Boolean) isChecked).commit();
			break;
		case (R.id.tbAinSMode):
			mPref.edit()
					.putBoolean(PreferenceHelper.PLAY_WHEN_SILENT,
							(Boolean) isChecked).commit();
			break;
		case (R.id.tbAlarmNotification):
			mPref.edit()
					.putBoolean(PreferenceHelper.ALERT_NOTIFICATION,
							(Boolean) isChecked).commit();
			break;
		case (R.id.tbShowNotification):
			mPref.edit()
					.putBoolean(PreferenceHelper.SHOW_NOTIFICATION,
							(Boolean) isChecked).commit();
			break;
		case (R.id.tbShowPopUp):
			mPref.edit()
					.putBoolean(PreferenceHelper.SHOW_POPUP,
							(Boolean) isChecked).commit();
			break;
		}
	}

	@SuppressLint("NewApi")
	void openSilenceAfterChooserDialog() {
		String pref = mPref.getString(PreferenceHelper.SILENCE, "-99");
		String[] mArray = getResources().getStringArray(
				R.array.silenceAfter_values);
		int selection = 0;
		for (int i = 0; i < mArray.length; i++) {
			if ((pref).equalsIgnoreCase(mArray[i])) {
				selection = i;
			}
		}
		
		final View mView = LayoutInflater.from(MainPreference.this).inflate(
				R.layout.listpref, null, true);

		AlertDialog.Builder builder = new AlertDialog.Builder(
				MainPreference.this);
		// builder.setView(mView, 0,0,0,0);

		((TextView) mView.findViewById(R.id.tvchangeLogTitle))
				.setText("Silence After");
		Typeface tf = Typeface.createFromAsset(getAssets(),
				Constants.FONT_DOSIS_BOLD);
		((TextView) mView.findViewById(R.id.tvchangeLogTitle)).setTypeface(tf);
		((Button) mView.findViewById(R.id.bCancel)).setTypeface(tf);

		// builder.setCancelable(false);
		
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this,
                R.layout.singlelist, getResources()
				.getStringArray(R.array.silenceAfter));
		
		final ListView listView = (ListView)mView.findViewById(R.id.lvListPref);
		
		listView.setAdapter(mAdapter);
		listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setItemChecked(selection, true);


		final AlertDialog ad = builder.create();
		try {
			ad.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		} catch (Exception ex) {
		}
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				ad.getActionBar().hide();
		} catch (Exception ex) {

		}
		ad.setView(mView, 0, 0, 0, 0);

		((Button) mView.findViewById(R.id.bCancel))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ad.dismiss(); 
					}
				});
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				setNewSAVal(getResources().getStringArray(
						R.array.silenceAfter_values)[position]);
				ad.cancel();
			}
		});
		
        listView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				setNewSAVal(getResources().getStringArray(
						R.array.silenceAfter_values)[position]);
				ad.cancel();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});

		ad.show();
/*
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this,
				R.layout.singlelist, getResources()
						.getStringArray(R.array.silenceAfter));
		Builder mBuilder = new AlertDialog.Builder(MainPreference.this)
				.setTitle("Silence After").setSingleChoiceItems(mAdapter,
						selection, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								setNewSAVal(getResources().getStringArray(
										R.array.silenceAfter_values)[which]);
								dialog.cancel();
							}
						});
		AlertDialog mAlert = mBuilder.create();
		mAlert.show();*/
	}

	@SuppressLint("NewApi")
	void openAlarmVolumeChooserDialog() {
		final View mView = LayoutInflater.from(MainPreference.this).inflate(
				R.layout.volumepref, null, true);

		AlertDialog.Builder builder = new AlertDialog.Builder(
				MainPreference.this);
		// builder.setView(mView, 0,0,0,0);

		((TextView) mView.findViewById(R.id.tvchangeLogTitle))
				.setText("Alarm Volume");
		Typeface tf = Typeface.createFromAsset(getAssets(),
				Constants.FONT_DOSIS_BOLD);
		((TextView) mView.findViewById(R.id.tvchangeLogTitle)).setTypeface(tf);
		((Button) mView.findViewById(R.id.bChangeLogOK)).setTypeface(tf);
		((Button) mView.findViewById(R.id.bChangeLogMore)).setTypeface(tf);

		// builder.setCancelable(false);

		final AlertDialog ad = builder.create();
		try {
			ad.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		} catch (Exception ex) {
		}
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				ad.getActionBar().hide();
		} catch (Exception ex) {

		}

		audioManager = null;
		setVolumeControlStream(AudioManager.STREAM_ALARM);
		initControls((SeekBar) mView.findViewById(R.id.sbWidth));

		ad.setView(mView, 0, 0, 0, 0);

		(mView.findViewById(R.id.bAdd))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int prog = ((SeekBar) mView.findViewById(R.id.sbWidth))
								.getProgress();
						if (prog != ((SeekBar) mView.findViewById(R.id.sbWidth))
								.getMax())
							prog++;
						progress = prog;
						((SeekBar) mView.findViewById(R.id.sbWidth))
								.setProgress(prog);
						//audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
							//	prog, 0);
					}
				});

		( mView.findViewById(R.id.bMinus))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int prog = ((SeekBar) mView.findViewById(R.id.sbWidth))
								.getProgress();
						if (prog != 0)
							prog--;
						progress = prog;
						((SeekBar) mView.findViewById(R.id.sbWidth))
								.setProgress(prog);
					//	audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						//		prog, 0);
					}
				});

		((Button) mView.findViewById(R.id.bChangeLogOK))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						audioManager.setStreamVolume(AudioManager.STREAM_ALARM,
								progress, 0);
						ad.dismiss();
					}
				});

		((Button) mView.findViewById(R.id.bChangeLogMore))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ad.dismiss();
					}
				});

		ad.show();
	}

	int progress = 0;
	AudioManager audioManager;

	private void initControls(SeekBar volumeSeekbar) {
		try {
			audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			volumeSeekbar.setMax(audioManager
					.getStreamMaxVolume(AudioManager.STREAM_ALARM));
			volumeSeekbar.setProgress(progress = audioManager
					.getStreamVolume(AudioManager.STREAM_ALARM));

			volumeSeekbar
					.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
						@Override
						public void onStopTrackingTouch(SeekBar arg0) {
						}

						@Override
						public void onStartTrackingTouch(SeekBar arg0) {
						}

						@Override
						public void onProgressChanged(SeekBar arg0,
								int progress, boolean arg2) {
							MainPreference.this.progress = progress;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void setNewSAVal(String newValue) {
		mPref.edit().putString(PreferenceHelper.SILENCE, newValue.toString())
				.commit();
		if (Integer.parseInt((String) newValue) == -99)
			tvsa.setText("Never");
		else
			tvsa.setText(Integer.parseInt((String) newValue) + " Minutes");
	}

	public void sendMail(String string) {
		String emailaddress[] = { "me@sachinshinde.com" };
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailaddress);

		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, string);

		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Sent From:\n"

		+ "Manufacter: " + android.os.Build.MANUFACTURER + "\n" + "Model: "
				+ android.os.Build.MODEL

				+ "\nAndroid Version: " + Build.VERSION.RELEASE
				+ "\nApplication Version: " + getLibraryVersion() + "\n\n");
		emailIntent.setType("plain/text");
		try {
			startActivity(emailIntent);
		} catch (Exception ex) {
		}
	}

	public String getLibraryVersion() {
		PackageManager manager = getPackageManager();
		PackageInfo info;
		try {
			info = manager.getPackageInfo(getPackageName(), 0);
			return info.versionCode + " ";
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return "";
	}

}
