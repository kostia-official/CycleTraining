package com.kozzztya.cycletraining;

import android.app.Activity;
import android.os.Bundle;

import java.sql.Date;
import java.util.Calendar;

public class TrainingJournalActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_journal);

        Date currentDate = new Date(Calendar.getInstance().getTime().getTime());


    }
}