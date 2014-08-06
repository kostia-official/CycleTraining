package com.kozzztya.cycletraining.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.entities.Program;
import com.kozzztya.cycletraining.db.entities.ProgramView;
import com.kozzztya.cycletraining.db.entities.Purpose;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PurposeProgramsAdapter extends MyExpListAdapter<Purpose, ProgramView> {

    private Context context;

    public PurposeProgramsAdapter(Context context, Map<Purpose, List<ProgramView>> groups) {
        super(groups);
        this.context = context;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.exp_list_item, null);

        TextView textView = (TextView) convertView.findViewById(R.id.title);
        Purpose purpose = getGroup(groupPosition);
        textView.setText(purpose.getName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_item, null);

        Program program = getChild(groupPosition, childPosition);
        TextView textView = (TextView) convertView.findViewById(R.id.title);
        textView.setText(program.toString());

        return convertView;
    }

    public void filterByWeeks(int weeks) {
        Map<Purpose, List<ProgramView>> filtered = new LinkedHashMap<>();
        for (int groupPos = 0; groupPos < getGroupCount(); groupPos++) {
            Purpose group = getGroup(groupPos);

            for (int childPos = 0; childPos < getChildrenCount(groupPos); childPos++) {
                ProgramView child = getChild(groupPos, childPos);

                if (child.getWeeks() == weeks) {
                    if (!filtered.containsKey(group))
                        filtered.put(group, new ArrayList<ProgramView>());
                    filtered.get(group).add(child);
                }
            }
        }
        groups.clear();
        groups.putAll(filtered);
        notifyDataSetChanged();
    }

    public void filterByTrainingsInWeeks(int trainingsInWeek) {
        Map<Purpose, List<ProgramView>> filtered = new LinkedHashMap<>();
        for (int groupPos = 0; groupPos < getGroupCount(); groupPos++) {
            Purpose group = getGroup(groupPos);

            for (int childPos = 0; childPos < getChildrenCount(groupPos); childPos++) {
                ProgramView child = getChild(groupPos, childPos);

                if (child.getTrainingsInWeek() == trainingsInWeek) {
                    if (!filtered.containsKey(group))
                        filtered.put(group, new ArrayList<ProgramView>());
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
