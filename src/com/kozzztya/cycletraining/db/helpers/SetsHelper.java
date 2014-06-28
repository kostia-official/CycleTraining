package com.kozzztya.cycletraining.db.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.MyDBHelper;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.SetView;

import java.util.ArrayList;
import java.util.List;

public class SetsHelper implements TableHelper<Set> {
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

    private MyDBHelper myDBHelper;

    public SetsHelper(Context context) {
        myDBHelper = new MyDBHelper(context);
    }

    public static void onCreate(SQLiteDatabase db) {
        Log.v("myDB", CREATE_TABLE);
        db.execSQL(CREATE_TABLE);
        Log.v("myDB", CREATE_VIEW);
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

    public long insert(Set set) {
        Log.v("myDB", "insert in " + TABLE_NAME);
        ContentValues values = new ContentValues();
        values.put(COLUMN_REPS, set.getReps());
        values.put(COLUMN_WEIGHT, set.getWeight());
        values.put(COLUMN_COMMENT, set.getComment());
        values.put(COLUMN_TRAINING, set.getTraining());
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        return db != null ? db.insert(TABLE_NAME, null, values) : -1;
    }

    @Override
    public Set getEntity(long id) {
        return null;
    }

    @Override
    public boolean update(Set set) {
        Log.v("myDB", "UPDATE " + TABLE_NAME);
        ContentValues values = new ContentValues();
        values.put(COLUMN_REPS, set.getReps());
        values.put(COLUMN_WEIGHT, set.getWeight());
        values.put(COLUMN_COMMENT, set.getComment());
        values.put(COLUMN_TRAINING, set.getTraining());
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        return db != null && db.update(TABLE_NAME, values,
                COLUMN_ID + " = " + set.getId(), null) != 0;
    }

    @Override
    public boolean delete(long id) {
        return false;
    }

    public List<Set> select(String selection, String groupBy, String having, String orderBy) {
        Log.v("myDB", "select from " + TABLE_NAME);
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        if (db != null) {
            Cursor cursor = db.query(TABLE_NAME, getColumns(), selection, null, groupBy, having, orderBy);
            return entityFromCursor(cursor);
        }
        return null;
    }

    public List<SetView> selectView(String selection, String groupBy, String having, String orderBy) {
        Log.v("myDB", "select from " + VIEW_NAME);
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        if (db != null) {
            Cursor cursor = db.query(VIEW_NAME, getViewColumns(), selection, null, groupBy, having, orderBy);
            return entityViewFromCursor(cursor);
        }
        return null;
    }

    public List<Set> selectGroupedSets(String selection, String[] selectionArgs) {
        Log.v("myDB", "select from " + VIEW_NAME);
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
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

    public List<Set> entityFromCursor(Cursor cursor) {
        List<Set> sets = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                sets.add(new Set(
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
}