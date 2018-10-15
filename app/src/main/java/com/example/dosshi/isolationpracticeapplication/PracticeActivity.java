package com.example.dosshi.isolationpracticeapplication;

import java.util.*;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.*;
import android.os.Bundle;
import android.os.Vibrator;

public class PracticeActivity extends AppCompatActivity implements SensorEventListener{
    private TextView timerText;
    private TextView prTimeText;
    private TextView accelText;
    private TextView gyroText;
    private int flag = 0;

    private Vibrator vibrator;
    private CountDown countDown;
    private SensorManager sensorManager;
    private Sensor accel;
    private Sensor gyro;


    private SimpleDateFormat timeFormat =
            new SimpleDateFormat("mm:ss.SSS", Locale.US);
    private SimpleDateFormat dataFormat =
            new SimpleDateFormat("s", Locale.US);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        //テキストのidを取得し、連携
        timerText = findViewById(R.id.count);
        prTimeText = findViewById(R.id.timeView);
        accelText = findViewById(R.id.accel);
        gyroText = findViewById(R.id.gyroscope);

        //加速度用のプログラム実装
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //タイマーの初期設定
        long countNumber = 4000;
        long interval = 10;
        prTimeText.setVisibility(View.INVISIBLE);
        countDown = new CountDown(countNumber, interval);
        countDown.start();

    }
    /*--以下は加速度センサーの実装--*/

    @Override
    protected void onResume() {
            super.onResume();
            // Listenerの登録
            accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);

    }

    // 解除するコードも入れる!
    @Override
    protected void onPause() {
        super.onPause();
        // Listenerを解除
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        float sensorX, sensorY, sensorZ;
        float gyroX, gyroY, gyroZ;
        if(flag == 1){
            switch (event.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                sensorX = event.values[0];
                sensorY = event.values[1];
                sensorZ = event.values[2];
                    if(sensorX > 8 || sensorZ > 4 ) vibrator.vibrate(100);
                    else vibrator.cancel();
                    String strTmp = "加速度センサー\n"
                            + " X: " + sensorX + "\n"
                            + " Y: " + sensorY + "\n"
                            + " Z: " + sensorZ;
                    accelText.setText(strTmp);
                break;

                case Sensor.TYPE_GYROSCOPE:
                gyroX = event.values[0];
                gyroY = event.values[1];
                gyroZ = event.values[2];
                    String gyroTmp = "ジャイロセンサー\n"
                            + " X: " + gyroX + "\n"
                            + " Y: " + gyroY + "\n"
                            + " Z: " + gyroZ;
                    gyroText.setText(gyroTmp);
                break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /*--以下はタイマーメソッドの実装--*/

    class CountDown extends CountDownTimer {

        CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            // 完了
            if (flag == 0){
                prTimeText.setVisibility(View.VISIBLE);
                timerText.setTextSize(60);
                timerText.setText("計測中・・・");
                countDown = new CountDown(5000, 10);
                countDown.start();
                flag = 1;
            } else {
                timerText.setText("finish");
                timerText.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(getApplication(), ResultActivity.class);
                startActivity(intent);
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // 残り時間を分、秒、ミリ秒に分割
            String cou = dataFormat.format(millisUntilFinished);
            if(Integer.parseInt(dataFormat.format(millisUntilFinished)) > 0 && flag == 0) {
                timerText.setText(dataFormat.format(millisUntilFinished));
            }else if(Integer.parseInt(dataFormat.format(millisUntilFinished)) > 0){
                prTimeText.setText(timeFormat.format(millisUntilFinished));
            }else if(Integer.parseInt(dataFormat.format(millisUntilFinished)) == 0 && flag == 0) {
                timerText.setText("start");
            }else{
                prTimeText.setText(timeFormat.format(millisUntilFinished));
            }
        }
    }
}
