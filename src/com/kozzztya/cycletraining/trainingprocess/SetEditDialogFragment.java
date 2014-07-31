package com.kozzztya.cycletraining.trainingprocess;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.utils.SetUtils;

import static android.content.DialogInterface.OnDismissListener;
import static android.content.DialogInterface.OnShowListener;

public class SetEditDialogFragment extends DialogFragment {

    private Set set;
    private EditText editTextReps;
    private EditText editTextWeight;
    private EditText editTextComment;
    private OnDismissListener onDismissListener;
    private AlertDialog alertDialog;

    public SetEditDialogFragment(Set set) {
        this.set = set;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.set_edit_fragment, null);

        editTextReps = (EditText) view.findViewById(R.id.editTextReps);
        editTextWeight = (EditText) view.findViewById(R.id.editTextWeight);
        editTextComment = (EditText) view.findViewById(R.id.editTextComment);

        editTextReps.setText(set.getReps());
        editTextWeight.setText(SetUtils.weightFormat(set.getWeight()));
        editTextComment.setText(set.getComment());

        alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.set_edit_dialog_title))
                .setPositiveButton(getResources().getString(R.string.dialog_ok), null)
                .setNegativeButton(getResources().getString(R.string.dialog_cancel), null)
                .setView(view)
                .create();

        alertDialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            //Use valueOf to validate number format of reps
                            set.setReps(Integer.valueOf(editTextReps.getText().toString()).toString());
                            set.setWeight(Float.valueOf(editTextWeight.getText().toString()));
                            set.setComment(editTextComment.getText().toString());

                            alertDialog.dismiss();
                        } catch (NumberFormatException ex) {
                            Toast.makeText(getActivity(), R.string.error_input, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return alertDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        alertDialog.setOnDismissListener(onDismissListener);
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }
}
