package saphion.stencil.batterywidget;

import saphion.batterycaster.R;
import saphion.logger.Log;
import saphion.utils.Functions;
import saphion.utils.MyColorPickerView;
import saphion.utils.NewFunctions;
import saphion.utils.PreferenceHelper;
import saphion.utils.WidgetContainer;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

@SuppressLint("AlwaysShowAction")
public class MyBatteryDialog extends ActionBarActivity implements
		OnSeekBarChangeListener, OnClickListener {

	int curr = 0;
	// WidPrefHelper wph;
	ImageView preview;
	ImageView i1, i2, i3, i4, i5;
	SharedPreferences getPrefs;
	int awID;
	private String value;
	int color1, color2, color3, color4, colorShad, radius, Xoff, Yoff;
	boolean mainchecked;
	int WidgetTheme;
	float LineBack;
	float LineLeft;
	float FontSize;
	String Font;
	int widget_text;

	@SuppressLint("NewApi")
	@SuppressWarnings({ "deprecation" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.battery_color_dialog);

		if ((getIntent().hasExtra("extraString") && getIntent().hasExtra(
				AppWidgetManager.EXTRA_APPWIDGET_ID))) {

			awID = getIntent().getExtras().getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);

			WidgetTheme = getIntent().getIntExtra("WidgetTheme", 0);
			LineBack = getIntent().getFloatExtra("LineBack", 0);
			LineLeft = getIntent().getFloatExtra("LineLeft", 0);
			FontSize = getIntent().getFloatExtra("FontSize", 0);
			Font = getIntent().getStringExtra("Font");
			widget_text = getIntent().getIntExtra("Widget_text", 0);

			RelativeLayout iv = (RelativeLayout) findViewById(R.id.rlPreview2);

			LayoutParams params = iv.getLayoutParams();

			if (getResources().getDisplayMetrics().widthPixels > getResources()
					.getDisplayMetrics().heightPixels) {
				params.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.82);
				params.width = (int) (getResources().getDisplayMetrics().widthPixels / 2);
			} else {
				params.height = (int) (getResources().getDisplayMetrics().widthPixels * 0.70);
				params.width = (int) (getResources().getDisplayMetrics().widthPixels);
			}

			iv.setLayoutParams(params);
			try {
				final WallpaperManager wallpaperManager = WallpaperManager
						.getInstance(this);
				final Drawable wallpaperDrawable = wallpaperManager
						.getDrawable();

				Drawable d = new BitmapDrawable(getResources(),
						createPreview(drawableToBitmap(wallpaperDrawable)));
				try {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
						iv.setBackground(d);
					else
						iv.setBackgroundDrawable(d);
				} catch (RuntimeException ex) {
					iv.setBackgroundDrawable(d);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				Log.d(ex.toString());
			}

			value = getIntent().getExtras().getString("extraString");
			// Toast.makeText(getBaseContext(), value,
			// Toast.LENGTH_LONG).show();

			String file = PreferenceHelper.SETTINGS_WIDGET_FILE + awID;
			getPrefs = getBaseContext().getSharedPreferences(file,
					Context.MODE_PRIVATE);

			color1 = getPrefs.getInt("color" + value + 0,
					Color.argb(255, 44, 172, 218));
			color2 = getPrefs.getInt("color" + value + 1,
					Color.argb(255, 24, 82, 112));
			color3 = getPrefs.getInt("color" + value + 2,
					Color.argb(0, 0, 0, 0));
			color4 = getPrefs.getInt("color" + value + 3, 0xffffffff);
			colorShad = getPrefs.getInt("colorShad" + value, Color.BLACK);
			radius = getPrefs.getInt("radiusShad" + value, 8);
			Xoff = getPrefs.getInt("Xoff" + value, 8);
			Yoff = getPrefs.getInt("yOff" + value, 8);

			i1 = (ImageView) findViewById(R.id.ivoutCicle);
			i2 = (ImageView) findViewById(R.id.ivbackCircle);
			i3 = (ImageView) findViewById(R.id.ivinnerCicle);
			i4 = (ImageView) findViewById(R.id.ivtextcolor);
			i5 = (ImageView) findViewById(R.id.ivshadowcolor);

			((SeekBar) findViewById(R.id.sbshadowradius)).setProgress(radius);
			((SeekBar) findViewById(R.id.sbshadowX)).setProgress(Xoff);
			((SeekBar) findViewById(R.id.sbshadowY)).setProgress(Yoff);

			((TextView) findViewById(R.id.tvblurradius))
					.setText("Blur Radius: " + radius);
			((TextView) findViewById(R.id.tvblurX))
					.setText("X-Offset: " + Xoff);
			((TextView) findViewById(R.id.tvblurY))
					.setText("Y-Offset: " + Yoff);

			((SeekBar) findViewById(R.id.sbshadowradius))
					.setOnSeekBarChangeListener(this);
			((SeekBar) findViewById(R.id.sbshadowX))
					.setOnSeekBarChangeListener(this);
			((SeekBar) findViewById(R.id.sbshadowY))
					.setOnSeekBarChangeListener(this);

			((Button) findViewById(R.id.bshadowradiusless))
					.setOnClickListener(this);
			((Button) findViewById(R.id.bshadowradiusmore))
					.setOnClickListener(this);
			((Button) findViewById(R.id.bshadowXless)).setOnClickListener(this);
			((Button) findViewById(R.id.bshadowXmore)).setOnClickListener(this);
			((Button) findViewById(R.id.bshadowYless)).setOnClickListener(this);
			((Button) findViewById(R.id.bshadowYmore)).setOnClickListener(this);

			((CheckBox) findViewById(R.id.cbdropshadow)).setChecked(getPrefs
					.getBoolean("Shadow" + value, false));
			mainchecked = getPrefs.getBoolean("Shadow" + value, false);
			SetVisibility(getPrefs.getBoolean("Shadow" + value, false));

			((CheckBox) findViewById(R.id.cbdropshadow))
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {

							mainchecked = isChecked;
							SetVisibility(isChecked);
							updatePreview();

						}
					});

			RelativeLayout r1, r2, r3, r4, rShad;
			r1 = (RelativeLayout) findViewById(R.id.rloutCircle);
			r2 = (RelativeLayout) findViewById(R.id.rlbackCircle);
			r3 = (RelativeLayout) findViewById(R.id.rlinnerCicle);
			r4 = (RelativeLayout) findViewById(R.id.rltextcolor);
			rShad = (RelativeLayout) findViewById(R.id.rlshadowcolor);

			rShad.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(getBaseContext(),
							MyColorPickerView.class).putExtra("color",
							colorShad));
					getSharedPreferences("saphion.batterycaster_preferences",
							Context.MODE_MULTI_PROCESS).edit()
							.putInt("used", 5).commit();
				}
			});

			r1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(getBaseContext(),
							MyColorPickerView.class).putExtra("color", color1));
					getSharedPreferences("saphion.batterycaster_preferences",
							Context.MODE_MULTI_PROCESS).edit()
							.putInt("used", 1).commit();
				}
			});

			r2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(getBaseContext(),
							MyColorPickerView.class).putExtra("color", color2));
					getSharedPreferences("saphion.batterycaster_preferences",
							Context.MODE_MULTI_PROCESS).edit()
							.putInt("used", 2).commit();
				}
			});

			r3.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(getBaseContext(),
							MyColorPickerView.class).putExtra("color", color3));
					getSharedPreferences("saphion.batterycaster_preferences",
							Context.MODE_MULTI_PROCESS).edit()
							.putInt("used", 3).commit();
				}
			});

			r4.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(getBaseContext(),
							MyColorPickerView.class).putExtra("color", color4));
					getSharedPreferences("saphion.batterycaster_preferences",
							Context.MODE_MULTI_PROCESS).edit()
							.putInt("used", 4).commit();
				}
			});

			updatePreview();
		} else {
			finish();
		}
	}

	public void SetVisibility(boolean bool) {
		if (bool) {

			((WidgetContainer) findViewById(R.id.wcshadow))
					.setVisibility(View.VISIBLE);
		} else {
			((WidgetContainer) findViewById(R.id.wcshadow))
					.setVisibility(View.GONE);
		}
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

	@Override
	protected void onResume() {
		switch (getSharedPreferences("saphion.batterycaster_preferences",
				Context.MODE_MULTI_PROCESS).getInt("used", 6)) {
		case 1:
			if (getSharedPreferences("saphion.batterycaster_preferences",
					Context.MODE_MULTI_PROCESS).getBoolean("fromcolor", false))
				color1 = getSharedPreferences(
						"saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS)

				.getInt("color", Color.argb(255, 44, 172, 218));
			else
				color1 = getPrefs

				.getInt("color" + value + "0", Color.argb(255, 44, 172, 218));
			break;
		case 2:
			if (getSharedPreferences("saphion.batterycaster_preferences",
					Context.MODE_MULTI_PROCESS).getBoolean("fromcolor", false))
				color2 = getSharedPreferences(
						"saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS)

				.getInt("color", Color.argb(255, 44, 172, 218));
			else
				color2 = getPrefs

				.getInt("color" + value + "1", Color.argb(255, 24, 82, 112));
			break;
		case 3:
			if (getSharedPreferences("saphion.batterycaster_preferences",
					Context.MODE_MULTI_PROCESS).getBoolean("fromcolor", false))
				color3 = getSharedPreferences(
						"saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS)

				.getInt("color", Color.argb(255, 44, 172, 218));
			else
				color3 = getPrefs

				.getInt("color" + value + "2", Color.argb(0, 0, 0, 0));
			break;
		case 4:
			if (getSharedPreferences("saphion.batterycaster_preferences",
					Context.MODE_MULTI_PROCESS).getBoolean("fromcolor", false))
				color4 = getSharedPreferences(
						"saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS).getInt("color",
						Color.argb(255, 44, 172, 218));
			else
				color4 = getPrefs

				.getInt("color" + value + "3", 0xffffffff);
			break;
		case 5:
			if (getSharedPreferences("saphion.batterycaster_preferences",
					Context.MODE_MULTI_PROCESS).getBoolean("fromcolor", false))
				colorShad = getSharedPreferences(
						"saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS)

				.getInt("color", Color.BLACK);
			else
				colorShad = getPrefs

				.getInt("colorShad" + value, Color.BLACK);
			break;
		default:
			break;
		}

		try {

			createBack(i1);
			createBack(i2);
			createBack(i3);
			createBack(i4);
			createBack(i5);

			createBit(i1, color1);
			createBit(i2, color2);
			createBit(i3, color3);
			createBit(i4, color4);
			createBit(i5, colorShad);
		} catch (RuntimeException ex) {

			ex.printStackTrace();
		}

		updatePreview();
		getSharedPreferences("saphion.batterycaster_preferences",
				Context.MODE_MULTI_PROCESS).edit().remove("fromcolor").commit();
		super.onResume();
	}

	public void createBit(ImageView iv, int colors) {
		iv.setImageBitmap(NewFunctions.RetBitCol(colors, getBaseContext()));
	}

	@SuppressLint("AlwaysShowAction")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem ok = menu.add("OK");
		ok.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		ok.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				getPrefs.edit().putInt("color" + value + "0", color1).commit();
				getPrefs.edit().putInt("color" + value + "1", color2).commit();
				getPrefs.edit().putInt("color" + value + "2", color3).commit();
				getPrefs.edit().putInt("color" + value + "3", color4).commit();
				getPrefs.edit().putInt("colorShad" + value, colorShad).commit();
				getPrefs.edit().putInt("radiusShad" + value, radius).commit();
				getPrefs.edit().putInt("Xoff" + value, Xoff).commit();
				getPrefs.edit().putInt("yOff" + value, Yoff).commit();
				getPrefs.edit().putBoolean("Shadow" + value, mainchecked)
						.commit();

				finish();
				return true;
			}
		});

		MenuItem cancel = menu.add("Cancel");
		cancel.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		cancel.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				finish();
				return true;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void createBack(ImageView iv) {
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
				iv.setBackground(NewFunctions
						.generatePatternBitmap(getBaseContext()));
			else
				iv.setBackgroundDrawable(NewFunctions
						.generatePatternBitmap(getBaseContext()));
		} catch (RuntimeException ex) {
			iv.setBackgroundDrawable(NewFunctions
					.generatePatternBitmap(getBaseContext()));
		}
	}

	public void updatePreview() {

		ImageView iv = (ImageView) findViewById(R.id.ivbatteryPreview);

		if (WidgetTheme == 4) {
			iv.setImageBitmap(getNewCircle(72, getBaseContext(), getPrefs));
			return;
		}

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.dialback);

		Bitmap circleBitmap = Bitmap.createBitmap(
				(int) (bitmap.getWidth() * 1.5),
				(int) (bitmap.getHeight() * 1.5), Config.ARGB_8888);

		Bitmap cfoSize;
		if (mainchecked)
			cfoSize = Bitmap.createBitmap((int) (bitmap.getWidth() * 1.38),
					(int) (bitmap.getHeight() * 1.38), Config.ARGB_8888);
		else
			cfoSize = circleBitmap;

		BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP,
				TileMode.CLAMP);
		Paint paint = new Paint();
		paint.setShader(shader);
		paint.setAlpha(220);
		paint.setAntiAlias(true);
		if (mainchecked)
			paint.setShadowLayer(radius, Xoff, Yoff, colorShad);

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
		if (mainchecked)
			mypaint.setShadowLayer(radius, Xoff, Yoff, colorShad);

		mypaint.setStrokeWidth(LineBack);/*
										 * getPrefs.getFloat("lineback", (float)
										 * (cfoSize.getWidth() * 0.0983)));
										 */

		mypaint.setStyle(Style.STROKE);
		mypaint.setAntiAlias(true);

		mypaint.setColor(color2);

		float left = (float) (cfoSize.getWidth() * 0.05);
		float top = (float) (cfoSize.getWidth() * 0.05);
		float right = cfoSize.getWidth() - (float) (cfoSize.getHeight() * 0.05);
		float bottom = cfoSize.getHeight()
				- (float) (cfoSize.getWidth() * 0.05);

		RectF rectf = new RectF(left, top, right, bottom);

		float angle = 60 * 360;
		angle = angle / 100;

		switch (WidgetTheme) {
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

		mypaint.setStrokeWidth(LineLeft);/*
										 * getPrefs.getFloat("lineleft", (float)
										 * (cfoSize.getWidth() * 0.0983)));
										 */

		mypaint.setColor(color1);

		switch (WidgetTheme) {

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
				angle = angle - Functions.Magnitude(angle);
				c.drawArc(rectf, -88 + ((i - 1) * 11) + (i - 1) * 4, angle,
						false, mypaint);
			}
			break;
		}

		mypaint = new Paint();
		mypaint.setColor(color4);
		mypaint.setAntiAlias(true);
		if (mainchecked)
			mypaint.setShadowLayer(radius, Xoff, Yoff, colorShad);
		float size = FontSize;/*
							 * getPrefs.getFloat("batfontsize", (float)
							 * (bitmap.getWidth() * 0.48));
							 */

		mypaint.setTextSize(size);
		mypaint.setTextAlign(Align.CENTER);
		Typeface tf = Typeface.DEFAULT_BOLD;
		try {
			String stf = Font;// getPrefs.getString("fontBatteryWidget",
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
		switch (widget_text) {
		case 0:
			mText = "72%";
			break;
		case 1:
			mText = "72";
			break;
		case 2:
			mText = "21h";
			break;
		case 3:
			mText = "21";
			break;
		case 4:
			mText = Functions.updateTemperature(21f, true, false);
			break;
		case 5:
			mText = Functions.updateTemperature(21f, false, false);
			break;
		}
		c.drawText(
				mText,
				(float) (cfoSize.getWidth() / 2),
				(float) (cfoSize.getHeight() * 0.5 + (mypaint.getFontSpacing() / 3.7)),
				mypaint);

		iv.setImageBitmap(circleBitmap);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.sbshadowradius:
			radius = progress;
			break;
		case R.id.sbshadowX:
			Xoff = progress;
			break;
		case R.id.sbshadowY:
			Yoff = progress;
			break;
		}

		((TextView) findViewById(R.id.tvblurradius)).setText("Blur Radius: "
				+ radius);
		((TextView) findViewById(R.id.tvblurX)).setText("X-Offset: " + Xoff);
		((TextView) findViewById(R.id.tvblurY)).setText("Y-Offset: " + Yoff);

		updatePreview();

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bshadowradiusless:
			if (radius != 0)
				radius--;

			break;
		case R.id.bshadowradiusmore:
			if (radius != 20)
				radius++;
			break;
		case R.id.bshadowXless:
			if (Xoff != 0)
				Xoff--;
			break;
		case R.id.bshadowXmore:
			if (Xoff != 20)
				Xoff++;
			break;
		case R.id.bshadowYless:
			if (Yoff != 0)
				Yoff--;
			break;
		case R.id.bshadowYmore:
			if (Yoff != 20)
				Yoff++;
			break;

		}
		((SeekBar) findViewById(R.id.sbshadowradius)).setProgress(radius);
		((SeekBar) findViewById(R.id.sbshadowX)).setProgress(Xoff);
		((SeekBar) findViewById(R.id.sbshadowY)).setProgress(Yoff);

		((TextView) findViewById(R.id.tvblurradius)).setText("Blur Radius: "
				+ radius);
		((TextView) findViewById(R.id.tvblurX)).setText("X-Offset: " + Xoff);
		((TextView) findViewById(R.id.tvblurY)).setText("Y-Offset: " + Yoff);

		updatePreview();

	}

	public Bitmap getNewCircle(int mLevel, Context mContext,
			SharedPreferences getPrefs) {

		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.dialback);

		Bitmap circleBitmap = Bitmap.createBitmap(
				(int) (bitmap.getWidth() * 1.5),
				(int) (bitmap.getHeight() * 1.5), Config.ARGB_8888);

		Bitmap cfoSize;
		if (mainchecked)
			cfoSize = Bitmap.createBitmap((int) (bitmap.getWidth() * 1.38),
					(int) (bitmap.getHeight() * 1.38), Config.ARGB_8888);
		else
			cfoSize = circleBitmap;

		BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP,
				TileMode.CLAMP);
		Paint paint = new Paint();
		paint.setShader(shader);
		paint.setAlpha(220);
		paint.setAntiAlias(true);

		if (mainchecked)
			paint.setShadowLayer(radius, Xoff, Yoff, colorShad);

		Canvas c = new Canvas(circleBitmap);
		/*
		 * c.drawCircle( bitmap.getWidth() / 2, bitmap.getHeight() / 2, (float)
		 * ((bitmap.getWidth() / 2) - (0.24 * (bitmap.getWidth() / 2))), paint);
		 */
		Paint mpaint = new Paint();
		mpaint.setColor(color3);
		mpaint.setAlpha(Color.alpha(color3));
		mpaint.setAntiAlias(true);

		c.drawCircle(
				cfoSize.getWidth() / 2,
				cfoSize.getHeight() / 2,
				(float) ((cfoSize.getWidth() / 2) - (0.08 * (cfoSize.getWidth() / 2))),
				mpaint);

		Paint mypaint = new Paint();
		mypaint.setAntiAlias(true);
		if (mainchecked)
			mypaint.setShadowLayer(radius, Xoff, Yoff, colorShad);

		mypaint.setStrokeWidth(getPrefs.getFloat("lineback",

		(float) (cfoSize.getWidth() * 0.01)));

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

		mypaint.setStrokeWidth(getPrefs.getFloat("lineleft",
				(float) (cfoSize.getWidth() * 0.0183)));

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
		mypaint = new Paint();
		mypaint.setColor(color4);
		mypaint.setAntiAlias(true);
		if (mainchecked)
			mypaint.setShadowLayer(radius, Xoff, Yoff, colorShad);
		float size = getPrefs.getFloat("batfontsize",
				(float) (bitmap.getWidth() * 0.48));

		mypaint.setTextSize(size);
		mypaint.setTextAlign(Align.CENTER);
		Typeface tf = Typeface.DEFAULT_BOLD;
		try {
			String stf = Font;// getPrefs.getString("fontBatteryWidget",
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
		switch (widget_text) {
		case 0:
			mText = mLevel + "%";
			break;
		case 1:
			mText = mLevel + "";
			break;
		case 2:
			mText = "21h";
			break;
		case 3:
			mText = "21";
			break;
		case 4:
			mText = Functions.updateTemperature(21f, true, false);
			break;
		case 5:
			mText = Functions.updateTemperature(21f, false, false);
			break;
		}
		c.drawText(
				mText,
				(float) (cfoSize.getWidth() / 2),
				(float) (cfoSize.getHeight() * 0.5 + (mypaint.getFontSpacing() / 3.7)),
				mypaint);

		return circleBitmap;
	}

}
