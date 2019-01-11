package com.example.dosshi.isolationpracticeapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MeasureActivity extends WearableActivity  implements SensorEventListener {
    private static final String TAG = MeasureActivity.class.getName();
    private GoogleApiClient mGoogleApiClient;

    private TextView accelText;
    private TextView gyroText;
    private TextView msgText;
    private SensorManager sensorManager;
    private String s;
    private String SEND_DATA;
    private String mNode;
    private String realdata;
    private ArrayList <String> WatchDataSet = new ArrayList<>();
    private ArrayList <String> WatchDataSet2 = new ArrayList<>();
    private Timer time = new Timer(false);
    private long timestamp;
    private  int flag = 0;
    private int count = 0;
    private Sensor accel;
    private Sensor gyro;
    private float gravityX = 0;
    private float gravityY = 0;
    private float gravityZ = 0;
    private int count2 = 0;
    private float gyroX = 0;
    private float gyroY = 0;
    private float gyroZ = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        //センサーマネージャーを取得
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //センサマネージャに TYPE_ACCELEROMETER(加速度センサ) を指定します。
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        accelText = (TextView) findViewById(R.id.accel);
        gyroText = (TextView) findViewById(R.id.gyro);
        msgText = (TextView) findViewById(R.id.textView4);

        accelText.setText("用意");


        // Enables Always-on
        setAmbientEnabled();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d(TAG, "onConnected");
                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                            @Override
                            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                                if (nodes.getNodes().size() > 0) {
                                    mNode = nodes.getNodes().get(0).getId();
                                }
                            }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d(TAG, "onConnectionSuspended");

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d(TAG, "onConnectionFailed : " + connectionResult.toString());
                    }
                })
                .build();

        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                flag = 1;
                time.cancel();
            }
        };
        time.schedule(task, 3000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Listenerの登録
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
        // Listenerを解除
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (flag == 1) {
                accelText.setText("計測");
                float sensorX, sensorY, sensorZ;
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        if (count <= 1000) {
                            gravityX = lowpassfilter(gravityX, event.values[0]);
                            gravityY = lowpassfilter(gravityY, event.values[1]);
                            gravityZ = lowpassfilter(gravityZ, event.values[2]);

                            sensorX = highPassFilter(gravityX, event.values[0]);
                            sensorY = highPassFilter(gravityY, event.values[1]);
                            sensorZ = highPassFilter(gravityZ, event.values[2]);

                            SEND_DATA = event.timestamp + "," + sensorX + "," + sensorY + "," + sensorZ;
                            WatchDataSet.add(SEND_DATA);
                            count++;
                        }
                        break;

                    case Sensor.TYPE_GYROSCOPE:
                        if (count2 <= 1000) {
                            gyroX = event.values[0];
                            gyroY = event.values[1];
                            gyroZ = event.values[2];
                            s = gyroX + "," + gyroY + "," + gyroZ;
                            WatchDataSet2.add(s);
                            count2++;
                        }
                        break;
                }
                    if (mNode != null && count == 1000 && count2 == 1000) {
                        msgText.setText("計測終了！");
                        sensorManager.unregisterListener(this);
                        for (int i = 0; i < WatchDataSet.size(); i++) {
                            SEND_DATA = WatchDataSet.get(i)+ "," + WatchDataSet2.get(i);
                            Wearable.MessageApi.sendMessage(mGoogleApiClient, mNode, SEND_DATA, null).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                                @Override
                                public void onResult(MessageApi.SendMessageResult result) {
                                    if (!result.getStatus().isSuccess()) {
                                        Log.d(TAG, "ERROR : failed to send Message" + result.getStatus());
                                    }
                                }
                            });
                        }
                    }
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public float highPassFilter(float value1, float value2){
        value2 = value2 - value1;
        return value2;
    }

    public float lowpassfilter(float value1, float value2){
        value1 = value2 * 0.1f + value1 * (1.0f - 0.1f);
        return value1;
    }

}
