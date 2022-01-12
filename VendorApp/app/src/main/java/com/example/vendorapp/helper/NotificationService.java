package com.example.vendorapp.helper;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.vendorapp.R;
import com.example.vendorapp.activity.OrderDetailActivity;
import com.example.vendorapp.model.Order;
import com.example.vendorapp.model.Vendor;


public class NotificationService extends Service {

    String TAG = "NotificationService";
    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        if (intent == null){
            Log.d(TAG, "intent null");
            return START_STICKY;
        }
        sendNotification(intent);

        return START_STICKY;
    }


    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");


    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");

        super.onDestroy();


    }

    public void sendNotification(Intent intent) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                String message = intent.getStringExtra("message");
//                String message = "Hello";
                createNotificationWithIntent(message, 1, intent);
            }
        });
    }

    // create notification
    public void createNotificationWithIntent(String aMessage, int notifyId , Intent intentView) {
        NotificationManager notifManager;

        String id = this.getString(R.string.app_name); // default_channel_id
        String title = this.getString(R.string.app_name); // Default Channel
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;

        notifManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = notifManager.getNotificationChannel(id);
        if (mChannel == null) {
            mChannel = new NotificationChannel(id, title, importance);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300});
            notifManager.createNotificationChannel(mChannel);
        }
        builder = new NotificationCompat.Builder(this, id);
        intent = new Intent(this, OrderDetailActivity.class);

        Log.d(this.getClass().getSimpleName(), "Hello noti intent: ");
        try {

            Order order = intentView.getParcelableExtra("order");
            Log.d(this.getClass().getSimpleName(), "Order in noti intent: " + order.toString());

            intent.putExtra("order", order);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP  | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

// Create the TaskStackBuilder and add the intent, which inflates the back stack
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);
            Vendor vendor = intentView.getParcelableExtra("vendor");
            Log.d(this.getClass().getSimpleName(), "Vendor in noti intent: " + vendor.toString());

            stackBuilder.editIntentAt(0).putExtra("vendor", vendor);

// Get the PendingIntent containing the entire back stack
            pendingIntent =
                    stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentTitle(aMessage)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(this.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300});


            Notification notification = builder.build();
            notifManager.notify(notifyId, notification);

        } catch (Exception ignored){
            ignored.printStackTrace();
        }
    }
}