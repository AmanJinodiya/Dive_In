package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class moveplaylist extends AppCompatActivity {
    CheckBox punjab, Travel, Broken, English, Old, Pop, Workout;
    ImageView submit;
    boolean p = false;
//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moveplaylist);
        punjab = findViewById(R.id.punjab);
        Travel = findViewById(R.id.travel);
        Broken = findViewById(R.id.Broken);
        English = findViewById(R.id.english);
        Old = findViewById(R.id.old);
        Pop = findViewById(R.id.pop);
        Workout = findViewById(R.id.workout);
        ArrayList<String> arrayList = new ArrayList<>();


        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p = punjab.isChecked();
                boolean travel = Travel.isChecked();
                boolean old = Old.isChecked();
                boolean broken = Broken.isChecked();
                boolean english = English.isChecked();

                boolean pop = Pop.isChecked();
                boolean wokout = Workout.isChecked();
                if (p) {
                    arrayList.add("Punjabi");
                }
                if (travel) arrayList.add("Travel");
                if (broken) arrayList.add("Broken");
                if (english) arrayList.add("English");
                if (old) arrayList.add("Old");
                if (pop) arrayList.add("Pop");
                if (wokout) arrayList.add("Workout");
                Intent intent1 = getIntent();
                Bundle bundle = intent1.getExtras();
                String link = bundle.getString("link");
                Toast.makeText(moveplaylist.this, String.valueOf(arrayList.size()), Toast.LENGTH_SHORT).show();
                add_files_inplaylist(arrayList);
                Uri uri11 = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri11);
//
                startActivity(intent);

            }
        });


    }

    public void add_files_inplaylist(ArrayList<String> ar) {
        ArrayList<File> arrayList = new ArrayList<>();
        File Download = new File(Environment.getExternalStorageDirectory().toString() + "/Download/");
        File[] files = Download.listFiles();
        int count = 0;
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith(".mp3")) {
                arrayList.add(files[i]);
                count++;
            }
        }

        boolean[] download = {false};
        int MINUTES = 1;
        int[] z = {0};
        int[] count1 = {count};

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void run() {
                z[0] = 0;
                File[] files = Download.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].getName().endsWith(".mp3")) z[0]++;
                }

                if (z[0] > count1[0]) {
                    ArrayList<File> arrayList1 = new ArrayList<>();
                    ArrayList<File> arrayList2 = new ArrayList<>();
                    File Download1 = new File(Environment.getExternalStorageDirectory().toString() + "/Download/");
                    File[] files1 = Download1.listFiles();
                    for (int i = 0; i < files1.length; i++) {
                        if (files1[i].getName().endsWith(".mp3")) {
                            arrayList1.add(files1[i]);
                            arrayList2.add(files1[i]);
                        }
                    }

                    Set<File> set1 = new HashSet<File>();
                    set1.addAll(arrayList);
                    Set<File> set2 = new HashSet<File>();
                    set2.addAll(arrayList1);
                    set2.removeAll(set1);

                    String nameoffile = set2.iterator().next().getName();

                    File source = set2.iterator().next();
                    File all = new File(Environment.getExternalStorageDirectory()+"/myapp/all/"+nameoffile);
                    try {
                        copyFile(source,all);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ArrayList<File>path_ofallfolder = new ArrayList<>();
                    for(int i = 0; i<ar.size();i++)
                    {
                        File junk = new File(Environment.getExternalStorageDirectory()+"/myapp/"+ar.get(i)+"/"+nameoffile);
                        try {
                            copyFile(source,junk);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    source.delete();

                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(1000);
                    download[0] = true;
                }
                if (download[0] == true) {
                    timer.cancel();
                }
            }
        }, 0, 1000 * 60 * MINUTES);
    }


    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
}