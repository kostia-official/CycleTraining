package com.kozzztya.cycletraining.db;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class TrainingJournal implements BaseColumns {

    public static final String TABLE_NAME = "training_diary";
    public static final String PROGRAM = "program";
    public static final String MESOCYCLE = "mesocycle";
    public static final String EXERCISE = "exercise";
    public static final String BEGIN_DATE = "begin_date";

    public static final String[] PROJECTION = new String[]{_ID, PROGRAM, MESOCYCLE,
            EXERCISE, BEGIN_DATE};

    public static final String VIEW_NAME = "training_diary_view";
    public static final String EXERCISE_NAME = "exercise_name";
    public static final String PROGRAM_NAME = "program_name";

    public static final String[] PROJECTION_VIEW = new String[]{_ID, PROGRAM, MESOCYCLE,
            EXERCISE, BEGIN_DATE, EXERCISE_NAME, PROGRAM_NAME};

    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + PROGRAM + " integer, "
            + MESOCYCLE + " integer,"
            + EXERCISE + " integer, "
            + BEGIN_DATE + " date);";

    private static final String CREATE_VIEW = "CREATE VIEW " + VIEW_NAME + " AS "
            + "SELECT tj.*, e." + Exercises.DISPLAY_NAME + " as " + EXERCISE_NAME +
            ", p." + Programs.DISPLAY_NAME + " as " + PROGRAM_NAME +
            " FROM " + TABLE_NAME + " tj, " + Exercises.TABLE_NAME + " e, " + Programs.TABLE_NAME +
            " p WHERE tj." + EXERCISE + " = e._id AND tj." + PROGRAM + " = p._id;";

    private static final String CREATE_DELETE_TRIGGER = "CREATE TRIGGER delete_training_diary " +
            "BEFORE DELETE ON " + TABLE_NAME + " " +
            "FOR EACH ROW BEGIN " +
            "DELETE FROM " + Mesocycles.TABLE_NAME +
            " WHERE _id = old." + MESOCYCLE + "; END";

    static void onCreate(SQLiteDatabase database) {
        Log.v("myDB", CREATE_TABLE);
        database.execSQL(CREATE_TABLE);
        Log.v("myDB", CREATE_VIEW);
        database.execSQL(CREATE_VIEW);
        database.execSQL(CREATE_DELETE_TRIGGER);
    }

    static void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        // Recreate table if it was created before stable version
        if (oldVersion <= DatabaseHelper.DATABASE_VERSION_STABLE) {
            database.execSQL("DROP VIEW IF EXISTS " + VIEW_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(database);
        }
    }
}