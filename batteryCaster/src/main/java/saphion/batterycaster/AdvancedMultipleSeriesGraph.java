package saphion.batterycaster;

import java.util.ArrayList;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import saphion.batterylib.HistoryItem;
import saphion.services.ForegroundService;
import saphion.utils.ActivityFuncs;
import saphion.utils.PreferenceHelper;
import saphion.utils.SerialPreference;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;

import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AdvancedMultipleSeriesGraph extends ActionBarActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.batteryfraglayout);

		Intent intent = new Intent(ForegroundService.ACTION_FOREGROUND);
		intent.setClass(this, ForegroundService.class);
		startService(intent);

	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(batteryReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryReceiver, mIntentFilter);

		new LoadStatData().execute(this);

	}

	private boolean isconnected = false;
	private int level = 0;
	BroadcastReceiver batteryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			// String action = intent.getAction();

			try {

				int rawlevel = intent.getIntExtra("level", -1);
				double scale = intent.getIntExtra("scale", -1);
				int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,
						-1);
				isconnected = (plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB);

				level = -1;
				if (rawlevel >= 0 && scale > 0) {
					level = (int) ((rawlevel * 100) / scale);
					Log.d("stencil", "rawLevel: " + rawlevel);
					Log.d("stencil", "scale: " + scale);
				}

				((ImageView) findViewById(R.id.ivbatmain))
						.setImageBitmap(ActivityFuncs.battery(level,
								getBaseContext(),100));
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
	};

	/*
	 * private void doRefresh() { new LoadStatData().execute(this); // Display
	 * the reference of the stat
	 * 
	 * // this.setListViewAdapter();
	 * BatteryStatsProxy.getInstance(this).invalidate(); //
	 * m_listViewAdapter.notifyDataSetChanged();
	 * 
	 * }
	 */

	double lastentry;
	ArrayList<Double> vals;
	ArrayList<Double> dates;
	ArrayList<HistoryItem> mList;

	// double firstentry;

	private XYMultipleSeriesDataset getDateDataset() {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		TimeSeries series = new TimeSeries("Battery History2");
		TimeSeries series2 = new TimeSeries("Battery History1");

		for (int i = 0; i < vals.size(); i++) {
			// Log.d("Stencil", "x: " + new Date((long) series.getX(i)));
			// Log.d("Stencil", "y: " + series.getY(i));
			series2.add(new Date((long) (double) dates.get(i)), vals.get(i));
		}

		Log.d("Stencil", "series2 size: " + series2.getItemCount());
		dataset.addSeries(series2);

		if (isconnected) {
			series = new TimeSeries("Battery History");

			series.add(
					new Date((long) series2.getX(series2.getItemCount() - 1)),
					series2.getY(series2.getItemCount() - 1));

			lastentry = series2.getX(series2.getItemCount() - 1)
					+ (long) (1000 * (81) * (100 - series2.getY(series2
							.getItemCount() - 1)));

			series.add(new Date((long) lastentry), 100);
			dataset.addSeries(series);

		} else {
			series = new TimeSeries("Battery History");
			series.add(new Date((long) series2.getMaxX()),
					series2.getY(series2.getItemCount() - 1));

			lastentry = series2.getX(series2.getItemCount() - 1)
					+ (long) (1000 * (792) * (100 - series2.getY(series2
							.getItemCount() - 1)));

			series.add(new Date((long) lastentry), 0);
			dataset.addSeries(series);
		}

		/*
		 * series = new TimeSeries("Last"); series.add(new Date((long)
		 * lastentry), 0); series.add(new Date((long) lastentry), 100);
		 * 
		 * dataset.addSeries(series);
		 */

		return dataset;
	}

	public void plotGraph() {
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.graph2);
			mChartView = ChartFactory.getTimeChartView(getBaseContext(),
					getDateDataset(), getRenderer(), "EEE, h:mm a");
			layout.addView(mChartView);
		} else {
			mChartView.repaint();
		}
	}

	private XYMultipleSeriesRenderer getRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(getDimension(10.67f));
		renderer.setChartTitleTextSize(getDimension(13.34f));
		// renderer.setLabelsTextSize(15);
		// renderer.setLegendTextSize(0);
		renderer.setPointSize(0f);
		// renderer.setMargins(new int[] { 50, 100, 50, 50 });
		// renderer.setMargins(new int[] { 50, 90, 0, 50 });
		renderer.setMargins(new int[] { (int) getDimension(13.34f),
				(int) getDimension(33.34f), (int) getDimension(10f),
				(int) getDimension(6.67f) });
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(0xff33b5e5);

		renderer.setBackgroundColor(0x00000000);
		renderer.setApplyBackgroundColor(true);
		r.setFillBelowLine(true);
		renderer.setYLabelsAlign(Align.RIGHT);

		// renderer.sety

		r.setFillBelowLineColor(0xff21637c);
		r.setLineWidth(getDimension(2.67f));
		// renderer.setMarginsColor(Color.WHITE);
		// renderer.addXTextLabel(lastentry,
		// DateUtils.format((long) lastentry, "EEE, h:mm a"));
		renderer.setMarginsColor(0xff111111);
		renderer.setLabelsTextSize(getDimension(10f));

		renderer.setXLabels(4);
		renderer.setYLabels(11);
		// renderer.setMarginsColor(Color.RED);
		// renderer.setGridColor(Color.DKGRAY);
		// r.setFillPoints(true);
		renderer.addSeriesRenderer(r);
		renderer.setShowGrid(true);
		renderer.setShowLegend(false);

		// renderer.setBarSpacing(2);

		r = new XYSeriesRenderer();
		r.setPointStyle(PointStyle.POINT);
		r.setLineWidth(4);
		if (isconnected) {

			r.setColor(0xff163845);

			r.setFillBelowLine(true);
			r.setFillBelowLineColor(0x71163845);

			// r.setFillPoints(true);
		} else {
			r.setColor(Color.RED);
			r.setFillBelowLine(true);
			r.setFillBelowLineColor(0x71ff0000);
		}

		// renderer.seti
		renderer.setPanEnabled(true, false);
		renderer.setShowAxes(true);
		renderer.setAntialiasing(true);
		renderer.setZoomEnabled(true, false);
		renderer.setYAxisMax(100);
		renderer.setYAxisMin(0);

		renderer.addSeriesRenderer(r);

		/*
		 * r = new XYSeriesRenderer(); r.setPointStyle(PointStyle.POINT);
		 * r.setLineWidth(6); r.setColor(Color.WHITE);
		 * r.setDisplayChartValues(true); r.setFillBelowLine(true);
		 * r.setFillBelowLineColor(0xffffffff); renderer.addSeriesRenderer(r);
		 */

		renderer.setXAxisMax(lastentry + 1800000);

		for (int i = 0; i <= 100; i = i + 10)
			renderer.addYTextLabel(i, i + " %");

		renderer.setAxesColor(Color.WHITE);
		renderer.setLabelsColor(Color.LTGRAY);
		return renderer;
	}

	/**
	 * Chart Variables
	 */
	private GraphicalView mChartView;

	// private XYSeries mCurrentSeries;

	// private XYSeriesRenderer mCurrentRenderer;

	// @see
	// http://code.google.com/p/makemachine/source/browse/trunk/android/examples/async_task/src/makemachine/android/examples/async/AsyncTaskExample.java
	// for more details
	private class LoadStatData extends AsyncTask<Context, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Context... params) {
			// super.doInBackground(params);

			vals = new ArrayList<Double>();
			dates = new ArrayList<Double>();

			if (getSharedPreferences("saphion.batterycaster_preferences",
					Context.MODE_MULTI_PROCESS)
					.getBoolean(PreferenceHelper.FIRST_TIME, true)) {
				getSharedPreferences("saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS)
						.edit().putBoolean(PreferenceHelper.FIRST_TIME, false)
						.commit();
				mList = BatteryStats.getHistList(getBaseContext());

				TimeSeries series = new TimeSeries("Battery History2");
				TimeSeries exceptionSeries = new TimeSeries("Battery HistoryE");

				for (int i = 0; i < mList.size(); i++) {
					if (i != 0) {

						if (mList.get(i - 1).getBatteryLevelInt() != mList.get(
								i).getBatteryLevelInt()) {

							Log.d("Stencil", "Entry number: " + i + " is time "
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

				Log.d("stencil", z + "");

				for (int i = z; i < series.getItemCount(); i++) {
					Log.d("Stencil", "x: " + new Date((long) series.getX(i)));
					Log.d("Stencil", "y: " + series.getY(i));
					// series2.add(new Date((long) series.getX(i)),
					// series.getY(i));
					vals.add(series.getY(i));
					dates.add(series.getX(i));
				}

				SerialPreference.savePrefs(getBaseContext(), dates,
						PreferenceHelper.BAT_TIME);
				SerialPreference.savePrefs(getBaseContext(), vals,
						PreferenceHelper.BAT_VALS);
			} else {
				vals = SerialPreference.retPrefs(getBaseContext(),
						PreferenceHelper.BAT_VALS);
				dates = SerialPreference.retPrefs(getBaseContext(),
						PreferenceHelper.BAT_TIME);
			}

			// StatsActivity.this.setListAdapter(m_listViewAdapter);
			// getStatList();
			return false;
		}

		@Override
		protected void onPostExecute(Boolean o) {
			super.onPostExecute(o);
			// update hourglass
			/*
			 * if (m_progressDialog != null) { m_progressDialog.hide();
			 * m_progressDialog = null; }
			 */

			try {
				getSupportActionBar().show();
			} catch (Exception ex) {
				Toast.makeText(getBaseContext(),
						"Cannot Show actionbar because of" + ex.toString(),
						Toast.LENGTH_LONG).show();
				ex.printStackTrace();
			}

			((RelativeLayout) findViewById(R.id.rlsplash))
					.setVisibility(View.GONE);

			plotGraph();
		}

		@Override
		protected void onPreExecute() {
			// update hourglass
			// @todo this code is only there because onItemSelected is called
			// twice
			/*
			 * if (m_progressDialog == null) { m_progressDialog = new
			 * ProgressDialog( AdvancedMultipleSeriesGraph.this);
			 * m_progressDialog.setMessage("Computing...");
			 * m_progressDialog.setIndeterminate(true);
			 * m_progressDialog.setCancelable(false); m_progressDialog.show(); }
			 */

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
					Context.MODE_MULTI_PROCESS)
					.getBoolean(PreferenceHelper.FIRST_TIME, true)) {
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

		}
	}

	/*
	 * private void callhandler() { handler.removeCallbacks(drawRunner);
	 * handler.post(drawRunner);
	 * 
	 * }
	 * 
	 * private final Handler handler = new Handler(); private final Runnable
	 * drawRunner = new Runnable() {
	 * 
	 * 
	 * public void run() {
	 * 
	 * ((ImageView) findViewById(R.id.ivsplashedit)).setRotation(1);
	 * callhandler();
	 * 
	 * }
	 * 
	 * };
	 * 
	 * public void removecallBacks() { try {
	 * handler.removeCallbacks(drawRunner); } catch (Exception ex) {
	 * ex.printStackTrace(); } }
	 */

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

	public float getDimension(float i) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		float ScreenDensity = dm.density;
		return i * ScreenDensity;
	}

}
