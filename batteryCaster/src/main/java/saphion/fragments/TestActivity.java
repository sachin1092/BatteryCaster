package saphion.fragments;

import java.util.List;

import saphion.batterycaster.R;
import saphion.batterycaster.providers.Alarm;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

public class TestActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.about);

		List<Alarm> mAlarms = Alarm.getAlarms(getContentResolver(),
				Alarm.BATTERY + " = ? ", new String[] { "" + 100 });
		String mString = "";
		for (int i = 0; i < mAlarms.size(); i++) {
			mString = mString + "\n" + "Level: " + mAlarms.get(i).battery
					+ " Label: " + mAlarms.get(i).label + " ID: " + mAlarms.get(i).id;
			List<Alarm> mAlarms1 = Alarm.getAlarms(getContentResolver(),
					Alarm._ID + " = ? ", new String[] { "" + mAlarms.get(i).id });
			Toast.makeText(getBaseContext(), "" + mAlarms1.size(), Toast.LENGTH_LONG).show();
		}
		Toast.makeText(getBaseContext(), mString, Toast.LENGTH_LONG).show();
	}

}
