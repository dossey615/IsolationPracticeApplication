package com.example.dosshi.isolationassist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;

public class PartsDescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parts_description);
        Intent intent = getIntent();
        String data = intent.getStringExtra(SelectPartsActivity.PARTS_DATA);
        TextView textView = findViewById(R.id.SelectedParts);
        textView.setText(data);
        initSpinners();
    }

    private void initSpinners() {
        Spinner spinner1 = (Spinner)findViewById(R.id.Spinner01);
        String[] labels = getResources().getStringArray(R.array.time_array);
        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labels);
        spinner1.setAdapter(adapter);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public void onClick_Button1(View view){
        Spinner spinner1 = (Spinner)findViewById(R.id.Spinner01);
        String str = spinner1.getSelectedItem().toString();
    }

}
