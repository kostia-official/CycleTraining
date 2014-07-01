package com.kozzztya.cycletraining.db.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.SetView;

import java.util.ArrayList;
import java.util.List;

public class SetsHelper extends TableHelper<Set> {
    public static final String TABLE_NAME = "sets";
    public static final String COLUMN_REPS = "reps";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_TRAINING = "training";

    public static final String VIEW_NAME = "sets_parents_view";
    public static final String COLUMN_MESOCYCLE = TrainingsHelper.COLUMN_MESOCYCLE;

    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + COLUMN_REPS + " integer, "
            + COLUMN_WEIGHT + " real, "
            + COLUMN_COMMENT + " text, "
            + COLUMN_TRAINING + " integer"
            + ");";

    //Представление позволяет делать выборку по родительским таблицам
    private static final String CREATE_VIEW = "CREATE VIEW " + VIEW_NAME + " AS " +
            "SELECT t." + TrainingsHelper.COLUMN_MESOCYCLE + " as " + COLUMN_MESOCYCLE + ", s." +
            COLUMN_TRAINING + " as " + COLUMN_TRAINING + ", s._id as _id, s." + COLUMN_REPS + " as " + COLUMN_REPS + ", s." +
            COLUMN_WEIGHT + " as " + COLUMN_WEIGHT + ", s." + COLUMN_COMMENT + " as " + COLUMN_COMMENT +
            " FROM " + TABLE_NAME + " s, " + TrainingsHelper.TABLE_NAME + " t, " +
            MesocyclesHelper.TABLE_NAME + " m " +
            "WHERE s." + COLUMN_TRAINING + " = t._id AND t." + COLUMN_MESOCYCLE + " = m._id;";

    public SetsHelper(Context context) {
        super(context);
    }

    public static void onCreate(SQLiteDatabase db) {
        Log.v(DBHelper.LOG_TAG, CREATE_TABLE);
        db.execSQL(CREATE_TABLE);
        Log.v(DBHelper.LOG_TAG, CREATE_VIEW);
        db.execSQL(CREATE_VIEW);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.v(SetsHelper.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        database.execSQL("DROP VIEW IF EXISTS " + VIEW_NAME);
        onCreate(database);
    }

    public String[] getColumns() {
        return new String[]{COLUMN_ID, COLUMN_REPS, COLUMN_WEIGHT, COLUMN_COMMENT, COLUMN_TRAINING};
    }

    public String[] getViewColumns() {
        return new String[]{COLUMN_MESOCYCLE, COLUMN_TRAINING, COLUMN_ID, COLUMN_REPS, COLUMN_WEIGHT, COLUMN_COMMENT};
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
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

    public Set entityFromCursor(Cursor cursor) {
        return new Set(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_REPS)),
                cursor.getFloat(cursor.getColumnIndex(COLUMN_WEIGHT)),
                cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_TRAINING)));
    }

    public List<SetView> selectView(String selection, String groupBy, String having, String orderBy) {
        Log.v(DBHelper.LOG_TAG, "select from " + VIEW_NAME);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db != null) {
            Cursor cursor = db.query(VIEW_NAME, getViewColumns(), selection, null, groupBy, having, orderBy);
            return entityViewFromCursor(cursor);
        }
        return null;
    }

    public List<SetView> entityViewFromCursor(Cursor cursor) {
        List<SetView> sets = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                sets.add(new SetView(
                        cursor.getLong(cursor.getColumnIndex(COLUMN_MESOCYCLE)),
                        cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_REPS)),
                        cursor.getFloat(cursor.getColumnIndex(COLUMN_WEIGHT)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT)),
                        cursor.getLong(cursor.getColumnIndex(COLUMN_TRAINING))
                ));
            } while (cursor.moveToNext());
        }
        return sets;
    }

    public List<Set> selectGroupedSets(String selection, String[] selectionArgs) {
        Log.v(DBHelper.LOG_TAG, "select from " + VIEW_NAME);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db != null) {
            String[] columns = {"count(_id)", COLUMN_REPS, COLUMN_WEIGHT, COLUMN_COMMENT, COLUMN_TRAINING};
            String groupBy = COLUMN_REPS + ", " + COLUMN_WEIGHT;
            String orderBy = COLUMN_ID;
            Cursor cursor = db.query(VIEW_NAME, columns, selection, selectionArgs, groupBy, null, orderBy);
            List<Set> sets = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    sets.add(new Set(
                            cursor.getLong(0),
                            cursor.getInt(1),
                            cursor.getFloat(2),
                            cursor.getString(3),
                            cursor.getLong(4)
                    ));
                } while (cursor.moveToNext());
            }
            return sets;
        }
        return null;
    }
}