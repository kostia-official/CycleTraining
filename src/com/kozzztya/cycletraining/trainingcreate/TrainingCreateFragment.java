package com.kozzztya.cycletraining.trainingcreate;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.custom.MyCaldroidFragment;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.ExercisesDS;
import com.kozzztya.cycletraining.db.datasources.MesocyclesDS;
import com.kozzztya.cycletraining.db.datasources.ProgramsDS;
import com.kozzztya.cycletraining.db.datasources.SetsDS;
import com.kozzztya.cycletraining.db.datasources.TrainingJournalDS;
import com.kozzztya.cycletraining.db.datasources.TrainingsDS;
import com.kozzztya.cycletraining.db.entities.Exercise;
import com.kozzztya.cycletraining.db.entities.Mesocycle;
import com.kozzztya.cycletraining.db.entities.ProgramView;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.SetView;
import com.kozzztya.cycletraining.db.entities.Training;
import com.kozzztya.cycletraining.db.entities.TrainingJournal;
import com.kozzztya.cycletraining.utils.DateUtils;
import com.kozzztya.cycletraining.utils.SetUtils;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TrainingCreateFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "log" + TrainingCreateFragment.class.getSimpleName();

    public static final int REQUEST_CODE_PROGRAM = 0;
    public static final int REQUEST_CODE_EXERCISE = 1;

    public static final String KEY_PROGRAM = "program";
    public static final String KEY_EXERCISE = "exercise";
    public static final String KEY_BEGIN_DATE = "beginDate";

    private Spinner mRoundSpinner;
    private EditText mWeightEditText;
    private EditText mRepsEditText;
    private TextView mDateChooser;
    private TextView mExerciseChooser;
    private TextView mProgramChooser;

    private Date mBeginDate;
    private ProgramView mProgram;
    private Exercise mExercise;
    private DBHelper mDBHelper;

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
        mDBHelper = DBHelper.getInstance(getActivity());

        mWeightEditText = (EditText) view.findViewById(R.id.editTextWeight);
        mRepsEditText = (EditText) view.findViewById(R.id.editTextReps);
        mRoundSpinner = (Spinner) view.findViewById(R.id.spinnerRound);
        mRoundSpinner.setSelection(1); //default value

        //TextViews with spinner style
        mDateChooser = (TextView) view.findViewById(R.id.dateChooser);
        mExerciseChooser = (TextView) view.findViewById(R.id.exerciseChooser);
        mProgramChooser = (TextView) view.findViewById(R.id.programChooser);

        mExerciseChooser.setOnClickListener(this);
        mProgramChooser.setOnClickListener(this);
        mDateChooser.setOnClickListener(this);

        if (savedInstanceState != null) {
            //Restore data from saved instant state
            retrieveData(savedInstanceState);
        } else {
            //Retrieve data from intent
            retrieveData(getArguments());
        }

        bindData();
        return view;
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

    private void bindData() {
        mDateChooser.setText(formatDate(mBeginDate));
        mProgramChooser.setText(mProgram.toString());
        mExerciseChooser.setText(mExercise.toString());
    }

    private void retrieveData(Bundle bundle) {
        mBeginDate = bundle != null && bundle.containsKey(KEY_BEGIN_DATE)
                ? new Date(bundle.getLong(KEY_BEGIN_DATE))
                : new Date(Calendar.getInstance().getTimeInMillis());

        mProgram = bundle != null && bundle.containsKey(KEY_PROGRAM)
                ? (ProgramView) bundle.getParcelable(KEY_PROGRAM)
                : new ProgramsDS(mDBHelper).getEntityView(1);

        mExercise = bundle != null && bundle.containsKey(KEY_EXERCISE)
                ? (Exercise) bundle.getParcelable(KEY_EXERCISE)
                : new ExercisesDS(mDBHelper).getEntity(1);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_PROGRAM, mProgram);
        outState.putParcelable(KEY_EXERCISE, mExercise);
        outState.putLong(KEY_BEGIN_DATE, mBeginDate.getTime());
        super.onSaveInstanceState(outState);
    }

    private void showCalendarDialog() {
        final MyCaldroidFragment dialogCaldroidFragment = new MyCaldroidFragment();

        Bundle bundle = new Bundle();
        bundle.putString(CaldroidFragment.DIALOG_TITLE, getString(R.string.date_dialog_title));
        bundle.putInt(CaldroidFragment.START_DAY_OF_WEEK, new Preferences(getActivity()).getFirstDayOfWeek());

        dialogCaldroidFragment.setArguments(bundle);
        dialogCaldroidFragment.show(getFragmentManager(),
                MyCaldroidFragment.class.getSimpleName());

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

    public void createTrainings() {
        TrainingJournalDS trainingJournalDS = new TrainingJournalDS(mDBHelper);
        MesocyclesDS mesocyclesDS = new MesocyclesDS(mDBHelper);
        TrainingsDS trainingsDS = new TrainingsDS(mDBHelper);
        SetsDS setsDS = new SetsDS(mDBHelper);

        if (mWeightEditText.getText().length() == 0
                || mWeightEditText.getText().charAt(0) == '.') {
            mWeightEditText.setError(getString(R.string.error_input));
            return;
        }

        if (mRepsEditText.getText().length() == 0) {
            mRepsEditText.setError(getString(R.string.error_input));
            return;
        }

        float weight = Float.valueOf(mWeightEditText.getText().toString());
        int reps = Integer.valueOf(mRepsEditText.getText().toString());
        float rm = SetUtils.maxRM(weight, reps);
        float roundValue = Float.valueOf(mRoundSpinner.getSelectedItem().toString());

        //Get chosen mProgram data
        Mesocycle mesocycle = mesocyclesDS.getEntity(mProgram.getMesocycle());
        List<Training> trainings = trainingsDS.select(TrainingsDS.COLUMN_MESOCYCLE + " = " + mProgram.getMesocycle(), null, null, null);
        List<SetView> sets = setsDS.selectView(SetsDS.COLUMN_MESOCYCLE + " = " + mProgram.getMesocycle(), null, null, null);

        //Insert mesocycle data from input
        mesocycle.setRm(rm);
        mesocycle.setId(mesocyclesDS.insert(mesocycle));

        //Generate trainings and sets data by chosen mProgram and RM
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < trainings.size(); i++) {
                Training t = trainings.get(i);
                long oldTrainingId = t.getId();
                Training newTraining = new Training();
                newTraining.setMesocycle(mesocycle.getId());
                //Generate training date
                long trainingDate = DateUtils.calcTrainingDate(i, mesocycle.getTrainingsInWeek(), mBeginDate);
                newTraining.setDate(new Date(trainingDate));
                newTraining.setComment(t.getComment());
                long newTrainingId = trainingsDS.insert(newTraining);
                for (Set s : sets) {
                    if (s.getTraining() == oldTrainingId) {
                        Set newSet = new Set();
                        newSet.setReps(s.getReps());
                        //Round weight to chosen value
                        newSet.setWeight(SetUtils.roundTo(s.getWeight() * rm, roundValue));
                        newSet.setTraining(newTrainingId);
                        setsDS.insert(newSet);
                    }
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        //Insert data to training journal
        TrainingJournal tj = new TrainingJournal();
        tj.setProgram(mProgram.getId());
        tj.setExercise(mExercise.getId());
        tj.setMesocycle(mesocycle.getId());
        tj.setBeginDate(mBeginDate);
        trainingJournalDS.insert(tj);

        mDBHelper.notifyDBChanged();
        db.close();

        mCallbacks.onTrainingCreated(mesocycle);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.done, menu);
        View actionView = MenuItemCompat.getActionView(menu.findItem(R.id.action_done));
        actionView.setOnClickListener(this);
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
            case R.id.done_menu_item:
                createTrainings();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            switch (requestCode) {
                case REQUEST_CODE_EXERCISE:
                    mExercise = extras.getParcelable(TrainingCreateFragment.KEY_EXERCISE);
                    mExerciseChooser.setText(mExercise.toString());
                    break;
                case REQUEST_CODE_PROGRAM:
                    mProgram = extras.getParcelable(TrainingCreateFragment.KEY_PROGRAM);
                    mProgramChooser.setText(mProgram.toString());
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

    public static interface TrainingCreateCallbacks {
        void onTrainingCreated(Mesocycle mesocycle);

        void onExerciseRequest(int requestCode);

        void onProgramRequest(int requestCode);
    }
}
