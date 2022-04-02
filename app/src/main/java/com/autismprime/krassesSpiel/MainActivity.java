package com.autismprime.krassesSpiel;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.preference.PreferenceManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
        //Why do most programmers use dark mode? Because the light attracts too many bugs.
        Button button2 = (Button) findViewById(R.id.button3);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lins.moschän=false;
                if(a!=0 && mp!=null)
                    mp.start();
                setContentView(lins);
            }
        });

        //OPTIONS
        Button button3 = (Button) findViewById(R.id.button4);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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

                FileInputStream fis = null;

                //int permisson=ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (SettingsActivity.SettingsFragment.ur!=null) {
                    mp = new MediaPlayer().create(this, SettingsActivity.SettingsFragment.ur);
                }

                if((SettingsActivity.SettingsFragment.ur==null||mp==null)) {
                    mp =  new MediaPlayer();
                    String imageUriString = sp.getString("uri", "kein Musik Pfad gespeichert.");

                    try {
                        File directory = new File(imageUriString);
                        fis = new FileInputStream(directory);
                        Log.i("", imageUriString);

                        mp.setDataSource(fis.getFD());
                        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mp.prepare();
                    } catch (IOException e) {
                        a=0;
                        e.printStackTrace();
                    }
                }


                break;
        }
//What code does a depressed programmer write? "Goodbye world!"
        if(sp!=null) {
            lins=new Lines(this,false);

            lins.vert = sp.getBoolean("vert", true);    //vertical movement
            lins.bug = sp.getBoolean("bug", true);      //Bug

            lins.blauGes = Float.parseFloat(sp.getString("ges", "9"));  //speed

            lins.spaceshipColor=Color.parseColor(sp.getString("scolor","#ffffff")); //spaceship color
            lins.bulletsColor=Color.parseColor(sp.getString("bcolor","#ffffff"));   //spaceship's bullets color
            lins.explosionColor=Color.parseColor(sp.getString("excolor","#ffffff"));    //explosion color

            lins.squaresColor=Color.parseColor(sp.getString("qcolor","#000000"));   //color of squares
            lins.badBulletsColor=Color.parseColor(sp.getString("bbcolor","#000000"));   //color of enemie bullets
            lins.bbOutColor=Color.parseColor(sp.getString("bbocolor","#ffffff"));   //outer color of enemie bullets

            lins.uiColor=Color.parseColor(sp.getString("uicolor","#ffffff"));   //GUI Color

            lins.color_radius=Float.parseFloat(sp.getString("color_radius","16"));  //radius of color gradient of enemie bullets

            lins.pew=sp.getBoolean("pewpew",false); //Pew sound

            lins.teleport=sp.getBoolean("teleport",true);   //teleport feature

            if(sp.getBoolean("singlegradcolor",false)){ //single gradient color background
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
//My programmer friend said I have a high IQ. He said it's 404
    @Override
    public void onBackPressed() {
        if(mp!=null) {
            mp.stop();
            mp.release();
            mp=null;
        }
        lins.mn.setRunning(false);
        try {
            lins.mn.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lins=null;

        finish();
        startActivity(getIntent());

    }
}
