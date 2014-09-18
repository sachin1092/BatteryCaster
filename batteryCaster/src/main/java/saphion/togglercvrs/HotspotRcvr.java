package saphion.togglercvrs;

import saphion.fragment.alarm.alert.Intents;
import saphion.togglehelper.hotspot.WifiApManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HotspotRcvr extends BroadcastReceiver {

	@Override
	public void onReceive(Context mContext, Intent intent) {

		try {
			//ToggleHelper.toggleWifi(mContext);
			WifiApManager wam = new WifiApManager(mContext);
			wam.toggleHotspot(mContext);
			mContext.sendBroadcast(new Intent(
					Intents.SWITCHER_NOTI));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
