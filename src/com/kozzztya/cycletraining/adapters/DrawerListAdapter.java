package com.kozzztya.cycletraining.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.kozzztya.cycletraining.R;

public class DrawerListAdapter extends ArrayAdapter<String> {

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
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        TextView text = (TextView) view.findViewById(R.id.title);
        text.setText(getItem(position));

        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        icon.setImageResource(icons[position]);

        return view;
    }
}
