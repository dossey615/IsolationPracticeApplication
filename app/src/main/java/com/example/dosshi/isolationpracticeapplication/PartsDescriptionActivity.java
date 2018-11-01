package com.example.dosshi.isolationpracticeapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class PartsDescriptionActivity extends AppCompatActivity {

    public static String PARTS_NAME;
    private String time;
    private String data;
    private String name = "no";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parts_description);

        //別の画面からデータを受け取る
        Intent intent = getIntent();
       data = intent.getStringExtra(SelectPartsActivity.PARTS_DATA);

        //選ばれた部分の表示
        TextView textView = findViewById(R.id.SelectedParts);
        if(data.equals("Chest"))name ="練習部位：胸";
        if(data.equals("Neck"))name ="練習部位：首";
        else name ="練習部位：腰";
        textView.setText(name);

        //時間のスピナー表示
        Button stbutton = findViewById(R.id.start_assist);
        initSpinners();
        startButton(stbutton);
    }

    private void startButton(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), PracticeActivity.class);
                intent.putExtra(PARTS_NAME,data);
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
