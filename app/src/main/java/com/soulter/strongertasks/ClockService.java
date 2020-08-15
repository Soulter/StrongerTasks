package com.soulter.strongertasks;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.soulter.strongertasks.DB.DBContruct;
import com.soulter.strongertasks.DB.DBDataUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ClockService extends Service {

    public final static String TASK_CONTENT_EXTRA_TAG = "taskContent";
    public final static String TASK_TIME_EXTRA_TAG = "taskTime";
    public final static String TASK_TIME_LONG_EXTRA_TAG = "taskTimeLong";

    DBDataUtils dbDataUtils = new DBDataUtils();


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("lwl","onStartCommand");



        Cursor cursor = dbDataUtils.getTaskDB(ClockService.this);
        AlarmManager alarmManager = (AlarmManager) ClockService.this.getSystemService(Context.ALARM_SERVICE);
        int type = AlarmManager.RTC_WAKEUP;
        int i;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dateTaskTime;
        long taskTimeLong;
        String taskContent;
        String taskTimeContent;
        Calendar calendarInstance = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();


        Log.v("lwl",String.valueOf(calendar.getTimeInMillis()));
        Intent intent2 = new Intent(ClockService.this, AlarmService.class);

        if (cursor.moveToFirst()){
            for (i = 0;i<cursor.getCount();i++){

                try{
                    //处理时间：先从数据库中取出时间  转换格式1 转换格式2
                    dateTaskTime = sdf.parse(cursor.getString(cursor.getColumnIndex(DBContruct.TaskDataEntry.COLUMN_TIME_DATA_STRING)));
                    taskContent = cursor.getString(cursor.getColumnIndex(DBContruct.TaskDataEntry.COLUMN_TASK_DATA_STRING));

                    calendar.setTime(dateTaskTime);
                    //时间之前的不用设置闹钟
                    if (calendar.before(calendarInstance))
                        continue;
                    taskTimeLong = calendar.getTimeInMillis();
                    taskTimeContent = sdf2.format(dateTaskTime);

                    intent2.putExtra(TASK_TIME_EXTRA_TAG,taskTimeContent)
                            .putExtra(TASK_CONTENT_EXTRA_TAG,taskContent)
                            .putExtra("feature_tag",1);

                    long id = cursor.getLong(cursor.getColumnIndex(DBContruct.TaskDataEntry._ID));

                    Log.v("lwl","cursor.getColumCount:"+cursor.getCount());
                    PendingIntent pi = PendingIntent.getService(ClockService.this,(int)id+2333,intent2,0);
                    alarmManager.setExact(type, taskTimeLong, pi);

                    //添加通知
                    NotificationUtil notificationUtil = new NotificationUtil();
                    notificationUtil.createNotificationChannel("StrongerTask", "StrongerTask", NotificationManager.IMPORTANCE_MAX);
                    notificationUtil.sendNotification(id,taskTimeLong,taskTimeContent, taskContent, "StrongerTask");

                    Log.v("lwl",dateTaskTime.toString());
                    Log.v("lwl",String.valueOf(calendar.getTimeInMillis()));

                }catch (Exception e){
                    e.printStackTrace();
                }
                cursor.moveToNext();
            }
        }


        return START_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        dbDataUtils.closeDB();
        Intent localIntent = new Intent();

        localIntent.setClass(this, ClockService.class); //销毁时重新启动Service

        this.startService(localIntent);
        super.onDestroy();
    }

    public class NotificationUtil {
        public void sendNotification(long id_sql, long longTaskTime, String taskTimeContrnt, String taskContent, String notificationChannel) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ClockService.this, notificationChannel).setSmallIcon(R.drawable.ic_launcher_background)
                    // 设置图标
                    .setSmallIcon(R.drawable.ic_launcher_web)
                    .setContentTitle(taskContent)
                    .setContentText(taskTimeContrnt)
                    .setTicker("\n" + taskContent)
                    .setOngoing(true)
                    .setShowWhen(true)
                    .setAutoCancel(false)
                    .setUsesChronometer(true)
                    .setWhen(longTaskTime)
                    .setFullScreenIntent(null, true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    //              .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setPriority(Notification.PRIORITY_MAX);

            Log.v("lwl",String.valueOf(System.currentTimeMillis())+String.valueOf(longTaskTime));



            //加按钮


            Intent yesIntent = new Intent(ClockService.this, MainActivity.class);
            yesIntent.putExtra("channel_id",(int)id_sql+233)
                    .putExtra("alarm_id",(int)id_sql+2333)
                    .putExtra("feature_tag",2);
            PendingIntent yesPendingIntent = PendingIntent.getActivity(ClockService.this,999,yesIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action action = new NotificationCompat.Action(
                    R.drawable.ic_launcher_web,
                    "完成",yesPendingIntent);
            mBuilder.addAction(action);

            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL|Notification.FLAG_ONGOING_EVENT;
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify((int)id_sql+233, notification);
        }
        @TargetApi(Build.VERSION_CODES.O)
        public void createNotificationChannel(String channelId, String channelName, int importance) {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
                channel.setShowBadge(true);
                NotificationManager notificationManager = (NotificationManager) ClockService.this.getSystemService(
                        NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
