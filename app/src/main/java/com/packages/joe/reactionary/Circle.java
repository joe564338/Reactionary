package com.packages.joe.reactionary;


import android.graphics.Color;

import java.util.Random;

/**
 * Created by Joe on 6/2/2015.
 */
public class Circle {
    private int color;
    private int x, y, r;
    private static final int PURPLE = 0xFF9400D3;
    private static final int DKGREEN = 0xFF006400;
    private static final int INDRED = 0xFFCD5C5C;
    public Circle(int x, int y, int r){
        this.x = x;
        this.y = y;
        this.r = r;
        Random rand = new Random();
        rand.setSeed(System.nanoTime());
        int c = rand.nextInt(9);
        switch(c){
            case 0: this.color = Color.BLUE;
                break;
            case 1: this.color = Color.CYAN;
                break;
            case 2: this.color = Color.GREEN;
                break;
            case 3: this.color = PURPLE;
                break;
            case 4: this.color = DKGREEN;
                break;
            case 5: this.color = Color.MAGENTA;
                break;
            case 6: this.color = Color.RED;
                break;
            case 7: this.color = INDRED;
                break;
            default: this.color = Color.YELLOW;
                break;
        }
    }
    public Circle(Circle circle){
        this.x = circle.getX();
        this.y = circle.getY();
        this.r = circle.getR();
        this.color = circle.getColor();
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getR(){
        return r;
    }
    public int getColor(){
        return color;
    }
}
