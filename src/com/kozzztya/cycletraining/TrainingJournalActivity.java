package com.kozzztya.cycletraining;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;

public class TrainingJournalActivity extends DrawerActivity {

    private Menu menu;
    private ShowcaseView showcaseView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.training_journal);

        openFragment(new TrainingWeekFragment());

        showcaseView = new ShowcaseView.Builder(this)
                .setTarget(new ActionViewTarget(this, ActionViewTarget.Type.HOME))
                .setContentText(getString(R.string.showcase_first_start_text))
                .setStyle(R.style.Theme_AppCompat_Light)
                .singleShot(1)
                .build();
        showcaseView.hideButton();
    }

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.training_journal_frame, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.training_journal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showcaseView.hide();
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsActivity = new Intent(this, Preferences.class);
                startActivity(settingsActivity);
                return true;
            case R.id.action_help:
                return true;
            case R.id.action_exit:
                finish();
                return true;
            case R.id.action_calendar:
                openFragment(new TrainingCalendarFragment());
                menu.findItem(R.id.action_calendar).setVisible(false);
                menu.findItem(R.id.action_week).setVisible(true);
                return true;
            case R.id.action_week:
                openFragment(new TrainingWeekFragment());
                menu.findItem(R.id.action_calendar).setVisible(true);
                menu.findItem(R.id.action_week).setVisible(false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}