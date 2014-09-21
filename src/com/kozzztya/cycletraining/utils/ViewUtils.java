package com.kozzztya.cycletraining.utils;

import android.content.Context;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.kozzztya.cycletraining.R;

import java.sql.Date;

public class ViewUtils {

    public static void setListViewCardStyle(ListView listView, Context context) {
        int cardMargin = context.getResources().getDimensionPixelSize(R.dimen.card_margin);
        listView.setPadding(cardMargin, cardMargin, cardMargin, cardMargin);
        listView.setClipToPadding(false);
        listView.setDividerHeight(cardMargin);
        listView.setDrawSelectorOnTop(false);
        listView.setSelector(context.getResources().getDrawable(android.R.color.transparent));
    }

    public static void setExpListViewCardStyle(ExpandableListView expListView, Context context) {
        int cardMargin = context.getResources().getDimensionPixelSize(R.dimen.card_margin);
        expListView.setPadding(cardMargin, 0, cardMargin, cardMargin);
        expListView.setClipToPadding(false);
        expListView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        expListView.setDividerHeight(0);
        expListView.setDrawSelectorOnTop(false);
        expListView.setSelector(context.getResources().getDrawable(android.R.color.transparent));
    }

    public static SimpleCursorAdapter getSimpleSpinnerCursorAdapter(String[] from, Context context) {
        int[] to = new int[]{android.R.id.text1};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(context,
                android.R.layout.simple_spinner_item, null, from, to, 0);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    public static void setDoneDrawable(TextView textView, boolean isDone, Date date) {
        switch (DateUtils.getTrainingStatus(date, isDone)) {
            case DateUtils.STATUS_DONE:
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_true, 0);
                break;
            case DateUtils.STATUS_MISSED:
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_false, 0);
                break;
            case DateUtils.STATUS_IN_PLANS:
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                break;
        }
    }
}
