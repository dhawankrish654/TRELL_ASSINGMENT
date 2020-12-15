package com.example.trell_assingment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

public class final_show extends AppCompatActivity {
    Uri uri;
    VideoView v;
    boolean playing;

    ImageView   play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_show);
        play = findViewById(R.id.imageView2);

        v = findViewById(R.id.videoView2);



        Intent i = getIntent(
        );
        if (i != null) {
            String path = i.getStringExtra("comressedfilepath");
            uri = Uri.parse(path);
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Video compressed and saved to "+path,
                    Toast.LENGTH_SHORT);

            toast.show();
            v = findViewById(R.id.videoView2);
            v.setVideoURI(uri);
            v.start();


            playing = true;

            setLoist();
        }
    }

    private void setLoist() {
        v.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                v.start();
                sec=mediaPlayer.getDuration()/1000;
                int mn=00;
                int hr=00;
                if(sec>60)
                    mn=sec/60;
                sec=sec-(mn*60);
                if(mn>60)
                    hr=mn/60;
                mn=mn-(hr*60);
                mediaPlayer.setLooping(true);

            }
        });
    }
    int sec;
    public void playpUSE(View view) {
        if(playing==true)
        {
            v.pause();
            play.setImageResource(R.drawable.play);
            playing=false;
        }
        else{
            v.start();

            play.setImageResource(R.drawable.pause);
            playing=true;



        }


    }
}