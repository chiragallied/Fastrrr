package com.fastrrr.Singletone;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.fastrrr.Activity.MainActivity;
import com.fastrrr.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Allied Infosoft on 4/12/2017.
 */

public class Constants {
    public static NotificationManager mNotificationManager;
    public static void showNotification(Context context,int id){

        Intent notificationIntent=null;
        NotificationCompat.Builder builder;
        notificationIntent = new Intent(context.getApplicationContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, notificationIntent, 0);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setContentText("Click To show active windows")
                    .setLargeIcon(BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.mipmap.ic_launcher))
                    .setContentIntent(contentIntent);
        }
        else
        {
            builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setContentText("Click To show active windows")
                    .setLargeIcon(BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.mipmap.ic_launcher))
                    .setContentIntent(contentIntent);
        }
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notif = builder.build();

        notif.contentIntent = contentIntent;
        notif.flags |= Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(id, notif);

    }
}
