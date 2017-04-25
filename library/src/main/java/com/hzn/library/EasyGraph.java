package com.hzn.library;

import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.ArrayList;

/**
 * 图形基类，所有在EasyCoordinate绘制的图形都继承此类
 * Created by huzn on 2017/3/17.
 */
public abstract class EasyGraph {

    protected OnPointSelectedListener onPointSelectedListener;
    private int startIndex;
    private int endIndex;

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
        int i;
        for (i = 0; i < size; i++) {
            EasyPoint p = rawPointList.get(i);
            if (p.x >= pMin.x)
                break;
        }
        i = i == 0 ? 0 : i - 1;
        startIndex = i;
        min.x = rawPointList.get(startIndex).x;
        min.y = rawPointList.get(startIndex).y; // init

        for (; i < size; i++) {
            EasyPoint p = rawPointList.get(i);

            if (p.y < min.y)
                min.y = p.y;
            else if (p.y > max.y)
                max.y = p.y;

            if (p.x > pMax.x)
                break;
        }
        endIndex = i == size ? i - 1 : i;
        max.x = rawPointList.get(endIndex).x;

        // 根据范围确定是否绘制
        RectF rectCanvas = new RectF(pMin.x, pMin.y, pMax.x, pMax.y);
        RectF rectGraph = new RectF(min.x, min.y, max.x, max.y);
        drawGraph(pointList, rawPointList, rectCanvas, rectGraph, startIndex, endIndex, pOriginal, pMin, pMax, axisWidth, canvas);
    }

    /**
     * 绘制图形，建议在绘制图形时，根据绘制范围rectCanvas和rectGraph，例如使用RectF.intersects(rectCanvas, rectGraph)进行判断；
     * 以及起始start、结束点end进行绘制，超出范围的点不进行绘制
     *
     * @param pointList    原始坐标点数据集，已经过排序
     * @param rawPointList 屏幕坐标点数据集，已经过排序
     * @param rectCanvas   绘制范围的最小矩形
     * @param rectGraph    绘制图形范围的最小矩形，以点为边界
     * @param start        绘制图形范围的最小矩形范围内的起始点下标
     * @param end          绘制图形范围的最小矩形范围内的结束点下标
     * @param pOriginal    原点（屏幕坐标）
     * @param pMin         绘制范围左上角的点（屏幕坐标）
     * @param pMax         绘制范围右下角的点（屏幕坐标）
     * @param axisWidth    坐标轴的宽度，单位px
     * @param canvas       Canvas
     */
    protected abstract void drawGraph(ArrayList<EasyPoint> pointList, ArrayList<EasyPoint> rawPointList, RectF rectCanvas, RectF rectGraph,
                                      int start, int end, EasyPoint pOriginal, EasyPoint pMin, EasyPoint pMax, float axisWidth, Canvas canvas);

    /**
     * 点击事件，由坐标系类EasyCoordinate调用，在EasyCoordinate回调此方法后，会调用一次refresh刷新视图
     *
     * @param pointList    原始坐标点数据集，已经过排序
     * @param rawPointList 屏幕坐标点数据集，已经过排序
     * @param x            点击的x坐标（屏幕坐标）
     * @param y            点击的y坐标（屏幕坐标）
     */
    public void onClick(ArrayList<EasyPoint> pointList, ArrayList<EasyPoint> rawPointList, float x, float y, EasyPoint pOriginal) {
        onClickGraph(pointList, rawPointList, startIndex, endIndex, x, y, pOriginal);
    }

    /**
     * 点击事件回调
     *
     * @param pointList    原始坐标点数据集，已经过排序
     * @param rawPointList 屏幕坐标点数据集，已经过排序
     * @param start        绘制图形范围的最小矩形范围内的起始点下标
     * @param end          绘制图形范围的最小矩形范围内的结束点下标
     * @param x            点击的x坐标（屏幕坐标）
     * @param y            点击的y坐标（屏幕坐标）
     */
    protected abstract void onClickGraph(ArrayList<EasyPoint> pointList, ArrayList<EasyPoint> rawPointList,
                                         int start, int end, float x, float y, EasyPoint pOriginal);

    /**
     * 设置选中和取消选中的监听
     *
     * @param onPointSelectedListener EasyGraph.OnPointSelectedListener
     */
    public void setOnPointSelectedListener(OnPointSelectedListener onPointSelectedListener) {
        this.onPointSelectedListener = onPointSelectedListener;
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
}
