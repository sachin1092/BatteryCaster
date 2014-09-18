package saphion.togglercvrs;

import saphion.fragment.alarm.alert.Intents;
import saphion.utils.ToggleHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ArotateRcvr extends BroadcastReceiver {

	@Override
	public void onReceive(Context mContext, Intent intent) {
		try {
			ToggleHelper.toggleRotation(mContext);
			mContext.sendBroadcast(new Intent(Intents.SWITCHER_NOTI));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
