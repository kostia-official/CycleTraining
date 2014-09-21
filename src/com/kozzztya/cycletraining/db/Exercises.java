package com.kozzztya.cycletraining.db;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class Exercises implements BaseColumns {

    public static final String TABLE_NAME = "exercises";
    public static final String DISPLAY_NAME = "name";
    public static final String EXERCISE_TYPE = "exercise_type";
    public static final String MUSCLE = "muscle";
    public static final String DESCRIPTION = "description";

    public static final String[] PROJECTION = new String[]{_ID, DISPLAY_NAME, EXERCISE_TYPE,
            MUSCLE, DESCRIPTION};

    private static final String TABLE_CREATE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + DISPLAY_NAME + " text not null, "
            + EXERCISE_TYPE + " integer,"
            + MUSCLE + " integer,"
            + DESCRIPTION + " text"
            + ");";

    static void onCreate(SQLiteDatabase database) {
        Log.v(DatabaseHelper.TAG, TABLE_NAME + " table creating");
        database.execSQL(TABLE_CREATE);
    }

    static void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        // Recreate table if it was created before stable version
        if (oldVersion <= DatabaseHelper.DATABASE_VERSION_STABLE) {
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(database);
        }
    }
}