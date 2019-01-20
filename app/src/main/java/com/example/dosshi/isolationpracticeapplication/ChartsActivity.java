package com.example.dosshi.isolationpracticeapplication;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

public class ChartsActivity extends AppCompatActivity {

    private LineChart mChart;
    private Globals globals;
    private StorageReference storageRef;
    private FirebaseStorage storage;

    private HistoryData hisdata;
    private int size = 0;
    private int hisflag = 0;
    private ArrayList<String> keys = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        storage = FirebaseStorage.getInstance();
        globals = (Globals)this.getApplication();

        Intent intent = getIntent();
        if(intent.getStringExtra(HistoryActivity.COMPARE) != null) {
            hisdata = globals.historyAccData;
        }else{
            hisdata = new HistoryData();
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
              switch (tab.getPosition()){
                  case 0:
                      initCharts(0);
                      break;
                  case 1:
                      initCharts(1);
                      break;
              }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        initCharts(0);
    }


    public void initCharts(int flag){
        mChart = findViewById(R.id.line_chart);

        // Grid背景色
        mChart.setDrawGridBackground(true);

        // no description text
        mChart.getDescription().setEnabled(true);

        // Grid縦軸を破線
        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(45);


        YAxis leftAxis = mChart.getAxisLeft();
        // Y軸最大最小設定
        leftAxis.getLabelPosition();

        leftAxis.setAxisMaximum(globals.maxValue() + 10f);
        leftAxis.setAxisMinimum(globals.minValue() - 10f);
        // Grid横軸を破線
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // 右側の目盛り
        mChart.getAxisRight().setEnabled(false);

        // add data
        setData(flag);

        mChart.animateX(2500);

    }

    //databaseから表を取得
    public void loadhistry(){
        //Get datasnapshot at your "user1" root node
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/User1/").child(globals.slctParts);
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        //Get map of users in datasnapshot
                        globals.historyData.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            String date = (String) dataSnapshot.child("Date").getValue();
                            String url = (String) dataSnapshot.child("URL").getValue();
                            globals.setHistoryData(date,url);
                            size++;
                            keys.add(date);
                            Log.d("Firebase", String.format("date:%s, url:%s", date, url));

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    public void setData(int flag){


        ArrayList<Entry> mobileAccelval_X = new ArrayList<>();
        ArrayList<Entry> mobileAccelval_Y = new ArrayList<>();
        ArrayList<Entry> mobileAccelval_Z = new ArrayList<>();
        ArrayList<Entry> watchAccelval_X = new ArrayList<>();
        ArrayList<Entry> watchAccelval_Y = new ArrayList<>();
        ArrayList<Entry> watchAccelval_Z = new ArrayList<>();
        ArrayList<String> timestamp = new ArrayList<>();

        if(flag == 0) {
            for (int i = 0; i < globals.mobileX.size(); i++) {
//                mobileAccelval_X.add(new Entry(i, Float.parseFloat((String) globals.mobileX.get(i))));
                mobileAccelval_Y.add(new Entry(i, Float.parseFloat((String) globals.mobileY.get(i))));
//                mobileAccelval_Z.add(new Entry(i, Float.parseFloat((String) globals.mobileZ.get(i))));
                timestamp.add((String)globals.mobileTimestamp.get(i));
            }
            for(int i = 0; i < hisdata.dataSize(); i++){
//                watchAccelval_X.add(new Entry(i, hisdata.mobX.get(i)));
                watchAccelval_Y.add(new Entry(i, hisdata.mobY.get(i)));
//                watchAccelval_Z.add(new Entry(i, hisdata.mobZ.get(i)));
            }
        }else if(flag == 1) {
            for (int i = 0; i < globals.watchX.size(); i++) {
//                mobileAccelval_X.add(new Entry(i, (float) globals.watchX.get(i)));
                mobileAccelval_Y.add(new Entry(i, (float) globals.watchY.get(i)));
//                mobileAccelval_Z.add(new Entry(i, (float) globals.watchZ.get(i)));
            }
            for(int i = 0; i < hisdata.dataSize(); i++){
//                watchAccelval_X.add(new Entry(i, hisdata.watX.get(i)));
                watchAccelval_Y.add(new Entry(i, hisdata.watY.get(i)));
//                watchAccelval_Z.add(new Entry(i, hisdata.watZ.get(i)));
            }
        }

        LineDataSet set1;
        LineDataSet set2;
        LineDataSet set3;
        LineDataSet set4;
        LineDataSet set5;
        LineDataSet set6;


            // create a dataset and give it a type
//            set1 = new LineDataSet(mobileAccelval_X, "X");
            set2 = new LineDataSet(mobileAccelval_Y, "今回のY軸");
//            set3 = new LineDataSet(mobileAccelval_Z, "Z");
//            set4 = new LineDataSet(watchAccelval_X, "比較X");
            set5 = new LineDataSet(watchAccelval_Y, "比較用のY軸");
//            set6 = new LineDataSet(watchAccelval_Z, "比較Z");

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
//            dataSets.add(setCharts(set1,Color.RED)); // add the datasets
            dataSets.add(setCharts(set2,Color.RED)); // add the datasets
//            dataSets.add(setCharts(set3,Color.GREEN)); // add the datasets
//            dataSets.add(setCharts(set4,Color.MAGENTA)); // add the datasets
            dataSets.add(setCharts(set5,Color.CYAN)); // add the datasets
//            dataSets.add(setCharts(set6,Color.LTGRAY)); //

            // create a data object with the datasets
            LineData lineData = new LineData(dataSets);

            // set data
            mChart.setData(lineData);
            mChart.invalidate();
        }

    private LineDataSet setCharts(LineDataSet set, int col){
        set.setDrawIcons(false);
        set.setDrawCircles(false);
        set.setColor(col);
        set.setLineWidth(1f);
        set.setValueTextSize(0f);
        set.setFormLineWidth(1f);
//        set.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set.setFormSize(15.f);

        return  set;
    }

    public void load(String name){
        hisdata.clear();
        try{
            FileInputStream in = openFileInput( name + ".csv" );
            BufferedReader reader = new BufferedReader( new InputStreamReader( in , "UTF-8") );
            String str = "";
            String tmp;
            int flag = 0;
            while( (tmp = reader.readLine()) != null ){
                if(flag != 1)flag = 1;
                else hisdata.setData(tmp.split(","));
            }
            reader.close();
        }catch( IOException e ){
            e.printStackTrace();
        }
    }
    public void exampleload(){
        try{
        hisdata.clear();
        Resources res = this.getResources();
        InputStream in2 = res.openRawResource(R.raw.master);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in2));
        String str = "";
        String tmp;
        int flag = 0;
        while( (tmp = reader.readLine()) != null ){
                if(flag != 1)flag = 1;
                else hisdata.setData(tmp.split(","));
            }
            reader.close();
        }catch( IOException e ){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        loadhistry();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(getApplication(), HistoryActivity.class);
            startActivity(intent);
        }
        return true;
    }

    public void loadFirebase(String downloadURL){
        storageRef = storage.getReferenceFromUrl(downloadURL);
        final File localFile = new File(getFilesDir(),"compare.csv");
        storageRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d("SUCSSESS", "SUCSSESS : yattane " + taskSnapshot);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("ERROR", "ERROR : failed to send Message " + exception);
            }
        });
    }
}
