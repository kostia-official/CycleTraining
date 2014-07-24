package com.kozzztya.cycletraining.trainingcreate;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.kozzztya.cycletraining.MyActionBarActivity;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.customviews.HintSpinner;
import com.kozzztya.cycletraining.db.datasources.ProgramsDS;
import com.kozzztya.cycletraining.db.datasources.PurposesDS;
import com.kozzztya.cycletraining.db.entities.Program;
import com.kozzztya.cycletraining.db.entities.Purpose;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class ProgramsSearchActivity extends MyActionBarActivity implements OnItemClickListener, OnItemSelectedListener {

    private HintSpinner purposeSpinner;
    private HintSpinner weeksSpinner;
    private HintSpinner trainingsInWeekSpinner;
    private ArrayAdapter programsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.programs_search);

        getSupportActionBar().setTitle(getString(R.string.programs_search));

        purposeSpinner = (HintSpinner) findViewById(R.id.spinnerPurpose);
        weeksSpinner = (HintSpinner) findViewById(R.id.spinnerWeeks);
        trainingsInWeekSpinner = (HintSpinner) findViewById(R.id.spinnerTrainingsInWeek);

        fillData();
    }

    public void fillData() {
        PurposesDS purposesDS = new PurposesDS(this);
        ProgramsDS programsDS = new ProgramsDS(this);

        ListView listViewPrograms = (ListView) findViewById(R.id.listViewPrograms);

        List<Program> programs = programsDS.select(null, null, null, null);
        programsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, programs);
        listViewPrograms.setAdapter(programsAdapter);
        listViewPrograms.setOnItemClickListener(this);

        List<Purpose> purposes = purposesDS.select(null, null, null, null);
        ArrayAdapter<Purpose> purposeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, purposes);
        purposeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        purposeSpinner.setAdapter(purposeAdapter);
        purposeSpinner.setOnItemSelectedListener(this);

        //Group and sort column weeks
        SortedSet<Integer> weeksSet = new TreeSet<>();
        for (Program p : programs) weeksSet.add(p.getWeeks());

        ArrayAdapter<Object> programsWeeksAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, weeksSet.toArray());
        programsWeeksAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weeksSpinner.setAdapter(programsWeeksAdapter);
        weeksSpinner.setOnItemSelectedListener(this);

        //Group and sort column trainingsInWeek
        SortedSet<Integer> trainingsInWeekSet = new TreeSet<>();
        //TODO for (Program p : programs) trainingsInWeekSet.add(p.getTrainingsInWeek());

        ArrayAdapter<Object> trainingsInWeekAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, trainingsInWeekSet.toArray());
        trainingsInWeekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trainingsInWeekSpinner.setAdapter(trainingsInWeekAdapter);
        trainingsInWeekSpinner.setOnItemSelectedListener(this);
    }

    private void search() {
        //TODO Use Filter
        ProgramsDS programsDS = new ProgramsDS(this);
        String selection = "";
        if (purposeSpinner.getSelectedItemPosition() >= 0)
            selection += ProgramsDS.COLUMN_PURPOSE + " = " +
                    ((Purpose) purposeSpinner.getSelectedItem()).getId() + " AND ";

        if (weeksSpinner.getSelectedItemPosition() >= 0)
            selection += ProgramsDS.COLUMN_WEEKS + " = " +
                    weeksSpinner.getSelectedItem() + " AND ";

//        TODO if (trainingsInWeekSpinner.getSelectedItemPosition() >= 0)
//            selection += ProgramsDataSource.COLUMN_TRAININGS_IN_WEEK + " = " +
//                    trainingsInWeekSpinner.getSelectedItem() + " AND ";

        //Delete last AND
        selection = selection.substring(0, selection.length() - 5);

        List<Program> programs = programsDS.select(selection, null, null, null);
        programsAdapter.clear();
        programsAdapter.addAll(programs);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, TrainingCreateActivity.class);
        intent.putExtra("program", (Program) parent.getItemAtPosition(position));
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset:
                fillData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        search();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //do nothing
    }
}