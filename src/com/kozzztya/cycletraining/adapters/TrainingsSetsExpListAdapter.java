package com.kozzztya.cycletraining.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.utils.DateUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TrainingsSetsExpListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private LinkedHashMap<TrainingView, List<Set>> groups;
    private ArrayList<List<Set>> childs;

    public TrainingsSetsExpListAdapter(Context context, LinkedHashMap<TrainingView, List<Set>> groups) {
        this.context = context;
        this.groups = groups;
        this.childs = new ArrayList<>(groups.values());
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int pos) {
        return childs.get(pos).size();
    }

    @Override
    public TrainingView getGroup(int pos) {
        return (TrainingView) groups.keySet().toArray()[pos];
    }

    @Override
    public Set getChild(int groupPos, int childPos) {
        return childs.get(groupPos).get(childPos);
    }

    @Override
    public long getGroupId(int pos) {
        return pos;
    }

    @Override
    public long getChildId(int groupPos, int childPos) {
        return childPos;
    }

    @Override
    public boolean isChildSelectable(int groupPos, int childPos) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int pos, boolean isExpanded, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.training_exp_list_item, null);

        //Разворачиваем все списки
        ExpandableListView mExpandableListView = (ExpandableListView) viewGroup;
        mExpandableListView.expandGroup(pos);

        TrainingView training = (TrainingView) getGroup(pos);
        TextView title = (TextView) view.findViewById(R.id.textViewTrainingExercise);
        title.setText(training.getExercise());
        title.setTextSize(18);

        ImageView done = (ImageView) view.findViewById(R.id.imageViewTrainingDone);

        switch (DateUtils.trainingStatus(training.getDate(), training.isDone())) {
            case DateUtils.STATUS_DONE:
                done.setImageResource(R.drawable.ic_done_true);
                break;
            case DateUtils.STATUS_IN_PLANS:
                done.setVisibility(View.INVISIBLE);
                break;
            case DateUtils.STATUS_NOT_DONE:
                done.setImageResource(R.drawable.ic_done_false);
                break;
        }
        return view;
    }

    @Override
    public View getChildView(int groupPos, int childPos, boolean isExpanded, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.set_exp_list_item, null);

        Set set = childs.get(groupPos).get(childPos);
        TextView textViewSetN = (TextView) view.findViewById(R.id.textViewSetN);
        textViewSetN.setText(String.valueOf(set.getId()));
        TextView textViewReps = (TextView) view.findViewById(R.id.textViewReps);
        textViewReps.setText(String.valueOf(set.getReps()));
        TextView textViewWeight = (TextView) view.findViewById(R.id.textViewWeight);
        textViewWeight.setText(" " + (int)set.getWeight() + context.getResources().getString(R.string.kg));
        return view;
    }


}
