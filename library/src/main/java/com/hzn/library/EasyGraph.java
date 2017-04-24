package com.hzn.library;

import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.ArrayList;

/**
 * 图形基类，所有在EasyCoordinate绘制的图形都继承此类
 * Created by huzn on 2017/3/17.
 */
public abstract class EasyGraph {

    /**
     * 由坐标系类EasyCoordinate调用绘制，将计算出画布范围以及绘制图形的范围，提供给图形绘制时使用
     *
     * @param pointList    原始坐标点数据集，已经过排序
     * @param rawPointList 屏幕坐标点数据集，已经过排序
     * @param pOriginal    原点（屏幕坐标）
     * @param pMin         绘制范围左上角的点（屏幕坐标）
     * @param pMax         绘制范围右下角的点（屏幕坐标）
     * @param axisWidth    坐标轴的宽度，单位px
     * @param canvas       Canvas
     */
    public void draw(ArrayList<EasyPoint> pointList, ArrayList<EasyPoint> rawPointList,
                     EasyPoint pOriginal, EasyPoint pMin, EasyPoint pMax, float axisWidth, Canvas canvas) {
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
        drawGraph(pointList, rawPointList, rectCanvas, rectGraph, start, end, pOriginal, pMin, pMax, axisWidth, canvas);
    }

    /**
     * 绘制图形，建议在绘制图形时，根据绘制范围rectCanvas和rectGraph，例如使用RectF.intersects(rectCanvas, rectGraph)进行判断；
     * 以及起始start、结束点end进行绘制，超出范围的点不进行绘制
     *
     * @param pointList    原始坐标点数据集，已经过排序
     * @param rawPointList 屏幕坐标点数据集，已经过排序
     * @param rectCanvas   绘制范围的最小矩形
     * @param rectGraph    绘制图形范围的最小矩形，以点为边界
     * @param start        绘制图形范围的最小矩形范围内的起始点
     * @param end          绘制图形范围的最小矩形范围内的结束点
     * @param pOriginal    原点（屏幕坐标）
     * @param pMin         绘制范围左上角的点（屏幕坐标）
     * @param pMax         绘制范围右下角的点（屏幕坐标）
     * @param axisWidth    坐标轴的宽度，单位px
     * @param canvas       Canvas
     */
    protected abstract void drawGraph(ArrayList<EasyPoint> pointList, ArrayList<EasyPoint> rawPointList, RectF rectCanvas, RectF rectGraph,
                                      int start, int end, EasyPoint pOriginal, EasyPoint pMin, EasyPoint pMax, float axisWidth, Canvas canvas);

    /**
     * 点击事件，在EasyCoordinate回调此方法后，会调用一次refresh刷新视图
     *
     * @param pointList    原始坐标点数据集，已经过排序
     * @param rawPointList 屏幕坐标点数据集，已经过排序
     * @param x            点击的x坐标（屏幕坐标）
     * @param y            点击的y坐标（屏幕坐标）
     */
    protected abstract void onClick(ArrayList<EasyPoint> pointList, ArrayList<EasyPoint> rawPointList, float x, float y);
}
