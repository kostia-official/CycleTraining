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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TrainingProcessActivity extends MyActionBarActivity implements OnSharedPreferenceChangeListener,
        ViewPager.OnPageChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "log" + TrainingProcessActivity.class.getSimpleName();

    public static final String KEY_TRAININGS = "trainings";
    public static final String KEY_CHOSEN_TRAINING_ID = "chosenTrainingId";
    public static final String KEY_POSITION = "position";

    private DBHelper mDBHelper;
    private TrainingsDS mTrainingsDS;
    private SetsDS mSetsDS;

    //Collection for sets of trainings
    private LinkedHashMap<TrainingView, List<Set>> mTrainingsSets;
    private List<TrainingView> mTrainingsByDay;

    private ViewPager mViewPager;
    private Spinner mNavigationSpinner;
    private TimerMenuItem mTimerMenuItem;
    private Preferences mPreferences;
    private ActionBar mActionBar;

    private long mChosenTrainingId;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_process);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowCustomEnabled(true);

        mDBHelper = DBHelper.getInstance(this);
        mTrainingsDS = new TrainingsDS(mDBHelper);
        mSetsDS = new SetsDS(mDBHelper);

        mPreferences = new Preferences(this);
        mPreferences.registerOnSharedPreferenceChangeListener(this);

        if (savedInstanceState != null) {
            //Restore data from saved instant state
            retrieveData(savedInstanceState);
        } else {
            //Retrieve data from intent
            retrieveData(getIntent().getExtras());
        }

        bindData();
    }

    private void retrieveData(Bundle bundle) {
        if (bundle != null) {
            mTrainingsByDay = bundle.getParcelableArrayList(KEY_TRAININGS);
            mChosenTrainingId = bundle.getLong(KEY_CHOSEN_TRAINING_ID, -1);
            mPosition = bundle.getInt(KEY_POSITION, 0);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //KEY_CHOSEN_TRAINING_ID don't need after recreating
        outState.putParcelableArrayList(KEY_TRAININGS, (ArrayList<TrainingView>) mTrainingsByDay);
        outState.putInt(KEY_POSITION, mPosition);
        super.onSaveInstanceState(outState);
    }

    private void bindData() {
        //Select sets of training
        mTrainingsSets = new LinkedHashMap<>();
        for (TrainingView t : mTrainingsByDay) {
            String where = SetsDS.COLUMN_TRAINING + " = " + t.getId();
            List<Set> sets = mSetsDS.select(where, null, null, null);
            mTrainingsSets.put(t, sets);

            //Determine chosen training position
            if (t.getId() == mChosenTrainingId)
                mPosition = mTrainingsByDay.indexOf(t);
        }

        //Custom ActionBar with navigation spinner and done MenuItem
        View trainingsDoneActionBar = getLayoutInflater().inflate(R.layout.trainings_done_actionbar, null);
        trainingsDoneActionBar.findViewById(R.id.action_done).setOnClickListener(this);
        mActionBar.setCustomView(trainingsDoneActionBar);

        //Spinner for trainings selection
        mNavigationSpinner = (Spinner) trainingsDoneActionBar.findViewById(R.id.navigation_spinner);
        mNavigationSpinner.setAdapter(new NavigationSpinnerAdapter(getSupportActionBar().getThemedContext(),
                R.layout.navigation_spinner_item, R.layout.navigation_spinner_dropdown_item, mTrainingsByDay));
        mNavigationSpinner.setOnItemSelectedListener(this);
        mNavigationSpinner.setSelection(mPosition);

        //ViewPager for swipe navigation and animation on training select
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new TrainingPagerAdapter(getSupportFragmentManager(), mTrainingsSets));
        mViewPager.setCurrentItem(mPosition, false);
        mViewPager.setOnPageChangeListener(this);
    }

    /**
     * On training done menu item click
     */
    @Override
    public void onClick(View v) {
        int pos = mViewPager.getCurrentItem();
        TrainingView training = mTrainingsByDay.get(pos);

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();

        List<Set> sets = mTrainingsSets.get(training);
        int setN = 0; //Set number for error message
        try {
            for (Set s : sets) {
                setN++;

                //Use valueOf to validate number format of reps
                Integer.parseInt(s.getReps());

                //Update in DB training status
                training.setDone(true);
                mTrainingsDS.update(training);

                //Update old sets or insert new
                if (!mSetsDS.update(s))
                    mSetsDS.insert(s);
            }

            //If on the last tab
            if (pos == mViewPager.getAdapter().getCount() - 1)
                finish();
            else //Go to the next tab
                mViewPager.setCurrentItem(pos + 1);

            db.setTransactionSuccessful();
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
        mTimerMenuItem = new TimerMenuItem(this, menu);
        mTimerMenuItem.configure(mPreferences.getTimerValue(), mPreferences.isVibrateTimer());

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mTimerMenuItem.configure(mPreferences.getTimerValue(), mPreferences.isVibrateTimer());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void onPageSelected(int i) {
        mNavigationSpinner.setSelection(i);
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
        mDBHelper.notifyDBChanged();
        mPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

}