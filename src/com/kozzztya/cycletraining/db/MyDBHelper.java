package com.kozzztya.cycletraining.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.kozzztya.cycletraining.db.helpers.*;
import com.kozzztya.cycletraining.utils.DBUtils;

import java.io.IOException;

public class MyDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cycle_training.db";
    private static final int DATABASE_VERSION = 33;
    private Context context;

    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ExerciseTypesHelper.onCreate(db);
        ExercisesHelper.onCreate(db);
        MusclesHelper.onCreate(db);
        ExercisesMusclesHelper.onCreate(db);

        SetsHelper.onCreate(db);
        TrainingsHelper.onCreate(db);
        CyclesHelper.onCreate(db);
        MesocyclesHelper.onCreate(db);

        PurposesHelper.onCreate(db);
        ProgramsHelper.onCreate(db);

        TrainingJournalHelper.onCreate(db);

        //Вставка данных
        Log.v("myDB", " data insert");
        try {
            DBUtils.executeSqlScript(context, db, "data_insert.sql", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        ExerciseTypesHelper.onUpgrade(db, oldVersion, newVersion);
        ExercisesHelper.onUpgrade(db, oldVersion, newVersion);
        MusclesHelper.onUpgrade(db, oldVersion, newVersion);
        ExercisesMusclesHelper.onUpgrade(db, oldVersion, newVersion);

        SetsHelper.onUpgrade(db, oldVersion, newVersion);
        TrainingsHelper.onUpgrade(db, oldVersion, newVersion);
        CyclesHelper.onUpgrade(db, oldVersion, newVersion);
        MesocyclesHelper.onUpgrade(db, oldVersion, newVersion);

        PurposesHelper.onUpgrade(db, oldVersion, newVersion);
        ProgramsHelper.onUpgrade(db, oldVersion, newVersion);

        TrainingJournalHelper.onUpgrade(db, oldVersion, newVersion);

        //Вставка данных
        Log.v("myDB", " data insert");
        try {
            DBUtils.executeSqlScript(context, db, "data_insert.sql", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
