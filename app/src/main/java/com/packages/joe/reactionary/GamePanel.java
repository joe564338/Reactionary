package com.packages.joe.reactionary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MotionEventCompat;

import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by Joe on 6/1/2015.
 */
public class GamePanel  extends SurfaceView implements SurfaceHolder.Callback{
    private MainThread thread;
    private int timer = 60;
    SharedPreferences sp;
    SharedPreferences.Editor edit;
    Player player = new Player();
    boolean gameOver = false;
    ArrayList<Circle> circles = new ArrayList<Circle>();
    int x,y;
    CountDownTimer cdt;
    MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.pop);
    Context context;
    boolean gameStart = true;
    boolean endDelay = false;
    public GamePanel(Context context){
        super(context);
        this.context = context;
        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);
        setFocusable(true);
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;

        while(retry){
            try{
                System.out.println("TEST2");
                thread.setRunning(false);
                thread.join();
                retry = false;
            }catch(InterruptedException e){
                e.printStackTrace();
            }

        }
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder){
        thread.setRunning(true);
        thread.start();

    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = event.getAction();
        int index = MotionEventCompat.getActionIndex(event);
        if(gameStart){
            gameStart = false;
            //timer starts on first touch thus starting the game
            cdt = new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timer = (int)millisUntilFinished/1000;
                }

                @Override
                public void onFinish(){
                    //when timer finishes save scores
                    sp = getContext().getSharedPreferences("Scores", 0);
                    edit = sp.edit();
                    edit.putInt("LastScore", player.getScore());
                    edit.commit();
                    if(sp.getInt("HighScore", 0) < player.getScore())
                        edit.putInt("HighScore", player.getScore());
                    edit.commit();
                    gameOver = true;
                    timer = 60;
                    CountDownTimer endCdt = new CountDownTimer(2000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            endDelay = true;
                        }
                    }.start();
                }
            }.start();
        }
        //Game logic for touch events
        if(!gameOver) {
            System.out.println("The action is " + actionToString(action));
            if (circles.size() > 0) {
                Iterator<Circle> itr = circles.iterator();
                while (itr.hasNext()) {
                    Circle circle = new Circle(itr.next());
                    x = (int) event.getX();
                    y = (int) event.getY();
                    int pointerIndex = event.getActionIndex();
                    int pointerId = event.getPointerId(pointerIndex);

                    if (Math.sqrt(Math.pow((x - circle.getX()), 2) + Math.pow(y - circle.getY(), 2)) < circle.getR()) {
                        itr.remove();
                        player.incScore();
                        if (mp.isPlaying()) {
                            mp.stop();
                            mp.release();
                            mp = MediaPlayer.create(getContext(), R.raw.pop);
                            mp.start();
                        } else {
                            mp.start();
                        }
                    }
                    if (event.getPointerCount() > 1) {
                        System.out.println("MULTITOUCH");

                    }
                }
            }
        }
        else{
            if(event.getAction()== MotionEvent.ACTION_UP && endDelay) {
                gameOver = false;
                gameStart = true;
                player.resetScore();
                circles.clear();
                endDelay = false;
            }
        }
        return true;
    }
    public static String actionToString(int action) {
        switch (action) {

            case MotionEvent.ACTION_DOWN: return "Down";
            case MotionEvent.ACTION_MOVE: return "Move";
            case MotionEvent.ACTION_POINTER_DOWN: return "Pointer Down";
            case MotionEvent.ACTION_UP: return "Up";
            case MotionEvent.ACTION_POINTER_UP: return "Pointer Up";
            case MotionEvent.ACTION_OUTSIDE: return "Outside";
            case MotionEvent.ACTION_CANCEL: return "Cancel";
        }
        return "";
    }
    //game logic for generating circles
    public void update(){

        int counter = 0;
        Random rand = new Random();
        long seed = System.nanoTime();
        rand.setSeed(seed);
        int r = getHeight()/11;
        if(circles.size() == 0){
            int xPos = rand.nextInt(getWidth() - r) + r;
            int yPos = rand.nextInt(getHeight() - r) + r;
            circles.add(new Circle(xPos, yPos, r));
            while(circles.size() < 7 && counter < 5000){
                counter++;
                xPos = rand.nextInt(getWidth() - r*2) + r*2;
                yPos = rand.nextInt(getHeight() - r*2) + r*2;
                for(Circle c : circles){
                    if(Math.sqrt(Math.pow( (xPos-c.getX()),2) + Math.pow(yPos - c.getY(), 2)) > c.getR()*2){
                        circles.add(new Circle(xPos, yPos, r));
                        counter = 0;
                    }
                }
            }
        }
    }
    @Override
    public void draw(Canvas canvas){
        //draw circles, score and time left
        if(!gameOver) {
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setStyle(Paint.Style.FILL);
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            if (circles.size() != 0) {
                for (Circle c : circles) {
                    Paint paint = new Paint();
                    paint.setColor(c.getColor());
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(c.getX(), c.getY(), c.getR(), paint);
                }
            }
            textPaint.setTextSize(35);
            String time = String.valueOf(timer);
            canvas.drawText(time, 25, 25, textPaint);
            String score = String.valueOf(player.getScore());
            canvas.drawText(score, getWidth() - 100, 25, textPaint);
        }
        else {
            //draw game over screen
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            Paint endPaint = new Paint();
            endPaint.setColor(Color.WHITE);
            endPaint.setStyle(Paint.Style.FILL);
            endPaint.setTextSize(45);
            canvas.drawText("Game Over", getWidth() / 4, getHeight() / 2, endPaint);
            canvas.drawText("Your Score: " + player.getScore(), getWidth() / 4, getHeight() / 2 + 40, endPaint);
        }

    }


}
