package com.kozzztya.cycletraining.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Environment;
import android.util.Log;

import com.kozzztya.cycletraining.db.DatabaseHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class DatabaseBackupUtils {

    public static final String BACKUP_DIR = ".CycleTraining//backup//";
    public static final String BACKUP_PREFIX = "_backup";

    /**
     * Save DB on external SD.
     */
    public static void backupDatabase(Context context) throws IOException {
        String backupFileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
                .format(System.currentTimeMillis()) + ".backup";

        File from = context.getDatabasePath(DatabaseHelper.DATABASE_NAME);
        File to = new File(Environment.getExternalStorageDirectory(),
                BACKUP_DIR + backupFileName);

        // Create missing directories.
        if (!to.exists()) {
            to.getParentFile().mkdirs();
            to.createNewFile();
        }

        FileUtils.copyFile(from, to);
    }

    /**
     * Restore DB from external SD.
     *
     * @param backupFileName The backup file to restore.
     */
    public static void restoreDatabase(Context context, String backupFileName) throws IOException {
        File from = new File(Environment.getExternalStorageDirectory(), BACKUP_DIR + backupFileName);
        File to = context.getDatabasePath(DatabaseHelper.DATABASE_NAME);
        FileUtils.copyFile(from, to);

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // Upgrade restored old DB if it needs
        int restoredVersion = db.getVersion();
        if (restoredVersion < DatabaseHelper.DATABASE_VERSION) {
            databaseHelper.onUpgrade(db, restoredVersion, DatabaseHelper.DATABASE_VERSION);
        }
    }

    /**
     * Copy selected rows to the backup table.
     */
    public static void backupTable(SQLiteDatabase database, String tableName,
                                   String[] columns, String where) {
        dropBackupTableIfExist(database, tableName);
        String query = "CREATE TABLE " + tableName + BACKUP_PREFIX + " AS " +
                SQLiteQueryBuilder.buildQueryString(false, tableName, columns, where,
                        null, null, null, null);

        database.execSQL(query);
        Log.v(DatabaseHelper.TAG, query);
    }

    /**
     * Copy selected rows from the backup table.
     */
    public static void restoreTable(SQLiteDatabase database, String tableName,
                                    String[] columns, String where) {
        String query = "INSERT INTO " + tableName + " (" + buildColumnsString(columns) + ") " +
                SQLiteQueryBuilder.buildQueryString(false, tableName + BACKUP_PREFIX, columns, where,
                        null, null, null, null);

        database.execSQL(query);
        dropBackupTableIfExist(database, tableName);
        Log.v(DatabaseHelper.TAG, query);
    }

    /**
     * Drop the backup table.
     */
    private static void dropBackupTableIfExist(SQLiteDatabase database, String tableName) {
        database.execSQL("DROP TABLE IF EXISTS " + tableName + BACKUP_PREFIX);
    }

    /**
     * Concatenate the columns that are non-null, separating them with commas.
     *
     * @param columns Query columns.
     * @return String with the enumeration of columns and spaces on the sides.
     */
    public static String buildColumnsString(String[] columns) {
        StringBuilder stringBuilder = new StringBuilder();
        SQLiteQueryBuilder.appendColumns(stringBuilder, columns);
        return stringBuilder.toString();
    }
}
