package com.kozzztya.cycletraining.db.datasources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.entities.Mesocycle;

public class MesocyclesDS extends DataSource<Mesocycle> {

    public static final String TABLE_NAME = "mesocycles";
    public static final String COLUMN_RM = "rm";
    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_TRAININGS_IN_WEEK = "trainings_in_week";

    private static final String CREATE_TABLE = "create table " +
            TABLE_NAME +
            " (_id integer primary key autoincrement, " +
            COLUMN_RM + " real, " +
            COLUMN_ACTIVE + " integer default 0, " +
            COLUMN_TRAININGS_IN_WEEK + " integer not null, " +
            COLUMN_DESCRIPTION + " text);";

    private static final String DELETE_TRIGGER = "CREATE TRIGGER delete_mesocycle " +
            "BEFORE DELETE ON " + TABLE_NAME + " " +
            "FOR EACH ROW BEGIN " +
            " DELETE FROM " + TrainingsDS.TABLE_NAME +
            " WHERE " + TrainingsDS.COLUMN_MESOCYCLE + " = old._id; " +
            " DELETE FROM " + TrainingJournalDS.TABLE_NAME +
            " WHERE " + TrainingJournalDS.COLUMN_MESOCYCLE + " = old._id; " +
            "END";

    public MesocyclesDS(Context context) {
        super(context);
    }

    public static void onCreate(SQLiteDatabase database) {
        Log.v("myDB", CREATE_TABLE);
        database.execSQL(CREATE_TABLE);
        database.execSQL(DELETE_TRIGGER);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        Log.v(DBHelper.LOG_TAG, "Upgrading table " + TABLE_NAME + " from version "
                + oldVersion + " to " + newVersion);
    }

    @Override
    public ContentValues getContentValues(Mesocycle entity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_RM, entity.getRm());
        values.put(COLUMN_ACTIVE, entity.isActive());
        values.put(COLUMN_TRAININGS_IN_WEEK, entity.getTrainingsInWeek());
        values.put(COLUMN_DESCRIPTION, entity.getDescription());
        return values;
    }

    @Override
    public Mesocycle entityFromCursor(Cursor cursor) {
        return new Mesocycle(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getFloat(cursor.getColumnIndex(COLUMN_RM)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_ACTIVE)) > 0,
                cursor.getInt(cursor.getColumnIndex(COLUMN_TRAININGS_IN_WEEK)),
                cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
        );
    }


    @Override
    public String[] getColumns() {
        return new String[]{COLUMN_ID, COLUMN_RM, COLUMN_ACTIVE, COLUMN_TRAININGS_IN_WEEK, COLUMN_DESCRIPTION};
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

}
