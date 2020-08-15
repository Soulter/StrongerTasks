package com.soulter.strongertasks;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.soulter.strongertasks.DB.DBDataUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.soulter.strongertasks.ClockService.TASK_CONTENT_EXTRA_TAG;
import static com.soulter.strongertasks.ClockService.TASK_TIME_EXTRA_TAG;


public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TaskDataRvAdapter myAdapter;

//    private FloatingActionButton fabStart;
    private FloatingActionButton fab;

    private TextView mEmptyRvList;

    private DBDataUtils dbDataUtils = new DBDataUtils();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getIntExtra("feature_tag",0)==2)
        {
            try{

                Log.v("lwl","okkkkkkkkkkk");
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                Intent intent2 = new Intent(MainActivity.this,AlarmService.class);
                PendingIntent pi = PendingIntent.getActivity(MainActivity.this,getIntent().getIntExtra("channel_id",999),intent2,0);


                notificationManager.cancel(getIntent().getIntExtra("channel_id",999));
                alarmManager.cancel(pi);

            }catch (Exception e){
                e.printStackTrace();
            }


        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });

//        fabStart = findViewById(R.id.fab_start);
//        fabStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.v("lwl", "onCLICK");
//
//                Intent intentService = new Intent(MainActivity.this, ClockService.class);
//
//                if (isRunning()){
//                    fabStart.setImageResource(R.drawable.start);
//
//                    stopService(intentService);
//
//                    Toast.makeText(MainActivity.this,"关闭成功！",Toast.LENGTH_LONG).show();
//                }
//
//                else{
//                    fabStart.setImageResource(R.drawable.stop);
//                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this) ;
//                    Log.v("lwl",String.valueOf(prefs.getBoolean("feature_window",false)));
//                    if (prefs.getBoolean("feature_window",true)){
//                        if (checkWP()) {
//                            startService(intentService);
//                        }else{
//                            Toast.makeText(MainActivity.this,"未开启权限！",Toast.LENGTH_LONG).show();
//                        }
//                    }else{
//                        startService(intentService);
//                    }
//                    Toast.makeText(MainActivity.this,"开启成功！",Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        mEmptyRvList = (TextView)findViewById(R.id.empty_rv_list);


        isFirst();



        initData();
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                long id = (long)viewHolder.itemView.getTag(R.string.view_key_id);
                int stateToken = (int)viewHolder.itemView.getTag(R.string.state_key_id);
                Log.v("TAG"," data from ");

                if (stateToken == 1){
                    dbDataUtils.delTask(MainActivity.this,id,1);
                }else{
                    dbDataUtils.delTask(MainActivity.this,id,2);
                }

                int itemCount = myAdapter.swapCursor();//重新调用getAllDesks()来刷新cursor
                if (itemCount == 0){
                    mEmptyRvList.setVisibility(View.VISIBLE);
                }

            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initData() {
        Cursor cursorTask = dbDataUtils.getTaskDB(this);
        Cursor cursorTasked = dbDataUtils.getTaskedDB(this);

        if(cursorTask.getCount() == 0 && cursorTasked.getCount() == 0){

            mEmptyRvList.setVisibility(View.VISIBLE);

        }else
            mEmptyRvList.setVisibility(View.INVISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.rv_task_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        myAdapter = new TaskDataRvAdapter(recyclerView.getContext(), cursorTask, cursorTasked);
        recyclerView.setAdapter(myAdapter);




    }

    public void isFirst(){
        SharedPreferences.Editor editor = this.getSharedPreferences("spfs",Context.MODE_PRIVATE).edit();
        SharedPreferences spfs = this.getSharedPreferences("spfs",Context.MODE_PRIVATE);
        if (spfs.getInt("firstuse",1) == 1){
        editor.putInt("firstuse",0);
        editor.apply();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());

        dbDataUtils.addDBData(this,"感谢下载本软件呀！",simpleDateFormat.format(date),1);
        dbDataUtils.addDBData(this,"右上方按钮可以打开设置",simpleDateFormat.format(date),1);
        dbDataUtils.addDBData(this,"右滑删除待办提醒",simpleDateFormat.format(date),1);
        dbDataUtils.addDBData(this,"右下方可以启动或添加待办提醒",simpleDateFormat.format(date),1);


//        final GuideView.Builder builder = new GuideView.Builder(this, "1.0");
//            builder.setTextSize(20);
//
//            builder.addHintView(fab, "这里可以添加待办提醒", GuideView.Direction.TOP, GuideView.MyShape.CIRCULAR)
//                    .addHintView(fabStart, "这里可以启动提醒服务", GuideView.Direction.TOP, GuideView.MyShape.CIRCULAR);
//            builder.show();


        }
}





    @Override
    protected void onRestart() {
        myAdapter.swapCursor();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        dbDataUtils.closeDB();
        super.onDestroy();
    }



    @Override
    protected void onResume() {
//        if (isRunning())
//            fabStart.setImageResource(R.drawable.stop);
        super.onResume();
    }

    private Boolean isRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.soulter.strongertasks.ClockService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



}


