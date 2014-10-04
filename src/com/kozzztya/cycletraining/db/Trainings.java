package com.kozzztya.cycletraining.db;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class Trainings implements BaseColumns {

    public static final String TABLE_NAME = "trainings";
    public static final String DATE = "date";
    public static final String MESOCYCLE = "mesocycle";
    public static final String COMMENT = "comment";
    public static final String PRIORITY = "priority";
    public static final String IS_DONE = "is_done";

    public static final String[] PROJECTION = new String[]{_ID, DATE, MESOCYCLE, COMMENT, IS_DONE, PRIORITY};

    public static final String VIEW_NAME = "trainings_view";
    public static final String EXERCISE = TrainingJournal.EXERCISE;

    public static final String[] PROJECTION_VIEW = new String[]{EXERCISE, _ID, DATE, MESOCYCLE,
            COMMENT, PRIORITY, IS_DONE};

    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + DATE + " date, "
            + MESOCYCLE + " integer, "
            + COMMENT + " text, "
            + IS_DONE + " integer default 0, "
            + PRIORITY + " integer default 100" //Set max priority to add new training into end of order
            + ");";

    private static final String CREATE_VIEW = "CREATE VIEW " + VIEW_NAME + " AS " +
            "SELECT e." + Exercises.DISPLAY_NAME + " " + EXERCISE + ", t.* " +
            "FROM " + TABLE_NAME + " AS t " +
            "INNER JOIN " + Mesocycles.TABLE_NAME + " AS m ON t." + MESOCYCLE + " = m._id " +
            "INNER JOIN " + TrainingJournal.TABLE_NAME + " AS tj ON tj." + TrainingJournal.MESOCYCLE + " = m._id " +
            "INNER JOIN " + Exercises.TABLE_NAME + " AS e ON tj." + TrainingJournal.EXERCISE + " = e._id " +
            "WHERE m." + Mesocycles.IS_ACTIVE + " = 1;";

    private static final String CREATE_TRIGGER_DELETE = "CREATE TRIGGER delete_training " +
            "BEFORE DELETE ON " + TABLE_NAME + " " +
            "FOR EACH ROW BEGIN " +
            "DELETE FROM " + Sets.TABLE_NAME + " WHERE " +
            Sets.TRAINING + " = old._id; END ";

    static void onCreate(SQLiteDatabase database) {
        Log.v(DatabaseHelper.TAG, CREATE_TABLE);
        database.execSQL(CREATE_TABLE);
        Log.v(DatabaseHelper.TAG, CREATE_VIEW);
        database.execSQL(CREATE_VIEW);
        database.execSQL(CREATE_TRIGGER_DELETE);
    }

    static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // Recreate table if it was created before stable version
        if (oldVersion <= DatabaseHelper.DATABASE_VERSION_STABLE) {
            database.execSQL("DELETE FROM " + TABLE_NAME);
            database.execSQL("DROP VIEW IF EXISTS " + VIEW_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(database);
        }
    }
}