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
    private int flag = 0;

    private Vibrator vibrator;
    private CountDown countDown;
    private SensorManager sensorManager;
    private Sensor accel;


    private SimpleDateFormat timeFormat =
            new SimpleDateFormat("mm:ss.SSS", Locale.US);
    private SimpleDateFormat dataFormat =
            new SimpleDateFormat("s", Locale.US);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        timerText = findViewById(R.id.count);
        prTimeText = findViewById(R.id.timeView);
        accelText = findViewById(R.id.textView3);
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
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

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && flag == 1) {
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
