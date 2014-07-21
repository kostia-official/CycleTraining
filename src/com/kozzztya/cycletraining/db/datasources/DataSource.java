package com.kozzztya.cycletraining.db.datasources;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.entities.Entity;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class DataSource<T extends Entity> {
    public static String COLUMN_ID = "_id";

    protected Context context;

    public DataSource(Context context) {
        this.context = context;
    }

    public abstract void onCreate(SQLiteDatabase database);

    public abstract void onUpgrade(SQLiteDatabase database, int oldVersion,
                                   int newVersion);

    public long insert(T entity) {
        if (entity == null) return -1;
        Log.v(DBHelper.LOG_TAG, "insert into " + getTableName());
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        ContentValues values = getContentValues(entity);
        return db != null ? db.insert(getTableName(), null, values) : -1;
    }

    public boolean update(T entity) {
        if (entity == null) return false;
        Log.v(DBHelper.LOG_TAG, "update " + getTableName());
        ContentValues values = getContentValues(entity);
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        return db != null && db.update(getTableName(), values,
                COLUMN_ID + " = " + entity.getId(), null) != 0;
    }

    public boolean delete(long id) {
        Log.v(DBHelper.LOG_TAG, "delete from " + getTableName());
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        return db != null && db.delete(getTableName(), COLUMN_ID + " = " + id, null) != 0;
    }

    public boolean delete(String where) {
        Log.v(DBHelper.LOG_TAG, "delete from " + getTableName());
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        return db != null && db.delete(getTableName(), where, null) != 0;
    }

    public List<T> select(String selection, String groupBy, String having, String orderBy) {
        Log.v(DBHelper.LOG_TAG, "select from " + getTableName());
        SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
        if (db != null) {
            List<T> list = new ArrayList<>();
            Cursor cursor = db.query(getTableName(), getColumns(), selection, null, groupBy, having, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(entityFromCursor(cursor));
                } while (cursor.moveToNext());
            }
            return list;
        }
        return null;
    }

    public T getEntity(String selection, String groupBy, String having, String orderBy) {
        Log.v(DBHelper.LOG_TAG, "get entity from " + getTableName());
        SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
        if (db != null) {
            Cursor cursor = db.query(getTableName(), getColumns(), selection, null, groupBy, having, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                return entityFromCursor(cursor);
            }
        }
        return null;
    }

    public T getEntity(long id) {
        String selection = COLUMN_ID + " = " + id;
        return getEntity(selection, null, null, null);
    }

    protected void fillCoreData(SQLiteDatabase db) {
        Log.v(DBHelper.LOG_TAG, "filling core data into " + getTableName());

        try {
            XmlResourceParser xrp = context.getResources().getXml(R.xml.core_data);
            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                    if (xrp.getName().equals(getTableName())) {
                        ContentValues values = new ContentValues();

                        //Get column name and value
                        for (int i = 0; i < xrp.getAttributeCount(); i++) {
                            String name = xrp.getAttributeName(i);
                            String value = xrp.getAttributeValue(i);
                            //If value is string reference
                            if (xrp.getAttributeValue(i).contains("@"))
                                value = context.getResources().getString(xrp.getAttributeResourceValue(i, 0));

                            values.put(name, value);
                        }

                        db.insert(getTableName(), null, values);
                    }
                }
                xrp.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    protected void fullDelete(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS " + getTableName());
    }

    public abstract String getTableName();

    public abstract String[] getColumns();

    public abstract ContentValues getContentValues(T entity);

    public abstract T entityFromCursor(Cursor cursor);
}
