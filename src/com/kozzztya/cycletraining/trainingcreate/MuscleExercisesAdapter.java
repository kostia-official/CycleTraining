package com.kozzztya.cycletraining.trainingcreate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kozzztya.cycletraining.MyExpListAdapter;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.entities.Exercise;
import com.kozzztya.cycletraining.db.entities.Muscle;

import java.util.List;
import java.util.Map;

public class MuscleExercisesAdapter extends MyExpListAdapter<Muscle, Exercise> {

    private Context mContext;

    public MuscleExercisesAdapter(Context context, Map<Muscle, List<Exercise>> groups) {
        super(groups);
        mContext = context;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.exp_list_item, null);

        TextView textView = (TextView) convertView.findViewById(R.id.title);
        Muscle muscle = getGroup(groupPosition);
        textView.setText(muscle.getName());

        return super.getGroupView(groupPosition, isExpanded, convertView, parent);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_item, null);

        Exercise exercise = getChild(groupPosition, childPosition);
        TextView textView = (TextView) convertView.findViewById(R.id.title);
        textView.setText(exercise.getName());

        return super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
    }
}
