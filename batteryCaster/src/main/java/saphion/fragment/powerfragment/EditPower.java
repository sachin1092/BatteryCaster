package saphion.fragment.powerfragment;

import java.util.ArrayList;

//import org.jraf.android.backport.switchwidget.SwitchPreference;

import saphion.batterycaster.R;
import saphion.utils.ToggleHelper;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

public class EditPower extends ActionBarActivity implements
		OnPreferenceChangeListener {

	public static final String PROF_NAME = "pfname";
	public static final String AMODE = "amode";
	public static final String MDATA = "mdata";
	public static final String WIFI = "wifi";
	public static final String BLUETOOTH = "btooth";
	public static final String SYNC = "sync";
	public static final String VIB = "vib";
	public static final String SCREEN_TIMEOUT = "stimeoutlist";
	public static final String AUTO_BNESS = "autobtness";
	public static final String BNESS_SEEKBAR = "btness_seekbar";
	public static final String AROTATE = "togRot";
	public static final String RINGMODE = "ringmode";
	public static final String POS = "position";
	public static final String TRIGGER_B_LEVEL = "trigger_val";
	public static final String TRIGGER = "trigger";
	public static final String S_OFF_MDATA = "mdataScreenOff";
	public static final String S_OFF_INT_MDATA = "mdataScreenOffValues";
	public static final String S_OFF_WIFI = "wifiScreenOff";
	public static final String S_OFF_INT_WIFI = "wifiScreenOffValues";
	public static final String TRIGGER_MODE = "tmode";

	EditTextPreference et;
//	SwitchPreference wifi, amode, btooth, mdata, sync, vib, arotate;
	//SeekBarPreference spl;
	PreferenceScreen blevel;
	CheckBoxPreference cbp, trig, s_off_mdata, s_off_wifi;
	ListPreference rmode, stimeout, s_off_int_mdata, s_off_int_wifi, t_mode;
	int pos;
	boolean changed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Edit Power Profile");
        getSupportActionBar().setBackgroundDrawable(
                getResources().getDrawable(R.drawable.actionbar_back));
        getSupportActionBar().setSplitBackgroundDrawable(new ColorDrawable(0xff161616));
        setContentView(R.layout.power_editor);
		/*addPreferencesFromResource(R.xml.powereditor);

		pos = getIntent().getIntExtra(POS, 0);
		et = (EditTextPreference) findPreference(PROF_NAME);
		et.setText(getIntent().getStringExtra("extraTitle"));
		et.setTitle(getIntent().getStringExtra("extraTitle"));
		et.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				et.setTitle(newValue.toString());
				changed = true;
				return true;
			}
		});
		cbp = (CheckBoxPreference) findPreference(AUTO_BNESS);
		//spl = (SeekBarPreference) findPreference(BNESS_SEEKBAR);
		blevel = (PreferenceScreen) findPreference(TRIGGER_B_LEVEL);

		trig = (CheckBoxPreference) findPreference(TRIGGER);
		s_off_mdata = (CheckBoxPreference) findPreference(S_OFF_MDATA);
		s_off_wifi = (CheckBoxPreference) findPreference(S_OFF_WIFI);
		s_off_int_mdata = (ListPreference) findPreference(S_OFF_INT_MDATA);
		s_off_int_wifi = (ListPreference) findPreference(S_OFF_INT_WIFI);
		t_mode = (ListPreference) findPreference(TRIGGER_MODE);

		trig.setChecked(getIntent().getBooleanExtra(TRIGGER, false));
		s_off_mdata.setChecked(getIntent().getBooleanExtra(S_OFF_MDATA, false));
		s_off_wifi.setChecked(getIntent().getBooleanExtra(S_OFF_WIFI, false));
		s_off_int_mdata.setValue(getIntent().getIntExtra(S_OFF_INT_MDATA, -99)
				+ "");
		s_off_int_wifi.setValue(getIntent().getIntExtra(S_OFF_INT_WIFI, -99)
				+ "");
		t_mode.setValueIndex(getIntent().getIntExtra(TRIGGER_MODE, 2));
		t_mode.setOnPreferenceChangeListener(this);

		trig.setOnPreferenceChangeListener(this);
		s_off_mdata.setOnPreferenceChangeListener(this);
		s_off_wifi.setOnPreferenceChangeListener(this);
		s_off_int_mdata.setOnPreferenceChangeListener(this);
		s_off_int_wifi.setOnPreferenceChangeListener(this);

		trig_value = getIntent().getIntExtra(TRIGGER_B_LEVEL, 100);
		blevel.setTitle("Battery Level: " + trig_value + "%");
		blevel.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				//showSeekBarPref(trig_value);
				//TODO
				return true;
			}
		});

		if (getIntent().getIntExtra(BNESS_SEEKBAR, -99) == -99)
			cbp.setChecked(true);
		*//*else
			spl.setProgress(getIntent().getIntExtra(BNESS_SEEKBAR, 177));

		spl.setEnabled(!cbp.isChecked());

		spl.setOnPreferenceChangeListener(this);*//*

		cbp.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				*//*spl.setEnabled(!(Boolean) newValue);*//*
				changed = true;
				return true;
			}
		});

//		wifi = (SwitchPreference) findPreference(WIFI);

//		amode = (SwitchPreference) findPreference(AMODE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//			amode.setEnabled(false);
//			amode.setShouldDisableView(true);
		}
*//*		mdata = (SwitchPreference) findPreference(MDATA);
		sync = (SwitchPreference) findPreference(SYNC);
		vib = (SwitchPreference) findPreference(VIB);
		btooth = (SwitchPreference) findPreference(BLUETOOTH);
		arotate = (SwitchPreference) findPreference(AROTATE);
		wifi.setChecked(getIntent().getBooleanExtra(WIFI, false));
		amode.setChecked(getIntent().getBooleanExtra(AMODE, false));
		mdata.setChecked(getIntent().getBooleanExtra(MDATA, false));
		sync.setChecked(getIntent().getBooleanExtra(SYNC, false));
		vib.setChecked(getIntent().getBooleanExtra(VIB, false));
		btooth.setChecked(getIntent().getBooleanExtra(BLUETOOTH, false));
		arotate.setChecked(getIntent().getBooleanExtra(AROTATE, false));

		wifi.setOnPreferenceChangeListener(this);
		amode.setOnPreferenceChangeListener(this);
		mdata.setOnPreferenceChangeListener(this);
		sync.setOnPreferenceChangeListener(this);
		vib.setOnPreferenceChangeListener(this);
		btooth.setOnPreferenceChangeListener(this);
		arotate.setOnPreferenceChangeListener(this);*//*

		rmode = (ListPreference) findPreference(RINGMODE);
		stimeout = (ListPreference) findPreference(SCREEN_TIMEOUT);

		rmode.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				changed = true;
				rmode.setSummary(rmode.getEntries()[Integer.parseInt(newValue
						.toString())]);
				return true;
			}
		});
		stimeout.setOnPreferenceChangeListener(this);

		rmode.setSummary(getBaseContext().getResources().getStringArray(
				R.array.ringmode)[getIntent().getIntExtra(RINGMODE, 0)]);
		stimeout.setValue(getIntent().getStringExtra(SCREEN_TIMEOUT));
		rmode.setValueIndex(getIntent().getIntExtra(RINGMODE, 0));*/
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem save = menu.add("Save");
        MenuItemCompat.setShowAsAction(save,
                MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		save.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				saveEverything();
				return true;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	protected void saveEverything() {
		ArrayList<PowerProfItems> item = PowerPreference
				.retPower(getBaseContext());
		if (pos < item.size())
			item.remove(pos);
		/*item.add(
				pos,
				new PowerProfItems(
						wifi.isChecked(),
						mdata.isChecked(),
						btooth.isChecked(),
						amode.isChecked(),
						sync.isChecked(),
						cbp.isChecked() ? -99
								: (*//*spl.getProgress() < ToggleHelper.MINIMUM_BACKLIGHT ?*//* ToggleHelper.MINIMUM_BACKLIGHT
										*//*: spl.getProgress()*//*), et.getText(), vib
								.isChecked(), arotate.isChecked(), Integer
								.parseInt(rmode.getValue()), stimeout
								.getValue(), trig.isChecked(), trig_value,
						Integer.parseInt(s_off_int_mdata.getValue()), Integer
								.parseInt(s_off_int_wifi.getValue()),
						s_off_mdata.isChecked(), s_off_wifi.isChecked(),
						Integer.parseInt(t_mode.getValue())));*/
		PowerPreference.savePower(getBaseContext(), item);
		/*if (pos == PreferenceManager.getDefaultSharedPreferences(
				getBaseContext()).getInt(POS, 0))
			sendBroadcast(new Intent(Intents.SWITCHER_INTENT).putExtra(
					PreferenceHelper.POSITION, pos));*/
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	private void showAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Battery Caster");
		builder.setIcon(R.drawable.ic_launcher);
		builder.setMessage("Abandon changes?" + "\n\n"
				+ "Click on Discard to discard changes." + "\n\n"
				+ "Click on Save to save them.");
		builder.setPositiveButton("Save",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						saveEverything();
					}

				});
		builder.setNegativeButton("Discard",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						finish();
						overridePendingTransition(R.anim.slide_in_right,
								R.anim.slide_out_left);

					}
				});
		builder.show();

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		changed = true;
		return true;
	}

	@Override
	public void onBackPressed() {
		if (changed)
			showAlert();
		else {
			finish();
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
		}
		//super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (changed)
				showAlert();
			else
				finish();
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	int trig_value = 0;

	/*public void showSeekBarPref(int val) {
		AlertDialog.Builder builder = new AlertDialog.Builder(EditPower.this);
		builder.setTitle("Select Battery Level");
		View view = LayoutInflater.from(EditPower.this).inflate(
				R.layout.number_picker, null);
		final NumberPicker picker = (NumberPicker) view
				.findViewById(R.id.numberPicker);
		builder.setView(view);
		picker.setMinValue(1);
		picker.setMaxValue(100);
		picker.setValue(val);

		trig_value = val;
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				trig_value = picker.getValue();
				blevel.setTitle("Battery Level: " + trig_value + "%");
				changed = true;

			}
		});
		builder.setNegativeButton(android.R.string.cancel, null);
		builder.show();
	}*/

}
