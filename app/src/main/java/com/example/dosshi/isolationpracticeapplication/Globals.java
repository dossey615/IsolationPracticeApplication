package com.example.dosshi.isolationpracticeapplication;

import android.app.Application;
import android.media.AudioAttributes;
import android.media.SoundPool;

import java.util.ArrayList;

public class Globals extends Application {
    //グローバル変数
    String slctParts = "";
    SoundPool soundPool;

    ArrayList watchData = new ArrayList();
    ArrayList mobileX = new ArrayList();
    ArrayList mobileY = new ArrayList();
    ArrayList mobileZ = new ArrayList();

    float old_X = 0;
    float old_Y = 0;
    float old_Z = 0;
    int size = 0;
    int countBgm;


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

    public String[] getWatchData(int i){
        String save = (String)(this.watchData.get(i));
        String[] data = save.split(",", 0);
        return data;
    }

    public void mobileAccelDataSet(float x, float y, float z){
        if(mobileX.size() != 0){
          x = lowpassfilter(old_X, x);
          y = lowpassfilter(old_Y, y);
          z = lowpassfilter(old_Z, z);
          old_X = x;
          old_Y = y;
          old_Z = z;
        }
        mobileX.add(String.valueOf(x));
        mobileY.add(String.valueOf(y));
        mobileZ.add(String.valueOf(z));
    }

    public float lowpassfilter(float value1, float value2){
        value2 = (float)(value1 * 0.9 + value2 * 0.1);
        return value2;
    }

    public void soundInit(){
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(5)
                .build();
        countBgm = soundPool.load(this, R.raw.count, 1);
    }
}
