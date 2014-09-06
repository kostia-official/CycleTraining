package com.kozzztya.cycletraining.trainingprocess;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.utils.SetUtils;

import static android.content.DialogInterface.OnClickListener;

public class SetEditDialogFragment extends DialogFragment {

    public static final String KEY_SET = "set";

    private EditText mEditTextReps;
    private EditText mEditTextWeight;
    private EditText mEditTextComment;

    //Set for editing
    private Set mSet;

    public SetEditDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            retrieveData(savedInstanceState);
        } else {
            retrieveData(getArguments());
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.set_edit_fragment, null);

        mEditTextReps = (EditText) view.findViewById(R.id.editTextReps);
        mEditTextWeight = (EditText) view.findViewById(R.id.editTextWeight);
        mEditTextComment = (EditText) view.findViewById(R.id.editTextComment);

        mEditTextReps.setText(mSet.getReps());
        mEditTextWeight.setText(SetUtils.weightFormat(mSet.getWeight()));
        mEditTextComment.setText(mSet.getComment());

        return new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.set_edit_dialog_title))
                .setPositiveButton(getResources().getString(R.string.dialog_ok), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            //Use valueOf to validate number format of input data
                            mSet.setReps(Integer.valueOf(mEditTextReps.getText().toString()).toString());
                            mSet.setWeight(Float.valueOf(mEditTextWeight.getText().toString()));
                            mSet.setComment(mEditTextComment.getText().toString());

                            //Result callback
                            getTargetFragment().onActivityResult(getTargetRequestCode(),
                                    Activity.RESULT_OK, getActivity().getIntent().putExtra(KEY_SET, mSet));
                        } catch (NumberFormatException ex) {
                            Toast.makeText(getActivity(), R.string.error_input, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(getResources().getString(R.string.dialog_cancel), null)
                .setView(view)
                .create();
    }

    private void retrieveData(Bundle bundle) {
        if (bundle != null) {
            mSet = bundle.getParcelable(KEY_SET);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_SET, mSet);
        super.onSaveInstanceState(outState);
    }
}