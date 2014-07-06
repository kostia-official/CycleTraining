package com.kozzztya.cycletraining.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.kozzztya.cycletraining.db.datasources.*;
import com.kozzztya.cycletraining.utils.DBUtils;

import java.io.IOException;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cycle_training.db";
    private static final int DATABASE_VERSION = 69;
    public static final String LOG_TAG = "myDB";

    private static DBHelper instance = null;
    private Context context;

    private ExerciseTypesDataSource exerciseTypesDataSource;
    private ExercisesDataSource exercisesDataSource;
    private ExercisesMusclesDataSource exercisesMusclesDataSource;
    private MusclesDataSource musclesDataSource;
    private TrainingJournalDataSource trainingJournalDataSource;
    private MesocyclesDataSource mesocyclesDataSource;
    private TrainingsDataSource trainingsDataSource;
    private SetsDataSource setsDataSource;
    private PurposesDataSource purposesDataSource;
    private ProgramsDataSource programsDataSource;

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        exercisesDataSource = new ExercisesDataSource(this, context);
        exerciseTypesDataSource = new ExerciseTypesDataSource(this, context);
        musclesDataSource = new MusclesDataSource(this, context);
        exercisesMusclesDataSource = new ExercisesMusclesDataSource(this, context);
        trainingJournalDataSource = new TrainingJournalDataSource(this, context);
        mesocyclesDataSource = new MesocyclesDataSource(this, context);
        trainingsDataSource = new TrainingsDataSource(this, context);
        setsDataSource = new SetsDataSource(this, context);
        purposesDataSource = new PurposesDataSource(this, context);
        programsDataSource = new ProgramsDataSource(this, context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        exerciseTypesDataSource.onCreate(db);
        exercisesDataSource.onCreate(db);
        musclesDataSource.onCreate(db);
        exercisesMusclesDataSource.onCreate(db);

        trainingJournalDataSource.onCreate(db);
        mesocyclesDataSource.onCreate(db);
        trainingsDataSource.onCreate(db);
        setsDataSource.onCreate(db);

        purposesDataSource.onCreate(db);
        programsDataSource.onCreate(db);

        //fillData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        exerciseTypesDataSource.onUpgrade(db, oldVersion, newVersion);
        exercisesDataSource.onUpgrade(db, oldVersion, newVersion);
        musclesDataSource.onUpgrade(db, oldVersion, newVersion);
        exercisesMusclesDataSource.onUpgrade(db, oldVersion, newVersion);

        trainingJournalDataSource.onUpgrade(db, oldVersion, newVersion);
        mesocyclesDataSource.onUpgrade(db, oldVersion, newVersion);
        trainingsDataSource.onUpgrade(db, oldVersion, newVersion);
        setsDataSource.onUpgrade(db, oldVersion, newVersion);

        purposesDataSource.onUpgrade(db, oldVersion, newVersion);
        programsDataSource.onUpgrade(db, oldVersion, newVersion);

        //fillData(db);
    }

    public void fillData(SQLiteDatabase db) {
        Log.v(LOG_TAG, " data insert");
        try {
            DBUtils.executeSqlScript(context, db, "data_insert.sql", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ExerciseTypesDataSource getExerciseTypesDataSource() {
        return exerciseTypesDataSource;
    }

    public ExercisesDataSource getExercisesDataSource() {
        return exercisesDataSource;
    }

    public MusclesDataSource getMusclesDataSource() {
        return musclesDataSource;
    }

    public ExercisesMusclesDataSource getExercisesMusclesDataSource() {
        return exercisesMusclesDataSource;
    }

    public TrainingJournalDataSource getTrainingJournalDataSource() {
        return trainingJournalDataSource;
    }

    public MesocyclesDataSource getMesocyclesDataSource() {
        return mesocyclesDataSource;
    }

    public TrainingsDataSource getTrainingsDataSource() {
        return trainingsDataSource;
    }

    public SetsDataSource getSetsDataSource() {
        return setsDataSource;
    }

    public PurposesDataSource getPurposesDataSource() {
        return purposesDataSource;
    }

    public ProgramsDataSource getProgramsDataSource() {
        return programsDataSource;
    }
}
