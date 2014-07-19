package com.kozzztya.cycletraining;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.ProgramsDataSource;
import com.kozzztya.cycletraining.db.datasources.PurposesDataSource;
import com.kozzztya.cycletraining.db.entities.Program;
import com.kozzztya.cycletraining.db.entities.Purpose;
import com.kozzztya.cycletraining.utils.HintSpinner;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class ProgramsSearchActivity extends ActionBarActivity implements OnItemClickListener, OnItemSelectedListener {

    private DBHelper dbHelper;

    private HintSpinner purposeSpinner;
    private HintSpinner weeksSpinner;
    private HintSpinner trainingsInWeekSpinner;
    private ArrayAdapter programsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.programs_search);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(getString(R.string.programs_search));

        purposeSpinner = (HintSpinner) findViewById(R.id.spinnerPurpose);
        weeksSpinner = (HintSpinner) findViewById(R.id.spinnerWeeks);
        trainingsInWeekSpinner = (HintSpinner) findViewById(R.id.spinnerTrainingsInWeek);

        dbHelper = DBHelper.getInstance(this);

        fillData();
    }

    public void fillData() {
        PurposesDataSource purposesDataSource = dbHelper.getPurposesDataSource();
        ProgramsDataSource programsDataSource = dbHelper.getProgramsDataSource();

        ListView listViewPrograms = (ListView) findViewById(R.id.listViewPrograms);

        List<Program> programs = programsDataSource.select(null, null, null, null);
        programsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, programs);
        listViewPrograms.setAdapter(programsAdapter);
        listViewPrograms.setOnItemClickListener(this);

        List<Purpose> purposes = purposesDataSource.select(null, null, null, null);
        ArrayAdapter<Purpose> purposeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, purposes);
        purposeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        purposeSpinner.setAdapter(purposeAdapter);
        purposeSpinner.setOnItemSelectedListener(this);

        //Group and sort column weeks
        SortedSet<Integer> weeksSet = new TreeSet<>();
        for (Program p : programs) weeksSet.add(p.getWeeks());

        ArrayAdapter<Object> programsWeeksAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, weeksSet.toArray());
        programsWeeksAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        weeksSpinner.setAdapter(programsWeeksAdapter);
        weeksSpinner.setOnItemSelectedListener(this);

        //Group and sort column trainingsInWeek
        SortedSet<Integer> trainingsInWeekSet = new TreeSet<>();
        for (Program p : programs) trainingsInWeekSet.add(p.getTrainingsInWeek());

        ArrayAdapter<Object> trainingsInWeekAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, trainingsInWeekSet.toArray());
        trainingsInWeekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        trainingsInWeekSpinner.setAdapter(trainingsInWeekAdapter);
        trainingsInWeekSpinner.setOnItemSelectedListener(this);
    }

    private void search() {
        //TODO Use Filter
        ProgramsDataSource programsDataSource = dbHelper.getProgramsDataSource();
        String selection = "";
        if (purposeSpinner.getSelectedItemPosition() >= 0)
            selection += ProgramsDataSource.COLUMN_PURPOSE + " = " +
                    ((Purpose) purposeSpinner.getSelectedItem()).getId() + " AND ";

        if (weeksSpinner.getSelectedItemPosition() >= 0)
            selection += ProgramsDataSource.COLUMN_WEEKS + " = " +
                    weeksSpinner.getSelectedItem() + " AND ";

        if (trainingsInWeekSpinner.getSelectedItemPosition() >= 0)
            selection += ProgramsDataSource.COLUMN_TRAININGS_IN_WEEK + " = " +
                    trainingsInWeekSpinner.getSelectedItem() + " AND ";

        //Delete last AND
        selection = selection.substring(0, selection.length() - 5);

        List<Program> programs = programsDataSource.select(selection, null, null, null);
        programsAdapter.clear();
        programsAdapter.addAll(programs);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, MesocycleCreateActivity.class);
        intent.putExtra("program", (Program) parent.getItemAtPosition(position));
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
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