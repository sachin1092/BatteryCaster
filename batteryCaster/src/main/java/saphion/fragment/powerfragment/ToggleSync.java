package saphion.fragment.powerfragment;

import saphion.batterycaster.R;
import saphion.fragment.alarm.alert.Intents;
import saphion.utils.ActivityFuncs;
import saphion.utils.ToggleHelper;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.widget.ImageButton;

public class ToggleSync extends AsyncTask<Void, Boolean, Boolean> {

	Context mContext;
	ImageButton ib;

	public ToggleSync(Context mContext, ImageButton ib) {
		this.mContext = mContext;
		this.ib = ib;
	}

	@Override
	protected void onPreExecute() {
		if (ToggleHelper.isSyncEnabled()) {
			ib.setImageResource(R.drawable.sync_on);
		} else {
			ib.setImageResource(R.drawable.sync_off);
		}
		
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			ToggleHelper.toggleSync();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		mContext.sendBroadcast(new Intent(Intents.SWITCHER_NOTI));
		if (ToggleHelper.isSyncEnabled()) {
			ib.setImageResource(R.drawable.sync_on);
		} else {
			ib.setImageResource(R.drawable.sync_off);
		}
		
		super.onPostExecute(result);
	}
	

}
