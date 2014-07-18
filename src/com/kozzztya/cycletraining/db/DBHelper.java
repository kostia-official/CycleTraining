package com.kozzztya.cycletraining.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.kozzztya.cycletraining.db.datasources.*;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cycle_training.db";
    private static final int DATABASE_VERSION = 85;
    public static final String LOG_TAG = "myDB";

    private static DBHelper instance = null;

    private ExerciseTypesDataSource exerciseTypesDataSource;
    private ExercisesDataSource exercisesDataSource;
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

        exercisesDataSource = new ExercisesDataSource(this, context);
        exerciseTypesDataSource = new ExerciseTypesDataSource(this, context);
        musclesDataSource = new MusclesDataSource(this, context);
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

        trainingJournalDataSource.onCreate(db);
        mesocyclesDataSource.onCreate(db);
        trainingsDataSource.onCreate(db);
        setsDataSource.onCreate(db);

        purposesDataSource.onCreate(db);
        programsDataSource.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        exerciseTypesDataSource.onUpgrade(db, oldVersion, newVersion);
        exercisesDataSource.onUpgrade(db, oldVersion, newVersion);
        musclesDataSource.onUpgrade(db, oldVersion, newVersion);

        trainingJournalDataSource.onUpgrade(db, oldVersion, newVersion);
        mesocyclesDataSource.onUpgrade(db, oldVersion, newVersion);
        trainingsDataSource.onUpgrade(db, oldVersion, newVersion);
        setsDataSource.onUpgrade(db, oldVersion, newVersion);

        purposesDataSource.onUpgrade(db, oldVersion, newVersion);
        programsDataSource.onUpgrade(db, oldVersion, newVersion);
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
