package com.hzn.library.line;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.hzn.library.EasyGraph;
import com.hzn.library.EasyPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * 折线图
 * Created by huzn on 2017/3/17.
 */
public class EasyGraphLine extends EasyGraph {

    // 点的绘制方式，为POINT_ABOVE或POINT_BELOW，默认为POINT_ABOVE
    private int pointDrawType = POINT_ABOVE;
    /**
     * 点位于折线上方
     */
    public static final int POINT_ABOVE = 0;
    /**
     * 点位于折线下方
     */
    public static final int POINT_BELOW = 1;

    private Path path;

    public EasyGraphLine() {
        super();
        path = new Path();
    }

    /**
     * 设置点的绘制方式
     *
     * @param pointDrawType POINT_ABOVE或POINT_BELOW
     */
    public void setPointDrawType(int pointDrawType) {
        this.pointDrawType = pointDrawType;
    }

    @Override
    protected void drawGraph(ArrayList<EasyPoint> coordinatePointList, EasyPoint cPOriginal,
                             float factorX, float factorY, Canvas canvas) {
        if (null == coordinatePointList || coordinatePointList.size() == 0)
            return;

        List<EasyPoint> list = new ArrayList<>();
        list.addAll(coordinatePointList);

        // 处理绘制参数
        initDrawAttr(list);

        // 按x值对坐标点数据集进行排序
        sortPointByX(list);

        // 获取排序后的第一个点
        EasyLinePoint firstPointLine = (EasyLinePoint) list.get(0);
        coordinateToRaw(firstPointLine, cPOriginal, factorX, factorY);

        // 初始化path
        path.reset();
        path.moveTo(firstPointLine.x, firstPointLine.y);

        // 绘制点和折线
        switch (pointDrawType) {
            case POINT_BELOW:
                drawBelow(list, cPOriginal, factorX, factorY, canvas);
                break;
            case POINT_ABOVE:
                drawAbove(list, cPOriginal, factorX, factorY, canvas);
                break;
            default:
                break;
        }
    }

    private void initDrawAttr(List<EasyPoint> list) {
        // 第一个点
        EasyPoint firstPoint = list.get(0);
        EasyLinePoint attrPoint = (EasyLinePoint) firstPoint;

        // 用第一个点的绘制参数向后设置
        int size = list.size();
        for (int i = 0; i < size; i++) {
            EasyLinePoint p = (EasyLinePoint) list.get(i);
            p.pointColor = attrPoint.pointColor;
            p.pointWidth = attrPoint.pointWidth;
            p.pointRadius = attrPoint.pointRadius;
            p.pointType = attrPoint.pointType;
            p.lineColor = attrPoint.lineColor;
            p.lineWidth = attrPoint.lineWidth;
        }

        // paint
        paint.setAntiAlias(true);
        strokePaint.setAntiAlias(true);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(attrPoint.lineWidth);
        strokePaint.setColor(attrPoint.lineColor);
    }

    // 开始绘制，点位于折线下方
    private void drawBelow(List<EasyPoint> list, EasyPoint cPOriginal, float factorX, float factorY, Canvas canvas) {
        // 绘制点
        int size = list.size();
        for (int i = 0; i < size; i++) {
            EasyPoint point = list.get(i);
            coordinateToRaw(point, cPOriginal, factorX, factorY);
            EasyLinePoint rawPointLine = (EasyLinePoint) point;
            path.lineTo(rawPointLine.x, rawPointLine.y);

            paint.setColor(rawPointLine.pointColor);
            paint.setStrokeWidth(rawPointLine.pointWidth);
            switch (rawPointLine.pointType) {
                case EasyLinePoint.POINT_TYPE_STROKE:
                    paint.setStyle(Paint.Style.STROKE);
                    break;
                case EasyLinePoint.POINT_TYPE_FILL:
                    paint.setStyle(Paint.Style.FILL);
                    break;
                case EasyLinePoint.POINT_TYPE_FILL_AND_STROKE:
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                    break;
                default:
                    paint.setStyle(Paint.Style.STROKE);
                    break;
            }
            canvas.drawCircle(rawPointLine.x, rawPointLine.y, rawPointLine.pointRadius, paint);
        }

        // 绘制折线
        canvas.drawPath(path, strokePaint);
    }

    // 开始绘制，点位于折线上方
    private void drawAbove(List<EasyPoint> list, EasyPoint cPOriginal, float factorX, float factorY, Canvas canvas) {
        // 绘制折线
        int size = list.size();
        for (int i = 1; i < size; i++) {
            EasyPoint point = list.get(i);
            coordinateToRaw(point, cPOriginal, factorX, factorY);
            EasyLinePoint rawPointLine = (EasyLinePoint) point;
            path.lineTo(rawPointLine.x, rawPointLine.y);
        }
        canvas.drawPath(path, strokePaint);

        // 绘制点
        for (int i = 0; i < size; i++) {
            EasyPoint point = list.get(i);
            EasyLinePoint rawPointLine = (EasyLinePoint) point;

            paint.setColor(rawPointLine.pointColor);
            paint.setStrokeWidth(rawPointLine.pointWidth);
            switch (rawPointLine.pointType) {
                case EasyLinePoint.POINT_TYPE_STROKE:
                    paint.setStyle(Paint.Style.STROKE);
                    break;
                case EasyLinePoint.POINT_TYPE_FILL:
                    paint.setStyle(Paint.Style.FILL);
                    break;
                case EasyLinePoint.POINT_TYPE_FILL_AND_STROKE:
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                    break;
                default:
                    paint.setStyle(Paint.Style.STROKE);
                    break;
            }
            canvas.drawCircle(rawPointLine.x, rawPointLine.y, rawPointLine.pointRadius, paint);
        }
    }

}
