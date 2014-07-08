package com.kozzztya.cycletraining.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.Training;
import com.kozzztya.cycletraining.utils.RMUtils;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;

public class MesocycleListAdapter extends SetsTableAdapter {

    private Context context;
    private LinkedHashMap<Training, List<Set>> trainingsSets;

    public MesocycleListAdapter(Context context, LinkedHashMap<Training, List<Set>> trainingsSets) {
        super(context);
        this.context = context;
        this.trainingsSets = trainingsSets;
    }

    @Override
    public int getCount() {
        return trainingsSets.size();
    }

    @Override
    public Training getItem(int position) {
        return (Training) trainingsSets.keySet().toArray()[position];
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
        Training training = getItem(position);
        TextView textViewTraining = (TextView) convertView.findViewById(R.id.textViewTrainingTitle);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd.MM.yyyy");
        textViewTraining.setText((position + 1) + ". " + dateFormat.format(training.getDate()));
    }

}
