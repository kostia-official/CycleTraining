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

    public static final String KEY_COMMENT = "comment";

    private String mComment;
    private EditText mCommentEditText;

    public CommentDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            retrieveData(savedInstanceState);
        } else {
            retrieveData(getArguments());
        }

        mCommentEditText = new EditText(getActivity());
        mCommentEditText.setText(mComment);

        return new AlertDialog.Builder(getActivity())
                .setView(mCommentEditText)
                .setTitle(getResources().getString(R.string.comment))
                .setPositiveButton(getResources().getString(R.string.dialog_ok), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mComment = mCommentEditText.getText().toString();

                        //Result callback
                        Intent intent = getActivity().getIntent().putExtra(KEY_COMMENT, mComment);
                        getTargetFragment().onActivityResult(getTargetRequestCode(),
                                Activity.RESULT_OK, intent);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.dialog_cancel), null)
                .create();
    }

    private void retrieveData(Bundle bundle) {
        if (bundle != null) {
            mComment = bundle.getString(KEY_COMMENT);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_COMMENT, mCommentEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }
}