package com.kozzztya.cycletraining.db;

import android.content.ContentProvider;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.kozzztya.cycletraining.db.helpers.*;
import com.kozzztya.cycletraining.utils.DBUtils;

import java.io.IOException;
import java.util.Calendar;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cycle_training.db";
    private static final int DATABASE_VERSION = 56;
    public static final String LOG_TAG = "myDB";

    private static DBHelper instance = null;
    private Context context;

    private ExerciseTypesHelper exerciseTypesHelper;
    private ExercisesHelper exercisesHelper;
    private MusclesHelper musclesHelper;
    private ExercisesMusclesHelper exercisesMusclesHelper;
    private TrainingJournalHelper trainingJournalHelper;
    private MesocyclesHelper mesocyclesHelper;
    private TrainingsHelper trainingsHelper;
    private SetsHelper setsHelper;
    private PurposesHelper purposesHelper;
    private ProgramsHelper programsHelper;

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ExerciseTypesHelper.onCreate(db);
        ExercisesHelper.onCreate(db);
        MusclesHelper.onCreate(db);
        ExercisesMusclesHelper.onCreate(db);

        TrainingJournalHelper.onCreate(db);
        MesocyclesHelper.onCreate(db);
        TrainingsHelper.onCreate(db);
        SetsHelper.onCreate(db);

        PurposesHelper.onCreate(db);
        ProgramsHelper.onCreate(db);

        fillData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        ExerciseTypesHelper.onUpgrade(db, oldVersion, newVersion);
        ExercisesHelper.onUpgrade(db, oldVersion, newVersion);
        MusclesHelper.onUpgrade(db, oldVersion, newVersion);
        ExercisesMusclesHelper.onUpgrade(db, oldVersion, newVersion);

        TrainingJournalHelper.onUpgrade(db, oldVersion, newVersion);
        MesocyclesHelper.onUpgrade(db, oldVersion, newVersion);
        TrainingsHelper.onUpgrade(db, oldVersion, newVersion);
        SetsHelper.onUpgrade(db, oldVersion, newVersion);

        PurposesHelper.onUpgrade(db, oldVersion, newVersion);
        ProgramsHelper.onUpgrade(db, oldVersion, newVersion);

        fillData(db);
    }

    public void fillData(SQLiteDatabase db) {
        Log.v(LOG_TAG, " data insert");
        try {
            DBUtils.executeSqlScript(context, db, "data_insert.sql", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
