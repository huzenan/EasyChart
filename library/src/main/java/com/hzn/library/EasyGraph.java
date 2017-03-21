package com.hzn.library;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 图形基类，所有在EasyCoordinate绘制的图形都继承此类
 * Created by huzn on 2017/3/17.
 */
public abstract class EasyGraph {

    /**
     * 绘制图形
     *
     * @param rawPointList 屏幕坐标点数据集
     * @param pOriginal    原点（屏幕坐标）
     * @param canvas       Canvas
     */
    protected abstract void drawGraph(ArrayList<EasyPoint> rawPointList, EasyPoint pOriginal,
                                      EasyPoint pMin, EasyPoint pMax, Canvas canvas);

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
