package com.kozzztya.cycletraining.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kozzztya.cycletraining.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "logDB";

    public static final String DATABASE_NAME = "cycle_training.db";
    public static final int DATABASE_VERSION = 165;

    private static DatabaseHelper instance = null;
    private final Context mContext;

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(TAG, "Create database");
        db.beginTransaction();
        try {
            ExerciseTypes.onCreate(db);
            Exercises.onCreate(db);
            Muscles.onCreate(db);
            Purposes.onCreate(db);

            Mesocycles.onCreate(db);
            Programs.onCreate(db);
            TrainingJournal.onCreate(db);
            Trainings.onCreate(db);
            Sets.onCreate(db);

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
        Log.v(TAG, "Upgrade database from version " + oldVersion + " to " + newVersion);
        db.beginTransaction();
        try {
            ExerciseTypes.onUpgrade(db, oldVersion, newVersion);
            Exercises.onUpgrade(db, oldVersion, newVersion);
            Muscles.onUpgrade(db, oldVersion, newVersion);
            Purposes.onUpgrade(db, oldVersion, newVersion);

            Mesocycles.onUpgrade(db, oldVersion, newVersion);
            Programs.onUpgrade(db, oldVersion, newVersion);
            TrainingJournal.onUpgrade(db, oldVersion, newVersion);
            Trainings.onUpgrade(db, oldVersion, newVersion);
            Sets.onUpgrade(db, oldVersion, newVersion);

            fillCoreData(db);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    protected void fillCoreData(SQLiteDatabase db) throws XmlPullParserException, IOException {
        Log.v(DatabaseHelper.TAG, "filling core data");
        XmlResourceParser xrp = mContext.getResources().getXml(R.xml.core_data);

        while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
            // If not root tag
            if (xrp.getAttributeCount() != 0) {
                if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                    String tableName = xrp.getName();
                    ContentValues contentValues = new ContentValues();

                    // Get column name and value from attributes
                    for (int i = 0; i < xrp.getAttributeCount(); i++) {
                        String column = xrp.getAttributeName(i);
                        String value = xrp.getAttributeValue(i);
                        // If value is string reference
                        if (value.startsWith("@")) {
                            value = mContext.getResources().getString(xrp.getAttributeResourceValue(i, 0));
                        }
                        contentValues.put(column, value);
                    }

                    db.replace(tableName, null, contentValues);
                }
            }
            xrp.next();
        }
    }
}
