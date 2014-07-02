package com.kozzztya.cycletraining.db.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.entities.TrainingJournal;
import com.kozzztya.cycletraining.utils.MyDateUtils;

public class TrainingJournalHelper extends TableHelper<TrainingJournal> {

    public static final String TABLE_NAME = "training_journal";
    public static final String COLUMN_PROGRAM = "program";
    public static final String COLUMN_MESOCYCLE = "mesocycle";
    public static final String COLUMN_BEGIN_DATE = "begin_date";

    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + COLUMN_PROGRAM + " integer, "
            + COLUMN_MESOCYCLE + " integer,"
            + COLUMN_BEGIN_DATE + " date);";

    private static final String DELETE_TRIGGER = "CREATE TRIGGER delete_training_journal " +
            "BEFORE DELETE ON " + TABLE_NAME + " " +
            "FOR EACH ROW BEGIN " +
            "DELETE FROM " + MesocyclesHelper.TABLE_NAME +
            " WHERE _id = old." + COLUMN_MESOCYCLE + "; END";

    public TrainingJournalHelper(Context context) {
        super(context);
    }

    public static void onCreate(SQLiteDatabase database) {
        Log.v("myDB", TABLE_NAME + " table creating");
        database.execSQL(CREATE_TABLE);
        database.execSQL(DELETE_TRIGGER);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.v(TrainingJournalHelper.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getColumns() {
        return new String[] {COLUMN_ID, COLUMN_PROGRAM, COLUMN_MESOCYCLE, COLUMN_BEGIN_DATE};
    }

    @Override
    public ContentValues getContentValues(TrainingJournal entity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROGRAM, entity.getProgram());
        values.put(COLUMN_MESOCYCLE, entity.getMesocycle());
        values.put(COLUMN_BEGIN_DATE, entity.getBeginDate().getTime());
        return values;
    }

    @Override
    public TrainingJournal entityFromCursor(Cursor cursor) {
        return new TrainingJournal(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_PROGRAM)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_MESOCYCLE)),
                MyDateUtils.safeParse(cursor.getString(cursor.getColumnIndex(COLUMN_BEGIN_DATE)))
        );
    }

}