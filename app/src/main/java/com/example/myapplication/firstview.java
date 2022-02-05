package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class firstview extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigation;
    ImageView bg,all,travel,punjabi,b,j,broken,english,old,pop,workout;
    HorizontalScrollView scrollView;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    boolean background = false;
//    static int z = 0;
    int m;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstview);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bg = findViewById(R.id.bg1);
        scrollView = findViewById(R.id.scroll);
        all = findViewById(R.id.all);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        j = findViewById(R.id.j);
        travel = findViewById(R.id.travel);
        b = findViewById(R.id.b);
        broken = findViewById(R.id.broken);
        english = findViewById(R.id.english);
        old = findViewById(R.id.old);
        pop = findViewById(R.id.pop);
        workout = findViewById(R.id.workout);
        user_permission();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }

        navigationView.bringToFront();



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        scrollView.setVisibility(View.GONE);

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("p","/myapp/all/"));


            }
        });
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                background = true;
                if(scrollView.getVisibility() == View.VISIBLE)
                {
                    scrollView.setVisibility(View.GONE);
                    int new_hight = 1000;
                    int new_width = 1000;
                    bg.requestLayout();
                    bg.getLayoutParams().height = new_hight;
                    bg.getLayoutParams().width = new_width;
                    bg.setAlpha(1f);
                    bg.setScaleType(ImageView.ScaleType.FIT_XY);
                }
                else {
                    int new_hight = 400;
                    int new_width = 400;
                    bg.requestLayout();
                    bg.getLayoutParams().height = new_hight;
                    bg.getLayoutParams().width = new_width;
                    bg.setAlpha(0.5f);
                    bg.setScaleType(ImageView.ScaleType.FIT_XY);
                    scrollView.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // yourMethod();
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.setSmoothScrollingEnabled(true);
                                    scrollView.smoothScrollTo(2980,0);
                                }
                            });
                        }
                    }, 1000);


                }
            }
        });

        travel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("p","/myapp/Travel/"));
            }
        });
        j.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("p","/myapp/Punjabi/"));
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(firstview.this, "Scroll Right", Toast.LENGTH_SHORT).show();
            }
        });
        broken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("p","/myapp/Broken/"));
            }
        });
        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("p","/myapp/English/"));
            }
        });
        old.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("p","/myapp/Old/"));
            }
        });
        pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("p","/myapp/Pop/"));
            }
        });
        workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("p","/myapp/Workout/"));
            }
        });

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.nav_download:
                        Intent intent = new Intent(getApplicationContext(),moveplaylist.class).putExtra("link","https://mp3quack.lol/");
                        startActivity(intent);
                        break;
                    case R.id.nav_home:
                        Toast.makeText(firstview.this, "case2", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(firstview.this,firstview.class);
                        startActivity(intent2);
                        break;
                    case R.id.nav_youtube:
                        Intent intent1 = new Intent(getApplicationContext(),moveplaylist.class).putExtra("link","https://320ytmp3.com/en33/");
                        startActivity(intent1);
                        break;
                }
                return true;
            }
        });


    }
    private void user_permission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.VIBRATE,Manifest.permission.WAKE_LOCK)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START) || background == true)
        {
            drawerLayout.closeDrawer(GravityCompat.START);
            bg.performClick();
            background = false;
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.nav_folder:
                Toast.makeText(firstview.this, "Playlist Folder Created", Toast.LENGTH_SHORT).show();
                String folder_main = "myapp";

                File f = new File(Environment.getExternalStorageDirectory(), folder_main);
                if (!f.exists()) {
                    f.mkdirs();
                }
                File f1 = new File(Environment.getExternalStorageDirectory() + "/" + folder_main, "Broken");
                if (!f1.exists()) {
                    f1.mkdirs();
                }
                File f2 = new File(Environment.getExternalStorageDirectory() + "/" + folder_main, "Punjabi");
                if (!f2.exists()) {
                    f2.mkdirs();
                }
                File f3 = new File(Environment.getExternalStorageDirectory() + "/" + folder_main, "Travel");
                if (!f3.exists()) {
                    f3.mkdirs();
                }
                File f4 = new File(Environment.getExternalStorageDirectory() + "/" + folder_main, "English");
                if (!f4.exists()) {
                    f4.mkdirs();
                }
                File f5 = new File(Environment.getExternalStorageDirectory() + "/" + folder_main, "Old");
                if (!f5.exists()) {
                    f5.mkdirs();
                }
                File f6 = new File(Environment.getExternalStorageDirectory() + "/" + folder_main, "Pop");
                if (!f6.exists()) {
                    f6.mkdirs();
                }
                File f7 = new File(Environment.getExternalStorageDirectory() + "/" + folder_main, "Workout");
                if (!f7.exists()) {
                    f7.mkdirs();
                }
                File f8 = new File(Environment.getExternalStorageDirectory() + "/" + folder_main, "all");
                if (!f8.exists()) {
                    f8.mkdirs();
                }
                break;
        }
        return true;
    }



}