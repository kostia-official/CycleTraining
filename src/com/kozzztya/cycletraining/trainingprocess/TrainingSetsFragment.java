package com.kozzztya.cycletraining.trainingprocess;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.ActionMenuView;
import android.text.TextUtils;
import android.view.LayoutInflater;
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

public class TrainingSetsFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ActionMenuView.OnMenuItemClickListener {

    public static final String KEY_TRAINING_ID = "trainingUri";
    public static final String[] PROJECTION_TRAINING = new String[]{
            Trainings._ID,
            Trainings.COMMENT,
            Trainings.IS_DONE
    };
    private static final String TAG = "log" + TrainingSetsFragment.class.getSimpleName();
    private static final int LOADER_SETS = 0;
    private static final int LOADER_TRAINING = 1;
    private TrainingSetsAdapter mSetsAdapter;
    private View mFooterComment;
    private View mHeaderSetList;

    private long mTrainingId;
    private ContentValues mTrainingValues;

    private SetsListCallbacks mCallbacks;

    /**
     * Initializes the fragment's arguments, and returns the new instance to the client.
     *
     * @param trainingId Id of the training, whose sets are to be displayed.
     */
    public static Fragment newInstance(long trainingId) {
        Bundle args = new Bundle();
        args.putLong(KEY_TRAINING_ID, trainingId);

        Fragment fragment = new TrainingSetsFragment();
        fragment.setArguments(args);
        return fragment;
    }

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

        mTrainingValues = new ContentValues();
        mCallbacks = (SetsListCallbacks) getParentFragment();
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
                Uri trainingUri = DatabaseProvider.uriParse(Trainings.TABLE_NAME, mTrainingId);
                return new CursorLoader(getActivity(), trainingUri, PROJECTION_TRAINING, null, null, null);
            case LOADER_SETS:
                String selection = Sets.TRAINING + "=" + mTrainingId;
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
                if (data.moveToFirst()) {
                    DatabaseUtils.cursorRowToContentValues(data, mTrainingValues);
                    showTrainingComment(mTrainingValues.getAsString(Trainings.COMMENT));
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_SETS)
            mSetsAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        if (view == mFooterComment)
            editComment(); // Edit footer data
        else // Subtract header position
            editSet(position - 1);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                addSet();
                return true;
            case R.id.action_comment:
                editComment();
                return true;
            case R.id.action_done:
                onDoneClick();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            onDoneClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        ContentValues setValues = new ContentValues();
        setValues.put(Sets.TRAINING, mTrainingId);

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
        Uri trainingUri = DatabaseProvider.uriParse(Trainings.TABLE_NAME, mTrainingId);
        bundle.putParcelable(CommentDialogFragment.KEY_TRAINING_URI, trainingUri);
        bundle.putString(CommentDialogFragment.KEY_COMMENT,
                mTrainingValues.getAsString(Trainings.COMMENT));

        DialogFragment dialogFragment = new CommentDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getFragmentManager(),
                CommentDialogFragment.class.getSimpleName());
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
            mTrainingId = bundle.getLong(KEY_TRAINING_ID);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(KEY_TRAINING_ID, mTrainingId);
        super.onSaveInstanceState(outState);
    }

    /**
     * On Done button click
     */
    public void onDoneClick() {
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
            contentResolver.update(DatabaseProvider.uriParse(Trainings.TABLE_NAME, mTrainingId),
                    trainingValues, null, null);

            // Notify table view that data was updated
            contentResolver.notifyChange(DatabaseProvider.TRAININGS_VIEW_URI, null);
        }
    }

    public interface SetsListCallbacks {
        /**
         * Training is done and this fragment can be replaced
         */
        public void onTrainingDone();
    }
}