package com.kozzztya.cycletraining.trainingcreate;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.custom.ExpandableListFragment;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.ExercisesDS;
import com.kozzztya.cycletraining.db.datasources.MusclesDS;
import com.kozzztya.cycletraining.db.entities.Exercise;
import com.kozzztya.cycletraining.db.entities.Muscle;
import com.kozzztya.cycletraining.utils.StyleUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ExercisesFragment extends ExpandableListFragment {

    private MySimpleExpListAdapter<Muscle, Exercise> mMuscleExercisesAdapter;
    private ExercisesCallbacks mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StyleUtils.setExpListViewCardStyle(getExpandableListView(), getActivity());
        bindData();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (ExercisesCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement "
                    + ExercisesCallbacks.class.getSimpleName());
        }
    }

    private void bindData() {
        DBHelper dbHelper = DBHelper.getInstance(getActivity());
        ExercisesDS exercisesDS = new ExercisesDS(dbHelper);
        MusclesDS musclesDS = new MusclesDS(dbHelper);

        List<Exercise> exercises = exercisesDS.select(null, null, null, null);
        List<Muscle> muscles = musclesDS.select(null, null, null, null);
        //Exercises grouped by muscle
        LinkedHashMap<Muscle, List<Exercise>> muscleExercises = new LinkedHashMap<>();

        for (Exercise exercise : exercises) {
            for (Muscle muscle : muscles) {
                if (exercise.getMuscle() == muscle.getId()) {
                    if (!muscleExercises.containsKey(muscle)) {
                        muscleExercises.put(muscle, new ArrayList<Exercise>());
                    }
                    muscleExercises.get(muscle).add(exercise);
                }
            }
        }

        mMuscleExercisesAdapter = new MySimpleExpListAdapter<>(getActivity(), muscleExercises);
        setListAdapter(mMuscleExercisesAdapter);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Exercise exercise = mMuscleExercisesAdapter.getChild(groupPosition, childPosition);
        mCallback.onExerciseSelected(exercise);
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.exercises, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                mCallback.onExerciseCreateRequest();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface ExercisesCallbacks {
        public void onExerciseSelected(Exercise exercise);

        public void onExerciseCreateRequest();
    }
}
