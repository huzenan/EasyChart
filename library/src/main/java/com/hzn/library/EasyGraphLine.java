package com.hzn.library;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import java.util.ArrayList;

/**
 * 折线图
 * Created by huzn on 2017/3/21.
 */
public class EasyGraphLine extends EasyGraph {

    // 点的半径，默认为10px
    private float pointRadius;
    // 点的颜色
    private int pointColor;
    // 当前选中点的颜色
    private int selectedColor;
    // 当前选中的点在点集中的下标
    private int selectedIndex;

    private Paint pointPaint;
    private Paint pathPaint;
    private Paint selectedBgPaint;
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
        selectedBgPaint = new Paint();
        selectedBgPaint.setAntiAlias(true);
        selectedBgPaint.setColor(Color.YELLOW);
        selectedBgPaint.setStrokeWidth(0);
        selectedBgPaint.setStyle(Paint.Style.FILL);
        selectedBgPaint.setAlpha(100);
        path = new Path();
        this.pointRadius = 10.0f;
        this.pointColor = Color.BLUE;
        this.selectedColor = Color.RED;
        selectedIndex = -1;
    }

    /**
     * 初始化图形
     *
     * @param pointColor       点的颜色
     * @param selectedColor    选中点的颜色
     * @param pointStrokeWidth 点的线段宽度
     * @param pointRadius      点的半径
     * @param pathColor        线的颜色
     * @param pathWidth        线的线段宽度
     */
    public EasyGraphLine(int pointColor, int selectedColor, int selectedBgColor,
                         float pointStrokeWidth, float pointRadius, int pathColor, float pathWidth) {
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
        selectedBgPaint = new Paint();
        selectedBgPaint.setAntiAlias(true);
        selectedBgPaint.setColor(selectedBgColor);
        selectedBgPaint.setStrokeWidth(0);
        selectedBgPaint.setStyle(Paint.Style.FILL);
        selectedBgPaint.setAlpha(100);
        path = new Path();
        this.pointRadius = pointRadius;
        this.pointColor = pointColor;
        this.selectedColor = selectedColor;
        selectedIndex = -1;
    }

    @Override
    protected void drawGraph(ArrayList<EasyPoint> pointList, ArrayList<EasyPoint> rawPointList, EasyPoint pOriginal,
                             EasyPoint pMin, EasyPoint pMax, float axisWidth, Canvas canvas) {
        if (null == rawPointList || rawPointList.size() == 0)
            return;

        // 计算出需要绘制的点的范围
        EasyPoint min = new EasyPoint();
        EasyPoint max = new EasyPoint();
        int size = rawPointList.size();
        int start;
        int end;
        int i;
        for (i = 0; i < size; i++) {
            EasyPoint p = rawPointList.get(i);
            if (p.x >= pMin.x)
                break;
        }
        i = i == 0 ? 0 : i - 1;
        start = i;
        min.x = rawPointList.get(start).x;
        min.y = rawPointList.get(start).y; // init

        for (; i < size; i++) {
            EasyPoint p = rawPointList.get(i);

            if (p.y < min.y)
                min.y = p.y;
            else if (p.y > max.y)
                max.y = p.y;

            if (p.x > pMax.x)
                break;
        }
        end = i == size ? i - 1 : i;
        max.x = rawPointList.get(end).x;

        // 根据范围确定是否绘制
        RectF rectCanvas = new RectF(pMin.x, pMin.y, pMax.x, pMax.y);
        RectF rectGraph = new RectF(min.x, min.y, max.x, max.y);
        if (RectF.intersects(rectCanvas, rectGraph)) {
            // 绘制点击后的矩形背景
            if (selectedIndex != -1) {
                EasyPoint p = rawPointList.get(selectedIndex);
                RectF bgRect = new RectF(
                        Math.min(pOriginal.x, p.x),
                        Math.min(pOriginal.y, p.y),
                        Math.max(pOriginal.x, p.x),
                        Math.max(pOriginal.y, p.y));
                if (RectF.intersects(rectCanvas, bgRect))
                    canvas.drawRect(bgRect, selectedBgPaint);
            }

            // 绘制线段
            EasyPoint startPoint = rawPointList.get(start);
            path.reset();
            path.moveTo(startPoint.x, startPoint.y);
            for (int j = start; j <= end; j++) {
                EasyPoint p = rawPointList.get(j);
                path.lineTo(p.x, p.y);
            }
            canvas.drawPath(path, pathPaint);

            // 绘制点
            for (int j = start; j <= end; j++) {
                EasyPoint p = rawPointList.get(j);
                if (j != selectedIndex) {
                    canvas.drawCircle(p.x, p.y, pointRadius, pointPaint);
                } else if (j == selectedIndex) {
                    pointPaint.setColor(selectedColor);
                    pointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    canvas.drawCircle(p.x, p.y, pointRadius, pointPaint);
                    pointPaint.setColor(pointColor);
                    pointPaint.setStyle(Paint.Style.STROKE);
                }
            }
        }
    }

    @Override
    protected void onClick(ArrayList<EasyPoint> pointList, ArrayList<EasyPoint> rawPointList, float x, float y) {
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
        if (i == size) {
            if (selectedIndex != -1 && null != onPointSelectedListener)
                onPointSelectedListener.onPointUnselected(pointList.get(selectedIndex));
            selectedIndex = -1;
        } else if (null != onPointSelectedListener) {
            onPointSelectedListener.onPointSelected(pointList.get(selectedIndex));
        }
    }

    /**
     * 监听点的选中与取消选中
     */
    public interface OnPointSelectedListener {
        /**
         * 点被选中时回调
         *
         * @param selectedPoint 被选中的点（原始坐标）
         */
        void onPointSelected(EasyPoint selectedPoint);

        /**
         * 点击其他区域取消点的选中时回调
         *
         * @param unselectedPoint 被取消选中的点（原始坐标）
         */
        void onPointUnselected(EasyPoint unselectedPoint);
    }

    private OnPointSelectedListener onPointSelectedListener;

    public void setOnPointSelectedListener(OnPointSelectedListener onPointSelectedListener) {
        this.onPointSelectedListener = onPointSelectedListener;
    }
}
