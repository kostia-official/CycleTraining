package com.kozzztya.cycletraining.db.datasources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.SetView;

public class SetsDataSource extends DataSourceView<Set, SetView> {
    public static final String TABLE_NAME = "sets";
    public static final String COLUMN_REPS = "reps";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_TRAINING = "training";

    public static final String VIEW_NAME = "sets_parents_view";
    public static final String COLUMN_MESOCYCLE = TrainingsDataSource.COLUMN_MESOCYCLE;

    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + COLUMN_REPS + " text, "
            + COLUMN_WEIGHT + " real, "
            + COLUMN_COMMENT + " text, "
            + COLUMN_TRAINING + " integer"
            + ");";

    //Представление позволяет делать выборку по родительским таблицам
    private static final String CREATE_VIEW = "CREATE VIEW " + VIEW_NAME + " AS " +
            "SELECT t." + TrainingsDataSource.COLUMN_MESOCYCLE + " as " + COLUMN_MESOCYCLE + ", s." +
            COLUMN_TRAINING + " as " + COLUMN_TRAINING + ", s._id as _id, s." + COLUMN_REPS + " as " + COLUMN_REPS + ", s." +
            COLUMN_WEIGHT + " as " + COLUMN_WEIGHT + ", s." + COLUMN_COMMENT + " as " + COLUMN_COMMENT +
            " FROM " + TABLE_NAME + " s, " + TrainingsDataSource.TABLE_NAME + " t, " +
            MesocyclesDataSource.TABLE_NAME + " m " +
            "WHERE s." + COLUMN_TRAINING + " = t._id AND t." + COLUMN_MESOCYCLE + " = m._id;";

    public SetsDataSource(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.v(DBHelper.LOG_TAG, CREATE_TABLE);
        database.execSQL(CREATE_TABLE);
        Log.v(DBHelper.LOG_TAG, CREATE_VIEW);
        database.execSQL(CREATE_VIEW);
        fillCoreData(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        Log.v(DBHelper.LOG_TAG, "Upgrading table " + TABLE_NAME + " from version "
                + oldVersion + " to " + newVersion);
        fullDelete(database);
        onCreate(database);
//        fillCoreData(database);
    }

    @Override
    public ContentValues getContentValues(Set entity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_REPS, entity.getReps());
        values.put(COLUMN_WEIGHT, entity.getWeight());
        values.put(COLUMN_COMMENT, entity.getComment());
        values.put(COLUMN_TRAINING, entity.getTraining());
        return values;
    }

    @Override
    public Set entityFromCursor(Cursor cursor) {
        return new Set(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_REPS)),
                cursor.getFloat(cursor.getColumnIndex(COLUMN_WEIGHT)),
                cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_TRAINING)));
    }

    @Override
    public SetView entityViewFromCursor(Cursor cursor) {
        return new SetView(
                cursor.getLong(cursor.getColumnIndex(COLUMN_MESOCYCLE)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_REPS)),
                cursor.getFloat(cursor.getColumnIndex(COLUMN_WEIGHT)),
                cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_TRAINING))
        );
    }

    @Override
    public String[] getColumns() {
        return new String[]{COLUMN_ID, COLUMN_REPS, COLUMN_WEIGHT, COLUMN_COMMENT, COLUMN_TRAINING};
    }

    @Override
    public String[] getViewColumns() {
        return new String[]{COLUMN_MESOCYCLE, COLUMN_TRAINING, COLUMN_ID, COLUMN_REPS, COLUMN_WEIGHT, COLUMN_COMMENT};
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