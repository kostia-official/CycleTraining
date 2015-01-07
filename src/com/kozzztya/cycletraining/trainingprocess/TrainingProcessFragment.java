package com.kozzztya.cycletraining.trainingprocess;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.kozzztya.cycletraining.MainActivity;
import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DatabaseProvider;
import com.kozzztya.cycletraining.db.Trainings;
import com.kozzztya.cycletraining.utils.DateUtils;

import java.sql.Date;

public class TrainingProcessFragment extends Fragment implements
        OnSharedPreferenceChangeListener, ViewPager.OnPageChangeListener,
        AdapterView.OnItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>,
        TrainingSetsFragment.SetsListCallbacks {

    private static final String TAG = "log" + TrainingProcessFragment.class.getSimpleName();

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
    private ActionMenuView mBottomMenu;

    private Preferences mPreferences;
    private Toolbar mToolbar;

    /**
     * Initializes the fragment's arguments, and returns the new instance to the client.
     *
     * @param trainingDay      The date of training day in milliseconds.
     * @param trainingPosition Position of the selected training.
     */
    public static Fragment newInstance(long trainingDay, int trainingPosition) {
        Bundle args = new Bundle();
        args.putLong(KEY_TRAINING_DAY, trainingDay);
        args.putInt(KEY_POSITION, trainingPosition);

        Fragment fragment = new TrainingProcessFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        mPreferences = new Preferences(getActivity());
        mPreferences.registerOnSharedPreferenceChangeListener(this);

        if (savedInstanceState != null) {
            // Restore data from saved instant state
            retrieveData(savedInstanceState);
        } else {
            // Retrieve data from intent
            retrieveData(getArguments());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.training_process, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mToolbar = ((MainActivity) getActivity()).getToolbar();
        createBottomMenu(view);

        setUpNavigation();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateToolbar();
    }

    @Override
    public void onPause() {
        mToolbar.removeView(mNavigationSpinner);
        super.onPause();
    }

    /**
     * Update toolbar content on fragment change.
     */
    private void updateToolbar() {
        getActivity().setTitle(null); // Disable regular title

        if (mToolbar.findViewById(R.id.navigation_spinner) == null) {
            mToolbar.addView(mNavigationSpinner);
        }
    }

    /**
     * Set up spinner navigation with swiped pages
     */
    private void setUpNavigation() {
        // Navigation spinner in toolbar for trainings selection
        LayoutInflater.from(mToolbar.getContext()).inflate(R.layout.navigation_spinner, mToolbar, true);
        mNavigationSpinner = (Spinner) mToolbar.findViewById(R.id.navigation_spinner);
        mNavigationSpinner.setOnItemSelectedListener(this);
        mNavigationSpinner.setSelection(mPosition);

        String[] from = new String[]{Trainings.EXERCISE};
        int[] to = new int[]{R.id.title};
        mNavigationAdapter = new NavigationAdapter(mToolbar.getContext(),
                R.layout.navigation_spinner_item, null, from, to, 0);
        mNavigationAdapter.setDropDownViewResource(R.layout.navigation_spinner_dropdown_item);
        mNavigationSpinner.setAdapter(mNavigationAdapter);

        // ViewPager for swipe navigation and animation on training select
        mViewPager.setOnPageChangeListener(this);
        mPagerAdapter = new TrainingPagerAdapter(getChildFragmentManager(), null);
        mViewPager.setAdapter(mPagerAdapter);

        Loader<Cursor> loader = getLoaderManager().getLoader(0);
        if (loader != null && !loader.isReset()) {
            getLoaderManager().restartLoader(0, null, this);
        } else {
            getLoaderManager().initLoader(0, null, this);
        }
    }

    /**
     * Create ActionMenuView on the bottom of the Activity
     */
    private void createBottomMenu(View view) {
        ViewGroup bottomMenuStub = (ViewGroup) view.findViewById(R.id.bottom_menu_stub);
        LayoutInflater.from(mToolbar.getContext()).inflate(R.layout.training_process_bottom_menu,
                bottomMenuStub, true);
        mBottomMenu = (ActionMenuView) bottomMenuStub.findViewById(R.id.bottom_menu);

        getActivity().getMenuInflater().inflate(R.menu.training_process, mBottomMenu.getMenu());

        mTimerMenuItem = new TimerMenuItem(getActivity(), mBottomMenu.getMenu().findItem(R.id.action_timer));
        mTimerMenuItem.configure(mPreferences.getTimerValue(), mPreferences.isVibrateTimer());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = Trainings.DATE + "=" + DateUtils.sqlFormat(mTrainingDay);
        return new CursorLoader(getActivity(), DatabaseProvider.TRAININGS_VIEW_URI,
                PROJECTION_TRAININGS, selection, null, Trainings.PRIORITY);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mNavigationAdapter.swapCursor(data);
        mPagerAdapter.swapCursor(data);

        selectTraining(mPosition);
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(KEY_TRAINING_DAY, mTrainingDay.getTime());
        outState.putInt(KEY_POSITION, mPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.done, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Callback from TrainingSetsFragment that training is done
     * and we can move to the next page
     */
    @Override
    public void onTrainingDone() {
        // If on the last page
        if (mPosition == mViewPager.getAdapter().getCount() - 1)
            getFragmentManager().popBackStack(); // Finish training
        else // Move to the next page
            mViewPager.setCurrentItem(mPosition + 1);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mTimerMenuItem.configure(mPreferences.getTimerValue(), mPreferences.isVibrateTimer());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectTraining(position);
    }

    @Override
    public void onPageSelected(int position) {
        selectTraining(position);
    }

    /**
     * Show selected training and set its behavior
     *
     * @param position Position of the training
     */
    private void selectTraining(int position) {
        mPosition = position;

        if (mNavigationSpinner.getSelectedItemPosition() != position)
            mNavigationSpinner.setSelection(position);

        if (mViewPager.getCurrentItem() != position)
            mViewPager.setCurrentItem(position);

        TrainingSetsFragment fragment = (TrainingSetsFragment) mPagerAdapter.getItem(position);
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