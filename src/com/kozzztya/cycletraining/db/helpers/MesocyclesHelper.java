package com.kozzztya.cycletraining.db.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.MyDBHelper;
import com.kozzztya.cycletraining.db.entities.Mesocycle;

public class MesocyclesHelper {

    private MyDBHelper myDBHelper;

    public static final String TABLE_NAME = "mesocycles";
    public static final String COLUMN_RM = "rm";
    public static final String COLUMN_EXERCISE = "exercise";

    private static final String TABLE_CREATE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + COLUMN_RM + " real, "
            + COLUMN_EXERCISE + " integer);";

    public MesocyclesHelper(Context context) {
        myDBHelper = new MyDBHelper(context);
    }

    public static void onCreate(SQLiteDatabase database) {
        Log.v("myDB", TABLE_NAME + " table creating");
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(ExercisesHelper.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public long insert(Mesocycle mesocycle) {
        Log.v("myDB", "insert in " + TABLE_NAME);
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RM, mesocycle.getRm());
        values.put(COLUMN_EXERCISE, mesocycle.getExercise());
        long id = db.insert(TABLE_NAME, null, values);
        return id;
    }
}
