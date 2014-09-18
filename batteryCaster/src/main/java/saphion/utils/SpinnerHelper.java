package saphion.utils;

import android.app.Activity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public abstract class SpinnerHelper {

	public SpinnerHelper(Activity mActivity, View menuButton, int choice,
			String[] mArray) {
		setupFakeSpinner(mActivity, menuButton, choice, mArray);
	}

	/**
	 * Installs click and touch listeners on a fake overflow menu button.
	 * 
	 * @param menuButton
	 *            the fragment's fake overflow menu button
	 */
	public void setupFakeSpinner(final Activity mActivity, View menuButton,
			int choice, final String[] mArray) {

		final TextView tv = (TextView) menuButton;
		tv.setText(mArray[choice]);

		final PopupMenu fakeOverflow = new PopupMenu(menuButton.getContext(),
				menuButton) {
			@Override
			public void show() {
				mActivity.onPrepareOptionsMenu(getMenu());
				super.show();
			}
		};

		Menu menu = fakeOverflow.getMenu();// .inflate(R.menu.bmenu);
		for (int i = 0; i < mArray.length; i++) {
			menu.add(mArray[i]);
		}

		fakeOverflow
				.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						int pos = indexOf(mArray, item.getTitle());
						/*
						 * mPref.edit().putInt(pref, pos).commit();
						 * tv.setText(item.getTitle());
						 */
						tv.setText(item.getTitle());
						onItemSelected(pos);
						return true;
					}
				});

		menuButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fakeOverflow.show();
			}
		});
	}

	protected int indexOf(String[] mArray, CharSequence title) {
		for (int i = 0; i < mArray.length; i++) {
			if (mArray[i].equals(title)) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * Called when an item is selected from spinner
	 */
	public abstract void onItemSelected(int position);

}
