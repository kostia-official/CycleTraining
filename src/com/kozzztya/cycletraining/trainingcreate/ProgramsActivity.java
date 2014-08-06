package com.kozzztya.cycletraining.trainingcreate;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import com.kozzztya.cycletraining.MyActionBarActivity;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.adapters.PurposeProgramsAdapter;
import com.kozzztya.cycletraining.customviews.PromptSpinner;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.ProgramsDS;
import com.kozzztya.cycletraining.db.datasources.PurposesDS;
import com.kozzztya.cycletraining.db.entities.Program;
import com.kozzztya.cycletraining.db.entities.ProgramView;
import com.kozzztya.cycletraining.db.entities.Purpose;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ProgramsActivity extends MyActionBarActivity implements OnItemSelectedListener, OnChildClickListener {

    private PromptSpinner weeksSpinner;
    private PromptSpinner trainingsInWeekSpinner;
    private PurposeProgramsAdapter purposeProgramsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.programs);

        getSupportActionBar().setTitle(getString(R.string.programs_search));

        weeksSpinner = (PromptSpinner) findViewById(R.id.spinnerWeeks);
        trainingsInWeekSpinner = (PromptSpinner) findViewById(R.id.spinnerTrainingsInWeek);

        fillData();
    }

    public void fillData() {
        DBHelper dbHelper = DBHelper.getInstance(this);
        PurposesDS purposesDS = new PurposesDS(dbHelper);
        ProgramsDS programsDS = new ProgramsDS(dbHelper);

        List<Purpose> purposes = purposesDS.select(null, null, null, null);
        List<ProgramView> programs = programsDS.selectView(null, null, null, null);

        //Programs grouped by purpose
        LinkedHashMap<Purpose, List<ProgramView>> purposePrograms = new LinkedHashMap<>();

        for (Purpose purpose : purposes) {
            for (ProgramView program : programs) {
                if (program.getPurpose() == purpose.getId()) {
                    if (!purposePrograms.containsKey(purpose)) {
                        purposePrograms.put(purpose, new ArrayList<ProgramView>());
                    }
                    purposePrograms.get(purpose).add(program);
                }
            }
        }

        ExpandableListView expListExercises = (ExpandableListView) findViewById(R.id.expListPrograms);
        purposeProgramsAdapter = new PurposeProgramsAdapter(this, purposePrograms);
        expListExercises.setAdapter(purposeProgramsAdapter);
        expListExercises.setOnChildClickListener(this);

        //Group and sort values of column weeks
        SortedSet<Integer> weeksSet = new TreeSet<>();
        for (Program p : programs) weeksSet.add(p.getWeeks());

        ArrayAdapter<Integer> programsWeeksAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(weeksSet));
        programsWeeksAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weeksSpinner.setAdapter(programsWeeksAdapter);
        weeksSpinner.setOnItemSelectedListener(this);

        //Group and sort column trainingsInWeek
        SortedSet<Integer> trainingsInWeekSet = new TreeSet<>();
        for (ProgramView p : programs) trainingsInWeekSet.add(p.getTrainingsInWeek());

        ArrayAdapter<Integer> trainingsInWeekAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(trainingsInWeekSet));
        trainingsInWeekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trainingsInWeekSpinner.setAdapter(trainingsInWeekAdapter);
        trainingsInWeekSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.programs, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset:
                resetFilter();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetFilter() {
        purposeProgramsAdapter.resetFilter();
        //Select hint items
        weeksSpinner.setSelection(-1);
        trainingsInWeekSpinner.setSelection(-1);
    }

    //Use filter by selected item of spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            purposeProgramsAdapter.resetFilter();
            if (trainingsInWeekSpinner.getSelectedItemPosition() >= 0) {
                int trainingsInWeek = (int) trainingsInWeekSpinner.getSelectedItem();
                purposeProgramsAdapter.filterChildren(
                        ProgramView.class.getMethod("getTrainingsInWeek"), trainingsInWeek);
            }

            if (weeksSpinner.getSelectedItemPosition() >= 0) {
                int weeks = (int) weeksSpinner.getSelectedItem();
                purposeProgramsAdapter.filterChildren(
                        ProgramView.class.getMethod("getWeeks"), weeks);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //do nothing
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Program program = purposeProgramsAdapter.getChild(groupPosition, childPosition);
        Intent intent = new Intent(this, TrainingCreateActivity.class);
        intent.putExtra("program", program);
        setResult(RESULT_OK, intent);

        finish();
        return true;
    }
}