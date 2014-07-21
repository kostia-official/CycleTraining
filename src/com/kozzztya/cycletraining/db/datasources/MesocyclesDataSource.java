package com.kozzztya.cycletraining.db.datasources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.entities.Mesocycle;
import com.kozzztya.cycletraining.db.entities.MesocycleView;

public class MesocyclesDataSource extends DataSourceView<Mesocycle, MesocycleView> {

    public static final String TABLE_NAME = "mesocycles";
    public static final String COLUMN_RM = "rm";
    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_DESCRIPTION = "description";

    public static final String VIEW_NAME = "mesocycles_view";
    public static final String COLUMN_EXERCISE = TrainingJournalDataSource.COLUMN_EXERCISE;
    public static final String COLUMN_TRAININGS_IN_WEEK = ProgramsDataSource.COLUMN_TRAININGS_IN_WEEK;

    private static final String CREATE_TABLE = "create table " +
            TABLE_NAME +
            " (_id integer primary key autoincrement, " +
            COLUMN_RM + " real, " +
            COLUMN_ACTIVE + " integer default 0, " +
            COLUMN_DESCRIPTION + " text);";

    private static final String CREATE_VIEW = "CREATE VIEW " + VIEW_NAME + " AS " +
            "SELECT m._id, m.rm, m.active, m.description, e.name exercise, p.trainings_in_week " +
            "FROM mesocycles m, training_journal tj, programs p, exercises e " +
            "WHERE tj.mesocycle = m._id AND tj.program = p._id AND tj.exercise = e._id;";

    private static final String DELETE_TRIGGER = "CREATE TRIGGER delete_mesocycle " +
            "BEFORE DELETE ON " + TABLE_NAME + " " +
            "FOR EACH ROW BEGIN " +
            " DELETE FROM " + TrainingsDataSource.TABLE_NAME +
            " WHERE " + TrainingsDataSource.COLUMN_MESOCYCLE + " = old._id; " +
            " DELETE FROM " + TrainingJournalDataSource.TABLE_NAME +
            " WHERE " + TrainingJournalDataSource.COLUMN_MESOCYCLE + " = old._id; " +
            "END";

    public MesocyclesDataSource(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.v("myDB", CREATE_TABLE);
        database.execSQL(CREATE_TABLE);
        Log.v("myDB", CREATE_VIEW);
        database.execSQL(CREATE_VIEW);
        database.execSQL(DELETE_TRIGGER);
        fillCoreData(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        Log.v(DBHelper.LOG_TAG, "Upgrading table " + TABLE_NAME + " from version "
                + oldVersion + " to " + newVersion);
        fillCoreData(database);
    }

    @Override
    public ContentValues getContentValues(Mesocycle entity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_RM, entity.getRm());
        values.put(COLUMN_ACTIVE, entity.isActive());
        return values;
    }

    @Override
    public Mesocycle entityFromCursor(Cursor cursor) {
        return new Mesocycle(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getFloat(cursor.getColumnIndex(COLUMN_RM)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_ACTIVE)) > 0,
                cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
        );
    }

    @Override
    public MesocycleView entityViewFromCursor(Cursor cursor) {
        return new MesocycleView(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getFloat(cursor.getColumnIndex(COLUMN_RM)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_ACTIVE)) > 0,
                cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndex(COLUMN_EXERCISE)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_TRAININGS_IN_WEEK))
        );
    }

    @Override
    public String[] getColumns() {
        return new String[]{COLUMN_ID, COLUMN_RM, COLUMN_ACTIVE, COLUMN_DESCRIPTION};
    }

    @Override
    public String[] getViewColumns() {
        return new String[]{COLUMN_ID, COLUMN_RM, COLUMN_ACTIVE, COLUMN_DESCRIPTION, COLUMN_EXERCISE, COLUMN_TRAININGS_IN_WEEK};
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getViewName() {
        return VIEW_NAME;
    }

}
