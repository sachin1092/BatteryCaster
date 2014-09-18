package saphion.fragment.powerfragment;

import saphion.batterycaster.R;
import saphion.utils.ToggleHelper;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageButton;

public class ToggleWifi extends AsyncTask<Void, Boolean, Boolean> {

	Context mContext;
	ImageButton ib;
 
	public ToggleWifi(Context mContext, ImageButton ib) {
		this.mContext = mContext;
		this.ib = ib;
	}

	@Override
	protected void onPreExecute() {
		if (ToggleHelper.isWifiEnabled(mContext)) {
			ib.setImageResource(R.drawable.wifi_off);
		} else {
			ib.setImageResource(R.drawable.wifi_on);
		}
		super.onPreExecute(); 
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			ToggleHelper.toggleWifi(mContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
