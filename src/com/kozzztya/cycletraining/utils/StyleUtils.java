package com.kozzztya.cycletraining.utils;

import android.content.Context;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.kozzztya.cycletraining.R;

public class StyleUtils {

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
}
