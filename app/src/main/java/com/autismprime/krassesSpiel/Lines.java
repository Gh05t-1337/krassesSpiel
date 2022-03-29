package com.autismprime.krassesSpiel;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import java.util.ArrayList;
import java.util.Random;


public class Lines extends SurfaceView implements View.OnTouchListener, SensorEventListener {
    Paint pain=new Paint();
    DisplayMetrics mets=new DisplayMetrics();

    Sensor sen;
    SensorManager man;
    int hoch;
    int weit;
    SurfaceHolder hold;

    MThread mn=new MThread(this);

    ArrayList<float[]> bullets = new ArrayList<>();
    ArrayList<float[]> badBullets = new ArrayList<>();
    ArrayList<int[]> sqrs = new ArrayList<>();

    float movVec=0;
    float movVecY=0;

    float blauPos=100;
    float blauPosY;

    int points=0;
    boolean dead=false;
    int deadRadius=0;
    boolean moschän;

    //User Settings
    float blauGes=9;
    boolean vert=true;
    boolean bug=true;

    boolean singleColor;
    boolean pew=false;
    boolean teleport=true;

    int spaceshipColor=Color.rgb(255, 255, 255);
    int bulletsColor=Color.rgb(255, 255, 255);
    int explosionColor=Color.rgb(255,255,255);

    int squaresColor=Color.rgb(0, 0, 0);
    int badBulletsColor=Color.rgb(0, 0, 0);
    int bbOutColor=Color.rgb(255, 255, 255);

    int uiColor=Color.rgb(255,255,255);
    float color_radius=16;

    View view;
    int[] colors;
    final String[][] kreisFarben= {{"#ff5096","#ffb100"},{"#d88bff","#c2eb00"},{"#ff7d6a","#99f119"},
            {"#ff7d6a","#2ad8af"},{"#d88bff","#00e48d"},{"#54c1f8","#4dd472"},{"#41acff","#ffe900"},
            {"#e9f500","#ff7e4d"}, {"#ff799d","#8a6ad2"},{"#00f293","#ff486c"},{"#fe7b4a","#8af82f"},
            {"#ff2e3d","#6658ff"},{"#00dd1d","#6658ff"},{"#9a3cf1","#e8f300"},{"#b561eb","#00dd5e"},
            {"#ffb800","#c33ee0"}};
    GradientDrawable gd;


    public Lines(Context con,boolean moschn){
        super(con);
        view=this;

        man=(SensorManager)con.getSystemService(Context.SENSOR_SERVICE);
        sen=man.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        man.registerListener(this,sen,SensorManager.SENSOR_DELAY_NORMAL);

        Lines lines=this;
        hold=getHolder();
        hold.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mn.setRunning(true);
                    mn.start();
                }
                catch (Exception e){
                    mn=new MThread(lines);
                    mn.setRunning(true);
                    mn.start();
                }
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mn.setRunning(false);
            }
        });

        setOnTouchListener(this);
        setSystemUiVisibility(SYSTEM_UI_FLAG_FULLSCREEN|SYSTEM_UI_FLAG_IMMERSIVE_STICKY|SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        init();
        ((Activity) con).getWindowManager().getDefaultDisplay().getMetrics(mets);
        hoch=mets.heightPixels;
        weit=mets.widthPixels;

        moschän=moschn;
        sqrs.add(new int[]{10, 50, weit/8, hoch/8});
        sqrs.add(new int[]{400, 90, weit/4, hoch/16});
        sqrs.add(new int[]{500, 40, weit/6, hoch/16});

        colors = new int[]{Color.parseColor(kreisFarben[0][0]), Color.parseColor(kreisFarben[0][1])};
        gd = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM, colors);
        gd.setCornerRadius(0f);
        this.setBackground(gd);

        blauPosY=(hoch - (hoch / 400)) - 20;
    }

    void init(){
        pain.setColor(Color.BLACK);
    }int o=0;

    private void animateColors(int mod){
        int l= kreisFarben.length;

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.parseColor(kreisFarben[((points / mod) % l + l - 1) % l][0]),
                Color.parseColor(kreisFarben[(points / mod) % l][0]));
        ValueAnimator colorAnimation2 = ValueAnimator.ofObject(new ArgbEvaluator(), Color.parseColor(kreisFarben[((points / mod) % l + l - 1) % l][1]),
                Color.parseColor(kreisFarben[(points / mod) % l][1]));

        colorAnimation.setDuration(700); // milliseconds
        colorAnimation2.setDuration(700);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                colors[0] = (int) animator.getAnimatedValue();
                gd = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM, new int[]{colors[0], colors[1]});
                gd.setCornerRadius(0f);
                view.setBackground(gd);
            }
        });
        colorAnimation2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                colors[1] = (int) animator.getAnimatedValue();
                gd = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM, new int[]{colors[0], colors[1]});
                gd.setCornerRadius(0f);
                view.setBackground(gd);
            }

        });
        colorAnimation.start();
        colorAnimation2.start();
    }


    @Override
    public void onDraw(Canvas can){
        pain.setColor(bulletsColor);

        //draw Bullets and check if Enemie is hit
        int i=0;
        while(i< bullets.size()){
            try {
                //bullet um movVec in y-Richtung bewegen
                bullets.set(i, new float[]{bullets.get(i)[0], (bullets.get(i)[1] + movVec)}); //manchmal scheinbar grundlos NULL-Pointer Exeption
            }
            catch (Exception e){
                e.printStackTrace();
            }

            can.drawCircle(bullets.get(i)[0],bullets.get(i)[1],8,pain);

            //check if enemie is hit
            int s=0;
            Random r = new Random();
            while(s<sqrs.size()) {
                //Falls bullet in sqare
                if (bullets.get(i)[1]<sqrs.get(s)[1]+sqrs.get(s)[3] && (bullets.get(i)[1]>sqrs.get(s)[1]||bug) && //VertikalFeature
                        bullets.get(i)[0]>sqrs.get(s)[0]&&bullets.get(i)[0]<sqrs.get(s)[0]+sqrs.get(s)[2]){

                    //increase points
                    points++;

                    //change color every 100 points
                    int mod=100;
                    if(points%mod==0&&!singleColor) {
                        animateColors(mod);
                    }

                    //remove hit square and add a new one at random position
                    sqrs.remove(s);
                    sqrs.add(new int[]{r.nextInt((weit-weit/3)  + 1), r.nextInt((hoch/3*2-hoch/3)  + 1),r.nextInt(((weit/3-weit/8)  + 1)) + weit/8 ,r.nextInt(((hoch/4-hoch/10)  + 1)) + hoch/10});
                }
                s++;
            }

            if(bullets.get(i)[1]<0){
                bullets.remove(i);
                continue;
            }
            i++;
        }

        //draw Squares
        i=0;
        pain.setColor(squaresColor);
        while(i< sqrs.size()){
            can.drawRect(sqrs.get(i)[0],sqrs.get(i)[1],sqrs.get(i)[0]+sqrs.get(i)[2],sqrs.get(i)[1]+sqrs.get(i)[3],pain);
            i++;
        }

        //draw BadBullets and check if Player is hit
        i=0;
        while(i< badBullets.size()){
            try {
                badBullets.set(i, new float[]{badBullets.get(i)[0], (badBullets.get(i)[1] - movVec / 2)});
            }
            catch (Exception e){
                //Log.wtf("", ""); is a really nice name for a function. used it often. wtf obviously means [W]hat a [T]errible [F]ailure.
                e.printStackTrace();
            }

            //radial gradient color for enemie bullets
            Shader shader=new RadialGradient(badBullets.get(i)[0],badBullets.get(i)[1],color_radius, new int[]{badBulletsColor, bbOutColor},null, Shader.TileMode.REPEAT);
            pain.setShader(shader);

            can.drawCircle(badBullets.get(i)[0],badBullets.get(i)[1],8,pain);

            //check if player is hit
                if (badBullets.get(i)[1]<blauPosY && badBullets.get(i)[1]>blauPosY-80&&
                        badBullets.get(i)[0]<blauPos+110&&badBullets.get(i)[0]>blauPos+10){
                    dead=true;
                    mn.st=0;
                    mn.sty=0;
                    badBullets.remove(i);
                    continue;

                }

            if(badBullets.get(i)[1]>hoch+100){
                badBullets.remove(i);
                continue;
            }
            i++;
        }
        pain.setShader(null);

        //Spaceship
        if(!dead) {
            pain.setColor(spaceshipColor);
            pain.setStyle(Paint.Style.FILL);
            Path wallpath = new Path();
            wallpath.reset();
            wallpath.moveTo(blauPos, blauPosY);
            wallpath.lineTo(blauPos + 60, blauPosY- 100);
            wallpath.lineTo(blauPos + 120, blauPosY);

            can.drawPath(wallpath, pain);
        }
        else{
            pain.setColor(explosionColor);
            if(deadRadius<70){
                can.drawCircle(blauPos+60,blauPosY-50,deadRadius,pain);
            }
            deadRadius+=2;
        }

        //Points
        pain.setColor(uiColor);
        pain.setTextSize(32);
        can.drawText(String.valueOf(points), 2, 34, pain);
        if(deadRadius>=200){
            pain.setTextSize(points);
            pain.setTextAlign(Paint.Align.CENTER);
            can.drawText(String.valueOf(points), weit/2, hoch/2-(pain.descent()+pain.ascent())/2, pain);
            pain.setTextAlign(Paint.Align.LEFT);

            mn.setRunning(false);

            try {
                mn.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    float lastPointerPos=0;
    float lastPointerPosY=0;
    float[] downPos=new float[2];
    @Override
    public boolean onTouch(View v,MotionEvent e){
        if(!dead &&!moschän) {
            if (e.getAction() == MotionEvent.ACTION_DOWN &&!vert){
                if(teleport) {
                    if (mn.st > 0 && e.getX() >= weit / 2 && !(blauPos>weit+120)) {
                        blauPos += 80;
                    }
                    if (mn.st < 0 && e.getX() < weit / 2 && !(blauPos<-120)) {
                        blauPos -= 80;
                    }
                }
                mn.st = (e.getX() >= weit / 2) ? blauGes : -blauGes;
            }
            else if(e.getAction()==MotionEvent.ACTION_MOVE){

                if(!vert) {
                    if (e.getX() > lastPointerPos + 10f) mn.st = blauGes;
                    if (e.getX() < lastPointerPos - 10f) mn.st = -blauGes;
                }
                else{
                    float dy=e.getY()-lastPointerPosY;
                    float dx=e.getX()-lastPointerPos;
                    mn.st = blauGes*dx/10;
                    mn.sty = blauGes*dy/10;
                }
            }
            lastPointerPos=e.getX();
            lastPointerPosY=e.getY();

            v.postInvalidate();
        }

        boolean movd=false;
        if(e.getAction()==MotionEvent.ACTION_DOWN){
            downPos[0]=e.getX();
            downPos[1]=e.getY();
        }
        if(e.getAction()==MotionEvent.ACTION_UP){
            movd=!(downPos[0]-e.getX()==0&&downPos[1]-e.getY()==0);
        }
        if(deadRadius>=200 && !movd && e.getAction()==MotionEvent.ACTION_UP){
            mn=new MThread(this);
            bullets = new ArrayList<>();
            badBullets = new ArrayList<>();
            sqrs = new ArrayList<>();
            movVec=0;movVecY=0;

            if(!singleColor) {
                colors = new int[]{Color.parseColor(kreisFarben[0][0]), Color.parseColor(kreisFarben[0][1])};
                //create a new gradient color
                gd = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM, colors);
                gd.setCornerRadius(0f);
                this.setBackground(gd);
            }

            lastValue=0;
            lastValueY=0;
            blauPos=100;
            blauPosY=(hoch - (hoch / 400)) - 20;
            points=0;
            dead=false;
            deadRadius=0;
            sqrs.add(new int[]{10, 50, weit/8, hoch/8});
            sqrs.add(new int[]{400, 90, weit/4, hoch/16});
            sqrs.add(new int[]{500, 40, weit/6, hoch/16});
            mn=new MThread(this);
            mn.setRunning(true);
            mn.start();
        }

        return true;
    }
    float lastValue=0;
    float lastValueY=0;
    @Override
    public void onSensorChanged(SensorEvent e){
        if(!dead&&moschän) {
                if(e.values[0]<blauGes&&e.values[0]>-blauGes)mn.st=-e.values[0]*3f;
                else if(e.values[0]>=blauGes)mn.st=-blauGes;
                else mn.st=blauGes;

                if(vert) {
                    if (e.values[1] < blauGes && e.values[1] > -blauGes)
                        mn.sty = e.values[1] * 3f;
                    else if (e.values[1] >= blauGes) mn.sty = blauGes;
                    else mn.st = -blauGes;
                }

            postInvalidate();
            lastValue=e.values[0];
            lastValueY=e.values[1];
        }

    }
    @Override
    public void onAccuracyChanged(Sensor sensor,int accuracy){ }

    public void singleGradientBackground(int[] color){
        if(color.length>1) {
            gd = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM, color);
            gd.setCornerRadius(0f);
            this.setBackground(gd);
        }
        else{
            this.setBackgroundColor(color[0]);
        }
    }
}
