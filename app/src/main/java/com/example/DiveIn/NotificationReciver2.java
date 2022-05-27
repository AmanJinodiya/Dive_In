package com.example.DiveIn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReciver2 extends BroadcastReceiver {
    public static final String ACTION_PREV = "actionprevious";
    public static final String ACTION_NEXT = "actionnext";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_SHUFFLE = "actionshuffle";
    public static final String ACTION_REPEAT = "actionrepeat";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context,MusicService.class);
        if(intent.getAction() != null)
        {
            switch (intent.getAction())
            {
                case ACTION_PREV:
                    intent1.putExtra("myactionname",intent.getAction());
                    context.startService(intent1);
                    break;
                case ACTION_NEXT:
                    Log.d("Aman","clicked");
                    intent1.putExtra("myactionname",intent.getAction());
                    context.startService(intent1);
                    break;
                case ACTION_PLAY:
                    intent1.putExtra("myactionname",intent.getAction());
                    context.startService(intent1);
                    break;
                case ACTION_SHUFFLE:
                    intent1.putExtra("myactionname",intent.getAction());
                    context.startService(intent1);
                    break;


            }
        }

    }
}
