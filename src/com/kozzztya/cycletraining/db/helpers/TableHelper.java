package com.kozzztya.cycletraining.db.helpers;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public abstract class TableHelper {
    public static String TABLE_NAME;
    private static String TABLE_CREATE;

    public static void onCreate(SQLiteDatabase database) {
        Log.v("myDB", TABLE_NAME + " table creating");
        database.execSQL(TABLE_CREATE);
        fillData(database);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(ExercisesHelper.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    private static void fillData(SQLiteDatabase database) {
        Log.v("myDB", TABLE_NAME + " data filling");
        String sql = "INSERT INTO exercises (name, exercise_type) "
                + "VALUES ('Жим лёжа', 1)";
        database.execSQL(sql);
    }

}
