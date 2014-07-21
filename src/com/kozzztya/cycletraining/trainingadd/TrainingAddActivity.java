package com.kozzztya.cycletraining.trainingadd;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.kozzztya.cycletraining.DrawerActivity;
import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.customviews.MyCaldroidFragment;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.MesocyclesDataSource;
import com.kozzztya.cycletraining.db.datasources.SetsDataSource;
import com.kozzztya.cycletraining.db.datasources.TrainingJournalDataSource;
import com.kozzztya.cycletraining.db.datasources.TrainingsDataSource;
import com.kozzztya.cycletraining.db.entities.*;
import com.kozzztya.cycletraining.utils.DateUtils;
import com.kozzztya.cycletraining.utils.SetUtils;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class TrainingAddActivity extends DrawerActivity implements OnClickListener {

    private Spinner spinnerRound;
    private EditText editTextWeight;
    private EditText editTextReps;
    private TextView dateChooser;
    private TextView exerciseChooser;
    private TextView programChooser;

    private Date beginDate;
    private long mesocycleId;

    private Program program;
    private Exercise exercise;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.training_plan);

        exerciseChooser = (TextView) findViewById(R.id.exerciseChooser);
        programChooser = (TextView) findViewById(R.id.programChooser);
        spinnerRound = (Spinner) findViewById(R.id.spinnerRound);
        dateChooser = (TextView) findViewById(R.id.dateChooser);
        editTextWeight = (EditText) findViewById(R.id.editTextWeight);
        editTextReps = (EditText) findViewById(R.id.editTextReps);
        Button buttonConfirm = (Button) findViewById(R.id.buttonConfirm);

        buttonConfirm.setOnClickListener(this);
        exerciseChooser.setOnClickListener(this);
        programChooser.setOnClickListener(this);
        dateChooser.setOnClickListener(this);

        getExtras(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        getExtras(intent);
        super.onNewIntent(intent);
    }

    private void getExtras(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.get("program") != null) {
                program = (Program) extras.get("program");
                programChooser.setText(program.toString());
            }

            if (extras.get("exercise") != null) {
                exercise = (Exercise) extras.get("exercise");
                exerciseChooser.setText(exercise.toString());
            }

            if (extras.get("beginDate") != null) {
                beginDate = (Date) extras.get("beginDate");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                String dayOfWeekName = DateUtils.getDayOfWeekName(beginDate, getApplicationContext());
                dateChooser.setText(dayOfWeekName + ", " + dateFormat.format(beginDate));
            }
        }
    }

    private void showCalendarDialog() {
        final MyCaldroidFragment dialogCaldroidFragment = new MyCaldroidFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CaldroidFragment.DIALOG_TITLE, getString(R.string.date_dialog_title));
        bundle.putInt(CaldroidFragment.START_DAY_OF_WEEK, Preferences.getFirstDayOfWeek(this));
        dialogCaldroidFragment.setArguments(bundle);
        dialogCaldroidFragment.show(getSupportFragmentManager(), "CALDROID_DIALOG_FRAGMENT");

        dialogCaldroidFragment.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(java.util.Date date, View view) {
                //Show chosen date on dateChooser
                beginDate = new Date(date.getTime());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                String dayOfWeekName = DateUtils.getDayOfWeekName(beginDate, getApplicationContext());
                dateChooser.setText(dayOfWeekName + ", " + dateFormat.format(date));

                dialogCaldroidFragment.dismiss();
            }
        });
    }

    private void newMesocycle() {
        DBHelper dbHelper = DBHelper.getInstance(this);
        TrainingJournalDataSource trainingJournalDataSource = dbHelper.getTrainingJournalDataSource();
        MesocyclesDataSource mesocyclesDataSource = dbHelper.getMesocyclesDataSource();
        TrainingsDataSource trainingsDataSource = dbHelper.getTrainingsDataSource();
        SetsDataSource setsDataSource = dbHelper.getSetsDataSource();

        //TODO validate input (program, exercise)
        float weight = Float.valueOf(editTextWeight.getText().toString());
        int reps = Integer.valueOf(editTextReps.getText().toString());
        float rm = SetUtils.maxRM(weight, reps);
        float roundValue = Float.valueOf(spinnerRound.getSelectedItem().toString());

        //Get chosen program data
        Mesocycle mesocycle = mesocyclesDataSource.getEntity(program.getMesocycle());
        List<Training> trainings = trainingsDataSource.select(TrainingsDataSource.COLUMN_MESOCYCLE + " = " + program.getMesocycle(), null, null, null);
        List<SetView> sets = setsDataSource.selectView(SetsDataSource.COLUMN_MESOCYCLE + " = " + program.getMesocycle(), null, null, null);

        //Insert training_add data from input
        mesocycle.setRm(rm);
        mesocycleId = mesocyclesDataSource.insert(mesocycle);

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
                long trainingDate = DateUtils.calcTrainingDate(i, program.getTrainingsInWeek(), beginDate);
                newTraining.setDate(new Date(trainingDate));
                newTraining.setComment(t.getComment());
                long newTrainingId = trainingsDataSource.insert(newTraining);
                for (Set s : sets) {
                    if (s.getTraining() == oldTrainingId) {
                        Set newSet = new Set();
                        newSet.setReps(s.getReps());
                        //Round weight to chosen value
                        newSet.setWeight(SetUtils.roundTo(s.getWeight() * rm, roundValue));
                        newSet.setTraining(newTrainingId);
                        setsDataSource.insert(newSet);
                    }
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        //Add data to training journal
        TrainingJournal tj = new TrainingJournal();
        tj.setProgram(program.getId());
        tj.setExercise(exercise.getId());
        tj.setMesocycle(mesocycleId);
        tj.setBeginDate(beginDate);
        trainingJournalDataSource.insert(tj);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dateChooser:
                showCalendarDialog();
                break;
            case R.id.buttonConfirm:
                newMesocycle();
                //Show new training_add
                Intent intent = new Intent(this, TrainingPlanActivity.class);
                intent.putExtra("mesocycleId", mesocycleId);
                startActivity(intent);
                break;
            case R.id.programChooser:
                startActivity(new Intent(this, ProgramsSearchActivity.class));
                break;
            case R.id.exerciseChooser:
                startActivity(new Intent(this, ExercisesSearchActivity.class));
                break;
        }
    }

}



















