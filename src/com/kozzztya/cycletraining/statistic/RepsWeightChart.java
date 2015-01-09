package com.kozzztya.cycletraining.statistic;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import com.kozzztya.cycletraining.db.Programs;
import com.kozzztya.cycletraining.db.Sets;
import com.kozzztya.cycletraining.db.TrainingJournal;
import com.kozzztya.cycletraining.utils.DateUtils;
import com.kozzztya.cycletraining.utils.SetUtils;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.model.XYValueSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class RepsWeightChart extends AbstractChart {

    public RepsWeightChart(Context context) {
        super(context);
    }

    @Override
    public View buildChartView(long exerciseId, String resultFunc, long minDate) {
        String columnReps = resultFunc + "(cast(" + Sets.REPS + " as integer))";
        String[] columns = new String[]{
                "p." + Programs.DISPLAY_NAME + " " + TrainingJournal.PROGRAM,
                TrainingJournal.BEGIN_DATE,
                Sets.WEIGHT,
                columnReps};
        String[] args = new String[]{String.valueOf(exerciseId), DateUtils.sqlFormat(minDate),
                DateUtils.sqlFormat(Calendar.getInstance().getTimeInMillis())};
        String groupBy = Sets.WEIGHT + ", " + TrainingJournal.BEGIN_DATE + ", "
                + TrainingJournal.PROGRAM;
        Cursor cursor = selectChartData(columns, args, groupBy);

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        Map<String, XYValueSeries> seriesMap = new LinkedHashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        double xAxisMin = Double.MAX_VALUE, xAxisMax = Double.MIN_VALUE;
        double yAxisMin = Double.MAX_VALUE, yAxisMax = Double.MIN_VALUE;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String program = cursor.getString(cursor.getColumnIndex(TrainingJournal.PROGRAM));
                String beginDate = dateFormat.format(Date.valueOf(cursor.getString(
                        cursor.getColumnIndex(TrainingJournal.BEGIN_DATE))));
                String title = program + ", " + beginDate;

                if (seriesMap.containsKey(title)) {
                    XYValueSeries series = seriesMap.get(title);
                    float weight = cursor.getFloat(cursor.getColumnIndex(Sets.WEIGHT));
                    int reps = cursor.getInt(cursor.getColumnIndex(columnReps));

                    series.add(weight, reps);
                    series.addAnnotation(SetUtils.weightFormat(weight),
                            weight, reps + 0.2); // Add bottom padding of annotation

                    // Get min & max values of all series
                    if (weight < xAxisMin) xAxisMin = weight;
                    if (weight > xAxisMax) xAxisMax = weight;
                    if (reps < yAxisMin) yAxisMin = reps;
                    if (reps > yAxisMax) yAxisMax = reps;
                } else {
                    XYValueSeries series = new XYValueSeries(title);
                    seriesMap.put(title, series);
                }
            } while (cursor.moveToNext());
        } else {
            return null;
        }
        dataset.addAllSeries(new ArrayList<XYSeries>(seriesMap.values()));

        XYMultipleSeriesRenderer renderer = buildRenderer();
        buildSeriesRenderers(renderer, dataset.getSeriesCount());

        renderer.setXAxisMin(xAxisMin * 0.8);
        renderer.setXAxisMax(xAxisMax * 1.2);

        yAxisMin *= 0.8;
        yAxisMax *= 1.2;
        renderer.setYAxisMin(yAxisMin);
        renderer.setYAxisMax(yAxisMax);
        renderer.setYLabels((int) (yAxisMax - yAxisMin) + 1); // with up round

        renderer.setMargins(new int[]{0, 20, 0, 0}); // margin left for 2 digits of the reps

        return ChartFactory.getLineChartView(mContext, dataset, renderer);
    }
}