package saphion.utils;

import saphion.batterycaster.R;
import saphion.stencil.batterywidget.WidPrefHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class Functions {

	/**
	 * Convert stacktrace to string
	 * 
	 * @param stackTrace
	 * @return
	 */
	public static CharSequence convStackTraceToString(
			StackTraceElement[] stackTrace) {
		String s = "";
		for (int i = 0; i < stackTrace.length; i++) {
			s = s + stackTrace[i].toString();
		}
		return s;
	}

	/**
	 * Create Battery Bitmap
	 */
	public static Bitmap battery(int mLevel, float temper, boolean isConnected,
			Context mycontext, WidPrefHelper wph, int awID) {

		int color1, color2, color3, color4, colorShad, radius, Xoff, Yoff;

		String file = PreferenceHelper.SETTINGS_WIDGET_FILE + awID;
		SharedPreferences getPrefs;
		// if (awID != -99)
		getPrefs = mycontext.getSharedPreferences(file, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
		/*
		 * else getPrefs =
		 * PreferenceManager.getDefaultSharedPreferences(mycontext);
		 */
		if (wph.getWidgetTheme() == 4) {
			return getNewCircle(mLevel, isConnected, mycontext, getPrefs, wph,
					temper);
		}
		String value;
		if (isConnected) {

			value = "plugged";

			color1 = getPrefs.getInt("color" + value + 0,
					0xff1e8bd4);//Color.argb(255, 44, 172, 218)
			color2 = getPrefs.getInt("color" + value + 1,
					0xff636466);//Color.argb(255, 24, 82, 112)
			color3 = getPrefs.getInt("color" + value + 2,
					Color.argb(0, 0, 0, 0));
			color4 = getPrefs.getInt("color" + value + 3, 0xffffffff);
			colorShad = getPrefs.getInt("colorShad" + value, Color.BLACK);
			radius = getPrefs.getInt("radiusShad" + value, 8);
			Xoff = getPrefs.getInt("Xoff" + value, 8);
			Yoff = getPrefs.getInt("yOff" + value, 8);

		} else {
			if (!getPrefs.getBoolean("diffbattery", false)) {
				value = "bat1";
			} else if (mLevel > 80)
				value = "bat1";
			else if (mLevel <= 80 && mLevel > 60)
				value = "bat2";
			else if (mLevel <= 60 && mLevel > 40)
				value = "bat3";
			else if (mLevel <= 40 && mLevel > 20)
				value = "bat4";
			else if (mLevel <= 20)
				value = "bat5";
			else
				value = "bat1";

			color1 = getPrefs.getInt("color" + value + 0,
					0xff1e8bd4);//Color.argb(255, 44, 172, 218)
			color2 = getPrefs.getInt("color" + value + 1,
					0xff636466);//Color.argb(255, 24, 82, 112)
			color3 = getPrefs.getInt("color" + value + 2,
					Color.argb(0, 0, 0, 0));
			color4 = getPrefs.getInt("color" + value + 3, 0xffffffff);
			colorShad = getPrefs.getInt("colorShad" + value, Color.BLACK);
			radius = getPrefs.getInt("radiusShad" + value, 8);
			Xoff = getPrefs.getInt("Xoff" + value, 8);
			Yoff = getPrefs.getInt("yOff" + value, 8);
		}

		Bitmap bitmap = BitmapFactory.decodeResource(mycontext.getResources(),
				R.drawable.dialback);

		Bitmap circleBitmap = Bitmap.createBitmap(
				(int) (bitmap.getWidth() * 1.5),
				(int) (bitmap.getHeight() * 1.5), Config.ARGB_8888);

		Bitmap cfoSize;
		if (getPrefs.getBoolean("Shadow" + value, false))
			cfoSize = Bitmap.createBitmap((int) (bitmap.getWidth() * 1.38),
					(int) (bitmap.getHeight() * 1.38), Config.ARGB_8888);
		else
			cfoSize = circleBitmap;

		BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP,
				TileMode.CLAMP);
		Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
		paint.setShader(shader);
		paint.setAlpha(220);
		paint.setAntiAlias(true);
		if (getPrefs.getBoolean("Shadow" + value, false))
			paint.setShadowLayer(radius, Xoff, Yoff, colorShad);

		Canvas c = new Canvas(circleBitmap);
		c.drawCircle(
				bitmap.getWidth() / 2,
				bitmap.getHeight() / 2,
				(float) ((bitmap.getWidth() / 2) - (0.24 * (bitmap.getWidth() / 2))),
				paint);
		Paint mpaint = new Paint(Paint.FILTER_BITMAP_FLAG);
		mpaint.setColor(color3);
		mpaint.setAlpha(Color.alpha(color3));
		mpaint.setAntiAlias(true);

		c.drawCircle(
				cfoSize.getWidth() / 2,
				cfoSize.getHeight() / 2,
				(float) ((cfoSize.getWidth() / 2) - (0.1 * (cfoSize.getWidth() / 2))),
				mpaint);

		Paint mypaint = new Paint(Paint.FILTER_BITMAP_FLAG);
		mypaint.setAntiAlias(true);
		if (getPrefs.getBoolean("Shadow" + value, false))
			mypaint.setShadowLayer(radius, Xoff, Yoff, colorShad);

		mypaint.setStrokeWidth(wph.getLineBack());

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

		switch (wph.getWidgetTheme()) {
		case 0:
			c.drawCircle((float) (cfoSize.getWidth() / 2), (float) (cfoSize
					.getHeight() / 2),
					(float) ((cfoSize.getWidth() / 2) - (0.1 * (cfoSize
							.getWidth() / 2))), mypaint);
			break;
		case 1:
			for (int i = 1; i <= 24; i++)
				c.drawArc(rectf, -88 + ((i - 1) * 11) + (i - 1) * 4, 11, false,
						mypaint);
			break;
		case 2:
			c.drawCircle((float) (cfoSize.getWidth() / 2), (float) (cfoSize
					.getHeight() / 2),
					(float) ((cfoSize.getWidth() / 2) - (0.1 * (cfoSize
							.getWidth() / 2))), mypaint);
			break;
		case 3:
			for (int i = 1; i <= 24; i++)
				c.drawArc(rectf, -88 + ((i - 1) * 11) + (i - 1) * 4, 11, false,
						mypaint);
			break;
		}

		mypaint.setStrokeWidth(wph.getLineLeft());

		mypaint.setColor(color1);

		switch (wph.getWidgetTheme()) {

		case 0:
		case 1:
			c.drawArc(rectf, -90, angle, false, mypaint);
			break;
		case 2:
		case 3:
			int i;
			for (i = 1; i <= (angle / 15); i++)
				c.drawArc(rectf, -88 + ((i - 1) * 11) + (i - 1) * 4, 11, false,
						mypaint);
			if (angle > -88 + ((i - 1) * 11) + (i - 1) * 4) {
				angle = angle - Magnitude(angle);
				c.drawArc(rectf, -88 + ((i - 1) * 11) + (i - 1) * 4, angle,
						false, mypaint);
			}
			break;
		}

		mypaint = new Paint(Paint.FILTER_BITMAP_FLAG);
		mypaint.setColor(color4);
		mypaint.setAntiAlias(true);
		if (getPrefs.getBoolean("Shadow" + value, false))
			mypaint.setShadowLayer(radius, Xoff, Yoff, colorShad);
		float size = wph.getFontSize();

		mypaint.setTextSize(size);
		mypaint.setTextAlign(Align.CENTER);
		Typeface tf = Typeface.DEFAULT_BOLD;
		try {
			String stf = wph.getFont();
			if (stf.equals("empty") || stf.equals("default")) {
				tf = Typeface.DEFAULT_BOLD;
			} else {
				tf = Typeface.createFromFile(stf);
			}
		} catch (RuntimeException e) {
			tf = Typeface.DEFAULT_BOLD;
		} finally {

			mypaint.setTypeface(tf);
		}
		String mText = "";
		switch (wph.getWidgetText()) {
		case 0:
			mText = mLevel + "%";
			break;
		case 1:
			mText = mLevel + "";
			break;
		case 2:
			if (isConnected)
				mText = TimeFuncs.convtohwidget(mycontext.getSharedPreferences(
						"saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS).getLong(
						PreferenceHelper.BAT_CHARGE, 81)
						* (100 - mLevel))
						+ "h";
			else
				mText = TimeFuncs.convtohwidget(mycontext.getSharedPreferences(
						"saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS).getLong(
						PreferenceHelper.BAT_DISCHARGE, 792)
						* (mLevel))
						+ "h";
			break;
		case 3:
			if (isConnected)
				mText = TimeFuncs.convtohwidget(mycontext.getSharedPreferences(
						"saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS).getLong(
						PreferenceHelper.BAT_CHARGE, 81)
						* (100 - mLevel));
			else
				mText = TimeFuncs.convtohwidget(mycontext.getSharedPreferences(
						"saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS).getLong(
						PreferenceHelper.BAT_DISCHARGE, 792)
						* (mLevel));
			break;
		case 4:
			mText = Functions.updateTemperature(temper, true, false);
			break;
		case 5:
			mText = Functions.updateTemperature(temper, false, false);
			break;
		}
		c.drawText(
				mText,
				(float) (cfoSize.getWidth() / 2),
				(float) (cfoSize.getHeight() * 0.5 + (mypaint.getFontSpacing() / 3.7)),
				mypaint);

		return circleBitmap;

	}

	public static Bitmap getNewCircle(int mLevel, boolean isConnected,
			Context mContext, SharedPreferences getPrefs, WidPrefHelper wph,
			float temper) {
		int color1, color2, color3, color4, colorShad, radius, Xoff, Yoff;
		String value;
		if (isConnected) {

			value = "plugged";
			
			color1 = getPrefs.getInt("color" + value + 0,
					0xff1e8bd4);//Color.argb(255, 44, 172, 218)
			color2 = getPrefs.getInt("color" + value + 1,
					0xff636466);//Color.argb(255, 24, 82, 112)

			/*color1 = getPrefs.getInt("color" + value + 0,
					Color.argb(255, 44, 172, 218));
			color2 = getPrefs.getInt("color" + value + 1,
					Color.argb(255, 24, 82, 112));*/
			color3 = getPrefs.getInt("color" + value + 2,
					Color.argb(0, 0, 0, 0));
			color4 = getPrefs.getInt("color" + value + 3, 0xffffffff);
			colorShad = getPrefs.getInt("colorShad" + value, Color.BLACK);
			radius = getPrefs.getInt("radiusShad" + value, 8);
			Xoff = getPrefs.getInt("Xoff" + value, 8);
			Yoff = getPrefs.getInt("yOff" + value, 8);

		} else {
			if (!getPrefs.getBoolean("diffbattery", false)) {
				value = "bat1";
			} else if (mLevel > 80)
				value = "bat1";
			else if (mLevel <= 80 && mLevel > 60)
				value = "bat2";
			else if (mLevel <= 60 && mLevel > 40)
				value = "bat3";
			else if (mLevel <= 40 && mLevel > 20)
				value = "bat4";
			else if (mLevel <= 20)
				value = "bat5";
			else
				value = "bat1";

			/*color1 = getPrefs.getInt("color" + value + 0,
					Color.argb(255, 44, 172, 218));
			color2 = getPrefs.getInt("color" + value + 1,
					Color.argb(255, 24, 82, 112));*/
			color1 = getPrefs.getInt("color" + value + 0,
					0xff1e8bd4);//Color.argb(255, 44, 172, 218)
			color2 = getPrefs.getInt("color" + value + 1,
					0xff636466);//Color.argb(255, 24, 82, 112)
			color3 = getPrefs.getInt("color" + value + 2,
					Color.argb(0, 0, 0, 0));
			color4 = getPrefs.getInt("color" + value + 3, 0xffffffff);
			colorShad = getPrefs.getInt("colorShad" + value, Color.BLACK);
			radius = getPrefs.getInt("radiusShad" + value, 8);
			Xoff = getPrefs.getInt("Xoff" + value, 8);
			Yoff = getPrefs.getInt("yOff" + value, 8);
		}

		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.dialback);

		Bitmap circleBitmap = Bitmap.createBitmap(
				(int) (bitmap.getWidth() * 1.5),
				(int) (bitmap.getHeight() * 1.5), Config.ARGB_8888);

		Bitmap cfoSize;
		if (getPrefs.getBoolean("Shadow" + value, false))
			cfoSize = Bitmap.createBitmap((int) (bitmap.getWidth() * 1.38),
					(int) (bitmap.getHeight() * 1.38), Config.ARGB_8888);
		else
			cfoSize = circleBitmap;

		BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP,
				TileMode.CLAMP);
		Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
		paint.setShader(shader);
		paint.setAlpha(220);
		paint.setAntiAlias(true);
		if (getPrefs.getBoolean("Shadow" + value, false))
			paint.setShadowLayer(radius, Xoff, Yoff, colorShad);

		Canvas c = new Canvas(circleBitmap);
		/*
		 * c.drawCircle( bitmap.getWidth() / 2, bitmap.getHeight() / 2, (float)
		 * ((bitmap.getWidth() / 2) - (0.24 * (bitmap.getWidth() / 2))), paint);
		 */
		Paint mpaint = new Paint(Paint.FILTER_BITMAP_FLAG);
		mpaint.setColor(color3);
		mpaint.setAlpha(Color.alpha(color3));
		mpaint.setAntiAlias(true);

		c.drawCircle(
				cfoSize.getWidth() / 2,
				cfoSize.getHeight() / 2,
				(float) ((cfoSize.getWidth() / 2) - (0.08 * (cfoSize.getWidth() / 2))),
				mpaint);

		Paint mypaint = new Paint(Paint.FILTER_BITMAP_FLAG);
		mypaint.setAntiAlias(true);
		if (getPrefs.getBoolean("Shadow" + value, false))
			mypaint.setShadowLayer(radius, Xoff, Yoff, colorShad);
		float sW = (float) (cfoSize.getWidth() * 0.01);
		mypaint.setStrokeWidth(wph.getLineBac() == -99 ? sW : wph.getLineBac());

		// getPrefs.getFloat("lineback", (float) (cfoSize.getWidth() *0.01));

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

		c.drawCircle(
				(float) (cfoSize.getWidth() / 2),
				(float) (cfoSize.getHeight() / 2),
				(float) ((cfoSize.getWidth() / 2) - (0.08 * (cfoSize.getWidth() / 2))),
				mypaint);

		mypaint.setStrokeWidth((float) (cfoSize.getWidth() * 0.0183));

		mypaint.setColor(color1);

		c.drawArc(rectf, 90 - angle / 2, angle / 2, false, mypaint);

		c.drawArc(rectf, 90, angle / 2, false, mypaint);

		mypaint.setShadowLayer(0, Xoff, Yoff, colorShad);

		float cx = (float) (cfoSize.getWidth() / 2);
		float r = (float) ((cfoSize.getWidth() / 2) - (0.1 * (cfoSize
				.getWidth() / 2)));
		float cy = (float) (cfoSize.getHeight() / 2);
		int level = 0;
		float angle2 = 0;

		while (level <= mLevel) {

			angle2 = level * 360;
			angle2 = angle2 / 100;
			level++;
			float startY = (float) (cx + (r * Math.cos(Math
					.toRadians(angle2 / 2))));
			float startX = (float) (cy + (r * Math.sin(Math
					.toRadians(angle2 / 2))));
			float stopY = (float) (cx + (r * Math.cos(Math
					.toRadians(-angle2 / 2))));
			float stopX = (float) (cy + (r * Math.sin(Math
					.toRadians(-angle2 / 2))));

			c.drawLine(startX, startY, stopX, stopY, mypaint);
		}

		float startY = (float) (cx + (r * Math.cos(Math.toRadians(angle2 / 2))));
		float startX = (float) (cy + (r * Math.sin(Math.toRadians(angle2 / 2))));
		float stopY = (float) (cx + (r * Math.cos(Math.toRadians(-angle2 / 2))));
		float stopX = (float) (cy + (r * Math.sin(Math.toRadians(-angle2 / 2))));

		c.drawLine((float) (startX + 3), startY, (float) (stopX - 3), stopY,
				mypaint);
		mypaint = new Paint(Paint.FILTER_BITMAP_FLAG);
		mypaint.setColor(color4);
		mypaint.setAntiAlias(true);
		if (getPrefs.getBoolean("Shadow" + value, false))
			mypaint.setShadowLayer(radius, Xoff, Yoff, colorShad);
		float size = wph.getFontSize();/*
										 * getPrefs.getFloat("batfontsize",
										 * (float) (bitmap.getWidth() * 0.48));
										 */

		mypaint.setTextSize(size);
		mypaint.setTextAlign(Align.CENTER);
		Typeface tf = Typeface.DEFAULT_BOLD;
		try {
			String stf = wph.getFont();// getPrefs.getString("fontBatteryWidget",
										// "empty");
			if (stf.equals("empty") || stf.equals("default")) {
				tf = Typeface.DEFAULT_BOLD;
			} else {
				tf = Typeface.createFromFile(stf);
			}
		} catch (RuntimeException e) {
			tf = Typeface.DEFAULT_BOLD;
		} finally {

			mypaint.setTypeface(tf);
		}
		String mText = "";
		switch (wph.getWidgetText()) {
		case 0:
			mText = mLevel + "%";
			break;
		case 1:
			mText = mLevel + "";
			break;
		case 2:
			if (isConnected)
				mText = TimeFuncs.convtohwidget(mContext.getSharedPreferences(
						"saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS).getLong(
						PreferenceHelper.BAT_CHARGE, 81)
						* (100 - level))

						+ "h";

			else
				mText = TimeFuncs.convtohwidget(mContext.getSharedPreferences(
						"saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS).getLong(
						PreferenceHelper.BAT_DISCHARGE, 792)
						* (level))
						+ "h";
			break;
		case 3:
			if (isConnected)
				mText = TimeFuncs.convtohwidget(mContext.getSharedPreferences(
						"saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS).getLong(
						PreferenceHelper.BAT_CHARGE, 81)
						* (100 - level));
			else
				mText = TimeFuncs.convtohwidget(mContext.getSharedPreferences(
						"saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS).getLong(
						PreferenceHelper.BAT_DISCHARGE, 792)
						* (level));
			break;
		case 4:
			mText = Functions.updateTemperature(temper, true, false);
			break;
		case 5:
			mText = Functions.updateTemperature(temper, false, false);
			break;
		}

		c.drawText(
				mText,
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

	public static String updateTemperature(float temperature, boolean bool,
			boolean nbool) {
		if (nbool) {
			if (bool) {
				return temperature + "° C";

			} else {
				return adjustLenght(String
						.valueOf(((float) (((temperature)) / 0.56) + 32)))
						+ "° F";
			}
		} else {
			if (bool) {
				String temp = temperature + "";
				if ((temp).contains(".")) {
					temp = temp.substring(0, temp.indexOf("."));
				}
				return temp + "°";

			} else {
				String temp = adjustLenght(String
						.valueOf(((float) (((temperature)) / 0.56) + 32))) + "";
				if ((temp).contains(".")) {
					temp = temp.substring(0, temp.indexOf("."));
				}
				return temp + "°";
			}
		}
	}

	public static String adjustLenght(String s) {
		if (s.length() > 4) {
			s = s.substring(0, 4);
		}
		return s;
	}

	/*
	 * @author Sachin Convert dp to px
	 */
	public static int ReturnHeight(int i, Context mContext) {

		DisplayMetrics displayMetrics = mContext.getResources()
				.getDisplayMetrics();
		return (int) ((i * displayMetrics.density) + 0.5);

	}

	public static void settypeface(LinearLayout ll, Typeface tf) {

		int childcount = ll.getChildCount();
		for (int i = 0; i < childcount; i++) {
			View v = ll.getChildAt(i);

			if (v instanceof TextView) {
				((TextView) v).setTypeface(tf);
			} else if (v instanceof Button) {
				((Button) v).setTypeface(tf);
			} else if (v instanceof LinearLayout) {
				settypeface((LinearLayout) v, tf);
			}

		}

	}

	public static void settypeface(ScrollView ll, Typeface tf) {

		int childcount = ll.getChildCount();
		for (int i = 0; i < childcount; i++) {
			View v = ll.getChildAt(i);

			if (v instanceof TextView) {
				((TextView) v).setTypeface(tf);
			} else if (v instanceof Button) {
				((Button) v).setTypeface(tf);
			} else if (v instanceof LinearLayout) {
				settypeface((LinearLayout) v, tf);
			}

		}

	}
}
