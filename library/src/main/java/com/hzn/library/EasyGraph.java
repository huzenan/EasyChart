package com.hzn.library;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 图形基类，所有在EasyCoordinate绘制的图形都继承此类
 * Created by huzn on 2017/3/17.
 */
public abstract class EasyGraph {

    protected Paint paint;
    protected Paint strokePaint;

    public EasyGraph() {
        paint = new Paint();
        strokePaint = new Paint();
    }

    /**
     * 绘制图形
     *
     * @param coordinatePointList 坐标点数据集
     * @param cPOriginal          原点（坐标系）
     * @param factorX             x轴方向上的缩放比例
     * @param factorY             y轴方向上的缩放比例
     * @param canvas              Canvas
     */
    protected abstract void drawGraph(ArrayList<EasyPoint> coordinatePointList, EasyPoint cPOriginal,
                                      float factorX, float factorY, Canvas canvas);

    /**
     * 用于将坐标系坐标转为屏幕坐标
     *
     * @param coordinatePoint 坐标系坐标点
     * @param cPOriginal      原点（坐标系）
     * @param factorX         x轴方向上的缩放比例
     * @param factorY         y轴方向上的缩放比例
     */
    protected void coordinateToRaw(EasyPoint coordinatePoint, EasyPoint cPOriginal,
                                   float factorX, float factorY) {
        coordinatePoint.x = cPOriginal.x + coordinatePoint.x * factorX;
        coordinatePoint.y = cPOriginal.y - coordinatePoint.y * factorY;
    }

    /**
     * 根据x值对坐标点数据集进行升序排序
     *
     * @param pointList 坐标点数据集
     */
    protected void sortPointByX(List<EasyPoint> pointList) {
        Collections.sort(pointList, new Comparator<Object>() {
            @Override
            public int compare(Object lhs, Object rhs) {
                EasyPoint l = (EasyPoint) lhs;
                EasyPoint r = (EasyPoint) rhs;
                return l.x > r.x ? 1 : l.x == r.x ? 0 : -1;
            }
        });
    }

    /**
     * 根据y值对坐标点数据集进行升序排序
     *
     * @param pointList 坐标点数据集
     */
    protected void sortPointByY(List<EasyPoint> pointList) {
        Collections.sort(pointList, new Comparator<Object>() {
            @Override
            public int compare(Object lhs, Object rhs) {
                EasyPoint l = (EasyPoint) lhs;
                EasyPoint r = (EasyPoint) rhs;
                return l.y > r.y ? 1 : l.y == r.y ? 0 : -1;
            }
        });
    }
}
