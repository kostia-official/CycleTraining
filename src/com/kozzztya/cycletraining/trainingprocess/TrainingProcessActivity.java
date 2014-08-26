package com.kozzztya.cycletraining.trainingprocess;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;
import com.kozzztya.cycletraining.MyActionBarActivity;
import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.SetsDS;
import com.kozzztya.cycletraining.db.datasources.TrainingsDS;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.utils.DateUtils;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class TrainingProcessActivity extends MyActionBarActivity implements OnSharedPreferenceChangeListener,
        ViewPager.OnPageChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private ViewPager viewPager;

    private TrainingsDS trainingsDS;
    private SetsDS setsDS;

    //Collection for sets of trainings
    private LinkedHashMap<TrainingView, List<Set>> trainingsSets;
    private List<TrainingView> trainingsByDay;

    private TimerMenuItem timerMenuItem;
    private Preferences preferences;
    private DBHelper dbHelper;
    private ActionBar actionBar;
    private Spinner navigationSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_process);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        dbHelper = DBHelper.getInstance(this);
        trainingsDS = new TrainingsDS(dbHelper);
        setsDS = new SetsDS(dbHelper);

        preferences = new Preferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        initTrainingData();
    }

    private void initTrainingData() {
        Bundle extras = getIntent().getExtras();
        Date dayOfTrainings = new Date(extras.getLong("dayOfTraining"));
        long chosenTrainingId = extras.getLong("chosenTrainingId");

        //Select trainings by chosen day
        String where = TrainingsDS.COLUMN_DATE + " = " + DateUtils.sqlFormat(dayOfTrainings);
        String orderBy = TrainingsDS.COLUMN_PRIORITY;
        trainingsByDay = trainingsDS.selectView(where, null, null, orderBy);
        trainingsSets = new LinkedHashMap<>();

        int chosenTrainingPos = 0;
        for (TrainingView t : trainingsByDay) {
            //Select sets of training
            where = SetsDS.COLUMN_TRAINING + " = " + t.getId();
            List<Set> sets = setsDS.select(where, null, null, null);
            trainingsSets.put(t, sets);

            //Determine chosen training position
            if (t.getId() == chosenTrainingId)
                chosenTrainingPos = trainingsByDay.indexOf(t);
        }

        //Custom ActionBar with navigation spinner and done MenuItem
        View trainingsDoneActionBar = getLayoutInflater().inflate(R.layout.trainings_done_actionbar, null);
        trainingsDoneActionBar.findViewById(R.id.action_done).setOnClickListener(this);
        actionBar.setCustomView(trainingsDoneActionBar);

        //Spinner for trainings selection
        navigationSpinner = (Spinner) trainingsDoneActionBar.findViewById(R.id.navigation_spinner);
        navigationSpinner.setAdapter(new NavigationSpinnerAdapter(getSupportActionBar().getThemedContext(),
                R.layout.navigation_spinner_item, R.layout.navigation_spinner_dropdown_item, trainingsByDay));
        navigationSpinner.setOnItemSelectedListener(this);
        navigationSpinner.setSelection(chosenTrainingPos);

        //ViewPager for swipe navigation and animation on training select
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new TrainingPagerAdapter(getSupportFragmentManager(), trainingsSets));
        viewPager.setCurrentItem(chosenTrainingPos, false);
        viewPager.setOnPageChangeListener(this);
    }

    /**
     * On training done menu item click
     */
    @Override
    public void onClick(View v) {
        int pos = viewPager.getCurrentItem();
        TrainingView training = trainingsByDay.get(pos);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        List<Set> sets = trainingsSets.get(training);
        int setN = 0; //Set number for error message
        try {
            for (Set s : sets) {
                setN++;

                //Use valueOf to validate number format of reps
                Integer.parseInt(s.getReps());

                //Update in DB training status and set data
                training.setDone(true);
                trainingsDS.update(training);
                setsDS.update(s);
            }

            //If on the last tab
            if (pos == viewPager.getAdapter().getCount() - 1)
                finish();
            else //Go to the next tab
                viewPager.setCurrentItem(pos + 1);

            db.setTransactionSuccessful();
            dbHelper.notifyDBChanged();
        } catch (NumberFormatException e) {
            Toast.makeText(this, String.format(getString(R.string.toast_input), setN), Toast.LENGTH_LONG).show();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.training_process, menu);
        timerMenuItem = new TimerMenuItem(this, menu);
        timerMenuItem.configure(preferences.getTimerValue(), preferences.isVibrateTimer());

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        timerMenuItem.configure(preferences.getTimerValue(), preferences.isVibrateTimer());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onPageSelected(int i) {
        navigationSpinner.setSelection(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @Override
    public void onDestroy() {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

}