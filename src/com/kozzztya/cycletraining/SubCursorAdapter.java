package com.kozzztya.cycletraining;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * ListView adapter that can load sub cursors for list items
 */
public abstract class SubCursorAdapter extends SimpleCursorAdapter {

    private static final String TAG = "log" + SubCursorAdapter.class.getSimpleName();

    private ActionBarActivity mActivity;
    private LoaderManager.LoaderCallbacks<Cursor> mCallback;

    private SparseArray<Cursor> mSubCursors;

    public SubCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to,
                            int flags, LoaderManager.LoaderCallbacks<Cursor> callback) {
        super(context, layout, c, from, to, flags);
        mActivity = (ActionBarActivity) context;
        mCallback = callback;
        mSubCursors = new SparseArray<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        View view;
        if (convertView == null) {
            view = newView(mContext, mCursor, parent);
        } else {
            view = convertView;
        }
        bindView(view, mContext, mCursor);

        Cursor subCursor = getSubCursor(mCursor);
        bindSubView(view, mContext, subCursor);
        return view;
    }

    /**
     * Bind sub cursor data to sub view
     */
    protected abstract void bindSubView(View convertView, Context context, Cursor subCursor);

    /**
     * Load cursor for sub data of return an existing
     *
     * @param mainCursor Cursor with the main data
     * @return Cursor with the sub data
     */
    protected Cursor getSubCursor(Cursor mainCursor) {
        int position = mainCursor.getPosition();

        // Check if cursor already loaded
        if (mSubCursors.indexOfKey(position) < 0) {
            // Args for sub data loader
            Bundle args = getRelationshipArgs(mainCursor);

            LoaderManager loaderManager = mActivity.getSupportLoaderManager();
            Loader<Cursor> loader = loaderManager.getLoader(position);
            if (loader != null && !loader.isReset()) {
                loaderManager.restartLoader(position, args, mCallback);
            } else {
                loaderManager.initLoader(position, args, mCallback);
            }
            return null;
        }
        return mSubCursors.get(position);
    }

    /**
     * Pass to loader arguments that make relationship of the main and sub data
     */
    protected Bundle getRelationshipArgs(Cursor mainCursor) {
        long id = mainCursor.getLong(mainCursor.getColumnIndex(BaseColumns._ID));

        Bundle args = new Bundle();
        args.putLong(BaseColumns._ID, id);
        return args;
    }

    /**
     * Set cursor with sub data
     *
     * @param position  The list item whose sub data are being set via this cursor
     * @param newCursor New sub cursor
     */
    public void setSubCursor(int position, Cursor newCursor) {
        if (mSubCursors.get(position) != newCursor) {
            mSubCursors.put(position, newCursor);
            notifyDataSetChanged();
        }
    }

    /**
     * If we have a new main cursor remove old sub cursors
     *
     * @param cursor New main cursor
     */
    @Override
    public Cursor swapCursor(Cursor cursor) {
        if (cursor != mCursor) {
            mSubCursors.clear();
        }
        return super.swapCursor(cursor);
    }
}
