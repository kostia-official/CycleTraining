package com.kozzztya.cycletraining.db.helpers;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public abstract class TableHelper {
    public static String COLUMN_ID = "_id";
    private static String TABLE_CREATE;

    public static void onCreate(SQLiteDatabase database) {
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
    }

    private static void fillData(SQLiteDatabase database) {
        Log.v("myDB", " data filling");
        String sql = "INSERT INTO exercises (name, exercise_type) "
                + "VALUES ('Жим лёжа', 1)";
        database.execSQL(sql);
    }

}
