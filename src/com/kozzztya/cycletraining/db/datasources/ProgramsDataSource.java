package com.kozzztya.cycletraining.db.datasources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.entities.Program;

public class ProgramsDataSource extends DataSource<Program> {

    public static final String TABLE_NAME = "programs";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PURPOSE = "purpose";
    public static final String COLUMN_WEEKS = "weeks";
    public static final String COLUMN_TRAININGS_IN_WEEK = "trainings_in_week";
    public static final String COLUMN_MESOCYCLE = "mesocycle";

    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + COLUMN_NAME + " text, "
            + COLUMN_PURPOSE + " integer, "
            + COLUMN_WEEKS + " integer, "
            + COLUMN_TRAININGS_IN_WEEK + " integer not null, "
            + COLUMN_MESOCYCLE + " integer );";

    public ProgramsDataSource(DBHelper dbHelper, Context context) {
        super(dbHelper, context);
    }

    public void onCreate(SQLiteDatabase database) {
        Log.v(DBHelper.LOG_TAG, TABLE_NAME + " table creating");
        database.execSQL(CREATE_TABLE);
        fillData(database);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        Log.v(ProgramsDataSource.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getColumns() {
        return new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_PURPOSE, COLUMN_WEEKS, COLUMN_MESOCYCLE, COLUMN_TRAININGS_IN_WEEK};
    }

    @Override
    public ContentValues getContentValues(Program entity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, entity.getName());
        values.put(COLUMN_PURPOSE, entity.getPurpose());
        values.put(COLUMN_WEEKS, entity.getWeeks());
        values.put(COLUMN_TRAININGS_IN_WEEK, entity.getTrainingsInWeek());
        values.put(COLUMN_MESOCYCLE, entity.getMesocycle());
        return values;
    }

    @Override
    public Program entityFromCursor(Cursor cursor) {
        return new Program(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_PURPOSE)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_WEEKS)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_TRAININGS_IN_WEEK)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_MESOCYCLE)));
    }
}