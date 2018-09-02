package com.example.dosshi.isolationassist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
//import android.view.View;
//import android.widget.Button;

public class PartsDescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parts_description);
        Intent intent = getIntent();
        String data = intent.getStringExtra(SelectPartsActivity.PARTS_DATA);
        TextView textView = findViewById(R.id.SelectedParts);
        textView.setText(data);
    }
}
