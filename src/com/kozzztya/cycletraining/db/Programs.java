package com.kozzztya.cycletraining.db;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class Programs implements BaseColumns {

    public static final String TABLE_NAME = "programs";
    public static final String DISPLAY_NAME = "name";
    public static final String PURPOSE = "purpose";
    public static final String WEEKS = "weeks";
    public static final String MESOCYCLE = "mesocycle";

    public static final String[] PROJECTION = new String[]{_ID, DISPLAY_NAME, PURPOSE, WEEKS, MESOCYCLE};

    public static final String VIEW_NAME = "programs_view";
    public static final String TRAININGS_IN_WEEK = Mesocycles.TRAININGS_IN_WEEK;

    public static final String[] PROJECTION_VIEW = new String[]{_ID, DISPLAY_NAME, PURPOSE,
            WEEKS, MESOCYCLE, TRAININGS_IN_WEEK};

    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + DISPLAY_NAME + " text, "
            + PURPOSE + " integer, "
            + WEEKS + " integer, "
            + MESOCYCLE + " integer );";

    private static final String CREATE_TRIGGER_DELETE = "CREATE TRIGGER delete_program " +
            "BEFORE DELETE ON " + TABLE_NAME + " " +
            "FOR EACH ROW BEGIN " +
            "DELETE FROM " + Mesocycles.TABLE_NAME +
            " WHERE _id = old." + MESOCYCLE + "; END";

    private static final String CREATE_VIEW = "CREATE VIEW " + VIEW_NAME + " AS " +
            "SELECT " + TRAININGS_IN_WEEK + ", p.* FROM " +
            TABLE_NAME + " p, " + Mesocycles.TABLE_NAME + " m " +
            "WHERE p." + MESOCYCLE + " = m._id;";

    static void onCreate(SQLiteDatabase database) {
        Log.v(DatabaseHelper.TAG, CREATE_TABLE);
        database.execSQL(CREATE_TABLE);
        Log.v(DatabaseHelper.TAG, CREATE_VIEW);
        database.execSQL(CREATE_VIEW);
        database.execSQL(CREATE_TRIGGER_DELETE);
    }

    static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // Cascade delete data with autoincrement id that can't be replaced
        database.execSQL("DELETE FROM " + TABLE_NAME);

        // Recreate table if it was created before stable version
        if (oldVersion <= DatabaseHelper.DATABASE_VERSION_STABLE) {
            database.execSQL("DROP VIEW IF EXISTS " + VIEW_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(database);
        }
    }
}