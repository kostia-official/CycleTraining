package com.kozzztya.cycletraining.db.datasources;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.entities.Entity;

import java.util.ArrayList;
import java.util.List;

public abstract class DataSource<T extends Entity> {
    public static String COLUMN_ID = "_id";

    protected DBHelper dbHelper;

    public DataSource(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public abstract void onCreate(SQLiteDatabase database);

    public abstract void onUpgrade(SQLiteDatabase database, int oldVersion,
                                   int newVersion);

    public long insert(T entity) {
        if (entity == null) return -1;
        Log.v(DBHelper.LOG_TAG, "insert into " + getTableName());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getContentValues(entity);
        return db != null ? db.insert(getTableName(), null, values) : -1;
    }

    public boolean update(T entity) {
        if (entity == null) return false;
        Log.v(DBHelper.LOG_TAG, "update " + getTableName());
        ContentValues values = getContentValues(entity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db != null && db.update(getTableName(), values,
                COLUMN_ID + " = " + entity.getId(), null) != 0;
    }

    public boolean delete(long id) {
        Log.v(DBHelper.LOG_TAG, "delete from " + getTableName());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db != null && db.delete(getTableName(), COLUMN_ID + " = " + id, null) != 0;
    }

    public boolean delete(String where) {
        Log.v(DBHelper.LOG_TAG, "delete from " + getTableName());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db != null && db.delete(getTableName(), where, null) != 0;
    }

    public List<T> select(String selection, String groupBy, String having, String orderBy) {
        Log.v(DBHelper.LOG_TAG, "select from " + getTableName());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
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

    public T getEntity(long id) {
        Log.v(DBHelper.LOG_TAG, "get entity from " + getTableName());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String where = COLUMN_ID + " = " + id;
        if (db != null) {
            Cursor cursor = db.query(getTableName(), getColumns(), where, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return entityFromCursor(cursor);
            }
        }
        return null;
    }

    public abstract String getTableName();

    public abstract String[] getColumns();

    public abstract ContentValues getContentValues(T entity);

    public abstract T entityFromCursor(Cursor cursor);
}
