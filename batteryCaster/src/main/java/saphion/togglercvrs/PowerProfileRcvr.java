package saphion.togglercvrs;

import java.util.ArrayList;

import saphion.fragment.alarm.alert.Intents;
import saphion.fragment.powerfragment.EditPower;
import saphion.fragment.powerfragment.PowerPreference;
import saphion.fragment.powerfragment.PowerProfItems;
import saphion.utils.PreferenceHelper;
import saphion.utils.ToggleHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PowerProfileRcvr extends BroadcastReceiver {

	Context context;

	@Override
	public void onReceive(Context mContext, Intent intent) {
		context = mContext;
		ArrayList<PowerProfItems> poweritems = PowerPreference
				.retPower(getBaseContext());
		if (mContext.getSharedPreferences("saphion.batterycaster_preferences",
				Context.MODE_MULTI_PROCESS).getInt(PreferenceHelper.PROF2, 0) < poweritems
				.size()
				&& mContext.getSharedPreferences(
						"saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS).getInt(
						PreferenceHelper.PROF1, 0) < poweritems.size()) {
			if (poweritems.get(
					mContext.getSharedPreferences(
							"saphion.batterycaster_preferences",
							Context.MODE_MULTI_PROCESS).getInt(
							PreferenceHelper.PROF1, 0)).isPowerProfequal(
					ToggleHelper.isWifiEnabled(getBaseContext()),
					ToggleHelper.isDataEnable(getBaseContext()),
					ToggleHelper.isBluetoothEnabled(getBaseContext()),
					ToggleHelper.isAModeEnabled(getBaseContext()),
					ToggleHelper.isSyncEnabled(),
					ToggleHelper.getBrightness(getBaseContext()),
					ToggleHelper.isHapticFback(getBaseContext()),
					ToggleHelper.isRotationEnabled(getBaseContext()),
					ToggleHelper.getRingerMode(getBaseContext()),
					String.valueOf(ToggleHelper
							.getScreenTimeOut(getBaseContext())))) {
				int position = (mContext.getSharedPreferences(
						"saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS).getInt(
						PreferenceHelper.PROF2, 0));
				mContext.sendBroadcast(new Intent(Intents.SWITCHER_INTENT)
						.putExtra(EditPower.BNESS_SEEKBAR,
								poweritems.get(position).getBrightness())
						.putExtra(PreferenceHelper.POSITION, position));
			} else {
				int position = (mContext.getSharedPreferences(
						"saphion.batterycaster_preferences",
						Context.MODE_MULTI_PROCESS).getInt(
						PreferenceHelper.PROF1, 0));
				mContext.sendBroadcast(new Intent(Intents.SWITCHER_INTENT)
						.putExtra(EditPower.BNESS_SEEKBAR,
								poweritems.get(position).getBrightness())
						.putExtra(PreferenceHelper.POSITION, position));
			}
		} else {
			Toast.makeText(getBaseContext(), "Can't switch power profile",
					Toast.LENGTH_LONG).show();
		}

		mContext.sendBroadcast(new Intent(Intents.SWITCHER_NOTI));

	}

	public Context getBaseContext() {
		return context;
	}

}
