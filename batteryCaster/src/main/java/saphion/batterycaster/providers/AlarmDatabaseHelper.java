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

import saphion.logger.Log;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Helper class for opening the database from multiple providers.  Also provides
 * some common functionality.
 */
class AlarmDatabaseHelper extends SQLiteOpenHelper {
    /**
     * Original Clock Database.
     **/
    //private static final int VERSION_1 = 1;

/*    *//**
     * CM 10.2 Clock database
     *//*
    private static final int VERSION_CM102 = 7;
*/
    /**
     * Introduce:
     * Added alarm_instances table
     * Added selected_cities table
     * Added DELETE_AFTER_USE column to alarms table
     */
    private static final int VERSION_6 = 8;

    /**
     * Added alarm settings to instance table.
     */
    private static final int VERSION_7 = 9;

    // This creates a default alarm at 8:30 for every Mon,Tue,Wed,Thu,Fri
    //private static final String DEFAULT_ALARM_1 = "(8, 30, 31, 0, 0, '', NULL, 0);";

    // This creates a default alarm at 9:30 for every Sat,Sun
  //  private static final String DEFAULT_ALARM_2 = "(9, 00, 96, 0, 0, '', NULL, 0);";

    // Database and table names
    static final String DATABASE_NAME = "alarms.db";
    static final String OLD_ALARMS_TABLE_NAME = "alarms";
    static final String ALARMS_TABLE_NAME = "alarm_templates";

    private static void createAlarmsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ALARMS_TABLE_NAME + " (" +
                AlarmContract.AlarmsColumns._ID + " INTEGER PRIMARY KEY," +
                AlarmContract.AlarmsColumns.BATTERY + " INTEGER NOT NULL, " +
                AlarmContract.AlarmsColumns.CHARGE + " INTEGER NOT NULL, " +
                AlarmContract.AlarmsColumns.DAYS_OF_WEEK + " INTEGER NOT NULL, " +
                AlarmContract.AlarmsColumns.ENABLED + " INTEGER NOT NULL, " +
                AlarmContract.AlarmsColumns.VIBRATE + " INTEGER NOT NULL, " +
                AlarmContract.AlarmsColumns.LABEL + " TEXT NOT NULL, " +
                AlarmContract.AlarmsColumns.RINGTONE + " TEXT, " +
                AlarmContract.AlarmsColumns.DELETE_AFTER_USE + " INTEGER NOT NULL DEFAULT 0);");
        Log.d("Alarms Table created");
    }

 /*   private static void createInstanceTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + INSTANCES_TABLE_NAME + " (" +
                ClockContract.InstancesColumns._ID + " INTEGER PRIMARY KEY," +
                ClockContract.InstancesColumns.YEAR + " INTEGER NOT NULL, " +
                ClockContract.InstancesColumns.MONTH + " INTEGER NOT NULL, " +
                ClockContract.InstancesColumns.DAY + " INTEGER NOT NULL, " +
                ClockContract.InstancesColumns.HOUR + " INTEGER NOT NULL, " +
                ClockContract.InstancesColumns.MINUTES + " INTEGER NOT NULL, " +
                ClockContract.InstancesColumns.VIBRATE + " INTEGER NOT NULL, " +
                ClockContract.InstancesColumns.LABEL + " TEXT NOT NULL, " +
                ClockContract.InstancesColumns.RINGTONE + " TEXT, " +
                ClockContract.InstancesColumns.ALARM_STATE + " INTEGER NOT NULL, " +
                ClockContract.InstancesColumns.ALARM_ID + " INTEGER REFERENCES " +
                    ALARMS_TABLE_NAME + "(" + ClockContract.AlarmsColumns._ID + ") " +
                    "ON UPDATE CASCADE ON DELETE CASCADE" +
                ");");
        Log.d("Instance table created");
    }

    private static void createCitiesTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CITIES_TABLE_NAME + " (" +
                ClockContract.CitiesColumns.CITY_ID + " TEXT PRIMARY KEY," +
                ClockContract.CitiesColumns.CITY_NAME + " TEXT NOT NULL, " +
                ClockContract.CitiesColumns.TIMEZONE_NAME + " TEXT NOT NULL, " +
                ClockContract.CitiesColumns.TIMEZONE_OFFSET + " INTEGER NOT NULL);");
        Log.d("Cities table created");
    }*/

  //  private Context mContext;

    public AlarmDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_7);
       // mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createAlarmsTable(db);

        // insert default alarms
     //   Log.d("Inserting default alarms");
    //    String cs = ", "; //comma and space
       /* String insertMe = "INSERT INTO " + ALARMS_TABLE_NAME + " (" +
                ClockContract.AlarmsColumns.BATTERY + cs +
                ClockContract.AlarmsColumns.CHARGE + cs +
                ClockContract.AlarmsColumns.DAYS_OF_WEEK + cs +
                ClockContract.AlarmsColumns.ENABLED + cs +
                ClockContract.AlarmsColumns.VIBRATE + cs +
                ClockContract.AlarmsColumns.LABEL + cs +
                ClockContract.AlarmsColumns.RINGTONE + cs +
                ClockContract.AlarmsColumns.DELETE_AFTER_USE + ") VALUES ";*/
     //   db.execSQL(insertMe + DEFAULT_ALARM_1);
      //  db.execSQL(insertMe + DEFAULT_ALARM_2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
        
            Log.d("Upgrading alarms database from version " + oldVersion + " to " + currentVersion);
        

        if (oldVersion <= VERSION_6) {
            // These were not used in DB_VERSION_6, so we can just drop them.

            // Create new alarms table and copy over the data
            createAlarmsTable(db);

            Log.d("Copying old alarms to new table");
            String[] OLD_TABLE_COLUMNS = {
                    "_id",
                    "battery",
                    "charge",
                    "daysofweek",
                    "enabled",
                    "vibrate",
                    "message",
                    "alert",
            };
            Cursor cursor = db.query(OLD_ALARMS_TABLE_NAME, OLD_TABLE_COLUMNS,
                    null, null, null, null, null);
        //    Calendar currentTime = Calendar.getInstance();
            while (cursor.moveToNext()) {
                Alarm alarm = new Alarm();
                alarm.id = cursor.getLong(0);
                alarm.battery = cursor.getInt(1);
                alarm.charge = cursor.getInt(2);
                alarm.daysOfWeek = new DaysOfWeek(cursor.getInt(3));
                alarm.enabled = cursor.getInt(4) == 1;
                alarm.vibrate = cursor.getInt(5) == 1;
                alarm.label = cursor.getString(6);

                String alertString = cursor.getString(7);
                if ("silent".equals(alertString)) {
                    alarm.alert = Alarm.NO_RINGTONE_URI;
                } else {
                    alarm.alert = TextUtils.isEmpty(alertString) ? null : Uri.parse(alertString);
                }

                // Save new version of alarm and create alarminstance for it
                db.insert(ALARMS_TABLE_NAME, null, Alarm.createContentValues(alarm));
                
            }
            cursor.close();

            Log.d("Dropping old alarm table");
            db.execSQL("DROP TABLE IF EXISTS " + OLD_ALARMS_TABLE_NAME + ";");
        }
    }

    long fixAlarmInsert(ContentValues values) {
        // Why are we doing this? Is this not a programming bug if we try to
        // insert an already used id?
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        long rowId = -1;
        try {
            // Check if we are trying to re-use an existing id.
            Object value = values.get(AlarmContract.AlarmsColumns._ID);
            if (value != null) {
                long id = (Long) value;
                if (id > -1) {
                    final Cursor cursor = db.query(ALARMS_TABLE_NAME,
                            new String[]{AlarmContract.AlarmsColumns._ID},
                            AlarmContract.AlarmsColumns._ID + " = ?",
                            new String[]{id + ""}, null, null, null);
                    if (cursor.moveToFirst()) {
                        // Record exists. Remove the id so sqlite can generate a new one.
                        values.putNull(AlarmContract.AlarmsColumns._ID);
                    }
                }
            }

            rowId = db.insert(ALARMS_TABLE_NAME, AlarmContract.AlarmsColumns.RINGTONE, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        if (rowId < 0) {
            throw new SQLException("Failed to insert row");
        }
        Log.d("Added alarm rowId = " + rowId);

        return rowId;
    }
}
