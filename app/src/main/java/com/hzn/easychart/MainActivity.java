package com.hzn.easychart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.hzn.library.EasyCoordinate;
import com.hzn.library.EasyGraphLine;
import com.hzn.library.EasyPoint;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EasyCoordinate easyCoordinate;
    private ArrayList<EasyPoint> pointList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        easyCoordinate = (EasyCoordinate) findViewById(R.id.chart);
        pointList = new ArrayList<>();
        pointList.add(new EasyPoint(140, 300));
        pointList.add(new EasyPoint(90, 200));
        pointList.add(new EasyPoint(-200, -150));
        pointList.add(new EasyPoint(300, 500));
        pointList.add(new EasyPoint(-150, 100));
        pointList.add(new EasyPoint(380, 0));
        pointList.add(new EasyPoint(250, 400));
        pointList.add(new EasyPoint(450, -250));
        pointList.add(new EasyPoint(340, 160));
        EasyGraphLine graph = new EasyGraphLine(
                this.getResources().getColor(R.color.colorPoint),
                this.getResources().getColor(R.color.colorPointSelected),
                this.getResources().getColor(R.color.colorSelectedBg),
                dipToPx(2.0f),
                dipToPx(5.0f),
                this.getResources().getColor(R.color.colorPath),
                dipToPx(2.0f));
        graph.setOnPointSelectedListener(new EasyGraphLine.OnPointSelectedListener() {
            @Override
            public void onPointSelected(EasyPoint selectedPoint) {
                Toast.makeText(MainActivity.this, "select point(" + selectedPoint.x + "," + selectedPoint.y + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPointUnselected(EasyPoint unselectedPoint) {
                Toast.makeText(MainActivity.this, "unselect point(" + unselectedPoint.x + "," + unselectedPoint.y + ")", Toast.LENGTH_SHORT).show();
            }
        });
        easyCoordinate.setDataList(pointList, graph, true);
    }

    public int dipToPx(float dipValue) {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return (int) (dipValue * dm.density + 0.5f);
    }
}
