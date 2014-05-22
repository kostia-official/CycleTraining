package com.kozzztya.cycletraining.db.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.MyDBHelper;
import com.kozzztya.cycletraining.db.entities.Program;

import java.util.ArrayList;
import java.util.List;

public class ProgramsHelper {
    private MyDBHelper myDBHelper;

    public static final String TABLE_NAME = "programs";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PURPOSE = "purpose";
    public static final String COLUMN_WEEKS = "weeks";
    public static final String COLUMN_MESOCYCLE = "mesocycle";

    private static final String TABLE_CREATE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + COLUMN_NAME + " text, "
            + COLUMN_PURPOSE + " integer, "
            + COLUMN_WEEKS + " integer, "
            + COLUMN_MESOCYCLE + " integer );";

    public ProgramsHelper(Context context) {
        myDBHelper = new MyDBHelper(context);
    }

    public static void onCreate(SQLiteDatabase database) {
        Log.v("myDB", TABLE_NAME + " table creating");
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(ProgramsHelper.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public List<Program> getAll() {
        List<Program> programs = new ArrayList<Program>();
        String selectQuery = "SELECT _id, "
                + COLUMN_NAME + ", "
                + COLUMN_PURPOSE + ", "
                + COLUMN_WEEKS + ", "
                + COLUMN_MESOCYCLE + " FROM " + TABLE_NAME;

        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                programs.add(new Program(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getInt(4)
                ));
            } while (cursor.moveToNext());
        }

        return programs;
    }
}