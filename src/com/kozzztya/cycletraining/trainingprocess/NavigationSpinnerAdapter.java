package com.kozzztya.cycletraining.trainingprocess;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.utils.DateUtils;

import java.util.List;

public class NavigationSpinnerAdapter extends ArrayAdapter<TrainingView> {

    private int mResource;
    private int mDropDownResource;

    public NavigationSpinnerAdapter(Context context, int resource, int dropDownResource, List<TrainingView> objects) {
        super(context, resource, objects);
        mResource = resource;
        mDropDownResource = dropDownResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            textView = (TextView) inflater.inflate(mResource, parent, false);
        } else {
            textView = (TextView) convertView;
        }

        TrainingView training = getItem(position);
        textView.setText(training.getExercise());

        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            textView = (TextView) inflater.inflate(mDropDownResource, parent, false);
        } else {
            textView = (TextView) convertView;
        }

        TrainingView training = getItem(position);
        textView.setText(training.getExercise());

        switch (DateUtils.getTrainingStatus(training.getDate(), training.isDone())) {
            case DateUtils.STATUS_DONE:
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.abc_ic_cab_done_holo_dark, 0);
                break;
            case DateUtils.STATUS_MISSED:
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_cancel_dark_theme, 0);
                break;
            case DateUtils.STATUS_IN_PLANS:
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                break;
        }

        return textView;
    }
}
