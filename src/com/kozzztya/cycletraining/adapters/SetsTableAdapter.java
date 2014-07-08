package com.kozzztya.cycletraining.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.Training;
import com.kozzztya.cycletraining.utils.RMUtils;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class SetsTableAdapter extends BaseAdapter {

    protected Context context;

    protected SetsTableAdapter(Context context) {
        this.context = context;
    }

    public abstract List<Set> getSets(int position);

    protected void buildSetsTable(int position, View convertView) {
        List<Set> sets = getSets(position);

        TableRow tableRowWeight = (TableRow) convertView.findViewById(R.id.tableRowWeight);
        TableRow tableRowReps = (TableRow) convertView.findViewById(R.id.tableRowReps);

        for (Set s : sets) {
            TextView textViewWeight = new TextView(context);
            TextView textViewReps = new TextView(context);

            textViewWeight.setText(RMUtils.weightFormat(s.getWeight()));
            textViewReps.setText(RMUtils.repsFormat(s.getReps(), context));

            textViewWeight.setBackgroundResource(R.drawable.cell_right_border);
            textViewReps.setBackgroundResource(R.drawable.cell_right_top_border);

            textViewWeight.setPadding(4, 0, 5, 0);
            textViewReps.setPadding(4, 0, 5, 0);

            textViewWeight.setGravity(Gravity.CENTER_HORIZONTAL);
            textViewReps.setGravity(Gravity.CENTER_HORIZONTAL);

            tableRowWeight.addView(textViewWeight);
            tableRowReps.addView(textViewReps);
        }
    }

}
