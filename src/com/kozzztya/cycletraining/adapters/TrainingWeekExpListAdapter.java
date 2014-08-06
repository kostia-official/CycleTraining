package com.kozzztya.cycletraining.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.utils.DateUtils;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class TrainingWeekExpListAdapter extends MyExpListAdapter<String, TrainingView> {

    private Context context;

    public TrainingWeekExpListAdapter(Context context, LinkedHashMap<String, List<TrainingView>> groups) {
        super(groups);
        this.context = context;
    }

    @Override
    public View getGroupView(final int pos, final boolean isExpanded, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.day_exp_list_item, null);

        List<TrainingView> trainings = getChildrenOfGroup(pos);

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

        TrainingView training = getChild(groupPos, childPos);
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

    public boolean isGroupDone(int pos) {
        List<TrainingView> trainings = getChildrenOfGroup(pos);
        for (TrainingView t : trainings) {
            if (!t.isDone()) {
                return false;
            }
        }
        return true;
    }

}
