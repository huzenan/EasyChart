package com.hzn.easychart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hzn.library.EasyCoordinate;
import com.hzn.library.EasyGraph;
import com.hzn.library.EasyGraphHistogram;
import com.hzn.library.EasyGraphLine;
import com.hzn.library.EasyPoint;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class FirstQuartileActivity extends AppCompatActivity {

    private String graphLine = "g_line";
    private String graphLine2 = "g_line2";
    private String graphHistogram = "g_histogram";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_quartile);
        init();
    }

    private void init() {
        final EasyCoordinate easyCoordinate = (EasyCoordinate) findViewById(R.id.chart1);
        HashMap<String, EasyCoordinate.EasyCoordinateEntity> coordinateMap = new HashMap<>();
        coordinateMap.put(graphLine, new EasyCoordinate.EasyCoordinateEntity(getPointList(), getGraphLine()));
        coordinateMap.put(graphLine2, new EasyCoordinate.EasyCoordinateEntity(getPointList(), getGraphLine2()));
        easyCoordinate.setDataList(coordinateMap);
        easyCoordinate.initAnimation(graphLine, true, 2500);
        easyCoordinate.initAnimation(graphLine2, true, 2500);
        easyCoordinate.startAnimation(graphLine);
        easyCoordinate.startAnimation(graphLine2);

        final EasyCoordinate easyCoordinate2 = (EasyCoordinate) findViewById(R.id.chart2);
        HashMap<String, EasyCoordinate.EasyCoordinateEntity> coordinateMap2 = new HashMap<>();
        coordinateMap2.put(graphHistogram, new EasyCoordinate.EasyCoordinateEntity(getFixXPointList(), getGraphHistogram()));
        easyCoordinate2.setDataList(coordinateMap2);
        easyCoordinate2.initAnimation(graphHistogram, true, 2500);
        easyCoordinate2.startAnimation(graphHistogram);

        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                easyCoordinate.startAnimation(graphLine);
                easyCoordinate.startAnimation(graphLine2);
                easyCoordinate2.startAnimation(graphHistogram);
            }
        });
    }

    private ArrayList<EasyPoint> getPointList() {
        ArrayList<EasyPoint> pointList = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00");
        float x, y;
        for (int i = 0; i < 50; i++) {
            x = (float) (Math.random() * 2000.0f);
            y = (float) (Math.random() * 500.0f);
            pointList.add(new EasyPoint(x, Float.valueOf(df.format(y))));
        }
        return pointList;
    }

    private ArrayList<EasyPoint> getFixXPointList() {
        ArrayList<EasyPoint> pointList = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00");
        float y;
        for (int i = 50; i <= 2550; i += 50) {
            y = (float) (Math.random() * 500.0f);
            pointList.add(new EasyPoint(i, Float.valueOf(df.format(y))));
        }
        return pointList;
    }

    // 测试折线图
    private EasyGraphLine getGraphLine() {
        return new EasyGraphLine(
                this.getResources().getColor(R.color.colorPoint),
                this.getResources().getColor(R.color.colorPointSelected),
                this.getResources().getColor(R.color.colorSelectedBg),
                dipToPx(2.0f),
                dipToPx(5.0f),
                this.getResources().getColor(R.color.colorPath),
                dipToPx(2.0f));
    }

    // 测试折线图
    private EasyGraphLine getGraphLine2() {
        return new EasyGraphLine(
                this.getResources().getColor(R.color.colorText),
                this.getResources().getColor(R.color.colorPrimaryDark),
                this.getResources().getColor(R.color.colorTextLight),
                dipToPx(2.0f),
                dipToPx(5.0f),
                this.getResources().getColor(R.color.colorPrimary),
                dipToPx(2.0f));
    }

    // 柱状图（带折线）
    private EasyGraphHistogram getGraphHistogram() {
        return new EasyGraphHistogram(
                dipToPx(12),
                this.getResources().getColor(R.color.colorTextSelected),
                this.getResources().getColor(R.color.colorPointSelected),
                dipToPx(1.5f),
                this.getResources().getColor(R.color.colorPoint),
                this.getResources().getColor(R.color.colorPath),
                this.getResources().getColor(R.color.colorText),
                this.getResources().getColor(R.color.colorPointSelected),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()),
                this.getResources().getColor(R.color.colorPath),
                dipToPx(2)
        );
    }

    public int dipToPx(float dipValue) {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return (int) (dipValue * dm.density + 0.5f);
    }
}
