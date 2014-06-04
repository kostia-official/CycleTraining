package com.kozzztya.cycletraining.db.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.MyDBHelper;
import com.kozzztya.cycletraining.db.entities.Mesocycle;

import java.util.List;

public class MesocyclesHelper implements TableHelper<Mesocycle> {

    private MyDBHelper myDBHelper;

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
        myDBHelper = new MyDBHelper(context);
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
    public long insert(Mesocycle mesocycle) {
        Log.v("myDB", "insert in " + TABLE_NAME);
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RM, mesocycle.getRm());
        values.put(COLUMN_EXERCISE, mesocycle.getExercise());
        values.put(COLUMN_ACTIVE, mesocycle.isActive());
        values.put(COLUMN_TRAININGS_IN_WEEK, mesocycle.getTrainingsInWeek());
        return db != null ? db.insert(TABLE_NAME, null, values) : -1;
    }

    @Override
    public boolean update(Mesocycle mesocycle) {
        Log.v("myDB", "UPDATE " + TABLE_NAME);
        ContentValues values = new ContentValues();
        values.put(COLUMN_RM, mesocycle.getRm());
        values.put(COLUMN_EXERCISE, mesocycle.getExercise());
        values.put(COLUMN_ACTIVE, mesocycle.isActive());
        values.put(COLUMN_TRAININGS_IN_WEEK, mesocycle.getTrainingsInWeek());
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        return db != null && db.update(TABLE_NAME, values,
                COLUMN_ID + " = " + mesocycle.getId(), null) != 0;
    }

    @Override
    public boolean delete(long id) {
        Log.v("myDB", "DELETE FROM " + TABLE_NAME);
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        return db != null && db.delete(TABLE_NAME, "_id = " + id, null) != 0;
    }

    @Override
    public List<Mesocycle> select(String selection, String groupBy, String having, String orderBy) {
        return null;
    }

    @Override
    public String[] getColumns() {
        return new String[]{COLUMN_ID, COLUMN_RM, COLUMN_EXERCISE, COLUMN_ACTIVE, COLUMN_TRAININGS_IN_WEEK};
    }

    @Override
    public List<Mesocycle> entityFromCursor(Cursor cursor) {
        return null;
    }

    @Override
    public Mesocycle getEntity(long id) {
        SQLiteDatabase db = myDBHelper.getReadableDatabase();

        String where = "_id =" + id;
        if (db != null) {
            Cursor cursor = db.query(TABLE_NAME, getColumns(), where, null, null, null, null);
            cursor.moveToFirst();
            return new Mesocycle(
                    cursor.getLong(0),
                    cursor.getFloat(1),
                    cursor.getLong(2),
                    cursor.getInt(3) > 0,
                    cursor.getInt(4)
            );
        }
        return null;
    }

}
