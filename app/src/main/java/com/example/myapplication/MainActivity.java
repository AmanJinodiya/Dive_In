package com.example.myapplication;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.BasePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String []items;
    RecyclerView recyclerView;
    MediaPlayer mediaPlayer;
    String pp;
    ImageView card1;
//    ImageButton play;
    ImageView imageView;
    long animation_duration = 1000;
    int pause_me = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listview);
        listView = findViewById(R.id.listview);
        listView.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }

//        imageView = findViewById(R.id.imageView1);
        card1 = findViewById(R.id.card1);
        
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ObjectAnimator animatory = ObjectAnimator.ofFloat(card1,"Y",-80f);
                animatory.setDuration(animation_duration);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animatory);
                animatorSet.start();
                listView.setVisibility(View.VISIBLE);
            }
        });

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

        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songname = (String)listView.getItemAtPosition(position);
                startActivity(new Intent(getApplicationContext(),recycle.class).putExtra("songname",songname).putExtra("songs",mysongs).putExtra("pos",position).putExtra("pause_me",pause_me).putExtra("pat",pp));
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
            ImageView imageView = view.findViewById(R.id.iv);
            t.setSelected(true);
            t.setText(items[position]);

            String path = Environment.getExternalStorageDirectory().toString()+pp;

            File directory = new File(path);
            File[] files = directory.listFiles();

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(files[position].getPath());
            byte [] data = mmr.getEmbeddedPicture();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            imageView.setImageBitmap(bitmap);
            return view;
        }
    }




}