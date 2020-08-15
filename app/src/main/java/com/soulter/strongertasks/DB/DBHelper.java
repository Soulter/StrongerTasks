package com.soulter.strongertasks.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2018/7/10.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TaskData.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TASK_DATA_TABLE = "CREATE TABLE "+ DBContruct.TaskDataEntry.TABLE_NAME
                +" ("
                + DBContruct.TaskDataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DBContruct.TaskDataEntry.COLUMN_TASK_DATA_STRING+ " TEXT NOT NULL, "
                + DBContruct.TaskDataEntry.COLUMN_TIME_DATA_STRING+" TEXT NOT NULL, "
                + DBContruct.TaskDataEntry.COLUMN_TIMESTAMP+" TIME DEFAULT CURRENT_TIMESTAMP"
                +");";
        final String SQL_CREATE_TASKED_DATA_TABLE = "CREATE TABLE "+ DBContruct.TaskedDataEntry.TABLE_NAME
                +" ("
                + DBContruct.TaskedDataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DBContruct.TaskedDataEntry.COLUMN_TASKED_DATA_STRING+ " TEXT NOT NULL, "
                + DBContruct.TaskedDataEntry.COLUMN_TIMED_DATA_STRING+" TEXT NOT NULL, "
                + DBContruct.TaskedDataEntry.COLUMN_TIMESTAMP+" TIME DEFAULT CURRENT_TIMESTAMP"
                +");";
        sqLiteDatabase.execSQL(SQL_CREATE_TASK_DATA_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TASKED_DATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ DBContruct.TaskDataEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ DBContruct.TaskedDataEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
