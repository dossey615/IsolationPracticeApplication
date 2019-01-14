package com.example.dosshi.isolationpracticeapplication;

import android.app.Application;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Globals extends Application {
    //グローバル変数
    String slctParts = "";
    SoundPool soundPool;
    MediaPlayer mediaPlayer;

    HashMap<String,String> historyData = new HashMap<String,String>();
    ArrayList watchData = new ArrayList();
    ArrayList mobileX = new ArrayList();
    ArrayList mobileY = new ArrayList();
    ArrayList mobileZ = new ArrayList();

    ArrayList<Float> watchX = new ArrayList<>();
    ArrayList<Float> watchY = new ArrayList<>();
    ArrayList<Float> watchZ = new ArrayList<>();
    ArrayList mobileTimestamp = new ArrayList();
    ArrayList<String> watchTimestamp = new ArrayList<>();
    ArrayList<String> watchrealData= new ArrayList<>();
    ArrayList<String> mobileRealdata = new ArrayList<>();



    float oldAccel_X = 0;
    float oldAccel_Y = 0;
    float oldAccel_Z = 0;
    float watcholdAccel_X = 0;
    float watcholdAccel_Y = 0;
    float watcholdAccel_Z = 0;


    //加速度から算出した速度
    float speed = 0;
    float differenceX = 0;
    float differenceY = 0;
    float differenceZ = 0;



    int size = 0;
    int countBgm;


    public void valueInit() {
        String slctParts = "";

        historyData.clear();
        watchData.clear();
        mobileX.clear();
        mobileY.clear();
        mobileZ.clear();
        mobileRealdata.clear();
        watchrealData.clear();
        watchTimestamp.clear();
        mobileTimestamp.clear();


        mobileTimestamp.add("0");
        watchTimestamp.add("0");
        mobileX.add("0");
        mobileY.add("0");
        mobileZ.add("0");
        watchX.add((float)0);
        watchY.add((float)0);
        watchZ.add((float)0);
        watchrealData.add("0,0,0");
        mobileRealdata.add("0,0,0");

        //加速度から算出した速度
        speed = 0;
        differenceX = 0;
        differenceY = 0;
        differenceZ = 0;
    }
    public void setHistoryData(String date, String url){
        historyData.put(date, url);
    }

    public void watchDataSet(ArrayList array){
        watchData = array;
    }

    public void getWatchData(String save){
        String[] data = save.split(",", 5);
        float x = Float.parseFloat(data[1]);
        float y = Float.parseFloat(data[2]);
        float z = Float.parseFloat(data[3]);
        watcholdAccel_X = lowpassfilter(watcholdAccel_X,x);
        watcholdAccel_Y = lowpassfilter(watcholdAccel_Y,y);
        watcholdAccel_Z = lowpassfilter(watcholdAccel_Z,z);

        watchTimestamp.add(data[0]);
        watchX.add(highPassFilter(watcholdAccel_X,x));
        watchY.add(highPassFilter(watcholdAccel_Y,y));
        watchZ.add(highPassFilter(watcholdAccel_Z,z));
        watchrealData.add(data[4]);
    }

    public void mobileAccelDataSet(float x, float y, float z,long stamp){
        oldAccel_X = lowpassfilter(oldAccel_X,x);
        oldAccel_Y = lowpassfilter(oldAccel_Y,y);
        oldAccel_Z = lowpassfilter(oldAccel_Z,z);

        mobileX.add(String.valueOf(highPassFilter(oldAccel_X,x)));
        mobileY.add(String.valueOf(highPassFilter(oldAccel_Y,y)));
        mobileZ.add(String.valueOf(highPassFilter(oldAccel_Z,z)));
        mobileTimestamp.add(String.valueOf(stamp));
    }

    public float noiseClear(float noise){
        //if(Math.abs(noise) < 0.024)noise = 0;
        return noise;
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
        value2 = value2 - value1;
        return value2;
    }

    public float lowpassfilter(float value1, float value2){
        value1 = value2 * 0.1f + value1 * (1.0f - 0.1f);
        return value1;
    }

    public int compasionSize(){
        int minSize = mobileX.size();
        if(minSize > watchX.size())minSize = watchX.size();
        return minSize;
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
