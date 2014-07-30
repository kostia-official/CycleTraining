package com.kozzztya.cycletraining.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class MyExpListAdapter<G, C> extends BaseExpandableListAdapter {

    public Map<G, List<C>> groups;
    public Map<G, List<C>> originalGroups;

    public MyExpListAdapter(Map<G, List<C>> groups) {
        this.groups = groups;
        this.originalGroups = new LinkedHashMap<>(groups);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int pos) {
        return getChildrenOfGroup(pos).size();
    }

    @Override
    public G getGroup(int pos) {
        ArrayList<G> groupsList = new ArrayList<>(groups.keySet());
        return groupsList.get(pos);
    }

    @Override
    public C getChild(int groupPos, int childPos) {
        return getChildrenOfGroup(groupPos).get(childPos);
    }

    public List<C> getChildrenOfGroup(int pos) {
        ArrayList<List<C>> childrenList = new ArrayList<>(groups.values());
        return childrenList.get(pos);
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
    public boolean isChildSelectable(int groupPos, int childPos) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public abstract View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent);

    @Override
    public abstract View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent);

}
