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
import java.util.HashMap;
import java.util.Map;

/**
 * 坐标系
 * Created by huzn on 2017/3/16.
 */
public class EasyCoordinate extends View {

    // 原点坐标x的位置，范围0.0f~1.0f，例如0.0f为最左端，默认0.5f
    private float originalXPercent;
    // 原点坐标y的位置，范围0.0f~1.0f，例如0.0f为最下端，默认0.5f
    private float originalYPercent;
    // 坐标轴的颜色，默认Color.BLACK
    private int axisColor;
    // 坐标轴的线段宽度，单位dp，默认2dp
    private int axisWidth;
    // 网格的颜色，默认不绘制网格
    private int gridColor;
    // 网格的线段宽度，单位dp，默认1dp
    private int gridWidth;
    // x方向上，网格绘制的单位标准，默认100个坐标单位
    private int gridUnitX;
    // y方向上，网格绘制的单位标准，默认100个坐标单位
    private int gridUnitY;
    // x轴方向上的缩放比例的最小值，默认0.5f
    private float minFactorX;
    // x轴方向上的缩放比例的最大值，默认2.0f
    private float maxFactorX;
    // y轴方向上的缩放比例的最小值，默认0.5f
    private float minFactorY;
    // y轴方向上的缩放比例的最大值，默认2.0f
    private float maxFactorY;
    // 背景颜色，默认不绘制背景颜色
    private int bgColor;

    private Paint cPaint;
    private Paint cGridPaint;
    private Paint cBgPaint;

    // 测量宽
    private int width;
    // 测量高
    private int height;
    // 坐标系实际绘制宽度
    private float cWidth;
    // 坐标系实际绘制高度
    private float cHeight;
    // 左上角点的坐标，用于固定绘制边界
    private EasyPoint pMin;
    // 左下角点的坐标
    private EasyPoint cPMin;
    // 右下角点的坐标，用于固定绘制边界
    private EasyPoint pMax;
    // 右上角点的坐标
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
        originalXPercent = a.getFloat(R.styleable.EasyCoordinate_ecOriginalXPercent, 0.5f);
        originalYPercent = a.getFloat(R.styleable.EasyCoordinate_ecOriginalYPercent, 0.5f);
        axisColor = a.getColor(R.styleable.EasyCoordinate_ecAxisColor, Color.BLACK);
        axisWidth = a.getDimensionPixelOffset(R.styleable.EasyCoordinate_ecAxisWidth, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2.0f, getResources().getDisplayMetrics()));
        gridColor = a.getColor(R.styleable.EasyCoordinate_ecGridColor, 0);
        gridWidth = a.getDimensionPixelOffset(R.styleable.EasyCoordinate_ecGridWidth, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1.0f, getResources().getDisplayMetrics()));
        gridUnitX = a.getInteger(R.styleable.EasyCoordinate_ecGridUnitX, 100);
        gridUnitY = a.getInteger(R.styleable.EasyCoordinate_ecGridUnitY, 100);
        minFactorX = a.getFloat(R.styleable.EasyCoordinate_ecMinFactorX, 0.5f);
        maxFactorX = a.getFloat(R.styleable.EasyCoordinate_ecMaxFactorX, 2.0f);
        minFactorY = a.getFloat(R.styleable.EasyCoordinate_ecMinFactorY, 0.5f);
        maxFactorY = a.getFloat(R.styleable.EasyCoordinate_ecMaxFactorY, 2.0f);
        bgColor = a.getInt(R.styleable.EasyCoordinate_ecBackgroundColor, 0);
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
        cGridPaint = new Paint();
        cGridPaint.setAntiAlias(true);
        cGridPaint.setStyle(Paint.Style.STROKE);
        cGridPaint.setColor(gridColor);
        cGridPaint.setStrokeWidth(gridWidth);
        cBgPaint = new Paint();
        cBgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        cBgPaint.setColor(bgColor);

        coordinatePointList = new ArrayList<>();
        rawPointList = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        if (mode != MeasureSpec.EXACTLY) { // wrap_content
            width = width + getPaddingLeft() + getPaddingRight();
        }

        mode = MeasureSpec.getMode(heightMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        if (mode != MeasureSpec.EXACTLY) { // wrap_content
            height = height + getPaddingTop() + getPaddingBottom();
        }

        cWidth = width - getPaddingLeft() - getPaddingRight();
        cHeight = height - getPaddingTop() - getPaddingBottom();

        // 固定绘制边界
        pMin.set(getPaddingLeft(), getPaddingTop());
        pMax.set(width - getPaddingRight(), height - getPaddingBottom());

        // 第一次初始化坐标系
        float ox = pMin.x + cWidth * originalXPercent;
        float oy = pMax.y - cHeight * originalYPercent;
        resetCoordinate(ox, oy, -(ox - pMin.x), -(pMax.y - oy), pMax.x - ox, oy - pMin.y);

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
        float tFactorX = cWidth / (cMaxX - cMinX);
        float tFactorY = cHeight / (cMaxY - cMinY);
        if (tFactorX >= minFactorX && tFactorX <= maxFactorX) {
            cPMin.x = cMinX;
            cPMax.x = cMaxX;
            pOriginal.x = oX;
            factorX = tFactorX;
        }
        if (tFactorY >= minFactorY && tFactorY <= maxFactorY) {
            cPMin.y = cMinY;
            cPMax.y = cMaxY;
            pOriginal.y = oY;
            factorY = tFactorY;
        }
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
        // 固定绘制范围
        canvas.clipRect(pMin.x, pMin.y, pMax.x, pMax.y);
        // 绘制背景
        if (bgColor != 0)
            canvas.drawRect(pMin.x, pMin.y, pMax.x, pMax.y, cBgPaint);
        // 网格
        if (gridColor != 0)
            drawGrid(canvas);
        // x轴
        if (pOriginal.y > pMin.y && pOriginal.y < pMax.y)
            canvas.drawLine(pMin.x, pOriginal.y, pMax.x, pOriginal.y, cPaint);
        // y轴
        if (pOriginal.x > pMin.x && pOriginal.x < pMax.x)
            canvas.drawLine(pOriginal.x, pMin.y, pOriginal.x, pMax.y, cPaint);
        // 图形
        if (null != graph)
            this.graph.drawGraph(rawPointList, pOriginal, pMin, pMax, canvas);
        // 指向原点的箭头
        drawOriginalArrow(canvas);
    }

    private void drawOriginalArrow(Canvas canvas) {
        float startX, startY, endX, endY;
        // 左上
        if ((pOriginal.x < pMin.x || pOriginal.y < pMin.y) &&
                (pOriginal.x < width / 2.0f && pOriginal.y < height / 2.0f)) {
            startX = pOriginal.x < pMin.x ? pMin.x + 10.0f : pOriginal.x + 10.0f;
            startY = pOriginal.y < pMin.y ? pMin.y + 10.0f : pOriginal.y + 10.0f;
            endX = startX + 50.0f;
            endY = startY + 50.0f;
            drawArrow(canvas, startX, startY, endX, endY);
        }
        // 左下
        if ((pOriginal.x < pMin.x || pOriginal.y > pMax.y) &&
                (pOriginal.x < width / 2.0f && pOriginal.y > height / 2.0f)) {
            startX = pOriginal.x < pMin.x ? pMin.x + 10.0f : pOriginal.x + 10.0f;
            startY = pOriginal.y > pMax.y ? pMax.y - 10.0f : pOriginal.y - 10.0f;
            endX = startX + 50.0f;
            endY = startY - 50.0f;
            drawArrow(canvas, startX, startY, endX, endY);
        }
        // 右上
        if ((pOriginal.x > pMax.x || pOriginal.y < pMin.y) &&
                (pOriginal.x > width / 2.0f && pOriginal.y < height / 2.0f)) {
            startX = pOriginal.x > pMax.x ? pMax.x - 10.0f : pOriginal.x - 10.0f;
            startY = pOriginal.y < pMin.y ? pMin.y + 10.0f : pOriginal.y + 10.0f;
            endX = startX - 50.0f;
            endY = startY + 50.0f;
            drawArrow(canvas, startX, startY, endX, endY);
        }
        // 右下
        if ((pOriginal.x > pMax.x || pOriginal.y > pMax.y) &&
                (pOriginal.x > width / 2.0f && pOriginal.y > height / 2.0f)) {
            startX = pOriginal.x > pMax.x ? pMax.x - 10.0f : pOriginal.x - 10.0f;
            startY = pOriginal.y > pMax.y ? pMax.y - 10.0f : pOriginal.y - 10.0f;
            endX = startX - 50.0f;
            endY = startY - 50.0f;
            drawArrow(canvas, startX, startY, endX, endY);
        }
    }

    private void drawArrow(Canvas canvas, float startX, float startY, float endX, float endY) {
        canvas.drawLine(startX, startY, endX, endY, cPaint);
        canvas.drawLine(startX, startY, endX, startY, cPaint);
        canvas.drawLine(startX, startY, startX, endY, cPaint);
    }

    // 绘制网格
    private void drawGrid(Canvas canvas) {
        // 纵向
        int start = (int) (cPMin.x / gridUnitX) - 1;
        int end = (int) (cPMax.x / gridUnitX) + 1;
        float raw;
        for (int x = start; x <= end; x++) {
            raw = pOriginal.x + gridUnitX * factorX * x;
            canvas.drawLine(raw, pMin.y, raw, pMax.y, cGridPaint);
        }

        // 横向
        start = (int) (cPMin.y / gridUnitY) - 1;
        end = (int) (cPMax.y / gridUnitY) + 1;
        for (int y = start; y <= end; y++) {
            raw = pOriginal.y - gridUnitY * factorY * y;
            canvas.drawLine(pMin.x, raw, pMax.x, raw, cGridPaint);
        }
    }

    private Map<Integer, EasyPoint> pointList = new HashMap<>();
    private Map<Integer, EasyPoint> downPointList = new HashMap<>();
    private EasyPoint cPMinDown = new EasyPoint();
    private EasyPoint cPMaxDown = new EasyPoint();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int actionIndex = event.getActionIndex();
        int pointerId = event.getPointerId(actionIndex);
        int pointerIndex = event.findPointerIndex(pointerId);

        if (pointerIndex != -1) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    pointList.put(pointerId, new EasyPoint(
                            event.getX(pointerIndex), event.getY(pointerIndex)));
                    downPointList.put(pointerId, new EasyPoint(
                            event.getX(pointerIndex), event.getY(pointerIndex)));
                    cPMinDown.set(cPMin.x, cPMin.y);
                    cPMaxDown.set(cPMax.x, cPMax.y);
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (event.getPointerCount() == 1) { // 单指操作
                        EasyPoint point = pointList.get(pointerId);
                        float x = event.getX(pointerIndex);
                        float y = event.getY(pointerIndex);
                        float deltaX = x - point.x;
                        float deltaY = y - point.y;
                        moveBy(deltaX, deltaY);
                        point.set(x, y);
                    } else { // 多指操作
                        // 得到最长的两条矢量
                        float maxDeltaSqr1 = -1.0f;
                        float maxDeltaSqr2 = -1.0f;
                        EasyVector maxVector1 = new EasyVector();
                        EasyVector maxVector2 = new EasyVector();
                        for (int i = 0; i < event.getPointerCount(); i++) {
                            int movePointerId = event.getPointerId(i);
                            int movePointerIndex = event.findPointerIndex(movePointerId);
                            EasyPoint point = downPointList.get(movePointerId);
                            float x = event.getX(movePointerIndex);
                            float y = event.getY(movePointerIndex);
                            float deltaX = x - point.x;
                            float deltaY = y - point.y;
                            float deltaSqr = deltaX * deltaX + deltaY * deltaY;

                            if (maxDeltaSqr2 < deltaSqr) {
                                if (maxDeltaSqr1 < deltaSqr) {
                                    maxDeltaSqr2 = maxDeltaSqr1;
                                    maxDeltaSqr1 = deltaSqr;
                                    maxVector2.set(maxVector1);
                                    maxVector1.set(point.x, point.y, x, y);
                                } else {
                                    maxDeltaSqr2 = deltaSqr;
                                    maxVector2.set(point.x, point.y, x, y);
                                }
                            }
                        }

                        // 通过矢量的距离计算出x方向和y方向各需要缩放的量
                        float endDistance = Math.abs(maxVector1.endX - maxVector2.endX);
                        float startDistance = Math.abs(maxVector1.startX - maxVector2.startX);
                        float deltaDistanceX = endDistance - startDistance;

                        endDistance = Math.abs(maxVector1.endY - maxVector2.endY);
                        startDistance = Math.abs(maxVector1.startY - maxVector2.startY);
                        float deltaDistanceY = endDistance - startDistance;

                        scaleBy(deltaDistanceX, deltaDistanceY);
                    }
                    refresh();
                    break;

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    pointList.remove(pointerId);
                    downPointList.remove(pointerId);
                    break;

                default:
                    break;
            }
        }
        return true;
    }

    /**
     * 移动整个坐标系
     *
     * @param deltaX x方向的偏移量
     * @param deltaY y方向的偏移量
     */
    public void moveBy(float deltaX, float deltaY) {
        float fDeltaX = deltaX / factorX;
        float fDeltaY = deltaY / factorY;
        resetCoordinate(pOriginal.x + deltaX, pOriginal.y + deltaY,
                cPMin.x - fDeltaX, cPMin.y + fDeltaY,
                cPMax.x - fDeltaX, cPMax.y + fDeltaY);
    }

    /**
     * 缩放整个坐标系
     *
     * @param deltaDistanceX x方向的缩放距离
     * @param deltaDistanceY y方向的缩放距离
     */
    public void scaleBy(float deltaDistanceX, float deltaDistanceY) {
        float deltaMinX = (-cPMin.x / (cPMax.x - cPMin.x)) * deltaDistanceX;
        float deltaMaxX = deltaDistanceX - deltaMinX;
        float deltaMinY = (-cPMin.y / (cPMax.y - cPMin.y)) * deltaDistanceY;
        float deltaMaxY = deltaDistanceY - deltaMinY;
        resetCoordinate(pOriginal.x, pOriginal.y,
                cPMinDown.x + deltaMinX, cPMinDown.y + deltaMinY,
                cPMaxDown.x - deltaMaxX, cPMaxDown.y - deltaMaxY);
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

        refresh();
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
        coordinateToRaw();
        invalidate();
    }

    /**
     * 设置原点并刷新视图
     *
     * @param originalXPercent 原点坐标x的位置，范围0.0f~1.0f，例如0.0f为最左端
     * @param originalYPercent 原点坐标y的位置，范围0.0f~1.0f，例如0.0f为最下端
     */
    public void refresh(float originalXPercent, float originalYPercent) {
        pOriginal.set(pMin.x + cWidth * originalXPercent,
                pMax.y - cHeight * originalYPercent);
        refresh();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        refresh();
    }

    private static class EasyVector {
        public float startX;
        public float startY;
        public float endX;
        public float endY;

        public void set(float startX, float startY, float endX, float endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }

        public void set(EasyVector vector) {
            this.startX = vector.startX;
            this.startY = vector.startY;
            this.endX = vector.endX;
            this.endY = vector.endY;
        }
    }
}
