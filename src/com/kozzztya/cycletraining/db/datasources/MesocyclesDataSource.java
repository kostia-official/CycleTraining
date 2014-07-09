package com.kozzztya.cycletraining.db.datasources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.entities.Mesocycle;

public class MesocyclesDataSource extends DataSource<Mesocycle> {

    public static final String TABLE_NAME = "mesocycles";
    public static final String COLUMN_RM = "rm";
    public static final String COLUMN_EXERCISE = "exercise";
    public static final String COLUMN_ACTIVE = "active";

    private static final String CREATE_TABLE = "create table " +
            TABLE_NAME +
            " (_id integer primary key autoincrement, " +
            COLUMN_RM + " real, " +
            COLUMN_EXERCISE + " integer, " +
            COLUMN_ACTIVE + " integer default 0);";

    private static final String DELETE_TRIGGER = "CREATE TRIGGER delete_mesocycle " +
            "BEFORE DELETE ON " + TABLE_NAME + " " +
            "FOR EACH ROW BEGIN " +
            " DELETE FROM " + TrainingsDataSource.TABLE_NAME +
            " WHERE " + TrainingsDataSource.COLUMN_MESOCYCLE + " = old._id; " +
            " DELETE FROM " + TrainingJournalDataSource.TABLE_NAME +
            " WHERE " + TrainingJournalDataSource.COLUMN_MESOCYCLE + " = old._id; " +
            "END";

    public MesocyclesDataSource(DBHelper dbHelper, Context context) {
        super(dbHelper, context);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public void onCreate(SQLiteDatabase database) {
        Log.v("myDB", CREATE_TABLE);
        database.execSQL(CREATE_TABLE);
        database.execSQL(DELETE_TRIGGER);
        fillData(database);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        Log.v(ExercisesDataSource.class.getName(), "Upgrading database from version "
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
        return values;
    }

    @Override
    public String[] getColumns() {
        return new String[]{COLUMN_ID, COLUMN_RM, COLUMN_EXERCISE, COLUMN_ACTIVE};
    }

    @Override
    public Mesocycle entityFromCursor(Cursor cursor) {
        return new Mesocycle(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getFloat(cursor.getColumnIndex(COLUMN_RM)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_EXERCISE)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_ACTIVE)) > 0
        );
    }

}
