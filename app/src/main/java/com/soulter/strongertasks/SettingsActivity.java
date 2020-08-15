package com.soulter.strongertasks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.soulter.strongertasks.ClockService.TASK_CONTENT_EXTRA_TAG;
import static com.soulter.strongertasks.ClockService.TASK_TIME_EXTRA_TAG;

public class SettingsActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_test) {
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Intent alarmTest = new Intent(SettingsActivity.this,AlarmService.class);
            alarmTest.putExtra(TASK_CONTENT_EXTRA_TAG,"快去淘宝抢小米10！！")
                    .putExtra(TASK_TIME_EXTRA_TAG, sdf2.format(Calendar.getInstance().getTime()));
                startService(alarmTest);
        }

        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            final String qqUrl = "https://qm.qq.com/cgi-bin/qm/qr?k=OZpb_cIHzdm2X3ni9KkuF9FJGyMb7cGL";
            final String afdian_url = "https://afdian.net/@soulter";

            SharedPreferences spfs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Preference qqNumber = findPreference("qq_number");
            Preference afdian = findPreference("afdian");

            final EditTextPreference vibratingTime = findPreference("vibrating_time");
            vibratingTime.setSummary(spfs.getString("vibrating_time","1000"));
            vibratingTime.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    vibratingTime.setSummary(newValue.toString());
                    vibratingTime.setDefaultValue(newValue);
                    return true;
                }
            });

            qqNumber.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    try {
                        //第二种方式：可以跳转到添加好友，如果qq号是好友了，直接聊天
                        String url = "mqqwpa://im/chat?chat_type=wpa&uin=905617992";//uin是发送过去的qq号码
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl));
                        startActivity(it);
                    }
                    return false;
                }
            });

            afdian.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    try {
                        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(afdian_url));
                        startActivity(it);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });


        }
    }
}