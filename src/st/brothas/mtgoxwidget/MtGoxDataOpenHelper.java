/*
 * Copyright (c) 2010-2011 Gösta Jonasson. All Rights Reserved.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package st.brothas.mtgoxwidget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static st.brothas.mtgoxwidget.MtGoxWidgetProvider.LOG_TAG;

public class MtGoxDataOpenHelper extends SQLiteOpenHelper {
	
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "mtgox";
    private static final String TICKER_DATA_TABLE_NAME = "ticker_data";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_HIGH = "high";
    private static final String COLUMN_LOW = "low";
    private static final String COLUMN_LAST = "last";
    private static final String COLUMN_BUY = "buy";
    private static final String COLUMN_SELL = "sell";
    private static final String LAST_TABLE_CREATE =
                "CREATE TABLE " + TICKER_DATA_TABLE_NAME + " (" +
                        COLUMN_TIMESTAMP + " INTEGER, " +
                        COLUMN_HIGH + " REAL, " +
                        COLUMN_LOW + " REAL, " +
                        COLUMN_LAST + " REAL, " +
                        COLUMN_BUY + " REAL, " +
                        COLUMN_SELL + " REAL);";
	private static final String QUERY_COUNT_LAST_VALUES = "SELECT COUNT(*) FROM "
			+ TICKER_DATA_TABLE_NAME + ";";
	private static final String QUERY_LAST_TICKER_DATA = "SELECT * FROM " +
			TICKER_DATA_TABLE_NAME + " ORDER BY "
			+ COLUMN_TIMESTAMP + " DESC LIMIT 1;";
    private static final String QUERY_NEWEST_TICKER_DATA = "SELECT * FROM " +
            TICKER_DATA_TABLE_NAME + " WHERE " + COLUMN_TIMESTAMP + " > ? " +
            "ORDER BY " + COLUMN_TIMESTAMP + " DESC;";
	private static final Long TIME_TO_KEEP_DATA_IN_SECS = 60*60*25L; // 25 hours

    MtGoxDataOpenHelper(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
        db.execSQL(LAST_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Log.d(LOG_TAG, "Upgrading DB from " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE " + TICKER_DATA_TABLE_NAME);
        onCreate(db);
	}

	public void storeTickerData(MtGoxTickerData data) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues(4);
		values.put(COLUMN_TIMESTAMP, System.currentTimeMillis()/1000);
		values.put(COLUMN_LOW, data.getLow());
        values.put(COLUMN_HIGH, data.getHigh());
        values.put(COLUMN_LAST, data.getLast());
        values.put(COLUMN_BUY, data.getBuy());
        values.put(COLUMN_SELL, data.getSell());
		long result = db.insert(TICKER_DATA_TABLE_NAME, null, values);
		if (result == -1) {
			Log.e(LOG_TAG, "Error when inserting data: " + data);
		}
		db.close();
	}

	public long getNumberOfLastValues() {
		return getLongFromDb(QUERY_COUNT_LAST_VALUES);
	}
	
	private long getLongFromDb(String sql) {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteStatement statement = db.compileStatement(QUERY_COUNT_LAST_VALUES);
		long count = statement.simpleQueryForLong();
		db.close();
		return count;
	}

	// Returns the last last value or null if no values have been stored.
	public MtGoxTickerData getLastTickerData() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(QUERY_LAST_TICKER_DATA, null);
		MtGoxTickerData lastData = null;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
            lastData = getTickerDataFromCursor(cursor);
		}
		cursor.close();
		db.close();
        //Log.d(LOG_TAG, "Last ticker data2: " + lastData);
		return lastData;
	}

    private MtGoxTickerData getTickerDataFromCursor(Cursor cursor) {
        MtGoxTickerData lastData;
        lastData = new MtGoxTickerData();
        lastData.setLow(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LOW)));
        lastData.setHigh(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HIGH)));
        lastData.setLast(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LAST)));
        lastData.setBuy(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_BUY)));
        lastData.setSell(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SELL)));
        lastData.setTimestamp(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))*1000));
        return lastData;
    }

    public void cleanUp() {
		SQLiteDatabase db = getWritableDatabase();
		Long nowSecs = System.currentTimeMillis()/1000;
		Long timeToDelete = nowSecs - TIME_TO_KEEP_DATA_IN_SECS;
		String[] args = {timeToDelete.toString()};
		db.delete(TICKER_DATA_TABLE_NAME, COLUMN_TIMESTAMP + " < ?", args);
		db.close();
	}

    public List<MtGoxTickerData> getTickerData(Long since) {
        List<MtGoxTickerData> data = new ArrayList<MtGoxTickerData>();
        SQLiteDatabase db = getReadableDatabase();
        String[] selection = {""+(since/1000)};
        Cursor cursor = db.rawQuery(QUERY_NEWEST_TICKER_DATA, selection);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            data.add(getTickerDataFromCursor(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        //Log.d(LOG_TAG, "getTickerData() found " + data.size() + " data points.");
        return data;
    }
}
