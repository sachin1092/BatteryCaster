package saphion.fragments;

import java.util.ArrayList;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;

import saphion.batterycaster.R;
import saphion.logger.Log;
import saphion.utils.Constants;
import saphion.utils.Functions;
import saphion.utils.NewFunctions;
import saphion.utils.PreferenceHelper;
import saphion.utils.SerialPreference;
import saphion.utils.TimeFuncs;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

@SuppressLint("InlinedApi")
public final class GraphFragment extends Fragment {
	private static final String KEY_CONTENT = "TestFragment:Content";
	public Object prevConnected = null;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		/*
		 * MenuItem record = menu.add("Record");
		 * record.setIcon(R.drawable.record);
		 * record.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
		 */
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// setHasOptionsMenu(true);
		super.onActivityCreated(savedInstanceState);
	}

	public static GraphFragment newInstance(String content) {
		GraphFragment fragment = new GraphFragment();

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

	double lastentry;
	ArrayList<Double> vals;
	ArrayList<Double> dates;

	// ArrayList<HistoryItem> mList;

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
		getBaseContext().registerReceiver(batteryReceiver, mIntentFilter);

		(new LoadStatData()).execute();
		/*
		 * Intent intent = new Intent(ForegroundService.ACTION_FOREGROUND);
		 * intent.setClass(k.getContext(), ForegroundService.class);
		 * k.getContext().startService(intent);
		 */

		super.onResume();
	}

	private boolean isconnected = false;
	private int level = 0;
	BroadcastReceiver batteryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			try {

				//Log.Toast(getBaseContext(), "Recieved", Toast.LENGTH_LONG);

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

				if (getActivity().getSharedPreferences("saphion.batterycaster_preferences",
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

				if (vals.size() > 0) {
					if (vals.get(vals.size() - 1) != level) {
						vals.add((Double) (double) level);
						dates.add(((Double) (double) (System
								.currentTimeMillis())));
						plotGraph(true);
					}
				}

				if (prevConnected == null) {
					prevConnected = isconnected;
				} else {

					if (isconnected != (Boolean) prevConnected) {
						plotGraph(true);
						prevConnected = isconnected;
						/*
						 * PreferenceManager
						 * .getDefaultSharedPreferences(getBaseContext())
						 * .edit() .putString(PreferenceHelper.LAST_CHARGED,
						 * TimeFuncs.getCurrentTimeStamp()) .commit();
						 */
					}
				}

				/*
				 * ((ImageView) k.findViewById(R.id.ivbatmain))
				 * .setImageBitmap(ActivityFuncs.newbattery(level,
				 * getBaseContext(),
				 * getBaseContext().getResources().getDisplayMetrics
				 * ().widthPixels));
				 */
				/*
				 * ActivityFuncs.createBatFrag( getBaseContext(), level,
				 * temperature, isconnected)
				 */
				drawTexts(level, isconnected);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
	};

	private void disableOnePercentHack(String reason) {

		getActivity()
				.getSharedPreferences(
						"saphion.batterycaster_preferences" + "_new",
						Context.MODE_MULTI_PROCESS).edit()
				.putBoolean(PreferenceHelper.KEY_ONE_PERCENT_HACK, false)
				.commit();

		saphion.logger.Log.d("Disabling one percent hack due to: " + reason);
	}

	public Context getBaseContext() {
		return k.getContext();
	}

	View k;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		k = (LinearLayout) inflater.inflate(R.layout.graphfraglayout,
				container, false);

		AsyncTask<Void, Void, Typeface> setFont = new AsyncTask<Void, Void, Typeface>() {

			@Override
			protected Typeface doInBackground(Void... params) {

				return Typeface.createFromAsset(getActivity().getAssets(),
						Constants.FONT_USING);
			}

			@Override
			protected void onPostExecute(Typeface result) {
				Functions.settypeface(
						((LinearLayout) k.findViewById(R.id.llgraphfrag)),
						(result));
				Functions.settypeface(
						((ScrollView) k.findViewById(R.id.svstats)), (result));
				super.onPostExecute(result);
			}

		};

		setFont.execute();

		/*
		 * ((ImageView) k.findViewById(R.id.ivbatmain)) .setOnTouchListener(new
		 * OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) { switch
		 * (event.getAction()) { case MotionEvent.ACTION_DOWN: case
		 * MotionEvent.ACTION_MOVE:
		 * 
		 * Intent i; i = new Intent("android.intent.action.MAIN");
		 * i.putExtra("msg", 1); k.getContext().sendBroadcast(i); break; }
		 * return false; } });
		 */

		readbattery();

		return k;
	}

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

		if (getActivity().getSharedPreferences(
				"saphion.batterycaster_preferences" + "_new",
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
		/*
		 * try { ((ImageView) k.findViewById(R.id.ivbatmain))
		 * .setImageBitmap(ActivityFuncs.createBatFrag( getBaseContext(), level,
		 * temperature, isconnected)); ((ImageView)
		 * k.findViewById(R.id.ivbatmain))
		 * .setImageBitmap(ActivityFuncs.newbattery(level, getBaseContext(),
		 * getBaseContext().getResources().getDisplayMetrics().widthPixels)); }
		 * catch (Exception ex) { Log.d(ex.toString()); }
		 */

		drawTexts(level, isconnected);

		return level;
	}

	private void drawTexts(int mLevel, boolean isCon) {
		String speedTitle;
		if (isCon) {

			speedTitle = "Charging Speed("
					+ getResources().getString(R.string.delta) + "1%)";
			String mainText = getActivity().getSharedPreferences("saphion.batterycaster_preferences",
					Context.MODE_MULTI_PROCESS).getString(PreferenceHelper.LAST_CHARGED,
					TimeFuncs.getCurrentTimeStamp());
			String time = TimeFuncs.convtohournminnday(TimeFuncs.newDiff(
					TimeFuncs.GetItemDate(mainText),
					TimeFuncs.GetItemDate(TimeFuncs.getCurrentTimeStamp())));
			if (!time.equals("0 Minute(s)"))
				mainText = time + " ago";
			else
				mainText = "right now";

			((TextView) k.findViewById(R.id.tvStatplugTitle))
					.setText("Plugged");
			((TextView) k.findViewById(R.id.tvStatplugVal)).setText(mainText);
			((TextView) k.findViewById(R.id.tvStatDisSpeedTitle))
					.setText(speedTitle);
			((TextView) k.findViewById(R.id.tvStatDisSpeedVal))
					.setText(TimeFuncs.convtohournminndaynsec(getActivity().getSharedPreferences("saphion.batterycaster_preferences",
							Context.MODE_MULTI_PROCESS)
							.getLong(PreferenceHelper.BAT_CHARGE, 81)));

		} else {
			speedTitle = "Discharging Speed "
					+ getResources().getString(R.string.delta) + "1%";

			String mainText = getActivity().getSharedPreferences("saphion.batterycaster_preferences",
					Context.MODE_MULTI_PROCESS).getString(PreferenceHelper.LAST_CHARGED,
					TimeFuncs.getCurrentTimeStamp());

			String time = TimeFuncs.convtohournminnday(TimeFuncs.newDiff(
					TimeFuncs.GetItemDate(mainText),
					TimeFuncs.GetItemDate(TimeFuncs.getCurrentTimeStamp())));

			if (!time.equals("0 Minute(s)"))
				mainText = time + " ago";
			else
				mainText = "right now";
			((TextView) k.findViewById(R.id.tvStatplugTitle))
					.setText("Unplugged");
			((TextView) k.findViewById(R.id.tvStatplugVal)).setText(mainText);
			((TextView) k.findViewById(R.id.tvStatDisSpeedTitle))
					.setText(speedTitle);
			((TextView) k.findViewById(R.id.tvStatDisSpeedVal))
					.setText(TimeFuncs.convtohournminndaynsec(getActivity().getSharedPreferences("saphion.batterycaster_preferences",
							Context.MODE_MULTI_PROCESS)
							.getLong(PreferenceHelper.BAT_DISCHARGE, 792)));
		}

		k.getContext();
		SharedPreferences mSharedPrefs = getActivity()
				.getSharedPreferences("saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS);
		long disChargingAvg = mSharedPrefs.getLong(
				PreferenceHelper.STAT_DISCHARGING_AVGTIME, 612) * 100;

		((TextView) (k.findViewById(R.id.tvStatsDisAverageVal)))
				.setText(TimeFuncs.convtohournminnday(disChargingAvg));

		long chargingAvg = mSharedPrefs.getLong(
				PreferenceHelper.STAT_CHARGING_AVGTIME, 90) * 100;

		((TextView) (k.findViewById(R.id.tvStatsAverageChargeVal)))
				.setText(TimeFuncs.convtohournminnday(chargingAvg));

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}

	private XYMultipleSeriesDataset getDateDataset() {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		TimeSeries series = new TimeSeries("Battery History2");
		TimeSeries series2 = new TimeSeries("Battery History1");

		for (int i = 0; i < vals.size(); i++) {
			// Log.d("Stencil", "x: " + new Date((long) series.getX(i)));
			// Log.d("Stencil", "y: " + series.getY(i));
			series2.add(new Date((long) (double) dates.get(i)), vals.get(i));
		}

		Log.d("series2 size: " + series2.getItemCount());
		dataset.addSeries(series2);

		if (isconnected) {
			series = new TimeSeries("Battery History");

			series.add(
					new Date((long) series2.getX(series2.getItemCount() - 1)),
					series2.getY(series2.getItemCount() - 1));

			lastentry = series2.getX(series2.getItemCount() - 1)
					+ (long) (1000 * ((getActivity().getSharedPreferences("saphion.batterycaster_preferences",
							Context.MODE_MULTI_PROCESS)
							.getLong(PreferenceHelper.BAT_CHARGE, 81)) * (100 - series2
							.getY(series2.getItemCount() - 1))));

			series.add(new Date((long) lastentry), 100);
			dataset.addSeries(series);

		} else {
			series = new TimeSeries("Battery History");
			series.add(new Date((long) series2.getMaxX()),
					series2.getY(series2.getItemCount() - 1));

			lastentry = series2.getX(series2.getItemCount() - 1)
					+ (long) (1000 * (getActivity().getSharedPreferences("saphion.batterycaster_preferences",
							Context.MODE_MULTI_PROCESS)
							.getLong(PreferenceHelper.BAT_DISCHARGE, 792)) * (series2
							.getY(series2.getItemCount() - 1)));

			series.add(new Date((long) lastentry), 0);
			dataset.addSeries(series);
		}

		return dataset;
	}

	public void plotGraph(boolean bool) {
		// final ScrollView sv = (ScrollView)k.findViewById(R.id.svbatfarg);
		//Log.Toast(getBaseContext(), "plotting graph: " + bool,
			//	Toast.LENGTH_LONG);
		if (mChartView == null || bool) {
			final LinearLayout layout = (LinearLayout) k
					.findViewById(R.id.graph2);

			mChartView = ChartFactory.getTimeChartView(getBaseContext(),
					getDateDataset(), getRenderer(), "EEE, h:mm a");

			try {
				layout.removeAllViews();
			} catch (Exception ex) {
				Log.d(ex.toString());
			}
			layout.addView(mChartView);

		} else {
			mChartView.repaint();
		}

		LayoutParams chartParams = mChartView.getLayoutParams();

		LayoutParams params = ((LinearLayout) k.findViewById(R.id.graph2))
				.getLayoutParams();

		if (getResources().getDisplayMetrics().heightPixels > getResources()
				.getDisplayMetrics().widthPixels) {

			chartParams.height = (int) (getResources().getDisplayMetrics().heightPixels / 3.3);

			chartParams.width = getResources().getDisplayMetrics().widthPixels
					- NewFunctions.ReturnHeight(20, getBaseContext());

			params.height = (int) (getResources().getDisplayMetrics().heightPixels / 3.3);// 3.4
																							// perfect
			params.width = getResources().getDisplayMetrics().widthPixels
					- NewFunctions.ReturnHeight(20, getBaseContext());
		} else {
			chartParams.height = (int) (getResources().getDisplayMetrics().heightPixels / 1.8);

			chartParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.4);

			params.height = (int) (getResources().getDisplayMetrics().heightPixels / 1.8);// 3.4
																							// perfect//1.4
			params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.4);
		}

		mChartView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				Intent i;
				i = new Intent("android.intent.action.MAIN");
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					i.putExtra("msg", 0);
					k.getContext().sendBroadcast(i);
				} else if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					i.putExtra("msg", 1);
					k.getContext().sendBroadcast(i);
				}
				return false;
			}
		});

	}

	private XYMultipleSeriesRenderer getRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(getDimension(10.67f));
		renderer.setChartTitleTextSize(getDimension(13.34f));
		renderer.setPointSize(0f);
		renderer.setMargins(new int[] { (int) getDimension(13.34f),
				(int) getDimension(33.34f), (int) getDimension(10f),
				(int) getDimension(6.67f) });
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(0xff1e8bd4);// 33b5e5

		renderer.setBackgroundColor(0x00000000);
		// renderer.setApplyBackgroundColor(true);
		// r.setFillBelowLine(true);
		FillOutsideLine fol = new FillOutsideLine(
				XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
		fol.setColor(0xa01e8bd4);
		r.addFillOutsideLine(fol);
		renderer.setYLabelsAlign(Align.RIGHT);

		// r.setFillBelowLineColor(0xff21637c);

		r.setLineWidth(getDimension(2.67f));
		renderer.setMarginsColor(0x00111111);
		renderer.setLabelsTextSize(getDimension(10f));
		/*
		 * if (getResources().getConfiguration().orientation ==
		 * Configuration.ORIENTATION_LANDSCAPE) { renderer.setXLabels(5); } else
		 * {
		 */
		renderer.setXLabels(5);
		// }
		renderer.setYLabels(11);
		renderer.addSeriesRenderer(r);
		renderer.setShowGrid(true);
		renderer.setShowLegend(false);
		renderer.setInScroll(true);

		r = new XYSeriesRenderer();
		r.setPointStyle(PointStyle.POINT);
		r.setLineWidth(4);

		if (isconnected) {

			r.setColor(0xff17699f);
			FillOutsideLine fol1 = new FillOutsideLine(
					XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
			fol1.setColor(0xa017699f);
			r.addFillOutsideLine(fol1);

			/*
			 * r.setFillBelowLine(true); r.setFillBelowLineColor(0x71163845);
			 */

		} else {
			r.setColor(0xffff4444);
			FillOutsideLine fol2 = new FillOutsideLine(
					XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
			fol2.setColor(0xa0ff4444);
			r.addFillOutsideLine(fol2);
			/*
			 * r.setFillBelowLine(true); r.setFillBelowLineColor(0x71ff0000);
			 */
		}

		renderer.setPanEnabled(true, false);
		renderer.setShowAxes(true);
		renderer.setAntialiasing(true);
		renderer.setZoomEnabled(true, false);
		renderer.setYAxisMax(100);
		renderer.setYAxisMin(0);
		renderer.setTextTypeface(Typeface.createFromAsset(GraphFragment.this
						.getActivity().getAssets(), Constants.FONT_ROBOTO_COND));

		renderer.addSeriesRenderer(r);

		renderer.setXAxisMax(lastentry + 1800000);

		for (int i = 0; i <= 100; i = i + 10)
			renderer.addYTextLabel(i, i + " %  ");

		renderer.setAxesColor(Color.WHITE);
		renderer.setLabelsColor(Color.LTGRAY);
		return renderer;
	}

	/**
	 * Chart Variables
	 */
	private GraphicalView mChartView;

	private class LoadStatData extends AsyncTask<Context, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Context... params) {

			vals = new ArrayList<Double>();
			dates = new ArrayList<Double>();

			vals = SerialPreference.retPrefs(getBaseContext(),
					PreferenceHelper.BAT_VALS);
			dates = SerialPreference.retPrefs(getBaseContext(),
					PreferenceHelper.BAT_TIME);

			if (vals.size() <= 0) {
				vals.add((Double) (double) level);
				dates.add(((Double) (double) (System.currentTimeMillis())));
			}

			return false;
		}

		@Override
		protected void onPostExecute(Boolean o) {
			super.onPostExecute(o);
			((LinearLayout) k.findViewById(R.id.llgraphfrag))
					.setVisibility(View.VISIBLE);
			// ((ProgressBar) k.findViewById(R.id.pbbatpage))
			// .setVisibility(View.GONE);
			//Log.Toast(getActivity(), "vals: " + vals.size() + " dates: "
				//	+ dates.size(), Toast.LENGTH_LONG);
			try {
				plotGraph(true);
			} catch (Exception ex) {
				Log.d(ex.toString());
			}

			/*
			 * Intent intent = new Intent(ForegroundService.ACTION_FOREGROUND);
			 * intent.setClass(k.getContext(), ForegroundService.class);
			 * k.getContext().startService(intent);
			 */
		}

		@Override
		protected void onPreExecute() {
			((LinearLayout) k.findViewById(R.id.llgraphfrag))
					.setVisibility(View.GONE);
			// ((ProgressBar) k.findViewById(R.id.pbbatpage))
			// .setVisibility(View.VISIBLE);
			super.onPreExecute();
		}
	}

	public float getDimension(float i) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		float ScreenDensity = dm.density;
		return i * ScreenDensity;
	}

}
