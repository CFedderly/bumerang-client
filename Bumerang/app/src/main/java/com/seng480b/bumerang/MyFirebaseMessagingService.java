package com.seng480b.bumerang;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FBMS";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Get from sender information from message
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification());
        }

        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Intent resultIntent = new Intent(this, Home.class);
        // Add information to the extra in order to specify action upon returning to home screen.
        resultIntent.putExtra("MsgRecieved", "messageRecieved");
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent
        stackBuilder.addParentStack(Home.class);
        // Adds the intent that starts the activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Map<String, String> msgData = remoteMessage.getData();
        // Set the notification sound.
        Uri sound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // Priority of the messages could be either MAX or HIGH. Set to max because they are time critical and urgent.
        // TODO: Update resultPendingIntent for .addAction below to redirect to the proper

        // Set text for the notification based upon which is received
        // 0 - request, 1 - offer.
        // TODO: Implement changing Content title based upon above number receieved.

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.bumerang_umbrella).setContentTitle("Bumerang")
                .setContentTitle("Someone has posted a request!")
                .setContentText(msgData.get("text"))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSound(sound)
                .setLights(Color.MAGENTA, 2000, 2000);

        mBuilder.setContentIntent(resultPendingIntent);
        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }
}
