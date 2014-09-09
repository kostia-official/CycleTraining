package com.kozzztya.cycletraining.trainingcreate;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.ExerciseTypesDS;
import com.kozzztya.cycletraining.db.datasources.ExercisesDS;
import com.kozzztya.cycletraining.db.datasources.MusclesDS;
import com.kozzztya.cycletraining.db.entities.Exercise;
import com.kozzztya.cycletraining.db.entities.ExerciseType;
import com.kozzztya.cycletraining.db.entities.Muscle;

import java.util.List;

public class ExerciseCreateFragment extends Fragment implements OnClickListener {

    private Spinner mSpinnerMuscles;
    private Spinner mSpinnerType;
    private EditText mEditTextName;
    private DBHelper mDBHelper;

    private OnExerciseAddedListener mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(getString(R.string.action_exercise_create));
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.exercise_create, container, false);
        mSpinnerMuscles = (Spinner) view.findViewById(R.id.spinnerMuscles);
        mSpinnerType = (Spinner) view.findViewById(R.id.spinnerTypes);
        mEditTextName = (EditText) view.findViewById(R.id.name);
        mDBHelper = DBHelper.getInstance(getActivity());

        bindData();
        return view;
    }

    private void bindData() {
        List<Muscle> muscles = new MusclesDS(mDBHelper).select(null, null, null, null);
        ArrayAdapter<Muscle> adapterMuscles = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, muscles);
        adapterMuscles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMuscles.setAdapter(adapterMuscles);

        List<ExerciseType> types = new ExerciseTypesDS(mDBHelper).select(null, null, null, null);
        ArrayAdapter<ExerciseType> adapterTypes = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, types);
        adapterTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerType.setAdapter(adapterTypes);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnExerciseAddedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + mCallback.getClass().getSimpleName());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.done, menu);
        View actionView = MenuItemCompat.getActionView(menu.findItem(R.id.action_done));
        actionView.setOnClickListener(this);
    }

    /**
     * On done menu item click
     */
    @Override
    public void onClick(View v) {
        ExercisesDS exercisesDS = new ExercisesDS(mDBHelper);

        if (mEditTextName.getText().length() == 0) {
            mEditTextName.setError(getString(R.string.error_input));
            return;
        }

        Muscle muscle = (Muscle) mSpinnerMuscles.getSelectedItem();
        ExerciseType type = (ExerciseType) mSpinnerType.getSelectedItem();

        Exercise exercise = new Exercise();
        exercise.setName(mEditTextName.getText().toString());
        exercise.setMuscle(muscle.getId());
        exercise.setExerciseType(type.getId());
        exercise.setId(exercisesDS.insert(exercise));

        mCallback.onExerciseCreated(exercise);
    }

    public interface OnExerciseAddedListener {
        public void onExerciseCreated(Exercise exercise);
    }
}