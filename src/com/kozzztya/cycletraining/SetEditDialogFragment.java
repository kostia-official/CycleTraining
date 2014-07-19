package com.kozzztya.cycletraining;

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
import com.kozzztya.cycletraining.db.OnDBChangeListener;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.utils.SetUtils;

public class SetEditDialogFragment extends DialogFragment {

    private Set set;
    private EditText editTextReps;
    private EditText editTextWeight;
    private EditText editTextComment;
    private OnDBChangeListener onDBChangeListener;

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

        editTextReps.setText(SetUtils.repsFormat(set.getReps(), getActivity()));
        editTextWeight.setText(SetUtils.weightFormat(set.getWeight()));
        editTextComment.setText(set.getComment());

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.set_edit_dialog_title))
                .setPositiveButton(getResources().getString(R.string.dialog_ok), null)
                .setNegativeButton(getResources().getString(R.string.dialog_cancel), null)
                .setView(view)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            set.setReps(Integer.valueOf(editTextReps.getText().toString()));
                            set.setWeight(Integer.valueOf(editTextWeight.getText().toString()));
                            set.setComment(editTextComment.getText().toString());

                            notifyDBChanged();
                            alertDialog.dismiss();
                        } catch (NumberFormatException ex) {
                            Toast.makeText(getActivity(), R.string.toast_number_format_exception, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return alertDialog;
    }

    public void notifyDBChanged() {
        if (onDBChangeListener != null)
            onDBChangeListener.onDBChange();
    }

    public void setOnDBChangeListener(OnDBChangeListener listener) {
        this.onDBChangeListener = listener;
    }
}
