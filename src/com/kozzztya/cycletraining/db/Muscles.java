package com.kozzztya.cycletraining.db;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class Muscles implements BaseColumns {

    public static final String TABLE_NAME = "muscles";
    public static final String DISPLAY_NAME = "name";

    public static final String[] PROJECTION = new String[]{_ID, DISPLAY_NAME};

    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + DISPLAY_NAME + " text not null"
            + ");";

    static void onCreate(SQLiteDatabase database) {
        Log.v(DatabaseHelper.TAG, TABLE_NAME + " table creating");
        database.execSQL(CREATE_TABLE);
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
