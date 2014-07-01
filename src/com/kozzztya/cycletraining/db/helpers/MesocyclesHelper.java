package com.kozzztya.cycletraining.db.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.entities.Mesocycle;

public class MesocyclesHelper extends TableHelper<Mesocycle> {

    public static final String TABLE_NAME = "mesocycles";
    public static final String COLUMN_RM = "rm";
    public static final String COLUMN_EXERCISE = "exercise";
    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_TRAININGS_IN_WEEK = "trainings_in_week";

    private static final String CREATE_TABLE = "create table " +
            TABLE_NAME +
            " (_id integer primary key autoincrement, " +
            COLUMN_RM + " real, " +
            COLUMN_EXERCISE + " integer, " +
            COLUMN_ACTIVE + " integer default 0, " +
            COLUMN_TRAININGS_IN_WEEK + " integer not null);";

    private static final String DELETE_TRIGGER = "CREATE TRIGGER delete_mesocycle " +
            "BEFORE DELETE ON " + TABLE_NAME + " " +
            "FOR EACH ROW BEGIN " +
            " DELETE FROM " + TrainingsHelper.TABLE_NAME +
            " WHERE " + TrainingsHelper.COLUMN_MESOCYCLE + " = old._id; " +
            " DELETE FROM " + TrainingJournalHelper.TABLE_NAME +
            " WHERE " + TrainingJournalHelper.COLUMN_MESOCYCLE + " = old._id; " +
            "END";

    public MesocyclesHelper(Context context) {
        super(context);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public static void onCreate(SQLiteDatabase database) {
        Log.v("myDB", CREATE_TABLE);
        database.execSQL(CREATE_TABLE);
        database.execSQL(DELETE_TRIGGER);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.v(ExercisesHelper.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    @Override
    public ContentValues getContentValues(Mesocycle entity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_RM, entity.getRm());
        values.put(COLUMN_EXERCISE, entity.getExercise());
        values.put(COLUMN_ACTIVE, entity.isActive());
        values.put(COLUMN_TRAININGS_IN_WEEK, entity.getTrainingsInWeek());
        return values;
    }

    @Override
    public String[] getColumns() {
        return new String[]{COLUMN_ID, COLUMN_RM, COLUMN_EXERCISE, COLUMN_ACTIVE, COLUMN_TRAININGS_IN_WEEK};
    }

    @Override
    public Mesocycle entityFromCursor(Cursor cursor) {
        return new Mesocycle(
                cursor.getLong(0),
                cursor.getFloat(1),
                cursor.getLong(2),
                cursor.getInt(3) > 0,
                cursor.getInt(4)
        );
    }

}
