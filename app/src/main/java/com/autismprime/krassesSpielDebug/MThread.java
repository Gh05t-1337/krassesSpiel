package com.autismprime.krassesSpielDebug;

import android.media.MediaPlayer;
import java.util.Random;

public class MThread extends Thread {
    Lines v;
    boolean running;

    float st=0;
    float sty=0;

    public MThread(Lines V){
        v=V;
    }
    void setRunning(boolean run){
        running=run;
    }

//Why do Java programmers wear glasses? Because they don’t C#
    @Override
    public void run(){
        final MediaPlayer m=MediaPlayer.create(v.getContext(),R.raw.laser);
        m.setVolume(0.1f,0.1f);

        long startTime = System.nanoTime();
        long shootCount=0;
        long count=0;
        long badBulletTimer=2000;
        Random r=new Random();

        while (running){
            long deltaTime = System.nanoTime() - startTime;
            startTime = System.nanoTime();
            shootCount+=deltaTime;
            count+=deltaTime;
            v.movVec=-3*deltaTime/1000000;
            v.movVecY=-3*deltaTime/1000000;

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
//A journalist asked a programmer:- What makes code bad? No comment.
            if(!((v.blauPos>v.weit+120&&st>0)||(v.blauPos<-120&&st<0))){
                v.blauPos+=st*deltaTime/10000000;
            }
            if(!((v.blauPosY>v.hoch+60&&sty>0)||(v.blauPosY<0&&sty<0))&&v.vert){
                v.blauPosY+=sty*deltaTime/10000000;

            }

            if(v.deadRadius>=200){
                setRunning(false);
            }

            v.postInvalidate();
            try{
                Thread.sleep(10);
            }catch (Exception e){

            }
        }
    }


}
