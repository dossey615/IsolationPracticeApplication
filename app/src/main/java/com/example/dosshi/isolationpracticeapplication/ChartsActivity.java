package com.example.dosshi.isolationpracticeapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class ChartsActivity extends AppCompatActivity {

    private LineChart mChart;
    private Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);

        globals = (Globals)this.getApplication();
        initCharts();
    }

    public void initCharts(){
        mChart = findViewById(R.id.line_chart);

        // Grid背景色
        mChart.setDrawGridBackground(true);

        // no description text
        mChart.getDescription().setEnabled(true);

        // Grid縦軸を破線
        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = mChart.getAxisLeft();
        // Y軸最大最小設定
        leftAxis.setAxisMaximum(10);
        leftAxis.setAxisMinimum(-10);
        // Grid横軸を破線
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // 右側の目盛り
        mChart.getAxisRight().setEnabled(false);

        // add data
        setData();

        mChart.animateX(2500);
        //mChart.invalidate();

        // dont forget to refresh the drawing
        // mChart.invalidate();
    }

    public void setData(){


        ArrayList<Entry> mobileAccelval_X = new ArrayList<>();
        ArrayList<Entry> mobileAccelval_Y = new ArrayList<>();
        ArrayList<Entry> mobileAccelval_Z = new ArrayList<>();


            for (int i = 0; i < globals.mobileX.size(); i++) {
                mobileAccelval_X.add(new Entry(i,Float.parseFloat((String)globals.mobileX.get(i))));
                mobileAccelval_Y.add(new Entry(i,Float.parseFloat((String)globals.mobileY.get(i))));
                mobileAccelval_Z.add(new Entry(i,Float.parseFloat((String)globals.mobileZ.get(i))));
            }

        LineDataSet set1;
        LineDataSet set2;
        LineDataSet set3;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {

            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(mobileAccelval_X);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(mobileAccelval_X, "軸部X");
            set2 = new LineDataSet(mobileAccelval_Y, "軸部Y");
            set3 = new LineDataSet(mobileAccelval_Z, "軸部Z");

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(setCharts(set1,Color.RED)); // add the datasets
            dataSets.add(setCharts(set2,Color.BLUE)); // add the datasets
            dataSets.add(setCharts(set3,Color.GREEN)); // add the datasets

            // create a data object with the datasets
            LineData lineData = new LineData(dataSets);

            // set data
            mChart.setData(lineData);
        }
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
}
