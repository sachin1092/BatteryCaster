/*
 * Copyright (C) 2013 The Android Open Source Project
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

package saphion.batterycaster.providers;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import saphion.batterycaster.R;
import saphion.fragments.alarm.AlarmItems;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.CursorLoader;

public final class Alarm implements Parcelable, AlarmContract.AlarmsColumns {
	/**
	 * Alarms start with an invalid id when it hasn't been saved to the
	 * database.
	 */
	public static final long INVALID_ID = -1;

	/**
	 * The default sort order for this table
	 */
	private static final String DEFAULT_SORT_ORDER = BATTERY + " ASC" + ", "
			+ _ID + " DESC";

	private static final String[] QUERY_COLUMNS = { _ID, BATTERY, CHARGE,
			DAYS_OF_WEEK, ENABLED, VIBRATE, LABEL, RINGTONE, DELETE_AFTER_USE };

	/**
	 * These save calls to cursor.getColumnIndexOrThrow() THEY MUST BE KEPT IN
	 * SYNC WITH ABOVE QUERY COLUMNS
	 */
	private static final int ID_INDEX = 0;
	private static final int BATTERY_INDEX = 1;
	private static final int CHARGE_INDEX = 2;
	private static final int DAYS_OF_WEEK_INDEX = 3;
	private static final int ENABLED_INDEX = 4;
	private static final int VIBRATE_INDEX = 5;
	private static final int LABEL_INDEX = 6;
	private static final int RINGTONE_INDEX = 7;
	private static final int DELETE_AFTER_USE_INDEX = 8;

	private static final int COLUMN_COUNT = DELETE_AFTER_USE_INDEX + 1;

	public static ContentValues createContentValues(Alarm alarm) {
		ContentValues values = new ContentValues(COLUMN_COUNT);
		if (alarm.id != INVALID_ID) {
			values.put(_ID, alarm.id);
		}

		values.put(ENABLED, alarm.enabled ? 1 : 0);
		values.put(BATTERY, alarm.battery);
		values.put(CHARGE, alarm.charge);
		values.put(DAYS_OF_WEEK, alarm.daysOfWeek.getBitSet());
		values.put(VIBRATE, alarm.vibrate ? 1 : 0);
		values.put(LABEL, alarm.label);
		values.put(DELETE_AFTER_USE, alarm.deleteAfterUse);
		if (alarm.alert == null) {
			// We want to put null, so default alarm changes
			values.putNull(RINGTONE);
		} else {
			values.put(RINGTONE, alarm.alert.toString());
		}

		return values;
	}

	public static Intent createIntent(String action, long alarmId) {
		return new Intent(action).setData(getUri(alarmId));
	}

	public static Intent createIntent(Context context, Class<?> cls,
			long alarmId) {
		return new Intent(context, cls).setData(getUri(alarmId));
	}

	public static Uri getUri(long alarmId) {
		return ContentUris.withAppendedId(CONTENT_URI, alarmId);
	}

	public static long getId(Uri contentUri) {
		return ContentUris.parseId(contentUri);
	}

	/**
	 * Get alarm cursor loader for all alarms.
	 * 
	 * @param context
	 *            to query the database.
	 * @return cursor loader with all the alarms.
	 */
	public static CursorLoader getAlarmsCursorLoader(Context context) {
		return new CursorLoader(context, CONTENT_URI, QUERY_COLUMNS, null,
				null, DEFAULT_SORT_ORDER);
	}

	/**
	 * Get alarm by id.
	 * 
	 * @param contentResolver
	 *            to perform the query on.
	 * @param alarmId
	 *            for the desired alarm.
	 * @return alarm if found, null otherwise
	 */
	public static Alarm getAlarm(ContentResolver contentResolver, long alarmId) {
		Cursor cursor = contentResolver.query(getUri(alarmId), QUERY_COLUMNS,
				null, null, null);
		Alarm result = null;
		if (cursor == null) {
			return result;
		}

		try {
			if (cursor.moveToFirst()) {
				result = new Alarm(cursor);
			}
		} finally {
			cursor.close();
		}

		return result;
	}

	/**
	 * Get all alarms given conditions.
	 * 
	 * @param contentResolver
	 *            to perform the query on.
	 * @param selection
	 *            A filter declaring which rows to return, formatted as an SQL
	 *            WHERE clause (excluding the WHERE itself). Passing null will
	 *            return all rows for the given URI.
	 * @param selectionArgs
	 *            You may include ?s in selection, which will be replaced by the
	 *            values from selectionArgs, in the order that they appear in
	 *            the selection. The values will be bound as Strings.
	 * @return list of alarms matching where clause or empty list if none found.
	 */
	public static List<Alarm> getAlarms(ContentResolver contentResolver,
			String selection, String... selectionArgs) {
		Cursor cursor = contentResolver.query(CONTENT_URI, QUERY_COLUMNS,
				selection, selectionArgs, null);
		List<Alarm> result = new LinkedList<Alarm>();
		if (cursor == null) {
			return result;
		}

		try {
			if (cursor.moveToFirst()) {
				do {
					result.add(new Alarm(cursor));
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
		}

		return result;
	}

	public static boolean contains(ContentResolver contentResolver, Alarm alarm) {
		Cursor cursor = contentResolver.query(CONTENT_URI, QUERY_COLUMNS, null,
				null, null);
		if (cursor == null) {
			return false;
		}

		try {
			if (cursor.moveToFirst()) {
				do {
					Alarm mAlarm = new Alarm(cursor);
					return areEqual(mAlarm, alarm);
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
		}
		return false;
	}

	private static boolean areEqual(Alarm mAlarm, Alarm alarm) {

		return mAlarm.deleteAfterUse == alarm.deleteAfterUse
				&& mAlarm.enabled == alarm.enabled
				&& mAlarm.vibrate == alarm.vibrate
				&& mAlarm.alert == alarm.alert
				&& mAlarm.battery == alarm.battery
				&& mAlarm.charge == alarm.charge
				&& mAlarm.daysOfWeek == alarm.daysOfWeek
				&& mAlarm.label == alarm.label;
	}

	public static Alarm addAlarm(ContentResolver contentResolver, Alarm alarm) {
		ContentValues values = createContentValues(alarm);
		Uri uri = contentResolver.insert(CONTENT_URI, values);
		alarm.id = getId(uri);
		return alarm;
	}

	public static Alarm addAlarm(ContentResolver contentResolver,
			AlarmItems alarmItems) {
		Alarm alarm = new Alarm(alarmItems);
		ContentValues values = createContentValues(alarm);
		Uri uri = contentResolver.insert(CONTENT_URI, values);
		alarm.id = getId(uri);
		return alarm;
	}

	public static boolean updateAlarm(ContentResolver contentResolver,
			Alarm alarm) {
		if (alarm.id == Alarm.INVALID_ID)
			return false;
		ContentValues values = createContentValues(alarm);
		long rowsUpdated = contentResolver.update(getUri(alarm.id), values,
				null, null);
		return rowsUpdated == 1;
	}

	public static boolean deleteAlarm(ContentResolver contentResolver,
			long alarmId) {
		if (alarmId == INVALID_ID)
			return false;
		int deletedRows = contentResolver.delete(getUri(alarmId), "", null);
		return deletedRows == 1;
	}

	public static final Parcelable.Creator<Alarm> CREATOR = new Parcelable.Creator<Alarm>() {
		public Alarm createFromParcel(Parcel p) {
			return new Alarm(p);
		}

		public Alarm[] newArray(int size) {
			return new Alarm[size];
		}
	};

	// Public fields
	// TODO: Refactor instance names
	public long id;
	public boolean enabled;
	public int battery;
	public int charge;
	public DaysOfWeek daysOfWeek;
	public boolean vibrate;
	public String label;
	public Uri alert;
	public boolean deleteAfterUse;

	// Creates a default alarm at the current time.
	public Alarm() {
		this(75);
	}

	public Alarm(int battery) {
		this.id = INVALID_ID;
		this.battery = battery;
		this.charge = 3;
		this.vibrate = true;
		this.daysOfWeek = new DaysOfWeek(0);
		this.label = "";
		this.alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		this.deleteAfterUse = false;
	}

	public Alarm(Cursor c) {
		id = c.getLong(ID_INDEX);
		enabled = c.getInt(ENABLED_INDEX) == 1;
		battery = c.getInt(BATTERY_INDEX);
		charge = c.getInt(CHARGE_INDEX);
		daysOfWeek = new DaysOfWeek(c.getInt(DAYS_OF_WEEK_INDEX));
		vibrate = c.getInt(VIBRATE_INDEX) == 1;
		label = c.getString(LABEL_INDEX);
		deleteAfterUse = c.getInt(DELETE_AFTER_USE_INDEX) == 1;

		if (c.isNull(RINGTONE_INDEX)) {
			// Should we be saving this with the current ringtone or leave it
			// null
			// so it changes when user changes default ringtone?
			alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		} else {
			alert = Uri.parse(c.getString(RINGTONE_INDEX));
		}
	}

	Alarm(Parcel p) {
		id = p.readLong();
		enabled = p.readInt() == 1;
		battery = p.readInt();
		charge = p.readInt();
		daysOfWeek = new DaysOfWeek(p.readInt());
		vibrate = p.readInt() == 1;
		label = p.readString();
		alert = (Uri) p.readParcelable(null);
		deleteAfterUse = p.readInt() == 1;
	}

	public Alarm(AlarmItems alarmItems) {

		this.id = INVALID_ID;
		this.battery = alarmItems.getLevel();
		this.charge = 3;
		String days = alarmItems.getDays().toString();

		if (days.equals("[]")) {
			this.daysOfWeek = new DaysOfWeek(0);
		}

		if (days.contains("[")) {
			days = days.substring(days.indexOf("[") + 1);
		}
		if (days.contains("]")) {
			days = days.substring(0, days.indexOf("]"));
		}

		int mDays = 0;

		if ((days.toLowerCase(Locale.US).contains("mon")))
			mDays = mDays | 64;
		if ((days.toLowerCase(Locale.US).contains("tue")))
			mDays = mDays | 32;
		if ((days.toLowerCase(Locale.US).contains("wed")))
			mDays = mDays | 16;
		if ((days.toLowerCase(Locale.US).contains("thu")))
			mDays = mDays | 8;
		if ((days.toLowerCase(Locale.US).contains("fri")))
			mDays = mDays | 4;
		if ((days.toLowerCase(Locale.US).contains("sat")))
			mDays = mDays | 2;
		if ((days.toLowerCase(Locale.US).contains("sun")))
			mDays = mDays | 1;

		this.daysOfWeek = new DaysOfWeek(mDays);

		this.vibrate = alarmItems.getVibrate();
		/* this.daysOfWeek = new DaysOfWeek(0); */
		this.label = alarmItems.getLabel();
		this.alert = Uri.parse(alarmItems.getRingTone());// RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		this.deleteAfterUse = false;
		this.enabled = alarmItems.getEnabled();

	}

	public String getLabelOrDefault(Context context) {
		if (label == null || label.length() == 0) {
			return context.getString(R.string.default_label);
		}
		return label;
	}

	public void writeToParcel(Parcel p, int flags) {
		p.writeLong(id);
		p.writeInt(enabled ? 1 : 0);
		p.writeInt(battery);
		p.writeInt(charge);
		p.writeInt(daysOfWeek.getBitSet());
		p.writeInt(vibrate ? 1 : 0);
		p.writeString(label);
		p.writeParcelable(alert, flags);
		p.writeInt(deleteAfterUse ? 1 : 0);
	}

	public int describeContents() {
		return 0;
	}

	/*
	 * public AlarmInstance createInstanceAfter(Calendar time) { Calendar
	 * nextInstanceTime = Calendar.getInstance();
	 * nextInstanceTime.set(Calendar.YEAR, time.get(Calendar.YEAR));
	 * nextInstanceTime.set(Calendar.MONTH, time.get(Calendar.MONTH));
	 * nextInstanceTime.set(Calendar.DAY_OF_MONTH,
	 * time.get(Calendar.DAY_OF_MONTH));
	 * nextInstanceTime.set(Calendar.HOUR_OF_DAY, hour);
	 * nextInstanceTime.set(Calendar.MINUTE, minutes);
	 * nextInstanceTime.set(Calendar.SECOND, 0);
	 * nextInstanceTime.set(Calendar.MILLISECOND, 0);
	 * 
	 * // If we are still behind the passed in time, then add a day if
	 * (nextInstanceTime.getTimeInMillis() <= time.getTimeInMillis()) {
	 * nextInstanceTime.add(Calendar.DAY_OF_YEAR, 1); }
	 * 
	 * // The day of the week might be invalid, so find next valid one int
	 * addDays = daysOfWeek.calculateDaysToNextAlarm(nextInstanceTime); if
	 * (addDays > 0) { nextInstanceTime.add(Calendar.DAY_OF_WEEK, addDays); }
	 * 
	 * AlarmInstance result = new AlarmInstance(nextInstanceTime, id);
	 * result.mVibrate = vibrate; result.mLabel = label; result.mRingtone =
	 * alert; return result; }
	 */

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Alarm))
			return false;
		final Alarm other = (Alarm) o;
		return id == other.id;
	}

	@Override
	public int hashCode() {
		return Long.valueOf(id).hashCode();
	}

	@Override
	public String toString() {
		return "Alarm{" + "alert=" + alert + ", id=" + id + ", enabled="
				+ enabled + ", battery level=" + battery + ", charge=" + charge
				+ ", daysOfWeek=" + daysOfWeek + ", vibrate=" + vibrate
				+ ", label='" + label + '\'' + ", deleteAfterUse="
				+ deleteAfterUse + '}';
	}

}
