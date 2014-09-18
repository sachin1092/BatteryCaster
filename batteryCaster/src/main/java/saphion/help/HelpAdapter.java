package saphion.help;

import saphion.batterycaster.R;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

class HelpAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
	protected static final String[] CONTENT = new String[] { "Welcome", "Tabs",
			"Info Tab", "Alarm Tab", "Power Tab", "Notification", "Widgets",
			"Let's start" };

	private int mCount = CONTENT.length;

	public HelpAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return HelpFragment.newInstance(position);
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return HelpAdapter.CONTENT[position % CONTENT.length];
	}

	public void setCount(int count) {
		if (count > 0 && count <= 10) {
			mCount = count;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getIconResId(int index) {
		return R.drawable.about;
	}
}