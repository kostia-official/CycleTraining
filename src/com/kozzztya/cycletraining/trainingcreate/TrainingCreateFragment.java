package com.kozzztya.cycletraining.trainingcreate;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.*;
import com.kozzztya.cycletraining.trainingjournal.TrainingCalendarFragment;
import com.kozzztya.cycletraining.utils.DateUtils;
import com.kozzztya.cycletraining.utils.SetUtils;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TrainingCreateFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final int REQUEST_CODE_PROGRAM = 0;
    public static final int REQUEST_CODE_EXERCISE = 1;
    public static final String KEY_BEGIN_DATE = "beginDate";
    public static final String KEY_PROGRAM_URI = "programUri";
    public static final String KEY_EXERCISE_URI = "exerciseUri";
    public static final String KEY_WEIGHT = "weight";
    public static final String KEY_REPS = "reps";
    public static final int LOADER_PROGRAM = 0;
    public static final int LOADER_EXERCISE = 1;
    private static final String TAG = "log" + TrainingCreateFragment.class.getSimpleName();
    private static final String[] PROJECTION_PROGRAMS = new String[]{
            Programs._ID,
            Programs.DISPLAY_NAME,
            Programs.MESOCYCLE
    };
    private static final String[] PROJECTION_EXERCISES = new String[]{
            Exercises._ID,
            Exercises.DISPLAY_NAME
    };

    private Spinner mRoundSpinner;
    private EditText mWeightEditText;
    private EditText mRepsEditText;
    private TextView mDateChooser;
    private TextView mExerciseChooser;
    private TextView mProgramChooser;

    private Date mBeginDate;
    private long mProgramId;
    private long mExerciseId;
    private long mMesocycleId;
    private float mWeight;
    private int mReps;

    private Uri mProgramUri;
    private Uri mExerciseUri;

    private TrainingCreateCallbacks mCallbacks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(getString(R.string.training_create));
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.training_create, container, false);

        mWeightEditText = (EditText) view.findViewById(R.id.editTextWeight);
        mRepsEditText = (EditText) view.findViewById(R.id.editTextReps);
        mRoundSpinner = (Spinner) view.findViewById(R.id.spinnerRound);
        mRoundSpinner.setSelection(1); //default value

        // TextViews with spinner style
        mExerciseChooser = (TextView) view.findViewById(R.id.exerciseChooser);
        mProgramChooser = (TextView) view.findViewById(R.id.programChooser);
        mDateChooser = (TextView) view.findViewById(R.id.dateChooser);

        mExerciseChooser.setOnClickListener(this);
        mProgramChooser.setOnClickListener(this);
        mDateChooser.setOnClickListener(this);

        if (savedInstanceState != null) {
            //Restore data from saved instant state
            retrieveData(savedInstanceState);
        } else {
            //Retrieve data from arguments
            retrieveData(getArguments());
        }

        getLoaderManager().initLoader(LOADER_PROGRAM, null, this);
        getLoaderManager().initLoader(LOADER_EXERCISE, null, this);

        return view;
    }

    private void retrieveData(Bundle bundle) {
        // Intent keys
        mBeginDate = bundle != null && bundle.containsKey(KEY_BEGIN_DATE) ?
                new Date(bundle.getLong(KEY_BEGIN_DATE)) :
                new Date(Calendar.getInstance().getTimeInMillis());
        mDateChooser.setText(formatDate(mBeginDate));

        mProgramUri = bundle != null && bundle.containsKey(KEY_PROGRAM_URI) ?
                (Uri) bundle.getParcelable(KEY_PROGRAM_URI) :
                DatabaseProvider.uriParse(Programs.TABLE_NAME, 1);

        mExerciseUri = bundle != null && bundle.containsKey(KEY_EXERCISE_URI) ?
                (Uri) bundle.getParcelable(KEY_EXERCISE_URI) :
                DatabaseProvider.uriParse(Exercises.TABLE_NAME, 1);

        // State keys
        if (bundle != null) {
            mWeightEditText.setText(bundle.getString(KEY_WEIGHT));
            mRepsEditText.setText(bundle.getString(KEY_REPS));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_PROGRAM_URI, mProgramUri);
        outState.putParcelable(KEY_EXERCISE_URI, mExerciseUri);
        outState.putLong(KEY_BEGIN_DATE, mBeginDate.getTime());
        outState.putString(KEY_WEIGHT, mWeightEditText.getText().toString());
        outState.putString(KEY_REPS, mRepsEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_PROGRAM:
                return new CursorLoader(getActivity(), mProgramUri, PROJECTION_PROGRAMS, null, null, null);
            case LOADER_EXERCISE:
                return new CursorLoader(getActivity(), mExerciseUri, PROJECTION_EXERCISES, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            switch (loader.getId()) {
                case LOADER_PROGRAM:
                    mProgramId = data.getInt(data.getColumnIndex(Programs._ID));
                    mMesocycleId = data.getInt(data.getColumnIndex(Programs.MESOCYCLE));
                    mProgramChooser.setText(data.getString(data.getColumnIndex(Programs.DISPLAY_NAME)));
                    break;
                case LOADER_EXERCISE:
                    mExerciseId = data.getInt(data.getColumnIndex(Exercises._ID));
                    mExerciseChooser.setText(data.getString(data.getColumnIndex(Exercises.DISPLAY_NAME)));
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void showCalendarDialog() {
        final TrainingCalendarFragment dialogCaldroidFragment = new TrainingCalendarFragment();

        Bundle bundle = new Bundle();
        bundle.putString(CaldroidFragment.DIALOG_TITLE, getString(R.string.date_dialog_title));
        bundle.putInt(CaldroidFragment.START_DAY_OF_WEEK, new Preferences(getActivity()).getFirstDayOfWeek());

        dialogCaldroidFragment.setArguments(bundle);
        dialogCaldroidFragment.show(getFragmentManager(),
                TrainingCalendarFragment.class.getSimpleName());

        dialogCaldroidFragment.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(java.util.Date date, View view) {
                //Show chosen date on dateChooser
                mBeginDate = new Date(date.getTime());
                mDateChooser.setText(formatDate(mBeginDate));

                dialogCaldroidFragment.dismiss();
            }
        });
    }

    /**
     * Validate EditText input
     *
     * @return {@code true} if all input views are valid
     */
    private boolean isValidInput() {
        try {
            mWeight = Float.valueOf(mWeightEditText.getText().toString());
        } catch (NumberFormatException e) {
            mWeightEditText.setError(getString(R.string.error_input));
            return false;
        }

        try {
            mReps = Integer.valueOf(mRepsEditText.getText().toString());
        } catch (NumberFormatException e) {
            mRepsEditText.setError(getString(R.string.error_input));
            return false;
        }
        return true;
    }

    public void createTrainings() {
        if (!isValidInput())
            return;

        float rm = SetUtils.maxRM(mWeight, mReps);
        float roundValue = Float.valueOf(mRoundSpinner.getSelectedItem().toString());

        ContentResolver contentResolver = getActivity().getContentResolver();
        // Get selected program data
        String selection = Trainings.MESOCYCLE + "=" + mMesocycleId;
        Cursor mesocycleCursor = contentResolver.query(Uri.parse(DatabaseProvider.MESOCYCLES_URI + "/" + mMesocycleId),
                Mesocycles.PROJECTION, null, null, null);
        Cursor trainingsCursor = contentResolver.query(DatabaseProvider.TRAININGS_URI,
                Trainings.PROJECTION, selection, null, null);

        // Insert mesocycle data from input
        ContentValues mesocycleValues = new ContentValues();
        if (trainingsCursor != null && mesocycleCursor.moveToFirst()) {
            DatabaseUtils.cursorRowToContentValues(mesocycleCursor, mesocycleValues);
            mesocycleCursor.close();
            mesocycleValues.put(Mesocycles.RM, rm);
            mesocycleValues.remove(Mesocycles._ID);
        }

        // Get new mesocycle id
        Uri uri = contentResolver.insert(DatabaseProvider.MESOCYCLES_URI, mesocycleValues);
        long newMesocycleId = Long.valueOf(uri.getLastPathSegment());

        SQLiteDatabase db = DatabaseHelper.getInstance(getActivity()).getWritableDatabase();
        db.beginTransaction();
        try {
            if (trainingsCursor != null && trainingsCursor.moveToFirst()) {
                do {
                    ContentValues trainingValues = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(trainingsCursor, trainingValues);
                    long trainingId = trainingsCursor.getLong(trainingsCursor.getColumnIndex(Trainings._ID));

                    //Generate training date
                    Date trainingDate = new Date(DateUtils.calcTrainingDate(
                            trainingsCursor.getPosition(),
                            mesocycleValues.getAsInteger(Mesocycles.TRAININGS_IN_WEEK),
                            mBeginDate));
                    trainingValues.put(Trainings.DATE, String.valueOf(trainingDate));
                    trainingValues.put(Trainings.MESOCYCLE, newMesocycleId);
                    trainingValues.remove(Trainings._ID);

                    // Get new training id
                    Uri tUri = contentResolver.insert(DatabaseProvider.TRAININGS_URI, trainingValues);
                    long newTrainingId = Long.valueOf(tUri.getLastPathSegment());

                    selection = Sets.TRAINING + "=" + trainingId;
                    Cursor setsCursor = contentResolver.query(DatabaseProvider.SETS_URI,
                            Sets.PROJECTION, selection, null, null);

                    if (setsCursor != null && setsCursor.moveToFirst()) {
                        int count = setsCursor.getCount();
                        ContentValues[] valuesList = new ContentValues[count];
                        for (int i = 0; i < count; i++, setsCursor.moveToNext()) {
                            ContentValues setsValues = new ContentValues();
                            DatabaseUtils.cursorRowToContentValues(setsCursor, setsValues);
                            setsValues.put(Sets.TRAINING, newTrainingId);
                            setsValues.put(Sets.WEIGHT, SetUtils.roundTo(
                                    setsCursor.getFloat(setsCursor.getColumnIndex(Sets.WEIGHT)) * rm,
                                    roundValue));
                            setsValues.remove(Sets._ID);
                            valuesList[i] = setsValues;
                        }
                        setsCursor.close();
                        contentResolver.bulkInsert(DatabaseProvider.SETS_URI, valuesList);
                    }
                } while (trainingsCursor.moveToNext());
                trainingsCursor.close();
            }
            // Insert data to training diary
            ContentValues trainingDiaryValues = new ContentValues();
            trainingDiaryValues.put(TrainingJournal.PROGRAM, mProgramId);
            trainingDiaryValues.put(TrainingJournal.EXERCISE, mExerciseId);
            trainingDiaryValues.put(TrainingJournal.MESOCYCLE, newMesocycleId);
            trainingDiaryValues.put(TrainingJournal.BEGIN_DATE, String.valueOf(mBeginDate));
            contentResolver.insert(DatabaseProvider.TRAINING_JOURNAL_URI, trainingDiaryValues);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();

        mCallbacks.onTrainingCreated(DatabaseProvider.uriParse(
                Mesocycles.TABLE_NAME, newMesocycleId));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.done, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            createTrainings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dateChooser:
                showCalendarDialog();
                break;
            case R.id.programChooser:
                mCallbacks.onProgramRequest(REQUEST_CODE_PROGRAM);
                break;
            case R.id.exerciseChooser:
                mCallbacks.onExerciseRequest(REQUEST_CODE_EXERCISE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            switch (requestCode) {
                case REQUEST_CODE_PROGRAM:
                    mProgramUri = extras.getParcelable(KEY_PROGRAM_URI);
                    getLoaderManager().restartLoader(LOADER_PROGRAM, null, this);
                    break;
                case REQUEST_CODE_EXERCISE:
                    mExerciseUri = extras.getParcelable(KEY_EXERCISE_URI);
                    getLoaderManager().restartLoader(LOADER_EXERCISE, null, this);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dayOfWeekName = DateUtils.getDayOfWeekName(mBeginDate, getActivity());
        return dayOfWeekName + ", " + dateFormat.format(date);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (TrainingCreateCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement "
                    + TrainingCreateCallbacks.class.getSimpleName());
        }
    }

    public static interface TrainingCreateCallbacks {
        void onTrainingCreated(Uri mesocycleUri);

        void onExerciseRequest(int requestCode);

        void onProgramRequest(int requestCode);
    }
}
