package com.kozzztya.cycletraining.trainingcreate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.Training;
import com.kozzztya.cycletraining.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;

public class TrainingPlanListAdapter extends SetsTableAdapter {

    private Context mContext;
    private LinkedHashMap<Training, List<Set>> mTrainingsSets;

    public TrainingPlanListAdapter(Context context, LinkedHashMap<Training, List<Set>> trainingsSets) {
        super(context);
        mContext = context;
        mTrainingsSets = trainingsSets;
    }

    @Override
    public int getCount() {
        return mTrainingsSets.size();
    }

    @Override
    public Training getItem(int position) {
        return (Training) mTrainingsSets.keySet().toArray()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<Set> getSets(int position) {
        return mTrainingsSets.get(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.training_list_item, parent, false);
        } else {
            view = convertView;
        }

        setTrainingTitle(position, view);
        buildSetsTable(position, view);

        return view;
    }

    protected void setTrainingTitle(int position, View convertView) {
        Training training = getItem(position);

        TextView textViewTitle = (TextView) convertView.findViewById(R.id.textViewTrainingTitle);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd.MM.yyyy");
        textViewTitle.setText((position + 1) + ". " + dateFormat.format(training.getDate()));

        ImageView doneIcon = (ImageView) convertView.findViewById(R.id.imageViewDoneIcon);
        switch (DateUtils.getTrainingStatus(training.getDate(), training.isDone())) {
            case DateUtils.STATUS_DONE:
                doneIcon.setImageResource(R.drawable.ic_done_true);
                break;
            case DateUtils.STATUS_IN_PLANS:
                doneIcon.setVisibility(View.GONE);
                break;
            case DateUtils.STATUS_MISSED:
                doneIcon.setImageResource(R.drawable.ic_done_false);
                break;
        }
    }

}
