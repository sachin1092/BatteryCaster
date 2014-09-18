package saphion.help;

import saphion.batterycaster.R;
import saphion.fragment.alarm.alert.Intents;
import saphion.utils.PreferenceHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitlePageIndicator.IndicatorStyle;

public class HelpActivity extends ActionBarActivity {

	@Override
	protected void onPause() {
		unregisterReceiver(br);
		super.onPause();
	}

	@Override
	protected void onResume() {
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intents.FINISH_INTENT);
		registerReceiver(br, filter);
		super.onResume();
	}

	BroadcastReceiver br = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			finish();

		}

	};

	HelpAdapter mAdapter;
	ViewPager mPager;
	PageIndicator mIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().hide();
		setContentView(R.layout.help);
		
		getSharedPreferences("saphion.batterycaster_preferences",
				Context.MODE_MULTI_PROCESS).edit()
				.putBoolean(PreferenceHelper.FIRST_TIME_HELP, false)
				.commit();

		mAdapter = new HelpAdapter(getSupportFragmentManager());

		mPager = (ViewPager) findViewById(R.id.pagerhelp);
		mPager.setAdapter(mAdapter);

		TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicatorhelp);
		mIndicator = indicator;
		indicator.setViewPager(mPager);

		final float density = getResources().getDisplayMetrics().density;
		indicator.setBackgroundColor(0x1833B5E5);
		indicator.setFooterColor(0xFF33B5E5);
		indicator.setFooterLineHeight(1 * density); // 1dp
		indicator.setFooterIndicatorHeight(3 * density); // 3dp
		indicator.setFooterIndicatorStyle(IndicatorStyle.Underline);
		indicator.setTextColor(0xAAFFFFFF);
		indicator.setSelectedColor(0xFFFFFFFF);
		indicator.setSelectedBold(true);
		Typeface tf = Typeface.createFromAsset(getBaseContext().getAssets(),
				"cnlbold.ttf");
		indicator.setTypeface(tf);
	}
}