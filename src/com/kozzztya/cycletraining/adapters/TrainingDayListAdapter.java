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
import com.kozzztya.cycletraining.db.entities.Training;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.utils.DateUtils;
import com.kozzztya.cycletraining.utils.RMUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TrainingDayListAdapter extends SetsTableAdapter {

    private Context context;
    private LinkedHashMap<TrainingView, List<Set>> trainingsSets;

    public TrainingDayListAdapter(Context context, LinkedHashMap<TrainingView, List<Set>> trainingsSets) {
        super(context);
        this.context = context;
        this.trainingsSets = trainingsSets;
    }

    @Override
    public int getCount() {
        return trainingsSets.size();
    }

    @Override
    public TrainingView getItem(int position) {
        return (TrainingView) trainingsSets.keySet().toArray()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<Set> getSets(int position) {
        return trainingsSets.get(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.training_list_item, parent, false);

        setTrainingTitle(position, convertView);
        buildSetsTable(position, convertView);

        return convertView;
    }

    protected void setTrainingTitle(int position, View convertView) {
        TrainingView training = getItem(position);
        TextView textViewTraining = (TextView) convertView.findViewById(R.id.textViewTrainingTitle);
        textViewTraining.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        textViewTraining.setText(training.getExercise());
    }

}
