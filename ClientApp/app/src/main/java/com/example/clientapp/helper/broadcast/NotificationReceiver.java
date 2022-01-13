package com.example.clientapp.helper.broadcast;

import com.example.clientapp.R;

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

import androidx.core.app.NotificationCompat;

import com.example.clientapp.activity.BillingActivity;
import com.example.clientapp.activity.MainActivity;
import com.example.clientapp.chat.MainChatActivity;
import com.example.clientapp.chat.MessageActivity;
import com.example.clientapp.model.Cart;
import com.example.clientapp.model.Client;
import com.example.clientapp.model.Vendor;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (intent.getAction().equals(MainActivity.ORDER_NOTIFICATION)){
//            Toast.makeText(context, MainActivity.ORDER_NOTIFICATION, Toast.LENGTH_SHORT).show();
            createNotificationInOrder(MainActivity.ORDER_NOTIFICATION, context , 0, intent);
            return;
        }

        // to main
        if (intent.getAction().equals(MainActivity.CANCEL_NOTIFICATION)){

            createNotificationWithIntent(MainActivity.CANCEL_NOTIFICATION, context , 0, intent);
            return;
        }

        // to chat
        if (intent.getAction().equals(MainActivity.NEW_MESSAGE)){
            String message = intent.getStringExtra("message");
            createNotificationToChat(message+"", context , 1, intent);
            return;
        }

        // to main
        if (intent.getAction().equals(MainActivity.PROCESS_NOTIFICATION)){
            createNotificationWithIntent(MainActivity.PROCESS_NOTIFICATION, context , 1, intent);
            return;
        }

    }

    // create notification
    public void createNotificationToChat(String aMessage, Context context , int notifyId , Intent intentView) {
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

            stackBuilder.editIntentAt(0).putExtra("client", client);
            stackBuilder.editIntentAt(1).putExtra("client", client);

// Get the PendingIntent containing the entire back stack
            pendingIntent =
                    stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
            int numMessages = 0;

            builder.setContentTitle(""+ vendor.getUserName())                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(aMessage) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setNumber(++ numMessages)
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
//            int oneTimeID = (int) SystemClock.uptimeMillis(); // Init onetime ID by current time so the notification can display multiple notification
            notifManager.notify(aMessage.length(), notification); // Notify by id and built notification

        } catch (Exception ignored){
            ignored.printStackTrace();
        }
    }

    // create notification
    public void createNotificationInOrder(String aMessage, Context context , int notifyId, Intent intentView) {
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
        Client client = intentView.getParcelableExtra("client");
        intent.putExtra("client", client);
        intent.putExtra("toHistory", true);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addNextIntent(intent);

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
    }

    // create notification
    public void createNotificationWithIntent(String aMessage, Context context , int notifyId , Intent intentView) {
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
        MainActivity mainActivity = (MainActivity) context;
        intent = new Intent(mainActivity, BillingActivity.class);

        Log.d(context.getClass().getSimpleName(), "Hello noti intent: ");
        try {

            Cart cart1 = intentView.getParcelableExtra("cart");
            Log.d(context.getClass().getSimpleName(), "Cart1 in noti intent: " + cart1.toString());

            intent.putExtra("cart", cart1);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP  | Intent.FLAG_ACTIVITY_NEW_TASK);


// Create the TaskStackBuilder and add the intent, which inflates the back stack
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(mainActivity);
            stackBuilder.addNextIntentWithParentStack(intent);
            Client client = intentView.getParcelableExtra("client");
            stackBuilder.editIntentAt(0).putExtra("client", client);

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
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});


            Notification notification = builder.build();
//            int oneTimeID = (int) SystemClock.uptimeMillis(); // Init onetime ID by current time so the notification can display multiple notification
            notifManager.notify(notifyId, notification); // Notify by id and built notification

        } catch (Exception ignored){
            ignored.printStackTrace();
        }
    }

}