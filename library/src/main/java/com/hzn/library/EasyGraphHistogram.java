package com.hzn.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.TypedValue;

import java.util.ArrayList;

/**
 * 柱状图
 * Created by huzn on 2017/4/25.
 */
public class EasyGraphHistogram extends EasyGraph {

    // 柱状图图形宽度
    private float width;
    // 边框线条颜色
    private int borderColor;
    // 边框线条选中时的颜色
    private int borderSelectedColor;
    // 边框线条宽度
    private float borderWidth;
    // 矩形填充色
    private int rectColor;
    // 矩形选中时的填充色
    private int rectSelectedColor;
    // 字体颜色
    private int textColor;
    // 文字选中时的颜色
    private int textSelectedColor;
    // 字体大小
    private float textSize;
    // 直线颜色
    private int lineColor;
    // 直线宽度
    private float lineWidth;

    private Paint borderPaint;
    private Paint rectPaint;
    private Paint linePaint;
    private TextPaint textPaint;
    private Paint.FontMetrics fm;
    private Path path;
    private Path pathDst;
    private PathMeasure pm;

    private int selectedIndex;
    private float halfFactorWidth;

    /**
     * 初始化柱状图图形
     *
     * @param width       柱状图宽度
     * @param borderColor 边框线条颜色
     * @param borderWidth 边框线条宽度
     * @param rectColor   填充矩形颜色
     * @param textColor   字体颜色
     * @param textSize    字体大小
     * @param lineColor   折线颜色
     * @param lineWidth   折线宽度
     */
    public EasyGraphHistogram(float width, int borderColor, int borderSelectedColor, float borderWidth, int rectColor, int rectSelectedColor,
                              int textColor, int textSelectedColor, float textSize, int lineColor, float lineWidth) {
        this.width = width;
        this.borderColor = borderColor;
        this.borderSelectedColor = borderSelectedColor;
        this.borderWidth = borderWidth;
        this.rectColor = rectColor;
        this.rectSelectedColor = rectSelectedColor;
        this.textColor = textColor;
        this.textSelectedColor = textSelectedColor;
        this.textSize = textSize;
        this.lineColor = lineColor;
        this.lineWidth = lineWidth;
        init();
    }

    /**
     * 使用默认值初始化柱状图图形
     *
     * @param context Context
     */
    public EasyGraphHistogram(Context context) {
        this(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics()),
                Color.GRAY,
                Color.RED,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics()),
                Color.RED,
                Color.YELLOW,
                Color.BLACK,
                Color.RED,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, context.getResources().getDisplayMetrics()),
                Color.BLUE,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics()));
    }

    private void init() {
        borderPaint = new Paint();
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidth);
        rectPaint = new Paint();
        rectPaint.setColor(rectColor);
        rectPaint.setStyle(Paint.Style.FILL);
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(lineColor);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        fm = textPaint.getFontMetrics();
        path = new Path();
        pathDst = new Path();
        pm = new PathMeasure();
        selectedIndex = -1;
    }

    @Override
    protected void drawGraph(ArrayList<EasyPoint> pointList, ArrayList<EasyPoint> rawPointList, RectF rectCanvas, RectF rectGraph,
                             int start, int end, EasyPoint pOriginal, EasyPoint pMin, EasyPoint pMax, float axisWidth,
                             float factorX, float factorY, float animatorValue, Canvas canvas) {
        if (rectCanvas.intersect(rectGraph)) {

            halfFactorWidth = width * factorX / 2;

            EasyPoint p;
            EasyPoint pR;
            RectF r = new RectF();
            String text;
            EasyPoint startP = rawPointList.get(start);
            path.reset();
            path.moveTo(startP.x, startP.y);

            // 绘制矩形
            for (int i = start; i <= end; i++) {
                p = pointList.get(i);
                pR = rawPointList.get(i);
                r.set(pR.x - halfFactorWidth / 2,
                        p.y > 0 ? pOriginal.y + (pR.y - pOriginal.y) * animatorValue : pOriginal.y + axisWidth / 2 + borderWidth / 2,
                        pR.x + halfFactorWidth / 2,
                        p.y > 0 ? pOriginal.y - axisWidth / 2 - borderWidth / 2 : pOriginal.y + (pR.y - pOriginal.y) * animatorValue);

                if (i != selectedIndex) {
                    canvas.drawRect(r, rectPaint);
                    canvas.drawRect(r, borderPaint);
                } else {
                    rectPaint.setColor(rectSelectedColor);
                    borderPaint.setColor(borderSelectedColor);
                    canvas.drawRect(r, rectPaint);
                    canvas.drawRect(r, borderPaint);
                    rectPaint.setColor(rectColor);
                    borderPaint.setColor(borderColor);
                }

                path.lineTo(pR.x, pR.y);
            }

            // 绘制线条
            pathDst.reset();
            pathDst.rLineTo(0, 0);
            pm.setPath(path, false);
            pm.getSegment(0.0f, pm.getLength() * animatorValue, pathDst, true);
            canvas.drawPath(pathDst, linePaint);

            // 绘制文字
            for (int i = start; i <= end; i++) {
                p = pointList.get(i);
                pR = rawPointList.get(i);

                text = String.valueOf(p.y);
                float textHeight = fm.bottom - fm.top;
                float textWidth = textPaint.measureText(text);
                float mid = p.y > 0 ?
                        pR.y - textHeight / 2 :
                        pR.y + textHeight / 2;
                float baseLine = mid - (fm.ascent + fm.descent) / 2;

                if (i != selectedIndex) {
                    canvas.drawText(text, pR.x - textWidth / 2, baseLine, textPaint);
                } else {
                    textPaint.setColor(textSelectedColor);
                    canvas.drawText(text, pR.x - textWidth / 2, baseLine, textPaint);
                    textPaint.setColor(textColor);
                }
            }
        }
    }

    @Override
    protected void onClickGraph(ArrayList<EasyPoint> pointList, ArrayList<EasyPoint> rawPointList,
                                int start, int end, float x, float y, EasyPoint pOriginal, float factorX, float factorY) {
        float touchRegion = 25 * Math.min(factorX, factorY);
        int i;
        for (i = start; i <= end; i++) {
            EasyPoint point = rawPointList.get(i);
            if (x > point.x - halfFactorWidth / 2 - touchRegion && x < point.x + halfFactorWidth / 2 + touchRegion &&
                    y > Math.min(point.y, pOriginal.y) - touchRegion && y < Math.max(point.y, pOriginal.y + touchRegion)) {
                selectedIndex = i;
                break;
            }
        }
        if (i == end + 1) {
            if (selectedIndex != -1 && null != onPointSelectedListener)
                onPointSelectedListener.onPointUnselected(pointList.get(selectedIndex));
            selectedIndex = -1;
        } else if (null != onPointSelectedListener) {
            onPointSelectedListener.onPointSelected(pointList.get(selectedIndex));
        }
    }
}
