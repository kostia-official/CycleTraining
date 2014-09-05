package com.kozzztya.cycletraining.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.datasources.ExerciseTypesDS;
import com.kozzztya.cycletraining.db.datasources.ExercisesDS;
import com.kozzztya.cycletraining.db.datasources.MesocyclesDS;
import com.kozzztya.cycletraining.db.datasources.MusclesDS;
import com.kozzztya.cycletraining.db.datasources.ProgramsDS;
import com.kozzztya.cycletraining.db.datasources.PurposesDS;
import com.kozzztya.cycletraining.db.datasources.SetsDS;
import com.kozzztya.cycletraining.db.datasources.TrainingJournalDS;
import com.kozzztya.cycletraining.db.datasources.TrainingsDS;
import com.kozzztya.cycletraining.utils.FileUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "cycle_training.db";
    public static final String LOG_TAG = "myDB";
    public static final String BACKUP_DIR = ".CycleTraining//backup//";

    public static final int DATABASE_VERSION = 161;
    //After stable version DB don't need recreate
    public static final int DATABASE_VERSION_STABLE = 160;

    private static DBHelper instance = null;
    private final Context mContext;
    private List<OnDBChangeListener> mListeners;

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
        this.mListeners = new ArrayList<>();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(DBHelper.LOG_TAG, "Create database");
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
        Log.v(DBHelper.LOG_TAG, "Upgrade database from version " + oldVersion + " to " + newVersion);
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
        XmlResourceParser xrp = mContext.getResources().getXml(R.xml.core_data);

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

    public void backup() {
        Log.v(LOG_TAG, "backup");
        try {
            File from = mContext.getDatabasePath(DATABASE_NAME);
            File to = new File(Environment.getExternalStorageDirectory(), BACKUP_DIR +
                    new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().getTime()) + ".backup");
            if (!to.exists()) {
                to.getParentFile().mkdirs();
                to.createNewFile();
            }

            FileUtils.copyFile(from, to);
            Toast.makeText(mContext, mContext.getString(R.string.toast_backup_successful), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restore(String backupFileName) {
        Log.v(LOG_TAG, "restore");
        try {
            File from = new File(Environment.getExternalStorageDirectory(), BACKUP_DIR + backupFileName);
            File to = mContext.getDatabasePath(DATABASE_NAME);
            if (from.exists()) {
                FileUtils.copyFile(from, to);
                Toast.makeText(mContext, mContext.getString(R.string.toast_restore_successful), Toast.LENGTH_SHORT).show();
            }

            //Upgrade restored old DB
            SQLiteDatabase db = getWritableDatabase();
            int restoredVersion = db.getVersion();
            if (restoredVersion < DATABASE_VERSION) {
                onUpgrade(db, restoredVersion, DATABASE_VERSION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerOnDBChangeListener(OnDBChangeListener onDBChangeListener) {
        mListeners.add(onDBChangeListener);
    }

    public void unregisterOnDBChangeListener(OnDBChangeListener onDBChangeListener) {
        mListeners.remove(onDBChangeListener);
    }

    public void notifyDBChanged() {
        for (OnDBChangeListener l : mListeners) {
            l.onDBChange();
        }
    }
}
