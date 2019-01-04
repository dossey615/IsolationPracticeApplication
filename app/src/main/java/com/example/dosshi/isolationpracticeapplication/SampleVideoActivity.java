package com.example.dosshi.isolationpracticeapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;


public class SampleVideoActivity extends AppCompatActivity {

    private VideoView videoView;
    private int position = 0;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_video);

        videoView = (VideoView) findViewById(R.id.videoView);

        videoView.setVideoPath("android.resource://" + this.getPackageName() + "/" + R.raw.temple);
        videoView.start();

        // Set the media controller buttons
        if (mediaController == null) {
            mediaController = new MediaController(this);

            // Set the videoView that acts as the anchor for the MediaController.
            mediaController.setAnchorView(videoView);

            // Set MediaController for VideoView
            videoView.setMediaController(mediaController);
        }

        
    }
}
