package com.kozzztya.cycletraining.trainingcreate;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.kozzztya.cycletraining.DrawerActivity;
import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.customviews.MyCaldroidFragment;
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

public class TrainingCreateActivity extends DrawerActivity implements OnClickListener {

    public static final int REQUEST_CODE_PROGRAM = 1;
    public static final int REQUEST_CODE_EXERCISE = 2;

    public static final String KEY_PROGRAM = "program";
    public static final String KEY_EXERCISE = "exercise";
    public static final String KEY_BEGIN_DATE = "beginDate";

    private Spinner mSpinnerRound;
    private EditText mEditTextWeight;
    private EditText mEditTextReps;
    private TextView mDateChooser;
    private TextView mExerciseChooser;
    private TextView mProgramChooser;

    private Date mBeginDate;
    private ProgramView mProgram;
    private Exercise mExercise;
    private DBHelper mDBHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.training_create);

        mDBHelper = DBHelper.getInstance(this);
        mExerciseChooser = (TextView) findViewById(R.id.exerciseChooser);
        mProgramChooser = (TextView) findViewById(R.id.programChooser);
        mSpinnerRound = (Spinner) findViewById(R.id.spinnerRound);
        mDateChooser = (TextView) findViewById(R.id.dateChooser);
        mEditTextWeight = (EditText) findViewById(R.id.editTextWeight);
        mEditTextReps = (EditText) findViewById(R.id.editTextReps);

        mExerciseChooser.setOnClickListener(this);
        mProgramChooser.setOnClickListener(this);
        mDateChooser.setOnClickListener(this);

        setDefaultValues();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done, menu);
        View actionView = MenuItemCompat.getActionView(menu.findItem(R.id.action_done));
        actionView.setOnClickListener(this);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            switch (requestCode) {
                case REQUEST_CODE_EXERCISE:
                    mExercise = extras.getParcelable(KEY_EXERCISE);
                    mExerciseChooser.setText(mExercise.toString());
                    break;
                case REQUEST_CODE_PROGRAM:
                    mProgram = extras.getParcelable(KEY_PROGRAM);
                    mProgramChooser.setText(mProgram.toString());
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setDefaultValues() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mBeginDate = (Date) extras.get(KEY_BEGIN_DATE);
        } else {
            mBeginDate = new Date(Calendar.getInstance().getTimeInMillis());
        }
        mDateChooser.setText(formatDate(mBeginDate));

        ProgramsDS programsDS = new ProgramsDS(mDBHelper);
        mProgram = programsDS.getEntityView(1);
        mProgramChooser.setText(mProgram.toString());

        ExercisesDS exercisesDS = new ExercisesDS(mDBHelper);
        mExercise = exercisesDS.getEntity(1);
        mExerciseChooser.setText(mExercise.toString());

        Spinner spinnerRound = (Spinner) findViewById(R.id.spinnerRound);
        spinnerRound.setSelection(1);
    }

    private void showCalendarDialog() {
        final MyCaldroidFragment dialogCaldroidFragment = new MyCaldroidFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CaldroidFragment.DIALOG_TITLE, getString(R.string.date_dialog_title));
        bundle.putInt(CaldroidFragment.START_DAY_OF_WEEK, new Preferences(this).getFirstDayOfWeek());
        dialogCaldroidFragment.setArguments(bundle);
        dialogCaldroidFragment.show(getSupportFragmentManager(),
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

        if (mEditTextWeight.getText().length() == 0
                || mEditTextWeight.getText().charAt(0) == '.') {
            mEditTextWeight.setError(getString(R.string.error_input));
            return;
        }

        if (mEditTextReps.getText().length() == 0) {
            mEditTextReps.setError(getString(R.string.error_input));
            return;
        }

        float weight = Float.valueOf(mEditTextWeight.getText().toString());
        int reps = Integer.valueOf(mEditTextReps.getText().toString());
        float rm = SetUtils.maxRM(weight, reps);
        float roundValue = Float.valueOf(mSpinnerRound.getSelectedItem().toString());

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

        //Show training plan
        Intent intent = new Intent(this, TrainingPlanActivity.class);
        intent.putExtra(TrainingPlanActivity.KEY_MESOCYCLE, mesocycle);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.dateChooser:
                showCalendarDialog();
                break;
            case R.id.programChooser:
                intent.setClass(this, ProgramsActivity.class);
                startActivityForResult(intent, REQUEST_CODE_PROGRAM);
                break;
            case R.id.exerciseChooser:
                intent.setClass(this, ExercisesActivity.class);
                startActivityForResult(intent, REQUEST_CODE_EXERCISE);
                break;
            case R.id.done_menu_item:
                createTrainings();
                break;
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dayOfWeekName = DateUtils.getDayOfWeekName(mBeginDate, this);
        return dayOfWeekName + ", " + dateFormat.format(date);
    }
}



















