package com.example.x_packs.Server;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.CalendarContract;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.x_packs.R;

import java.util.Calendar;

public class ReminderNotification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationCompat.Builder builder  = new NotificationCompat.Builder(context, "Channel_1")
                .setSmallIcon(R.drawable.ic_fitness)
                .setContentTitle(intent.getStringExtra("TITLE"))
                .setContentText(intent.getStringExtra("EVENT"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());

        if(intent.getBooleanExtra("CALENDER", false)){
            Calendar c = Calendar.getInstance();
            ContentResolver cr = context.getContentResolver();
            ContentValues cv = new ContentValues();

            cv.put(CalendarContract.Events.TITLE,intent.getStringExtra("TITLE"));

            c.add(Calendar.DAY_OF_YEAR,1);
            cv.put(CalendarContract.Events.DTSTART,c.getTimeInMillis());
            cv.put(CalendarContract.Events.DTEND,c.getTimeInMillis());

            cv.put(CalendarContract.Events.CALENDAR_ID,1);
            cv.put(CalendarContract.Events.EVENT_TIMEZONE,Calendar.getInstance().getTimeZone().getID());
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI,cv);
        }

        if(intent.getBooleanExtra("VIBRATE",false)){
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    }
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }
            }
        }

}