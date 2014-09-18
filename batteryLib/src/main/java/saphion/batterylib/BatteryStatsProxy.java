package saphion.batterylib;

/*
 * Copyright (C) 2011 asksven
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.content.Context;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * A proxy to the non-public API BatteryStats
 * http://grepcode.com/file/repository
 * .grepcode.com/java/ext/com.google.android/android
 * /2.3.3_r1/android/os/BatteryStats.java/?v=source
 * 
 * @author sven
 * 
 */
public class BatteryStatsProxy {
	/*
	 * Instance of the BatteryStatsImpl
	 */
	private Object m_Instance = null;
	@SuppressWarnings("rawtypes")
	private Class m_ClassDefinition = null;

//	private static final String TAG = "BatteryStatsProxy";
	/*
	 * The UID stats are kept here as their methods / data can not be accessed
	 * outside of this class due to non-public types (Uid, Proc, etc.)
	 */
//	private SparseArray<? extends Object> m_uidStats = null;

	/**
	 * An instance to the UidNameResolver
	 */
	@SuppressWarnings("unused")
	private UidNameResolver m_nameResolver;
	private static BatteryStatsProxy m_proxy = null;

	synchronized public static BatteryStatsProxy getInstance(Context ctx) {
		if (m_proxy == null) {
			m_proxy = new BatteryStatsProxy(ctx);
		}

		return m_proxy;
	}

	public void invalidate() {
		m_proxy = null;
	}

	/**
	 * Default cctor
	 */
	private BatteryStatsProxy(Context context) {
		
		/**  As BatteryStats is a service we need to get a binding using the
		  IBatteryStats.Stub.getStatistics() method (using reflection). If we
		  would be using a public API the code would look like:
		  
		  @see com.android.settings.fuelgauge.PowerUsageSummary.java protected
		  */
		/*  void onCreate(Bundle icicle) { super.onCreate(icicle);
		  
		  mStats = (BatteryStatsImpl)getLastNonConfigurationInstance();
		  
		  addPreferencesFromResource(R.xml.power_usage_summary); mBatteryInfo =
		  IBatteryStats.Stub.asInterface(
		  ServiceManager.getService("batteryinfo")); mAppListGroup =
		  (PreferenceGroup) findPreference("app_list"); mPowerProfile = new
		  PowerProfile(this); }
		  
		  followed by private void load() { try { byte[] data =
		  mBatteryInfo.getStatistics(); Parcel parcel = Parcel.obtain();
		  parcel.unmarshall(data, 0, data.length); parcel.setDataPosition(0);
		  mStats = com.android.internal.os.BatteryStatsImpl.CREATOR
		  .createFromParcel(parcel);
		  mStats.distributeWorkLocked(BatteryStats.STATS_SINCE_CHARGED); }
		  catch (RemoteException e) { Log.e(TAG, "RemoteException:", e); } }
		 */

		m_nameResolver = new UidNameResolver();

		try {
			ClassLoader cl = context.getClassLoader();

			m_ClassDefinition = cl
					.loadClass("com.android.internal.os.BatteryStatsImpl");

			// get the IBinder to the "batteryinfo" service
			@SuppressWarnings("rawtypes")
			Class serviceManagerClass = cl
					.loadClass("android.os.ServiceManager");

			// parameter types
			@SuppressWarnings("rawtypes")
			Class[] paramTypesGetService = new Class[1];
			paramTypesGetService[0] = String.class;

			@SuppressWarnings("unchecked")
			Method methodGetService = serviceManagerClass.getMethod(
					"getService", paramTypesGetService);

			// parameters
			Object[] paramsGetService = new Object[1];
			paramsGetService[0] = "batteryinfo";

			//Log.i(TAG,
				//	"invoking android.os.ServiceManager.getService(\"batteryinfo\")");
			IBinder serviceBinder = (IBinder) methodGetService.invoke(
					serviceManagerClass, paramsGetService);

		//	Log.i(TAG,
			//		"android.os.ServiceManager.getService(\"batteryinfo\") returned a service binder");
			// now we have a binder. Let's us that on
			// IBatteryStats.Stub.asInterface
			// to get an IBatteryStats
			// Note the $-syntax here as Stub is a nested class
			@SuppressWarnings("rawtypes")
			Class iBatteryStatsStub = cl
					.loadClass("com.android.internal.app.IBatteryStats$Stub");

			// Parameters Types
			@SuppressWarnings("rawtypes")
			Class[] paramTypesAsInterface = new Class[1];
			paramTypesAsInterface[0] = IBinder.class;

			@SuppressWarnings("unchecked")
			Method methodAsInterface = iBatteryStatsStub.getMethod(
					"asInterface", paramTypesAsInterface);

			// Parameters
			Object[] paramsAsInterface = new Object[1];
			paramsAsInterface[0] = serviceBinder;

		//	Log.i(TAG,
			//		"invoking com.android.internal.app.IBatteryStats$Stub.asInterface");
			Object iBatteryStatsInstance = methodAsInterface.invoke(
					iBatteryStatsStub, paramsAsInterface);

			// and finally we call getStatistics from that IBatteryStats to
			// obtain a Parcel
			@SuppressWarnings("rawtypes")
			Class iBatteryStats = cl
					.loadClass("com.android.internal.app.IBatteryStats");

			@SuppressWarnings("unchecked")
			Method methodGetStatistics = iBatteryStats
					.getMethod("getStatistics");

			//Log.i(TAG, "invoking getStatistics");
			byte[] data = (byte[]) methodGetStatistics
					.invoke(iBatteryStatsInstance);

			//Log.i(TAG, "retrieving parcel");

			Parcel parcel = Parcel.obtain();
			parcel.unmarshall(data, 0, data.length);
			parcel.setDataPosition(0);

			@SuppressWarnings("rawtypes")
			Class batteryStatsImpl = cl
					.loadClass("com.android.internal.os.BatteryStatsImpl");

			//Log.i(TAG, "reading CREATOR field");
			Field creatorField = batteryStatsImpl.getField("CREATOR");

			// From here on we don't need reflection anymore
			@SuppressWarnings("rawtypes")
			Parcelable.Creator batteryStatsImpl_CREATOR = (Parcelable.Creator) creatorField
					.get(batteryStatsImpl);

			m_Instance = batteryStatsImpl_CREATOR.createFromParcel(parcel);
		} catch (Exception e) {
			if (e instanceof InvocationTargetException && e.getCause() != null) {
				Log.e("TAG",
						"An exception occured in BatteryStatsProxy(). Message: "
								+ e.getCause().getMessage());
			} else {
				Log.e("TAG",
						"An exception occured in BatteryStatsProxy(). Message: "
								+ e.getMessage());
			}
			m_Instance = null;

		}
	}

	/**
	 * Returns true if the proxy could not be initialized properly
	 * 
	 * @return true if the proxy wasn't initialized
	 */
	public boolean initFailed() {
		return m_Instance == null;
	}

	/**
	 * Initalizes the collection of history items
	 */
	public boolean startIteratingHistoryLocked()
			throws BatteryInfoUnavailableException {
		Boolean ret = false;

		try {
			@SuppressWarnings("unchecked")
			Method method = m_ClassDefinition
					.getMethod("startIteratingHistoryLocked");

			ret = (Boolean) method.invoke(m_Instance);

		} catch (IllegalArgumentException e) {
			Log.e("TAG",
					"An exception occured in startIteratingHistoryLocked(). Message: "
							+ e.getMessage() + ", cause: "
							+ e.getCause().getMessage());
			throw e;
		} catch (Exception e) {
			ret = false;
			throw new BatteryInfoUnavailableException();
		}

		return ret;

	}

	@SuppressWarnings("unchecked")
	public ArrayList<HistoryItem> getHistory(Context context) throws Exception {

		ArrayList<HistoryItem> myStats = new ArrayList<HistoryItem>();

		try {
			ClassLoader cl = context.getClassLoader();
			@SuppressWarnings("rawtypes")
			Class classHistoryItem = cl
					.loadClass("android.os.BatteryStats$HistoryItem");

			// get constructor
			@SuppressWarnings("rawtypes")
			Constructor cctor = classHistoryItem.getConstructor();

			Object myHistoryItem = cctor.newInstance();

			// prepare the method call for getNextHistoryItem
			// Parameters Types
			@SuppressWarnings("rawtypes")
			Class[] paramTypes = new Class[1];
			paramTypes[0] = classHistoryItem;

			// @SuppressWarnings("unchecked")
			Method methodNext = m_ClassDefinition.getMethod(
					"getNextHistoryLocked", paramTypes);

			// Parameters
			Object[] params = new Object[1];

			// initalize hist and iterate like this
			// if (stats.startIteratingHistoryLocked()) {
			// final HistoryItem rec = new HistoryItem();
			// while (stats.getNextHistoryLocked(rec)) {

			// read the time of query for history
			// Long statTimeRef =
			// Long.valueOf(this.computeBatteryRealtime(SystemClock.elapsedRealtime()
			// * 1000,
			// BatteryStatsTypes.STATS_SINCE_CHARGED));
			Long statTimeRef = System.currentTimeMillis();

		/*	Log.d(TAG,
					"Reference time ("
							+ statTimeRef
							+ ": "
							+ DateUtils.format(DateUtils.DATE_FORMAT_NOW,
									statTimeRef));*/
			// statTimeLast stores the timestamp of the last sample
			// Calendar cal = Calendar.getInstance();

			Long statTimeLast = System.currentTimeMillis() - 86400000;// Long.valueOf(0);

			if (this.startIteratingHistoryLocked()) {
				params[0] = myHistoryItem;
				Boolean bNext = (Boolean) methodNext.invoke(m_Instance, params);
				while (bNext) {
					// process stats: create HistoryItems from params
					Field timeField = classHistoryItem.getField("time"); // long

					Field cmdField = classHistoryItem.getField("cmd"); // byte
					Byte cmdValue = (Byte) cmdField.get(params[0]);

					// process only valid items
					byte updateCmd = 0;

					// ICS has a different implementation of HistoryItems
					// constants
					if (AndroidVersion.isIcs()) {
						updateCmd = HistoryItemIcs.CMD_UPDATE;
					} else {
						updateCmd = HistoryItem.CMD_UPDATE;
					}

					if (cmdValue == updateCmd) {
						Field batteryLevelField = classHistoryItem
								.getField("batteryLevel"); // byte
						Field batteryStatusField = classHistoryItem
								.getField("batteryStatus"); // byte
						Field batteryHealthField = classHistoryItem
								.getField("batteryHealth"); // byte
						Field batteryPlugTypeField = classHistoryItem
								.getField("batteryPlugType"); // byte

						Field batteryTemperatureField = classHistoryItem
								.getField("batteryTemperature"); // char
						Field batteryVoltageField = classHistoryItem
								.getField("batteryVoltage"); // char

						Field statesField = classHistoryItem.getField("states"); // int

						// retrieve all values
						// @SuppressWarnings("rawtypes")
						Long timeValue = (Long) timeField.get(params[0]);

						// store values only once
						if (!statTimeLast.equals(timeValue)) {
							Byte batteryLevelValue = (Byte) batteryLevelField
									.get(params[0]);
							Byte batteryStatusValue = (Byte) batteryStatusField
									.get(params[0]);
							Byte batteryHealthValue = (Byte) batteryHealthField
									.get(params[0]);
							Byte batteryPlugTypeValue = (Byte) batteryPlugTypeField
									.get(params[0]);

							String batteryTemperatureValue = String
									.valueOf(batteryTemperatureField
											.get(params[0]));
							String batteryVoltageValue = String
									.valueOf(batteryVoltageField.get(params[0]));

							Integer statesValue = (Integer) statesField
									.get(params[0]);

							HistoryItem myItem = null;

							// ICS has a different implementation of
							// HistoryItems constants
							if (AndroidVersion.isIcs()) {
								myItem = new HistoryItemIcs(timeValue,
										cmdValue, batteryLevelValue,
										batteryStatusValue, batteryHealthValue,
										batteryPlugTypeValue,
										batteryTemperatureValue,
										batteryVoltageValue, statesValue);
							} else {
								myItem = new HistoryItem(timeValue, cmdValue,
										batteryLevelValue, batteryStatusValue,
										batteryHealthValue,
										batteryPlugTypeValue,
										batteryTemperatureValue,
										batteryVoltageValue, statesValue);
							}

							myStats.add(myItem);
						//	Log.d(TAG, "Added HistoryItem " + myItem.toString());

						}
						// overwrite the time of the last sample
						statTimeLast = timeValue;

					} else {
						//Log.d(TAG, "Skipped item");
					}

					bNext = (Boolean) methodNext.invoke(m_Instance, params);
				}

				// norm the time of each sample
				// stat time last is the number of millis since
				// the stats is being collected
				// the ref time is a full plain time (with date)
				Long offset = statTimeRef - statTimeLast;
			/*	Log.d(TAG,
						"Reference time ("
								+ statTimeRef
								+ ")"
								+ DateUtils.format("MM/dd/yyyy hh:mm:ss a",
										statTimeRef));

				Log.d(TAG,
						"Last sample ("
								+ statTimeLast
								+ ")"
								+ DateUtils.format("MM/dd/yyyy hh:mm:ss a",
										statTimeLast + offset));

				Log.d(TAG,
						"Correcting all HistoryItem times by an offset of ("
								+ offset + ")"
								+ DateUtils.formatDuration(offset * 1000));*/
				for (int i = 0; i < myStats.size(); i++) {
					myStats.get(i).setOffset(offset);
				}
			}
		} catch (Exception e) {
			Log.e("TAG",
					"An exception occured in getHistory(). Message: "
							+ e.getMessage() + ", cause: "
							+ e.getCause().getMessage());
			throw e;
		}

		return myStats;
	}

}
