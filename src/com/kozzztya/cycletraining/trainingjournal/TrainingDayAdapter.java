package com.kozzztya.cycletraining.trainingjournal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v4.app.LoaderManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.SubCursorAdapter;
import com.kozzztya.cycletraining.custom.MyHorizontalScrollView;
import com.kozzztya.cycletraining.custom.SetsTableView;
import com.kozzztya.cycletraining.db.Trainings;
import com.kozzztya.cycletraining.utils.ViewUtils;

import java.sql.Date;

import static com.kozzztya.cycletraining.custom.MyHorizontalScrollView.OnScrollViewClickListener;

public class TrainingDayAdapter extends SubCursorAdapter implements OnScrollViewClickListener {

    private static final String TAG = "log" + TrainingDayAdapter.class.getSimpleName();
    private ViewGroup mParent;

    public TrainingDayAdapter(Context context, int layout, Cursor c, int flags,
                              LoaderManager.LoaderCallbacks<Cursor> callback) {
        super(context, layout, c, new String[]{}, new int[]{}, flags, callback);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mParent = parent; // Save reference to parent
        return super.getView(position, convertView, parent);
    }

    /**
     * Show exercise name and done/not done icon
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ContentValues trainingValues = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, trainingValues);

        String exercise = trainingValues.getAsString(Trainings.EXERCISE);
        Date date = Date.valueOf(trainingValues.getAsString(Trainings.DATE));
        boolean isDone = trainingValues.getAsInteger(Trainings.IS_DONE) != 0;
        String comment = trainingValues.getAsString(Trainings.COMMENT);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(exercise);
        ViewUtils.setDoneDrawable(title, isDone, date);

        if (!TextUtils.isEmpty(comment)) {
            TextView textViewComment = (TextView) view.findViewById(R.id.comment);
            textViewComment.setText(comment);
            textViewComment.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Show scrollable table of sets with weight and reps rows
     */
    @Override
    protected void bindSubView(View convertView, Context context, Cursor subCursor) {
        SetsTableView setsTableView = (SetsTableView) convertView.findViewById(R.id.sets_table);
        setsTableView.bindView(subCursor);

        // Handle on ScrollView clicks (by default it can't be clicked)
        if (subCursor != null) {
            MyHorizontalScrollView scrollView = (MyHorizontalScrollView) convertView
                    .findViewById(R.id.horizontal_scroll_view);
            scrollView.setOnScrollViewClickListener(this, getCursor().getPosition());
        }
    }

    /**
     * Pass click from ScrollView to list item
     */
    @Override
    public void onScrollViewClick(View view, int position) {
        ListView listView = (ListView) mParent;
        listView.performItemClick(view, position, getItemId(position));
    }

    /**
     * Pass long click from ScrollView to list item
     */
    @Override
    public void onScrollViewLongClick(View view, int position) {
        ListView listView = (ListView) mParent;
        listView.getOnItemLongClickListener().onItemLongClick(null, view,
                position, getItemId(position));
    }
}
