package com.kozzztya.cycletraining.db.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.MyDBHelper;
import com.kozzztya.cycletraining.db.entities.TrainingJournal;

public class TrainingJournalHelper {

    private MyDBHelper myDBHelper;

    public static final String TABLE_NAME = "training_journal";
    public static final String COLUMN_PROGRAM = "program";
    public static final String COLUMN_MESOCYCLE = "mesocycle";
    public static final String COLUMN_BEGIN_DATE = "begin_date";
    public static final String COLUMN_DONE = "done";

    private static final String TABLE_CREATE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + COLUMN_PROGRAM + " integer, "
            + COLUMN_MESOCYCLE + " integer,"
            + COLUMN_BEGIN_DATE + " date, "
            + COLUMN_DONE + " integer default 0);";

    public TrainingJournalHelper(Context context) {
        myDBHelper = new MyDBHelper(context);
    }

    public static void onCreate(SQLiteDatabase database) {
        Log.v("myDB", TABLE_NAME + " table creating");
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.v(TrainingJournalHelper.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public long insert(TrainingJournal trainingJournal) {
        Log.v("myDB", " insert in " + TABLE_NAME);
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROGRAM, trainingJournal.getProgram());
        values.put(COLUMN_MESOCYCLE, trainingJournal.getMesocycle());
        values.put(COLUMN_BEGIN_DATE, trainingJournal.getBeginDate().getTime());
        values.put(COLUMN_DONE, trainingJournal.isDone());
        long id = db.insert(TABLE_NAME, null, values);
        return id;
    }

}