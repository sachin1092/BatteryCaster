package saphion.batterycaster;

import saphion.utils.Constants;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class AboutClass extends Activity {

	@Override
	public void onBackPressed() {
		/*startActivity(new Intent(AboutClass.this, AllCities.class));*/
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		//getSupportActionBar().hide();

		setContentView(R.layout.about);

		super.onCreate(savedInstanceState);
		ImageView iv = (ImageView) findViewById(R.id.ivAboutRate);
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				launchMarket(getBaseContext(), getPackageName());

			}
		});
		Typeface tf = Typeface
				.createFromAsset(getAssets(), Constants.FONT_USING);
		//TextView tv1 = (TextView) findViewById(R.id.tvAbout1);
		((TextView) findViewById(R.id.tvAboutTagLine)).setTypeface(tf);
		//tv1.setTypeface(tf);
		TextView tv2 = (TextView) findViewById(R.id.tvAbout2);
		TextView tv5 = (TextView) findViewById(R.id.tvAboutName);
		tv2.setTypeface(tf);
		tv5.setTypeface(tf);

		//tv1.setTextColor(Color.WHITE);
		tv2.setTextColor(Color.WHITE);
		tv5.setTextColor(Color.WHITE);
		((LinearLayout) findViewById(R.id.llabout))
				.setBackgroundColor(Color.BLACK);
		((ScrollView) findViewById(R.id.svAbout))
				.setBackgroundColor(Color.BLACK);
		((View) findViewById(R.id.vAbout)).setBackgroundColor(Color.WHITE);
		setlinked((TextView) findViewById(R.id.tvAboutSendFeedback),
				"Send Feedback");
		((TextView) findViewById(R.id.tvAboutSendFeedback))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						sendMail("Feedback - Battery Caster");
					}
				});

		((TextView) findViewById(R.id.tvAboutReportProblem))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						sendMail("Reporting bug - Battery Caster");
					}
				});
		((TextView) findViewById(R.id.tvAboutSendFeedback)).setTypeface(tf);
		setlinked((TextView) findViewById(R.id.tvAboutReportProblem),
				"Report a problem");
		((TextView) findViewById(R.id.tvAboutReportProblem)).setTypeface(tf);
		((TextView) findViewById(R.id.tvAboutCurVersion)).setTypeface(tf);
		((TextView) findViewById(R.id.tvAboutTellus)).setTypeface(tf);
		((TextView) findViewById(R.id.tvAboutLetusKnow)).setTypeface(tf);
		((TextView) findViewById(R.id.tvAboutCurVersion)).setText("Version "
				+ getVersion());

		setlinked((TextView) findViewById(R.id.tvAboutchangelog), "ChangeLog");
		((TextView) findViewById(R.id.tvAboutchangelog)).setTypeface(tf);
		((TextView) findViewById(R.id.tvAboutchangelog))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ChangeLog cl = new ChangeLog(AboutClass.this);

						cl.getFullLogDialog().show();

					}
				});

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

	private String getVersion() {
		PackageManager manager = getPackageManager();
		PackageInfo info;
		try {
			info = manager.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "0.1";

	}

	private void setlinked(TextView textView, String sContent) {
		SpannableString content = new SpannableString(sContent);
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		textView.setText(content);
	}

	@Override
	protected void onResume() {
		if ((PreferenceManager.getDefaultSharedPreferences(getBaseContext())
				.getBoolean("fullscreen", false))) {
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
		super.onResume();
	}
	
	public static void launchMarket(Context context, String packageName) {
		Uri uri = Uri.parse("market://details?id=" + packageName);
		Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
		myAppLinkToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		try {
			context.startActivity(myAppLinkToMarket);
		} catch (ActivityNotFoundException e) {
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse("http://play.google.com/store/apps/details?id="
							+ packageName))
					.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}
	}

}
