package com.example.dosshi.isolationpracticeapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

public class DescriptionActivity extends WearableActivity implements GoogleApiClient.ConnectionCallbacks, MessageApi.MessageListener {

    private int flag = 1  ;
    private static final String TAG = DescriptionActivity.class.getName();
    private GoogleApiClient mGoogleApiClient;
    private String activChangeFlag = "off";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

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

        // idがswitchButtonのSwitchを取得
        Switch switchButton = (Switch) findViewById(R.id.okswitch);
        // switchButtonのオンオフが切り替わった時の処理を設定
        switchButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton comButton, boolean isChecked) {
                        // 表示する文字列をスイッチのオンオフで変える
                        String displayChar = "";
                        // オンなら
                        if (isChecked) {
                            displayChar = "ウォッチ準備OK";
                        }
                        // オフなら
                        else {
                            displayChar = "取り消し";
                        }
                        Toast toast = Toast.makeText(DescriptionActivity.this, displayChar, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }

        );
        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //GoogleApiClient接続
        mGoogleApiClient.connect();
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
        Intent intent = new Intent(getApplication(), MeasureActivity.class);
        // ここで1秒間スリープし、スプラッシュを表示させたままにする。
        startActivity(intent);
    }
}
