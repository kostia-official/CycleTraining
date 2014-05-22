package com.kozzztya.cycletraining.db.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.MyDBHelper;
import com.kozzztya.cycletraining.db.entities.Set;

import java.util.ArrayList;
import java.util.List;

public class SetsHelper {
    private MyDBHelper myDBHelper;

    public static final String TABLE_NAME = "sets";
    public static final String COLUMN_REPS = "reps";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_TRAINING = "training";

    private static final String TABLE_CREATE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + COLUMN_REPS + " integer, "
            + COLUMN_WEIGHT + " real, "
            + COLUMN_COMMENT + " text, "
            + COLUMN_TRAINING + " integer"
            + ");";

    public SetsHelper(Context context) {
        myDBHelper = new MyDBHelper(context);
    }

    public static void onCreate(SQLiteDatabase database) {
        Log.v("myDB", TABLE_NAME + " table creating");
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(SetsHelper.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public long insert(Set set) {
        Log.v("myDB", "insert in " + TABLE_NAME);
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_REPS, set.getReps());
        values.put(COLUMN_WEIGHT, set.getWeight());
        values.put(COLUMN_COMMENT, set.getComment());
        values.put(COLUMN_TRAINING, set.getTraining());
        long id = db.insert(TABLE_NAME, null, values);
        return id;
    }

    public List<Set> getAllByMesocycle(long mesocycle) {
        Log.v("myDB", "get all from " + TABLE_NAME + "by mesocycle");
        List<Set> sets = new ArrayList<>();
        String selectQuery = "SELECT " + TABLE_NAME + "._id, "
                + TABLE_NAME + "." + COLUMN_REPS + ", "
                + TABLE_NAME + "." + COLUMN_WEIGHT + ", "
                + TABLE_NAME + "." + COLUMN_COMMENT + ", "
                + TABLE_NAME + "." + COLUMN_TRAINING + " "
                + "FROM " + CyclesHelper.TABLE_NAME + ", " + TrainingsHelper.TABLE_NAME + ", " + TABLE_NAME + " " +
                "WHERE " + TABLE_NAME + "." + COLUMN_TRAINING + " = " + TrainingsHelper.TABLE_NAME + "._id AND "
                + TrainingsHelper.TABLE_NAME + "." + TrainingsHelper.COLUMN_CYCLE + " = " + CyclesHelper.TABLE_NAME + "._id AND "
                + CyclesHelper.TABLE_NAME + "." + CyclesHelper.COLUMN_MESOCYCLE + " = " + mesocycle;

        Cursor cursor = myDBHelper.getWritableDatabase().rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                sets.add(new Set(
                        cursor.getLong(0),
                        cursor.getInt(1),
                        cursor.getFloat(2),
                        cursor.getString(3),
                        cursor.getLong(4)
                ));
            } while (cursor.moveToNext());
        }
        return sets;
    }
}