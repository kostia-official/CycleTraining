package com.kozzztya.cycletraining.trainingcreate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.kozzztya.cycletraining.MyExpListAdapter;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.entities.Program;
import com.kozzztya.cycletraining.db.entities.ProgramView;
import com.kozzztya.cycletraining.db.entities.Purpose;

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

        return super.getGroupView(groupPosition, isExpanded, convertView, parent);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_item, null);

        Program program = getChild(groupPosition, childPosition);
        TextView textView = (TextView) convertView.findViewById(R.id.title);
        textView.setText(program.toString());

        return super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
    }

}
