/*
 * Copyright (C) 2011 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package saphion.pageindicators;

import java.util.ArrayList;

import saphion.batterycaster.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FixedTabsView extends LinearLayout implements
		ViewPager.OnPageChangeListener {

	@SuppressWarnings("unused")
	private static final String TAG = "com.astuetz.viewpager.extensions";

	private Context mContext;

	private ViewPager mPager;

	private TabsAdapter mAdapter;

	private ArrayList<View> mTabs = new ArrayList<View>();

	private Drawable mDividerDrawable;

	private int mDividerColor = 0xFF636363;
	private int mDividerMarginTop = 12;
	private int mDividerMarginBottom = 21;
	private int mCurrentPosition = 1;

	public FixedTabsView(Context context) {
		this(context, null);
	}

	public FixedTabsView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FixedTabsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);

		this.mContext = context;

		final TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ViewPagerExtensions, defStyle, 0);

		mDividerColor = a.getColor(
				R.styleable.ViewPagerExtensions_dividerColor, mDividerColor);

		mDividerMarginTop = a.getDimensionPixelSize(
				R.styleable.ViewPagerExtensions_dividerMarginTop,
				mDividerMarginTop);
		mDividerMarginBottom = a.getDimensionPixelSize(
				R.styleable.ViewPagerExtensions_dividerMarginBottom,
				mDividerMarginBottom);

		mDividerDrawable = a
				.getDrawable(R.styleable.ViewPagerExtensions_dividerDrawable);

		a.recycle();

		this.setOrientation(LinearLayout.HORIZONTAL);
	}

	/**
	 * Sets the data behind this FixedTabsView.
	 * 
	 * @param adapter
	 *            The {@link TabsAdapter} which is responsible for maintaining
	 *            the data backing this FixedTabsView and for producing a view
	 *            to represent an item in that data set.
	 */
	public void setAdapter(TabsAdapter adapter) {
		this.mAdapter = adapter;

		if (mPager != null && mAdapter != null)
			initTabs();
	}

	/**
	 * Binds the {@link ViewPager} to this View
	 * 
	 */
	public void setViewPager(ViewPager pager) {
		this.mPager = pager;
		mPager.setOnPageChangeListener(this);

		if (mPager != null && mAdapter != null)
			initTabs();
	}

	/**
	 * Initialize and add all tabs to the layout
	 */
	private void initTabs() {

		removeAllViews();
		mTabs.clear();

		if (mAdapter == null)
			return;

		for (int i = 0; i < mPager.getAdapter().getCount(); i++) {

			final int index = i;

			View tab = mAdapter.getView(i);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
					LayoutParams.WRAP_CONTENT, 1.0f);
			tab.setLayoutParams(params);
			this.addView(tab);

			mTabs.add(tab);

			if (i != mPager.getAdapter().getCount() - 1) {
				this.addView(getSeparator());
			}

			tab.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mPager.setCurrentItem(index);
				}
			});
			
			tab.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					//Toast t = new Toast(getContext());
					Toast t = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
					//t.setGravity(Gravity.TOP, v.getRight()/2 - v.getLeft()/2, v.getBottom());
					switch(index){
					case 0:
						t.setText("Power Profiles");
						t.setGravity(Gravity.TOP | Gravity.LEFT, v.getLeft(), v.getBottom());
						break;
					case 1:
						t.setText("Battery Info");
						t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,0, v.getBottom());
						break;
					case 2:
						t.setText("Graph and Stats");
						t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,0, v.getBottom());
						break;
					case 3:
						t.setText("Battery Alarms");
						t.setGravity(Gravity.TOP, v.getLeft(), v.getBottom());
						break;
						
					}
					t.setDuration(Toast.LENGTH_SHORT);
					t.show();
					return true;
				}
			});

		}

		selectTab(mPager.getCurrentItem());
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		selectTab(position);
		mCurrentPosition = position;
	}
	
	public int getPosition(){
		return mCurrentPosition;
	}
	
	public void setPosition(int mPos){
		mCurrentPosition = mPos;
	}


	/**
	 * Creates and returns a new Separator View
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private View getSeparator() {
		View v = new View(mContext);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1,
				LayoutParams.MATCH_PARENT);
		params.setMargins(0, mDividerMarginTop, 0, mDividerMarginBottom);
		v.setLayoutParams(params);

		if (mDividerDrawable != null)
			v.setBackgroundDrawable(mDividerDrawable);
		else
			v.setBackgroundColor(mDividerColor);

		return v;
	}

	/**
	 * Runs through all tabs and sets if they are currently selected.
	 * 
	 * @param position
	 *            The position of the currently selected tab.
	 */
	private void selectTab(int position) {

		// Log.Toast(getContext(), "" + position, Toast.LENGTH_LONG);

		((TextView) mTabs.get(position))
				.setCompoundDrawablesWithIntrinsicBounds(null, mContext
						.getResources().getDrawable(getIcon(position, true)),
						null, null);

		for (int i = 0; i < 4; i++)
			if (i != position)
				((TextView) mTabs.get(i))
						.setCompoundDrawablesWithIntrinsicBounds(null, mContext
								.getResources().getDrawable(getIcon(i, false)),
								null, null);

		for (int i = 0, pos = 0; i < getChildCount(); i++) {

			if (this.getChildAt(i) instanceof ViewPagerTabButton) {
				this.getChildAt(i).setSelected(pos == position);

				pos++;
			}

		}
	}

	private int getIcon(int i, boolean b) {
		if (b)
			switch (i) {
			case 0:
				return R.drawable.power;
			case 1:
				return R.drawable.battery_icon_activated;
			case 2:
				return R.drawable.graph_icon_activated;
			case 3:
				return R.drawable.alarm;
			}
		else
			switch (i) {
			case 0:
				return R.drawable.power_normal;
			case 1:
				return R.drawable.battery_icon_normal1;
			case 2:
				return R.drawable.graph_icon_normal;
			case 3:
				return R.drawable.alarm_normal;
			}

		return 0;
	}

}
