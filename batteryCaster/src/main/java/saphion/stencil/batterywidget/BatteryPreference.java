package saphion.stencil.batterywidget;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import saphion.batterycaster.MainPreference;
import saphion.batterycaster.R;
import saphion.logger.Log;
import saphion.services.UpdateService;
import saphion.utils.Constants;
import saphion.utils.Functions;
import saphion.utils.NewFunctions;
import saphion.utils.PreferenceHelper;
import saphion.utils.SpinnerHelper;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.os.EnvironmentCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.github.espiandev.showcaseview.ShowcaseView;

public class BatteryPreference extends ActionBarActivity implements
		OnCheckedChangeListener, OnClickListener, OnSeekBarChangeListener {

	@Override
	public void onBackPressed() {

		try {
			if (wph.hasChanged()) {
				showAlert();
			} else {
				super.onBackPressed();
			}
		} catch (Exception ex) {
			Log.d(ex.toString());
		}

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
						wph.saveEverything(getBaseContext(), awID);
						Intent result = new Intent();
						result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
								awID);
						setResult(RESULT_OK, result);
						int[] appWidgetIds = { awID };

						startService(new Intent(getBaseContext(),
								UpdateService.class)
								.putExtra(PreferenceHelper.EXTRA_WIDGET_IDS,
										appWidgetIds));
						finish();
					}

				});
		builder.setNegativeButton("Discard",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent result = new Intent();
						result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
								awID);
						setResult(RESULT_OK, result);
						int[] appWidgetIds = { awID };

						startService(new Intent(getBaseContext(),
								UpdateService.class)
								.putExtra(PreferenceHelper.EXTRA_WIDGET_IDS,
										appWidgetIds));
						finish();

					}
				});
		builder.show();

	}

	private int awID;
	WidPrefHelper wph;

	// SharedPreferences getPrefs;

	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.widgetpref);

		// PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit()
		// .remove("pTime").commit();

		Intent i = getIntent();
		Bundle extra = i.getExtras();

		if (extra != null) {
			awID = extra.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);

			Toast.makeText(getBaseContext(),
					"Quick Tip: Put fonts in "+Environment.getExternalStorageDirectory()+"/fonts to use them",
					Toast.LENGTH_LONG).show();

			final ViewFlipper iv = (ViewFlipper) findViewById(R.id.vfpreview);
			final WallpaperManager wallpaperManager = WallpaperManager
					.getInstance(this);
			final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
			Drawable d = new BitmapDrawable(getResources(),
					createPreview(drawableToBitmap(wallpaperDrawable)));
			// iv
			LayoutParams params = ((RelativeLayout) findViewById(R.id.rlpreview))
					.getLayoutParams();

			if (getResources().getDisplayMetrics().widthPixels > getResources()
					.getDisplayMetrics().heightPixels) {
				params.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.82);
				params.width = (int) (getResources().getDisplayMetrics().widthPixels / 2);
			} else {
				params.height = (int) (getResources().getDisplayMetrics().widthPixels * 0.65);
				params.width = (int) (getResources().getDisplayMetrics().widthPixels);
			}

			((RelativeLayout) findViewById(R.id.rlpreview))
					.setLayoutParams(params);
			((RelativeLayout) findViewById(R.id.rlpreview))
					.setBackgroundDrawable(d);

			wph = new WidPrefHelper(getBaseContext(), awID);

			switch (wph.getWidgetTheme()) {
			case 0:
				((ImageView) findViewById(R.id.PreviewimageView3))
						.setImageBitmap(Functions.battery(readbattery(),
								temperature, isconnected, getBaseContext(),
								wph, awID));
				break;
			case 1:
				((ImageView) findViewById(R.id.PreviewimageViewSecond))
						.setImageBitmap(Functions.battery(readbattery(),
								temperature, isconnected, getBaseContext(),
								wph, awID));
				break;
			case 2:
				((ImageView) findViewById(R.id.PreviewimageViewThird))
						.setImageBitmap(Functions.battery(readbattery(),
								temperature, isconnected, getBaseContext(),
								wph, awID));
				break;
			case 3:
				((ImageView) findViewById(R.id.PreviewimageViewFourth))
						.setImageBitmap(Functions.battery(readbattery(),
								temperature, isconnected, getBaseContext(),
								wph, awID));
				break;
			case 4:
				((ImageView) findViewById(R.id.PreviewimageViewFifth))
						.setImageBitmap(Functions.battery(readbattery(),
								temperature, isconnected, getBaseContext(),
								wph, awID));
				((SeekBar) findViewById(R.id.sbbatleftwidth))
						.setVisibility(View.GONE);
				((TextView) findViewById(R.id.tvbatleft))
						.setVisibility(View.GONE);
				break;

			}

			setVisibilties();

			settingListeners();

			readAllFonts(Environment.getExternalStorageDirectory() + "/fonts/");

			final Spinner s3 = (Spinner) findViewById(R.id.spbatteryfont);

			s3.setAdapter(new FontSelectorSpinner(this, fontretS, fontNamesS));

			s3.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					try {
						wph.setFont(fontNamesS[position]);
						/*
						 * getPrefs.edit()
						 * .putString(PreferenceHelper.WIDGET_FONT,
						 * fontNamesS[position]).commit();
						 */
					} catch (ArrayIndexOutOfBoundsException e) {
					}
					updateWidget();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

				}
			});

			String sel = wph.getFont();/*
										 * getPrefs.getString(PreferenceHelper.
										 * WIDGET_FONT, "empty");
										 */
			int j = 0;
			int sele = 0;
			for (j = 0; j < fontNamesS.length; j++) {
				if (fontNamesS[j].equals(sel)) {
					sele = j;
					break;
				} else
					sele = 0;
			}

			s3.setSelection(sele);

			/**
			 * Spinner for handle click events
			 */
			
			new SpinnerHelper(this,
					findViewById(R.id.spbatteryonclicked), wph.getWidgetClick(),
					new String[] {
						"Open Widget Settings", "Show Battery Info",
						"Show Battery Use" , "Show Toggles Dialog"}) {

				@Override
				public void onItemSelected(int position) {
					wph.setWidgetClick(position);

					if (position != 0) {
						showAlertDialog();
					}
				}
			};


			/*final Spinner s4 = (Spinner) findViewById(R.id.spbatteryonclicked);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.layout.spinnerlist, R.id.tvSpinner, new String[] {
							"Open Widget Settings", "Show Battery Info",
							"Show Battery Use" });

			s4.setAdapter(adapter);
			
			 * s4.setAdapter(new MySpinnerAdapater(this, new String[] {
			 * "Open Widget Settings", "Show Battery Info", "Show Battery Use"
			 * }));
			 

			s4.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {

					
					 * getPrefs.edit() .putInt(PreferenceHelper.WIDGET_ONCLICK,
					 * position) .commit();
					 
					wph.setWidgetClick(position);

					if (position != 0) {
						showAlertDialog(view);
					}

				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

				}
			});

			s4.setSelection(wph.getWidgetClick());*/// getPrefs.getInt(PreferenceHelper.WIDGET_ONCLICK,
													// 0)

			/**
			 * Spinner for text to show
			 */

			// final Spinner s5 = (Spinner) findViewById(R.id.spbatterytext);

			new SpinnerHelper(this,
					findViewById(R.id.spbatterytext), wph.getWidgetText(),
					new String[] { "Show Battery Level with % sign",
							"Show Battery Level without % sign",
							"Show Remaining hours",
							"Show Remaining hours without h",
							"Show Temperature in Celsius",
							"Show Temperature in Fahrenheit" }) {

				@Override
				public void onItemSelected(int position) {
					wph.setWidgetText(position);
					updateWidget();
				}
			};

			/*
			 * s5.setAdapter(new MySpinnerAdapater(this, new String[] {
			 * "Show Battery Level with % sign",
			 * "Show Battery Level without % sign", "Show Remaining hours",
			 * "Show Remaining hours without h", "Show Temperature in Celsius",
			 * "Show Temperature in Fahrenheit" }));
			 */

/*			adapter = new ArrayAdapter<String>(this, R.layout.spinnerlist,
					R.id.tvSpinner, new String[] {
							"Show Battery Level with % sign",
							"Show Battery Level without % sign",
							"Show Remaining hours",
							"Show Remaining hours without h",
							"Show Temperature in Celsius",
							"Show Temperature in Fahrenheit" });
			s5.setAdapter(adapter);

			s5.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {

					
					 * getPrefs.edit() .putInt(PreferenceHelper.WIDGET_TEXT,
					 * position) .commit();
					 
					wph.setWidgetText(position);
					updateWidget();

				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

				}
			});

			s5.setSelection(wph.getWidgetText());*/// getPrefs.getInt(PreferenceHelper.WIDGET_TEXT,
													// 0)

			/*
			 * android.os.StrictMode .setThreadPolicy(new
			 * android.os.StrictMode.ThreadPolicy.Builder()
			 * .detectDiskWrites().penaltyDialog().build());
			 */

			/**
			 * Setting the buttons for flippers
			 */

			final Button fore = (Button) findViewById(R.id.bwidgetfor);
			final Button prev = (Button) findViewById(R.id.bwidgetprev);

			if (getSharedPreferences("saphion.batterycaster_preferences",
					Context.MODE_MULTI_PROCESS).getInt("firsthelpint", 1) < 3) {
				ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
				co.hideOnClickOutside = true;
				ShowcaseView.insertShowcaseView(R.id.bwidgetfor, this,
						"Switch theme",
						"Click the button to switch to another theme.", co);
				getSharedPreferences("saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS)
						.edit()
						.putInt("firsthelpint",
								getSharedPreferences(
										"saphion.batterycaster_preferences",
										Context.MODE_MULTI_PROCESS).getInt(
										"firsthelpint", 1) + 1).commit();
			}

			fore.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// prev.setVisibility(View.VISIBLE);
					// fore.setVisibility(View.INVISIBLE);
					iv.setInAnimation(getBaseContext(), R.anim.slide_in_right);
					iv.setOutAnimation(getBaseContext(), R.anim.slide_out_left);
					if (wph.getWidgetTheme()/*
											 * getPrefs.getInt(PreferenceHelper.
											 * WIDGET_THEMES, 0)
											 */!= 4) {
						/*
						 * getPrefs.edit()
						 * .putInt(PreferenceHelper.WIDGET_THEMES,
						 * (getPrefs.getInt( PreferenceHelper.WIDGET_THEMES, 0))
						 * + 1).commit();
						 */
						wph.setWidgetTheme(wph.getWidgetTheme() + 1);
					} else {
						/*
						 * getPrefs.edit()
						 * .putInt(PreferenceHelper.WIDGET_THEMES, 0) .commit();
						 */
						wph.setWidgetTheme(0);
					}
					iv.showNext();
					updateWidget();

					if (wph.getWidgetTheme() == 4) {
						((SeekBar) findViewById(R.id.sbbatleftwidth))
								.setVisibility(View.GONE);
						((TextView) findViewById(R.id.tvbatleft))
								.setVisibility(View.GONE);
					} else {
						((SeekBar) findViewById(R.id.sbbatleftwidth))
								.setVisibility(View.VISIBLE);
						((TextView) findViewById(R.id.tvbatleft))
								.setVisibility(View.VISIBLE);
					}

				}
			});

			prev.setOnClickListener(new OnClickListener() {
 
				@Override
				public void onClick(View v) {
					// fore.setVisibility(View.VISIBLE);
					// prev.setVisibility(View.INVISIBLE);
					iv.setInAnimation(getBaseContext(), R.anim.slide_in_left);
					iv.setOutAnimation(getBaseContext(), R.anim.slide_out_right);

					if (wph.getWidgetTheme() != 0) {
						/*
						 * getPrefs.edit()
						 * .putInt(PreferenceHelper.WIDGET_THEMES,
						 * (getPrefs.getInt( PreferenceHelper.WIDGET_THEMES, 0))
						 * - 1).commit();
						 */
						wph.setWidgetTheme(wph.getWidgetTheme() - 1);
					} else {
						/*
						 * getPrefs.edit()
						 * .putInt(PreferenceHelper.WIDGET_THEMES, 4) .commit();
						 */
						wph.setWidgetTheme(4);
					}
					iv.showPrevious();
					updateWidget();

					if (wph.getWidgetTheme() == 4) {
						((SeekBar) findViewById(R.id.sbbatleftwidth))
								.setVisibility(View.GONE);
						((TextView) findViewById(R.id.tvbatleft))
								.setVisibility(View.GONE);
					} else {
						((SeekBar) findViewById(R.id.sbbatleftwidth))
								.setVisibility(View.VISIBLE);
						((TextView) findViewById(R.id.tvbatleft))
								.setVisibility(View.VISIBLE);
					}

				}
			});

			switch (wph.getWidgetTheme()) {
			case 0:
				break;
			case 1:
				iv.showNext();
				break;
			case 2:
				iv.showNext();
				iv.showNext();
				break;
			case 3:
				iv.showNext();
				iv.showNext();
				iv.showNext();
				break;
			case 4:
				iv.showNext();
				iv.showNext();
				iv.showNext();
				iv.showNext();
				break;
			}

			/**
			 * Seek bars
			 */

			SeekBar lineleft = (SeekBar) findViewById(R.id.sbbatleftwidth);
			SeekBar lineback = (SeekBar) findViewById(R.id.sbbatbackwidth);

			lineleft.setMax(65);
			lineback.setMax(65);

			lineleft.setProgress((int) wph.getLineLeft());
			if (wph.getWidgetTheme() != 4)
				lineback.setProgress((int) wph.getLineBack());
			else
				lineback.setProgress((int) wph.getLineBac());

			lineleft.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					/*
					 * getPrefs.edit()
					 * .putFloat(PreferenceHelper.WIDGET_LINE_LEFT,
					 * progress).commit();
					 */
					wph.setLineLeft(progress);
					updateWidget();

				}
			});

			lineback.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					/*
					 * getPrefs.edit()
					 * .putFloat(PreferenceHelper.WIDGET_LINE_BACK,
					 * progress).commit();
					 */
					if (wph.getWidgetTheme() != 4)
						wph.setLineBack(progress);
					else
						wph.setLineBac(progress);
					updateWidget();

				}
			});

			/**
			 * Checkbox
			 */
			((CheckBox) findViewById(R.id.cbbatteryonclicked)).setChecked(wph
					.getDisableClick());
			((CheckBox) findViewById(R.id.cbbatteryonclicked))
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							/*
							 * getPrefs.edit() .putBoolean(
							 * PreferenceHelper.DISABLE_CLICKS,
							 * isChecked).commit();
							 */
							wph.setDisableClick(isChecked);
							if (isChecked)
								findViewById(R.id.spbatteryonclicked).setVisibility(View.GONE);
							else
								findViewById(R.id.spbatteryonclicked).setVisibility(View.VISIBLE);

						}
					});

		} else {
			finish();
		}

	}

	@SuppressLint("NewApi")
	public void showAlertDialog() {
		final View mView = LayoutInflater.from(BatteryPreference.this).inflate(
				R.layout.alert, null, true);
		AlertDialog.Builder builder = new AlertDialog.Builder(BatteryPreference.this);
		
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
		
		Typeface tf = Typeface.createFromAsset(getAssets(),
				Constants.FONT_DOSIS);
		
		ad.setView(mView, 0, 0, 0, 0);
		
		((TextView)mView.findViewById(R.id.tvMessage)).setText("After selecting this on click event you won't be able to edit this widget's settings.");
		((TextView)mView.findViewById(R.id.tvMessage)).setTypeface(tf);
		((TextView) mView.findViewById(R.id.tvchangeLogTitle)).setText("Warning");
		((TextView) mView.findViewById(R.id.tvchangeLogTitle)).setTypeface(tf);
		((Button)mView.findViewById(R.id.bOK)).setTypeface(tf);
		((Button)mView.findViewById(R.id.bOK)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ad.dismiss();
			}
		});
		((TextView) mView.findViewById(R.id.tvchangeLogTitle)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.popup_window, 0, 0, 0);
		/*builder.setTitle("Warning");
		builder.setIcon(R.drawable.popup_window);
		builder.setMessage("After selecting this on click event you won't be able to edit this widget's settings.");
		builder.setPositiveButton("OK", null);
		builder.show();*/
		ad.show();

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	private void settingListeners() {

		((CheckBox) findViewById(R.id.cbbatterycolors))
				.setOnCheckedChangeListener(this);

		((RelativeLayout) findViewById(R.id.rlbattery1Color))
				.setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.rlbattery2Color))
				.setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.rlbattery3Color))
				.setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.rlbattery4Color))
				.setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.rlbattery5Color))
				.setOnClickListener(this);

		((RelativeLayout) findViewById(R.id.rlpluggedColor))
				.setOnClickListener(this);

		((SeekBar) findViewById(R.id.sbbatsize))
				.setOnSeekBarChangeListener(this);
	}

	private void setVisibilties() {

		((SeekBar) findViewById(R.id.sbbatsize)).setProgress((int) wph
				.getFontSize());

		((CheckBox) findViewById(R.id.cbbatterycolors)).setChecked(wph
				.getWidDiffColor());

		if (!wph.getWidDiffColor()) {
			((TextView) findViewById(R.id.tv5))
					.setText("Default Battery Color");
			((TextView) findViewById(R.id.tv5sub))
					.setText("Default Color for Battery");
			((RelativeLayout) findViewById(R.id.rlpluggedColor))
					.setVisibility(View.VISIBLE);
			((RelativeLayout) findViewById(R.id.rlbattery1Color))
					.setVisibility(View.VISIBLE);
			((RelativeLayout) findViewById(R.id.rlbattery2Color))
					.setVisibility(View.GONE);
			((RelativeLayout) findViewById(R.id.rlbattery3Color))
					.setVisibility(View.GONE);
			((RelativeLayout) findViewById(R.id.rlbattery4Color))
					.setVisibility(View.GONE);
			((RelativeLayout) findViewById(R.id.rlbattery5Color))
					.setVisibility(View.GONE);
			/*
			 * ((Divider) findViewById(R.id.dvd1)).setVisibility(View.GONE);
			 * ((Divider) findViewById(R.id.dvd2)).setVisibility(View.GONE);
			 * ((Divider) findViewById(R.id.dvd3)).setVisibility(View.GONE);
			 */
		} else {
			((TextView) findViewById(R.id.tv5)).setText("81% to 100%");
			((TextView) findViewById(R.id.tv5sub))
					.setText("Color for Battery for 81% to 100%");
			((RelativeLayout) findViewById(R.id.rlpluggedColor))
					.setVisibility(View.VISIBLE);
			((RelativeLayout) findViewById(R.id.rlbattery1Color))
					.setVisibility(View.VISIBLE);
			((RelativeLayout) findViewById(R.id.rlbattery2Color))
					.setVisibility(View.VISIBLE);
			((RelativeLayout) findViewById(R.id.rlbattery3Color))
					.setVisibility(View.VISIBLE);
			((RelativeLayout) findViewById(R.id.rlbattery4Color))
					.setVisibility(View.VISIBLE);
			((RelativeLayout) findViewById(R.id.rlbattery5Color))
					.setVisibility(View.VISIBLE);
			/*
			 * ((Divider) findViewById(R.id.dvd1)).setVisibility(View.VISIBLE);
			 * ((Divider) findViewById(R.id.dvd2)).setVisibility(View.VISIBLE);
			 * ((Divider) findViewById(R.id.dvd3)).setVisibility(View.VISIBLE);
			 */
		}

	}

	String[] fontNamesS;
	String[] fontretS;

	public String[] readAllFonts(String path) {
		List<String> fontNames = new ArrayList<String>();
		List<String> fontret = new ArrayList<String>();
		fontret.add("Default Font");
		fontNames.add("default");

		try {
			File temp = new File(path);

			String fontSuffix = ".ttf";
			String fontName = "";
			for (File font : temp.listFiles()) {
				fontName = font.getName();
				if (fontName.endsWith(fontSuffix)) {
					fontNames.add(font.getAbsolutePath());
					fontret.add(fontName.substring(0, fontName.indexOf(".ttf")));

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		fontNamesS = new String[fontNames.size()];
		fontretS = new String[fontNames.size()];

		for (int i = 0; i < fontNames.size(); i++) {
			fontNamesS[i] = fontNames.get(i);
			fontretS[i] = fontret.get(i);
		}

		return fontretS;
	}

	boolean isconnected;
	int level;
	float temperature;

	public int readbattery() {
		Intent batteryIntent = getApplicationContext().registerReceiver(null,
				new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		int rawlevel = batteryIntent.getIntExtra("level", -1);
		double scale = batteryIntent.getIntExtra("scale", -1);
		int plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED,
				-1);
		isconnected = (plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB);
		level = -1;
		if (rawlevel >= 0 && scale > 0) {
			level = (int) ((rawlevel * 100) / scale);
		}
		temperature = (float) ((float) (batteryIntent.getIntExtra(
				"temperature", 0)) / 10);
		return level;
	}

	public Bitmap createPreview(Bitmap bitmap) {

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int height, width;

		if (getResources().getDisplayMetrics().widthPixels > getResources()
				.getDisplayMetrics().heightPixels) {
			height = (int) (getResources().getDisplayMetrics().heightPixels * 0.82);
			width = (int) (getResources().getDisplayMetrics().widthPixels / 2);

			Bitmap circleBitmap = Bitmap.createBitmap(width, height,
					Config.ARGB_8888);
			Canvas c = new Canvas(circleBitmap);
			c.drawBitmap(Bitmap.createBitmap(bitmap, 0, height / 2, width,
					(int) (height)), 0, 0, null);
			return circleBitmap;
		} else {
			height = (int) (getResources().getDisplayMetrics().widthPixels * 0.70);
			width = (int) (getResources().getDisplayMetrics().widthPixels);

			Bitmap circleBitmap = Bitmap.createBitmap(width, height,
					Config.ARGB_8888);
			Canvas c = new Canvas(circleBitmap);
			c.drawBitmap(Bitmap.createBitmap(bitmap, 0, (int) (height), width,
					(int) (height)), 0, 0, null);
			return circleBitmap;
		}

		// int height = (int) (dm.widthPixels / 1.2);

	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	Bitmap ba;

	public class retBat extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			ba = Functions.battery(readbattery(), temperature, isconnected,
					getBaseContext(), wph, awID);
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			switch (wph.getWidgetTheme()) {
			case 0:
				((ImageView) findViewById(R.id.PreviewimageView3))
						.setImageBitmap(ba);
				break;
			case 1:
				((ImageView) findViewById(R.id.PreviewimageViewSecond))
						.setImageBitmap(ba);
				break;
			case 2:
				((ImageView) findViewById(R.id.PreviewimageViewThird))
						.setImageBitmap(ba);
				break;
			case 3:
				((ImageView) findViewById(R.id.PreviewimageViewFourth))
						.setImageBitmap(ba);
				break;
			case 4:
				((ImageView) findViewById(R.id.PreviewimageViewFifth))
						.setImageBitmap(ba);
				break;
			}
			super.onPostExecute(result);
		}

	}

	public void updateWidget() {
		new retBat().execute();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem m1 = menu.add("OK");
		MenuItemCompat.setShowAsAction(m1, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

		if (getResources().getDisplayMetrics().widthPixels > getResources()
				.getDisplayMetrics().heightPixels) {
			m1.setIcon(R.drawable.ok);
		}
		m1.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {

				wph.saveEverything(getBaseContext(), awID);

				Intent result = new Intent();
				result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, awID);
				setResult(RESULT_OK, result);
				int[] appWidgetIds = { awID };

				startService(new Intent(getBaseContext(), UpdateService.class)
						.putExtra(PreferenceHelper.EXTRA_WIDGET_IDS,
								appWidgetIds));
				finish();
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		switch (buttonView.getId()) {

		case R.id.cbbatterycolors:
			/*
			 * getPrefs.edit() .putBoolean(PreferenceHelper.WIDGET_DIFF_BAT,
			 * isChecked) .commit();
			 */
			wph.setWidDiffColor(isChecked);
			if (!isChecked) {
				((TextView) findViewById(R.id.tv5))
						.setText("Default Battery Color");
				((TextView) findViewById(R.id.tv5sub))
						.setText("Default Color for Battery");
				((RelativeLayout) findViewById(R.id.rlpluggedColor))
						.setVisibility(View.VISIBLE);
				((RelativeLayout) findViewById(R.id.rlbattery1Color))
						.setVisibility(View.VISIBLE);
				((RelativeLayout) findViewById(R.id.rlbattery2Color))
						.setVisibility(View.GONE);
				((RelativeLayout) findViewById(R.id.rlbattery3Color))
						.setVisibility(View.GONE);
				((RelativeLayout) findViewById(R.id.rlbattery4Color))
						.setVisibility(View.GONE);
				((RelativeLayout) findViewById(R.id.rlbattery5Color))
						.setVisibility(View.GONE);
				/*
				 * ((Divider) findViewById(R.id.dvd1)).setVisibility(View.GONE);
				 * ((Divider) findViewById(R.id.dvd2)).setVisibility(View.GONE);
				 * ((Divider) findViewById(R.id.dvd3)).setVisibility(View.GONE);
				 */
			} else {
				((TextView) findViewById(R.id.tv5)).setText("81% to 100%");
				((TextView) findViewById(R.id.tv5sub))
						.setText("Color for Battery for 81% to 100%");
				((RelativeLayout) findViewById(R.id.rlpluggedColor))
						.setVisibility(View.VISIBLE);
				((RelativeLayout) findViewById(R.id.rlbattery1Color))
						.setVisibility(View.VISIBLE);
				((RelativeLayout) findViewById(R.id.rlbattery2Color))
						.setVisibility(View.VISIBLE);
				((RelativeLayout) findViewById(R.id.rlbattery3Color))
						.setVisibility(View.VISIBLE);
				((RelativeLayout) findViewById(R.id.rlbattery4Color))
						.setVisibility(View.VISIBLE);
				((RelativeLayout) findViewById(R.id.rlbattery5Color))
						.setVisibility(View.VISIBLE);
				/*
				 * ((Divider)
				 * findViewById(R.id.dvd1)).setVisibility(View.VISIBLE);
				 * ((Divider)
				 * findViewById(R.id.dvd2)).setVisibility(View.VISIBLE);
				 * ((Divider)
				 * findViewById(R.id.dvd3)).setVisibility(View.VISIBLE);
				 */
			}
			updateWidget();
			break;

		}

	}

	@Override
	protected void onResume() {
		String file = PreferenceHelper.SETTINGS_WIDGET_FILE + awID;
		SharedPreferences getPrefs = getBaseContext().getSharedPreferences(
				file, Context.MODE_PRIVATE);
		((ImageView) findViewById(R.id.ivbattery1Color))
				.setImageBitmap(NewFunctions.createSquare(
						getPrefs.getInt("colorbat10",
								Color.argb(255, 44, 172, 218)),
						getPrefs.getInt("colorbat11",
								Color.argb(255, 24, 82, 112)),
						getPrefs.getInt("colorbat12", Color.argb(0, 0, 0, 0)),
						getPrefs.getInt("colorbat13", Color.WHITE),
						getBaseContext()));
		((ImageView) findViewById(R.id.ivbattery2Color))
				.setImageBitmap(NewFunctions.createSquare(
						getPrefs.getInt("colorbat20",
								Color.argb(255, 44, 172, 218)),
						getPrefs.getInt("colorbat21",
								Color.argb(255, 24, 82, 112)),
						getPrefs.getInt("colorbat22", Color.argb(0, 0, 0, 0)),
						getPrefs.getInt("colorbat23", Color.WHITE),
						getBaseContext()));
		((ImageView) findViewById(R.id.ivbattery3Color))
				.setImageBitmap(NewFunctions.createSquare(
						getPrefs.getInt("colorbat30",
								Color.argb(255, 44, 172, 218)),
						getPrefs.getInt("colorbat31",
								Color.argb(255, 24, 82, 112)),
						getPrefs.getInt("colorbat32", Color.argb(0, 0, 0, 0)),
						getPrefs.getInt("colorbat33", Color.WHITE),
						getBaseContext()));
		((ImageView) findViewById(R.id.ivbattery4Color))
				.setImageBitmap(NewFunctions.createSquare(
						getPrefs.getInt("colorbat40",
								Color.argb(255, 44, 172, 218)),
						getPrefs.getInt("colorbat41",
								Color.argb(255, 24, 82, 112)),
						getPrefs.getInt("colorbat42", Color.argb(0, 0, 0, 0)),
						getPrefs.getInt("colorbat43", Color.WHITE),
						getBaseContext()));
		((ImageView) findViewById(R.id.ivbattery5Color))
				.setImageBitmap(NewFunctions.createSquare(
						getPrefs.getInt("colorbat50",
								Color.argb(255, 44, 172, 218)),
						getPrefs.getInt("colorbat51",
								Color.argb(255, 24, 82, 112)),
						getPrefs.getInt("colorbat52", Color.argb(0, 0, 0, 0)),
						getPrefs.getInt("colorbat53", Color.WHITE),
						getBaseContext()));

		((ImageView) findViewById(R.id.ivpluggedColor))
				.setImageBitmap(NewFunctions.createSquare(getPrefs.getInt(
						"colorplugged0", Color.argb(255, 44, 172, 218)),
						getPrefs.getInt("colorplugged1",
								Color.argb(255, 24, 82, 112)), getPrefs.getInt(
								"colorplugged2", Color.argb(0, 0, 0, 0)),
						getPrefs.getInt("colorplugged3", Color.WHITE),
						getBaseContext()));

		updateWidget();
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		float lineb;
		if (wph.getWidgetTheme() != 4)
			lineb = wph.getLineBack();
		else
			lineb = wph.getLineBac();

		String value = "empty";
		switch (v.getId()) {
		case R.id.rlbattery1Color:
			value = "bat1";
			startActivity(new Intent(getBaseContext(), MyBatteryDialog.class)
					.putExtra("extraString", value)
					.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, awID)
					.putExtra("WidgetTheme", wph.getWidgetTheme())
					.putExtra("LineBack", lineb)
					.putExtra("LineLeft", wph.getLineLeft())
					.putExtra("FontSize", wph.getFontSize())
					.putExtra("Font", wph.getFont())
					.putExtra("Widget_text", wph.getWidgetText()));
			break;
		case R.id.rlbattery2Color:
			value = "bat2";
			startActivity(new Intent(getBaseContext(), MyBatteryDialog.class)
					.putExtra("extraString", value)
					.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, awID)
					.putExtra("WidgetTheme", wph.getWidgetTheme())
					.putExtra("LineBack", lineb)
					.putExtra("LineLeft", wph.getLineLeft())
					.putExtra("FontSize", wph.getFontSize())
					.putExtra("Font", wph.getFont())
					.putExtra("Widget_text", wph.getWidgetText()));
			break;
		case R.id.rlbattery3Color:
			value = "bat3";
			startActivity(new Intent(getBaseContext(), MyBatteryDialog.class)
					.putExtra("extraString", value)
					.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, awID)
					.putExtra("WidgetTheme", wph.getWidgetTheme())
					.putExtra("LineBack", lineb)
					.putExtra("LineLeft", wph.getLineLeft())
					.putExtra("FontSize", wph.getFontSize())
					.putExtra("Font", wph.getFont())
					.putExtra("Widget_text", wph.getWidgetText()));
			break;
		case R.id.rlbattery4Color:
			value = "bat4";
			startActivity(new Intent(getBaseContext(), MyBatteryDialog.class)
					.putExtra("extraString", value)
					.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, awID)
					.putExtra("WidgetTheme", wph.getWidgetTheme())
					.putExtra("LineBack", lineb)
					.putExtra("LineLeft", wph.getLineLeft())
					.putExtra("FontSize", wph.getFontSize())
					.putExtra("Font", wph.getFont())
					.putExtra("Widget_text", wph.getWidgetText()));
			break;
		case R.id.rlbattery5Color:
			value = "bat5";
			startActivity(new Intent(getBaseContext(), MyBatteryDialog.class)
					.putExtra("extraString", value)
					.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, awID)
					.putExtra("WidgetTheme", wph.getWidgetTheme())
					.putExtra("LineBack", lineb)
					.putExtra("LineLeft", wph.getLineLeft())
					.putExtra("FontSize", wph.getFontSize())
					.putExtra("Font", wph.getFont())
					.putExtra("Widget_text", wph.getWidgetText()));
			break;

		case R.id.rlpluggedColor:
			value = "plugged";
			startActivity(new Intent(getBaseContext(), MyBatteryDialog.class)
					.putExtra("extraString", value)
					.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, awID)
					.putExtra("WidgetTheme", wph.getWidgetTheme())
					.putExtra("LineBack", lineb)
					.putExtra("LineLeft", wph.getLineLeft())
					.putExtra("FontSize", wph.getFontSize())
					.putExtra("Font", wph.getFont())
					.putExtra("Widget_text", wph.getWidgetText()));
			break;

		}

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.sbbatsize:
			/*
			 * getPrefs.edit() .putFloat(PreferenceHelper.WIDGET_FONT_SIZE,
			 * progress) .commit();
			 */
			wph.setFontSize(progress);
			updateWidget();
			break;
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

}
