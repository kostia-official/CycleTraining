package com.kozzztya.cycletraining.trainingjournal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.actionbar.reflection.BaseReflector;
import com.espian.showcaseview.targets.ViewTarget;
import com.kozzztya.cycletraining.DrawerActivity;
import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;

public class TrainingJournalActivity extends DrawerActivity {

    private Menu mMenu;
    private ShowcaseView mShowcaseView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.training_journal);

        openFragment(new TrainingWeekFragment());

        if (new Preferences(this).isFirstRun()) {
            BaseReflector reflector = BaseReflector.getReflectorForActivity(this);
            View homeButton = reflector.getHomeButton();
            mShowcaseView = ShowcaseView.insertShowcaseView(new ViewTarget(homeButton), this, R.string.showcase_title,
                    R.string.showcase_first_start_text);
        }
    }

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.training_journal_frame, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        getMenuInflater().inflate(R.menu.training_journal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_calendar:
                openFragment(new TrainingCalendarFragment());
                mMenu.findItem(R.id.action_calendar).setVisible(false);
                mMenu.findItem(R.id.action_week).setVisible(true);
                return true;
            case R.id.action_week:
                openFragment(new TrainingWeekFragment());
                mMenu.findItem(R.id.action_calendar).setVisible(true);
                mMenu.findItem(R.id.action_week).setVisible(false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (mShowcaseView != null)
            mShowcaseView.hide();
        return super.onSupportNavigateUp();
    }
}