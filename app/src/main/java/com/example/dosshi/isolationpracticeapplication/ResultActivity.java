package com.example.dosshi.isolationpracticeapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class ResultActivity extends AppCompatActivity {

    private Globals globals;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private String fileName;
    private GoogleApiClient mGoogleApiClient;
    private int countmob = 0;
    private int countwat = 0;
    private int skip = 0;
    private int skip2 = 0;
    private float mobtime = 0f;
    private float wattime = 0f;

    private String date;

    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //現在日時を取得する
        Calendar cal = Calendar.getInstance();
        //フォーマットパターンを指定して表示する
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
        date = sdf.format(cal.getTime());

        //UIのデータを取得
        setContentView(R.layout.activity_result);
        Button title = findViewById(R.id.titleback);
        Button chart = findViewById(R.id.practiceAdvice);

        globals = (Globals)this.getApplication();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        FileOutput();
        missMoveSet();

        //それぞれのボタンを押した時の設定
        title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), MainResearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        chart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), ChartsActivity.class);
                startActivity(intent);
            }
        });
    }

//csvファイルの作成
    private void FileOutput() {

        fileName = date + ".csv";
        //出力先を作成する
        File file = new File(getFilesDir(), fileName);
        FileWriter fileWriter = null;

        ArrayList mobAccelX = globals.mobileX;
        ArrayList mobAccelY = globals.mobileY;
        ArrayList mobAccelZ = globals.mobileZ;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(
                    "Mobiletimestamp,MobileAccl_X,MobileAccl_Y,MobileAccl_Z,"
                            + "MobileGyro_X,MobileGyro_Y,MobileGyro_Z, "
                            + "Watchtimestamp,Watch_X,Watch_Y,Watch_Z,"
                            + "WatchGyro_X,WatchGyro_Y,WatchGyro_Z"
                            + "\n"
            );
            int size = globals.compasionSize();
            for (int i = 0; i < size - 1 ; i++) {
                fileWriter.write(globals.mobileTimestamp.get(i) + ","
                                + mobAccelX.get(i) + ","
                                + mobAccelY.get(i) + ","
                                + mobAccelZ.get(i) + ","
                                + globals.mobileRealdata.get(i) + ","
                                + globals.watchTimestamp.get(i) + ","
                                + globals.watchX.get(i) + ","
                                + globals.watchY.get(i) + ","
                                + globals.watchZ.get(i) + ","
                                + globals.watchrealData.get(i) + ","
                                + "\n"
                );
            }
            fileWriter.close();
            fileUpload(file);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //csvファイルをcloud storageへ保存
    public void fileUpload(File dataFile){
        Uri file = Uri.fromFile(dataFile);
        //csvファイルを入れるpathの作成
        final StorageReference riversRef = storageRef.child(globals.slctParts + "/" +file.getLastPathSegment());

        //csvファイルをcloud storageに
        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                setDatabase(uri.toString());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    public void setDatabase(String path){
        database = FirebaseDatabase.getInstance();
        DatabaseReference RefCSV = database.getReference("User1/"+globals.slctParts+"/");
        RefCSV = RefCSV.push();
        RefCSV.child("URL").setValue(path);
        RefCSV.child("Date").setValue(date);

    }
    //間違えた時間，回数のカウント
    public void missMoveSet(){
        TextView moveTime = findViewById(R.id.movetime);
        TextView moveCount = findViewById(R.id.movecount);
        TextView stopTime = findViewById(R.id.stoptime);
        TextView stopCount = findViewById(R.id.stopcount);
        for(int i = 1; i < 1000; i++ ){
            float watGyroX = globals.watchgyroX.get(i);
            float watGyroZ = globals.watchgyroZ.get(i);
            float mobGyroX = globals.mobilegyroX.get(i);
            float mobGyroZ = globals.mobilegyroZ.get(i);
            float oldwatGyroX = globals.watchgyroX.get(i-1);
            float oldwatGyroZ = globals.watchgyroZ.get(i-1);
            float oldmobGyroX = globals.mobilegyroX.get(i-1);
            float oldmobGyroZ = globals.mobilegyroZ.get(i-1);
            check(watGyroZ,oldwatGyroZ,1, i);
            check(mobGyroZ,oldmobGyroZ,0, i);
            moveCount.setText("可動部："+ countwat + "回");
            stopCount.setText("軸部　："+ countmob + "回");
            moveTime.setText("可動部："+ wattime * 0.01 + "秒");
            stopTime.setText("軸部　："+ mobtime * 0.01 + "秒");

//            if (event.values[0] >= 1 || event.values[2] >= 0.5|| event.values[0] <= -1.0 || event.values[2] <= -0.7)
//                if ( >= 0.2 || gyroZ >= 0.23|| gyroX <= -0.2 || gyroZ <= -0.2)
        }

    }
    public  void check(float a, float b, int c,int i){
        switch(c) {
            case 0:
                if (a >= 0.23 || a <= -0.2) {
                    mobtime++;
                    if (b < 0.23 && b > -0.2 && (i - skip >= 50 || skip == 0)) {
                        countmob++;
                        skip = i;
                    }
                }
                break;
            case 1:
                if (a >= 0.5 || a <= -0.7) {
                    wattime++;
                    if (b < 0.5 && b > -0.7&&(i - skip2 >= 50 || skip2 == 0)) {
                        countwat++;
                        skip2 = i;
                    }
                }
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction()==KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    Toast toast = Toast.makeText(ResultActivity.this, "戻るボタンは利用できないよ", Toast.LENGTH_SHORT);
                    toast.show();
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

}
