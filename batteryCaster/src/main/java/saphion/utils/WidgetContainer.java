package saphion.utils;

import saphion.batterycaster.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WidgetContainer extends LinearLayout {
	private View titleView;

	public WidgetContainer(Context context) {
		this(context, null);
	}

	public WidgetContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(android.widget.LinearLayout.VERTICAL);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.WidgetContainer);
		int layout = a.getResourceId(
				R.styleable.WidgetContainer_android_layout, -1);
		int titleLayout = a.getResourceId(
				R.styleable.WidgetContainer_titleLayout,
				R.layout.widget_container_title);
		String title = a.getString(R.styleable.WidgetContainer_android_text);
		a.recycle();
		if (layout > 0) {
			View view = LayoutInflater.from(context).inflate(layout, null);
			if (view instanceof ViewGroup) { 
				ViewGroup group = (ViewGroup) view;
				for (int i = 0; i < group.getChildCount(); i++) {
					View v = group.getChildAt(i);
					group.removeViewAt(i);
					addView(v);
				}
			} else {
				addView(view);
			}
		}
		addView(titleView = LayoutInflater.from(context).inflate(titleLayout,
				null), 0);
		if (title != null && title.length() > 0) {
			setTitle(title);
		}
	}

	public WidgetContainer(Context context, AttributeSet attrs, int defStyleAttr) {
		this(context, attrs);
	}

	public void setTitle(CharSequence title) {
		TextView textView = (TextView) titleView
				.findViewById(android.R.id.text1);
		if (textView != null) {
			textView.setText(title);
		}
	}

	public void setTitle(int title) {
		setTitle(getResources().getText(title));
	}
}