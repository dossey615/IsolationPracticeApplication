package com.example.dosshi.isolationpracticeapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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

    private String date;

    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //現在日時を取得する
        Calendar cal = Calendar.getInstance();
        //フォーマットパターンを指定して表示する
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_E_HH:mm:ss");
        date = sdf.format(cal.getTime());

        //UIのデータを取得
        setContentView(R.layout.activity_result);
        Button title = findViewById(R.id.titleback);
        Button chart = findViewById(R.id.practiceAdvice);

        globals = (Globals)this.getApplication();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

//        SaveToSDcard();
        FileOutput();

        //それぞれのボタンを押した時の設定
        title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), MainResearchActivity.class);
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

        fileName = globals.slctParts + ".csv";
        //出力先を作成する
        File file = new File(getFilesDir(), fileName);
        FileWriter fileWriter = null;

        ArrayList mobAccelX = globals.mobileX;
        ArrayList mobAccelY = globals.mobileY;
        ArrayList mobAccelZ = globals.mobileZ;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(
                    "time,AxisAccl_X,AxisAccl_Y,AxisAccl_Z,"
                            + "MvAccl_X,MvAccl_Y,MvAccl_Z, "
                            + "AxisGyr_X,AxisGyr_Y,AxisGyr_Z,"
                            + "MvGyr_X,MvGyr_Y,MvGyr_Z"
                            + "\n"
            );
            for (int i = 0; i < globals.mobileX.size(); i++) {
                fileWriter.write(i + ","
                                + mobAccelX.get(i) + ","
                                + mobAccelY.get(i) + ","
                                + mobAccelZ.get(i) + ","
//                                + globals.watchData.get(i)
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
        final StorageReference riversRef = storageRef.child(date + "/" +file.getLastPathSegment());

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

    public void SaveToSDcard(){

    }

}
