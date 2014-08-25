package com.kozzztya.cycletraining.trainingprocess;

import android.app.Activity;
import android.content.Intent;
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
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.TrainingView;

import java.util.List;

public class SetsDataFragment extends Fragment implements OnItemClickListener {

    private static final int REQUEST_CODE_SET_EDIT = 1;

    private TrainingView training;
    private List<Set> sets;
    private SetsListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        retrieveArgs();

        View view = inflater.inflate(R.layout.sets_data_fragment, container, false);
        ListView listView = (ListView) view.findViewById(R.id.listViewSets);
        listView.setOnItemClickListener(this);

        adapter = new SetsListAdapter(getActivity(), R.layout.set_list_item, sets);
        listView.setAdapter(adapter);

        String comment = training.getComment();
        if (comment != null && comment.length() != 0) {
            view.findViewById(R.id.card_comment).setVisibility(View.VISIBLE);
            TextView textViewComment = (TextView) view.findViewById(R.id.textViewComment);
            textViewComment.setText(training.getComment());
        }
        return view;
    }

    private void retrieveArgs() {
        Bundle args = getArguments();
        if (args != null) {
            training = args.getParcelable("training");
            sets = args.getParcelableArrayList("sets");
        }
    }

    //Edit data of clicked set
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Set set = sets.get(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable("set", set);

        SetEditDialogFragment setEditDialogFragment = new SetEditDialogFragment();
        setEditDialogFragment.setArguments(bundle);
        setEditDialogFragment.setTargetFragment(this, REQUEST_CODE_SET_EDIT);
        setEditDialogFragment.show(getFragmentManager(), SetEditDialogFragment.class.getSimpleName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SET_EDIT
                && resultCode == Activity.RESULT_OK) {
            adapter.notifyDataSetChanged();
        }
    }

}