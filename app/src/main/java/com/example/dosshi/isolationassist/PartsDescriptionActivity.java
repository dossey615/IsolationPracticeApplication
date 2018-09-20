package com.example.dosshi.isolationassist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;

public class PartsDescriptionActivity extends AppCompatActivity {

    public static String PRACTICE_TIME;
    private String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parts_description);
        Intent intent = getIntent();
        String data = intent.getStringExtra(SelectPartsActivity.PARTS_DATA);
        TextView textView = findViewById(R.id.SelectedParts);
        textView.setText(data);
        Button stbutton = findViewById(R.id.start_assist);
        initSpinners();
        startButton(stbutton);
    }



    private void startButton(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), PracticeActivity.class);
                intent.putExtra(PRACTICE_TIME,"a");
                startActivity(intent);
            }
        });
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
