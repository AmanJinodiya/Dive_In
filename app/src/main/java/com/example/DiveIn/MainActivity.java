package com.example.DiveIn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String []items;
    RecyclerView recyclerView;
    boolean del_check = false;
    MediaPlayer mediaPlayer;
    String pp;
    LottieAnimationView card1;
//    ImageButton play;
    LottieAnimationView imageView;
    SearchView searchView;
    long animation_duration = 1000;
    int pause_me = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyleview);
        recyclerView.setVisibility(View.INVISIBLE);
        card1 = findViewById(R.id.card1);
        searchView = findViewById(R.id.search_view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }

//        imageView = findViewById(R.id.imageView1);
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                card1 = findViewById(R.id.card1);
                card1.animate().translationY(-245).setDuration(1000);
                recyclerView.animate().translationY(470).setDuration(1000);
            }
        }, 200);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(del_check == false)
                {
                    del_check = true;
                    displaysong();
                }
                else
                {
                    del_check = false;
                    displaysong();
                }
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.setVisibility(View.VISIBLE);
            }
        }, 1100);

        user_permission();


       

    }

    private void user_permission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.VIBRATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displaysong();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> findsong()
    {
        ArrayList<File> arrayList = new ArrayList<>();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        pp = intent.getStringExtra("p");

        String path = Environment.getExternalStorageDirectory().toString()+pp;

        File directory = new File(path);
        File[] files = directory.listFiles();

        for(int i = 0; i < files.length; i++)
        {
            if(files[i].getName().endsWith(".mp3"))
            {
                arrayList.add(files[i]);
            }
        }
        return arrayList;
    }

    public void displaysong()
    {

        final ArrayList<File> mysongs = findsong();
        items = new String[mysongs.size()];
        for(int i = 0 ;i < mysongs.size();i++)
        {
            items[i] = mysongs.get(i).getName().replace(".mp3","").replace(".wav","");
        }

         mainActivityAdapter m = new mainActivityAdapter(mysongs,del_check,pp);
        recyclerView.setAdapter(m);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                m.getFilter().filter(newText);
                return false;
            }
        });


    }

    }