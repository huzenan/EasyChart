package com.hzn.easychart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.hzn.library.EasyCoordinate;
import com.hzn.library.EasyGraphLine;
import com.hzn.library.EasyPoint;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EasyCoordinate easyCoordinate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        easyCoordinate = (EasyCoordinate) findViewById(R.id.chart);
        ArrayList<EasyPoint> pointList = new ArrayList<>();
        pointList.add(new EasyPoint(150, 300));
        pointList.add(new EasyPoint(100, 200));
        pointList.add(new EasyPoint(-200, -150));
        pointList.add(new EasyPoint(300, 500));
        pointList.add(new EasyPoint(-150, 100));
        pointList.add(new EasyPoint(250, 400));
        EasyGraphLine graph = new EasyGraphLine(
                this.getResources().getColor(R.color.colorPoint),
                dipToPx(2.0f),
                dipToPx(5.0f),
                this.getResources().getColor(R.color.colorPath),
                dipToPx(2.0f));
        easyCoordinate.setDataList(pointList, EasyCoordinate.SORT_TYPE_X, graph, true);
    }

    public int dipToPx(float dipValue) {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return (int) (dipValue * dm.density + 0.5f);
    }
}
