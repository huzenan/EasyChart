package com.hzn.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * 坐标系
 * Created by huzn on 2017/3/16.
 */
public class EasyCoordinate extends View {

    // 原点坐标x的位置，范围0.0f~1.0f，例如0.0f为最左端，默认0.5f
    private float originalXScale;
    // 原点坐标y的位置，范围0.0f~1.0f，例如0.0f为最下端，默认0.5f
    private float originalYScale;
    // 坐标轴的颜色，默认Color.BLACK
    private int axisColor;
    // 坐标轴的宽度，单位dp，默认1dp
    private int axisWidth;

    private Paint cPaint;

    // 坐标系实际绘制宽度
    private float cWidth;
    // 坐标系实际绘制高度
    private float cHeight;
    // 左上角点的坐标，用于固定绘制边界
    private EasyPoint pMin;
    // 左上角点的坐标
    private EasyPoint cPMin;
    // 右下角点的坐标，用于固定绘制边界
    private EasyPoint pMax;
    // 右下角点的坐标
    private EasyPoint cPMax;
    // 原点坐标
    private EasyPoint pOriginal;
    // x轴方向上的缩放比例
    private float factorX;
    // y轴方向上的缩放比例
    private float factorY;

    private ArrayList<EasyPoint> coordinatePointList;
    private ArrayList<EasyPoint> rawPointList;
    private EasyGraph graph;

    public EasyCoordinate(Context context) {
        this(context, null);
    }

    public EasyCoordinate(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyCoordinate(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EasyCoordinate, defStyleAttr, 0);
        originalXScale = a.getFloat(R.styleable.EasyCoordinate_ecOriginalXScale, 0.5f);
        originalYScale = a.getFloat(R.styleable.EasyCoordinate_ecOriginalYScale, 0.5f);
        axisColor = a.getColor(R.styleable.EasyCoordinate_ecAxisColor, Color.BLACK);
        axisWidth = a.getDimensionPixelOffset(R.styleable.EasyCoordinate_ecAxisWidth, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1.0f, getResources().getDisplayMetrics()));
        a.recycle();

        pMin = new EasyPoint();
        cPMin = new EasyPoint();
        pMax = new EasyPoint();
        cPMax = new EasyPoint();
        pOriginal = new EasyPoint();

        cPaint = new Paint();
        cPaint.setAntiAlias(true);
        cPaint.setStyle(Paint.Style.STROKE);
        cPaint.setColor(axisColor);
        cPaint.setStrokeWidth(axisWidth);

        coordinatePointList = new ArrayList<>();
        rawPointList = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (mode != MeasureSpec.EXACTLY) { // wrap_content
            width = width + getPaddingLeft() + getPaddingRight();
        }

        mode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mode != MeasureSpec.EXACTLY) { // wrap_content
            height = height + getPaddingTop() + getPaddingBottom();
        }

        cWidth = width - getPaddingLeft() - getPaddingRight();
        cHeight = height - getPaddingTop() - getPaddingBottom();

        // 固定绘制边界
        pMin.set(getPaddingLeft(), getPaddingTop());
        pMax.set(width - getPaddingRight(), height - getPaddingBottom());

        // 第一次初始化坐标系
        float ox = pMin.x + cWidth * originalXScale;
        float oy = pMax.y - cHeight * originalYScale;
        resetCoordinate(ox, oy, -ox, oy - cHeight, pMax.x - ox, oy - pMin.y);

        setMeasuredDimension(width, height);
    }

    /**
     * 重置坐标系
     *
     * @param oX    原点x值（屏幕）
     * @param oY    原点y值（屏幕）
     * @param cMinX 最小x值（坐标系）
     * @param cMinY 最小y值（坐标系）
     * @param cMaxX 最大x值（坐标系）
     * @param cMaxY 最大y值（坐标系）
     */
    private void resetCoordinate(float oX, float oY, float cMinX, float cMinY, float cMaxX, float cMaxY) {
        cPMin.set(cMinX, cMinY);
        cPMax.set(cMaxX, cMaxY);
        pOriginal.set(oX, oY);
        factorX = cWidth / (cPMax.x - cPMin.x);
        factorY = cHeight / (cPMax.y - cPMin.y);
        coordinateToRaw();
    }

    // 坐标点转屏幕坐标点
    private void coordinateToRaw() {
        if (null == coordinatePointList || coordinatePointList.size() == 0)
            return;

        rawPointList.clear();

        EasyPoint point;
        int size = coordinatePointList.size();
        for (int i = 0; i < size; i++) {
            point = coordinatePointList.get(i);
            rawPointList.add(new EasyPoint(pOriginal.x + point.x * factorX, pOriginal.y - point.y * factorY));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(pMin.x, pOriginal.y, pMax.x, pOriginal.y, cPaint);
        canvas.drawLine(pOriginal.x, pMin.y, pOriginal.x, pMax.y, cPaint);
        drawGraph(canvas);
    }

    // 绘制图形内容
    private void drawGraph(Canvas canvas) {
        this.graph.drawGraph(rawPointList, pOriginal, factorX, factorY, canvas);
    }

    private EasyPoint lastPoint = new EasyPoint();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                float deltaX = (x - lastPoint.x) * factorX;
                float deltaY = (y - lastPoint.y) * factorY;
                resetCoordinate(pOriginal.x + deltaX, pOriginal.y + deltaY,
                        cPMin.x - deltaX, cPMin.y + deltaY,
                        cPMax.x - deltaX, cPMax.y + deltaY);
                lastPoint.set(x, y);
                refresh();
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 设置坐标点数据集
     *
     * @param pointList 坐标点数据集
     * @param clear     是否清除原有数据
     */
    public void setDataList(ArrayList<EasyPoint> pointList, boolean clear) {
        if (null == pointList || pointList.size() == 0)
            return;

        if (clear)
            this.coordinatePointList.clear();
        this.coordinatePointList.addAll(pointList);
    }

    /**
     * 设置坐标点数据集和图形类
     *
     * @param pointList 坐标点数据集
     * @param graph     图形类
     * @param clear     是否清除原有数据
     */
    public void setDataList(ArrayList<EasyPoint> pointList, EasyGraph graph, boolean clear) {
        setDataList(pointList, clear);
        this.graph = graph;
    }

    /**
     * 刷新视图
     */
    public void refresh() {
        invalidate();
    }

    /**
     * 设置原点并刷新视图
     *
     * @param originalXScale 原点坐标x的位置，范围0.0f~1.0f，例如0.0f为最左端
     * @param originalYScale 原点坐标y的位置，范围0.0f~1.0f，例如0.0f为最下端
     */
    public void refresh(float originalXScale, float originalYScale) {
        pOriginal.set(pMin.x + cWidth * originalXScale,
                pMax.y - cHeight * originalYScale);
        invalidate();
    }
}
