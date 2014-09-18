package saphion.batterycaster;

import java.util.ArrayList;

import saphion.batterylib.AndroidVersion;
import saphion.batterylib.BatteryStatsProxy;
import saphion.batterylib.HistoryItem;
import android.content.Context;
import android.util.Log;

public class BatteryStats {
	public static ArrayList<HistoryItem> getHistList(Context mContext) {
		if (AndroidVersion.isFroyo()) {
			/*
			 * Toast.makeText(this,
			 * "Unfortunately Froyo does not support history data.",
			 * Toast.LENGTH_SHORT).show();
			 */
		}
		ArrayList<HistoryItem> myRet = new ArrayList<HistoryItem>();

		BatteryStatsProxy mStats = BatteryStatsProxy.getInstance(mContext);
		try {
			myRet = mStats.getHistory(mContext);
		} catch (Exception e) {
			Log.e("Stencil",
					"An error occured while retrieving history. No result");
		}
		return myRet;
	}
}
