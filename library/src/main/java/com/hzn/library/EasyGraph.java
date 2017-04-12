package com.hzn.library;

import android.graphics.Canvas;

import java.util.ArrayList;

/**
 * 图形基类，所有在EasyCoordinate绘制的图形都继承此类
 * Created by huzn on 2017/3/17.
 */
public abstract class EasyGraph {

    /**
     * 绘制图形，建议图形根据绘制范围进行绘制，超出范围的点不进行绘制
     *
     * @param pointList    原始坐标点数据集，已经过排序
     * @param rawPointList 屏幕坐标点数据集，已经过排序
     * @param pOriginal    原点（屏幕坐标）
     * @param pMin         绘制范围左上角的点（屏幕坐标）
     * @param pMax         绘制范围右下角的点（屏幕坐标）
     * @param axisWidth    坐标轴的宽度，单位px
     * @param canvas       Canvas
     */
    protected abstract void drawGraph(ArrayList<EasyPoint> pointList, ArrayList<EasyPoint> rawPointList,
                                      EasyPoint pOriginal, EasyPoint pMin, EasyPoint pMax, float axisWidth, Canvas canvas);

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
