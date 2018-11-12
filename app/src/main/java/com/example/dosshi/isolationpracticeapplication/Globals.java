package com.example.dosshi.isolationpracticeapplication;

import android.app.Application;

import java.util.ArrayList;

public class Globals extends Application {
    //グローバル変数
    String slctParts = "";
    ArrayList watchData = new ArrayList();
    ArrayList mobileX = new ArrayList();
    ArrayList mobileY = new ArrayList();
    ArrayList mobileZ = new ArrayList();
    int size = 0;

    public void valueInit() {
        String slctParts = "";
        watchData.clear();
        mobileX.clear();
        mobileY.clear();
        mobileZ.clear();
    }

    public void watchDataSet(ArrayList array){
        watchData = array;
    }
    public void mobileAccelDataSet(float x, float y, float z){
        mobileX.add(String.valueOf(x));
        mobileY.add(String.valueOf(y));
        mobileZ.add(String.valueOf(z));
    }
}
