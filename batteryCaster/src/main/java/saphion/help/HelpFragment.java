package saphion.help;

import saphion.batterycaster.R;
import saphion.fragment.alarm.alert.Intents;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public final class HelpFragment extends Fragment {

	View k;

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		TextView text = new TextView(getActivity());
		text.setGravity(Gravity.CENTER);
		text.setText(mContent + "");
		text.setTextSize(20 * getResources().getDisplayMetrics().density);
		text.setPadding(20, 20, 20, 20);

		LinearLayout layout = new LinearLayout(getActivity());
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		layout.setGravity(Gravity.CENTER);
		layout.addView(text);
		Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
				"cnlbold.ttf");
		switch (mContent) {
		case 0:
			k = inflater.inflate(R.layout.welcome, container, false);
			welcome();
			break;
		case 1:
			k = inflater.inflate(R.layout.otherhelpfrag, container, false);
			((TextView) k.findViewById(R.id.tvtabstext)).setTypeface(tf);
			break;
		case 2:
			k = inflater.inflate(R.layout.compound_tabs, container, false);
			((TextView) k.findViewById(R.id.tvcomptabstext1)).setTypeface(tf);
			break;
		case 3:
			k = inflater.inflate(R.layout.alarm_help, container, false);
			((TextView) k.findViewById(R.id.tvalarmtabstext1)).setTypeface(tf);
			((TextView) k.findViewById(R.id.tvalarmtabstext2)).setTypeface(tf);
			((TextView) k.findViewById(R.id.tvalarmtabstext3)).setTypeface(tf);
			break;
		case 4:
			k = inflater.inflate(R.layout.power_frag_help, container, false);
			((TextView) k.findViewById(R.id.tvpowertabstext1)).setTypeface(tf);
			((TextView) k.findViewById(R.id.tvpowertabstext2)).setTypeface(tf);
			((TextView) k.findViewById(R.id.tvpowertabstext3)).setTypeface(tf);
			break;
		case 5:
			k = inflater.inflate(R.layout.notification_help, container, false);
			((TextView) k.findViewById(R.id.tvnotitabstext1)).setTypeface(tf);
			break;
		case 6:
			k = inflater.inflate(R.layout.widgets_help, container, false);
			((TextView) k.findViewById(R.id.tvwidgettabstext1)).setTypeface(tf);
			break;
		case 7:
			k = inflater.inflate(R.layout.starthelp, container, false);
			((TextView) k.findViewById(R.id.tvStart1)).setTypeface(tf);
			((TextView) k.findViewById(R.id.tvStart2)).setTypeface(tf);
			((Button) k.findViewById(R.id.bStart)).setTypeface(tf);
			((Button) k.findViewById(R.id.bStart))
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							k.getContext().sendBroadcast(
									new Intent(Intents.FINISH_INTENT));
						}
					});
			break;
		default:
			return layout;
		}

		return k;
	}

	private void welcome() {
		Typeface tf = Typeface.createFromAsset(k.getContext().getAssets(),
				"cnlbold.ttf");
		((TextView) k.findViewById(R.id.tvwelcomeName)).setTypeface(tf);
		((TextView) k.findViewById(R.id.tvwelcomeSwipe)).setTypeface(tf);
		((TextView) k.findViewById(R.id.tvwelcometext)).setTypeface(tf);
		((Button) k.findViewById(R.id.bwelcome)).setTypeface(tf);
		((Button) k.findViewById(R.id.bwelcome))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						k.getContext().sendBroadcast(
								new Intent(Intents.FINISH_INTENT));
					}
				});

	}

	private static final String KEY_CONTENT = "TestFragment:Content";

	public static HelpFragment newInstance(int content) {
		HelpFragment fragment = new HelpFragment();

		/*
		 * StringBuilder builder = new StringBuilder(); for (int i = 0; i < 20;
		 * i++) { builder.append(content).append(" "); }
		 * builder.deleteCharAt(builder.length() - 1);
		 */
		fragment.mContent = content;

		return fragment;
	}

	private int mContent = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ((savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT)) {
			mContent = savedInstanceState.getInt(KEY_CONTENT);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_CONTENT, mContent);
	}
}
