package com.kozzztya.cycletraining.db.datasources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.entities.TrainingJournal;
import com.kozzztya.cycletraining.db.entities.TrainingJournalView;
import com.kozzztya.cycletraining.utils.DateUtils;

public class TrainingJournalDS extends DataSourceView<TrainingJournal, TrainingJournalView> {

    public static final String TABLE_NAME = "training_journal";
    public static final String COLUMN_PROGRAM = "program";
    public static final String COLUMN_MESOCYCLE = "mesocycle";
    public static final String COLUMN_EXERCISE = "exercise";
    public static final String COLUMN_BEGIN_DATE = "begin_date";

    private static final String VIEW_NAME = "training_journal_view";

    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + COLUMN_PROGRAM + " integer, "
            + COLUMN_MESOCYCLE + " integer,"
            + COLUMN_EXERCISE + " integer, "
            + COLUMN_BEGIN_DATE + " date);";

    private static final String CREATE_VIEW = "CREATE VIEW " + VIEW_NAME + " AS "
            + "SELECT tj._id, tj." + COLUMN_MESOCYCLE + ", tj." + COLUMN_BEGIN_DATE + ", e." + ExercisesDS.COLUMN_NAME + " "
            + COLUMN_EXERCISE + ", p." + ProgramsDS.COLUMN_NAME + " " + COLUMN_PROGRAM + " FROM "
            + TABLE_NAME + " tj, " + ExercisesDS.TABLE_NAME + " e, " + ProgramsDS.TABLE_NAME + " p"
            + " WHERE tj." + COLUMN_EXERCISE + " = e._id AND tj." + COLUMN_PROGRAM + " = p._id;";

    private static final String CREATE_DELETE_TRIGGER = "CREATE TRIGGER delete_training_journal " +
            "BEFORE DELETE ON " + TABLE_NAME + " " +
            "FOR EACH ROW BEGIN " +
            "DELETE FROM " + MesocyclesDS.TABLE_NAME +
            " WHERE _id = old." + COLUMN_MESOCYCLE + "; END";

    public TrainingJournalDS(Context context) {
        super(context);
    }

    public static void onCreate(SQLiteDatabase database) {
        Log.v("myDB", CREATE_TABLE);
        database.execSQL(CREATE_TABLE);
        Log.v("myDB", CREATE_VIEW);
        database.execSQL(CREATE_VIEW);
        database.execSQL(CREATE_DELETE_TRIGGER);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.v(DBHelper.LOG_TAG, "Upgrading table " + TABLE_NAME + " from version "
                + oldVersion + " to " + newVersion);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getColumns() {
        return new String[]{COLUMN_ID, COLUMN_PROGRAM, COLUMN_MESOCYCLE, COLUMN_EXERCISE, COLUMN_BEGIN_DATE};
    }

    @Override
    public ContentValues getContentValues(TrainingJournal entity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROGRAM, entity.getProgram());
        values.put(COLUMN_MESOCYCLE, entity.getMesocycle());
        values.put(COLUMN_EXERCISE, entity.getExercise());
        values.put(COLUMN_BEGIN_DATE, entity.getBeginDate().getTime());
        return values;
    }

    @Override
    public TrainingJournal entityFromCursor(Cursor cursor) {
        return new TrainingJournal(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_PROGRAM)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_MESOCYCLE)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_EXERCISE)),
                DateUtils.safeParse(cursor.getString(cursor.getColumnIndex(COLUMN_BEGIN_DATE)))
        );
    }

    @Override
    public String getViewName() {
        return VIEW_NAME;
    }

    @Override
    public String[] getViewColumns() {
        return getColumns();
    }

    @Override
    public TrainingJournalView entityViewFromCursor(Cursor cursor) {
        return new TrainingJournalView(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_MESOCYCLE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_PROGRAM)),
                cursor.getString(cursor.getColumnIndex(COLUMN_EXERCISE)),
                DateUtils.safeParse(cursor.getString(cursor.getColumnIndex(COLUMN_BEGIN_DATE)))
        );
    }
}