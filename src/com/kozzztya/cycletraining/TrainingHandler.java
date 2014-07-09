package com.kozzztya.cycletraining;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.MesocyclesDataSource;
import com.kozzztya.cycletraining.db.datasources.TrainingsDataSource;
import com.kozzztya.cycletraining.db.entities.Training;

public class TrainingHandler {

    private Context context;
    private Training training;

    private AlertDialog alertDialog;

    private TrainingsDataSource trainingsDataSource;
    private MesocyclesDataSource mesocyclesDataSource;

    public TrainingHandler(Context context, Training training) {
        this.context = context;
        this.training = training;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.training_actions, new DialogInterface.OnClickListener() {
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

    public void show() {
        alertDialog.show();
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        alertDialog.setOnDismissListener(listener);
    }

    public void showMesocycle() {
        Intent intent = new Intent(context, MesocycleShowActivity.class);
        intent.putExtra("mesocycleId", training.getMesocycle());
        context.startActivity(intent);
    }

    public void delete() {
        mesocyclesDataSource.delete(training.getMesocycle());
    }

    public void move() {
        
    }

}
