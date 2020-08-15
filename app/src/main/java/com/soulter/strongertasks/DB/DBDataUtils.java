package com.soulter.strongertasks.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBDataUtils {

    private SQLiteDatabase mDb;

    public Long addDBData(Context context, String content, String timeStr, int tag){

        DBHelper dbHelper = new DBHelper(context);
        mDb = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if (tag == 1){
            cv.put(DBContruct.TaskDataEntry.COLUMN_TASK_DATA_STRING,content);
            cv.put(DBContruct.TaskDataEntry.COLUMN_TIME_DATA_STRING,timeStr);
            return mDb.insert(DBContruct.TaskDataEntry.TABLE_NAME,null,cv);
        }else{
            cv.put(DBContruct.TaskedDataEntry.COLUMN_TASKED_DATA_STRING,content);
            cv.put(DBContruct.TaskedDataEntry.COLUMN_TIMED_DATA_STRING,timeStr);
            return mDb.insert(DBContruct.TaskedDataEntry.TABLE_NAME,null,cv);
        }



    }

    public Cursor getTaskDB(Context context){

        DBHelper dbHelper = new DBHelper(context);
        mDb = dbHelper.getWritableDatabase();
        Cursor cursor = mDb.query(
                DBContruct.TaskDataEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                DBContruct.TaskDataEntry.COLUMN_TIMESTAMP+" desc");
//        mDb.close();//关闭数据库
        return cursor;
    }

    public Cursor getTaskedDB(Context context){

        DBHelper dbHelper = new DBHelper(context);
        mDb = dbHelper.getWritableDatabase();
        Cursor cursor = mDb.query(
                DBContruct.TaskedDataEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                DBContruct.TaskedDataEntry.COLUMN_TIMESTAMP+" desc");
//        mDb.close();//关闭数据库
        return cursor;
    }

    public boolean delTask(Context context,long id,int tag){

        DBHelper dbHelper = new DBHelper(context);
        mDb = dbHelper.getWritableDatabase();
        if (tag == 1)
            return mDb.delete(DBContruct.TaskDataEntry.TABLE_NAME,
                    DBContruct.TaskDataEntry._ID + "=" + id,null) > 0;
        else
            return mDb.delete(DBContruct.TaskedDataEntry.TABLE_NAME,
                    DBContruct.TaskedDataEntry._ID + "=" + id,null) > 0;

    }

    public void closeDB(){
        mDb.close();
    }
}
