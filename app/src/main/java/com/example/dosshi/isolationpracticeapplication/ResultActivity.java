package com.example.dosshi.isolationpracticeapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ResultActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //UIのデータを取得
        setContentView(R.layout.activity_result);
        Button title = findViewById(R.id.titleback);


//        String filename =  data + "_test_1.csv";
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
}
