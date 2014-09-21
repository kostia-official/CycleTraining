package com.kozzztya.cycletraining.trainingprocess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.TextView;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.Sets;
import com.kozzztya.cycletraining.utils.SetUtils;

public class TrainingSetsAdapter extends SimpleCursorAdapter {

    public TrainingSetsAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, new String[]{}, new int[]{}, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewSetN = (TextView) view.findViewById(R.id.number);
        TextView textViewReps = (TextView) view.findViewById(R.id.reps);
        TextView textViewWeight = (TextView) view.findViewById(R.id.weight);

        ContentValues setValues = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, setValues);

        textViewSetN.setText(String.valueOf(cursor.getPosition() + 1));
        textViewReps.setText(setValues.getAsString(Sets.REPS));
        textViewWeight.setText(SetUtils.weightFormat(setValues.getAsFloat(Sets.WEIGHT)));

        String comment = setValues.getAsString(Sets.COMMENT);
        if (comment != null && comment.length() != 0) {
            TextView textViewComment = (TextView) view.findViewById(R.id.comment);
            textViewComment.setText(comment);
            textViewComment.setVisibility(View.VISIBLE);
        }
    }
}
