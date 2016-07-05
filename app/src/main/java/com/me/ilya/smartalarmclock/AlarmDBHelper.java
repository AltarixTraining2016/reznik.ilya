package com.me.ilya.smartalarmclock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya on 7/1/2016.
 */
public class AlarmDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "alarmclock.db";

    private static final String SQL_CREATE_ALARM = "CREATE TABLE " + AlarmItem.COLUMN_NAMES[0]+ " (" +
            AlarmItem.COLUMN_NAMES[1] + " INTEGER PRIMARY KEY AUTOINCREMENT," +//id
            AlarmItem.COLUMN_NAMES[2] + " TEXT," +//name
            AlarmItem.COLUMN_NAMES[3]+ " INTEGER," +//hour
            AlarmItem.COLUMN_NAMES[4] + " INTEGER," +//minute
            AlarmItem.COLUMN_NAMES[5] + " TEXT," +//song
            AlarmItem.COLUMN_NAMES[6]+ " TEXT," +//days
            AlarmItem.COLUMN_NAMES[7] + " BOOLEAN" +
            " )";

    private static final String SQL_DELETE_ALARM =
            "DROP TABLE IF EXISTS " +  AlarmItem.COLUMN_NAMES[0];

    public AlarmDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ALARM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ALARM);
        onCreate(db);
    }

    private AlarmItem populateAlarm(Cursor c) {
        return AlarmItem.fromCursor(c);
    }

    private ContentValues populateContent(AlarmItem alarmItem){
        ContentValues values = new ContentValues();
        values.put(AlarmItem.COLUMN_NAMES[2],alarmItem.getName());
        values.put(AlarmItem.COLUMN_NAMES[3], alarmItem.getTimeHour());
        values.put(AlarmItem.COLUMN_NAMES[4],alarmItem.getTimeMinute());
        values.put(AlarmItem.COLUMN_NAMES[5], alarmItem.getSong().getUri());
        String repeatingDays = "";
        for (int i = 0; i < 7; ++i) {
            repeatingDays +=alarmItem.getDay(i) + ",";
        }
        values.put(AlarmItem.COLUMN_NAMES[6], repeatingDays);
        values.put(AlarmItem.COLUMN_NAMES[7],alarmItem.isEnabled());

        return values;
    }

    public long createAlarm(AlarmItem alarmItem){
        ContentValues values = populateContent(alarmItem);
        return getWritableDatabase().insert(AlarmItem.COLUMN_NAMES[0], null, values);
    }

    public long updateAlarm(AlarmItem alarmItem) {
        ContentValues values = populateContent(alarmItem);
        return getWritableDatabase().update(AlarmItem.COLUMN_NAMES[0], values,AlarmItem.COLUMN_NAMES[1] + " = ?", new String[] { String.valueOf(alarmItem.getId()) });
    }

    public AlarmItem getAlarm(long id){
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + AlarmItem.COLUMN_NAMES[0] + " WHERE " + AlarmItem.COLUMN_NAMES[1]+ " = " + id;

        Cursor c = db.rawQuery(select, null);

        if (c.moveToNext()) {
            return populateAlarm(c);
        }

        return null;
    }

    public List<AlarmItem> getAlarms() {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + AlarmItem.COLUMN_NAMES[0];

        Cursor c = db.rawQuery(select, null);

        List<AlarmItem> alarmList = new ArrayList<AlarmItem>();

        while (c.moveToNext()) {
            alarmList.add(populateAlarm(c));
        }

        if (!alarmList.isEmpty()) {
            return alarmList;
        }

        return null;
    }

    public int deleteAlarm(long id) {
        return getWritableDatabase().delete(AlarmItem.COLUMN_NAMES[0], AlarmItem.COLUMN_NAMES[1]+ " = ?", new String[] { String.valueOf(id) });
    }
}