package com.kozzztya.cycletraining.trainingprocess;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DatabaseProvider;
import com.kozzztya.cycletraining.db.Sets;
import com.kozzztya.cycletraining.db.Trainings;

public class TrainingSetsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private static final String TAG = "log" + TrainingSetsFragment.class.getSimpleName();

    private static final int LOADER_SETS = 0;
    private static final int LOADER_TRAINING = 1;

    public static final String KEY_TRAINING_URI = "trainingUri";

    public static final String[] PROJECTION_TRAINING = new String[]{
            Trainings._ID,
            Trainings.COMMENT,
            Trainings.IS_DONE
    };

    private TrainingSetsAdapter mSetsAdapter;
    private View mFooterComment;
    private View mHeaderSetList;

    private Uri mTrainingUri;
    private ContentValues mTrainingValues;

    private SetsListCallbacks mCallbacks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

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
        View view = inflater.inflate(R.layout.sets_data_fragment, container, false);
        mHeaderSetList = inflater.inflate(R.layout.set_list_header, null);
        mFooterComment = inflater.inflate(R.layout.comment_footer, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().addHeaderView(mHeaderSetList, null, false);

        initLoaders();
    }

    private void initLoaders() {
        mSetsAdapter = new TrainingSetsAdapter(getActivity(), R.layout.set_list_item, null, 0);
        getListView().setAdapter(mSetsAdapter);

        getLoaderManager().initLoader(LOADER_TRAINING, null, this);
        getLoaderManager().initLoader(LOADER_SETS, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_TRAINING:
                return new CursorLoader(getActivity(), mTrainingUri, PROJECTION_TRAINING, null, null, null);
            case LOADER_SETS:
                String selection = Sets.TRAINING + "=" + mTrainingUri.getLastPathSegment();
                return new CursorLoader(getActivity(), DatabaseProvider.SETS_URI, Sets.PROJECTION,
                        selection, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        switch (id) {
            case LOADER_SETS:
                mSetsAdapter.swapCursor(data);
                break;
            case LOADER_TRAINING:
                setTrainingValues(data);
                showTrainingComment(mTrainingValues.getAsString(Trainings.COMMENT));
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_SETS)
            mSetsAdapter.swapCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.getCustomView().findViewById(R.id.action_done).setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                addSet();
                return true;
            case R.id.action_comment:
                editComment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        if (view == mFooterComment)
            editComment(); // Edit footer data
        else // Subtract header position
            editSet(position - 1);
    }

    /**
     * Show dialog for editing set data
     *
     * @param position Position of set in adapter
     */
    private void editSet(int position) {
        Cursor cursor = (Cursor) mSetsAdapter.getItem(position);
        ContentValues setValues = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, setValues);

        Bundle bundle = new Bundle();
        bundle.putParcelable(SetEditDialogFragment.KEY_SET_VALUES, setValues);

        DialogFragment dialogFragment = new SetEditDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getFragmentManager(), SetEditDialogFragment.class.getSimpleName());
    }

    /**
     * Show dialog for entering new set data
     */
    private void addSet() {
        // Specify parent table id
        Long trainingId = mTrainingValues.getAsLong(Trainings._ID);
        ContentValues setValues = new ContentValues();
        setValues.put(Sets.TRAINING, trainingId);

        Bundle bundle = new Bundle();
        bundle.putParcelable(SetEditDialogFragment.KEY_SET_VALUES, setValues);

        DialogFragment dialogFragment = new SetEditDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getFragmentManager(),
                SetEditDialogFragment.class.getSimpleName());
    }

    /**
     * Show dialog for comment editing
     */
    private void editComment() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(CommentDialogFragment.KEY_TRAINING_URI, mTrainingUri);
        bundle.putString(CommentDialogFragment.KEY_COMMENT,
                mTrainingValues.getAsString(Trainings.COMMENT));

        DialogFragment dialogFragment = new CommentDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getFragmentManager(),
                CommentDialogFragment.class.getSimpleName());
    }

    public void setTrainingValues(Cursor cursor) {
        if (cursor.moveToFirst()) {
            mTrainingValues = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(cursor, mTrainingValues);
        }
    }

    /**
     * Show training comment in ListView footer
     */
    private void showTrainingComment(String comment) {
        if (!TextUtils.isEmpty(comment)) {
            // Add new footer for comment
            if (getListView().getFooterViewsCount() == 0)
                getListView().addFooterView(mFooterComment);

            // Change comment value
            TextView textViewComment = (TextView) mFooterComment.findViewById(R.id.comment);
            textViewComment.setText(comment);
        } else {
            // Delete footer for empty comment
            getListView().removeFooterView(mFooterComment);
        }
    }

    private void retrieveData(Bundle bundle) {
        if (bundle != null) {
            mTrainingUri = bundle.getParcelable(KEY_TRAINING_URI);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_TRAINING_URI, mTrainingUri);
        super.onSaveInstanceState(outState);
    }

    /**
     * On Done button click
     */
    @Override
    public void onClick(View v) {
        if (!isValidSets()) {
            return;
        }

        setTrainingDone();
        mCallbacks.onTrainingDone();
    }

    /**
     * Validate number format of reps and show wrong set number
     * Return true if all sets are valid
     */
    private boolean isValidSets() {
        Cursor cursor = mSetsAdapter.getCursor();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int setN = cursor.getPosition() + 1;
                try {
                    // Use valueOf to validate number format of reps
                    Integer.valueOf(cursor.getString(cursor.getColumnIndex(Sets.REPS)));
                } catch (NumberFormatException e) {
                    // Show wrong set number
                    Toast.makeText(getActivity(), String.format(getString(R.string.toast_input), setN),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
            } while (cursor.moveToNext());
        }
        return true;
    }

    /**
     * Update in db that training is done
     */
    private void setTrainingDone() {
        boolean isDone = mTrainingValues.getAsInteger(Trainings.IS_DONE) != 0;
        if (!isDone) {
            // Put true in done column
            ContentValues trainingValues = new ContentValues();
            trainingValues.put(Trainings.IS_DONE, 1);

            ContentResolver contentResolver = getActivity().getContentResolver();
            contentResolver.update(mTrainingUri, trainingValues, null, null);

            // Notify table view that data was updated
            contentResolver.notifyChange(DatabaseProvider.TRAININGS_VIEW_URI, null);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (SetsListCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement "
                    + SetsListCallbacks.class.getSimpleName());
        }
    }

    public interface SetsListCallbacks {
        /**
         * Training is done and this fragment can be replaced
         */
        public void onTrainingDone();
    }
}