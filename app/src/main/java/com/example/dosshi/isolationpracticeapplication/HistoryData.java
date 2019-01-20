package com.example.dosshi.isolationpracticeapplication;

import java.util.ArrayList;

public class HistoryData{
    public ArrayList<Float> mobX = new ArrayList<>();
    public ArrayList<Float> mobY = new ArrayList<>();
    public ArrayList<Float> mobZ = new ArrayList<>();
    public ArrayList<Float> watX = new ArrayList<>();
    public ArrayList<Float> watY = new ArrayList<>();
    public ArrayList<Float> watZ = new ArrayList<>();

    public HistoryData(){
        mobX.add(0f);
        mobY.add(0f);
        mobZ.add(0f);
        watX.add(0f);
        watY.add(0f);
        watZ.add(0f);
    }
    public void clear(){
        mobX.clear();
        mobY.clear();
        mobZ.clear();
        watX.clear();
        watY.clear();
        watZ.clear();
    }

    public void setData(String[] data){
        mobX.add(Float.parseFloat(data[1]));
        mobY.add(Float.parseFloat(data[2]));
        mobZ.add(Float.parseFloat(data[3]));
        watX.add(Float.parseFloat(data[8]));
        watY.add(Float.parseFloat(data[9]));
        watZ.add(Float.parseFloat(data[10]));
    }
    public int dataSize(){
        return mobX.size();
    }

    public int dataSizeWatch(){
        return watX.size();
    }

}
