package com.kozzztya.cycletraining;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.entities.*;
import com.kozzztya.cycletraining.db.datasources.*;
import com.kozzztya.cycletraining.utils.DateUtils;
import com.kozzztya.cycletraining.utils.RMUtils;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import static android.view.View.OnClickListener;

public class MesocycleCreateActivity extends DrawerActivity implements OnClickListener {

    private Spinner spinnerExercise;
    private Spinner spinnerProgram;
    private Spinner spinnerRound;
    private Button buttonCreate;
    private TextView textViewDate;
    private EditText editTextWeight;
    private EditText editTextReps;

    private Date beginDate;
    private long newMesocycleId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.mesocycle_create);

        spinnerExercise = (Spinner) findViewById(R.id.spinnerExercise);
        spinnerProgram = (Spinner) findViewById(R.id.spinnerProgram);
        spinnerRound = (Spinner) findViewById(R.id.spinnerRound);
        buttonCreate = (Button) findViewById(R.id.buttonCreateProgram);
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        editTextWeight = (EditText) findViewById(R.id.editTextWeight);
        editTextReps = (EditText) findViewById(R.id.editTextReps);

        buttonCreate.setOnClickListener(this);
        textViewDate.setOnClickListener(this);

        fillSpinners();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textViewDate:
                showCalendarDialog();
                break;
            case R.id.buttonCreateProgram:
                newMesocycle();
                //Show new mesocycle
                Intent intent = new Intent(this, MesocycleShowActivity.class);
                intent.putExtra("mesocycleId", newMesocycleId);
                startActivity(intent);
                break;
        }
    }

    private void fillSpinners() {
        ExercisesDataSource exercisesDataSource = DBHelper.getInstance(this).getExercisesDataSource();
        ProgramsDataSource programsDataSource = DBHelper.getInstance(this).getProgramsDataSource();

        List<Exercise> exercises = exercisesDataSource.select(null, null, null, null);
        ArrayAdapter<Exercise> exerciseAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, exercises);
        exerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerExercise.setAdapter(exerciseAdapter);

        List<Program> programs = programsDataSource.select(null, null, null, null);
        ArrayAdapter<Program> programAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, programs);
        programAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerProgram.setAdapter(programAdapter);
    }

    private void showCalendarDialog() {
        final CaldroidFragment dialogCaldroidFragment = new CaldroidFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CaldroidFragment.DIALOG_TITLE, getString(R.string.date_dialog_title));
        bundle.putInt(CaldroidFragment.START_DAY_OF_WEEK, Preferences.getFirstDayOfWeek(this));
        dialogCaldroidFragment.setArguments(bundle);
        dialogCaldroidFragment.show(getSupportFragmentManager(), "CALDROID_DIALOG_FRAGMENT");

        dialogCaldroidFragment.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(java.util.Date date, View view) {
                //Show chosen date on textViewDate
                beginDate = new Date(date.getTime());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                String dayOfWeekName = DateUtils.getDayOfWeekName(beginDate, getApplicationContext());
                textViewDate.setText(dayOfWeekName + ", " + dateFormat.format(date));

                dialogCaldroidFragment.dismiss();
            }
        });
    }

    private void newMesocycle() {
        TrainingJournalDataSource trainingJournalDataSource = DBHelper.getInstance(this).getTrainingJournalDataSource();
        MesocyclesDataSource mesocyclesDataSource = DBHelper.getInstance(this).getMesocyclesDataSource();
        TrainingsDataSource trainingsDataSource = DBHelper.getInstance(this).getTrainingsDataSource();
        SetsDataSource setsDataSource = DBHelper.getInstance(this).getSetsDataSource();

        //Get chosen program data
        Program program = (Program) spinnerProgram.getSelectedItem();
        Mesocycle mesocycle = mesocyclesDataSource.getEntity(program.getMesocycle());
        List<Training> trainings = trainingsDataSource.select(TrainingsDataSource.COLUMN_MESOCYCLE + " = " + program.getMesocycle(), null, null, null);
        List<SetView> sets = setsDataSource.selectView(SetsDataSource.COLUMN_MESOCYCLE + " = " + program.getMesocycle(), null, null, null);

        //TODO validate input
        //Insert mesocycle data from input
        float weight = Float.valueOf(editTextWeight.getText().toString());
        int reps = Integer.valueOf(editTextReps.getText().toString());
        float rm = RMUtils.maxRM(weight, reps);
        float roundValue = Float.valueOf(spinnerRound.getSelectedItem().toString());
        long exerciseId = ((Exercise) spinnerExercise.getSelectedItem()).getId();
        mesocycle.setRm(rm);
        mesocycle.setExercise(exerciseId);
        newMesocycleId = mesocyclesDataSource.insert(mesocycle);

        //Generate trainings and sets data by chosen program and RM
        SQLiteDatabase db = DBHelper.getInstance(this).getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < trainings.size(); i++) {
                Training t = trainings.get(i);
                long oldTrainingId = t.getId();
                Training newTraining = new Training();
                newTraining.setMesocycle(newMesocycleId);
                //Generate training date
                long trainingDate = DateUtils.calcTrainingDate(i, program.getTrainingsInWeek(), beginDate);
                newTraining.setDate(new Date(trainingDate));
                long newTrainingId = trainingsDataSource.insert(newTraining);
                for (Set s : sets) {
                    if (s.getTraining() == oldTrainingId) {
                        Set newSet = new Set();
                        newSet.setReps(s.getReps());
                        //Round weight to chosen value
                        newSet.setWeight(RMUtils.roundTo(s.getWeight() * rm, roundValue));
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
        long programId = ((Program) spinnerProgram.getSelectedItem()).getId();
        TrainingJournal tj = new TrainingJournal();
        tj.setProgram(programId);
        tj.setMesocycle(newMesocycleId);
        tj.setBeginDate(beginDate);
        trainingJournalDataSource.insert(tj);
    }

}



















