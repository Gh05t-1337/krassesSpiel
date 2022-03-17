package com.autismprime.krassesSpiel;

//import android.media.MediaPlayer;
//import android.util.Log;

//import java.util.ArrayList;
import android.media.MediaPlayer;

import java.util.Random;

//import java.util.ArrayList;
public class MThread extends Thread {
    Lines v;
    boolean running;
   // boolean ded;
    float st=0;
    float sty=0;//NEU für Vertikal

    //float ballYges=2;
    //float ballXges=1;
    public MThread(Lines V){
        v=V;
    }
    void setRunning(boolean run){
        running=run;
    }


    @Override
    public void run(){
        final MediaPlayer m=MediaPlayer.create(v.getContext(),R.raw.laser);
        m.setVolume(0.1f,0.1f);
       // Log.i("","Thread starting");
        long startTime = System.nanoTime();
        //int yPos=(v.hoch-(v.hoch/400));//unnötig wegen Vertikal
        long shootCount=0;
        long count=0;
        long badBulletTimer=2000;
        Random r=new Random();
        //Log.i("", "thread"+String.valueOf(sty)+","+String.valueOf(st));
        while (running){
            long deltaTime = System.nanoTime() - startTime;
            startTime = System.nanoTime();
            shootCount+=deltaTime;
            count+=deltaTime;
            v.movVec=-3*deltaTime/1000000;
            v.movVecY=-3*deltaTime/1000000;//NEU für Vertikal

            //Bullets
            if(shootCount>220*1000000 &&!v.dead){
                v.bullets.add(new float[]{v.blauPos+60, v.blauPosY-100});
                if(v.pew)
                    m.start();
                shootCount=0;
            }
            //BadBullets
            if(count>badBulletTimer*1000000 &&!v.dead){
                v.badBullets.add(new float[]{ r.nextInt((v.weit-16 - 0) + 1) + 0, 0});
                if (badBulletTimer>300) {
                    badBulletTimer *= 0.9;
                }
                count=0;
            }
            //ballYges+=(ballYges>0)?1*deltaTime:-1*deltaTime;
            //ballXges+=(ballXges>0)?0.001:-0.001;

            if(!((v.blauPos>v.weit+120&&st>0)||(v.blauPos<-120&&st<0))){
                v.blauPos+=st*deltaTime/10000000;
            }
            if(!((v.blauPosY>v.hoch+60&&sty>0)||(v.blauPosY<0&&sty<0))&&v.vert){
                v.blauPosY+=sty*deltaTime/10000000;

            }
           // v.rotPos-=st;
            if(v.deadRadius>=200){
               // ded=true;
                setRunning(false);
            }
            //v.ballX+=ballXges;
            //v.ballY+=ballYges;
            v.postInvalidate();
            try{
                Thread.sleep(10);
            }catch (Exception e){

            }
           // if(v.ballX>=v.weit){ballXges=-ballXges;}
           // if(v.ballX<=0){ballXges=-ballXges;}
           // if(v.ballX-v.blauPos<=v.weit/5&&v.ballX-v.blauPos>=0&&v.ballY<v.hoch-v.hoch/400&&v.ballY>=v.hoch-(v.hoch/400+20)){ballYges=-Math.abs(ballYges);}
           // if(v.ballX-v.blauPos<=v.weit/5&&v.ballX-v.blauPos>=0&&v.ballY>=v.hoch/400&&v.ballY<=v.hoch/400+20){ballYges=Math.abs(ballYges);}
        }
       // Log.i("","Thread Ended");

    }


}
