package saphion.services;

import saphion.batterycaster.R;
import saphion.fragments.TabNavigation;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class FakeService extends Service {

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Intent myIntent = new Intent(getBaseContext(), TabNavigation.class);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.abc_ab_bottom_transparent_dark_holo)
				.setContentTitle("SwipeIt")

				.setContentIntent(
						PendingIntent.getActivity(getBaseContext(), 0,
								myIntent, Intent.FLAG_ACTIVITY_NEW_TASK))
				.setContentText("BatteryCaster is Running").setTicker(null)
				.setWhen(0);

		builder.setPriority(NotificationCompat.PRIORITY_MIN);
		//builder.setContent(rvLargeNoti);

		startForeground(ForegroundService.notiID, builder.build());
		//new Logger(getBaseContext()).Toast("started fakeservice");
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				stopSelf();
			}
		}, 1000);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
