package com.kozzztya.cycletraining.db.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.entities.Program;

public class ProgramsHelper extends TableHelper<Program> {

    public static final String TABLE_NAME = "programs";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PURPOSE = "purpose";
    public static final String COLUMN_WEEKS = "weeks";
    public static final String COLUMN_MESOCYCLE = "mesocycle";

    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + COLUMN_NAME + " text, "
            + COLUMN_PURPOSE + " integer, "
            + COLUMN_WEEKS + " integer, "
            + COLUMN_MESOCYCLE + " integer );";

    private static final String CREATE_VIEW = "CREATE VIEW program_data AS \n" +
            "SELECT p._id program, t.cycle, s.training, s.reps, s.weight\n" +
            "FROM programs p, mesocycles m, cycles c, trainings t, sets s\n" +
            "WHERE p.mesocycle = m._id AND c.mesocycle = m._id AND t.cycle = c._id AND s.training = t._id;";

    public ProgramsHelper(Context context) {
        super(context);
    }

    public static void onCreate(SQLiteDatabase database) {
        Log.v(DBHelper.LOG_TAG, TABLE_NAME + " table creating");
        database.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.v(ProgramsHelper.class.getName(), "Upgrading database from version "
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
        return new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_PURPOSE, COLUMN_WEEKS, COLUMN_MESOCYCLE};
    }

    @Override
    public ContentValues getContentValues(Program entity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, entity.getName());
        values.put(COLUMN_PURPOSE, entity.getPurpose());
        values.put(COLUMN_WEEKS, entity.getWeeks());
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
                cursor.getLong(cursor.getColumnIndex(COLUMN_MESOCYCLE)));
    }

}