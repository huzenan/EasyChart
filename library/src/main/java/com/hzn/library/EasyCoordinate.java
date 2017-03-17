package com.hzn.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.hzn.library.line.EasyGraphLine;
import com.hzn.library.line.EasyLinePoint;

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
    private EasyPoint cPOriginal;
    // x轴方向上的缩放比例
    private float factorX;
    // y轴方向上的缩放比例
    private float factorY;

    private ArrayList<EasyPoint> coordinatePointList;
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
        cPOriginal = new EasyPoint();

        cPaint = new Paint();
        cPaint.setAntiAlias(true);
        cPaint.setStyle(Paint.Style.STROKE);
        cPaint.setColor(axisColor);
        cPaint.setStrokeWidth(axisWidth);

        coordinatePointList = new ArrayList<>();

        // TODO 测试
        ArrayList<EasyPoint> pointList = new ArrayList<>();
        pointList.add(new EasyLinePoint(100.25f, 120.4f, Color.BLUE, 5, 10, Color.RED, 6, EasyLinePoint.POINT_TYPE_FILL));
        pointList.add(new EasyLinePoint(178.78f, 250.0f));
        pointList.add(new EasyLinePoint(55.55f, 300.2f));
        pointList.add(new EasyLinePoint(111.5f, 320.5f));
        setDataList(pointList, new EasyGraphLine(), true);

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
        resetCoordinate(pMin.x + cWidth * originalXScale,
                pMax.y - cHeight * originalYScale,
                pMin.x, pMin.y, pMax.x, pMax.y);

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
        cPOriginal.set(oX, oY);
        factorX = cWidth / (cPMax.x - cPMin.x);
        factorY = cHeight / (cPMax.y - cPMin.y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(pMin.x, cPOriginal.y, pMax.x, cPOriginal.y, cPaint);
        canvas.drawLine(cPOriginal.x, pMin.y, cPOriginal.x, pMax.y, cPaint);
        drawGraph(canvas);
    }

    // 绘制图形内容
    private void drawGraph(Canvas canvas) {
        this.graph.drawGraph(coordinatePointList, cPOriginal, factorX, factorY, canvas);
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
        cPOriginal.set(pMin.x + cWidth * originalXScale,
                pMax.y - cHeight * originalYScale);
        invalidate();
    }
}
