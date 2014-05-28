package com.kozzztya.cycletraining.db.helpers;

import android.database.Cursor;

import java.util.List;

public interface TableHelper<T> {
    public static String COLUMN_ID = "_id";

//    public static void onCreate(SQLiteDatabase database){}
//
//    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){}

    public long insert(T entity);

    public T getEntity(long id);

    public boolean update(T entity);

    public boolean delete(long id);

    public List<T> select(String selection, String groupBy, String having, String orderBy);

    public String[] getColumns();

    public List<T> entityFromCursor(Cursor cursor);
}
