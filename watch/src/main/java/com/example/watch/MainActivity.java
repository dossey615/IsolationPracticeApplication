package com.example.watch;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private TextView mTextView;
    private SensorManager sensorManager;
    private Sensor accel;
    private Sensor gyro;
    private String strTmp = "no";
    private String gyroTmp = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //センサーマネージャーを取得
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        //センサマネージャに TYPE_ACCELEROMETER(加速度センサ) を指定します。
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Listenerの登録
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float sensorX, sensorY, sensorZ;
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            sensorX = event.values[0];
            sensorY = event.values[1];
            sensorZ = event.values[2];
            String strTmp = "加速度センサー\n"
                    + " X: " + sensorX + "\n"
                    + " Y: " + sensorY + "\n"
                    + " Z: " + sensorZ;
            mTextView.setText(strTmp);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
