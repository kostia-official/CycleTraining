package com.kozzztya.cycletraining.trainingjournal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kozzztya.customview.CardView;
import com.kozzztya.cycletraining.MyExpListAdapter;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.utils.DateUtils;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class TrainingJournalAdapter extends MyExpListAdapter<String, TrainingView> {

    private Context mContext;

    public TrainingJournalAdapter(Context context, LinkedHashMap<String, List<TrainingView>> groups) {
        super(groups);
        mContext = context;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(R.layout.weekday_exp_list_item, parent, false);

        CardView cardView = (CardView) convertView.findViewById(R.id.card);
        imitateCardGroup(isExpanded, cardView);

        List<TrainingView> trainings = getChildrenOfGroup(groupPosition);

        TextView title = (TextView) convertView.findViewById(R.id.textViewGroupDayOfWeek);
        String dayOfWeek = getGroup(groupPosition);
        title.setText(dayOfWeek);

        ImageView done = (ImageView) convertView.findViewById(R.id.imageViewGroupDone);
        setDoneIcon(isGroupDone(groupPosition), trainings.get(0).getDate(), done);

        final ExpandableListView expList = (ExpandableListView) parent;
        expList.setItemChecked(groupPosition, true);
        expList.setSelectedGroup(groupPosition);

        ImageView indicator = (ImageView) convertView.findViewById(R.id.imageButtonIndicator);
        indicator.setFocusable(false);
        indicator.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) expList.collapseGroup(groupPosition);
                else expList.expandGroup(groupPosition);
            }
        });
        indicator.setImageResource(isExpanded ? R.drawable.ic_expanded : R.drawable.ic_collapsed);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(R.layout.training_exp_list_item, parent, false);

        CardView cardView = (CardView) convertView.findViewById(R.id.card);
        imitateCardChild(isLastChild, cardView);

        TrainingView training = getChild(groupPosition, childPosition);
        TextView title = (TextView) convertView.findViewById(R.id.textViewTrainingExercise);
        title.setText(training.getExercise());

        ImageView done = (ImageView) convertView.findViewById(R.id.imageViewTrainingDone);
        setDoneIcon(training.isDone(), training.getDate(), done);

        return convertView;
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
