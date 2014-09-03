package com.kozzztya.cycletraining.trainingprocess;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.TrainingView;

import java.util.List;

public class SetsDataFragment extends Fragment implements OnItemClickListener {

    private static final int REQUEST_CODE_EDIT_SET = 1;
    private static final int REQUEST_CODE_ADD_SET = 2;
    private static final int REQUEST_CODE_COMMENT = 3;

    public static final String ARG_TRAINING = "training";
    public static final String ARG_SETS = "sets";

    private TrainingView training;
    private List<Set> sets;
    private ListView setsListView;
    private SetsListAdapter setsListAdapter;
    private View footerComment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        retrieveArgs();

        View view = inflater.inflate(R.layout.sets_data_fragment, container, false);
        setsListView = (ListView) view.findViewById(R.id.listViewSets);
        setsListView.setOnItemClickListener(this);

        View headerSetList = inflater.inflate(R.layout.set_list_header, null);
        footerComment = inflater.inflate(R.layout.comment_footer, null);
        setsListView.addHeaderView(headerSetList, null, false); //disable clicking

        setsListAdapter = new SetsListAdapter(getActivity(), R.layout.set_list_item, sets);
        setsListView.setAdapter(setsListAdapter);

        showTrainingComment();
        return view;
    }

    private void retrieveArgs() {
        Bundle args = getArguments();
        if (args != null) {
            training = args.getParcelable(ARG_TRAINING);
            sets = args.getParcelableArrayList(ARG_SETS);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                addSet();
                return true;
            case R.id.action_comment:
                editComment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (view == footerComment)
            //Edit footer data
            editComment();
        else
            //Subtract header position
            editSet(position - 1);
    }

    private void editSet(int position) {
        Set set = sets.get(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable(SetEditDialogFragment.ARG_SET, set);

        DialogFragment dialogFragment = new SetEditDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.setTargetFragment(this, REQUEST_CODE_EDIT_SET);
        dialogFragment.show(getFragmentManager(), SetEditDialogFragment.class.getSimpleName());
    }

    private void addSet() {
        Set set = new Set();
        set.setTraining(training.getId());

        Bundle bundle = new Bundle();
        bundle.putParcelable(SetEditDialogFragment.ARG_SET, set);

        DialogFragment dialogFragment = new SetEditDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.setTargetFragment(this, REQUEST_CODE_ADD_SET);
        dialogFragment.show(getFragmentManager(),
                SetEditDialogFragment.class.getSimpleName());
    }

    private void editComment() {
        Bundle bundle = new Bundle();
        bundle.putString(CommentDialogFragment.ARG_COMMENT, training.getComment());

        DialogFragment dialogFragment = new CommentDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.setTargetFragment(this, REQUEST_CODE_COMMENT);
        dialogFragment.show(getFragmentManager(),
                CommentDialogFragment.class.getSimpleName());
    }

    private void showTrainingComment() {
        String comment = training.getComment();
        if (comment != null && comment.length() != 0) {
            if (setsListView.getFooterViewsCount() == 0)
                setsListView.addFooterView(footerComment);

            TextView textViewComment = (TextView) footerComment.findViewById(R.id.textViewComment);
            textViewComment.setText(comment);
        } else {
            setsListView.removeFooterView(footerComment);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ADD_SET:
                    Set set = data.getParcelableExtra(SetEditDialogFragment.ARG_SET);
                    setsListAdapter.add(set);
                case REQUEST_CODE_EDIT_SET:
                    setsListAdapter.notifyDataSetChanged();
                    break;
                case REQUEST_CODE_COMMENT:
                    training.setComment(data.getStringExtra(CommentDialogFragment.ARG_COMMENT));
                    showTrainingComment();
                    break;
            }
        }
    }

}