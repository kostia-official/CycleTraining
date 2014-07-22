package com.kozzztya.cycletraining.db.datasources;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.entities.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * DataSource for sql view
 */

public abstract class DataSourceView<T extends Entity, V extends Entity> extends DataSource<T> {

    public DataSourceView(Context context) {
        super(context);
    }

    public List<V> selectView(String selection, String groupBy, String having, String orderBy) {
        Log.v(DBHelper.LOG_TAG, "select from " + getViewName());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db != null) {
            List<V> list = new ArrayList<>();
            Cursor cursor = db.query(getViewName(), getViewColumns(), selection, null, groupBy, having, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(entityViewFromCursor(cursor));
                } while (cursor.moveToNext());
            }
            return list;
        }
        return null;
    }

    public V getEntityView(String selection, String groupBy, String having, String orderBy) {
        Log.v(DBHelper.LOG_TAG, "get entity from " + getViewName());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db != null) {
            Cursor cursor = db.query(getViewName(), getViewColumns(), selection, null, groupBy, having, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                return entityViewFromCursor(cursor);
            }
        }
        return null;
    }

    public V getEntityView(long id) {
        String selection = COLUMN_ID + " = " + id;
        return getEntityView(selection, null, null, null);
    }

    public abstract String getViewName();

    public abstract String[] getViewColumns();

    public abstract V entityViewFromCursor(Cursor cursor);

}
