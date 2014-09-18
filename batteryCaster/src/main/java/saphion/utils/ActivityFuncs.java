package saphion.utils;

import saphion.batterycaster.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.util.DisplayMetrics;

public class ActivityFuncs {

	public static Bitmap CreateImageBitmap(Context mContext, int mLevel,
			String mVoltage, String temperature, String mTechnology,
			String mStatus, String mHealth, String mLastCharged,
			boolean isConnected) {

		int height;
		int width;
		if (mContext.getResources().getDisplayMetrics().widthPixels > mContext
				.getResources().getDisplayMetrics().heightPixels) {
			height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.82) / 2;
			width = (int) (mContext.getResources().getDisplayMetrics().widthPixels / 2);
		} else {
			height = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.70);
			width = (int) (mContext.getResources().getDisplayMetrics().widthPixels / 2);
		}

		// Bitmap batBit = battery(mLevel, mContext);

		Bitmap circleBitmap = Bitmap.createBitmap((int) (width),
				(int) (height), Config.ARGB_8888);

		Canvas c = new Canvas(circleBitmap);

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Align.LEFT);
		paint.setTextSize((float) (circleBitmap.getWidth() * 0.20));

		String esti = getEsti(mLevel, mContext, isConnected);

		c.drawText(esti, (float) (circleBitmap.getWidth() * 0.20),
				(float) (circleBitmap.getHeight() * 0.20), paint);

		return circleBitmap;
	}

	private static String getEsti(int level, Context mContext,
			boolean isconnected) {

		String prevTime = mContext
				.getSharedPreferences("saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS).getString(
						PreferenceHelper.PREV_BAT_TIME,
						TimeFuncs.getCurrentTimeStamp());

		long diff = TimeFuncs.newDiff(TimeFuncs.GetItemDate(prevTime),
				TimeFuncs.GetItemDate(TimeFuncs.getCurrentTimeStamp()));

		String subtext;
		if (level < mContext
				.getSharedPreferences("saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS).getInt(
						PreferenceHelper.PREV_BAT_LEVEL, level)) {

			subtext = "Empty in " + TimeFuncs.convtohournmin(diff * level);

			/*
			 * PreferenceManager.getDefaultSharedPreferences(mContext).edit()
			 * .putLong(PreferenceHelper.BAT_DISCHARGE, diff).commit();
			 */
			// Log.d("stencil", "Discharging with " + diff);

		} else {
			if (level > mContext.getSharedPreferences(
					"saphion.batterycaster_preferences",
					Context.MODE_MULTI_PROCESS).getInt(
					PreferenceHelper.PREV_BAT_LEVEL, level)) {
				if (level != 100
						&& TimeFuncs.convtohournmin(diff * (100 - level))
								.equalsIgnoreCase("0 h " + "0 m")) {
					subtext = "E: Full Charge in "
							+ TimeFuncs
									.convtohournmin((long) (81 * (100 - level)));
				} else {
					subtext = "Full Charge in "
							+ TimeFuncs.convtohournmin(diff * (100 - level));
					/*
					 * PreferenceManager.getDefaultSharedPreferences(mContext)
					 * .edit().putLong(PreferenceHelper.BAT_CHARGE, diff)
					 * .commit();
					 */
				}

				// Log.d("stencil", "Charging with " + diff);

			} else {

				if (isconnected) {
					subtext = "E: Full Charge in "
							+ TimeFuncs
									.convtohournmin((long) (mContext
											.getSharedPreferences(
													"saphion.batterycaster_preferences",
													Context.MODE_MULTI_PROCESS)
											.getLong(
													PreferenceHelper.BAT_CHARGE,
													81) * (100 - level)));
					// Log.d("stencil", "Estimating Charging");
					// PreferenceManager.getDefaultSharedPreferences(getBaseContext())
					// .edit().putLong("batcharge", diff).commit();
					// Log.d("stencil", "EST Charging with " + diff);

				} else {
					subtext = "E: Empty in "
							+ TimeFuncs
									.convtohournmin((long) (mContext
											.getSharedPreferences(
													"saphion.batterycaster_preferences",
													Context.MODE_MULTI_PROCESS)
											.getLong(
													PreferenceHelper.BAT_DISCHARGE,
													792) * (level)));
					// Log.d("stencil", "Estimating Discharging");
				}
			}
		}

		if (level == 100) {
			subtext = "Fully Charged";
		}
		return subtext;
	}

	/**
	 * Create Battery Bitmap
	 */
	public static Bitmap battery(int mLevel, Context mycontext, int scale) {

		int color1, color2, color3, color4;

		color1 = 0xff1e8bd4;// Color.argb(255, 44, 172, 218);
		color2 = 0xa01e8bd4;// Color.argb(255, 24, 82, 112);
		color3 = 0x00111111;
		color4 = 0xffffffff;
		Bitmap bitmap = BitmapFactory.decodeResource(mycontext.getResources(),
				R.drawable.dialback);

		bitmap = Bitmap.createScaledBitmap(bitmap, (int) (scale / 1.5),
				(int) (scale / 1.5), true);

		Bitmap circleBitmap = Bitmap.createBitmap(scale, scale,
				Config.ARGB_8888);

		Bitmap cfoSize = bitmap;

		Paint paint = new Paint();
		paint.setAlpha(220);
		paint.setAntiAlias(true);
		paint.setColor(color3);
		// if (getPrefs.getBoolean("Shadow" + value, false))
		// paint.setShadowLayer(radius, Xoff, Yoff, colorShad);

		Canvas c = new Canvas(circleBitmap);
		c.drawCircle(
				bitmap.getWidth() / 2,
				bitmap.getHeight() / 2,
				(float) ((bitmap.getWidth() / 2) - (0.24 * (bitmap.getWidth() / 2))),
				paint);
		Paint mpaint = new Paint();
		mpaint.setColor(color3);
		mpaint.setAlpha(Color.alpha(color3));
		mpaint.setAntiAlias(true);

		c.drawCircle(
				cfoSize.getWidth() / 2,
				cfoSize.getHeight() / 2,
				(float) ((cfoSize.getWidth() / 2) - (0.1 * (cfoSize.getWidth() / 2))),
				mpaint);

		Paint mypaint = new Paint();
		mypaint.setAntiAlias(true);
		// if (getPrefs.getBoolean("Shadow" + value, false))
		// mypaint.setShadowLayer(radius, Xoff, Yoff, colorShad);

		mypaint.setStrokeWidth((float) (cfoSize.getWidth() * 0.0253));// 0.0983

		mypaint.setStyle(Style.STROKE);
		mypaint.setAntiAlias(true);

		mypaint.setColor(color2);

		float left = (float) (cfoSize.getWidth() * 0.05);
		float top = (float) (cfoSize.getWidth() * 0.05);
		float right = cfoSize.getWidth() - (float) (cfoSize.getHeight() * 0.05);
		float bottom = cfoSize.getHeight()
				- (float) (cfoSize.getWidth() * 0.05);

		RectF rectf = new RectF(left, top, right, bottom);

		float angle = mLevel * 360;
		angle = angle / 100;

		// switch (getPrefs.getInt(PreferenceHelper.WIDGET_THEMES, 0)) {
		// case 0:
		c.drawCircle(
				(float) (cfoSize.getWidth() / 2),
				(float) (cfoSize.getHeight() / 2),
				(float) ((cfoSize.getWidth() / 2) - (0.1 * (cfoSize.getWidth() / 2))),
				mypaint);
		/*
		 * break; case 1: for (int i = 1; i <= 24; i++) c.drawArc(rectf, -88 +
		 * ((i - 1) * 11) + (i - 1) * 4, 11, false, mypaint); break; case 2:
		 * c.drawCircle((float) (cfoSize.getWidth() / 2), (float) (cfoSize
		 * .getHeight() / 2), (float) ((cfoSize.getWidth() / 2) - (0.1 *
		 * (cfoSize .getWidth() / 2))), mypaint); break; case 3: for (int i = 1;
		 * i <= 24; i++) c.drawArc(rectf, -88 + ((i - 1) * 11) + (i - 1) * 4,
		 * 11, false, mypaint); break; }
		 */

		mypaint.setStrokeWidth((float) (cfoSize.getWidth() * 0.0783));

		mypaint.setColor(color1);

		/*
		 * switch (getPrefs.getInt(PreferenceHelper.WIDGET_THEMES, 0)) {
		 * 
		 * case 0: case 1:
		 */
		c.drawArc(rectf, -90, angle, false, mypaint);
		/*
		 * break; case 2: case 3: int i; for (i = 1; i <= (angle / 15); i++)
		 * c.drawArc(rectf, -88 + ((i - 1) * 11) + (i - 1) * 4, 11, false,
		 * mypaint); if (angle > -88 + ((i - 1) * 11) + (i - 1) * 4) { angle =
		 * angle - Magnitude(angle); c.drawArc(rectf, -88 + ((i - 1) * 11) + (i
		 * - 1) * 4, angle, false, mypaint); } break; }
		 */

		mypaint = new Paint();
		mypaint.setColor(color4);
		mypaint.setAntiAlias(true);
		// if (getPrefs.getBoolean("Shadow" + value, false))
		// mypaint.setShadowLayer(radius, Xoff, Yoff, colorShad);
		float size = (float) (bitmap.getWidth() * 0.42);

		mypaint.setTextSize(size);
		mypaint.setTextAlign(Align.CENTER);
		Typeface tf = Typeface.createFromAsset(mycontext.getAssets(),
				Constants.FONT_USING);

		mypaint.setTypeface(tf);

		c.drawText(
				mLevel + "%",
				(float) (cfoSize.getWidth() / 2),
				(float) (cfoSize.getHeight() * 0.5 + (mypaint.getFontSpacing() / 3.7)),
				mypaint);

		return circleBitmap;

	}

	public static float Magnitude(float angle) {
		String t = String.valueOf(angle);
		if ((t).contains(".")) {
			t = t.substring(0, t.indexOf("."));
		}
		return Float.parseFloat(t);
	}

	public static Bitmap loader(Context mContext) {

		int mLevel = 5;

		Bitmap circleBitmap = Bitmap.createBitmap(
				(int) (NewFunctions.ReturnHeight(180, mContext)),
				(int) (NewFunctions.ReturnHeight(180, mContext)),
				Config.ARGB_8888);

		Bitmap cfoSize;

		cfoSize = Bitmap.createBitmap(
				(int) NewFunctions.ReturnHeight(180, mContext),
				NewFunctions.ReturnHeight(180, mContext), Config.ARGB_8888);

		Paint paint = new Paint();
		paint.setAlpha(220);
		paint.setAntiAlias(true);

		Canvas c = new Canvas(circleBitmap);

		Paint mypaint = new Paint();
		mypaint.setAntiAlias(true);

		mypaint.setStrokeWidth((float) (cfoSize.getWidth() * 0.0253));

		mypaint.setStyle(Style.STROKE);
		mypaint.setAntiAlias(true);

		mypaint.setColor(0xffffffff);

		float left = (float) (cfoSize.getWidth() * 0.05);
		float top = (float) (cfoSize.getWidth() * 0.05);
		float right = cfoSize.getWidth() - (float) (cfoSize.getHeight() * 0.05);
		float bottom = cfoSize.getHeight()
				- (float) (cfoSize.getWidth() * 0.05);

		RectF rectf = new RectF(left, top, right, bottom);

		float angle = mLevel * 360;
		angle = angle / 100;

		for (int i = 1; i <= 24; i++) {
			c.drawArc(rectf, -88 + ((i - 1) * 11) + (i - 1) * 4, 11, false,
					mypaint);
		}

		mypaint.setStrokeWidth((float) (cfoSize.getWidth() * 0.0783));

		mypaint.setColor(0xff33b5e5);

		int i;
		for (i = 1; i <= (angle / 15); i++)
			c.drawArc(rectf, -88 + ((i - 1) * 11) + (i - 1) * 4, 11, false,
					mypaint);
		if (angle > -88 + ((i - 1) * 11) + (i - 1) * 4) {
			angle = angle - Magnitude(angle);
			c.drawArc(rectf, -88 + ((i - 1) * 11) + (i - 1) * 4, angle, false,
					mypaint);
		}

		return circleBitmap;

	}

	public static Bitmap createBatFrag(Context mContext, int mLevel,
			String temperature, boolean isConnected) {
		int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels);
		int height = mContext.getResources().getDisplayMetrics().heightPixels;

		if (width > height) {
			width = height + width;
			height = width - height;
			width = width - height;
		}

		Bitmap circleBitmap = Bitmap.createBitmap(width, (int) (height / 2.8),
				Config.ARGB_8888);
		Canvas c = new Canvas(circleBitmap);

		float x = (float) (circleBitmap.getHeight() * 0.0255);
		float y = (float) (circleBitmap.getHeight() * 0.16);

		Bitmap batCircle = battery(mLevel, mContext,
				(int) (c.getWidth() * 0.55));

		float xz = x;

		x = (float) ((4 * x) + (batCircle.getWidth() * 0.8));

		c.drawBitmap(batCircle, (float) (x - (batCircle.getWidth() / 1.24)),// 0.05
				(float) (circleBitmap.getHeight() * 0.08), null);

		Paint paint = new Paint();

		paint.setTypeface(Typeface.createFromAsset(mContext.getAssets(),
				Constants.FONT_USING));

		paint.setAntiAlias(true);
		paint.setColor(0xff595959);
		paint.setStrokeWidth((float) (circleBitmap.getHeight() * 0.015));// width
		paint.setStyle(Style.FILL);

		// x = (float) ((4 * x) + (batCircle.getWidth() * 0.8));

		c.drawLine(x, (float) (circleBitmap.getHeight() * 0.06), x, x, paint);

		paint.setTextAlign(Align.LEFT);
		paint.setTextSize((float) (circleBitmap.getHeight() * 0.1));// width
		paint.setColor(0xff33b5e5);

		x = x + (2 * xz);

		c.drawText("Stats:", x, y, paint);

		paint.setColor(0xffffffff);

		y = y + paint.getFontSpacing();
		paint.setTextSize((float) (circleBitmap.getHeight() * 0.07));// width
		if (isConnected)
			c.drawText(
					"Charging Speed("
							+ mContext.getResources().getString(R.string.delta)
							+ "1%)", x, y, paint);
		else
			c.drawText(
					"Discharging Speed("
							+ mContext.getResources().getString(R.string.delta)
							+ "1%)", x, y, paint);

		y = y + paint.getFontSpacing();

		paint.setColor(0xffc9c9c9);
		paint.setTextSize((float) (circleBitmap.getHeight() * 0.065));// width

		if (isConnected)
			c.drawText(TimeFuncs.convtohournminndaynsec(mContext
					.getSharedPreferences("saphion.batterycaster_preferences",
							Context.MODE_MULTI_PROCESS).getLong(
							PreferenceHelper.BAT_CHARGE, 81)), x, y, paint);
		else
			c.drawText(TimeFuncs.convtohournminndaynsec(mContext
					.getSharedPreferences("saphion.batterycaster_preferences",
							Context.MODE_MULTI_PROCESS).getLong(
							PreferenceHelper.BAT_DISCHARGE, 792)), x, y, paint);

		float yz = y + paint.getFontSpacing();

		y = (float) (batCircle.getHeight() * 0.75);

		Bitmap temp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.temp_light),
				(int) (circleBitmap.getHeight() * 0.2), (int) (circleBitmap
						.getHeight() * 0.2), true);

		paint.setTextSize((float) (temp.getWidth() * 0.6));

		c.drawBitmap(
				temp,
				(float) ((batCircle.getWidth() / 2.2))
						- paint.measureText(temperature), y, paint);

		y = y + paint.getFontSpacing();

		paint.setColor(Color.WHITE);

		paint.setTextAlign(Align.CENTER);

		c.drawText(temperature, (float) ((batCircle.getWidth() / 2)), y, paint);// "23°C"

		paint.setTextAlign(Align.LEFT);

		paint.setTextSize((float) (circleBitmap.getHeight() * 0.07));

		yz = (float) (yz + yz * 0.10);
		if (isConnected)
			c.drawText("Charger Plugged", x, yz, paint);
		else
			c.drawText("Charger Unplugged", x, yz, paint);
		yz = yz + paint.getFontSpacing();

		paint.setColor(0xffc9c9c9);
		paint.setTextSize((float) (circleBitmap.getHeight() * 0.065));

		c.drawText(getLastPlugged(mContext), x, yz, paint);

		paint.setTextSize((float) (circleBitmap.getHeight() * 0.07));
		yz = yz + paint.getFontSpacing();
		yz = (float) (yz + yz * 0.10);
		paint.setColor(0xffffffff);
		if (isConnected)
			c.drawText("Charged in", x, yz, paint);
		else
			c.drawText("Empty in", x, yz, paint);

		paint.setTextSize((float) (circleBitmap.getHeight() * 0.065));
		yz = yz + paint.getFontSpacing();
		paint.setColor(0xffc9c9c9);
		String willlast;

		if (isConnected) {
			long diff;
			diff = mContext.getSharedPreferences(
					"saphion.batterycaster_preferences",
					Context.MODE_MULTI_PROCESS).getLong(
					PreferenceHelper.BAT_CHARGE, 81);
			willlast = TimeFuncs.convtohournminnday(diff * (100 - mLevel));
		} else {
			long diff;
			diff = (long) (mContext.getSharedPreferences(
					"saphion.batterycaster_preferences",
					Context.MODE_MULTI_PROCESS).getLong(
					PreferenceHelper.BAT_DISCHARGE, 792));
			willlast = TimeFuncs.convtohournminnday(diff * (mLevel));
		}

		if (!willlast.contains("Day(s)"))
			c.drawText(willlast, x, yz, paint);
		else {
			String days = willlast.substring(0, willlast.indexOf("Day(s)") + 7);
			c.drawText(days, x, yz, paint);
			yz = yz + paint.getFontSpacing();
			c.drawText(
					willlast.substring(willlast.indexOf("Day(s)") + 7,
							willlast.length()), x, yz, paint);
		}

		return circleBitmap;
	}

	public static String getLastPlugged(Context mContext) {
		String mainText = mContext
				.getSharedPreferences("saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS).getString(
						PreferenceHelper.LAST_CHARGED,
						TimeFuncs.getCurrentTimeStamp());

		String time = TimeFuncs.convtohournminnday(TimeFuncs.newDiff(
				TimeFuncs.GetItemDate(mainText),
				TimeFuncs.GetItemDate(TimeFuncs.getCurrentTimeStamp())));

		if (!time.equals("0 Minute(s)"))
			mainText = time + " ago";
		else
			mainText = "right now";

		return mainText;
	}

	public static Drawable getBar(Context mContext, boolean bool, float width) {
		Bitmap circleBitmap = Bitmap.createBitmap((int) width,
				NewFunctions.ReturnHeight(2, mContext), Config.ARGB_8888);
		Canvas c = new Canvas(circleBitmap);

		if (bool)
			c.drawColor(0xff33b5e5);
		else
			c.drawColor(mContext.getResources().getColor(R.color.clock_gray));

		Drawable d = new BitmapDrawable(mContext.getResources(), circleBitmap);
		d.setBounds(new Rect(0, 0, circleBitmap.getWidth(), circleBitmap
				.getHeight()));
		return d;

	}

	public static Bitmap getLockScreen(int mLevel, int width, int height,
			Context mContext, String Label) {

		Bitmap circleBitmap = Bitmap.createBitmap(width, height,
				Config.ARGB_8888);
		Canvas c = new Canvas(circleBitmap);

		Bitmap batCircle = battery(mLevel, mContext,
				(int) (c.getWidth() * 1.35));

		c.drawBitmap(batCircle, (width) / 24, (width) / 24, null);

		Paint mypaint = new Paint();
		mypaint.setAntiAlias(true);
		float size = (float) (c.getWidth() * 0.12);

		mypaint.setTextSize(size);
		mypaint.setTextAlign(Align.CENTER);
		Typeface tf = Typeface.createFromAsset(mContext.getAssets(),
				Constants.FONT_USING);

		mypaint.setTypeface(tf);
		mypaint.setTextAlign(Align.CENTER);
		mypaint.setColor(Color.WHITE);

		c.drawText(Label, c.getWidth() / 2, (float) (c.getHeight() * 0.98),
				mypaint);

		return circleBitmap;
	}

	public static Bitmap newbattery(int mLevel, Context mycontext, int scale,
			boolean isconnected) {
		int color1, color2, color3, color4;

		scale = (int) (scale * 0.825);

		color1 = 0xff1e8bd4;// 0xffffffff;//0xff00bff3;//Color.argb(255, 44,
							// 172, 218);
		color2 = 0xa08e8f90;// 0xa000bff3;//0xa01e8bd4;//Color.argb(255, 24, 82,
							// 112);
		color3 = 0x00111111;
		color4 = 0xffffffff;

		Bitmap circleBitmap = Bitmap.createBitmap(scale, scale,
				Config.ARGB_8888);

		Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
		paint.setAlpha(220);
		paint.setAntiAlias(true);
		paint.setColor(color3);

		Canvas c = new Canvas(circleBitmap);
		c.drawCircle(scale / 2, scale / 2,
				(float) ((scale / 2) - (0.24 * (scale / 2))), paint);
		Paint mpaint = new Paint(Paint.FILTER_BITMAP_FLAG);
		mpaint.setColor(color3);
		mpaint.setAlpha(Color.alpha(color3));
		mpaint.setAntiAlias(true);

		c.drawCircle(scale / 2, scale / 2,
				(float) ((scale / 2) - (0.1 * (scale / 2))), mpaint);

		Paint mypaint = new Paint(Paint.FILTER_BITMAP_FLAG);
		mypaint.setAntiAlias(true);

		mypaint.setStrokeWidth((float) (scale * 0.0253));// 0.0983

		mypaint.setStyle(Style.STROKE);
		mypaint.setAntiAlias(true);

		mypaint.setColor(color2);

		float left = (float) (scale * 0.05);
		float top = (float) (scale * 0.05);
		float right = scale - (float) (scale * 0.05);
		float bottom = scale - (float) (scale * 0.05);

		RectF rectf = new RectF(left, top, right, bottom);

		float angle = mLevel * 360;
		angle = angle / 100;

		c.drawCircle((float) (scale / 2), (float) (scale / 2),
				(float) ((scale / 2) - (0.1 * (scale / 2))), mypaint);

		mypaint.setStrokeWidth((float) (scale * 0.0783));

		mypaint.setColor(color1);

		c.drawArc(rectf, -90, angle, false, mypaint);

		mypaint = new Paint(Paint.FILTER_BITMAP_FLAG);
		mypaint.setColor(color4);
		mypaint.setAntiAlias(true);

		// float size = (float) (scale * 0.11);

		mypaint.setTextSize(SpToPx(mycontext, 100f));
		mypaint.setTextAlign(Align.CENTER);
		Typeface tf = Typeface.createFromAsset(mycontext.getAssets(),
				Constants.FONT_USING);

		mypaint.setTypeface(tf);

		c.drawText(mLevel + "%", (float) (scale / 2),
				(float) (scale * 0.5 + (mypaint.getFontSpacing() / 3.7)),
				mypaint);

		if (isconnected) {
			Bitmap connected = BitmapFactory.decodeResource(
					mycontext.getResources(), R.drawable.charging);
			int mScale = (int) (scale * 0.2);
			connected = Bitmap.createScaledBitmap(connected, mScale, mScale,
					true);
			float mX = scale / 2 - connected.getWidth() / 2;
			float mY = (float) (scale * 0.7);
			c.drawBitmap(connected, mX, mY, mypaint);
		}

		return circleBitmap;
	}

	/**
	 * Convert dp to px
	 * 
	 * @author Sachin
	 * @param i
	 * @param mContext
	 * @return
	 */

	public static int ReturnHeight(float i, Context mContext) {

		DisplayMetrics displayMetrics = mContext.getResources()
				.getDisplayMetrics();
		return (int) ((i * displayMetrics.density) + 0.5);

	}

	public static float SpToPx(Context context, Float i) {
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return i * scaledDensity;
	}

	public static Bitmap createOldStatStrip(Context mContext, String temp,
			String val) {

		Paint p = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
		p.setAntiAlias(true);

		Bitmap bitmap = Bitmap.createBitmap(mContext.getResources()
				.getDisplayMetrics().widthPixels, (int) (mContext
				.getResources().getDisplayMetrics().heightPixels * 0.125),
				Config.ARGB_8888);

		Canvas c = new Canvas(bitmap);

		Options options = new BitmapFactory.Options();

		Bitmap windmill = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.temp_white, options);

		Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG
				| Paint.ANTI_ALIAS_FLAG);
		mPaint.setAntiAlias(true);
		// mPaint.setColor(Color.RED);
		// c.drawRect(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
		// mPaint);
		Typeface regT = Typeface.createFromAsset(mContext.getAssets(),
				Constants.FONT_USING);
		Typeface boldT = Typeface.createFromAsset(mContext.getAssets(),
				Constants.FONT_USING_BOLD);

		float x = (int) (bitmap.getWidth() * 0.125);
		float y = (int) (bitmap.getWidth() * 0.125);
		windmill = Bitmap.createScaledBitmap(windmill, (int) x, (int) y, true);
		c.drawBitmap(windmill, (float) (bitmap.getWidth() * 0.031),
				(float) (bitmap.getHeight() * 0.2), p);
		x = (float) (bitmap.getWidth() * 0.155);
		y = (float) (bitmap.getHeight() * 0.45);
		// mPaint.setTextScaleX(1);
		mPaint.setTextSize(SpToPx(mContext, 19f));
		mPaint.setTypeface(boldT);
		mPaint.setColor(0xff1e8bd4);

		c.drawText("Temperature:", x, y, mPaint);
		mPaint.setColor(Color.WHITE);
		mPaint.setTypeface(regT);
		mPaint.setTextSize(SpToPx(mContext, 15f));
		c.drawText(temp, x, y + mPaint.getFontSpacing(), mPaint);

		Bitmap pres = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.plus_mod, options);

		x = (int) (bitmap.getWidth() * 0.125);
		y = (int) (bitmap.getWidth() * 0.125);

		pres = Bitmap.createScaledBitmap(pres, (int) x, (int) y, true);

		c.drawBitmap(pres, (float) (bitmap.getWidth() * 0.5),
				(float) (bitmap.getHeight() * 0.2), p);

		x = (float) ((float) (bitmap.getWidth() * 0.48) + pres.getWidth() * 1.4);
		y = (float) (bitmap.getHeight() * 0.45);
		// mPaint.setTextScaleX(1);
		mPaint.setTextSize(SpToPx(mContext, 19f));
		mPaint.setTypeface(boldT);
		mPaint.setColor(0xff1e8bd4);

		c.drawText("Battery Health:",
				c.getWidth() - mPaint.measureText("Battery Health: "), y,
				mPaint);

		mPaint.setColor(Color.WHITE);
		mPaint.setTypeface(regT);
		mPaint.setTextSize(SpToPx(mContext, 15f));
		c.drawText(val, x, y + mPaint.getFontSpacing(), mPaint);

		return bitmap;
	}

	public static Bitmap createStatStrip(Context mContext, String temp,
			String val) {

		Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);
		p.setAntiAlias(true);

		Bitmap bitmap = Bitmap.createBitmap(mContext.getResources()
				.getDisplayMetrics().widthPixels, (int) (mContext
				.getResources().getDisplayMetrics().heightPixels * 0.125),
				Config.ARGB_8888);

		Canvas c = new Canvas(bitmap);

		Options options = new BitmapFactory.Options();

		Bitmap windmill = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.temp_white, options);

		Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
		mPaint.setAntiAlias(true);
		// mPaint.setColor(Color.RED);
		// c.drawRect(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
		// mPaint);
		Typeface regT = Typeface.createFromAsset(mContext.getAssets(),
				Constants.FONT_USING);
		Typeface boldT = Typeface.createFromAsset(mContext.getAssets(),
				Constants.FONT_USING_BOLD);

		float x = (int) (bitmap.getWidth() * 0.13);
		float y = (int) (bitmap.getWidth() * 0.13);
		windmill = Bitmap.createScaledBitmap(windmill, (int) x, (int) y, true);
		c.drawBitmap(windmill, (float) (bitmap.getWidth() * 0.035),
				(float) (bitmap.getHeight() * 0.21), p);
		x = (float) (bitmap.getWidth() * 0.18);
		y = (float) (bitmap.getHeight() * 0.45);
		mPaint.setTextScaleX(1);
		mPaint.setTextSize((float) (mContext.getResources().getDisplayMetrics().widthPixels * 0.057));
		mPaint.setTypeface(boldT);
		mPaint.setColor(0xff1e8bd4);

		c.drawText("Temperature:", x, y, mPaint);
		mPaint.setColor(Color.WHITE);
		mPaint.setTypeface(regT);
		c.drawText(temp, x, y + mPaint.getFontSpacing(), mPaint);

		Bitmap pres = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.plus_mod, options);

		x = (int) (bitmap.getWidth() * 0.156);
		y = (int) (bitmap.getWidth() * 0.156);

		pres = Bitmap.createScaledBitmap(pres, (int) x, (int) y, true);

		c.drawBitmap(pres, bitmap.getWidth() / 2,
				(float) (bitmap.getHeight() * 0.15), p);

		x = (float) ((float) (bitmap.getWidth() * 0.48) + pres.getWidth() * 1.2);
		y = (float) (bitmap.getHeight() * 0.45);
		mPaint.setTextScaleX(1);
		mPaint.setTextSize((float) (mContext.getResources().getDisplayMetrics().widthPixels * 0.057));
		mPaint.setTypeface(boldT);
		mPaint.setColor(0xff1e8bd4);

		c.drawText("Health:", x, y, mPaint);
		mPaint.setColor(Color.WHITE);
		mPaint.setTypeface(regT);
		c.drawText(val, x, y + mPaint.getFontSpacing(), mPaint);

		return bitmap;
	}

	public static String getBatHealth(Intent batteryIntent) {
		int inthealth = batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH,
				0);
		String health = "Unknown";
		switch (inthealth) {
		case BatteryManager.BATTERY_HEALTH_COLD:
			health = "Cold";
			break;
		case BatteryManager.BATTERY_HEALTH_DEAD:
			health = "Dead";
			break;
		case BatteryManager.BATTERY_HEALTH_GOOD:
			health = "Good";
			break;
		case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
			health = "Over Voltage";
			break;
		case BatteryManager.BATTERY_HEALTH_OVERHEAT:
			health = "Overheat";
			break;
		case BatteryManager.BATTERY_HEALTH_UNKNOWN:
			health = "Unknown";
			break;
		case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
			health = "Unspecified failure";
			break;
		}

		return health;
	}

	public static Drawable createFilteredIcon(Context mContext,
			boolean enabled, int id) {
		Drawable d = mContext.getResources().getDrawable(id);
		int color1 = 0xff1e8bd4;
		int color2 = 0xa08e8f90;
		if (enabled)
			d.setColorFilter(color1, Mode.MULTIPLY);
		else
			d.setColorFilter(color2, Mode.MULTIPLY);
		return d;
	}

	public static Bitmap createFilteredIconBitmap(Context mContext,
			boolean enabled, int id) {
		Drawable d = mContext.getResources().getDrawable(id);
		int color1 = 0xff1e8bd4;
		int color2 = 0xa08e8f90;
		if (enabled)
			d.setColorFilter(color1, Mode.MULTIPLY);
		else
			d.setColorFilter(color2, Mode.MULTIPLY);
		return drawableToBitmap(d);
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		int width = drawable.getIntrinsicWidth();
		width = width > 0 ? width : 1;
		int height = drawable.getIntrinsicHeight();
		height = height > 0 ? height : 1;

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

}
