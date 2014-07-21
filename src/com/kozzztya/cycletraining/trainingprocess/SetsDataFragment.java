package com.kozzztya.cycletraining.trainingprocess;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.adapters.SetsListAdapter;
import com.kozzztya.cycletraining.db.OnDBChangeListener;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.TrainingView;

import java.util.List;

public class SetsDataFragment extends Fragment implements OnItemClickListener, OnDBChangeListener {

    private TrainingView training;
    private List<Set> sets;
    private SetsListAdapter adapter;

    public SetsDataFragment(TrainingView training, List<Set> sets) {
        this.training = training;
        this.sets = sets;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sets_data_fragment, container, false);
        ListView listView = (ListView) view.findViewById(R.id.listViewSets);
        listView.setOnItemClickListener(this);

        adapter = new SetsListAdapter(getActivity(), R.layout.set_list_item, sets);
        listView.setAdapter(adapter);

        TextView textViewComment = (TextView) view.findViewById(R.id.textViewComment);
        textViewComment.setText(training.getComment());

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Set set = sets.get(position);

        SetEditDialogFragment editNameDialog = new SetEditDialogFragment(set);
        editNameDialog.setOnDBChangeListener(this);
        editNameDialog.show(getFragmentManager(), "set_edit_fragment");
    }

    @Override
    public void onDBChange() {
        adapter.notifyDataSetChanged();
    }
}