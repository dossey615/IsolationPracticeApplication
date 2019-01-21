package com.example.dosshi.isolationpracticeapplication;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.wear.widget.BoxInsetLayout;
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

    private ColorDrawable colorDrawable;
    private BoxInsetLayout con;

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
    private int temporaryColorInt;
    private  Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();

        //センサーマネージャーを取得
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //センサマネージャに TYPE_ACCELEROMETER(加速度センサ) を指定します。
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        vibrator.vibrate(100);

        accelText = (TextView) findViewById(R.id.accel);
        gyroText = (TextView) findViewById(R.id.gyro);

//        con = (BoxInsetLayout)findViewById(R.id.BoxInsetLayou);
//        colorDrawable = (ColorDrawable)con.getBackground();
//        temporaryColorInt = colorDrawable.getColor();


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
                flag = 3;
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
        if (flag == 3){
            accelText.setText("計測");
            accelText.setTextSize(30);
            accelText.setTextColor(Color.RED);
//            con.setBackgroundColor(Color.GREEN);
            flag = 1;
        }
        if (flag == 1) {
                float sensorX, sensorY, sensorZ;
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        if (count <= 1000) {

                            SEND_DATA = event.timestamp + "," + event.values[0] + "," + event.values[1] + "," + event.values[2];
                            WatchDataSet.add(SEND_DATA);
                            count++;
                        }
                        break;

                    case Sensor.TYPE_GYROSCOPE:
                        if (count2 <= 1000) {
                            WatchDataSet2.add(event.values[0]+ "," + event.values[1] + "," + event.values[2]);

                            count2++;
                        }
                        break;
                }
                    if (mNode != null && count == 1000 && count2 == 1000) {

                        accelText.setText("計測終了！");
                        //con.setBackgroundColor(Color.RED);
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


}
