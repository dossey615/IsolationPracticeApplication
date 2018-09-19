package com.example.dosshi.isolationassist;

import java.util.*;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import java.text.SimpleDateFormat;

import android.view.View;
import android.widget.*;
import android.os.Bundle;

public class PracticeActivity extends AppCompatActivity {
    private TextView timerText;
    private CountDown countDown;

    private SimpleDateFormat dataFormat =
            new SimpleDateFormat("s", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        timerText = findViewById(R.id.count);
        long countNumber = 4000;
        long interval = 10;
        countDown = new CountDown(countNumber, interval);
        countDown.start();

    }

    class CountDown extends CountDownTimer {

        CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            // 完了
            timerText.setTextSize(60);
            timerText.setText("計測中・・・");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // 残り時間を分、秒、ミリ秒に分割
            String cou = dataFormat.format(millisUntilFinished);
            if(Integer.parseInt(dataFormat.format(millisUntilFinished)) > 0) {
                timerText.setText(dataFormat.format(millisUntilFinished));
            }else if(Integer.parseInt(dataFormat.format(millisUntilFinished)) == 0) {
                timerText.setText("Start");
            }else{
                timerText.setVisibility(View.INVISIBLE);
                countDown.cancel();
            }
        }
    }
}
