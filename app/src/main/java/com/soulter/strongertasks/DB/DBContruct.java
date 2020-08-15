package com.soulter.strongertasks.DB;

import android.provider.BaseColumns;

public class DBContruct {
    public static final class TaskDataEntry implements BaseColumns {
        public static final String TABLE_NAME = "taskdata";
        public static final String COLUMN_TASK_DATA_STRING = "taskDataString";
        public static final String COLUMN_TIME_DATA_STRING = "taskTimeDataString";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
    public static final class TaskedDataEntry implements BaseColumns {
        public static final String TABLE_NAME = "taskeddata";
        public static final String COLUMN_TASKED_DATA_STRING = "taskDataString";
        public static final String COLUMN_TIMED_DATA_STRING = "taskTimeDataString";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}