package com.kozzztya.cycletraining.trainingcreate;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.custom.ExpandableListFragment;
import com.kozzztya.cycletraining.custom.PromptSpinner;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.ProgramsDS;
import com.kozzztya.cycletraining.db.datasources.PurposesDS;
import com.kozzztya.cycletraining.db.entities.Program;
import com.kozzztya.cycletraining.db.entities.ProgramView;
import com.kozzztya.cycletraining.db.entities.Purpose;
import com.kozzztya.cycletraining.utils.StyleUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class ProgramsFragment extends ExpandableListFragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "log" + ProgramsFragment.class.getSimpleName();

    private PromptSpinner mWeeksSpinner;
    private PromptSpinner mTrainingsInWeekSpinner;
    private MySimpleExpListAdapter<Purpose, ProgramView> mPurposeProgramsAdapter;

    private ProgramsCallbacks mCallbacks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StyleUtils.setExpListViewCardStyle(getExpandableListView(), getActivity());

        View filterHeader = getLayoutInflater(savedInstanceState)
                .inflate(R.layout.programs_filter_header, null);
        getExpandableListView().addHeaderView(filterHeader);

        mWeeksSpinner = (PromptSpinner) filterHeader.findViewById(R.id.spinnerWeeks);
        mTrainingsInWeekSpinner = (PromptSpinner) filterHeader.findViewById(R.id.spinnerTrainingsInWeek);

        bindData();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (ProgramsCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement "
                    + ProgramsCallbacks.class.getSimpleName());
        }
    }


    private void bindData() {
        DBHelper dbHelper = DBHelper.getInstance(getActivity());
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

        mPurposeProgramsAdapter = new MySimpleExpListAdapter<>(getActivity(), purposePrograms);
        setListAdapter(mPurposeProgramsAdapter);

        //Populate spinners with filter data
        //Group and sort values of column weeks
        SortedSet<Integer> weeksSet = new TreeSet<>();
        for (ProgramView p : programs) weeksSet.add(p.getWeeks());

        ArrayAdapter<Integer> programsWeeksAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, new ArrayList<>(weeksSet));
        programsWeeksAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mWeeksSpinner.setAdapter(programsWeeksAdapter);
        mWeeksSpinner.setOnItemSelectedListener(this);

        //Group and sort column trainingsInWeek
        SortedSet<Integer> trainingsInWeekSet = new TreeSet<>();
        for (ProgramView p : programs) trainingsInWeekSet.add(p.getTrainingsInWeek());

        ArrayAdapter<Integer> trainingsInWeekAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, new ArrayList<>(trainingsInWeekSet));
        trainingsInWeekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTrainingsInWeekSpinner.setAdapter(trainingsInWeekAdapter);
        mTrainingsInWeekSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        ProgramView program = mPurposeProgramsAdapter.getChild(groupPosition, childPosition);
        mCallbacks.onProgramSelected(program);
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.programs, menu);
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
        mPurposeProgramsAdapter.resetFilter();
        //Select prompt items
        mWeeksSpinner.setSelection(-1);
        mTrainingsInWeekSpinner.setSelection(-1);
    }

    //Filter by selected item of spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            mPurposeProgramsAdapter.resetFilter();
            if (mTrainingsInWeekSpinner.getSelectedItemPosition() >= 0) {
                int trainingsInWeek = (int) mTrainingsInWeekSpinner.getSelectedItem();
                mPurposeProgramsAdapter.filterChildren(
                        ProgramView.class.getMethod("getTrainingsInWeek"), trainingsInWeek);
            }

            if (mWeeksSpinner.getSelectedItemPosition() >= 0) {
                int weeks = (int) mWeeksSpinner.getSelectedItem();
                mPurposeProgramsAdapter.filterChildren(
                        ProgramView.class.getMethod("getWeeks"), weeks);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Do nothing
    }

    public interface ProgramsCallbacks {
        public void onProgramSelected(Program program);
    }
}
