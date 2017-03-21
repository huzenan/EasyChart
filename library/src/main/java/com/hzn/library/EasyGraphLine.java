package com.hzn.library;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

/**
 * Created by huzn on 2017/3/21.
 */
public class EasyGraphLine extends EasyGraph {

    public EasyGraphLine() {
        strokePaint.setColor(Color.BLUE);
        strokePaint.setStrokeWidth(5);
        strokePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void drawGraph(ArrayList<EasyPoint> rawPointList, EasyPoint cPOriginal, float factorX, float factorY, Canvas canvas) {
        if (null == rawPointList || rawPointList.size() == 0)
            return;

        EasyPoint point;
        int size = rawPointList.size();
        for (int i = 0; i < size; i++) {
            point = rawPointList.get(i);
            canvas.drawCircle(point.x, point.y, 10, strokePaint);
        }
    }
}
