package saphion.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		//if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
			//	PreferenceHelper.NOTIFICATION_ENABLE, true)) {
			Intent intent = new Intent(ForegroundService.ACTION_FOREGROUND);
			intent.setClass(context, ForegroundService.class);
			context.startService(intent);
		//}

	}

}
