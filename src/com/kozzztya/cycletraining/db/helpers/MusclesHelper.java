package com.kozzztya.cycletraining.db.helpers;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MusclesHelper {
    public static final String TABLE_NAME = "muscles";
    public static final String COLUMN_NAME = "name";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + COLUMN_NAME + " text not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        Log.v("myDB", TABLE_NAME + " table creating");
        database.execSQL(DATABASE_CREATE);
        //fillData(database);
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
        String sql = "INSERT INTO muscles (name) VALUES "
                + "('грудь'), "
                + "('спина'), "
                + "('плечи'), "
                + "('трицепс'), "
                + "('бицепс');";
        database.execSQL(sql);
    }
}
