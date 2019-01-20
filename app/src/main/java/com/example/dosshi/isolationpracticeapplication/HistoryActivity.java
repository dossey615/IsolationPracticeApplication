package com.example.dosshi.isolationpracticeapplication;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class HistoryActivity extends AppCompatActivity {

    private ArrayList<String> histry = new ArrayList<String>();
    private ArrayList<String> loadURL = new ArrayList<String>();
    private Globals globals;
    private HistoryData hisdata = new HistoryData();
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private Intent intent;
    public static String COMPARE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ListView list = findViewById(R.id.dynamic);
        storage = FirebaseStorage.getInstance();

        globals = (Globals)this.getApplication();
        // 画面遷移
        intent = new Intent(getApplication(), ChartsActivity.class);
        intent.putExtra(COMPARE, "start");


        //list表示する文字列を設定
        histry.addAll(globals.historyData.keySet());
        Collections.sort(histry);
        histry.add(0, "上級者");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, histry);
        list.setAdapter(arrayAdapter);


        // リスト項目がクリックされた時の処理
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String str = (String) listView.getItemAtPosition(position);
                if(str.equals("上級者")) exampleload();
                else loadFirebase(globals.historyData.get(str));
            }
        });
    }

    public void loadFirebase(String downloadURL){
            storageRef = storage.getReferenceFromUrl(downloadURL);
            final File localFile = new File(getFilesDir(), "compare.csv");
            storageRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d("SUCSSESS", "SUCSSESS : yattane " + taskSnapshot);
                            loadCSVforFire();
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("ERROR", "ERROR : failed to send Message " + exception);
                }
            });
        }

    public void loadCSVforFire(){
        try{
            globals.historyAccData.clear();
            FileInputStream in = openFileInput( "compare.csv" );
            BufferedReader reader = new BufferedReader( new InputStreamReader( in , "UTF-8") );
            String str = "";
            String tmp;
            int flag = 0;
            while( (tmp = reader.readLine()) != null ){
                if(flag != 1)flag = 1;
                else globals.historyAccData.setData(tmp.split(","));
            }
            reader.close();
        }catch( IOException e ){
            e.printStackTrace();
        }
    }

    public void exampleload(){
        try{
            globals.historyAccData.clear();
            Resources res = this.getResources();
            InputStream in2 = res.openRawResource(R.raw.master);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in2));
            String str = "";
            String tmp;
            int flag = 0;
            while( (tmp = reader.readLine()) != null ){
                if(flag != 1)flag = 1;
                else globals.historyAccData.setData(tmp.split(","));
            }
            reader.close();
            startActivity(intent);
        }catch( IOException e ){
            e.printStackTrace();
        }
    }

}
