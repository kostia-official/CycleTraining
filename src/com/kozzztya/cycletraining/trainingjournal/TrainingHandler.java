package com.kozzztya.cycletraining.trainingjournal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.customviews.MyCaldroidFragment;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.OnDBChangeListener;
import com.kozzztya.cycletraining.db.datasources.MesocyclesDataSource;
import com.kozzztya.cycletraining.db.datasources.TrainingsDataSource;
import com.kozzztya.cycletraining.db.entities.MesocycleView;
import com.kozzztya.cycletraining.db.entities.Training;
import com.kozzztya.cycletraining.trainingadd.TrainingPlanActivity;
import com.kozzztya.cycletraining.trainingprocess.TrainingProcessActivity;
import com.kozzztya.cycletraining.utils.DateUtils;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class TrainingHandler {

    private Context context;
    private Training training;

    private TrainingsDataSource trainingsDataSource;
    private MesocyclesDataSource mesocyclesDataSource;

    private OnDBChangeListener onDBChangeListener;

    public TrainingHandler(Context context, Training training) {
        this.context = context;
        this.training = training;

        DBHelper dbHelper = DBHelper.getInstance(context);
        trainingsDataSource = dbHelper.getTrainingsDataSource();
        mesocyclesDataSource = dbHelper.getMesocyclesDataSource();
    }

    public void showMainDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setItems(R.array.training_actions, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                showDeleteDialog();
                                break;
                            case 1:
                                showMesocycle();
                                break;
                            case 2:
                                showMoveDialog();
                                break;
                        }

                    }
                })
                .create();

        dialog.show();
    }

    public void showMoveDialog() {
        final MyCaldroidFragment dialogCaldroidFragment = new MyCaldroidFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CaldroidFragment.DIALOG_TITLE, context.getString(R.string.date_dialog_title));
        bundle.putInt(CaldroidFragment.START_DAY_OF_WEEK, Preferences.getFirstDayOfWeek(context));
        dialogCaldroidFragment.setArguments(bundle);

        dialogCaldroidFragment.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(java.util.Date date, View view) {
                move(date.getTime());
                dialogCaldroidFragment.dismiss();
            }
        });

        dialogCaldroidFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "CALDROID_DIALOG_FRAGMENT");
        notifyDBChanged();
    }

    public void showMissedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.on_missed_title)
                .setItems(R.array.on_missed_actions, new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0)
                                    showMoveDialog();
                                else if (which == 1) {
                                    startTraining();
                                }
                            }
                        }
                );
        builder.show();
    }

    public void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.on_delete_title)
                .setItems(R.array.delete_actions, new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0)
                                    fullDelete();
                                else if (which == 1)
                                    deleteOnlyUndone();
                            }
                        }
                );
        builder.show();
    }

    public void fullDelete() {
        mesocyclesDataSource.delete(training.getMesocycle());
        notifyDBChanged();
    }

    public void deleteOnlyUndone() {
        String where = TrainingsDataSource.COLUMN_DONE + " = 0 AND " +
                TrainingsDataSource.COLUMN_MESOCYCLE + " = " + training.getMesocycle();
        trainingsDataSource.delete(where);
        notifyDBChanged();
    }

    public void move(long newDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String where = TrainingsDataSource.COLUMN_DATE + " >= '" + dateFormat.format(training.getDate()) + "' AND " +
                TrainingsDataSource.COLUMN_MESOCYCLE + " = " + training.getMesocycle();
        List<Training> trainings = trainingsDataSource.select(where, null, null, TrainingsDataSource.COLUMN_DATE);
        MesocycleView mesocycle = mesocyclesDataSource.getEntityView(training.getMesocycle());

        for (int i = 0; i < trainings.size(); i++) {
            Training t = trainings.get(i);

            long trainingDate = DateUtils.calcTrainingDate(i, mesocycle.getTrainingsInWeek(), new Date(newDate));
            t.setDate(new Date(trainingDate));

            trainingsDataSource.update(t);
        }
        notifyDBChanged();
    }

    public void showMesocycle() {
        Intent intent = new Intent(context, TrainingPlanActivity.class);
        intent.putExtra("mesocycleId", training.getMesocycle());
        context.startActivity(intent);
    }

    public void startTraining() {
        Intent intent = new Intent(context, TrainingProcessActivity.class);
        intent.putExtra("dayOfTraining", training.getDate().getTime());
        intent.putExtra("chosenTrainingId", training.getId());
        context.startActivity(intent);
    }

    public void setOnDBChangeListener(OnDBChangeListener onDBChangeListener) {
        this.onDBChangeListener = onDBChangeListener;
    }

    public void notifyDBChanged() {
        if (onDBChangeListener != null)
            onDBChangeListener.onDBChange();
    }

    public Training getTraining() {
        return training;
    }

    public void setTraining(Training training) {
        this.training = training;
    }
}