/*
 * Copyright (C) 2007 The Android Open Source Project
 * Copyright (C) 2012 Yuriy Kulikov yuriy.kulikov.87@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package saphion.fragment.alarm.alert;

import saphion.batterycaster.R;
import saphion.utils.PreferenceHelper;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

/**
 * Glue class: connects AlarmAlert IntentReceiver to AlarmAlert activity. Passes
 * through Alarm ID.
 */
public class AlarmAlertReceiver extends BroadcastReceiver {

	public final static int ID = -999;
	int level = 0;

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(final Context context, final Intent intent) {
		String action = intent.getAction();
		int pos = intent.getIntExtra(PreferenceHelper.BAT_VALS, 0);
		level = intent.getIntExtra(PreferenceHelper.CURR_RING, 72);
		
		//Log.Toast(context, level + " in receiver", Toast.LENGTH_LONG);
		// int id = intent.getIntExtra(Intents.EXTRA_ID, -1);

		if (action.equals(Intents.ALARM_ALERT_ACTION)) {
			/* Close dialogs and window shade */
			Intent closeDialogs = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
			context.sendBroadcast(closeDialogs);

			// Decide which activity to start based on the state of the
			// keyguard.
			/*
			 * KeyguardManager km = (KeyguardManager)
			 * context.getSystemService(Context.KEYGUARD_SERVICE); if
			 * (km.inKeyguardRestrictedInputMode()) { // Use the full screen
			 * activity for security. c = AlarmAlertFullScreen.class; }
			 */

			// Trigger a notification that, when clicked, will show the alarm
			// alert
			// dialog. No need to check for fullscreen since this will always be
			// launched from a user action.

			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				Intent notify = new Intent(context,
						saphion.fragments.alarm.AlarmAlert.class);
				notify.putExtra(PreferenceHelper.BAT_VALS, pos);
				PendingIntent pendingNotify = PendingIntent.getActivity(
						context, ID, notify, 0);

				// Alarm alarm = AlarmsManager.getAlarmsManager().getAlarm(id);
				Notification n = new Notification(R.drawable.stat_notify_alarm,
						"abc", System.currentTimeMillis());
				n.setLatestEventInfo(context, "Battery Alarm",
						"Battery Level is " + level, pendingNotify);
				n.flags |= Notification.FLAG_SHOW_LIGHTS
						| Notification.FLAG_ONGOING_EVENT;
				n.defaults |= Notification.DEFAULT_LIGHTS;

				// NEW: Embed the full-screen UI here. The notification manager
				// will // take care of displaying it if it's OK to do so.
				Intent alarmAlert = new Intent(context,
						saphion.fragments.alarm.AlarmAlert.class);
				alarmAlert.putExtra(PreferenceHelper.CURR_RING, level);
				alarmAlert.putExtra(PreferenceHelper.BAT_VALS, pos);
				alarmAlert.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_NO_USER_ACTION);
				n.fullScreenIntent = PendingIntent.getActivity(context, ID,
						alarmAlert, 0);

				// Send the notification using the alarm id to easily identify
				// the // correct notification.
				NotificationManager nm = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				// Log.Toast(context, "Recieved", Toast.LENGTH_LONG);
				// mNotificationManager.notify(ID, builder.build());
				nm.notify(ID, n);
			} else {
				// Log.Toast(context, "Recieved", Toast.LENGTH_LONG);
				newMethod(context, pos);
			}

		} else if (action.equals(Intents.ALARM_DISMISS_ACTION)) {
			NotificationManager nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.cancel(ID);
		}
	}

	public void newMethod(Context mContext, int pos) {
		// Instantiate a Builder object.
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				mContext).setSmallIcon(R.drawable.clock)
				.setContentTitle("Battery Alarm")
				.setContentText("Battery Level is " + level);

		// Creates an Intent for the Activity
		Intent notifyIntents = new Intent(mContext,
				saphion.fragments.alarm.AlarmAlert.class);
		// Sets the Activity to start in a new, empty task
		notifyIntents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notifyIntents.putExtra(PreferenceHelper.BAT_VALS, pos);
		notifyIntents.putExtra(PreferenceHelper.CURR_RING, level);
		// notifyIntents.
		// Creates the PendingIntent
		PendingIntent notifyIntent = PendingIntent.getActivity(mContext, 0,
				notifyIntents, PendingIntent.FLAG_UPDATE_CURRENT);

		// Puts the PendingIntent into the notification builder
		builder.setContentIntent(notifyIntent);

		builder.setFullScreenIntent(notifyIntent, true);
		// Notifications are issued by sending them to the
		// NotificationManager system service.
		NotificationManager mNotificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);

		builder.setPriority(Notification.PRIORITY_HIGH);
		// Builds an anonymous Notification object from the builder, and
		// passes it to the NotificationManager
		// Toast.makeText(mContext, "Recieved", Toast.LENGTH_LONG).show();
		mNotificationManager.notify(ID, builder.build());

	}
}
