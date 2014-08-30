package com.kozzztya.cycletraining.trainingprocess;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.utils.SetUtils;

import java.util.List;

public class SetsListAdapter extends ArrayAdapter<Set> {

    public SetsListAdapter(Context context, int resource, List<Set> sets) {
        super(context, resource, sets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.set_list_item, parent, false);
        } else {
            view = convertView;
        }

        Set set = getItem(position);

        TextView textViewSetN = (TextView) view.findViewById(R.id.textViewSetN);
        TextView textViewReps = (TextView) view.findViewById(R.id.textViewReps);
        TextView textViewWeight = (TextView) view.findViewById(R.id.textViewWeight);

        textViewSetN.setText(String.valueOf(position + 1));
        textViewReps.setText(set.getReps());
        textViewWeight.setText(SetUtils.weightFormat(set.getWeight()));

        String comment = set.getComment();
        if (comment != null && comment.length() != 0) {
            TextView textViewComment = (TextView) view.findViewById(R.id.textViewComment);
            textViewComment.setText(comment);
            textViewComment.setVisibility(View.VISIBLE);
        }

        return view;
    }

}