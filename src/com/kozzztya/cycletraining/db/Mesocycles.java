package com.kozzztya.cycletraining.db;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.kozzztya.cycletraining.utils.DatabaseBackupUtils;

public class Mesocycles implements BaseColumns {

    public static final String TABLE_NAME = "mesocycles";
    public static final String IS_ACTIVE = "is_active";
    public static final String DESCRIPTION = "description";
    public static final String TRAININGS_IN_WEEK = "trainings_in_week";

    public static final String[] PROJECTION = new String[]{_ID, IS_ACTIVE, TRAININGS_IN_WEEK,
            DESCRIPTION};

    private static final String CREATE_TABLE = "create table " +
            TABLE_NAME +
            " (_id integer primary key autoincrement, " +
            IS_ACTIVE + " integer default 0, " +
            TRAININGS_IN_WEEK + " integer not null, " +
            DESCRIPTION + " text);";

    private static final String DELETE_TRIGGER = "CREATE TRIGGER delete_mesocycle " +
            "BEFORE DELETE ON " + TABLE_NAME + " " +
            "FOR EACH ROW BEGIN " +
            " DELETE FROM " + Trainings.TABLE_NAME +
            " WHERE " + Trainings.MESOCYCLE + " = old._id; " +
            " DELETE FROM " + TrainingJournal.TABLE_NAME +
            " WHERE " + TrainingJournal.MESOCYCLE + " = old._id; " +
            "END";

    private static final int CORE_DATA_ROWS = 100;

    static void onCreate(SQLiteDatabase database) {
        Log.v(DatabaseHelper.TAG, CREATE_TABLE);
        database.execSQL(CREATE_TABLE);
        database.execSQL(DELETE_TRIGGER);
    }

    static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        String[] backupColumns = new String[]{_ID, IS_ACTIVE, TRAININGS_IN_WEEK, DESCRIPTION};
        String where = BaseColumns._ID + ">" + CORE_DATA_ROWS;
        DatabaseBackupUtils.backupTable(database, TABLE_NAME, backupColumns, where);

        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);

        DatabaseBackupUtils.restoreTable(database, TABLE_NAME, backupColumns, where);
    }
}
