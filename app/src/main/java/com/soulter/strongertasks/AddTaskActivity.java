package com.soulter.strongertasks;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.soulter.strongertasks.DB.DBDataUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {


    private String taskContent;
    private String taskTimeStr;
    private static final int REQUEST_CODE = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        checkWP();

        final Button taskTime = (Button) findViewById(R.id.task_time);
        FloatingActionButton taskCreate = (FloatingActionButton)findViewById(R.id.task_create);
        final EditText taskContentET = (EditText)findViewById(R.id.task);
        taskContentET.setFocusable(true);
        taskContentET.setFocusableInTouchMode(true);
        taskContentET.requestFocus();

        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(Calendar.SECOND,0);
        final TimePickerView pvTime;
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                taskTimeStr = getTimes(date);
                SimpleDateFormat format2 = new SimpleDateFormat("MM-dd HH:mm");
                taskTime.setText("选择时间: "+format2.format(date));

            }
        })

                .setType(new boolean[]{false, true, true, true, true, false})
                .setLabel("年", "月", "日", "时", "", "")
                .isCenterLabel(true)
                .setDividerColor(Color.DKGRAY)
                .setContentSize(21)
                .setDate(selectedDate)
                .setDecorView(null)
                .build();


        taskTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pvTime.show(view);

            }
        });

        taskCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                taskContent = taskContentET.getText().toString();
                if (taskContent.equals("")){
                    Toast.makeText(AddTaskActivity.this,"还没输入内容呀",Toast.LENGTH_LONG).show();

                }else if(taskTimeStr == null){
                    Toast.makeText(AddTaskActivity.this,"还没选择时间呀",Toast.LENGTH_LONG).show();


                }else{
                    DBDataUtils dbDataUtils = new DBDataUtils();
                    dbDataUtils.addDBData(AddTaskActivity.this, taskContent, taskTimeStr, 1);
                    dbDataUtils.closeDB();
                    Intent intentService = new Intent(AddTaskActivity.this, ClockService.class);
                    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                        if ("com.soulter.strongertasks.ClockService".equals(service.service.getClassName())) {
                            stopService(intentService);
                            startService(intentService);
                        }
                    }
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AddTaskActivity.this) ;
                    Log.v("lwl",String.valueOf(prefs.getBoolean("feature_window",false)));
                    if (prefs.getBoolean("feature_window",true)){
                        if (checkWP()) {
                            startService(intentService);
                        }else{
                            Toast.makeText(AddTaskActivity.this,"未开启权限！",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        startService(intentService);
                    }
                    finish();
                }


            }
        });
    }

    private String getTimes(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public boolean checkWP() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                return false;
            }
        }
        return true;
    }






}