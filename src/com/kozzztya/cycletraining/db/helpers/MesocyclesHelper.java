package com.kozzztya.cycletraining.db.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.MyDBHelper;
import com.kozzztya.cycletraining.db.entities.Mesocycle;

public class MesocyclesHelper {

    private MyDBHelper myDBHelper;

    public static final String TABLE_NAME = "mesocycles";
    public static final String COLUMN_RM = "rm";
    public static final String COLUMN_EXERCISE = "exercise";

    private static final String CREATE_TABLE = "create table " +
            TABLE_NAME +
            " (_id integer primary key autoincrement, " +
            COLUMN_RM + " real, " +
            COLUMN_EXERCISE + " integer);";

    private static final String DELETE_TRIGGER = "CREATE TRIGGER delete_mesocycle " +
            "BEFORE DELETE ON " + TABLE_NAME + " " +
            "FOR EACH ROW BEGIN " +
            " DELETE FROM " + CyclesHelper.TABLE_NAME +
            " WHERE " + CyclesHelper.COLUMN_MESOCYCLE + " = old._id; " +
            " DELETE FROM " + TrainingJournalHelper.TABLE_NAME +
            " WHERE " + TrainingJournalHelper.COLUMN_MESOCYCLE + " = old._id; " +
            "END";

    public MesocyclesHelper(Context context) {
        myDBHelper = new MyDBHelper(context);
    }

    public static void onCreate(SQLiteDatabase database) {
        Log.v("myDB", TABLE_NAME + " table creating");
        database.execSQL(CREATE_TABLE);
        database.execSQL(DELETE_TRIGGER);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(ExercisesHelper.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public long insert(Mesocycle mesocycle) {
        Log.v("myDB", "insert in " + TABLE_NAME);
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RM, mesocycle.getRm());
        values.put(COLUMN_EXERCISE, mesocycle.getExercise());
        return db.insert(TABLE_NAME, null, values);
    }

    public Mesocycle getMesocycle(long id) {
        SQLiteDatabase db = myDBHelper.getReadableDatabase();

        String[] columns = {"_id", COLUMN_RM, COLUMN_EXERCISE};
        String where = "_id =" + id;
        Cursor cursor = db.query(TABLE_NAME, columns, where, null, null, null, null);

        cursor.moveToFirst();
        return new Mesocycle(
                cursor.getLong(0),
                cursor.getFloat(1),
                cursor.getLong(2)
        );
    }

    public void delete(long id) {
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        if (db != null) {
            db.delete(TABLE_NAME, "_id = " + id, null);
        }
    }
}
