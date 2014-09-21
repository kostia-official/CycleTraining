package com.kozzztya.cycletraining.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.database.DatabaseUtilsCompat;

public class DatabaseProvider extends ContentProvider {

    private static final String TAG = "log" + DatabaseProvider.class.getSimpleName();

    private static final String AUTHORITY = "com.kozzztya.cycletraining.DatabaseProvider";

    private static final int MESOCYCLES = 10;
    private static final int MESOCYCLE_ID = 11;
    private static final int PROGRAMS = 20;
    private static final int PROGRAM_ID = 21;
    private static final int PROGRAMS_VIEW = 25;
    private static final int PROGRAM_VIEW_ID = 26;
    private static final int TRAINING_JOURNAL = 30;
    private static final int TRAINING_JOURNAL_ID = 31;
    private static final int TRAINING_JOURNAL_VIEW = 35;
    private static final int TRAINING_JOURNAL_VIEW_ID = 36;
    private static final int TRAININGS = 40;
    private static final int TRAINING_ID = 41;
    private static final int TRAININGS_VIEW = 45;
    private static final int TRAINING_VIEW_ID = 46;
    private static final int SETS = 50;
    private static final int SET_ID = 51;
    private static final int EXERCISE_TYPES = 60;
    private static final int EXERCISE_TYPE_ID = 61;
    private static final int EXERCISES = 70;
    private static final int EXERCISE_ID = 71;
    private static final int MUSCLES = 80;
    private static final int MUSCLE_ID = 81;
    private static final int PURPOSES = 90;
    private static final int PURPOSE_ID = 91;

    private static final String PROGRAMS_TABLE = Programs.TABLE_NAME;
    private static final String PROGRAMS_VIEW_TABLE = Programs.VIEW_NAME;
    private static final String MESOCYCLES_TABLE = Mesocycles.TABLE_NAME;
    private static final String TRAINING_JOURNAL_TABLE = TrainingJournal.TABLE_NAME;
    private static final String TRAINING_JOURNAL_VIEW_TABLE = TrainingJournal.VIEW_NAME;
    private static final String TRAININGS_TABLE = Trainings.TABLE_NAME;
    private static final String TRAININGS_VIEW_TABLE = Trainings.VIEW_NAME;
    private static final String SETS_TABLE = Sets.TABLE_NAME;
    private static final String EXERCISE_TYPES_TABLE = ExerciseTypes.TABLE_NAME;
    private static final String EXERCISES_TABLE = Exercises.TABLE_NAME;
    private static final String MUSCLES_TABLE = Muscles.TABLE_NAME;
    private static final String PURPOSES_TABLE = Purposes.TABLE_NAME;

    public static Uri uriParse(String tableName) {
        return Uri.parse("content://" + AUTHORITY + "/" + tableName);
    }

    public static Uri uriParse(String tableName, long id) {
        return uriParse(tableName + "/" + id);
    }

    public static final Uri PROGRAMS_URI = uriParse(PROGRAMS_TABLE);
    public static final Uri PROGRAMS_VIEW_URI = uriParse(PROGRAMS_VIEW_TABLE);
    public static final Uri MESOCYCLES_URI = uriParse(MESOCYCLES_TABLE);
    public static final Uri TRAINING_JOURNAL_URI = uriParse(TRAINING_JOURNAL_TABLE);
    public static final Uri TRAINING_JOURNAL_VIEW_URI = uriParse(TRAINING_JOURNAL_VIEW_TABLE);
    public static final Uri TRAININGS_URI = uriParse(TRAININGS_TABLE);
    public static final Uri TRAININGS_VIEW_URI = uriParse(TRAININGS_VIEW_TABLE);
    public static final Uri SETS_URI = uriParse(SETS_TABLE);
    public static final Uri EXERCISE_TYPES_URI = uriParse(EXERCISE_TYPES_TABLE);
    public static final Uri EXERCISES_URI = uriParse(EXERCISES_TABLE);
    public static final Uri MUSCLES_URI = uriParse(MUSCLES_TABLE);
    public static final Uri PURPOSES_URI = uriParse(PURPOSES_TABLE);

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTHORITY, PROGRAMS_TABLE, PROGRAMS);
        uriMatcher.addURI(AUTHORITY, PROGRAMS_TABLE + "/#", PROGRAM_ID);
        uriMatcher.addURI(AUTHORITY, PROGRAMS_VIEW_TABLE, PROGRAMS_VIEW);
        uriMatcher.addURI(AUTHORITY, PROGRAMS_VIEW_TABLE + "/#", PROGRAM_VIEW_ID);
        uriMatcher.addURI(AUTHORITY, MESOCYCLES_TABLE, MESOCYCLES);
        uriMatcher.addURI(AUTHORITY, MESOCYCLES_TABLE + "/#", MESOCYCLE_ID);
        uriMatcher.addURI(AUTHORITY, TRAINING_JOURNAL_TABLE, TRAINING_JOURNAL);
        uriMatcher.addURI(AUTHORITY, TRAINING_JOURNAL_TABLE + "/#", TRAINING_JOURNAL_ID);
        uriMatcher.addURI(AUTHORITY, TRAINING_JOURNAL_VIEW_TABLE, TRAINING_JOURNAL_VIEW);
        uriMatcher.addURI(AUTHORITY, TRAINING_JOURNAL_VIEW_TABLE + "/#", TRAINING_JOURNAL_VIEW_ID);
        uriMatcher.addURI(AUTHORITY, TRAININGS_TABLE, TRAININGS);
        uriMatcher.addURI(AUTHORITY, TRAININGS_TABLE + "/#", TRAINING_ID);
        uriMatcher.addURI(AUTHORITY, TRAININGS_VIEW_TABLE, TRAININGS_VIEW);
        uriMatcher.addURI(AUTHORITY, TRAININGS_VIEW_TABLE + "/#", TRAINING_VIEW_ID);
        uriMatcher.addURI(AUTHORITY, SETS_TABLE, SETS);
        uriMatcher.addURI(AUTHORITY, SETS_TABLE + "/#", SET_ID);
        uriMatcher.addURI(AUTHORITY, EXERCISE_TYPES_TABLE, EXERCISE_TYPES);
        uriMatcher.addURI(AUTHORITY, EXERCISE_TYPES_TABLE + "/#", EXERCISE_TYPE_ID);
        uriMatcher.addURI(AUTHORITY, EXERCISES_TABLE, EXERCISES);
        uriMatcher.addURI(AUTHORITY, EXERCISES_TABLE + "/#", EXERCISE_ID);
        uriMatcher.addURI(AUTHORITY, MUSCLES_TABLE, MUSCLES);
        uriMatcher.addURI(AUTHORITY, MUSCLES_TABLE + "/#", MUSCLE_ID);
        uriMatcher.addURI(AUTHORITY, PURPOSES_TABLE, PURPOSES);
        uriMatcher.addURI(AUTHORITY, PURPOSES_TABLE + "/#", PURPOSE_ID);
    }

    private DatabaseHelper mDatabaseHelper;

    @Override
    public boolean onCreate() {
        mDatabaseHelper = DatabaseHelper.getInstance(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        UriData uriData = getUriData(uri);
        String id = uriData.getId();
        String tableName = uriData.getTableName();

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(tableName);
        if (id != null) queryBuilder.appendWhere(BaseColumns._ID + "=" + id);

        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String tableName = getUriData(uri).getTableName();

        long id = db.insert(tableName, null, values);
        if (id == -1) {
            db.replace(tableName, null, values);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return uriParse(tableName, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        UriData uriData = getUriData(uri);
        String tableName = uriData.getTableName();
        String id = uriData.getId();
        int rowsDeleted;

        if (id == null) {
            rowsDeleted = db.delete(tableName, selection, selectionArgs);
        } else {
            String where = DatabaseUtilsCompat.concatenateWhere(BaseColumns._ID + "=" + id, selection);
            rowsDeleted = db.delete(tableName, where, null);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        UriData uriData = getUriData(uri);
        String tableName = uriData.getTableName();
        String id = uriData.getId();
        int rowsUpdated;

        if (id == null) {
            rowsUpdated = db.update(tableName, values, selection, selectionArgs);
        } else {
            String where = DatabaseUtilsCompat.concatenateWhere(BaseColumns._ID + "=" + id, selection);
            rowsUpdated = db.update(tableName, values, where, null);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private UriData getUriData(Uri uri) {
        int uriType = uriMatcher.match(uri);
        UriData uriData = new UriData();
        switch (uriType) {
            case PROGRAM_ID:
                uriData.setId(uri.getLastPathSegment());
            case PROGRAMS:
                uriData.setTableName(PROGRAMS_TABLE);
                break;

            case PROGRAM_VIEW_ID:
                uriData.setId(uri.getLastPathSegment());
            case PROGRAMS_VIEW:
                uriData.setTableName(PROGRAMS_VIEW_TABLE);
                break;

            case MESOCYCLE_ID:
                uriData.setId(uri.getLastPathSegment());
            case MESOCYCLES:
                uriData.setTableName(MESOCYCLES_TABLE);
                break;

            case TRAINING_JOURNAL_ID:
                uriData.setId(uri.getLastPathSegment());
            case TRAINING_JOURNAL:
                uriData.setTableName(TRAINING_JOURNAL_TABLE);
                break;

            case TRAINING_JOURNAL_VIEW_ID:
                uriData.setId(uri.getLastPathSegment());
            case TRAINING_JOURNAL_VIEW:
                uriData.setTableName(TRAINING_JOURNAL_VIEW_TABLE);
                break;

            case SET_ID:
                uriData.setId(uri.getLastPathSegment());
            case SETS:
                uriData.setTableName(SETS_TABLE);
                break;

            case TRAINING_ID:
                uriData.setId(uri.getLastPathSegment());
            case TRAININGS:
                uriData.setTableName(TRAININGS_TABLE);
                break;

            case TRAINING_VIEW_ID:
                uriData.setId(uri.getLastPathSegment());
            case TRAININGS_VIEW:
                uriData.setTableName(TRAININGS_VIEW_TABLE);
                break;

            case EXERCISE_TYPE_ID:
                uriData.setId(uri.getLastPathSegment());
            case EXERCISE_TYPES:
                uriData.setTableName(EXERCISE_TYPES_TABLE);
                break;

            case EXERCISE_ID:
                uriData.setId(uri.getLastPathSegment());
            case EXERCISES:
                uriData.setTableName(EXERCISES_TABLE);
                break;

            case MUSCLE_ID:
                uriData.setId(uri.getLastPathSegment());
            case MUSCLES:
                uriData.setTableName(MUSCLES_TABLE);
                break;

            case PURPOSE_ID:
                uriData.setId(uri.getLastPathSegment());
            case PURPOSES:
                uriData.setTableName(PURPOSES_TABLE);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return uriData;
    }

    /**
     * Entity for return two values
     */
    static class UriData {
        private String mId;
        private String mTableName;

        public String getId() {
            return mId;
        }

        public void setId(String id) {
            mId = id;
        }

        public String getTableName() {
            return mTableName;
        }

        public void setTableName(String tableName) {
            mTableName = tableName;
        }

        @Override
        public String toString() {
            return mId + " " + mTableName;
        }
    }
}
