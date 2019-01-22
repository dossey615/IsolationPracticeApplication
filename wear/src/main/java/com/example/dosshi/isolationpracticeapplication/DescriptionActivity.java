package com.example.dosshi.isolationpracticeapplication;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.wear.widget.BoxInsetLayout;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DescriptionActivity extends WearableActivity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, MessageApi.MessageListener {

    private int flag = 0;
    private static final String TAG = DescriptionActivity.class.getName();
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiClient mGoogleApiClient2;
    private ColorDrawable colorDrawable;
    private BoxInsetLayout con;
    private int temporaryColorInt;
    private String activChangeFlag = "off";
    private  Vibrator vibrator;

    private TextView message;
    private TextView acceltext;

    private SensorManager sensorManager;
    private String s;
    private String SEND_DATA;
    private String mNode;
    private String realdata;

    private ArrayList<String> WatchDataSet = new ArrayList<>();
    private ArrayList <String> WatchDataSet2 = new ArrayList<>();
    private Timer time = new Timer(false);
    private int count = 0;
    private Sensor accel;
    private Sensor gyro;
    private int count2 = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        message = (TextView)findViewById(R.id.msg);
        acceltext = (TextView) findViewById(R.id.accelTextView);
        con = (BoxInsetLayout)findViewById(R.id.background);
        colorDrawable = (ColorDrawable)con.getBackground();
        temporaryColorInt = colorDrawable.getColor();

        //画面を常時インタラクティブモードにする
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //センサーマネージャーを取得
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //センサマネージャに TYPE_ACCELEROMETER(加速度センサ) を指定します。
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        //GoogleApiClientインスタンス生成
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d(TAG, "onConnectionFailed:" + connectionResult.toString());
                    }
                })
                .addApi(Wearable.API)
                .build();

        // Enables Always-on
        setAmbientEnabled();
        mGoogleApiClient2 = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d(TAG, "onConnected");
                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient2).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        //GoogleApiClient接続
        mGoogleApiClient.connect();
        mGoogleApiClient2.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
        mGoogleApiClient2.disconnect();
        // Listenerを解除
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //GoogleApiClient 接続成功時に呼ばれます。
        Log.d(TAG, "onConnected");
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        //接続中断時に呼ばれます。
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        activChangeFlag = messageEvent.getPath();
        StartActive();
    }

    void StartActive() {

        message.setText("用意");

        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                flag = 3;
                time.cancel();
            }
        };
        time.schedule(task, 4000);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (flag == 3){
            message.setText("計測中");
            message.setTextSize(30);
            message.setTextColor(Color.WHITE);
            con.setBackgroundColor(Color.GREEN);
            flag = 1;
        }
        if (flag == 1) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    if (count <= 1001) {

                        SEND_DATA = event.timestamp + "," + event.values[0] + "," + event.values[1] + "," + event.values[2];
                        WatchDataSet.add(SEND_DATA);
                        count++;
                    }
                    break;

                case Sensor.TYPE_GYROSCOPE:
                    if (count2 <= 1001) {
                        acceltext.setText(event.values[0]+"\n"+ event.values[1]+"\n"+event.values[2]);
                        WatchDataSet2.add(event.values[0]+ "," + event.values[1] + "," + event.values[2]);
                        if (event.values[0] >= 1 || event.values[2] >= 0.5|| event.values[0] <= -1.0 || event.values[2] <= -0.7) vibrator.vibrate(100);
                        count2++;
                    }
                    break;
            }
            if (mNode != null && count == 1001 && count2 == 1001) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                con.setBackgroundColor(Color.BLUE);
                message.setText("計測終了！");
                sensorManager.unregisterListener(this);
                for (int i = 0; i < WatchDataSet.size(); i++) {
                    SEND_DATA = WatchDataSet.get(i)+ "," + WatchDataSet2.get(i);
                    Wearable.MessageApi.sendMessage(mGoogleApiClient2, mNode, SEND_DATA, null).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
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
