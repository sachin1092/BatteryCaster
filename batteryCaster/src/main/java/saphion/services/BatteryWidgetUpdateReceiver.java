package saphion.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BatteryWidgetUpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		int awID = intent.getIntExtra("myappid", 0);
		context.startService(new Intent(context, UpdateService.class).putExtra(
				"name", "One").putExtra("myappid", awID));
		//Toast.makeText(context, "Received", Toast.LENGTH_LONG).show();

	}

}