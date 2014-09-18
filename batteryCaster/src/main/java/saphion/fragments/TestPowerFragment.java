package saphion.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("AlwaysShowAction")
public final class TestPowerFragment extends Fragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onPause() {
		
		super.onPause();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onResume() {
		
		super.onResume();
	}

	/*@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem newAlarm = menu.add("New Alarm");
		newAlarm.setIcon(R.drawable.add);
		newAlarm.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
		newAlarm.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {

				mAdapter.add(mAdapter.getCount(), new PowerProfItems());
				mAdapter.notifyDataSetChanged();
				PowerPreference.savePower(getBaseContext(), mAdapter.getAll());

				return true;
			}
		});
		super.onCreateOptionsMenu(menu, inflater);
	}
*/
	private static final String KEY_CONTENT = "TestFragment:Content";

	public static TestPowerFragment newInstance(String content) {
		TestPowerFragment fragment = new TestPowerFragment();

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 20; i++) {
			builder.append(content).append(" ");
		}
		builder.deleteCharAt(builder.length() - 1);
		fragment.mContent = builder.toString();

		return fragment;
	}

	private String mContent = "???";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ((savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT)) {
			mContent = savedInstanceState.getString(KEY_CONTENT);
		}
	}
	View k;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		k = new TextView(getActivity());


		return k;
	}

}
