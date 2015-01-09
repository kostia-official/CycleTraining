package com.kozzztya.cycletraining.statistic;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DatabaseHelper;
import com.kozzztya.cycletraining.db.Mesocycles;
import com.kozzztya.cycletraining.db.Programs;
import com.kozzztya.cycletraining.db.Sets;
import com.kozzztya.cycletraining.db.TrainingJournal;
import com.kozzztya.cycletraining.db.Trainings;
import com.kozzztya.cycletraining.utils.DateUtils;

import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public abstract class AbstractChart {

    protected Context mContext;
    protected int[] mChartColors;
    private final XYMultipleSeriesRenderer mRenderer;

    public AbstractChart(Context context) {
        mContext = context;
        mChartColors = context.getResources().getIntArray(R.array.chart_colors);
        mRenderer = new XYMultipleSeriesRenderer();
    }

    /**
     * Build chart for the selected parameters.
     *
     * @param exerciseId exercise of the chart data.
     * @param resultFunc an SQL function of the chart data.
     * @param beginDate  begin date of the chart data.
     * @return View of chart.
     */
    public abstract View buildChartView(long exerciseId, String resultFunc, long beginDate);

    protected Cursor selectChartData(String[] columns, String[] args, String groupBy) {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext).getReadableDatabase();
        String tables = Sets.TABLE_NAME + " s, " + Trainings.TABLE_NAME + " t, " + Mesocycles.TABLE_NAME +
                " m, " + TrainingJournal.TABLE_NAME + " tj, " + Programs.TABLE_NAME + " p";
        String where = String.format("s." + Sets.TRAINING + " = t._id " +
                "AND tj." + TrainingJournal.MESOCYCLE + " = m._id " +
                "AND tj." + TrainingJournal.PROGRAM + " = p._id " +
                "AND t." + Trainings.IS_DONE + " = 1 " +
                "AND t." + Trainings.MESOCYCLE + " = m._id " +
                "AND tj." + TrainingJournal.EXERCISE + " = %s " +
                "AND t." + Trainings.DATE + " >= %s  " +
                "AND t." + Trainings.DATE + " <= %s", args);
        String orderBy = Trainings.DATE;
        String query = SQLiteQueryBuilder.buildQueryString(false, tables, columns, where, groupBy, null, orderBy, null);
        return db.rawQuery(query, null);
    }

    /**
     * Add padding to date chart.
     */
    protected void setDateChartPadding(XYMultipleSeriesRenderer renderer, long minDate, long maxDate) {
        minDate = DateUtils.addDays(minDate, -1);
        maxDate = DateUtils.addDays(maxDate, 1);

        long minScreenDate = DateUtils.addDays(-60);
        if (minDate < minScreenDate) {
            minDate = minScreenDate;
        }

        renderer.setXAxisMin(minDate);
        renderer.setXAxisMax(maxDate);
    }

    /**
     * Set the stile for the chart series.
     *
     * @param seriesCount count of series must be equal to dataset count.
     */
    protected void buildSeriesRenderers(XYMultipleSeriesRenderer renderer, int seriesCount) {
        for (int i = 0; i < seriesCount; i++) {
            int color = getChartColor(i);

            XYSeriesRenderer seriesRenderers = new XYSeriesRenderer();
            seriesRenderers.setColor(color);
            seriesRenderers.setPointStyle(PointStyle.CIRCLE);
            seriesRenderers.setFillPoints(true);

            seriesRenderers.setAnnotationsColor(color);
            seriesRenderers.setAnnotationsTextSize(15);
            seriesRenderers.setAnnotationsTextAlign(Paint.Align.CENTER);

            renderer.addSeriesRenderer(seriesRenderers);
        }
    }

    /**
     * Set the stile for the chart.
     */
    protected XYMultipleSeriesRenderer buildRenderer() {
        int colorDarkGray = mContext.getResources().getColor(R.color.dark_gray);
        int colorBackgroundLight = mContext.getResources().getColor(R.color.background_theme_light);

        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(colorBackgroundLight);

        mRenderer.setAxesColor(colorDarkGray);

        mRenderer.setXLabelsColor(colorDarkGray);
        mRenderer.setYLabelsColor(0, colorDarkGray);
        mRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        mRenderer.setYLabelsPadding(3);
        mRenderer.setXRoundedLabels(true);
        mRenderer.setLabelsTextSize(15);
        mRenderer.setXLabels(mRenderer.getXLabels() * 2);
        mRenderer.setYLabels(mRenderer.getYLabels() * 2);

        mRenderer.setLegendTextSize(18);
        mRenderer.setFitLegend(true);

        mRenderer.setMargins(new int[]{0, 30, 0, 0}); //margin left for 3 digits
        mRenderer.setMarginsColor(colorBackgroundLight);

        mRenderer.setShowGrid(true);
        return mRenderer;
    }

    /**
     * Get a unique color for the chart series.
     */
    private int getChartColor(int index) {
        int color = mChartColors[index % mChartColors.length];
        if (index >= mChartColors.length) {
            color = getDarkerColor(color, index);
        }
        return color;
    }

    /**
     * Get a darker color if the current set of colors is over.
     *
     * @param color  prior color.
     * @param amount amount of the darkness.
     * @return darker color.
     */
    protected static int getDarkerColor(int color, float amount) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= ((amount % 5) + 5) / 10f;
        return Color.HSVToColor(hsv);
    }
}