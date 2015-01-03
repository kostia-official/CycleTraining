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
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import com.kozzztya.cycletraining.BaseActivity;
import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DatabaseProvider;
import com.kozzztya.cycletraining.db.Trainings;
import com.kozzztya.cycletraining.utils.DateUtils;

import java.sql.Date;

public class TrainingProcessActivity extends BaseActivity implements
        OnSharedPreferenceChangeListener, ViewPager.OnPageChangeListener,
        AdapterView.OnItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>,
        TrainingSetsFragment.SetsListCallbacks {

    public static final String KEY_TRAINING_DAY = "trainingDay";
    public static final String KEY_POSITION = "position";
    private static final String TAG = "log" + TrainingProcessActivity.class.getSimpleName();
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
    private ActionMenuView mBottomMenu;

    private Preferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewGroup contentView = (ViewGroup) findViewById(R.id.content);
        getLayoutInflater().inflate(R.layout.training_process, contentView);

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
        createBottomMenu();
    }

    /**
     * Set up spinner navigation with swiped pages
     */
    private void setUpNavigation() {
        Toolbar toolbar = getToolbar();
        toolbar.setTitle(null); // Disable regular title

        // Navigation spinner into toolbar for trainings selection
        LayoutInflater.from(toolbar.getContext()).inflate(R.layout.navigation_spinner, toolbar, true);
        mNavigationSpinner = (Spinner) toolbar.findViewById(R.id.navigation_spinner);
        mNavigationSpinner.setOnItemSelectedListener(this);
        mNavigationSpinner.setSelection(mPosition);

        String[] from = new String[]{Trainings.EXERCISE};
        int[] to = new int[]{R.id.title};
        mNavigationAdapter = new NavigationAdapter(toolbar.getContext(),
                R.layout.navigation_spinner_item, null, from, to, 0);
        mNavigationAdapter.setDropDownViewResource(R.layout.navigation_spinner_dropdown_item);
        mNavigationSpinner.setAdapter(mNavigationAdapter);

        // ViewPager for swipe navigation and animation on training select
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(this);
        mPagerAdapter = new TrainingPagerAdapter(getSupportFragmentManager(), null);
        mViewPager.setAdapter(mPagerAdapter);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    /**
     * Create ActionMenuView on the bottom of the Activity
     */
    private void createBottomMenu() {
        // Inflate BottomBar with ActionBar ThemedContext
        ViewGroup bottomMenuStub = (ViewGroup) findViewById(R.id.bottom_menu_stub);
        LayoutInflater.from(getSupportActionBar().getThemedContext())
                .inflate(R.layout.training_process_bottom_menu, bottomMenuStub, true);
        mBottomMenu = (ActionMenuView) bottomMenuStub.findViewById(R.id.bottom_menu);

        getMenuInflater().inflate(R.menu.training_process, mBottomMenu.getMenu());

        mTimerMenuItem = new TimerMenuItem(this, mBottomMenu.getMenu().findItem(R.id.action_timer));
        mTimerMenuItem.configure(mPreferences.getTimerValue(), mPreferences.isVibrateTimer());
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

        selectPage(mPosition);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done, menu);
        return super.onCreateOptionsMenu(menu);
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mTimerMenuItem.configure(mPreferences.getTimerValue(), mPreferences.isVibrateTimer());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectPage(position);
    }

    @Override
    public void onPageSelected(int position) {
        selectPage(position);
    }

    private void selectPage(int position) {
        if (mNavigationSpinner.getSelectedItemPosition() != position)
            mNavigationSpinner.setSelection(position);

        if (mViewPager.getCurrentItem() != position)
            mViewPager.setCurrentItem(position);

        mPosition = position;

        TrainingSetsFragment fragment = mPagerAdapter.getItem(position);
        mBottomMenu.setOnMenuItemClickListener(fragment);
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