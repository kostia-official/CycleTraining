package com.kozzztya.cycletraining.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import com.kozzztya.customview.CardView;
import com.kozzztya.cycletraining.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class MyExpListAdapter<G, C> extends BaseExpandableListAdapter {

    private Map<G, List<C>> groupsChildrenMap;
    private List<G> groups;
    private List<List<C>> children;
    private Map<G, List<C>> originalMap;

    public MyExpListAdapter(Map<G, List<C>> groupsChildrenMap) {
        this.groupsChildrenMap = groupsChildrenMap;

        originalMap = new LinkedHashMap<>(groupsChildrenMap);
        groups = new ArrayList<>(groupsChildrenMap.keySet());
        children = new ArrayList<>(groupsChildrenMap.values());
    }

    @Override
    public int getGroupCount() {
        return groupsChildrenMap.size();
    }

    @Override
    public int getChildrenCount(int pos) {
        return getChildrenOfGroup(pos).size();
    }

    @Override
    public G getGroup(int pos) {
        return groups.get(pos);
    }

    @Override
    public C getChild(int groupPos, int childPos) {
        return getChildrenOfGroup(groupPos).get(childPos);
    }

    public List<C> getChildrenOfGroup(int pos) {
        return children.get(pos);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        //Imitate card style for ExpandableListView group
        CardView cardView = (CardView) convertView.findViewById(R.id.card);
        if (cardView != null) cardView.setBottomShadow(!isExpanded);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        CardView cardView = (CardView) convertView.findViewById(R.id.card);
        if (cardView != null) {
            cardView.setTopShadow(false);
            cardView.setBottomShadow(isLastChild);
        }

        return convertView;
    }

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

        setItemsMap(filtered);
    }

    public void resetFilter() {
        setItemsMap(originalMap);
    }

    public void setItemsMap(Map<G, List<C>> map) {
        groupsChildrenMap = new LinkedHashMap<>(map);
        groups = new ArrayList<>(map.keySet());
        children = new ArrayList<>(map.values());
        notifyDataSetChanged();
    }
}
