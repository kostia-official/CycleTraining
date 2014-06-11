package com.kozzztya.cycletraining;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.utils.MyDateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

public class TrainingWeekExpListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private LinkedHashMap<Integer, List<TrainingView>> groups;
    private ArrayList<List<TrainingView>> childs;

    public TrainingWeekExpListAdapter(Context context, LinkedHashMap<Integer, List<TrainingView>> groups) {
        this.context = context;
        this.groups = groups;
        this.childs = new ArrayList<>(groups.values());
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int pos) {
        return childs.get(pos).size();
    }

    @Override
    public Object getGroup(int pos) {
        return groups.keySet().toArray()[pos];
    }

    @Override
    public Object getChild(int groupPos, int childPos) {
        return childs.get(groupPos).get(childPos);
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
    public View getGroupView(int pos, boolean isExpanded, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.exp_list_group, null);

        TextView title = (TextView) view.findViewById(R.id.textViewGroupDayOfWeek);
        String[] daysOfWeek = context.getResources().getStringArray(R.array.days_of_week);
        int dayNum = (int) getGroup(pos);
        title.setText(daysOfWeek[dayNum]);

        ImageView done = (ImageView) view.findViewById(R.id.imageViewGroupDone);
        List<TrainingView> trainings = childs.get(pos);
        boolean isDone = true;
        for (TrainingView t : trainings) {
            if (!t.isDone()) {
                isDone = false;
                break;
            }
        }
        setDoneIcon(isDone, done, (int) getGroup(pos));
        return view;
    }

    @Override
    public View getChildView(int groupPos, int childPos, boolean isExpanded, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.exp_list_child, null);

        TrainingView child = childs.get(groupPos).get(childPos);
        TextView title = (TextView) view.findViewById(R.id.textViewChildExercise);
        title.setText(child.getExercise());

        ImageView done = (ImageView) view.findViewById(R.id.imageViewChildDone);
        setDoneIcon(child.isDone(), done, (int) getGroup(groupPos));
        return view;
    }

    private void setDoneIcon(boolean isDone, ImageView done, int dayNum) {
        //Выполнено
        if (isDone) {
            done.setImageResource(R.drawable.done_true);
        } else {
            Calendar calendar = Calendar.getInstance();
            int curDayNum = MyDateUtils.dayOfWeekNum(calendar.get(Calendar.DAY_OF_WEEK), 2);
            //Еще не выполнено
            if (dayNum >= curDayNum)
                done.setVisibility(View.INVISIBLE);
                //Не выполнено
            else
                done.setImageResource(R.drawable.done_false);
        }
    }

}
