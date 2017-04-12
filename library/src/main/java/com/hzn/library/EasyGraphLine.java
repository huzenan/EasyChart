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

    // 点的半径，默认为10px
    private float pointRadius;
    // 当前选中的点在点集中的下标
    private int selectedIndex;

    private Paint pointPaint;
    private Paint pathPaint;
    private Path path;

    /**
     * 使用默认值初始化图形
     */
    public EasyGraphLine() {
        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setColor(Color.BLUE);
        pointPaint.setStrokeWidth(5);
        pointPaint.setStyle(Paint.Style.STROKE);
        pathPaint = new Paint();
        pathPaint.setAntiAlias(true);
        pathPaint.setColor(Color.RED);
        pathPaint.setStrokeWidth(4);
        pathPaint.setStyle(Paint.Style.STROKE);
        path = new Path();
        this.pointRadius = 10.0f;
        selectedIndex = -1;
    }

    /**
     * 初始化图形
     *
     * @param pointColor       点的颜色
     * @param pointStrokeWidth 点的线段宽度
     * @param pointRadius      点的半径
     * @param pathColor        线的颜色
     * @param pathWidth        线的线段宽度
     */
    public EasyGraphLine(int pointColor, float pointStrokeWidth, float pointRadius, int pathColor, float pathWidth) {
        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setColor(pointColor);
        pointPaint.setStrokeWidth(pointStrokeWidth);
        pointPaint.setStyle(Paint.Style.STROKE);
        pathPaint = new Paint();
        pathPaint.setAntiAlias(true);
        pathPaint.setColor(pathColor);
        pathPaint.setStrokeWidth(pathWidth);
        pathPaint.setStyle(Paint.Style.STROKE);
        path = new Path();
        this.pointRadius = pointRadius;
        selectedIndex = -1;
    }

    @Override
    protected void drawGraph(ArrayList<EasyPoint> rawPointList, EasyPoint pOriginal,
                             EasyPoint pMin, EasyPoint pMax, Canvas canvas) {
        if (null == rawPointList || rawPointList.size() == 0)
            return;

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
                if (i != selectedIndex) {
                    canvas.drawCircle(point.x, point.y, this.pointRadius, pointPaint);
                } else {
                    pointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    canvas.drawCircle(point.x, point.y, this.pointRadius * 2.0f, pointPaint);
                    pointPaint.setStyle(Paint.Style.STROKE);
                }
                if (i != start && (point.x < pMin.x || point.x > pMax.x ||
                        point.y < pMin.y || point.y > pMax.y)) {
                    break;
                }
            }
        }
    }

    @Override
    protected void onClick(ArrayList<EasyPoint> rawPointList, float x, float y) {
        int size = rawPointList.size();
        int i;
        for (i = 0; i < size; i++) {
            EasyPoint point = rawPointList.get(i);
            if (x > point.x - 50 && x < point.x + 50 &&
                    y > point.y - 50 && y < point.y + 50) {
                selectedIndex = i;
                break;
            }
        }
        if (i == size)
            selectedIndex = -1;
    }
}
