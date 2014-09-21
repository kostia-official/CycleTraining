package com.kozzztya.cycletraining.trainingprocess;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DatabaseProvider;
import com.kozzztya.cycletraining.db.Sets;
import com.kozzztya.cycletraining.utils.SetUtils;

import static android.content.DialogInterface.OnClickListener;

public class SetEditDialogFragment extends DialogFragment {

    public static final String KEY_SET_VALUES = "setValues";

    private ContentValues mSetValues;

    private EditText mEditTextReps;
    private EditText mEditTextWeight;
    private EditText mEditTextComment;

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

        if (mSetValues.containsKey(Sets.REPS))
            mEditTextReps.setText(mSetValues.getAsString(Sets.REPS));
        if (mSetValues.containsKey(Sets.WEIGHT))
            mEditTextWeight.setText(SetUtils.weightFormat(mSetValues.getAsFloat(Sets.WEIGHT)));
        if (mSetValues.containsKey(Sets.COMMENT))
            mEditTextComment.setText(mSetValues.getAsString(Sets.COMMENT));

        return new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.set_edit_dialog_title))
                .setPositiveButton(getResources().getString(R.string.dialog_ok), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            // Use valueOf to validate number format of input data
                            mSetValues.put(Sets.REPS, Integer.valueOf(mEditTextReps.getText().toString()));
                            mSetValues.put(Sets.WEIGHT, Float.valueOf(mEditTextWeight.getText().toString()));
                            mSetValues.put(Sets.COMMENT, mEditTextComment.getText().toString());

                            // Insert or replace new set values
                            ContentResolver contentResolver = getActivity().getContentResolver();
                            contentResolver.insert(DatabaseProvider.SETS_URI, mSetValues);

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
            mSetValues = bundle.getParcelable(KEY_SET_VALUES);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_SET_VALUES, mSetValues);
        super.onSaveInstanceState(outState);
    }
}