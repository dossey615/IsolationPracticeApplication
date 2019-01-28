package com.example.dosshi.isolationpracticeapplication;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class SelectPartsActivity extends AppCompatActivity {
    public static String PARTS_DATA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_select_parts);

        Button neckButton = findViewById(R.id.parts1);
        Button breastButton = findViewById(R.id.parts2);
        Button hipButton = findViewById(R.id.parts3);

        //それぞれのボタンを押した時の設定
        neckButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), PartsDescriptionActivity.class);
                String message = "Neck";
                intent.putExtra(PARTS_DATA,message);
                startActivity(intent);
            }
        });

        breastButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), PartsDescriptionActivity.class);
                String message = "Chest";
                intent.putExtra(PARTS_DATA,message);
                startActivity(intent);
            }
        });

        hipButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), PartsDescriptionActivity.class);
                String message = "Waist";
                intent.putExtra(PARTS_DATA,message);
                startActivity(intent);
            }
        });

    }
}
