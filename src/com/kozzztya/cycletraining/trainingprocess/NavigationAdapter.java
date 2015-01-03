package com.kozzztya.cycletraining.trainingprocess;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.Trainings;
import com.kozzztya.cycletraining.utils.DateUtils;

import java.sql.Date;

public class NavigationAdapter extends SimpleCursorAdapter {

    private static final int DROPDOWN_TAG = 0;

    public NavigationAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View newDropDownView(Context context, Cursor cursor, ViewGroup parent) {
        View view = super.newDropDownView(context, cursor, parent);
        view.setTag(DROPDOWN_TAG);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Set done icon only in DropDownView
        if (view.getTag() == DROPDOWN_TAG) {
            TextView textView = (TextView) view;

            Date date = Date.valueOf(cursor.getString(cursor.getColumnIndex(Trainings.DATE)));
            boolean isDone = cursor.getInt(cursor.getColumnIndex(Trainings.IS_DONE)) != 0;

            switch (DateUtils.getTrainingStatus(date, isDone)) {
                case DateUtils.STATUS_DONE:
                    textView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ic_action_done, 0);
                    break;
                case DateUtils.STATUS_MISSED:
                    textView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.abc_ic_clear_mtrl_alpha, 0);
                    break;
                case DateUtils.STATUS_IN_PLANS:
                    textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    break;
            }
        }
        super.bindView(view, context, cursor);
    }
}
