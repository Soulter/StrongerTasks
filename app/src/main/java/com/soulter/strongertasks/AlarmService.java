package com.soulter.strongertasks;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import static com.soulter.strongertasks.ClockService.TASK_CONTENT_EXTRA_TAG;
import static com.soulter.strongertasks.ClockService.TASK_TIME_EXTRA_TAG;

public class AlarmService extends Service {

    private LinearLayout mainWinLayout;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {




        int featureTag  = intent.getIntExtra("feature_tag",0);
        if (featureTag == 1){
            final String taskContent = intent.getStringExtra(TASK_CONTENT_EXTRA_TAG);
            final String taskTimeContent = intent.getStringExtra(TASK_TIME_EXTRA_TAG);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            Log.v("lwl", taskContent);
            //方式一 吐司提示 -----
            if (prefs.getBoolean("feature_toast", true)) {
                Toast.makeText(this, taskContent, Toast.LENGTH_LONG).show();
            }
            //方式二 壁纸提示
            //方式三 悬浮窗提示
            if (prefs.getBoolean("feature_window", true)) {
//                floatWindowUtil(taskContent,taskTimeContent);
                //在 Service 中创建全局变量 mHandler
                Handler mHandler;
//在 Service 生命周期方法 onCreate() 中初始化 mHandler
                mHandler = new Handler(Looper.getMainLooper());
//在子线程中想要 Toast 的地方添加如下
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //show dialog
                        justShowDialog(taskContent,taskTimeContent);
                    }
                });
            }
            //方式四 通知提示 ---
            if (prefs.getBoolean("feature_announcer", true)) {
//            NotificationUtil notificationUtil = new NotificationUtil();
//            notificationUtil.createNotificationChannel("StrongerTask", "StrongerTask", NotificationManager.IMPORTANCE_MAX);
//            notificationUtil.sendNotification(taskTimeContent, taskContent, "StrongerTask");

            }

            //方式五 震动 --
            if (prefs.getBoolean("feature_vibrator",true)){
                int vTime = 1000;
                try{
                    vTime = Integer.parseInt(prefs.getString("vibrating_time","1000"));
                }catch (Exception e){
                    e.printStackTrace();
                }
                Vibrator vibrator = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(vTime);
            }
        }else if (featureTag == 2){
            //
            //取消待办。
            //




        }

        stopSelf();


        return super.onStartCommand(intent, flags, startId);

    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void wallPaperUtil(){

//        Bitmap wpBmp = Bitmap.createBitmap(wpBmpX,wpBmpY,Bitmap.Config.ARGB_8888);
    }

    public void floatWindowUtil(String taskContent,String taskTime){

//        LayoutInflater inflater = LayoutInflater.from(this);
//        mainWinLayout = (LinearLayout) inflater.inflate(R.layout.window_layout, null);
//        TextView taskContentDisp = (TextView)mainWinLayout.findViewById(R.id.window_tv_task_disp);
//        TextView taskTimeDisp = (TextView)mainWinLayout.findViewById(R.id.window_tv_task_time_disp);
//        taskContentDisp.setText(taskContent);
//        taskTimeDisp.setText(taskTime);
//        final WindowManager wm = (WindowManager)getApplicationContext().getSystemService(WINDOW_SERVICE);
//        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
//        if(Build.VERSION.SDK_INT >= 26)
//            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        else
//            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//
//        params.format= PixelFormat.TRANSPARENT;
//        params.flags=WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
////        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        params.width = WindowManager.LayoutParams.MATCH_PARENT;
//        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        params.gravity = Gravity.CENTER;
//
//        wm.addView(mainWinLayout, params);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                wm.removeView(mainWinLayout);
//            }
//        },5000);
//

    }


    private void justShowDialog(String taskContent,String taskTime) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext())
                .setIcon(R.drawable.ic_launcher_web)
                .setTitle(taskTime)
                .setMessage(taskContent)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                            }
                        });
        //下面这行代码放到子线程中会 Can't create handler inside thread that has not called Looper.prepare()
        AlertDialog dialog = builder.create();
        //设置点击其他地方不可取消此 Dialog
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        //8.0系统加强后台管理，禁止在其他应用和窗口弹提醒弹窗，如果要弹，必须使用TYPE_APPLICATION_OVERLAY，否则弹不出
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));
        }else {
            dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        }
        dialog.show();
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
