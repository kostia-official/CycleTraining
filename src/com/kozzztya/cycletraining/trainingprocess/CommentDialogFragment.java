package com.kozzztya.cycletraining.trainingprocess;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.Trainings;

public class CommentDialogFragment extends DialogFragment {

    public static final String KEY_TRAINING_URI = "trainingUri";
    public static final String KEY_COMMENT = "comment";

    private Uri mTrainingUri;
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
                .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mComment = mCommentEditText.getText().toString();

                        // Update comment data
                        ContentValues values = new ContentValues();
                        values.put(Trainings.COMMENT, mComment);
                        getActivity().getContentResolver().update(mTrainingUri, values, null, null);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.dialog_cancel), null)
                .create();
    }

    private void retrieveData(Bundle bundle) {
        if (bundle != null) {
            mTrainingUri = bundle.getParcelable(KEY_TRAINING_URI);
            mComment = bundle.getString(KEY_COMMENT);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_TRAINING_URI, mTrainingUri);
        outState.putString(KEY_COMMENT, mCommentEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }
}