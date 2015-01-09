package com.kozzztya.cycletraining.db;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.kozzztya.cycletraining.utils.DatabaseBackupUtils;

public class Sets implements BaseColumns {

    public static final String TABLE_NAME = "sets";
    public static final String REPS = "reps";
    public static final String WEIGHT = "weight";
    public static final String COMMENT = "comment";
    public static final String TRAINING = "training";

    public static final String[] PROJECTION = new String[]{_ID, REPS, WEIGHT, COMMENT, TRAINING};

    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + REPS + " text, "
            + WEIGHT + " real, "
            + COMMENT + " text, "
            + TRAINING + " integer"
            + ");";

    private static final int CORE_DATA_ROWS = 5000;

    static void onCreate(SQLiteDatabase database) {
        Log.v(DatabaseHelper.TAG, CREATE_TABLE);
        database.execSQL(CREATE_TABLE);
    }

    static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        String[] backupColumns = new String[]{_ID, REPS, WEIGHT, COMMENT, TRAINING};
        String where = BaseColumns._ID + ">" + CORE_DATA_ROWS;
        DatabaseBackupUtils.backupTable(database, TABLE_NAME, backupColumns, where);

        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);

        DatabaseBackupUtils.restoreTable(database, TABLE_NAME, backupColumns, where);
    }
}