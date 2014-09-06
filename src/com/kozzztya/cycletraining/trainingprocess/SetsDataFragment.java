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

import java.util.ArrayList;

public class SetsDataFragment extends Fragment implements OnItemClickListener {

    private static final int REQUEST_CODE_EDIT_SET = 0;
    private static final int REQUEST_CODE_ADD_SET = 1;
    private static final int REQUEST_CODE_COMMENT = 2;

    public static final String ARG_TRAINING = "training";
    public static final String ARG_SETS = "sets";

    private TrainingView mTraining;
    private ArrayList<Set> mSets;

    private ListView mSetsListView;
    private SetsListAdapter mSetsListAdapter;
    private View mFooterComment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            retrieveData(savedInstanceState);
        } else {
            retrieveData(getArguments());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sets_data_fragment, container, false);
        mSetsListView = (ListView) view.findViewById(R.id.listViewSets);
        mSetsListView.setOnItemClickListener(this);

        View headerSetList = inflater.inflate(R.layout.set_list_header, null);
        mFooterComment = inflater.inflate(R.layout.comment_footer, null);
        mSetsListView.addHeaderView(headerSetList, null, false); //with disabled clicking

        mSetsListAdapter = new SetsListAdapter(getActivity(), R.layout.set_list_item, mSets);
        mSetsListView.setAdapter(mSetsListAdapter);

        showTrainingComment();
        return view;
    }

    private void retrieveData(Bundle bundle) {
        if (bundle != null) {
            mTraining = bundle.getParcelable(ARG_TRAINING);
            mSets = bundle.getParcelableArrayList(ARG_SETS);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(ARG_TRAINING, mTraining);
        outState.putParcelableArrayList(ARG_SETS, mSets);
        super.onSaveInstanceState(outState);
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
        if (view == mFooterComment)
            //Edit footer data
            editComment();
        else
            //Subtract header position
            editSet(position - 1);
    }

    private void editSet(int position) {
        Set set = mSets.get(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable(SetEditDialogFragment.KEY_SET, set);

        DialogFragment dialogFragment = new SetEditDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.setTargetFragment(this, REQUEST_CODE_EDIT_SET);
        dialogFragment.show(getFragmentManager(), SetEditDialogFragment.class.getSimpleName());
    }

    private void addSet() {
        Set set = new Set();
        set.setTraining(mTraining.getId());

        Bundle bundle = new Bundle();
        bundle.putParcelable(SetEditDialogFragment.KEY_SET, set);

        DialogFragment dialogFragment = new SetEditDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.setTargetFragment(this, REQUEST_CODE_ADD_SET);
        dialogFragment.show(getFragmentManager(),
                SetEditDialogFragment.class.getSimpleName());
    }

    private void editComment() {
        Bundle bundle = new Bundle();
        bundle.putString(CommentDialogFragment.KEY_COMMENT, mTraining.getComment());

        DialogFragment dialogFragment = new CommentDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.setTargetFragment(this, REQUEST_CODE_COMMENT);
        dialogFragment.show(getFragmentManager(),
                CommentDialogFragment.class.getSimpleName());
    }

    private void showTrainingComment() {
        String comment = mTraining.getComment();
        if (comment != null && comment.length() != 0) {
            if (mSetsListView.getFooterViewsCount() == 0)
                mSetsListView.addFooterView(mFooterComment);

            TextView textViewComment = (TextView) mFooterComment.findViewById(R.id.textViewComment);
            textViewComment.setText(comment);
        } else {
            mSetsListView.removeFooterView(mFooterComment);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ADD_SET:
                    Set set = data.getParcelableExtra(SetEditDialogFragment.KEY_SET);
                    mSetsListAdapter.add(set);
                case REQUEST_CODE_EDIT_SET:
                    mSetsListAdapter.notifyDataSetChanged();
                    break;
                case REQUEST_CODE_COMMENT:
                    mTraining.setComment(data.getStringExtra(CommentDialogFragment.KEY_COMMENT));
                    showTrainingComment();
                    break;
            }
        }
    }

}