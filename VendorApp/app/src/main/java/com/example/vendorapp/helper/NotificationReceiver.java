package com.example.vendorapp.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.vendorapp.activity.MainActivity;
import com.example.vendorapp.R;
import com.example.vendorapp.activity.OrderDetailActivity;
import com.example.vendorapp.chat.MainChatActivity;
import com.example.vendorapp.chat.MessageActivity;
import com.example.vendorapp.model.Client;
import com.example.vendorapp.model.Order;
import com.example.vendorapp.model.Vendor;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (intent.getAction().equals(MainActivity.ORDER_COMING)){
            Toast.makeText(context, MainActivity.ORDER_COMING, Toast.LENGTH_SHORT).show();
            createNotificationWithIntent(context ,MainActivity.ORDER_COMING,  0,intent);
            return;
        }

        // to chat
        if (intent.getAction().equals(MainActivity.NEW_MESSAGE)){

            createNotificationToChat( context , 1, intent);
            return;
        }

    }

    // create notification
    public void createNotificationToChat( Context context , int notifyId , Intent intentView) {
        NotificationManager notifManager;

        // setup variables
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        String id = context.getString(R.string.app_name); // default_channel_id

        // build noti
        builder = new NotificationCompat.Builder(context, id);
        MainActivity mainActivity = (MainActivity) context;
        intent = new Intent(mainActivity, MessageActivity.class);

        Log.d(context.getClass().getSimpleName(), "Hello noti intent: ");
        try {

            String aMessage = intentView.getStringExtra("message");
            // get the object from intent
            Client client = intentView.getParcelableExtra("client");
            Vendor vendor = intentView.getParcelableExtra("vendor");
            Log.d(context.getClass().getSimpleName(), "Client in noti intent: " + client.toString());

            // pass to new intent to get the value in the next activity
            intent.putExtra("client", client);
            intent.putExtra("vendor", vendor);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP  );


            String title = MainActivity.NEW_MESSAGE + ""; // Default Channel


// Create the TaskStackBuilder and add the intent, which inflates the back stack
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(mainActivity);

            // add with parent stack
            stackBuilder.addNextIntentWithParentStack(new Intent(mainActivity, MainChatActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP )); // add main activity
            stackBuilder.addNextIntent(intent);

            stackBuilder.editIntentAt(0).putExtra("vendor", vendor);
            stackBuilder.editIntentAt(1).putExtra("vendor", vendor);

// Get the PendingIntent containing the entire back stack
            pendingIntent =
                    stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentTitle(""+ client.getUserName())                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(aMessage) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300});



            notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);

            // set up mChannel
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300});
                notifManager.createNotificationChannel(mChannel);
            }


            Notification notification = builder.build();
            int oneTimeID = (int) SystemClock.uptimeMillis(); // Init onetime ID by current time so the notification can display multiple notification
            notifManager.notify(notifyId, notification); // Notify by id and built notification

        } catch (Exception ignored){
            ignored.printStackTrace();
        }
    }


    // create notification
    public void createNotificationWithIntent(Context context, String aMessage, int notifyId , Intent intentView) {
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
            mChannel.setVibrationPattern(new long[]{100, 200, 300});
            notifManager.createNotificationChannel(mChannel);
        }
        builder = new NotificationCompat.Builder(context, id);
        intent = new Intent(context, OrderDetailActivity.class);

        Log.d(this.getClass().getSimpleName(), "Hello noti intent: ");
        try {

            String message = intentView.getStringExtra("message");
            Order order = intentView.getParcelableExtra("order");
            Vendor vendor = intentView.getParcelableExtra("vendor");

            Log.d(this.getClass().getSimpleName(), "Order in noti intent: " + order.toString());

            intent.putExtra("order", order);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP  | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

// Create the TaskStackBuilder and add the intent, which inflates the back stack
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntentWithParentStack(intent);
            Log.d(this.getClass().getSimpleName(), "Vendor in noti intent: " + vendor.toString());

            stackBuilder.editIntentAt(0).putExtra("vendor", vendor);

// Get the PendingIntent containing the entire back stack
            pendingIntent =
                    stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentTitle(aMessage)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(context.getString(R.string.app_name)) // required
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