package com.kozzztya.cycletraining.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.datasources.*;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cycle_training.db";
    private static final int DATABASE_VERSION = 89;
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
    private Context context;

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        exercisesDataSource = new ExercisesDataSource(this);
        exerciseTypesDataSource = new ExerciseTypesDataSource(this);
        musclesDataSource = new MusclesDataSource(this);
        trainingJournalDataSource = new TrainingJournalDataSource(this);
        mesocyclesDataSource = new MesocyclesDataSource(this);
        trainingsDataSource = new TrainingsDataSource(this);
        setsDataSource = new SetsDataSource(this);
        purposesDataSource = new PurposesDataSource(this);
        programsDataSource = new ProgramsDataSource(this);
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

        fillCoreData(db);
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

        fillCoreData(db);
    }

    private void fillCoreData(SQLiteDatabase db) {
        Log.v(LOG_TAG, "filling core data");
        db.beginTransaction();
        try {
            XmlResourceParser xrp = context.getResources().getXml(R.xml.core_data);
            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                //Skip root tag
                if (xrp.getAttributeCount() != 0) {
                    if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                        ContentValues values = new ContentValues();
                        String tableName = xrp.getName();

                        //Get column name and value
                        for (int i = 0; i < xrp.getAttributeCount(); i++) {
                            String name = xrp.getAttributeName(i);
                            String value = xrp.getAttributeValue(i);
                            //If value is string reference
                            if (xrp.getAttributeValue(i).contains("@"))
                                value = context.getResources().getString(xrp.getAttributeResourceValue(i, 0));

                            values.put(name, value);
                        }

                        db.insert(tableName, null, values);
                    }
                }
                xrp.next();
            }
            db.setTransactionSuccessful();
        } catch (XmlPullParserException | IOException e) {
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
