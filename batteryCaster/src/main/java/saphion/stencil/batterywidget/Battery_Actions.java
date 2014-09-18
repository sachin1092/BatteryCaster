package saphion.stencil.batterywidget;

import java.io.File;

import saphion.services.BatteryWidgetUpdateReceiver;
import saphion.services.UpdateService;
import saphion.utils.PreferenceHelper;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class Battery_Actions extends AppWidgetProvider {

	private static Intent mIntent = null;

	@Override
	public void onDisabled(Context context) {
		if (mIntent == null) {
			mIntent = new Intent(context, UpdateService.class);
		}
		context.stopService(mIntent);
	}



	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		if (mIntent == null) {
			mIntent = new Intent(context, UpdateService.class);
		}
		context.stopService(mIntent);
		
		for (int i : appWidgetIds) {
            String file = PreferenceHelper.SETTINGS_WIDGET_FILE + i;
            context.getSharedPreferences(file, Context.MODE_PRIVATE).edit().clear().commit();
            new File(context.getFilesDir() + "/../shared_prefs/" + file + ".xml").delete();
        }
		
		PendingIntent sen2 = PendingIntent.getBroadcast(context, 0, new Intent(
				context, BatteryWidgetUpdateReceiver.class), 0);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.cancel(sen2);
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		if (mIntent == null) {
			mIntent = new Intent(context, UpdateService.class);
		}
		mIntent.putExtra(PreferenceHelper.EXTRA_WIDGET_IDS, appWidgetIds);
		context.startService(mIntent);

	}

}