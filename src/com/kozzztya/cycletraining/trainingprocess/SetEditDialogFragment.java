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

    private EditText editTextReps;
    private EditText editTextWeight;
    private EditText editTextComment;

    //Set for editing
    private Set set;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        retrieveArgs();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.set_edit_fragment, null);

        editTextReps = (EditText) view.findViewById(R.id.editTextReps);
        editTextWeight = (EditText) view.findViewById(R.id.editTextWeight);
        editTextComment = (EditText) view.findViewById(R.id.editTextComment);

        editTextReps.setText(set.getReps());
        editTextWeight.setText(SetUtils.weightFormat(set.getWeight()));
        editTextComment.setText(set.getComment());

        return new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.set_edit_dialog_title))
                .setPositiveButton(getResources().getString(R.string.dialog_ok), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            //Use valueOf to validate number format of input data
                            set.setReps(Integer.valueOf(editTextReps.getText().toString()).toString());
                            set.setWeight(Float.valueOf(editTextWeight.getText().toString()));
                            set.setComment(editTextComment.getText().toString());

                            //Result callback
                            getTargetFragment().onActivityResult(getTargetRequestCode(),
                                    Activity.RESULT_OK, getActivity().getIntent());
                        } catch (NumberFormatException ex) {
                            Toast.makeText(getActivity(), R.string.error_input, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(getResources().getString(R.string.dialog_cancel), null)
                .setView(view)
                .create();
    }

    private void retrieveArgs() {
        Bundle args = getArguments();
        if (args != null) {
            set = args.getParcelable("set");
        }
    }
}