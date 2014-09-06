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
        ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.set_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textViewSetN = (TextView) view.findViewById(R.id.textViewSetN);
            viewHolder.textViewReps = (TextView) view.findViewById(R.id.textViewReps);
            viewHolder.textViewWeight = (TextView) view.findViewById(R.id.textViewWeight);
            viewHolder.textViewComment = (TextView) view.findViewById(R.id.textViewComment);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Set set = getItem(position);

        viewHolder.textViewSetN.setText(String.valueOf(position + 1));
        viewHolder.textViewReps.setText(set.getReps());
        viewHolder.textViewWeight.setText(SetUtils.weightFormat(set.getWeight()));

        String comment = set.getComment();
        if (comment != null && comment.length() != 0) {
            viewHolder.textViewComment.setText(comment);
            viewHolder.textViewComment.setVisibility(View.VISIBLE);
        }
        return view;
    }

    static class ViewHolder {
        TextView textViewSetN;
        TextView textViewReps;
        TextView textViewWeight;
        TextView textViewComment;
    }
}
