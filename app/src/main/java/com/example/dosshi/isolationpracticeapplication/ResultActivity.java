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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;


public class ResultActivity extends AppCompatActivity {

    private Globals globals;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //UIのデータを取得
        setContentView(R.layout.activity_result);
        Button title = findViewById(R.id.titleback);
        Button chart = findViewById(R.id.practiceAdvice);

        globals = (Globals)this.getApplication();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        //fileUpload();

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

    private void sampleFileOutput() {
        Date date = new Date();
        String fileName =  globals.slctParts + "_"+ date + "_.csv";
        ArrayList mobAccelX = globals.mobileX;
        ArrayList mobAccelY = globals.mobileY;
        ArrayList mobAccelZ = globals.mobileZ;
        try {
            //出力先を作成する

            FileWriter fw = new FileWriter(fileName, true);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

            //書き込み
            pw.print("time, moAccel_X, moAccel_Y, moAccel_Z, watchAccel_X, watchAccel_Y, watchAccel_Z\n");
            for(int i = 0; i < globals.mobileX.size(); i++) {
                pw.print(i + ","
                        + mobAccelX.get(i) + ","
                        + mobAccelY.get(i) + ","
                        + mobAccelZ.get(i) + ","
                        + globals.watchData.get(i)
                );
                pw.println();
            }
            pw.close();

        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public void fileUpload() {


        Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

    }

}
