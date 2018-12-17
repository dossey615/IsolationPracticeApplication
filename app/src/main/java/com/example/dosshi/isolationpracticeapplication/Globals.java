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

    ArrayList watchX = new ArrayList();
    ArrayList watchY = new ArrayList();
    ArrayList watchZ = new ArrayList();
    ArrayList mobileTimestamp = new ArrayList();
    ArrayList<String> watchTimestamp = new ArrayList<>();
    ArrayList<String> watchrealData= new ArrayList<>();
    ArrayList<String> mobileRealdata = new ArrayList<>();



    float oldAccel_X = 0;
    float oldAccel_Y = 0;
    float oldAccel_Z = 0;
    float oldSpeed_X = 0;
    float oldSpeed_Y = 0;
    float oldSpeed_Z = 0;

    float oldAccel_WX = 0;
    float oldAccel_WY = 0;
    float oldAccel_WZ = 0;
    float oldSpeed_WX = 0;
    float oldSpeed_WY = 0;
    float oldSpeed_WZ = 0;

    float timeSpan = 0.1f;

    //加速度から算出した速度
    float speed = 0;
    float differenceX = 0;
    float differenceY = 0;
    float differenceZ = 0;
    float differenceWX = 0;
    float differenceWY = 0;
    float differenceWZ = 0;


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

        oldAccel_X = 0;
        oldAccel_Y = 0;
        oldAccel_Z = 0;
        oldSpeed_X = 0;
        oldSpeed_Y = 0;
        oldSpeed_Z = 0;
        oldAccel_WX = 0;
        oldAccel_WY = 0;
        oldAccel_WZ = 0;
        oldSpeed_WX = 0;
        oldSpeed_WY = 0;
        oldSpeed_WZ = 0;

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
        watchTimestamp.add(data[0]);
        watchX.add(x);
        watchY.add(y);
        watchZ.add(z);
        watchrealData.add(data[4]);

//        watchX.add(CalculationDisplacement(x,oldAccel_WX,timeSpan,oldSpeed_WX,differenceWX,3));
//        watchY.add(CalculationDisplacement(y,oldAccel_WY,timeSpan,oldSpeed_WY,differenceWY,4));
//        watchZ.add(CalculationDisplacement(z,oldAccel_WZ,timeSpan,oldSpeed_WZ,differenceWZ,5));
    }

    public void mobileAccelDataSet(float x, float y, float z,long stamp){
        oldAccel_X = lowpassfilter(oldAccel_X,x);
        oldAccel_Y = lowpassfilter(oldAccel_Y,y);
        oldAccel_Z = lowpassfilter(oldAccel_Z,z);



        mobileX.add(String.valueOf(highPassFilter(oldAccel_X,x)));
        mobileY.add(String.valueOf(highPassFilter(oldAccel_Y,y)));
        mobileZ.add(String.valueOf(highPassFilter(oldAccel_Z,z)));
        mobileTimestamp.add(String.valueOf(stamp));
        //スマホの値をそれぞれセット
//        mobileX.add(CalculationDisplacement(noiseClear(x),oldAccel_X,timeSpan,oldSpeed_X,differenceX,0));
//        mobileY.add(CalculationDisplacement(noiseClear(y),oldAccel_Y,timeSpan,oldSpeed_Y,differenceY,1));
//        mobileZ.add(CalculationDisplacement(noiseClear(z),oldAccel_Z,timeSpan,oldSpeed_Z,differenceZ,2));
    }

    public float noiseClear(float noise){
        //if(Math.abs(noise) < 0.024)noise = 0;
        return noise;
    }

    public String CalculationDisplacement(float highpassValue,float oldAccel,float timeSpan,float oldSpeed,float difference,int j) {
        // 速度計算(加速度を台形積分する)
        speed = ((highpassValue + oldAccel) * timeSpan) / 2f + oldSpeed;
        // 変位計算(速度を台形積分する)
        difference = ((speed + oldSpeed) * timeSpan) / 2f + difference;
        switch (j) {
            case 0:
                oldAccel_X = highpassValue;
                oldSpeed_X = speed;
                differenceX = difference;
                break;
            case 1:
                oldAccel_Y = highpassValue;
                oldSpeed_Y = speed;
                differenceY = difference;
                break;
            case 2:
                oldAccel_Z = highpassValue;
                oldSpeed_Z = speed;
                differenceZ = difference;
                break;
            case 3:
                oldAccel_WX = highpassValue;
                oldSpeed_WX = speed;
                differenceWX = difference;
                break;
            case 4:
                oldAccel_WY = highpassValue;
                oldSpeed_WY = speed;
                differenceWY = difference;
                break;
            default:
                oldAccel_WZ = highpassValue;
                oldSpeed_WZ = speed;
                differenceWZ = difference;
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
