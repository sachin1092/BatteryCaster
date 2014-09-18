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
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class AlarmProvider extends ContentProvider {
    private AlarmDatabaseHelper mOpenHelper;

    private static final int ALARMS = 1;
    private static final int ALARMS_ID = 2;
    private static final int INSTANCES = 3;
    private static final int INSTANCES_ID = 4;
    private static final int CITIES = 5;
    private static final int CITIES_ID = 6;

    private static final UriMatcher sURLMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURLMatcher.addURI(AlarmContract.AUTHORITY, "alarms", ALARMS);
        sURLMatcher.addURI(AlarmContract.AUTHORITY, "alarms/#", ALARMS_ID);
        sURLMatcher.addURI(AlarmContract.AUTHORITY, "instances", INSTANCES);
        sURLMatcher.addURI(AlarmContract.AUTHORITY, "instances/#", INSTANCES_ID);
        sURLMatcher.addURI(AlarmContract.AUTHORITY, "cities", CITIES);
        sURLMatcher.addURI(AlarmContract.AUTHORITY, "cities/*", CITIES_ID);
    }

    public AlarmProvider() {
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new AlarmDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projectionIn, String selection, String[] selectionArgs,
            String sort) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // Generate the body of the query
        int match = sURLMatcher.match(uri);
        switch (match) {
            case ALARMS:
                qb.setTables(AlarmDatabaseHelper.ALARMS_TABLE_NAME);
                break;
            case ALARMS_ID:
                qb.setTables(AlarmDatabaseHelper.ALARMS_TABLE_NAME);
                qb.appendWhere(AlarmContract.AlarmsColumns._ID + "=");
                qb.appendWhere(uri.getLastPathSegment());
                break;
           
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor ret = qb.query(db, projectionIn, selection, selectionArgs,
                              null, null, sort);

        if (ret == null) {
            Log.d("Alarms.query: failed");
        } else {
            ret.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return ret;
    }

    @Override
    public String getType(Uri uri) {
        int match = sURLMatcher.match(uri);
        switch (match) {
            case ALARMS:
                return "vnd.android.cursor.dir/alarms";
            case ALARMS_ID:
                return "vnd.android.cursor.item/alarms";
            default:
                throw new IllegalArgumentException("Unknown URL");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        int count;
        String alarmId;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sURLMatcher.match(uri)) {
            case ALARMS_ID:
                alarmId = uri.getLastPathSegment();
                count = db.update(AlarmDatabaseHelper.ALARMS_TABLE_NAME, values,
                        AlarmContract.AlarmsColumns._ID + "=" + alarmId,
                        null);
                break;
            
            default: {
                throw new UnsupportedOperationException(
                        "Cannot update URL: " + uri);
            }
        }
         Log.d("*** notifyChange() id: " + alarmId + " url " + uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        long rowId;
        switch (sURLMatcher.match(uri)) {
            case ALARMS:
                rowId = mOpenHelper.fixAlarmInsert(initialValues);
                break;
            default:
                throw new IllegalArgumentException("Cannot insert from URL: " + uri);
        }

        Uri uriResult = ContentUris.withAppendedId(AlarmContract.AlarmsColumns.CONTENT_URI, rowId);
        getContext().getContentResolver().notifyChange(uriResult, null);
        return uriResult;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        int count;
        String primaryKey;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sURLMatcher.match(uri)) {
            case ALARMS:
                count = db.delete(AlarmDatabaseHelper.ALARMS_TABLE_NAME, where, whereArgs);
                break;
            case ALARMS_ID:
                primaryKey = uri.getLastPathSegment();
                if (TextUtils.isEmpty(where)) {
                    where = AlarmContract.AlarmsColumns._ID + "=" + primaryKey;
                } else {
                    where = AlarmContract.AlarmsColumns._ID + "=" + primaryKey +
                            " AND (" + where + ")";
                }
                count = db.delete(AlarmDatabaseHelper.ALARMS_TABLE_NAME, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete from URL: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
