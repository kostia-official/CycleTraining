package com.kozzztya.cycletraining.trainingjournal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kozzztya.cycletraining.MySimpleCursorTreeAdapter;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.Trainings;
import com.kozzztya.cycletraining.utils.DateUtils;
import com.kozzztya.cycletraining.utils.ViewUtils;

import java.sql.Date;

public class TrainingWeekTreeAdapter extends MySimpleCursorTreeAdapter {

    private static final String TAG = "log" + TrainingWeekTreeAdapter.class.getSimpleName();
    private ViewGroup mParent;

    public TrainingWeekTreeAdapter(Context context, Cursor cursor, int collapsedGroupLayout,
                                   int expandedGroupLayout, int childLayout, int lastChildLayout,
                                   LoaderManager.LoaderCallbacks<Cursor> callback) {
        super(context, cursor, collapsedGroupLayout, expandedGroupLayout, new String[]{}, new int[]{},
                childLayout, lastChildLayout, new String[]{}, new int[]{}, callback);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        mParent = parent; // Save reference to parent
        return super.getGroupView(groupPosition, isExpanded, convertView, parent);
    }

    /**
     * Children will be select by group date
     */
    @Override
    protected Bundle getRelationshipArgs(Cursor groupCursor) {
        Date date = DateUtils.safeParse(groupCursor.getString(
                groupCursor.getColumnIndex(Trainings.DATE)));

        Bundle args = new Bundle();
        args.putLong(Trainings.DATE, date.getTime());
        return args;
    }

    /**
     * Show day of week name and done/not done icon
     * Custom binding. Can't use ViewBinder
     */
    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
        int position = cursor.getPosition();
        Date weekDay = DateUtils.safeParse(cursor.getString(
                cursor.getColumnIndex(Trainings.DATE)));

        TextView title = (TextView) view.findViewById(R.id.title);
        ImageView indicator = (ImageView) view.findViewById(R.id.indicator);

        title.setText(DateUtils.getDayOfWeekName(weekDay, context));
        ViewUtils.setDoneDrawable(title, isGroupDone(position), weekDay);
        setIndicator(isExpanded, position, indicator);
    }

    /**
     * Show exercise name and done/not done icon
     * Custom binding. Can't use ViewBinder
     */
    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
        ContentValues trainingValues = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, trainingValues);

        String exercise = trainingValues.getAsString(Trainings.EXERCISE);
        Date date = Date.valueOf(trainingValues.getAsString(Trainings.DATE));
        boolean isDone = trainingValues.getAsInteger(Trainings.IS_DONE) != 0;

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(exercise);
        ViewUtils.setDoneDrawable(title, isDone, date);
    }

    /**
     * ExpandableListView can expand/collapse only on indicator click.
     * Group list item may have their own click events
     */
    private void setIndicator(final boolean isExpanded, final int groupPosition, ImageView indicator) {
        final ExpandableListView expListView = (ExpandableListView) mParent;
        indicator.setFocusable(false);
        indicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) expListView.collapseGroup(groupPosition);
                else expListView.expandGroup(groupPosition);
            }
        });
        indicator.setImageResource(isExpanded ? R.drawable.ic_expanded : R.drawable.ic_collapsed);
    }

    public boolean isGroupDone(int groupPosition) {
        Cursor cursor = getGroup(groupPosition);
        return cursor.getInt(cursor.getColumnIndex(
                TrainingWeekFragment.COLUMN_IS_DAY_DONE)) != 0;
    }
}
