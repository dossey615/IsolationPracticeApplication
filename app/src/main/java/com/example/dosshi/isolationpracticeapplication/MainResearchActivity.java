package com.example.dosshi.isolationpracticeapplication;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainResearchActivity extends AppCompatActivity {

    private TextView text;
    private Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        // ここで1秒間スリープし、スプラッシュを表示させたままにする。
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        // スプラッシュthemeを通常themeに変更する
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main_reaearch);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        Button sendButton = findViewById(R.id.start);
        Button test = findViewById(R.id.help);
        globals = (Globals) this.getApplication();

        globals.valueInit();
        globals.soundInit();
        globals.SetBgm();

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), SelectPartsActivity.class);
                startActivity(intent);
            }
        });

        test.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), PDFActivity.class);
                startActivity(intent);
            }
        });
    }
}

