package com.kozzztya.cycletraining.db.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.entities.ExercisesMuscle;

public class ExercisesMusclesHelper extends TableHelper<ExercisesMuscle> {
    public static final String TABLE_NAME = "exercises_muscles";
    public static final String COLUMN_MUSCLE = "muscle";
    public static final String COLUMN_EXERCISE = "exercise";

    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + COLUMN_MUSCLE + " integer not null, "
            + COLUMN_EXERCISE + " integer not null"
            + ");";

    public ExercisesMusclesHelper(Context context) {
        super(context);
    }

    public static void onCreate(SQLiteDatabase database) {
        Log.v(DBHelper.LOG_TAG, TABLE_NAME + " table creating");
        database.execSQL(CREATE_TABLE);
        fillData(database);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.v(DBHelper.LOG_TAG, "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    private static void fillData(SQLiteDatabase database) {
        Log.v("myDB", TABLE_NAME + " data filling");
//        INSERT INTO muscles (name) VALUES
//                ('грудь'),
//                ('спина'),
//                ('плечи'),
//                ('трицепс'),
//                ('бицепс'),
//                ('голень'),
//                ('бедро');
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getColumns() {
        return new String[]{COLUMN_ID, COLUMN_MUSCLE, COLUMN_EXERCISE};
    }

    @Override
    public ContentValues getContentValues(ExercisesMuscle entity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MUSCLE, entity.getMuscle());
        values.put(COLUMN_EXERCISE, entity.getExercise());
        return values;
    }

    @Override
    public ExercisesMuscle entityFromCursor(Cursor cursor) {
        return new ExercisesMuscle(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_MUSCLE)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_EXERCISE))
        );
    }
}
