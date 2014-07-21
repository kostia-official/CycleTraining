package com.kozzztya.cycletraining.db.datasources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.entities.ExerciseType;

public class ExerciseTypesDataSource extends DataSource<ExerciseType> {
    public static final String TABLE_NAME = "exercise_types";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_DESCRIPTION + " text "
            + ");";

    public ExerciseTypesDataSource(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.v("myDB", TABLE_NAME + " table creating");
        database.execSQL(DATABASE_CREATE);
        fillCoreData(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        Log.v(DBHelper.LOG_TAG, "Upgrading table " + TABLE_NAME + " from version "
                + oldVersion + " to " + newVersion);
        database.execSQL("DELETE FROM " + TABLE_NAME);
        fillCoreData(database);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getColumns() {
        return new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION};
    }

    @Override
    public ContentValues getContentValues(ExerciseType entity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, entity.getName());
        values.put(COLUMN_DESCRIPTION, entity.getDescription());
        return values;
    }

    @Override
    public ExerciseType entityFromCursor(Cursor cursor) {
        return new ExerciseType(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
        );
    }

}
