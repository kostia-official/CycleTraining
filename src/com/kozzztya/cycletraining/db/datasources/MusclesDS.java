package com.kozzztya.cycletraining.db.datasources;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.entities.Muscle;

public class MusclesDS extends DataSource<Muscle> {
    public static final String TABLE_NAME = "muscles";
    public static final String COLUMN_NAME = "name";

    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + COLUMN_NAME + " text not null"
            + ");";

    public MusclesDS(DBHelper dbHelper) {
        super(dbHelper);
    }

    public static void onCreate(SQLiteDatabase database) {
        Log.v(DBHelper.LOG_TAG, TABLE_NAME + " table creating");
        database.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        //Recreate table if it was created before stable version
        if (oldVersion <= DBHelper.DATABASE_VERSION_STABLE) {
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(database);
        }
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getColumns() {
        return new String[]{COLUMN_ID, COLUMN_NAME};
    }

    @Override
    public ContentValues getContentValues(Muscle entity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, entity.getName());
        return values;
    }

    @Override
    public Muscle entityFromCursor(Cursor cursor) {
        return new Muscle(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
        );
    }

}
