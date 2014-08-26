package com.kozzztya.cycletraining;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.kozzztya.cycletraining.statistic.StatisticCreateActivity;
import com.kozzztya.cycletraining.trainingcreate.TrainingCreateActivity;
import com.kozzztya.cycletraining.trainingjournal.TrainingJournalActivity;

import static android.widget.ListView.OnItemClickListener;

public class DrawerActivity extends MyActionBarActivity implements OnItemClickListener {

    protected ListView drawerList;
    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle drawerToggle;

    public void onCreate(Bundle savedInstanceState, int layoutId) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        );

        String[] titles = getResources().getStringArray(R.array.drawer_items);
        int[] icons = new int[]{
                R.drawable.ic_action_create_training,
                R.drawable.ic_action_diary,
                R.drawable.ic_action_statistic
        };

        drawerList.setAdapter(new DrawerListAdapter(this,
                R.layout.drawer_list_item, titles, icons));
        drawerList.setOnItemClickListener(this);

        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Pass the event to ActionBarDrawerToggle, if it returns
        //true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

    public void selectItem(int position) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        switch (position) {
            case 0:
                intent.setClass(this, TrainingCreateActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent.setClass(this, TrainingJournalActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent.setClass(this, StatisticCreateActivity.class);
                startActivity(intent);
                break;
        }
        drawerList.setItemChecked(position, true);
        drawerLayout.closeDrawer(drawerList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    public static class DrawerListAdapter extends ArrayAdapter<String> {

        private int resource;
        private int[] icons;

        public DrawerListAdapter(Context context, int resource, String[] objects, int[] icons) {
            super(context, resource, objects);
            this.resource = resource;
            this.icons = icons;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(resource, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(R.id.title);
            textView.setText(getItem(position));
            textView.setCompoundDrawablesWithIntrinsicBounds(icons[position], 0, 0, 0);

            return view;
        }
    }
}