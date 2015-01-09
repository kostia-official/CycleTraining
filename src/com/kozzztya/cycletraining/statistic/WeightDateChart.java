package com.kozzztya.cycletraining.statistic;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.Sets;
import com.kozzztya.cycletraining.db.Trainings;
import com.kozzztya.cycletraining.utils.DateUtils;
import com.kozzztya.cycletraining.utils.SetUtils;

import org.achartengine.ChartFactory;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.sql.Date;
import java.util.Calendar;

public class WeightDateChart extends AbstractChart {

    public WeightDateChart(Context context) {
        super(context);
    }

    @Override
    public View buildChartView(long exerciseId, String resultFunc, long beginDate) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        TimeSeries seriesDateWeight = new TimeSeries(mContext.getString(R.string.weight));

        String columnWeight = resultFunc + "(" + Sets.WEIGHT + ")";
        String[] columns = new String[]{Trainings.DATE, columnWeight};
        String[] args = new String[]{String.valueOf(exerciseId), DateUtils.sqlFormat(beginDate),
                DateUtils.sqlFormat(Calendar.getInstance().getTimeInMillis())};
        String groupBy = Trainings.DATE;
        Cursor cursor = selectChartData(columns, args, groupBy);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Date date = DateUtils.safeParse(cursor.getString(cursor.getColumnIndex(Trainings.DATE)));
                float weight = cursor.getFloat(cursor.getColumnIndex(columnWeight));

                seriesDateWeight.add(date, weight);     // point values
                seriesDateWeight.addAnnotation(SetUtils.weightFormat(weight),
                        date.getTime(), weight + 1);    // x, y coordinates of point label
            } while (cursor.moveToNext());
        } else {
            return null;
        }

        dataset.addSeries(seriesDateWeight);

        XYMultipleSeriesRenderer renderer = buildRenderer();
        buildSeriesRenderers(renderer, dataset.getSeriesCount());

        renderer.setYAxisMin(seriesDateWeight.getMinY() * 0.8);
        renderer.setYAxisMax(seriesDateWeight.getMaxY() * 1.2);
        setDateChartPadding(renderer, (long) seriesDateWeight.getMinX(), (long) seriesDateWeight.getMaxX());

        return ChartFactory.getTimeChartView(mContext, dataset, renderer, "dd MMM");
    }
}
