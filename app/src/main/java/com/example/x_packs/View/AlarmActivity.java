package com.example.x_packs.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.x_packs.R;

import com.example.x_packs.Server.ReminderNotification;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static android.R.layout.simple_list_item_multiple_choice;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

public class AlarmActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter adapter;
    private String[] arr ={"Vibrate","Add to calender"};
    private ActionBar actionBar;
    private Switch aSwitch;
    private NotificationManagerCompat notificationManager;
    private String CHANNEL_ID;
    private TimePicker timePicker;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String HOUR = "hour";
    private static final String MIN = "min";
    private static final String SWITCH = "switch";
    private static final String CAL = "cal";
    private static final String VIB = "vib";

    private boolean switched;
    private int hour;
    private int min;
    private boolean cal;
    private boolean vib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        listView = findViewById(R.id.list_settings);
        timePicker = findViewById(R.id.spinner_timer);
        actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.switch_layout);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM);
        aSwitch=findViewById(R.id.switchForActionBar);
        adapter = new ArrayAdapter<>(this, simple_list_item_multiple_choice,arr);
        listView.setAdapter(adapter);
        CHANNEL_ID ="Channel_1";
        notificationManager = NotificationManagerCompat.from(AlarmActivity.this);
        createNotificationChannel();
        loadData();
        updateViews();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(listView.isItemChecked(position) && position == 1) {
                    if (ContextCompat.checkSelfPermission(AlarmActivity.this,
                            Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                        requestStoragePermission();
                    }
                }
            }
        });
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        hour = sharedPreferences.getInt(HOUR, 0);
        min = sharedPreferences.getInt(MIN, 0);
        switched = sharedPreferences.getBoolean(SWITCH, false);
        cal = sharedPreferences.getBoolean(CAL,false);
        vib = sharedPreferences.getBoolean(VIB,false);
    }

    private void updateViews() {
        aSwitch.setChecked(switched);
        timePicker.setHour(hour);
        timePicker.setMinute(min);
        listView.setItemChecked(0,vib);
        listView.setItemChecked(1,cal);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel1";
            String description = "Event Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);

        } else {
            Log.d("notification", "==== " + "NotificationChannel was not created");

        }
    }
    private void setReminder(int hour,int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        Intent intentR = new Intent(this, ReminderNotification.class );
        intentR.putExtra("TITLE","TRAINING TIME");
        intentR.putExtra("EVENT","You Have training session now");

        if(listView.isItemChecked(0)){
            intentR.putExtra("VIBRATE",true);
        }
        if(listView.isItemChecked(1)){
            intentR.putExtra("CALENDER",true);
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intentR,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }
    private void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intentR = new Intent(this, ReminderNotification.class );
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intentR,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        getData();
        finish();
        return super.onOptionsItemSelected(item);
    }
    private void getData(){
        cancelAlarm();
        if(aSwitch.isChecked()){
            setReminder(timePicker.getHour(),timePicker.getMinute());
        }
        saveData();
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(HOUR, timePicker.getHour());
        editor.putInt(MIN,timePicker.getMinute());
        editor.putBoolean(SWITCH, aSwitch.isChecked());
        editor.putBoolean(VIB,listView.isItemChecked(0));
        editor.putBoolean(CAL,listView.isItemChecked(1));
        editor.apply();
    }

    private void requestStoragePermission() {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_CALENDAR}, PERMISSION_GRANTED);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_GRANTED)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
                listView.setItemChecked(1,false);
            }
        }
    }

}