package com.example.clientapp.helper.broadcast;
import com.example.clientapp.R;
import com.example.clientapp.activity.MainActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HydrationReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "HydrationReminderReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Get current time
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        // an Intent broadcast.
        createNotification(time+" It's time to drink some water", context );
        Log.d(TAG, "onReceive: alarm after ");

    }


    private void createNotification(String aMessage, Context context) {
        //Call Main activity when noti is clicked
        NotificationManager notifManager;

        String id = context.getString(R.string.app_name); // default_channel_id
        String title = context.getString(R.string.app_name); // Default Channel
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;

        notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = notifManager.getNotificationChannel(id);
        if (mChannel == null) {
            mChannel = new NotificationChannel(id, title, importance);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notifManager.createNotificationChannel(mChannel);
        }
        builder = new NotificationCompat.Builder(context, id);
        intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentTitle(aMessage)                            // required
                .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                .setContentText(context.getString(R.string.app_name)) // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.bun)  // Set icon
                .setTicker(aMessage)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400}); // Set vibration


        Notification notification = builder.build();
        int oneTimeID = (int) SystemClock.uptimeMillis(); // Init onetime ID by current time so the notification can display multiple notification
        notifManager.notify(oneTimeID, notification); // Notify by id and built notification
    }
}