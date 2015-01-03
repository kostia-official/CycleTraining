package com.kozzztya.cycletraining;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Superclass with default behaviour of all activities
 */
public class BaseActivity extends ActionBarActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(settingsActivity);
                return true;
            case R.id.action_help:
                return true;
            case R.id.action_about:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * On Navigate Up go to the parent activity
     * or go to the back stack if it not specified in AndroidManifest
     */
    @Override
    public boolean onSupportNavigateUp() {
        Intent parentActivity = NavUtils.getParentActivityIntent(this);

        if (parentActivity != null) {
            NavUtils.navigateUpTo(this, parentActivity);
        } else {
            onBackPressed();
        }
        return true;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }
}
