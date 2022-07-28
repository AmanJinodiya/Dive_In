package com.example.DiveIn;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;

public class MusicService extends Service {

    IBinder mBinder = new MyBinder();
    public static final String ACTION_PREV = "actionprevious";
    public static final String ACTION_NEXT = "actionnext";
    public static final String ACTION_PLAY = "actionplay";
    ActionPlaying actionPlaying;




    public void onCreate()
    {
        super.onCreate();
//        arrayList = mysongs;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("hi","hello");
        String actionname = intent.getStringExtra("myactionname");
        Log.d("Aman",actionname);
        if(actionPlaying != null) Log.d("Aman","not null");
        else Log.d("Aman","null");
        if(actionname != null) {

            switch (actionname) {
                case ACTION_PREV:
                    if(actionPlaying != null)
                    {
                        actionPlaying.prev();
                    }
                    break;
                case ACTION_NEXT:
                    if(actionPlaying != null)
                    {
                        Log.d("Aman","in prev");
                        actionPlaying.nxt();
                    }
                    break;
                case ACTION_PLAY:
                    if(actionPlaying != null)
                    {
                        Log.d("Aman","in play");
                        actionPlaying.playpause();
                    }
                    else Log.d("Aman","in play else");
                default:
                    Log.d("Aman","in def");

                    break;
            }
        }
        else Log.d("Aman","last null");
        return START_STICKY;
    }

    public  void setCallback(ActionPlaying actionPlaying)
    {
        this.actionPlaying = actionPlaying;
    }

    public class MyBinder extends Binder{
        MusicService getService()
        {
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind","method");
        return mBinder;
    }

}
