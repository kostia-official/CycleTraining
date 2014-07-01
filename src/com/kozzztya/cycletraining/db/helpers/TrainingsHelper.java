package com.kozzztya.cycletraining.db.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.entities.Training;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.utils.MyDateUtils;

import java.util.ArrayList;
import java.util.List;

public class TrainingsHelper extends TableHelper<Training> {

    public static final String TABLE_NAME = "trainings";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_MESOCYCLE = "mesocycle";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_DONE = "done";

    public static final String VIEW_NAME = "trainings_view";
    public static final String COLUMN_EXERCISE = MesocyclesHelper.COLUMN_EXERCISE;

    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + COLUMN_DATE + " date, "
            + COLUMN_MESOCYCLE + " integer, "
            + COLUMN_COMMENT + " text, "
            + COLUMN_DONE + " integer default 0"
            + ");";

    private static final String CREATE_VIEW = "CREATE VIEW " + VIEW_NAME + " AS " +
            "SELECT tj." + COLUMN_MESOCYCLE + ", e." + ExercisesHelper.COLUMN_NAME + " " + COLUMN_EXERCISE + ", t._id as _id, t." +
            COLUMN_DATE + " as " + COLUMN_DATE + ", t." + COLUMN_MESOCYCLE + " as " + COLUMN_MESOCYCLE + ", t." +
            COLUMN_COMMENT + " as " + COLUMN_COMMENT + ", t." + COLUMN_DONE + " as " + COLUMN_DONE + " " +
            "FROM " + TrainingJournalHelper.TABLE_NAME + " tj, " + MesocyclesHelper.TABLE_NAME + " m, " +
            TABLE_NAME + " t, " + ExercisesHelper.TABLE_NAME + " e " +
            "WHERE m." + MesocyclesHelper.COLUMN_ACTIVE + "=1 AND tj." + COLUMN_MESOCYCLE + " = m._id AND m." +
            COLUMN_EXERCISE + "=e._id AND t." + COLUMN_MESOCYCLE + "=m._id;";

    private static final String CREATE_TRIGGER_DELETE = "CREATE TRIGGER delete_training " +
            "BEFORE DELETE ON " + TABLE_NAME + " " +
            "FOR EACH ROW BEGIN " +
            " DELETE FROM " + SetsHelper.TABLE_NAME + " WHERE " +
            SetsHelper.COLUMN_TRAINING + " = old._id; END ";

    public static void onCreate(SQLiteDatabase database) {
        Log.v(DBHelper.LOG_TAG, CREATE_TABLE);
        database.execSQL(CREATE_TABLE);
        Log.v(DBHelper.LOG_TAG, CREATE_VIEW);
        database.execSQL(CREATE_VIEW);
        database.execSQL(CREATE_TRIGGER_DELETE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.v(TrainingsHelper.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        database.execSQL("DROP VIEW IF EXISTS " + VIEW_NAME);
        onCreate(database);
    }

    public TrainingsHelper(Context context) {
        super(context);
    }

    public ContentValues getContentValues(Training training) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, String.valueOf(training.getDate()));
        values.put(COLUMN_MESOCYCLE, training.getMesocycle());
        values.put(COLUMN_COMMENT, training.getComment());
        values.put(COLUMN_DONE, training.isDone());
        return values;
    }

    public List<TrainingView> selectView(String selection, String groupBy, String having, String orderBy) {
        Log.v(DBHelper.LOG_TAG, "select from " + TABLE_NAME);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db != null) {
            Cursor cursor = db.query(VIEW_NAME, getViewColumns(), selection, null, groupBy, having, orderBy);
            return entityViewFromCursor(cursor);
        }
        return null;
    }

    @Override
    public Training entityFromCursor(Cursor cursor) {
        return new Training(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                MyDateUtils.safeParse(cursor.getString(cursor.getColumnIndex(COLUMN_DATE))),
                cursor.getLong(cursor.getColumnIndex(COLUMN_MESOCYCLE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_DONE)) > 0
        );
    }

    public List<TrainingView> entityViewFromCursor(Cursor cursor) {
        List<TrainingView> trainings = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                trainings.add(new TrainingView(
                        cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                        MyDateUtils.safeParse(cursor.getString(cursor.getColumnIndex(COLUMN_DATE))),
                        cursor.getLong(cursor.getColumnIndex(COLUMN_MESOCYCLE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_DONE)) > 0,
                        cursor.getString(cursor.getColumnIndex(COLUMN_EXERCISE))
                ));
            } while (cursor.moveToNext());
        }
        return trainings;
    }

    @Override
    public String[] getColumns() {
        return new String[]{COLUMN_ID, COLUMN_DATE, COLUMN_MESOCYCLE, COLUMN_COMMENT, COLUMN_DONE};
    }

    public String[] getViewColumns() {
        return new String[]{COLUMN_EXERCISE, COLUMN_ID, COLUMN_DATE, COLUMN_MESOCYCLE, COLUMN_COMMENT, COLUMN_DONE};
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}