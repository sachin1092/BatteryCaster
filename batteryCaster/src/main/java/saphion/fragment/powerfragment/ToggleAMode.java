package saphion.fragment.powerfragment;

import saphion.batterycaster.R;
import saphion.utils.ActivityFuncs;
import saphion.utils.ToggleHelper;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageButton;

public class ToggleAMode extends AsyncTask<Void, Boolean, Boolean> {

	Context mContext;
	ImageButton ib;

	public ToggleAMode(Context mContext, ImageButton ib) {
		this.mContext = mContext;
		this.ib = ib;
	}

	@Override
	protected void onPreExecute() {
		if (ToggleHelper.isAModeEnabled(mContext)) {
			ib.setImageResource(R.drawable.amode_on);
		} else {
			ib.setImageResource(R.drawable.amode_off);
		}
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			ToggleHelper.toggleAirplane(mContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
