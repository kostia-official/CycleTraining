package com.kozzztya.cycletraining;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.kozzztya.cycletraining.db.entities.TrainingView;

import java.util.List;
import java.util.SortedMap;

public class MyExpListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private SortedMap<String, List<TrainingView>> groups;

    public MyExpListAdapter(Context context, SortedMap<String, List<TrainingView>> groups) {
        this.context = context;
        this.groups = groups;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int pos) {
        return groups.values().size();
    }

    @Override
    public Object getGroup(int pos) {
        return groups.get(pos);
    }

    @Override
    public Object getChild(int groupPos, int childPos) {
        return groups.get(groupPos).get(childPos);
    }

    @Override
    public long getGroupId(int pos) {
        return pos;
    }

    @Override
    public long getChildId(int groupPos, int childPos) {
        return childPos;
    }

    @Override
    public View getGroupView(int pos, boolean isExpanded, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.exp_list_group, null);
        }
        if (groups.get(pos).size() == 0) {
            view.setVisibility(View.GONE);
            return view;
        }
        TextView title = (TextView) view.findViewById(R.id.textViewDayOfWeek);
        String[] daysOfWeek = context.getResources().getStringArray(R.array.days_of_week);
        title.setText(daysOfWeek[pos]);
        return view;
    }

    @Override
    public View getChildView(int groupPos, int childPos, boolean isExpanded, View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPos, int childPos) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
