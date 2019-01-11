package com.example.dosshi.isolationpracticeapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class PartsDescriptionActivity extends AppCompatActivity {

    private static final String TAG = PartsDescriptionActivity.class.getName();
    public static String PARTS_NAME;
    private String time;
    private String data;
    private String mNode;
    private Globals globals;
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parts_description);


        setGoogleAPI();

                    //別の画面からデータを受け取る
        Intent intent = getIntent();
       data = intent.getStringExtra(SelectPartsActivity.PARTS_DATA);
       globals = (Globals)this.getApplication();
       globals.slctParts = data;

        //選ばれた部分の表示
        TextView textView = findViewById(R.id.SelectedParts);
        if(data.equals("Chest"))data ="練習部位：胸";
        else if(data.equals("Neck"))data ="練習部位：首";
        else data ="練習部位：腰";
        textView.setText(data);

        //時間のスピナー表示
        Button stbutton = findViewById(R.id.start_assist);
        Button examplebutton = findViewById(R.id.example);

        startButton(stbutton);
        exampleButton(examplebutton);

    }

    private void exampleButton(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Intent intent = new Intent(getApplication(),SampleVideoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startButton(Button button) {
        final Intent intent = new Intent(getApplication(), PracticeActivity.class);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mNode != null) {
                    startActivity(intent);
                    Wearable.MessageApi.sendMessage(client, mNode, "OK", null).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult result) {
                            if (!result.getStatus().isSuccess()) {
                                Log.d(TAG, "ERROR : failed to send Message" + result.getStatus());
                            }
                        }
                    });
                }else{
                    builder.setTitle("ウォッチがセットされていません");
                    builder.setMessage("ウォッチアプリを起動し\n指定した箇所にセットしてください")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        client.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        client.disconnect();
    }

    public void setGoogleAPI(){
        //ウォッチ側のアプリを起動
        client = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d(TAG, "onConnected");
                        Wearable.NodeApi.getConnectedNodes(client).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
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

}
