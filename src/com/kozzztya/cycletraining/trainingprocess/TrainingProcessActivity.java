package com.kozzztya.cycletraining.trainingprocess;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.kozzztya.cycletraining.MyActionBarActivity;
import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DatabaseProvider;
import com.kozzztya.cycletraining.db.Trainings;
import com.kozzztya.cycletraining.utils.DateUtils;

import java.sql.Date;

public class TrainingProcessActivity extends MyActionBarActivity implements
        OnSharedPreferenceChangeListener, ViewPager.OnPageChangeListener,
        AdapterView.OnItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>,
        TrainingSetsFragment.SetsListCallbacks {

    private static final String TAG = "log" + TrainingProcessActivity.class.getSimpleName();

    public static final String KEY_TRAINING_DAY = "trainingDay";
    public static final String KEY_POSITION = "position";

    private static final String[] PROJECTION_TRAININGS = new String[]{
            Trainings._ID,
            Trainings.EXERCISE,
            Trainings.DATE,
            Trainings.IS_DONE
    };

    private Date mTrainingDay;
    private int mPosition;

    private ViewPager mViewPager;
    private Spinner mNavigationSpinner;
    private SimpleCursorAdapter mNavigationAdapter;
    private TrainingPagerAdapter mPagerAdapter;

    private TimerMenuItem mTimerMenuItem;
    private Preferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_process);

        mPreferences = new Preferences(this);
        mPreferences.registerOnSharedPreferenceChangeListener(this);

        if (savedInstanceState != null) {
            // Restore data from saved instant state
            retrieveData(savedInstanceState);
        } else {
            // Retrieve data from intent
            retrieveData(getIntent().getExtras());
        }

        setUpNavigation();
    }

    private void setUpNavigation() {
        // Custom ActionBar with navigation spinner and done MenuItem
        View trainingsDoneActionBar = getLayoutInflater().inflate(R.layout.trainings_done_actionbar, null);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(trainingsDoneActionBar);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        // ViewPager for swipe navigation and animation on training select
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(this);
        mPagerAdapter = new TrainingPagerAdapter(getSupportFragmentManager(), null);
        mViewPager.setAdapter(mPagerAdapter);

        // Spinner for trainings selection
        mNavigationSpinner = (Spinner) trainingsDoneActionBar.findViewById(R.id.navigation_spinner);
        mNavigationSpinner.setOnItemSelectedListener(this);
        mNavigationSpinner.setSelection(mPosition);

        String[] from = new String[]{Trainings.EXERCISE};
        int[] to = new int[]{R.id.title};
        mNavigationAdapter = new NavigationAdapter(getSupportActionBar().getThemedContext(),
                R.layout.navigation_spinner_item, null, from, to, 0);
        mNavigationAdapter.setDropDownViewResource(R.layout.navigation_spinner_dropdown_item);
        mNavigationSpinner.setAdapter(mNavigationAdapter);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = Trainings.DATE + "=" + DateUtils.sqlFormat(mTrainingDay);
        return new CursorLoader(this, DatabaseProvider.TRAININGS_VIEW_URI,
                PROJECTION_TRAININGS, selection, null, Trainings.PRIORITY);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mNavigationAdapter.swapCursor(data);
        mPagerAdapter.swapCursor(data);

        mNavigationSpinner.setSelection(mPosition);
        mViewPager.setCurrentItem(mPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNavigationAdapter.swapCursor(null);
        mPagerAdapter.swapCursor(null);
    }

    private void retrieveData(Bundle bundle) {
        if (bundle != null) {
            mTrainingDay = new Date(bundle.getLong(KEY_TRAINING_DAY));
            mPosition = bundle.getInt(KEY_POSITION, 0);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(KEY_TRAINING_DAY, mTrainingDay.getTime());
        outState.putInt(KEY_POSITION, mPosition);
        super.onSaveInstanceState(outState);
    }

    /**
     * Callback from TrainingSetsFragment that training is done
     * and Activity can move to the next page
     */
    @Override
    public void onTrainingDone() {
        // If on the last page
        if (mPosition == mViewPager.getAdapter().getCount() - 1)
            finish(); // Finish training
        else // Move to the next page
            mViewPager.setCurrentItem(mPosition + 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.training_process, menu);
        mTimerMenuItem = new TimerMenuItem(this, menu.findItem(R.id.action_timer));
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
        mPosition = position;
    }

    @Override
    public void onPageSelected(int position) {
        mNavigationSpinner.setSelection(position);
        mPosition = position;
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
        mPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
}