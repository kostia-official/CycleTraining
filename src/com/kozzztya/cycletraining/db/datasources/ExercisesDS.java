package com.kozzztya.cycletraining.db.datasources;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.entities.Exercise;

public class ExercisesDS extends DataSource<Exercise> {

    public static final String TABLE_NAME = "exercises";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EXERCISE_TYPE = "exercise_type";
    public static final String COLUMN_MUSCLE = "muscle";
    public static final String COLUMN_DESCRIPTION = "description";

    private static final String TABLE_CREATE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_EXERCISE_TYPE + " integer,"
            + COLUMN_MUSCLE + " integer,"
            + COLUMN_DESCRIPTION + " text"
            + ");";

    public ExercisesDS(DBHelper dbHelper) {
        super(dbHelper);
    }

    public static void onCreate(SQLiteDatabase database) {
        Log.v(DBHelper.LOG_TAG, TABLE_NAME + " table creating");
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.v(DBHelper.LOG_TAG, "Upgrading table " + TABLE_NAME + " from version "
                + oldVersion + " to " + newVersion);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    @Override
    public String[] getColumns() {
        return new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_EXERCISE_TYPE, COLUMN_MUSCLE, COLUMN_DESCRIPTION};
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues(Exercise entity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, entity.getName());
        values.put(COLUMN_EXERCISE_TYPE, entity.getExerciseType());
        values.put(COLUMN_MUSCLE, entity.getMuscle());
        values.put(COLUMN_DESCRIPTION, entity.getDescription());
        return values;
    }

    @Override
    public Exercise entityFromCursor(Cursor cursor) {
        return new Exercise(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_EXERCISE_TYPE)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_MUSCLE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
        );
    }

}