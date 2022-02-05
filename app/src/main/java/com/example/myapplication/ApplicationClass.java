package com.example.myapplication;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class ApplicationClass extends Application{
    public static final String CHANNEL_ID_1 = "channel1";
    public static final String CHANNEL_ID_2 = "channel2";
    public static final String ACTION_PREV = "actionprevious";
    public static final String ACTION_NEXT = "actionnext";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_SHUFFLE = "actionshuffle";
    public static final String ACTION_REPEAT = "actionrepeate";


    public void onCreate()
    {
        super.onCreate();
        CreateNotificationChannel1();
    }

    private void CreateNotificationChannel1() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID_1,"Channel(1)", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel 1 desc");
            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID_2,"Channel(2)", NotificationManager.IMPORTANCE_HIGH);
            channel2.setDescription("Channel 2 desc");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
        }
    }


}
