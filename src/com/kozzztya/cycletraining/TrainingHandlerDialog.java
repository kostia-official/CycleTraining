package com.kozzztya.cycletraining;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.MesocyclesDataSource;
import com.kozzztya.cycletraining.db.datasources.TrainingsDataSource;
import com.kozzztya.cycletraining.db.entities.Training;

public class TrainingHandlerDialog {

    private Context context;
    private Training training;

    private AlertDialog alertDialog;

    private TrainingsDataSource trainingsDataSource;
    private MesocyclesDataSource mesocyclesDataSource;
    private OnDismissListener listener;

    public TrainingHandlerDialog(Context context, Training training) {
        this.context = context;
        this.training = training;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.training_actions, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        delete();
                        break;
                    case 1:
                        showMesocycle();
                        break;
                    case 2:
                        move();
                        break;
                }

            }
        });

        alertDialog = builder.create();

        DBHelper dbHelper = DBHelper.getInstance(context);
        trainingsDataSource = dbHelper.getTrainingsDataSource();
        mesocyclesDataSource = dbHelper.getMesocyclesDataSource();
    }

    public void showMesocycle() {
        Intent intent = new Intent(context, MesocycleShowActivity.class);
        intent.putExtra("mesocycleId", training.getMesocycle());
        context.startActivity(intent);
    }

    public void delete() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.delete_title)
                .setItems(R.array.delete_actions, new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) //Full delete
                                    mesocyclesDataSource.delete(training.getMesocycle());
                                else if (which == 1) { //Save done trainings
                                    String where = TrainingsDataSource.COLUMN_DONE + " = 0 AND " +
                                            TrainingsDataSource.COLUMN_MESOCYCLE + " = " + training.getMesocycle();
                                    trainingsDataSource.delete(where);
                                }
                            }
                        }
                );
        alertDialog = builder.create();
        show();
    }

    public void move() {

    }

    public void show() {
        if (listener != null)
            alertDialog.setOnDismissListener(listener);
        alertDialog.show();
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.listener = listener;
    }

}
