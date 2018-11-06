package com.example.dosshi.isolationpracticeapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ResultActivity extends AppCompatActivity {

    private Globals globals;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //UIのデータを取得
        setContentView(R.layout.activity_result);
        Button title = findViewById(R.id.titleback);
        globals = (Globals)this.getApplication();

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("getReference");
        myRef.setValue("Hello, World!");

        fileUpload();

//        String filename =  globals.slctParts + "_test.csv";
//        String output = "サンプル";
//        FileOutputStream outputStream;
//        try {
//            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
//            outputStream.write(output.getBytes());
//            outputStream.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //それぞれのボタンを押した時の設定
        title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), MainResearchActivity.class);
                startActivity(intent);
            }
        });
    }

    public void fileUpload() {
// Write a message to the database

    }

}
