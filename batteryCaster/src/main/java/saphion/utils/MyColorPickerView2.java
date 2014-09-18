package saphion.utils;

import saphion.batterycaster.R;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.saphion.colorpicker.ColorPickerPanelView;
import com.saphion.colorpicker.ColorPickerView;

public class MyColorPickerView2 extends ActionBarActivity implements
		ColorPickerView.OnColorChangedListener, View.OnClickListener {

	private ColorPickerView mColorPicker;

	private ColorPickerPanelView mOldColor;
	private ColorPickerPanelView mNewColor;

	private OnColorChangedListener mListener;

	String key = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.setContentView(R.layout.dialog_color_picker);
		super.onCreate(savedInstanceState);

		if (getIntent().hasExtra("color")
				&& getIntent().hasExtra("extraString")) {
			init(getIntent().getIntExtra("color", 0xff33b5e5));
			key = "color" + getIntent().getStringExtra("extraString");
		} else {
			finish();
		}

	}

	public interface OnColorChangedListener {
		public void onColorChanged(int color);
	}

	public void onColorChanged(int color) {
		mNewColor.setColor(color);

		/*
		 * if (mListener != null) { mListener.onColorChanged(color); }
		 */
	}

	private void init(int color) {
		// To fight color banding.
		getWindow().setFormat(PixelFormat.RGBA_8888);

		setUp(color);

	}

	private void setUp(int color) {

		mColorPicker = (ColorPickerView) findViewById(R.id.color_picker_view);
		mOldColor = (ColorPickerPanelView) findViewById(R.id.old_color_panel);
		mNewColor = (ColorPickerPanelView) findViewById(R.id.new_color_panel);

		((LinearLayout) mOldColor.getParent()).setPadding(
				Math.round(mColorPicker.getDrawingOffset()), 0,
				Math.round(mColorPicker.getDrawingOffset()), 0);

		mOldColor.setOnClickListener(this);
		mNewColor.setOnClickListener(this);
		mColorPicker.setOnColorChangedListener(this);
		mOldColor.setColor(color);
		mColorPicker.setColor(color, true);
		setAlphaSliderVisible(true);

	}

	public void setAlphaSliderVisible(boolean visible) {
		mColorPicker.setAlphaSliderVisible(visible);
	}

	/**
	 * Set a OnColorChangedListener to get notified when the color selected by
	 * the user has changed.
	 * 
	 * @param listener
	 */
	public void setOnColorChangedListener(OnColorChangedListener listener) {
		mListener = listener;
	}

	public int getColor() {
		return mColorPicker.getColor();
	}

	public void onClick(View v) {
		if (v.getId() == R.id.new_color_panel) {
			if (mListener != null) {
				mListener.onColorChanged(mNewColor.getColor());
			}
			getSharedPreferences("saphion.batterycaster_preferences",
					Context.MODE_MULTI_PROCESS).edit()
					.putInt(key, mNewColor.getColor()).commit();

		}
		finish();
	}

}
