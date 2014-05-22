package com.kozzztya.cycletraining.db.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.MyDBHelper;
import com.kozzztya.cycletraining.db.entities.Cycle;

import java.util.ArrayList;
import java.util.List;

public class CyclesHelper {
    private MyDBHelper myDBHelper;

    public static final String TABLE_NAME = "cycles";
    public static final String COLUMN_INTERVAL = "interval";
    public static final String COLUMN_MESOCYCLE = "mesocycle";

    //TODO триггер на установку default интервала
    private static final String TABLE_CREATE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + COLUMN_INTERVAL + " integer,"
            + COLUMN_MESOCYCLE + " integer);";

    public CyclesHelper(Context context) {
        myDBHelper = new MyDBHelper(context);
    }

    public static void onCreate(SQLiteDatabase database) {
        Log.v("myDB", TABLE_NAME + " table creating");
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.v(ExercisesHelper.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public long insert(Cycle cycle) {
        Log.v("myDB", "insert in " + TABLE_NAME);
        ContentValues values = new ContentValues();
        values.put(COLUMN_INTERVAL, cycle.getInterval());
        values.put(COLUMN_MESOCYCLE, cycle.getMesocycle());
        long id = myDBHelper.getWritableDatabase().insert(TABLE_NAME, null, values);
        return id;
    }

    public List<Cycle> getAll(long mesocycle) {
        List<Cycle> cycles = new ArrayList<Cycle>();
        String selectQuery = "SELECT _id, " + COLUMN_INTERVAL + ", "
                + COLUMN_MESOCYCLE + " FROM " + TABLE_NAME +
                " WHERE " + COLUMN_MESOCYCLE + " = " + mesocycle;

        Cursor cursor = myDBHelper.getWritableDatabase().rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                cycles.add(new Cycle(
                        cursor.getLong(0),
                        cursor.getInt(1),
                        cursor.getLong(2)
                ));
            } while (cursor.moveToNext());
        }
        return cycles;
    }
}
