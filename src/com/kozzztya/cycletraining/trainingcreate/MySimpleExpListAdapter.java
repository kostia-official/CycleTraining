package com.kozzztya.cycletraining.trainingcreate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kozzztya.customview.CardView;
import com.kozzztya.cycletraining.MyExpListAdapter;
import com.kozzztya.cycletraining.R;

import java.util.List;
import java.util.Map;

public class MySimpleExpListAdapter<G, C> extends MyExpListAdapter<G, C> {

    private Context mContext;

    public MySimpleExpListAdapter(Context context, Map<G, List<C>> groups) {
        super(groups);
        mContext = context;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(R.layout.exp_list_item, parent, false);

        CardView cardView = (CardView) convertView.findViewById(R.id.card);
        imitateCardGroup(isExpanded, cardView);

        G group = getGroup(groupPosition);

        TextView textView = (TextView) convertView.findViewById(R.id.title);
        textView.setText(group.toString());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(R.layout.list_item, parent, false);

        CardView cardView = (CardView) convertView.findViewById(R.id.card);
        imitateCardChild(isLastChild, cardView);

        C child = getChild(groupPosition, childPosition);

        TextView textView = (TextView) convertView.findViewById(R.id.title);
        textView.setText(child.toString());

        return convertView;
    }

}
