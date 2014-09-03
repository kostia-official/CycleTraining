package com.kozzztya.cycletraining.trainingprocess;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;

import com.kozzztya.cycletraining.R;

import static android.content.DialogInterface.OnClickListener;

public class CommentDialogFragment extends DialogFragment {

    public static final String ARG_COMMENT = "comment";

    private String comment;

    public CommentDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        retrieveArgs();

        final EditText editTextComment = new EditText(getActivity());
        editTextComment.setText(comment);

        return new AlertDialog.Builder(getActivity())
                .setView(editTextComment)
                .setTitle(getResources().getString(R.string.comment))
                .setPositiveButton(getResources().getString(R.string.dialog_ok), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        comment = editTextComment.getText().toString();

                        //Result callback
                        Intent intent = getActivity().getIntent().putExtra(ARG_COMMENT, comment);
                        getTargetFragment().onActivityResult(getTargetRequestCode(),
                                Activity.RESULT_OK, intent);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.dialog_cancel), null)
                .create();
    }

    private void retrieveArgs() {
        Bundle args = getArguments();
        if (args != null) {
            comment = args.getString(ARG_COMMENT);
        }
    }
}