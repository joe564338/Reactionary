package com.packages.joe.reactionary;

import android.content.SharedPreferences;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Serializable;

/**
 * Created by Joe on 6/3/2015.
 */
public class Player{
    private int score;

    public Player(){
        score = 0;
    }
    public int getScore(){
        return score;
    }
    public void incScore(){
        score++;
    }
    public void resetScore(){
        score = 0;
    }

}
