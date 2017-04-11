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

        EasyPoint point;
        int size = rawPointList.size();
        int start = -1;
        for (int i = 0; i < size; i++) {
            point = rawPointList.get(i);
            if (point.x > pMin.x && point.x < pMax.x &&
                    point.y > pMin.y && point.y < pMax.y) {
                path.moveTo(point.x, point.y);
                start = i == 0 ? 0 : i - 1;
                break;
            }
        }

        if (start != -1) {
            for (int i = start; i < size; i++) {
                point = rawPointList.get(i);
                path.lineTo(point.x, point.y);
                if (i != start && (point.x < pMin.x || point.x > pMax.x ||
                        point.y < pMin.y || point.y > pMax.y)) {
                    break;
                }
            }
            canvas.drawPath(path, pathPaint);

            for (int i = start; i < size; i++) {
                point = rawPointList.get(i);
                canvas.drawCircle(point.x, point.y, 10, strokePaint);
                if (i != start && (point.x < pMin.x || point.x > pMax.x ||
                        point.y < pMin.y || point.y > pMax.y)) {
                    break;
                }
            }
        }
    }
}
