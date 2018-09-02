package com.example.dosshi.isolationassist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectPartsActivity extends AppCompatActivity {
    public static String PARTS_DATA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_parts);

        Button neckButton = findViewById(R.id.parts1);
        Button breastButton = findViewById(R.id.parts2);
        Button hipButton = findViewById(R.id.parts3);


        //それぞれのボタンを押した時の設定
        neckButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), PartsDescriptionActivity.class);
                String message = "練習部位：首";
                intent.putExtra(PARTS_DATA,message);
                startActivity(intent);
            }
        });

        breastButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), PartsDescriptionActivity.class);
                String message = "練習部位：胸";
                intent.putExtra(PARTS_DATA,message);
                startActivity(intent);
            }
        });

        hipButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), PartsDescriptionActivity.class);
                String message = "練習部位：腰";
                intent.putExtra(PARTS_DATA,message);
                startActivity(intent);
            }
        });

    }
}
