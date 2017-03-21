package com.hzn.easychart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hzn.library.EasyCoordinate;
import com.hzn.library.EasyGraphLine;
import com.hzn.library.EasyPoint;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EasyCoordinate easyCoordinate = (EasyCoordinate) findViewById(R.id.chart);
        ArrayList<EasyPoint> pointList = new ArrayList<>();
        pointList.add(new EasyPoint(150, 300));
        pointList.add(new EasyPoint(100, 200));
        pointList.add(new EasyPoint(-200, -150));
        pointList.add(new EasyPoint(300, 500));
        pointList.add(new EasyPoint(-120, 80));
        pointList.add(new EasyPoint(250, 400));
        easyCoordinate.setDataList(pointList, new EasyGraphLine(), true);
        easyCoordinate.refresh();

    }
}
