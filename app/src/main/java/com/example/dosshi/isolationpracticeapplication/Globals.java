package com.example.dosshi.isolationpracticeapplication;

import android.app.Application;

import java.util.ArrayList;

public class Globals extends Application {
    //グローバル変数
    String slctParts = "";
    ArrayList watchData = new ArrayList();

    public void valueInit() {
        String slctParts = "";
        watchData.clear();
    }

    public void watchDataSet(ArrayList array){
        watchData = array;
    }
}
