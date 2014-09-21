package com.kozzztya.cycletraining.trainingcreate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v4.app.LoaderManager;
import android.view.View;
import android.widget.TextView;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.SubCursorAdapter;
import com.kozzztya.cycletraining.custom.SetsTableView;
import com.kozzztya.cycletraining.db.Trainings;
import com.kozzztya.cycletraining.utils.ViewUtils;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class TrainingPlanAdapter extends SubCursorAdapter {

    private static final String TAG = "log" + TrainingPlanAdapter.class.getSimpleName();

    public TrainingPlanAdapter(Context context, int layout, Cursor c, int flags,
                               LoaderManager.LoaderCallbacks<Cursor> callback) {
        super(context, layout, c, new String[]{}, new int[]{}, flags, callback);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ContentValues trainingValues = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, trainingValues);

        Date date = Date.valueOf(trainingValues.getAsString(Trainings.DATE));
        boolean isDone = trainingValues.getAsInteger(Trainings.IS_DONE) != 0;
        int position = cursor.getPosition();

        TextView title = (TextView) view.findViewById(R.id.title);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd.MM.yyyy");
        title.setText((position + 1) + ". " + dateFormat.format(date));

        ViewUtils.setDoneDrawable(title, isDone, date);
    }

    @Override
    protected void bindSubView(View convertView, Context context, Cursor subCursor) {
        SetsTableView setsTableView = (SetsTableView) convertView.findViewById(R.id.sets_table);
        setsTableView.bindView(subCursor);
    }
}
