package com.kozzztya.cycletraining.custom;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.Sets;
import com.kozzztya.cycletraining.utils.SetUtils;

public class SetsTableView extends TableLayout {

    public SetsTableView(Context context) {
        super(context);
    }

    public SetsTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void bindView(Cursor cursor) {
        // Reinflate layout
        removeAllViewsInLayout();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.sets_table, this);

        TableRow tableRowWeight = (TableRow) view.findViewById(R.id.tableRowWeight);
        TableRow tableRowReps = (TableRow) view.findViewById(R.id.tableRowReps);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                TextView textViewWeight = new TextView(getContext());
                TextView textViewReps = new TextView(getContext());

                textViewWeight.setBackgroundResource(R.drawable.cell_right_border);
                textViewReps.setBackgroundResource(R.drawable.cell_right_top_border);

                textViewWeight.setPadding(4, 0, 5, 0);
                textViewReps.setPadding(4, 0, 5, 0);

                textViewWeight.setGravity(Gravity.CENTER_HORIZONTAL);
                textViewReps.setGravity(Gravity.CENTER_HORIZONTAL);

                tableRowWeight.addView(textViewWeight);
                tableRowReps.addView(textViewReps);

                //Bind data
                float weight = cursor.getFloat(cursor.getColumnIndex(Sets.WEIGHT));
                String reps = cursor.getString(cursor.getColumnIndex(Sets.REPS));

                textViewWeight.setText(SetUtils.weightFormat(weight));
                textViewReps.setText(reps);
            } while (cursor.moveToNext());
        }
    }
}
