package saphion.fragment.powerfragment;

import saphion.batterycaster.R;
import saphion.fragment.alarm.alert.Intents;
import saphion.utils.ToggleHelper;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageButton;

public class ToggleBness {

	public void toggle() {
		try {
			ToggleHelper.toggleAutoBrightness(mContext, window);
		} catch (Exception e) {
			e.printStackTrace();
		}
		(new Handler()).postDelayed(new Runnable() {

			@Override
			public void run() {
				mContext.sendBroadcast(new Intent(Intents.SWITCHER_NOTI));
				switch (ToggleHelper.getBrightnessMode(mContext)) {
				case 1:
					ib.setImageResource(R.drawable.blow_on);
					break;
				case 2:
					ib.setImageResource(R.drawable.bhalf_on);
					break;
				case 3:
					ib.setImageResource(R.drawable.bfull_on);
					break;
				case 0:
					ib.setImageResource(R.drawable.bauto_on);
					break;
				}

			};
		}, 300);
	}

	Context mContext;
	ImageButton ib;
	Window window;

	public ToggleBness(Context mContext, ImageButton ib, Window window) {

		this.mContext = mContext;
		this.ib = ib;
		this.window = window;
		// getWindow().setAttributes(lp);
	}

}
