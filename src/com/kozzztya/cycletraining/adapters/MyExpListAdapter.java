package com.kozzztya.cycletraining.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    /**
     * Filter by children's method return value
     *
     * @param method      Getter or another method that returns child's value
     * @param filterValue Number or text value for comparison
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */

    public void filterChildren(Method method, Object filterValue) throws
            InvocationTargetException, IllegalAccessException {
        Map<G, List<C>> filtered = new LinkedHashMap<>();
        for (int groupPos = 0; groupPos < getGroupCount(); groupPos++) {
            G group = getGroup(groupPos);

            for (int childPos = 0; childPos < getChildrenCount(groupPos); childPos++) {
                C child = getChild(groupPos, childPos);
                Object childValue = method.invoke(child, (Object[]) null);

                boolean comparison = false;

                if (childValue instanceof String && filterValue instanceof String) {
                    //Text comparison
                    String childStringValue = ((String) childValue).toLowerCase();
                    String filterStringValue = ((String) filterValue).toLowerCase();

                    comparison = childStringValue.contains(filterStringValue);
                } else {
                    //Simple object comparison
                    comparison = childValue.equals(filterValue);
                }

                //Put filtered values by groups
                if (comparison) {
                    if (!filtered.containsKey(group))
                        filtered.put(group, new ArrayList<C>());
                    filtered.get(group).add(child);
                }
            }
        }
        groups.clear();
        groups.putAll(filtered);
        notifyDataSetChanged();
    }

    public void resetFilter() {
        groups.clear();
        groups.putAll(originalGroups);
        notifyDataSetChanged();
    }
}
