package com.kozzztya.cycletraining;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorTreeAdapter;

public class MySimpleCursorTreeAdapter extends SimpleCursorTreeAdapter {

    private static final String TAG = "log" + MySimpleCursorTreeAdapter.class.getSimpleName();

    private ActionBarActivity mActivity;
    private LoaderManager.LoaderCallbacks<Cursor> mCallback;
    private SparseArray<Cursor> mChildrenCursors;

    public MySimpleCursorTreeAdapter(Context context, Cursor cursor, int collapsedGroupLayout, int expandedGroupLayout, String[] groupFrom, int[] groupTo, int childLayout, int lastChildLayout, String[] childFrom, int[] childTo, LoaderManager.LoaderCallbacks<Cursor> callback) {
        super(context, cursor, collapsedGroupLayout, expandedGroupLayout, groupFrom, groupTo, childLayout, lastChildLayout, childFrom, childTo);
        mActivity = (ActionBarActivity) context;
        mCallback = callback;
        mChildrenCursors = new SparseArray<>();
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        int groupPos = groupCursor.getPosition();
        Cursor childrenCursor = mChildrenCursors.get(groupPos);

        // Check if cursor already loaded
        if (childrenCursor == null || childrenCursor.isClosed()) {
            // Args for children data loader with group id
            Bundle args = getRelationshipArgs(groupCursor);

            LoaderManager loaderManager = mActivity.getSupportLoaderManager();
            Loader<Cursor> loader = loaderManager.getLoader(groupPos);
            if (loader != null && !loader.isReset()) {
                loaderManager.restartLoader(groupPos, args, mCallback);
            } else {
                loaderManager.initLoader(groupPos, args, mCallback);
            }
            return null;
        }
        return childrenCursor;
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        // Override and prevent children cursor deactivate.
    }

    @Override
    public void setChildrenCursor(int groupPosition, Cursor childrenCursor) {
        // If group of children is exist.
        if (getGroup(groupPosition) != null) {
            mChildrenCursors.put(groupPosition, childrenCursor);
            super.setChildrenCursor(groupPosition, childrenCursor);
        }
    }

    /**
     * Pass to loader arguments that make relationship of the group and children data
     */
    protected Bundle getRelationshipArgs(Cursor groupCursor) {
        long groupId = groupCursor.getLong(groupCursor
                .getColumnIndex(BaseColumns._ID));

        Bundle args = new Bundle();
        args.putLong(BaseColumns._ID, groupId);
        return args;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Cursor cursor = getGroup(groupPosition);
        if (cursor == null) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }

        View view;

        //Recreate view if needed to change layout for expanded/collapsed group
        if (convertView == null || convertView.getTag() != isExpanded) {
            view = newGroupView(mActivity, cursor, isExpanded, parent);
            view.setTag(isExpanded);
        } else {
            view = convertView;
        }

        bindGroupView(view, mActivity, cursor, isExpanded);
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Cursor cursor = getChild(groupPosition, childPosition);
        if (cursor == null) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }

        View view;

        //Recreate view if needed to change layout for last child
        if (convertView == null || convertView.getTag() != isLastChild) {
            view = newChildView(mActivity, cursor, isLastChild, parent);
            view.setTag(isLastChild);
        } else {
            view = convertView;
        }

        bindChildView(view, mActivity, cursor, isLastChild);
        return view;
    }

    @Override
    public void setGroupCursor(Cursor cursor) {
        mChildrenCursors.clear();
        super.setGroupCursor(cursor);
    }
}
