package com.kozzztya.cycletraining.db.helpers;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ExercisesMusclesHelper {
    public static final String TABLE_NAME = "exercises_muscles";
    public static final String COLUMN_MUSCLE = "muscle";
    public static final String COLUMN_EXERCISE = "exercise";

    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + COLUMN_MUSCLE + " integer not null, "
            + COLUMN_EXERCISE + " integer not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        Log.v("myDB", TABLE_NAME + " table creating");
        database.execSQL(CREATE_TABLE);
        fillData(database);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.v(ExercisesHelper.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    private static void fillData(SQLiteDatabase database){
        Log.v("myDB", TABLE_NAME + " data filling");
    }
}
