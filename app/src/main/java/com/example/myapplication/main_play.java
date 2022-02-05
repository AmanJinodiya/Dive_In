package com.example.myapplication;

import static com.example.myapplication.ApplicationClass.ACTION_NEXT;
import static com.example.myapplication.ApplicationClass.ACTION_PLAY;
import static com.example.myapplication.ApplicationClass.ACTION_PREV;
import static com.example.myapplication.ApplicationClass.ACTION_SHUFFLE;
import static com.example.myapplication.ApplicationClass.CHANNEL_ID_2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.palette.graphics.Palette;

import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class main_play extends AppCompatActivity implements ActionPlaying,ServiceConnection{

    ImageView imageView,shuffle_active;
    ImageButton shuffle_button,skip_prev,skip_next,repeat_button;
    TextView textView;
    ConstraintLayout constraintLayout;
    MaterialButton play_button,tb;

    ListView listView;
    RelativeLayout r,rl;

    boolean play_pause_state = true;
    ImageView iv;


    SeekBar seekBar;
    String []items;
    String song_n;
    int position;
    ArrayList<File> mysongs;
    boolean shuffle = false,repeat = false;
    static MediaPlayer mediaPlayer;
    Thread update_seekbar;
    AudioManager audioManager;
    MediaSessionCompat mediaSession;
    AudioAttributes playbackAttributes;
    ImageView b;
    MusicService musicService;

//    path of the folder
    String path;
    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
//                mediaPlayer.start();
                if( mediaPlayer != null ) {
                    if( !mediaPlayer.isPlaying() ) {
                        mediaPlayer.start();
                    }
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                mediaPlayer.pause();

            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                if( mediaPlayer.isPlaying() ) {
                    mediaPlayer.stop();
                }
            }
        }
    };

    MediaSessionCompat.Callback mediacall = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            super.onPlay();
            if(mediaPlayer != null)
            {

                Toast.makeText(main_play.this, "button pressed", Toast.LENGTH_SHORT).show();
                playpause();
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            if(mediaPlayer != null)
            {
                Toast.makeText(main_play.this, "button pressed", Toast.LENGTH_SHORT).show();
                playpause();
            }
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            if(mediaPlayer != null)
            {
                Toast.makeText(main_play.this, "next", Toast.LENGTH_SHORT).show();
                skip_next.performClick();
            }
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            if(mediaPlayer != null)
            {
                Toast.makeText(main_play.this, "prev", Toast.LENGTH_SHORT).show();
                skip_prev.performClick();
            }
        }
    };





        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_play);

        textView = findViewById(R.id.textView);
        constraintLayout = findViewById(R.id.body);
        shuffle_button = findViewById(R.id.shuffle_button);
        skip_next = findViewById(R.id.skip_next);
        skip_prev  = findViewById(R.id.skip_prev);
        play_button = findViewById(R.id.play_button);
        repeat_button = findViewById(R.id.repeat_button);

        listView = findViewById(R.id.listview1);
        iv = findViewById(R.id.iv);
        seekBar = findViewById(R.id.seekBar);

        shuffle_active = findViewById(R.id.shuffle_action);
        shuffle_active.setVisibility(View.INVISIBLE);
        r = findViewById(R.id.relative_lauid);


        mediaSession = new MediaSessionCompat(this,"playeraudio");
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        playbackAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(playbackAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .build();

        final int audioFocusRequest = audioManager.requestAudioFocus(focusRequest);

        mediaSession.setCallback(mediacall);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        mediaSession.setActive(true);
        PlaybackStateCompat state = new PlaybackStateCompat.Builder().setActions(PlaybackStateCompat.ACTION_PLAY
                | PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PAUSE
                | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS).build();
        mediaSession.setPlaybackState(state);




        Intent intent1 = new Intent(this,MusicService.class);
        bindService(intent1,this,BIND_AUTO_CREATE);

        if(mediaPlayer != null)
        {
            if (audioFocusRequest == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mediaPlayer.start();
                mediaPlayer.release();
            };

        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mysongs = (ArrayList)bundle.getParcelableArrayList("songs");
        displaysong();
        position = bundle.getInt("pos",0);
        path = bundle.getString("pat");
        String song_name = mysongs.get(position).getName();
        song_name = song_name.replace(".mp3","");
        textView.setText(song_name);
        textView.setSelected(true);

        Uri uri = Uri.parse(mysongs.get(position).toString());

        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();


        song_n = mysongs.get(position).getName();
        song_n  = song_n.replace(".mp3","");

        if(mediaPlayer.isPlaying())
        {
            shownotification(R.drawable.ic_baseline_pause_24);

        }
        else {
            {
                shownotification(R.drawable.ic_baseline_play_arrow_24);
            }
        }

        update_seekbar = new Thread()
        {
            @Override
            public void run() {
                int total_duration = mediaPlayer.getDuration();
                int current_duration = 0;
                while (current_duration<total_duration)
                {
                    try {
                        sleep(500);
                        current_duration = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(current_duration);

                    }catch (InterruptedException | IllegalStateException e)
                    {
                        e.printStackTrace();
                    }
                }
                super.run();
            }
        };

        seekBar.setMax(mediaPlayer.getDuration());
        update_seekbar.start();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null  && fromUser)
                {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioFocusRequest == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    playpause();
                }
            }
        });

        skip_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nxt();
            }
        });

        skip_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prev();
            }
        });

        shuffle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuf();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                skip_next.performClick();
            }
        });





        imageView = findViewById(R.id.song_pic);
//        imageView.setImageResource();
        imageView.setImageBitmap(coverpicture(mysongs.get(position).getPath()));
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                //work with the palette here
                int defaultValue = 0x000000;
                int vibrant = palette.getVibrantColor(defaultValue);
                int vibrantLight = palette.getLightVibrantColor(defaultValue);
                int vibrantDark = palette.getDarkVibrantColor(defaultValue);
                int muted = palette.getMutedColor(defaultValue);
                int mutedLight = palette.getLightMutedColor(defaultValue);
                int mutedDark = palette.getDarkMutedColor(defaultValue);

//                vibrantView.setBackgroundColor(vibrant);
//                vibrantLightView.setBackgroundColor(vibrantLight);
//                vibrantDarkView.setBackgroundColor(vibrantDark);
//                mutedView.setBackgroundColor(muted);
//                mutedLightView.setBackgroundColor(mutedLight);
//                mutedDarkView.setBackgroundColor(mutedDark);



                if(muted == 0)
                {
                    constraintLayout.setBackgroundColor(vibrantDark);
                    skip_next.setBackgroundColor(vibrantDark);
                    skip_prev.setBackgroundColor(vibrantDark);
                    shuffle_button.setBackgroundColor(vibrantDark);
                    repeat_button.setBackgroundColor(vibrantDark);
                    play_button.setBackgroundColor(vibrantLight);
                    imageView.setBackgroundColor(vibrantDark);
//                    tb.setBackgroundColor(vibrantLight);

//
                    View v = LayoutInflater.from(main_play.this).inflate(R.layout.list_item, null);
//                    View view = getLayoutInflater().inflate(R.layout.list_item,null);
                    TextView t = v.findViewById(R.id.txtsong);
                    t.setText("AMna");


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(vibrantDark);
                    }

                }
                else {

                    constraintLayout.setBackgroundColor(mutedDark);
                    skip_next.setBackgroundColor(mutedDark);
                    skip_prev.setBackgroundColor(mutedDark);
                    shuffle_button.setBackgroundColor(mutedDark);
                    repeat_button.setBackgroundColor(mutedDark);
                    play_button.setBackgroundColor(mutedLight);
                    imageView.setBackgroundColor(mutedDark);


                    View view = View.inflate(main_play.this, R.layout.list_item, null);
                    TextView t = view.findViewById(R.id.txtsong);
                    t.setText("AMna");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(mutedDark);
                    }

                }
            }
        });




    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this,MusicService.class);
        bindService(intent,this,BIND_AUTO_CREATE);
    }


    public void cover_bgupdate(int position)
    {
        imageView.setImageBitmap(coverpicture(mysongs.get(position).getPath()));
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                //work with the palette here
                int defaultValue = 0x000000;
                int vibrant = palette.getVibrantColor(defaultValue);
                int vibrantLight = palette.getLightVibrantColor(defaultValue);
                int vibrantDark = palette.getDarkVibrantColor(defaultValue);
                int muted = palette.getMutedColor(defaultValue);
                int mutedLight = palette.getLightMutedColor(defaultValue);
                int mutedDark = palette.getDarkMutedColor(defaultValue);

//                vibrantView.setBackgroundColor(vibrant);
//                vibrantLightView.setBackgroundColor(vibrantLight);
//                vibrantDarkView.setBackgroundColor(vibrantDark);
//                mutedView.setBackgroundColor(muted);
//                mutedLightView.setBackgroundColor(mutedLight);
//                mutedDarkView.setBackgroundColor(mutedDark);



                if(muted == 0 || vibrantDark !=0)
                {
                    constraintLayout.setBackgroundColor(vibrantDark);
                    skip_next.setBackgroundColor(vibrantDark);
                    skip_prev.setBackgroundColor(vibrantDark);
                    shuffle_button.setBackgroundColor(vibrantDark);
                    repeat_button.setBackgroundColor(vibrantDark);
                    if(vibrantDark == vibrantLight) play_button.setBackgroundColor(mutedLight);
                    play_button.setBackgroundColor(vibrantLight);

                    play_button.setBackgroundColor(vibrantLight);
                    imageView.setBackgroundColor(vibrantDark);

//                    tb.setBackgroundColor(vibrantLight);



//                    DrawableCompat.setTint(shuffle_button.getDrawable(),ContextCompat.getColor(getApplicationContext(),cp));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(vibrantDark);
                    }

                }
                else {

                    constraintLayout.setBackgroundColor(mutedDark);
                    skip_next.setBackgroundColor(mutedDark);
                    skip_prev.setBackgroundColor(mutedDark);
                    shuffle_button.setBackgroundColor(mutedDark);
                    repeat_button.setBackgroundColor(mutedDark);
                    if(mutedDark == mutedLight) play_button.setBackgroundColor(vibrantLight);
                    else play_button.setBackgroundColor(mutedLight);
                    play_button.setBackgroundColor(mutedLight);
                    imageView.setBackgroundColor(mutedDark);

//                    tb.setBackgroundColor(mutedLight);

//                (constraintLayout.getBackground()).setColorFilter(Color.parseColor("#FFDE03"), PorterDuff.Mode.SRC_IN);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(mutedDark);
                    }

                }
            }
        });

    }

    public  Bitmap coverpicture(String path) {

        MediaMetadataRetriever mr = new MediaMetadataRetriever();

        mr.setDataSource(path);

        byte[] byte1 = mr.getEmbeddedPicture();
        mr.release();
        if(byte1 != null)
            return BitmapFactory.decodeByteArray(byte1, 0, byte1.length);
        else
            return  null;

    }

    public int getrandom(int i) {
        Random random = new Random();
        return  random.nextInt(i+1);
    }

    @Override
    public void nxt() {
        imageanimation(this,imageView);

        mediaPlayer.stop();
        mediaPlayer.release();

        if(shuffle)
        {

            shuffle_active.setVisibility(View.VISIBLE);
        }
        else
        {
            shuffle_active.setVisibility(View.INVISIBLE);
        }
        if(shuffle)
        {
            position = getrandom(mysongs.size()-1);
        }
        else
        {
            position = ((position + 1)%mysongs.size());
        }
        Uri uri = Uri.parse(mysongs.get(position).toString());
        mediaPlayer =MediaPlayer.create(getApplicationContext(),uri);
        song_n = mysongs.get(position).toString();
        String extra = "/storage/emulated/0/myapp/";
        ArrayList<String>remove_foldername = new ArrayList<>();
        cover_bgupdate(position);
        remove_foldername.add("Broken");
        remove_foldername.add("Punjabi");
        remove_foldername.add("Old");
        remove_foldername.add("Pop");
        remove_foldername.add("Workout");
        remove_foldername.add("Travel");
        remove_foldername.add("English");
        remove_foldername.add("all");
        for(String s : remove_foldername)
        {
            extra+=(s+"/");
            song_n = song_n.replace(extra,"");
            extra = "/storage/emulated/0/myapp/";
        }

        song_n  = song_n.replace(".mp3","");
        textView.setText(song_n);
        mediaPlayer.start();
        if(play_pause_state == false) play_button.setIconResource(R.drawable.pause_me);


        if(mediaPlayer.isPlaying())
        {
            shownotification(R.drawable.ic_baseline_pause_24);

        }
        else {
            {
                shownotification(R.drawable.ic_baseline_play_arrow_24);
            }
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                skip_next.performClick();
            }
        });
    }

    @Override
    public void prev() {
        imageanimationprev(imageView);
        mediaPlayer.stop();
        mediaPlayer.release();
        if(shuffle)
        {
            shuffle_active.setVisibility(View.VISIBLE);
        }
        else
        {
            shuffle_active.setVisibility(View.INVISIBLE);
        }
        position = ((position - 1)<0)?(mysongs.size()-1):position-1;
        Uri uri = Uri.parse(mysongs.get(position).toString());
        mediaPlayer =MediaPlayer.create(getApplicationContext(),uri);
        song_n = mysongs.get(position).toString();
        String extra = "/storage/emulated/0/myapp/";
        ArrayList<String>remove_foldername = new ArrayList<>();
        cover_bgupdate(position);
        remove_foldername.add("Broken");
        remove_foldername.add("Punjabi");
        remove_foldername.add("Old");
        remove_foldername.add("Pop");
        remove_foldername.add("Workout");
        remove_foldername.add("Travel");
        remove_foldername.add("English");
        remove_foldername.add("all");
        for(String s : remove_foldername)
        {
            extra+=(s+"/");
            song_n = song_n.replace(extra,"");
            extra = "/storage/emulated/0/myapp/";
        }

        song_n  = song_n.replace(".mp3","");
        textView.setText(song_n);

        mediaPlayer.start();
        if(play_pause_state == false) play_button.setIconResource(R.drawable.pause_me);

        if(mediaPlayer.isPlaying())
        {
            shownotification(R.drawable.ic_baseline_pause_24);

        }
        else {
            {
                shownotification(R.drawable.ic_baseline_play_arrow_24);
            }
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                skip_next.performClick();
            }
        });

    }

    @Override
    public void playpause() {

        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
            play_button.setIconResource(R.drawable.play_me);
            shownotification(R.drawable.play_me);
            play_pause_state = false;

        }
        else {
            {
                mediaPlayer.start();
                play_button.setIconResource(R.drawable.pause_me);
                shownotification(R.drawable.pause_me);
                play_pause_state = true;
            }
        }
    }



    @Override
    public void shuf() {
        if(shuffle)
        {
            shuffle = false;
            shuffle_active.setVisibility(View.INVISIBLE);


        }
        else
        {
            shuffle = true;
            shuffle_active.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();
        musicService.setCallback(main_play.this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }

    public void shownotification(int playpausebtn)
    {
        Intent intent = new Intent(this,main_play.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent prevIntent = new Intent(this,NotificationReciver2.class).setAction(ACTION_PREV);
        PendingIntent prevpendingintent = PendingIntent.getBroadcast(this,0,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent = new Intent(this,NotificationReciver2.class).setAction(ACTION_PLAY);
        PendingIntent playpendingintent = PendingIntent.getBroadcast(this,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this,NotificationReciver2.class).setAction(ACTION_NEXT);
        PendingIntent nextpendingintent = PendingIntent.getBroadcast(this,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent shuffleIntent = new Intent(this,NotificationReciver2.class).setAction(ACTION_SHUFFLE);
        PendingIntent shufflependingintent = PendingIntent.getBroadcast(this,0,shuffleIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap pic = coverpicture(mysongs.get(position).getPath());
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID_2)
                .setSmallIcon(R.drawable.ic_baseline_home_24)
                .setLargeIcon(pic)
                .setContentTitle(mysongs.get(position).getName().replace(".mp3",""))
                .addAction(R.drawable.ic_baseline_shuffle_24,"shuffle",shufflependingintent)
                .addAction(R.drawable.ppp,"previous",prevpendingintent)
                .addAction(playpausebtn,"play",playpendingintent)
                .addAction(R.drawable.snn,"next",nextpendingintent)
                .addAction(R.drawable.ic_baseline_repeat_24,"repeat",null)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(contentIntent)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify(0,notification);



    }

    public  void imageanimation(final Context context,final ImageView imageView)
    {
        Animation animout = AnimationUtils.loadAnimation(this,R.anim.anim_left);
        imageView.setAnimation(animout);
    }

    public  void imageanimationprev(ImageView imageView)
    {
        Animation animout = AnimationUtils.loadAnimation(this,R.anim.anim_right);
        imageView.setAnimation(animout);
    }

    public void displaysong()
    {
        items = new String[mysongs.size()];
        for(int i = 0 ;i < mysongs.size();i++)
        {
            items[i] = mysongs.get(i).getName().replace(".mp3","").replace(".wav","");
        }

        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list_item_change_song(position);
//                parent.getChildAt(position).setBackgroundColor(Color.RED);
                View view1 = getLayoutInflater().inflate(R.layout.list_item,null);
                TextView t = view1.findViewById(R.id.txtsong);
                ImageView imageView1 = view1.findViewById(R.id.iv);
                t.setBackgroundColor(Color.MAGENTA);

            }
        });

    }
//
    void list_item_change_song(int pos)
    {
        imageanimation(this,imageView);
        mediaPlayer.stop();
        mediaPlayer.release();
        Uri uri = Uri.parse(mysongs.get(pos).toString());
        mediaPlayer =MediaPlayer.create(getApplicationContext(),uri);
        song_n = mysongs.get(pos).toString();
        String extra = "/storage/emulated/0/myapp/";
        ArrayList<String>remove_foldername = new ArrayList<>();
        cover_bgupdate(pos);
        remove_foldername.add("Broken");
        remove_foldername.add("Punjabi");
        remove_foldername.add("Old");
        remove_foldername.add("Pop");
        remove_foldername.add("Workout");
        remove_foldername.add("Travel");
        remove_foldername.add("English");
        remove_foldername.add("all");
        for(String s : remove_foldername)
        {
            extra+=(s+"/");
            song_n = song_n.replace(extra,"");
            extra = "/storage/emulated/0/myapp/";
        }

        song_n  = song_n.replace(".mp3","");
        textView.setText(song_n);
        mediaPlayer.start();
        if(play_pause_state == false) play_button.setIconResource(R.drawable.pause_me);
        if(mediaPlayer.isPlaying())
        {
            shownotification(R.drawable.ic_baseline_pause_24);
        }
        else {
            {

                shownotification(R.drawable.ic_baseline_play_arrow_24);
            }
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                skip_next.performClick();
            }
        });
    }

    class customAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = getLayoutInflater().inflate(R.layout.list_item,null);
            TextView t = view.findViewById(R.id.txtsong);
            ImageView imageView1 = view.findViewById(R.id.iv);
            t.setSelected(true);
//            t.setBackgroundColor(Color.MAGENTA);
//
//
            CardView cardView = view.findViewById(R.id.cardd);
            RelativeLayout r = view.findViewById(R.id.relative_lauid);
//            t.setBackgroundColor(Color.parseColor("#FF8800"));
//            t.setBackgroundColor(android.R.color.holo_orange_dark);
//
//            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
//            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
//                @Override
//                public void onGenerated(Palette palette) {
//                    //work with the palette here
//                    int defaultValue = 0x000000;
//                    int vibrant = palette.getVibrantColor(defaultValue);
//                    int vibrantLight = palette.getLightVibrantColor(defaultValue);
//                    int vibrantDark = palette.getDarkVibrantColor(defaultValue);
//                    int muted = palette.getMutedColor(defaultValue);
//                    int mutedLight = palette.getLightMutedColor(defaultValue);
//                    int mutedDark = palette.getDarkMutedColor(defaultValue);
//
//                    if(muted == 0)
//                    {
//                        String hexColor = String.format("#%08X", (0xFFFFFFFF & vibrantDark));
//                        t.setBackgroundColor(Color.parseColor(hexColor));
//                        cardView.setBackgroundColor(Color.parseColor(hexColor));
//                        r.setBackgroundColor(Color.parseColor(hexColor));
//                        imageView1.setBackgroundColor(Color.parseColor(hexColor));
//
//                    }
//                    else {
//
//                        String hexColor = String.format("#%08X", (0xFFFFFFFF & mutedDark));
//                        t.setBackgroundColor(Color.parseColor(hexColor));
//                        cardView.setBackgroundColor(Color.parseColor(hexColor));
//                        r.setBackgroundColor(Color.parseColor(hexColor));
//                        imageView1.setBackgroundColor(Color.parseColor(hexColor));
//
//
//                    }
//                }
//            });
            t.setText(items[position]);
            String pp = path;

            String path = Environment.getExternalStorageDirectory().toString()+pp;

            File directory = new File(path);
            File[] files = directory.listFiles();

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(files[position].getPath());
            byte [] data = mmr.getEmbeddedPicture();
            Bitmap bitmap1 = BitmapFactory.decodeByteArray(data, 0, data.length);
            imageView1.setImageBitmap(bitmap1);
            return view;
        }
    }



}