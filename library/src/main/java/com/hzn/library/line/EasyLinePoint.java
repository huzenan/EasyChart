package com.hzn.library.line;

import android.graphics.Color;

import com.hzn.library.EasyPoint;

/**
 * 折线图使用的点，继承自EasyPoint
 * Created by huzn on 2017/3/17.
 */
public class EasyLinePoint extends EasyPoint {
    /**
     * 点的颜色，默认Color.BLACK
     */
    public int pointColor;
    /**
     * 点的线段宽度，单位px，默认5px
     */
    public int pointWidth;
    /**
     * 点的半径，单位px，默认10px
     */
    public int pointRadius;
    /**
     * 折线的颜色，默认Color.BLACK
     */
    public int lineColor;
    /**
     * 折线的宽度，单位px，默认5px
     */
    public int lineWidth;

    /**
     * 点的类型，为POINT_TYPE_FILL,POINT_TYPE_STROKE或POINT_TYPE_FILL_AND_STROKE，
     * 默认为POINT_TYPE_STROKE
     */
    public int pointType;
    /**
     * 点的类型-只描边
     */
    public static final int POINT_TYPE_STROKE = 0;
    /**
     * 点的类型-只填充
     */
    public static final int POINT_TYPE_FILL = 1;
    /**
     * 点的类型-填充+描边
     */
    public static final int POINT_TYPE_FILL_AND_STROKE = 2;

    /**
     * 通过坐标初始化，并使用默认绘制参数
     *
     * @param x x坐标
     * @param y y坐标
     */
    public EasyLinePoint(float x, float y) {
        super(x, y);
        this.pointColor = Color.BLACK;
        this.pointWidth = 5;
        this.pointRadius = 10;
        this.lineColor = Color.BLACK;
        this.lineWidth = 5;
        this.pointType = POINT_TYPE_STROKE;
    }

    /**
     * 通过坐标和绘制参数初始化
     *
     * @param x           x坐标
     * @param y           y坐标
     * @param pointColor  点的颜色
     * @param pointWidth  点的线段宽度，单位px
     * @param pointRadius 点的半径，单位px
     * @param lineColor   线段的颜色
     * @param lineWidth   线段的宽度，单位px
     * @param pointType   点的类型，为POINT_TYPE_FILL,POINT_TYPE_STROKE或POINT_TYPE_FILL_AND_STROKE
     */
    public EasyLinePoint(float x, float y, int pointColor, int pointWidth, int pointRadius, int lineColor, int lineWidth, int pointType) {
        super(x, y);
        this.pointColor = pointColor;
        this.pointWidth = pointWidth;
        this.pointRadius = pointRadius;
        this.lineColor = lineColor;
        this.lineWidth = lineWidth;
        this.pointType = pointType;
    }
}
