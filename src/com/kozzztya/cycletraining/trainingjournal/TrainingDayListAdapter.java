package com.kozzztya.cycletraining.trainingjournal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.custom.MyHorizontalScrollView;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.trainingcreate.SetsTableAdapter;
import com.kozzztya.cycletraining.utils.DateUtils;

import java.util.LinkedHashMap;
import java.util.List;

import static com.kozzztya.cycletraining.custom.MyHorizontalScrollView.OnScrollViewClickListener;

public class TrainingDayListAdapter extends SetsTableAdapter {

    private Context mContext;
    private LinkedHashMap<TrainingView, List<Set>> mTrainingsSets;
    private OnScrollViewClickListener onScrollViewClickListener;

    public TrainingDayListAdapter(Context context, LinkedHashMap<TrainingView, List<Set>> trainingsSets) {
        super(context);
        mContext = context;
        mTrainingsSets = trainingsSets;
    }

    @Override
    public int getCount() {
        return mTrainingsSets.size();
    }

    @Override
    public TrainingView getItem(int position) {
        return (TrainingView) mTrainingsSets.keySet().toArray()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<Set> getSets(int position) {
        return mTrainingsSets.get(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(R.layout.training_list_item, parent, false);

        setTrainingTitle(position, convertView);
        buildSetsTable(position, convertView);

        return convertView;
    }

    protected void setTrainingTitle(int position, View convertView) {
        TrainingView training = getItem(position);
        TextView textViewTraining = (TextView) convertView.findViewById(R.id.textViewTrainingTitle);
        textViewTraining.setText(training.getExercise());

        if (onScrollViewClickListener != null) {
            MyHorizontalScrollView scrollView = (MyHorizontalScrollView) convertView.findViewById(R.id.horizontalScrollView);
            scrollView.configure(onScrollViewClickListener, position);
        }

        ImageView doneIcon = (ImageView) convertView.findViewById(R.id.imageViewDoneIcon);
        switch (DateUtils.getTrainingStatus(training.getDate(), training.isDone())) {
            case DateUtils.STATUS_DONE:
                doneIcon.setImageResource(R.drawable.ic_done_true);
                break;
            case DateUtils.STATUS_IN_PLANS:
                doneIcon.setVisibility(View.GONE);
                break;
            case DateUtils.STATUS_MISSED:
                doneIcon.setImageResource(R.drawable.ic_done_false);
                break;
        }

        String comment = training.getComment();
        if (comment != null && comment.length() != 0) {
            TextView textViewComment = (TextView) convertView.findViewById(R.id.textViewComment);
            textViewComment.setText(comment);
            textViewComment.setVisibility(View.VISIBLE);
        }
    }

    public void setOnScrollViewClickListener(OnScrollViewClickListener onScrollViewClickListener) {
        this.onScrollViewClickListener = onScrollViewClickListener;
    }

}
