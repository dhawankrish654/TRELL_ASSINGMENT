package com.example.trell_assingment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;

import java.io.File;

public class VIEW_SELECTED_VIDEO extends AppCompatActivity {
    ProgressDialog progressDialog;
    VideoView v;
    Uri uri;
    boolean playing;
    ImageView play;
    EditText bitratel;
    FFmpeg ffmpeg;
    File dest;
    String destfilePath;
    String original_path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_v_i_e_w__s_e_l_e_c_t_e_d__v_i_d_e_o);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Compressing Please Wait...");
        play = findViewById(R.id.play);
        progressDialog.setCancelable(false);
        bitratel=findViewById(R.id.bitrate);
        starttime="0";


        v = findViewById(R.id.videoView);
        Intent i = getIntent(
        );
        if (i != null) {
            String path = i.getStringExtra("uri");
            uri = Uri.parse(path);
            v = findViewById(R.id.videoView);
            v.setVideoURI(uri);
            v.start();


            playing = true;
            play.setImageResource(R.drawable.pause);
            setLoist();

        }
    }
    int sec;
    int totalsec;
    private void setLoist() {
        v.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                v.start();

                sec=mediaPlayer.getDuration()/1000;
                totalsec=sec;
                endtime=String.valueOf(totalsec);
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


    public void compress(View view) {
        String newrate=bitratel.getText().toString().trim();
        if(newrate.length()==0)
        {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "ENTER A VALID RATE",
                    Toast.LENGTH_SHORT);

            toast.show();
            return;
        }
        File folder = new File(Environment.getExternalStorageDirectory()+"/CompressedVideos");
        if(!folder.exists())
        {
            folder.mkdir();
        }
        getRealPathFromUri(getApplicationContext(),uri);
        String filename=original_path.substring(original_path.lastIndexOf("/")+1);
        dest = new File(folder,filename);
        destfilePath=folder.getAbsolutePath()+"/"+filename;
        Log.d("path", destfilePath);
// ffmpeg Command for trimming video        String[] comm = {"-ss", "" + Integer.parseInt(startTime) , "-y", "-i", original_path, "-t", "" + (Integer.parseInt(endTime)-Integer.parseInt(startTime)),"-vcodec", "mpeg4", "-ac", "2", "-ar", "22050", destfilePath};

// ffmpeg  command line for compressing video        String[] comm={ffmpeg -i originalpath -b:v 64k -bufsize 64k output.avi}
        String[] comm= {"-y", "-i", original_path, "-strict", "experimental", "-vcodec", "libx264", "-preset", "ultrafast", "-crf", "24", "-acodec", "aac", "-ar", "22050", "-ac", "2", "-b", newrate+"k", "-s", "1280x720", destfilePath};

        loadFFMpegBinary();
        execFFMpegCommand(comm);
    }
    private void getRealPathFromUri(Context context, Uri contentUri)
    {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor=context.getContentResolver().query(contentUri,proj,null,null,null);
            int coulumn_index=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            original_path = cursor.getString(coulumn_index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
            }
        }
    }
    public void loadFFMpegBinary()
    {
        try {
            if(ffmpeg==null)
            {
                ffmpeg = FFmpeg.getInstance(this);
            }
            ffmpeg.loadBinary(new LoadBinaryResponseHandler(){
                @Override
                public void onFailure() {
                    super.onFailure();
                }

                @Override
                public void onSuccess() {
                    super.onSuccess();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public  void execFFMpegCommand(String[] command)
    {
        try{

            ffmpeg.execute(command, new ExecuteBinaryResponseHandler()
            {
                @Override
                public void onFailure(String message) {
                    Log.d("res","Failed : "+message);
                }

                @Override
                public void onSuccess(String message) {
                    Log.d("res","success : "+message);
                }

                @Override
                public void onProgress(String message) {
                    Log.d("res","progress : "+message);
                }

                @Override
                public void onStart() {

                    progressDialog.show();
                    Log.d("res", "Started command : ffmpeg " + command);
                }

                @Override
                public void onFinish() {
                    Log.d("res","Finished command : ffmpeg "+ command);
                    progressDialog.dismiss();
//                    Uri cmp=Uri.parse(destfilePath);
//                    v.setVideoURI(cmp);
//                    v.start();

                    Intent in = new Intent(VIEW_SELECTED_VIDEO.this, final_show.class);
                    in.putExtra("comressedfilepath", destfilePath);
                    startActivity(in);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

String starttime,endtime;
    public void trim(View view) {
        starttime="0";
        endtime="5";

        File folder = new File(Environment.getExternalStorageDirectory()+"/CompressedVideos");
        if(!folder.exists())
        {
            folder.mkdir();
        }
        getRealPathFromUri(getApplicationContext(),uri);
        String filename=original_path.substring(original_path.lastIndexOf("/")+1);
        dest = new File(folder,filename);
        destfilePath=folder.getAbsolutePath()+"/"+filename;
        Log.d("path", destfilePath);
// ffmpeg Command for trimming video
 String[] comm = {"-ss", "" + Integer.parseInt(starttime) , "-y", "-i", original_path, "-t", "" + (Integer.parseInt(endtime)-Integer.parseInt(starttime)),"-vcodec", "mpeg4", "-ac", "2", "-ar", "22050", destfilePath};

// ffmpeg  command line for compressing video        String[] comm={ffmpeg -i originalpath -b:v 64k -bufsize 64k output.avi}
//        String[] comm= {"-y", "-i", original_path, "-strict", "experimental", "-vcodec", "libx264", "-preset", "ultrafast", "-crf", "24", "-acodec", "aac", "-ar", "22050", "-ac", "2", "-b", newrate+"k", "-s", "1280x720", destfilePath};

        loadFFMpegBinary();
        execFFMpegCommand(comm);













    }
    String d;
    public String showdialbpxs() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(VIEW_SELECTED_VIDEO.this);
        builder1.setMessage("Please Input Start Time Note Max time is :-"+String.valueOf(totalsec));
        builder1.setCancelable(false);
        final EditText rb=new EditText(VIEW_SELECTED_VIDEO.this);
        builder1.setView(rb);

        builder1.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                d=rb.getText().toString().trim();
                starttime=d;

                dialogInterface.cancel();

            }
        });



        AlertDialog alert11 = builder1.create();
        alert11.show();
return d;
    }
    public String showdialbpxe() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(VIEW_SELECTED_VIDEO.this);
        builder1.setMessage("Please Input End Time Note Max time is :-"+String.valueOf(totalsec));
        builder1.setCancelable(false);
        final EditText rb=new EditText(VIEW_SELECTED_VIDEO.this);
        builder1.setView(rb);

        builder1.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                d=rb.getText().toString().trim();
                endtime=d;


                dialogInterface.cancel();

            }
        });



        AlertDialog alert11 = builder1.create();
        alert11.show();
        return d;
    }
}