package com.kozzztya.cycletraining.trainingcreate;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ExpandableListView;

import com.kozzztya.cycletraining.MySimpleCursorTreeAdapter;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.custom.ExpandableListFragment;
import com.kozzztya.cycletraining.db.DatabaseProvider;
import com.kozzztya.cycletraining.db.Programs;
import com.kozzztya.cycletraining.db.Purposes;
import com.kozzztya.cycletraining.utils.ViewUtils;

public class ProgramsFragment extends ExpandableListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "log" + ProgramsFragment.class.getSimpleName();

    //Loaders of programs get id from purpose row id
    private static final int LOADER_PURPOSES = -1;

    private static final String[] PROJECTION_PROGRAMS = new String[]
            {Programs._ID, Programs.DISPLAY_NAME};

    private MySimpleCursorTreeAdapter mAdapter;

    private ProgramsCallbacks mCallbacks;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewUtils.setExpListViewCardStyle(getExpandableListView(), getActivity());
        initLoader();
    }

    private void initLoader() {
        String[] groupFrom = new String[]{Purposes.DISPLAY_NAME};
        int[] groupTo = new int[]{R.id.title};
        String[] childFrom = new String[]{Programs.DISPLAY_NAME};
        int[] childTo = new int[]{R.id.title};

        mAdapter = new MySimpleCursorTreeAdapter(getActivity(), null,
                R.layout.group_list_item, R.layout.group_list_item_expanded, groupFrom, groupTo,
                R.layout.child_list_item, R.layout.child_list_item_last, childFrom, childTo, this);
        setListAdapter(mAdapter);
        setListShown(false);

        Loader<Cursor> loader = getLoaderManager().getLoader(LOADER_PURPOSES);
        if (loader != null && !loader.isReset()) {
            getLoaderManager().restartLoader(LOADER_PURPOSES, null, this);
        } else {
            getLoaderManager().initLoader(LOADER_PURPOSES, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection;
        switch (id) {
            case LOADER_PURPOSES: //group data
                return new CursorLoader(getActivity(), DatabaseProvider.PURPOSES_URI,
                        Purposes.PROJECTION, null, null, null);
            default: //child data
                if (args != null) {
                    long purposeId = args.getLong(Purposes._ID);
                    selection = Programs.PURPOSE + "=" + purposeId;
                    return new CursorLoader(getActivity(), DatabaseProvider.PROGRAMS_VIEW_URI,
                            PROJECTION_PROGRAMS, selection, null, null);
                }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        switch (id) {
            case LOADER_PURPOSES: //group data
                mAdapter.setGroupCursor(data);

                if (isResumed()) setListShown(true);
                else setListShownNoAnimation(true);
                break;
            default: //child data
                mAdapter.setChildrenCursor(id, data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_PURPOSES) {
            //CursorHelper closes group and children cursors
            mAdapter.setGroupCursor(null);
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Uri programUri = DatabaseProvider.uriParse(Programs.TABLE_NAME, id);
        mCallbacks.onProgramSelected(programUri);
        return true;
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

    public interface ProgramsCallbacks {
        public void onProgramSelected(Uri programUri);
    }
}
