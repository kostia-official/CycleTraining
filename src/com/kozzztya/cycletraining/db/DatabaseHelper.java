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
import com.kozzztya.cycletraining.utils.FileUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "log" + DatabaseHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "cycle_training.db";
    public static final String BACKUP_DIR = ".CycleTraining//backup//";

    public static final int DATABASE_VERSION = 164;

    /**
     * After stable version DB don't need recreate
     * and will only be update with new data
     */
    public static final int DATABASE_VERSION_STABLE = 164;

    private static DatabaseHelper instance = null;
    private final Context mContext;

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(DatabaseHelper.TAG, "Create database");
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
        Log.v(DatabaseHelper.TAG, "Upgrade database from version " + oldVersion + " to " + newVersion);
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

    public void backup() {
        Log.v(TAG, "backup");
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
        Log.v(TAG, "restore " + backupFileName);
        try {
            File from = new File(Environment.getExternalStorageDirectory(), BACKUP_DIR + backupFileName);
            File to = mContext.getDatabasePath(DATABASE_NAME);
            if (from.exists()) {
                FileUtils.copyFile(from, to);
                Toast.makeText(mContext, mContext.getString(R.string.toast_restore_successful), Toast.LENGTH_SHORT).show();
            }

            // Upgrade restored old DB if it needs
            SQLiteDatabase db = getWritableDatabase();
            int restoredVersion = db.getVersion();
            if (restoredVersion < DATABASE_VERSION) {
                onUpgrade(db, restoredVersion, DATABASE_VERSION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
