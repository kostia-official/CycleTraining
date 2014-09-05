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
import com.kozzztya.cycletraining.db.datasources.MesocyclesDS;
import com.kozzztya.cycletraining.db.datasources.TrainingsDS;
import com.kozzztya.cycletraining.db.entities.Mesocycle;
import com.kozzztya.cycletraining.db.entities.Training;
import com.kozzztya.cycletraining.trainingcreate.TrainingPlanActivity;
import com.kozzztya.cycletraining.trainingprocess.TrainingProcessActivity;
import com.kozzztya.cycletraining.utils.DateUtils;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.sql.Date;
import java.util.List;

public class TrainingHandler {

    private Context mContext;
    private Training mTraining;

    private TrainingsDS mTrainingsDS;
    private MesocyclesDS mMesocyclesDS;
    private final DBHelper mDBHelper;

    public TrainingHandler(Context context, Training training) {
        mContext = context;
        mTraining = training;

        mDBHelper = DBHelper.getInstance(context);
        mTrainingsDS = new TrainingsDS(mDBHelper);
        mMesocyclesDS = new MesocyclesDS(mDBHelper);
    }

    public void showMainDialog() {
        AlertDialog dialog = new AlertDialog.Builder(mContext)
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
        bundle.putString(CaldroidFragment.DIALOG_TITLE, mContext.getString(R.string.date_dialog_title));
        bundle.putInt(CaldroidFragment.START_DAY_OF_WEEK, new Preferences(mContext).getFirstDayOfWeek());
        dialogCaldroidFragment.setArguments(bundle);

        dialogCaldroidFragment.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(java.util.Date date, View view) {
                move(date.getTime());
                dialogCaldroidFragment.dismiss();
            }
        });

        dialogCaldroidFragment.show(((FragmentActivity) mContext).getSupportFragmentManager(),
                MyCaldroidFragment.class.getSimpleName());
    }

    public void showMissedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
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
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
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
        mMesocyclesDS.delete(mTraining.getMesocycle());
        mDBHelper.notifyDBChanged();
    }

    public void deleteOnlyUndone() {
        String where = TrainingsDS.COLUMN_DONE + " = 0 AND " +
                TrainingsDS.COLUMN_MESOCYCLE + " = " + mTraining.getMesocycle();
        mTrainingsDS.delete(where);
        mDBHelper.notifyDBChanged();
    }

    public void move(long newDate) {
        //Select following trainings
        String where = TrainingsDS.COLUMN_DATE + " >= " + DateUtils.sqlFormat(mTraining.getDate()) + " AND " +
                TrainingsDS.COLUMN_MESOCYCLE + " = " + mTraining.getMesocycle();
        List<Training> trainings = mTrainingsDS.select(where, null, null, TrainingsDS.COLUMN_DATE);
        Mesocycle mesocycle = mMesocyclesDS.getEntity(mTraining.getMesocycle());

        for (int i = 0; i < trainings.size(); i++) {
            Training t = trainings.get(i);
            long trainingDate = DateUtils.calcTrainingDate(i, mesocycle.getTrainingsInWeek(), new Date(newDate));
            t.setDate(new Date(trainingDate));
            mTrainingsDS.update(t);
        }

        mDBHelper.notifyDBChanged();
    }

    public void showMesocycle() {
        Intent intent = new Intent(mContext, TrainingPlanActivity.class);
        Mesocycle mesocycle = mMesocyclesDS.getEntity(mTraining.getMesocycle());
        intent.putExtra(TrainingPlanActivity.KEY_MESOCYCLE, mesocycle);
        mContext.startActivity(intent);
    }

    public void startTraining() {
        Intent intent = new Intent(mContext, TrainingProcessActivity.class);
        intent.putExtra(TrainingProcessActivity.KEY_TRAINING_DAY, mTraining.getDate().getTime());
        intent.putExtra(TrainingProcessActivity.KEY_CHOSEN_TRAINING_ID, mTraining.getId());
        mContext.startActivity(intent);
    }

    public Training getTraining() {
        return mTraining;
    }

    public void setTraining(Training training) {
        this.mTraining = training;
    }
}
