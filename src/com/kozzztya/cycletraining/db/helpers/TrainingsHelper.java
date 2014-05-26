package com.kozzztya.cycletraining.db.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.MyDBHelper;
import com.kozzztya.cycletraining.db.entities.Training;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class TrainingsHelper {
    private MyDBHelper myDBHelper;

    public static final String TABLE_NAME = "trainings";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_CYCLE = "cycle";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_DONE = "done";

    private static final String TABLE_CREATE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + COLUMN_DATE + " date, "
            + COLUMN_CYCLE + " integer, "
            + COLUMN_COMMENT + " text, "
            + COLUMN_PRIORITY + " integer default 0, "
            + COLUMN_DONE + " integer default 0"
            + ");";

    private static final String DELETE_TRIGGER = "CREATE TRIGGER delete_training " +
            "BEFORE DELETE ON " + TABLE_NAME + " " +
            "FOR EACH ROW BEGIN " +
            " DELETE FROM " + SetsHelper.TABLE_NAME + " WHERE " +
            SetsHelper.COLUMN_TRAINING + " = old._id; END ";

    public TrainingsHelper(Context context) {
        myDBHelper = new MyDBHelper(context);
    }

    public static void onCreate(SQLiteDatabase database) {
        Log.v("myDB", TABLE_NAME + " table creating");
        database.execSQL(TABLE_CREATE);
        database.execSQL(DELETE_TRIGGER);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(TrainingsHelper.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public long insert(Training training) {
        Log.v("myDB", "insert in " + TABLE_NAME);
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, String.valueOf(training.getDate()));
        values.put(COLUMN_CYCLE, training.getCycle());
        values.put(COLUMN_COMMENT, training.getComment());
        values.put(COLUMN_PRIORITY, training.getPriority());
        values.put(COLUMN_DONE, training.isDone());
        long id = db.insert(TABLE_NAME, null, values);
        return id;
    }

    public List<Training> selectByMesocycle(long mesocycle) {
        Log.v("myDB", "get all from " + TABLE_NAME + "by mesocycle");
        List<Training> trainings = new ArrayList<>();
        String selectQuery = "SELECT " + TABLE_NAME + "._id, "
                + TABLE_NAME + "." + COLUMN_DATE + ", "
                + TABLE_NAME + "." + COLUMN_CYCLE + ", "
                + TABLE_NAME + "." + COLUMN_COMMENT + ", "
                + TABLE_NAME + "." + COLUMN_PRIORITY + ", "
                + TABLE_NAME + "." + COLUMN_DONE + " "
                + "FROM " + CyclesHelper.TABLE_NAME + ", " + TABLE_NAME + " " +
                "WHERE " + TABLE_NAME + "." + COLUMN_CYCLE + " = " + CyclesHelper.TABLE_NAME + "._id AND "
                + CyclesHelper.TABLE_NAME + "." + CyclesHelper.COLUMN_MESOCYCLE + " = " + mesocycle;

        Cursor cursor = myDBHelper.getReadableDatabase().rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                trainings.add(new Training(
                        cursor.getLong(0),
                        new Date(cursor.getLong(1)*1000),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        Boolean.valueOf(cursor.getString(5))
                ));
            } while (cursor.moveToNext());
        }
        return trainings;
    }
}