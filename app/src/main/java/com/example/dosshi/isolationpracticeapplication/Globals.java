package com.example.dosshi.isolationpracticeapplication;

import android.app.Application;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.io.IOException;
import java.util.ArrayList;

public class Globals extends Application {
    //グローバル変数
    String slctParts = "";
    SoundPool soundPool;
    MediaPlayer mediaPlayer;


    ArrayList watchData = new ArrayList();
    ArrayList mobileX = new ArrayList();
    ArrayList mobileY = new ArrayList();
    ArrayList mobileZ = new ArrayList();



    float oldAccel_X = 0;
    float oldAccel_Y = 0;
    float oldAccel_Z = 0;
    float oldSpeed_X = 0;
    float oldSpeed_Y = 0;
    float oldSpeed_Z = 0;
    float timeSpan = 0.03f;

    //加速度から算出した速度
    float speed = 0;
    float difference = 0;
    float differenceX = 0;
    float differenceY = 0;
    float differenceZ = 0;


    int size = 0;
    int countBgm;


    public void valueInit() {
        String slctParts = "";
        watchData.clear();
        mobileX.clear();
        mobileY.clear();
        mobileZ.clear();
        oldAccel_X = 0;
        oldAccel_Y = 0;
        oldAccel_Z = 0;
        oldSpeed_X = 0;
        oldSpeed_Y = 0;
        oldSpeed_Z = 0;

        mobileX.add("0");
        mobileY.add("0");
        mobileZ.add("0");


        //加速度から算出した速度
        speed = 0;
        difference = 0;
        differenceX = 0;
        differenceY = 0;
        differenceZ = 0;
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
        //スマホの値をそれぞれセット
        mobileX.add(CalculationDisplacement(x,oldAccel_X,timeSpan,oldSpeed_X,differenceX,0));
        mobileY.add(CalculationDisplacement(y,oldAccel_Y,timeSpan,oldSpeed_Y,differenceY,1));
        mobileZ.add(CalculationDisplacement(z,oldAccel_Z,timeSpan,oldSpeed_Z,differenceY,2));
    }

    public float noiseClear(float noise){
        if(Math.abs(noise) < 0.024)noise = 0;
        return noise;
    }

    public String CalculationDisplacement(float highpassValue,float oldAccel,float timeSpan,float oldSpeed,float difference,int j){
        // 速度計算(加速度を台形積分する)
        speed = ((highpassValue + oldAccel) * timeSpan) / 2f ;
        // 変位計算(速度を台形積分する)
        difference = ((speed + oldSpeed) * timeSpan) / 2f;
        switch (j){
            case 0:
                oldSpeed_X = speed;
                differenceX = difference;
                break;
            case 1:
                oldSpeed_Y = speed;
                differenceY = difference;
                break;
            default:
                oldSpeed_Z = speed;
                differenceZ = difference;
                break;
        }
        return String.valueOf(difference);
    }

    //加速度の最大値、最小値を求める関数
    public float maxValue(){
        float max = Float.parseFloat((String)mobileX.get(0));
        float x = 0;
        float y = 0;
        float z = 0;
        for(int i = 0; i < mobileX.size(); i++){
            x = Float.parseFloat((String)mobileX.get(i));
            y = Float.parseFloat((String)mobileY.get(i));
            z = Float.parseFloat((String)mobileZ.get(i));
            if(max < x) max = x;
            if(max < y) max = y;
            if(max < z) max = z;
        }
        return max;
    }

    public float minValue(){
        float min = Float.parseFloat((String)mobileX.get(0));
        float x = 0;
        float y = 0;
        float z = 0;
        for(int i = 0; i < mobileX.size(); i++){
            x = Float.parseFloat((String)mobileX.get(i));
            y = Float.parseFloat((String)mobileY.get(i));
            z = Float.parseFloat((String)mobileZ.get(i));
            if(min > x) min = x;
            if(min > y) min = y;
            if(min > z) min = z;
        }
        return min;
    }


    public float highPassFilter(float value1, float value2){
        value1 = value2 - lowpassfilter(value1,value2);
        return value1;
    }

    public float lowpassfilter(float value1, float value2){
        value1 = (float)(value1 * 0.9 + value2 * 0.1);
        return value1;
    }

    //音楽再生用の関数
    public void SetBgm(){
        String filePath = "beat8.wav";
        // assetsのファイルをオープン
        // MediaPlayer のインスタンス生成
        mediaPlayer = new MediaPlayer();

        try (AssetFileDescriptor afdescripter = getAssets().openFd(filePath);){
        // MediaPlayerに読み込んだ音楽ファイルを指定
        mediaPlayer.setDataSource(afdescripter.getFileDescriptor(),
                afdescripter.getStartOffset(),
                afdescripter.getLength());
        mediaPlayer.prepare();
    } catch (IOException e1) {
        e1.printStackTrace();
    }
    }

    //カウントダウン用の効果音セット
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
