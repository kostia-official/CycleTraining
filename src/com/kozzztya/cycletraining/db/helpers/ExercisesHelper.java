package com.kozzztya.cycletraining.db.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.MyDBHelper;
import com.kozzztya.cycletraining.db.entities.Exercise;

import java.util.ArrayList;
import java.util.List;

public class ExercisesHelper {

    private MyDBHelper myDBHelper;

    public static final String TABLE_NAME = "exercises";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EXERCISE_TYPE = "exercise_type";
    public static final String COLUMN_DESCRIPTION = "description";

    private static final String TABLE_CREATE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_EXERCISE_TYPE + " integer,"
            + COLUMN_DESCRIPTION + " text"
            + ");";

    private static final String TABLE_VIEW_CREATE = "CREATE VIEW view_"
            + TABLE_NAME + " AS SELECT _id, "
            + COLUMN_NAME + ", "
            + COLUMN_DESCRIPTION
            + ", " + ExerciseTypesHelper.TABLE_NAME + "." + ExerciseTypesHelper.COLUMN_NAME
            + " FROM " + TABLE_NAME
            + " INNER JOIN " + ExerciseTypesHelper.TABLE_NAME
            + " ON " + ExerciseTypesHelper.TABLE_NAME + "._id = " + TABLE_NAME + "._id;";

    public ExercisesHelper(Context context) {
        myDBHelper = new MyDBHelper(context);
    }

    public static void onCreate(SQLiteDatabase database) {
        Log.v("myDB", TABLE_NAME + " table creating");
        database.execSQL(TABLE_CREATE);
        //database.execSQL(TABLE_VIEW_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.v(ExercisesHelper.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public List<Exercise> selectAll() {
        List<Exercise> exercises = new ArrayList<Exercise>();
        String selectQuery = "SELECT _id, " + COLUMN_NAME + ", "
                + COLUMN_EXERCISE_TYPE + ", "
                + COLUMN_DESCRIPTION + " FROM " + TABLE_NAME;

        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                exercises.add(new Exercise(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getString(3)
                ));
            } while (cursor.moveToNext());
        }

        return exercises;
    }

    public Exercise getExercise(long id) {
        SQLiteDatabase db = myDBHelper.getReadableDatabase();

        String[] columns = {"_id", COLUMN_NAME, COLUMN_EXERCISE_TYPE, COLUMN_DESCRIPTION};
        String where = "_id =" + id;
        Cursor cursor = db.query(TABLE_NAME, columns, where, null, null, null, null);

        cursor.moveToFirst();
        return new Exercise(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getLong(2),
                cursor.getString(3)
        );
    }
}