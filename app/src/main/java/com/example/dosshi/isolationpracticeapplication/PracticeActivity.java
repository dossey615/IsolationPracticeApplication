package com.example.dosshi.isolationpracticeapplication;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PracticeActivity extends AppCompatActivity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, MessageApi.MessageListener {

    private static final String TAG = PracticeActivity.class.getName();
    private GoogleApiClient mGoogleApiClient;
    public static String PARTS_NAME;
    private int flag = 0;
    private Globals globals;

    private TextView timerText;
    private TextView prTimeText;
    private TextView accelText;
    private TextView gyroText;

    private ArrayList watchResult = new ArrayList();
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
        //グローバル変数を取得
        globals = (Globals) this.getApplication();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //テキストのidを取得し、連携
        timerText = findViewById(R.id.count);
        prTimeText = findViewById(R.id.timeView);
        accelText = findViewById(R.id.accel);
        gyroText = findViewById(R.id.gyroscope);

        //加速度用のプログラム実装
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

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

        //タイマーの初期設定
        long countNumber = 4000;
        long interval = 1000;
        prTimeText.setVisibility(View.INVISIBLE);
        countDown = new CountDown(countNumber, interval);
    }

    /*--アクティビティが表示された時--*/
    @Override
    protected void onStart() {
        super.onStart();
        //GoogleApiClient接続
        mGoogleApiClient.connect();
        countDown.start();
        globals.soundPool.play(globals.countBgm, 1.0f, 1.0f, 0, 0, 1.0f);

    }

    /*--以下は加速度センサーの実装--*/

    @Override
    protected void onResume() {
            super.onResume();
            // Listenerの登録
            accel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);

    }

    // 解除するコードも入れる!
    @Override
    protected void onPause() {
        super.onPause();
        countDown.cancel();
        // Listenerを解除
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onConnected(Bundle bundle) {
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
        if(flag == 1) {
            watchResult.add(messageEvent.getPath());
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        float sensorX, sensorY, sensorZ;
        float gyroX, gyroY, gyroZ;
        if(flag == 1){
            switch (event.sensor.getType()){
                case Sensor.TYPE_LINEAR_ACCELERATION:
                sensorX = event.values[0];
                sensorY = event.values[1];
                sensorZ = event.values[2];
                globals.mobileAccelDataSet(sensorX, sensorY, sensorZ);
                    if(sensorX > 8 || sensorZ > 4 ) vibrator.vibrate(100);
                    else vibrator.cancel();
                    String strTmp = "加速度センサー\n"
                            + " X: " + sensorX + "\n"
                            + " Y: " + sensorY + "\n"
                            + " Z: " + sensorZ;
                   globals.size++;
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

    /*--以下はタイマーメソッドの実装--*/

    class CountDown extends CountDownTimer {
        CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            // 完了
            if (flag == 0) {
                globals.mediaPlayer.start();
                globals.soundPool.stop(globals.countBgm);
                prTimeText.setVisibility(View.VISIBLE);
                timerText.setTextSize(60);
                timerText.setText("計測中・・・");
                countDown = new CountDown(10000, 10);
                countDown.start();
                flag = 1;
            } else {
                globals.mediaPlayer.stop();
                timerText.setText("finish");
                timerText.setVisibility(View.INVISIBLE);
                globals.watchDataSet(watchResult);
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
