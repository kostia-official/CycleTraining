package com.kozzztya.cycletraining;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.kozzztya.cycletraining.db.entities.*;
import com.kozzztya.cycletraining.db.helpers.*;
import com.kozzztya.cycletraining.utils.RMCalc;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static android.app.DatePickerDialog.OnDateSetListener;
import static android.view.View.OnClickListener;

public class NewMesocycleActivity extends Activity implements OnClickListener {

    private Spinner spinnerExercise;
    private Spinner spinnerProgram;
    private Button buttonCreate;
    private Button buttonDate;
    private EditText editTextWeight;
    private EditText editTextReps;
    private DatePickerDialog dateDialog;

    private Date beginDate;
    private long newMesocycleId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_mesocycle);

        spinnerExercise = (Spinner) findViewById(R.id.spinnerExercise);
        spinnerProgram = (Spinner) findViewById(R.id.spinnerProgram);
        buttonCreate = (Button) findViewById(R.id.buttonCreateProgram);
        buttonDate = (Button) findViewById(R.id.buttonDate);
        editTextWeight = (EditText) findViewById(R.id.editTextWeight);
        editTextReps = (EditText) findViewById(R.id.editTextReps);

        ExercisesHelper exercisesHelper = new ExercisesHelper(this);
        ProgramsHelper programsHelper = new ProgramsHelper(this);

        //Наполняем спиннеры данными c базы
        List<Exercise> exercises = exercisesHelper.getAll();
        ArrayAdapter<Exercise> exerciseAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, exercises);
        exerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerExercise.setAdapter(exerciseAdapter);
        List<Program> programs = programsHelper.getAll();
        ArrayAdapter<Program> programAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, programs);
        programAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerProgram.setAdapter(programAdapter);

        //Создаём диалог выбора даты
        OnDateSetListener datePickerListener = new OnDateSetListener() {
            public void onDateSet(DatePicker view, int year,
                                  int month, int day) {
                Calendar c = new GregorianCalendar(year, month, day);
                beginDate = new Date(c.getTimeInMillis());
                buttonDate.setText(String.format("%d.%d.%d", day, month, year));
            }
        };
        final Calendar c = Calendar.getInstance();
        dateDialog = new DatePickerDialog(this, datePickerListener,
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
        dateDialog.getDatePicker().setSpinnersShown(false);
        dateDialog.getDatePicker().setCalendarViewShown(true);

        buttonCreate.setOnClickListener(this);
        buttonDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonDate:
                dateDialog.show();
                break;
            case R.id.buttonCreateProgram:
                newMesocycle();
                //Просмотр созданного мезоцикла
                Intent intent = new Intent(this, MesocycleActivity.class);
                intent.putExtra("mesocycleId", newMesocycleId);
                startActivity(intent);
                break;
        }
    }

    private void newMesocycle() {
        //TODO проверить валидность ввода
        TrainingJournalHelper trainingJournalHelper = new TrainingJournalHelper(this);
        MesocyclesHelper mesocyclesHelper = new MesocyclesHelper(this);
        CyclesHelper cyclesHelper = new CyclesHelper(this);
        TrainingsHelper trainingsHelper = new TrainingsHelper(this);
        SetsHelper setsHelper = new SetsHelper(this);

        //Считываем данные выбранной программы тренировок
        long programMesocycleId = ((Program)spinnerProgram.getSelectedItem()).getMesocycle();
        List<Cycle> cycles = cyclesHelper.getAll(programMesocycleId);
        List<Training> trainings = trainingsHelper.getAllByMesocycle(programMesocycleId);
        List<Set> sets = setsHelper.getAllByMesocycle(programMesocycleId);

        //Создаём новую мезоцикл на основе указанных данных
        float weight = Float.valueOf(editTextWeight.getText().toString());
        int reps = Integer.valueOf(editTextReps.getText().toString());
        float rm = RMCalc.maxRM(weight, reps);
        long exerciseId = ((Exercise)spinnerExercise.getSelectedItem()).getId();
        Mesocycle m = new Mesocycle();
        m.setRm(rm);
        m.setExercise(exerciseId);
        newMesocycleId = mesocyclesHelper.insert(m);

        //Cоздаём новую связку цикл-тренировка-подход
        for (Cycle c : cycles) {
            long oldCycleId = c.getId();
            Cycle newCycle = new Cycle();
            newCycle.setMesocycle(newMesocycleId);
            long newCycleId = cyclesHelper.insert(newCycle);
            for (Training t : trainings) {
                if (t.getCycle() == oldCycleId) {
                    long oldTrainingId = t.getId();
                    Training newTraining = new Training();
                    newTraining.setCycle(newCycleId);
                    long newTrainingId = trainingsHelper.insert(newTraining);
                    for (Set s : sets) {
                        if (s.getTraining() == oldTrainingId) {
                            Set newSet = new Set();
                            newSet.setReps(s.getReps());
                            newSet.setWeight(s.getWeight()*rm);
                            newSet.setTraining(newTrainingId);
                            setsHelper.insert(newSet);
                        }
                    }
                }
            }
        }

        //Вносим данные в журнал тренировок
        long programId = ((Program)spinnerProgram.getSelectedItem()).getId();
        TrainingJournal tj = new TrainingJournal();
        tj.setProgram(programId);
        tj.setMesocycle(newMesocycleId);
        tj.setBeginDate(beginDate);
        trainingJournalHelper.insert(tj);
    }

}



















