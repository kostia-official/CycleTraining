package com.kozzztya.cycletraining;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.kozzztya.cycletraining.adapters.SetsListAdapter;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.utils.RMUtils;

public class SetEditDialogFragment extends DialogFragment {

    private Set set;
    private SetsListAdapter adapter;
    private EditText editTextReps;
    private EditText editTextWeight;
    private EditText editTextComment;

    public SetEditDialogFragment(Set set, SetsListAdapter adapter) {
        this.set = set;
        this.adapter = adapter;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.set_edit_fragment, null);

        editTextReps = (EditText) view.findViewById(R.id.editTextReps);
        editTextWeight = (EditText) view.findViewById(R.id.editTextWeight);
        editTextComment = (EditText) view.findViewById(R.id.editTextComment);

        editTextReps.setText(String.valueOf(set.getReps()));
        editTextWeight.setText(RMUtils.weightFormat(set.getWeight()));
        editTextComment.setText(set.getComment());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.set_edit_dialog_title))
                .setPositiveButton(getResources().getString(R.string.dialog_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                try {
                                    set.setReps(Integer.valueOf(editTextReps.getText().toString()));
                                    set.setWeight(Integer.valueOf(editTextWeight.getText().toString()));
                                    set.setComment(editTextComment.getText().toString());

                                    //Обновляем изменённые данные
                                    adapter.notifyDataSetChanged();
                                } catch (NumberFormatException ex) {
                                    Toast.makeText(getActivity(), R.string.toast_number_format_exception, Toast.LENGTH_SHORT);
                                }
                            }
                        }
                )
                .setNegativeButton(getResources().getString(R.string.dialog_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );
        builder.setView(view);
        return builder.create();
    }
}
