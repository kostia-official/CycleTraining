package com.kozzztya.cycletraining.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.utils.DateUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

public class TrainingWeekExpListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private LinkedHashMap<String, List<TrainingView>> groups;
    private ArrayList<List<TrainingView>> childs;

    public TrainingWeekExpListAdapter(Context context, LinkedHashMap<String, List<TrainingView>> groups) {
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
    public String getGroup(int pos) {
        return (String) groups.keySet().toArray()[pos];
    }

    @Override
    public TrainingView getChild(int groupPos, int childPos) {
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

    public boolean isGroupDone(int pos) {
        List<TrainingView> trainings = childs.get(pos);
        for (TrainingView t : trainings) {
            if (!t.isDone()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public View getGroupView(final int pos, final boolean isExpanded, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.day_exp_list_item, null);

        List<TrainingView> trainings = childs.get(pos);

        //If training is today
        if (DateUtils.isSameDay(trainings.get(0).getDate().getTime(), Calendar.getInstance().getTimeInMillis()))
            view.setBackgroundColor(context.getResources().getColor(R.color.selected_background));

        TextView title = (TextView) view.findViewById(R.id.textViewGroupDayOfWeek);

        String dayOfWeek = getGroup(pos);
        title.setText(dayOfWeek);

        ImageView done = (ImageView) view.findViewById(R.id.imageViewGroupDone);

        setDoneIcon(isGroupDone(pos), trainings.get(0).getDate(), done);

        final ExpandableListView expList = (ExpandableListView) viewGroup;
        expList.setItemChecked(pos, true);
        expList.setSelectedGroup(pos);

        ImageView imageButtonIndicator = (ImageView) view.findViewById(R.id.imageButtonIndicator);
        imageButtonIndicator.setFocusable(false);
        imageButtonIndicator.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) expList.collapseGroup(pos);
                else expList.expandGroup(pos);
            }
        });
        if (isExpanded) imageButtonIndicator.setImageResource(R.drawable.ic_expanded);
        else imageButtonIndicator.setImageResource(R.drawable.ic_collapsed);

        return view;
    }

    @Override
    public View getChildView(int groupPos, int childPos, boolean isExpanded, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.training_exp_list_item, null);

        TrainingView training = childs.get(groupPos).get(childPos);
        TextView title = (TextView) view.findViewById(R.id.textViewTrainingExercise);
        title.setText(training.getExercise());

        ImageView done = (ImageView) view.findViewById(R.id.imageViewTrainingDone);
        setDoneIcon(training.isDone(), training.getDate(), done);
        return view;
    }

    public void setDoneIcon(boolean isDone, Date date, ImageView done) {
        switch (DateUtils.getTrainingStatus(date, isDone)) {
            case DateUtils.STATUS_DONE:
                done.setImageResource(R.drawable.ic_done_true);
                break;
            case DateUtils.STATUS_IN_PLANS:
                done.setVisibility(View.INVISIBLE);
                break;
            case DateUtils.STATUS_MISSED:
                done.setImageResource(R.drawable.ic_done_false);
                break;
        }
    }

}
