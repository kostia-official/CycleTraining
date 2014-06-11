package com.kozzztya.cycletraining;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import static android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button newProgramButton = (Button) findViewById(R.id.buttonNewProgram);
        newProgramButton.setOnClickListener(this);

        Button buttonTrainingJournal = (Button) findViewById(R.id.buttonDiary);
        buttonTrainingJournal.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonNewProgram:
                startActivity(new Intent(this, MesocycleCreateActivity.class));
                break;
            case R.id.buttonDiary:
                startActivity(new Intent(this, TrainingJournalShowActivity.class));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_help:
                return true;
            case R.id.action_exit:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}