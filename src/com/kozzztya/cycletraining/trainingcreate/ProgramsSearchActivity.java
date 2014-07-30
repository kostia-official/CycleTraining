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
import com.kozzztya.cycletraining.customviews.HintSpinner;
import com.kozzztya.cycletraining.db.datasources.ProgramsDS;
import com.kozzztya.cycletraining.db.datasources.PurposesDS;
import com.kozzztya.cycletraining.db.entities.Program;
import com.kozzztya.cycletraining.db.entities.ProgramView;
import com.kozzztya.cycletraining.db.entities.Purpose;

import java.util.*;

public class ProgramsSearchActivity extends MyActionBarActivity implements OnItemSelectedListener, OnChildClickListener {

    private HintSpinner weeksSpinner;
    private HintSpinner trainingsInWeekSpinner;
    private PurposeProgramsAdapter purposeProgramsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.programs_search);

        getSupportActionBar().setTitle(getString(R.string.programs_search));

        weeksSpinner = (HintSpinner) findViewById(R.id.spinnerWeeks);
        trainingsInWeekSpinner = (HintSpinner) findViewById(R.id.spinnerTrainingsInWeek);

        fillData();
    }

    public void fillData() {
        PurposesDS purposesDS = new PurposesDS(this);
        ProgramsDS programsDS = new ProgramsDS(this);

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
                purposeProgramsAdapter.resetFilter();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (trainingsInWeekSpinner.getSelectedItemPosition() >= 0) {
            int trainingsInWeek = (int) trainingsInWeekSpinner.getSelectedItem();
            purposeProgramsAdapter.filterByTrainingsInWeeks(trainingsInWeek);
        }

        if (weeksSpinner.getSelectedItemPosition() >= 0) {
            int weeks = (int) weeksSpinner.getSelectedItem();
            purposeProgramsAdapter.filterByWeeks(weeks);
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