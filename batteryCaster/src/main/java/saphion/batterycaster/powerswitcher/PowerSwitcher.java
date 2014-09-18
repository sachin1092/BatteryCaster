package saphion.batterycaster.powerswitcher;

import java.util.ArrayList;

import saphion.fragment.powerfragment.PowerPreference;
import saphion.fragment.powerfragment.PowerProfItems;
import saphion.logger.Log;
import saphion.togglercvrs.BrightnessRcvr;
import saphion.utils.PreferenceHelper;
import saphion.utils.ToggleHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class PowerSwitcher extends BroadcastReceiver {

	public static String POSITION = "pos";

	@Override
	public void onReceive(Context mContext, Intent intent) {
		int pos = mContext.getSharedPreferences("saphion.batterycaster_preferences",
				Context.MODE_MULTI_PROCESS)
				.getInt(POSITION, 0);
		if (intent.hasExtra(POSITION)) {
			pos = intent.getIntExtra(POSITION, 0);
		}

		mContext.getSharedPreferences("saphion.batterycaster_preferences",
				Context.MODE_MULTI_PROCESS).edit()
				.putInt(PreferenceHelper.POSITIONS, pos).commit();

		ArrayList<PowerProfItems> items = PowerPreference.retPower(mContext);
		Log.d("Will Switch Profile now");
		if (pos < items.size()) {
			Log.d("Inside if: Will Switch Profile now and position is: " + pos);
			PowerProfItems newItem = items.get(pos);
			switchtoWifi(newItem.getWifi(), mContext);
			switchtoData(newItem.getData(), mContext);
			switchtoBluetooth(newItem.getBluetooth(), mContext);
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
				switchtoAmode(newItem.getAmode(), mContext);
			switchtoSync(newItem.getSync(), mContext);
			// switchtoBrightness(newItem.getBrightness(), mContext);
			switchtohfback(newItem.getHapticFeedback(), mContext);
			switchtoArotate(newItem.getAutoRotate(), mContext);
			switchtoRingMode(newItem.getRingMode(), mContext);
			switchtoSTimeout(newItem.getScreenTimeout(), mContext);
			if (!intent.hasExtra("use")) {
				Intent mIntent = new Intent(mContext, BrightnessRcvr.class);
				mIntent.putExtra(BrightnessRcvr.FROM_SWITCHER,
						newItem.getBrightness());
				mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mIntent.putExtra(PowerSwitcher.POSITION, pos);
				mContext.startActivity(mIntent);
			}
		}

	}

	private void switchtoSTimeout(String screenTimeout, Context mContext) {
		Log.d("Screen Timeout Mode from "
				+ ToggleHelper.getScreenTimeOutIndex(mContext) + " to "
				+ screenTimeout);
		ToggleHelper
				.setScreenTimeout(mContext, Integer.parseInt(screenTimeout));

	}

	private void switchtoRingMode(int ringMode, Context mContext) {
		Log.d("Toggling Ringer Mode from "
				+ ToggleHelper.getRingerMode(mContext) + " to " + ringMode);
		ToggleHelper.setRingerMode(mContext, ringMode);
	}

	private void switchtoArotate(boolean autoRotate, Context mContext) {
		Log.d("Change Arotate from " + ToggleHelper.isRotationEnabled(mContext)
				+ " to " + autoRotate);
		if (autoRotate != ToggleHelper.isRotationEnabled(mContext)) {
			ToggleHelper.toggleRotation(mContext);
			// Log.Toast(mContext, "Toggling Rotation to " + autoRotate,
			// Toast.LENGTH_SHORT);
		}

	}

	private void switchtohfback(boolean hapticFeedback, Context mContext) {
		Log.d("Change hfback from " + ToggleHelper.isHapticFback(mContext)
				+ " to " + hapticFeedback);
		if (hapticFeedback != ToggleHelper.isHapticFback(mContext)) {
			ToggleHelper.toggleHapticFeedback(mContext);
			// Log.Toast(mContext, "Toggling hapticFeedback to " +
			// hapticFeedback,
			// Toast.LENGTH_SHORT);
		}
	}

	/*
	 * private void switchtoBrightness(int brightness, Context mContext) {
	 * Window window = ((Activity) mContext).getWindow();
	 * ToggleHelper.setBrightness(mContext, window, brightness);
	 * 
	 * }
	 */

	private void switchtoSync(boolean sync, Context mContext) {
		Log.d("Change sync from " + ToggleHelper.isSyncEnabled() + " to "
				+ sync);
		if (sync != ToggleHelper.isSyncEnabled()) {
			ToggleHelper.toggleSync();
			// Log.Toast(mContext, "Toggling Sync " + sync, Toast.LENGTH_SHORT);
		}

	}

	private void switchtoAmode(boolean amode, Context mContext) {
		Log.d("Change amode from " + ToggleHelper.isAModeEnabled(mContext)
				+ " to " + amode);
		if (amode != ToggleHelper.isAModeEnabled(mContext)) {
			ToggleHelper.toggleAirplane(mContext);
			// Log.Toast(mContext, "Toggling Amode to " + amode,
			// Toast.LENGTH_SHORT);
		}

	}

	private void switchtoBluetooth(boolean bluetooth, Context mContext) {
		Log.d("Change btooth from " + ToggleHelper.isBluetoothEnabled(mContext)
				+ " to " + bluetooth);
		if (bluetooth != ToggleHelper.isBluetoothEnabled(mContext)) {
			ToggleHelper.toggleBlueTooth(mContext);
		}

	}

	private void switchtoData(boolean data, Context mContext) {
		Log.d("Change mdata from " + ToggleHelper.isDataEnable(mContext)
				+ " to " + data);
		if (data != ToggleHelper.isDataEnable(mContext))
			try {
				ToggleHelper.toggleMData(mContext);
			} catch (Exception e) {
				Log.d(e.toString());
				e.printStackTrace();
			}

	}

	private void switchtoWifi(boolean wifi, Context mContext) {
		Log.d("Change wifi from " + ToggleHelper.isWifiEnabled(mContext)
				+ " to " + wifi);
		if (wifi != ToggleHelper.isWifiEnabled(mContext)) {
			ToggleHelper.toggleWifi(mContext);
		}

	}
}
