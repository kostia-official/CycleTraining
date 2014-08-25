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
import com.kozzztya.cycletraining.db.datasources.*;
import com.kozzztya.cycletraining.db.entities.*;
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

    private Spinner spinnerRound;
    private EditText editTextWeight;
    private EditText editTextReps;
    private TextView dateChooser;
    private TextView exerciseChooser;
    private TextView programChooser;

    private Date beginDate;
    private ProgramView program;
    private Exercise exercise;
    private DBHelper dbHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.training_create);

        dbHelper = DBHelper.getInstance(this);
        exerciseChooser = (TextView) findViewById(R.id.exerciseChooser);
        programChooser = (TextView) findViewById(R.id.programChooser);
        spinnerRound = (Spinner) findViewById(R.id.spinnerRound);
        dateChooser = (TextView) findViewById(R.id.dateChooser);
        editTextWeight = (EditText) findViewById(R.id.editTextWeight);
        editTextReps = (EditText) findViewById(R.id.editTextReps);

        exerciseChooser.setOnClickListener(this);
        programChooser.setOnClickListener(this);
        dateChooser.setOnClickListener(this);

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
                    exercise = extras.getParcelable("exercise");
                    exerciseChooser.setText(exercise.toString());
                    break;
                case REQUEST_CODE_PROGRAM:
                    program = extras.getParcelable("program");
                    programChooser.setText(program.toString());
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setDefaultValues() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            beginDate = (Date) extras.get("beginDate");
        } else {
            beginDate = new Date(Calendar.getInstance().getTimeInMillis());
        }
        dateChooser.setText(formatDate(beginDate));

        ProgramsDS programsDS = new ProgramsDS(dbHelper);
        program = programsDS.getEntityView(1);
        programChooser.setText(program.toString());

        ExercisesDS exercisesDS = new ExercisesDS(dbHelper);
        exercise = exercisesDS.getEntity(1);
        exerciseChooser.setText(exercise.toString());

        Spinner spinnerRound = (Spinner) findViewById(R.id.spinnerRound);
        spinnerRound.setSelection(1);
    }

    private void showCalendarDialog() {
        final MyCaldroidFragment dialogCaldroidFragment = new MyCaldroidFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CaldroidFragment.DIALOG_TITLE, getString(R.string.date_dialog_title));
        bundle.putInt(CaldroidFragment.START_DAY_OF_WEEK, new Preferences(this).getFirstDayOfWeek());
        dialogCaldroidFragment.setArguments(bundle);
        dialogCaldroidFragment.show(getSupportFragmentManager(), "CALDROID_DIALOG_FRAGMENT");

        dialogCaldroidFragment.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(java.util.Date date, View view) {
                //Show chosen date on dateChooser
                beginDate = new Date(date.getTime());
                dateChooser.setText(formatDate(beginDate));

                dialogCaldroidFragment.dismiss();
            }
        });
    }

    public void createTrainings() {
        TrainingJournalDS trainingJournalDS = new TrainingJournalDS(dbHelper);
        MesocyclesDS mesocyclesDS = new MesocyclesDS(dbHelper);
        TrainingsDS trainingsDS = new TrainingsDS(dbHelper);
        SetsDS setsDS = new SetsDS(dbHelper);

        if (editTextWeight.getText().length() == 0
                || editTextWeight.getText().charAt(0) == '.') {
            editTextWeight.setError(getString(R.string.error_input));
            return;
        }

        if (editTextReps.getText().length() == 0) {
            editTextReps.setError(getString(R.string.error_input));
            return;
        }

        float weight = Float.valueOf(editTextWeight.getText().toString());
        int reps = Integer.valueOf(editTextReps.getText().toString());
        float rm = SetUtils.maxRM(weight, reps);
        float roundValue = Float.valueOf(spinnerRound.getSelectedItem().toString());

        //Get chosen program data
        Mesocycle mesocycle = mesocyclesDS.getEntity(program.getMesocycle());
        List<Training> trainings = trainingsDS.select(TrainingsDS.COLUMN_MESOCYCLE + " = " + program.getMesocycle(), null, null, null);
        List<SetView> sets = setsDS.selectView(SetsDS.COLUMN_MESOCYCLE + " = " + program.getMesocycle(), null, null, null);

        //Insert mesocycle data from input
        mesocycle.setRm(rm);
        long mesocycleId = mesocyclesDS.insert(mesocycle);

        //Generate trainings and sets data by chosen program and RM
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < trainings.size(); i++) {
                Training t = trainings.get(i);
                long oldTrainingId = t.getId();
                Training newTraining = new Training();
                newTraining.setMesocycle(mesocycleId);
                //Generate training date
                long trainingDate = DateUtils.calcTrainingDate(i, mesocycle.getTrainingsInWeek(), beginDate);
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
        tj.setProgram(program.getId());
        tj.setExercise(exercise.getId());
        tj.setMesocycle(mesocycleId);
        tj.setBeginDate(beginDate);
        trainingJournalDS.insert(tj);

        dbHelper.notifyDBChanged();
        db.close();

        //Show training plan
        Intent intent = new Intent(this, TrainingPlanActivity.class);
        intent.putExtra("mesocycleId", mesocycleId);
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
        String dayOfWeekName = DateUtils.getDayOfWeekName(beginDate, this);
        return dayOfWeekName + ", " + dateFormat.format(date);
    }
}



















