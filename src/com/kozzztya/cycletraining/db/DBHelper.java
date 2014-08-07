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
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cycle_training.db";
    private static final int DATABASE_VERSION = 156;
    public static final String LOG_TAG = "myDB";

    private static DBHelper instance = null;
    private final Context context;
    private List<OnDBChangeListener> listeners;

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.listeners = new ArrayList<>();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            ExerciseTypesDS.onCreate(db);
            ExercisesDS.onCreate(db);
            MusclesDS.onCreate(db);
            PurposesDS.onCreate(db);

            MesocyclesDS.onCreate(db);
            ProgramsDS.onCreate(db);
            TrainingJournalDS.onCreate(db);
            TrainingsDS.onCreate(db);
            SetsDS.onCreate(db);

            fillCoreData(db);

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
            ExerciseTypesDS.onUpgrade(db, oldVersion, newVersion);
            ExercisesDS.onUpgrade(db, oldVersion, newVersion);
            MusclesDS.onUpgrade(db, oldVersion, newVersion);
            PurposesDS.onUpgrade(db, oldVersion, newVersion);

            MesocyclesDS.onUpgrade(db, oldVersion, newVersion);
            ProgramsDS.onUpgrade(db, oldVersion, newVersion);
            TrainingJournalDS.onUpgrade(db, oldVersion, newVersion);
            TrainingsDS.onUpgrade(db, oldVersion, newVersion);
            SetsDS.onUpgrade(db, oldVersion, newVersion);

            fillCoreData(db);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    protected void fillCoreData(SQLiteDatabase db) throws XmlPullParserException, IOException {
        Log.v(DBHelper.LOG_TAG, "filling core data");
        XmlResourceParser xrp = context.getResources().getXml(R.xml.core_data);

        while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
            //If not root tag
            if (xrp.getAttributeCount() != 0) {
                if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                    String tableName = xrp.getName();
                    ContentValues contentValues = new ContentValues();

                    //Get column name and value from attributes
                    for (int i = 0; i < xrp.getAttributeCount(); i++) {
                        String column = xrp.getAttributeName(i);
                        String value = xrp.getAttributeValue(i);
                        //If value is string reference
                        if (value.startsWith("@")) {
                            value = context.getResources().getString(xrp.getAttributeResourceValue(i, 0));
                        }
                        contentValues.put(column, value);
                    }

                    db.insert(tableName, null, contentValues);
                }
            }
            xrp.next();
        }
    }

    public void registerOnDBChangeListener(OnDBChangeListener onDBChangeListener) {
        listeners.add(onDBChangeListener);
    }

    public void unregisterOnDBChangeListener(OnDBChangeListener onDBChangeListener) {
        listeners.remove(onDBChangeListener);
    }

    public void notifyDBChanged() {
        for (OnDBChangeListener l : listeners) {
            l.onDBChange();
        }
    }
}
