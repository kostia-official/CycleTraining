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

public class WeightRepsDateChart extends AbstractChart {

    public WeightRepsDateChart(Context context) {
        super(context);
    }

    @Override
    public View buildChartView(long exerciseId, String resultFunc, long beginDate) {
        String weightStr = mContext.getString(R.string.weight);

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        TimeSeries seriesDateWeight = new TimeSeries(weightStr);
        TimeSeries seriesDateWeightReps = new TimeSeries(weightStr +
                "+" + mContext.getString(R.string.reps_stat));

        String columnWeight = resultFunc + "(" + Sets.WEIGHT + ")";
        String columnReps = resultFunc + "(cast(" + Sets.REPS + " as integer))";
        String[] columns = new String[]{Trainings.DATE, columnWeight, columnReps};

        String[] args = new String[]{String.valueOf(exerciseId), DateUtils.sqlFormat(beginDate),
                DateUtils.sqlFormat(Calendar.getInstance().getTimeInMillis())};
        String groupBy = Trainings.DATE;
        Cursor cursor = selectChartData(columns, args, groupBy);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Date date = DateUtils.safeParse(cursor.getString(cursor.getColumnIndex(Trainings.DATE)));
                float weight = cursor.getFloat(cursor.getColumnIndex(columnWeight));
                int reps = cursor.getInt(cursor.getColumnIndex(columnReps));

                // Point values
                seriesDateWeight.add(date, weight);
                seriesDateWeightReps.add(date, weight + reps);

                // Point label values
                seriesDateWeight.addAnnotation(SetUtils.weightFormat(weight),
                        date.getTime(), weight + 1);
                seriesDateWeightReps.addAnnotation(String.valueOf(reps),
                        date.getTime(), weight + reps + 1);
            } while (cursor.moveToNext());
        } else {
            return null;
        }

        dataset.addSeries(seriesDateWeight);
        dataset.addSeries(seriesDateWeightReps);

        XYMultipleSeriesRenderer renderer = buildRenderer();
        buildSeriesRenderers(renderer, dataset.getSeriesCount());

        renderer.setYAxisMin(seriesDateWeight.getMinY() * 0.8);
        renderer.setYAxisMax(seriesDateWeightReps.getMaxY() * 1.2);
        setDateChartPadding(renderer, (long) seriesDateWeight.getMinX(), (long) seriesDateWeight.getMaxX());

        return ChartFactory.getTimeChartView(mContext, dataset, renderer, "dd MMM");
    }
}
