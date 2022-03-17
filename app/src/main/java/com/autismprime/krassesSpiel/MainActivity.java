package com.autismprime.krassesSpiel;

//import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.graphics.Color;
//import android.graphics.drawable.GradientDrawable;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
//import android.net.Uri;
import android.os.Bundle;

import android.app.Activity;
//import android.os.Environment;
import android.util.Log;
//import android.view.MotionEvent;
import android.view.View;
//import android.util.DisplayMetrics;
import android.widget.Button;

import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
//import java.io.IOException;

public class MainActivity extends Activity {
Lines lins;int a=0;MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        Button button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lins.moschän=true;
                if(a!=0 && mp!=null)
                    mp.start();
                setContentView(lins);
            }
        });

        Button button2 = (Button) findViewById(R.id.button3);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lins.moschän=false;
                //lins.setBackgroundColor(Color.rgb(0, 0, 27));
                if(a!=0 && mp!=null)
                    mp.start();
                setContentView(lins);
            }
        });

        //OPTIONS
        Button button3 = (Button) findViewById(R.id.button4);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               // mp.stop();
                //mp.release();
                Intent i=new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(i);
            }
        });

        //SETTINGS
        SharedPreferences sp = null;
        if(!(PreferenceManager.getDefaultSharedPreferences(this).getAll().size()==0)) {
            sp = PreferenceManager.getDefaultSharedPreferences(this);
            Log.i("",sp.getString("color","#0000ff"));
        }

        switch ((sp!=null)?sp.getString("music","0"):"0"){
            case "0": //play nothing
                a=0;
                break;
            case "-1": //play chosen music
                a=1;
                mp =  new MediaPlayer();//MediaPlayer.create(this,a);
                FileInputStream fis = null;
                String uriString=null;
                try {
                    //a File directory = new File(Environment.getExternalStorageDirectory().getPath()+"/Download/badnerlied.mp3");
                    //a fis = new FileInputStream(directory);
                    //a mp.setDataSource(fis.getFD());
                    //mp.setDataSource(this, Uri.parse("content://com.android.providers.media.documents/document/audio%3A46312"));
                    if(SettingsActivity.SettingsFragment.ur!=null) {
                        SharedPreferences.Editor editor = sp.edit();

                        uriString=PathUtil.getPath(this,SettingsActivity.SettingsFragment.ur);

                        editor.putString("uri", uriString);
                        editor.commit();
                        mp = new MediaPlayer().create(this, SettingsActivity.SettingsFragment.ur);//SettingsActivity.SettingsFragment.ur);
                    }
                    else{

                       // try{
                            String imageUriString = sp.getString("uri", "");
                            Log.i("",imageUriString);
                            uriString = imageUriString;
                            File directory = new File(uriString);//Environment.getExternalStorageDirectory().getPath()+"/Download/badnerlied.mp3");
                            fis = new FileInputStream(directory);
                            mp.setDataSource(fis.getFD());
                        /*}catch (Exception e){
                            e.printStackTrace();
                        }*/
                        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        //mp.setDataSource(Environment.getExternalStorageDirectory().getPath()+"/Music/badnerlied.mp3");//content://com.android.providers.media.documents/document/audio%3A46312");
                        mp.prepare();
                    }

                } catch (Exception e) {
                    a=0;
                    e.printStackTrace();
                }

                break;
            /*case "1":
                a=R.raw.krasses_klavier_klimper;
                mp = MediaPlayer.create(this,a);
                break;
            case "2":
                a=R.raw.wallace_and_gromit_train_chase;
                mp = MediaPlayer.create(this,a);
                break;
            case "3":
                a=R.raw.duel_of_fates;
                mp = MediaPlayer.create(this,a);
                break;
            case "4":
                a=R.raw.merkelwave;
                mp = MediaPlayer.create(this,a);
                break;*/

        }

        if(sp!=null) {
            lins=new Lines(this,false);//,sp.getBoolean("singlecolor",false),Color.parseColor(sp.getString("color","#a0a0a0"))
            lins.vert = sp.getBoolean("vert", true);
            lins.bug = sp.getBoolean("bug", true);
            lins.blauGes = Float.parseFloat(sp.getString("ges", ""));
            lins.explosionColor=Color.parseColor(sp.getString("excolor","#ffffff"));
            lins.spaceshipColor=Color.parseColor(sp.getString("scolor","#ffffff"));
            lins.squaresColor=Color.parseColor(sp.getString("qcolor","#000000"));
            lins.badBulletsColor=Color.parseColor(sp.getString("bbcolor","#000000"));
            lins.bbOutColor=Color.parseColor(sp.getString("bbocolor","#ffffff"));
            lins.uiColor=Color.parseColor(sp.getString("uicolor","#ffffff"));
            lins.bulletsColor=Color.parseColor(sp.getString("bcolor","#ffffff"));
            lins.color_radius=Float.parseFloat(sp.getString("color_radius","16"));
            lins.pew=sp.getBoolean("pewpew",false);
            lins.teleport=sp.getBoolean("teleport",true);
            if(sp.getBoolean("singlegradcolor",false)){
                int[] color=new int[Integer.parseInt(sp.getString("color_count","2"))];
                for(int i=0;i<color.length;i++)
                    color[i]=Color.parseColor(sp.getString("color"+String.valueOf(i+1),"#0a0a0a"));
                lins.singleGradientBackground(color);
                lins.singleColor=true;
            }
        }
        else {
            lins=new Lines(this,false);
        }

        //BACKGROUND MUSIC
        if(mp!=null) {
            mp.setLooping(true); // Set looping
            mp.setVolume(100, 100);
        }
    }

   int media_pos=0;

   @Override
   protected void onPause() {
       super.onPause();

       if(mp!=null&&mp.isPlaying()) {
           mp.pause();
           media_pos = mp.getCurrentPosition();
       }
   }
    @Override
    protected void onResume() {
        super.onResume();

        if(mp!=null&&media_pos!=0) {
            mp.seekTo(media_pos);
            mp.start();
        }

    }

   /* @Override
    protected void onDestroy() {
        super.onDestroy();
        if(lins.mn!=null) {
            lins.mn.setRunning(false);
            try {
                lins.mn.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int id= android.os.Process.myPid();
        android.os.Process.killProcess(id);
        //MainActivity.this.finish();
        //System.exit(0);
    }*/

    @Override
    public void onBackPressed() {
        if(mp!=null) {
            mp.stop();
            mp.release();
            mp=null;
        }
       // super.onBackPressed();
        lins.mn.setRunning(false);
        try {
            lins.mn.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lins=null;
        //setContentView(R.layout.activity_main);
        finish();
        startActivity(getIntent());

    }
}
