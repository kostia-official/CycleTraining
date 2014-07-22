package com.kozzztya.cycletraining.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.kozzztya.cycletraining.db.datasources.*;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cycle_training.db";
    private static final int DATABASE_VERSION = 128;
    public static final String LOG_TAG = "myDB";

    private static DBHelper instance = null;

    private ExerciseTypesDataSource exerciseTypesDataSource;
    private ExercisesDataSource exercisesDataSource;
    private MusclesDataSource musclesDataSource;
    private PurposesDataSource purposesDataSource;
    private ProgramsDataSource programsDataSource;
    private TrainingJournalDataSource trainingJournalDataSource;
    private MesocyclesDataSource mesocyclesDataSource;
    private TrainingsDataSource trainingsDataSource;
    private SetsDataSource setsDataSource;

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        exercisesDataSource = new ExercisesDataSource(context);
        exerciseTypesDataSource = new ExerciseTypesDataSource(context);
        musclesDataSource = new MusclesDataSource(context);
        purposesDataSource = new PurposesDataSource(context);
        programsDataSource = new ProgramsDataSource(context);
        trainingJournalDataSource = new TrainingJournalDataSource(context);
        mesocyclesDataSource = new MesocyclesDataSource(context);
        trainingsDataSource = new TrainingsDataSource(context);
        setsDataSource = new SetsDataSource(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            exerciseTypesDataSource.onCreate(db);
            exercisesDataSource.onCreate(db);
            musclesDataSource.onCreate(db);
            purposesDataSource.onCreate(db);
            programsDataSource.onCreate(db);

            trainingJournalDataSource.onCreate(db);
            mesocyclesDataSource.onCreate(db);
            trainingsDataSource.onCreate(db);
            setsDataSource.onCreate(db);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            exerciseTypesDataSource.onUpgrade(db, oldVersion, newVersion);
            exercisesDataSource.onUpgrade(db, oldVersion, newVersion);
            musclesDataSource.onUpgrade(db, oldVersion, newVersion);
            purposesDataSource.onUpgrade(db, oldVersion, newVersion);
            programsDataSource.onUpgrade(db, oldVersion, newVersion);

            trainingJournalDataSource.onUpgrade(db, oldVersion, newVersion);
            mesocyclesDataSource.onUpgrade(db, oldVersion, newVersion);
            trainingsDataSource.onUpgrade(db, oldVersion, newVersion);
            setsDataSource.onUpgrade(db, oldVersion, newVersion);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
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
