package com.hzn.library;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;

/**
 * 折线图
 * Created by huzn on 2017/3/21.
 */
public class EasyGraphLine extends EasyGraph {

    private Paint strokePaint;
    private Paint pathPaint;
    private Path path;

    public EasyGraphLine() {
        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setColor(Color.BLUE);
        strokePaint.setStrokeWidth(5);
        strokePaint.setStyle(Paint.Style.STROKE);
        pathPaint = new Paint();
        pathPaint.setAntiAlias(true);
        pathPaint.setColor(Color.RED);
        pathPaint.setStrokeWidth(4);
        pathPaint.setStyle(Paint.Style.STROKE);
        path = new Path();
    }

    @Override
    protected void drawGraph(ArrayList<EasyPoint> rawPointList, EasyPoint pOriginal,
                             EasyPoint pMin, EasyPoint pMax, Canvas canvas) {
        if (null == rawPointList || rawPointList.size() == 0)
            return;

        sortPointByX(rawPointList);

        path.reset();
        path.moveTo(rawPointList.get(0).x, rawPointList.get(0).y);

        EasyPoint point;
        int size = rawPointList.size();
        for (int i = 0; i < size; i++) {
            point = rawPointList.get(i);
            path.lineTo(point.x, point.y);
        }
        canvas.drawPath(path, pathPaint);

        for (int i = 0; i < size; i++) {
            point = rawPointList.get(i);
            canvas.drawCircle(point.x, point.y, 10, strokePaint);
        }
    }
}
