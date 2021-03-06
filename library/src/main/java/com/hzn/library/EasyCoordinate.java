package com.hzn.library;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    // 是否显示网格，默认true
    private boolean gridShow;
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
    // 是否在坐标轴移出显示区域时，显示箭头，默认绘制
    private boolean drawArrow;
    // 是否为第一象限模式，为true时将只显示第一象限，默认false
    private boolean firstQuartileMode;
    // 移动模式，分别为X，Y，XY和none，默认为XY
    private int moveMode;
    // 缩放模式，分别为X，Y，XY和none，默认为XY
    private int scaleMode;

    /**
     * x方向模式
     */
    public static final int MODE_X = 0;
    /**
     * y方向模式
     */
    public static final int MODE_Y = 1;
    /**
     * xy方向模式
     */
    public static final int MODE_XY = 2;
    /**
     * 锁定模式
     */
    public static final int MODE_NONE = 3;

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
    // 触摸开始滑动的最小距离
    private float scaledTouchSlop;

    // Key=name, Value=EasyCoordinateEntity
    private HashMap<String, EasyCoordinateEntity> coordinateMap;
    // Key=name, Value=EasyAnimationEntity
    private HashMap<String, EasyAnimationEntity> animationMap;

    // 动画执行默认时长，单位毫秒
    private static final long DEFAULT_ANIM_DURATION = 500;

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
        gridShow = a.getBoolean(R.styleable.EasyCoordinate_ecGridShow, true);
        minFactorX = a.getFloat(R.styleable.EasyCoordinate_ecMinFactorX, 0.5f);
        maxFactorX = a.getFloat(R.styleable.EasyCoordinate_ecMaxFactorX, 2.0f);
        minFactorY = a.getFloat(R.styleable.EasyCoordinate_ecMinFactorY, 0.5f);
        maxFactorY = a.getFloat(R.styleable.EasyCoordinate_ecMaxFactorY, 2.0f);
        bgColor = a.getInt(R.styleable.EasyCoordinate_ecBackgroundColor, 0);
        drawArrow = a.getBoolean(R.styleable.EasyCoordinate_ecDrawArrow, true);
        firstQuartileMode = a.getBoolean(R.styleable.EasyCoordinate_ecFirstQuartileMode, false);
        moveMode = a.getInteger(R.styleable.EasyCoordinate_ecMoveMode, MODE_XY);
        scaleMode = a.getInteger(R.styleable.EasyCoordinate_ecScaleMode, MODE_XY);
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

        scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop() / 2.0f;

        coordinateMap = new HashMap<>();
        animationMap = new HashMap<>();
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
        Set<Map.Entry<String, EasyCoordinateEntity>> entries = coordinateMap.entrySet();
        for (Map.Entry<String, EasyCoordinateEntity> entry : entries) {
            EasyCoordinateEntity entity = entry.getValue();
            if (null == entity || entity.coordinatePointList.size() == 0)
                continue;

            entity.rawPointList.clear();

            EasyPoint point;
            int size = entity.coordinatePointList.size();
            for (int i = 0; i < size; i++) {
                point = entity.coordinatePointList.get(i);
                entity.rawPointList.add(new EasyPoint(pOriginal.x + point.x * factorX, pOriginal.y - point.y * factorY));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 裁剪绘制范围
        canvas.clipRect(pMin.x, pMin.y, pMax.x, pMax.y);

        // 绘制背景
        if (bgColor != 0)
            canvas.drawRect(pMin.x, pMin.y, pMax.x, pMax.y, cBgPaint);
        // 网格
        if (gridColor != 0 && gridShow)
            drawGrid(canvas);
        // x轴
        if (pOriginal.y > pMin.y && pOriginal.y < pMax.y) {
            float min = !firstQuartileMode ? pMin.x :
                    pOriginal.x < pMin.x ? pMin.x :
                            pOriginal.x > pMax.x ? pMax.x : pOriginal.x;
            float max = pMax.x;
            canvas.drawLine(min, pOriginal.y, max, pOriginal.y, cPaint);
        }
        // y轴
        if (pOriginal.x > pMin.x && pOriginal.x < pMax.x) {
            float min = pMin.y;
            float max = !firstQuartileMode ? pMax.y :
                    pOriginal.y < pMin.y ? pMin.y :
                            pOriginal.y > pMax.y ? pMax.y : pOriginal.y;
            canvas.drawLine(pOriginal.x, min, pOriginal.x, max, cPaint);
        }
        // 图形
        if (coordinateMap.size() > 0) {
            Set<Map.Entry<String, EasyCoordinateEntity>> entries = coordinateMap.entrySet();
            for (Map.Entry<String, EasyCoordinateEntity> entry : entries) {
                EasyCoordinateEntity entity = entry.getValue();
                if (null != entity.coordinatePointList && entity.coordinatePointList.size() > 0) {
                    EasyAnimationEntity animationEntity = animationMap.get(entry.getKey());
                    if (null != animationEntity && animationEntity.isAnimationEnabled)
                        entity.graph.draw(entity.coordinatePointList, entity.rawPointList, pOriginal,
                                pMin, pMax, axisWidth, factorX, factorY, animationEntity.animatorValue, canvas);
                    else
                        entity.graph.draw(entity.coordinatePointList, entity.rawPointList, pOriginal,
                                pMin, pMax, axisWidth, factorX, factorY, 1.0f, canvas);
                }
            }
        }
        // 指向原点的箭头
        if (drawArrow)
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
        int start = !firstQuartileMode ? (int) (cPMin.x / gridUnitX) - 1 : 0;
        int end = (int) (cPMax.x / gridUnitX) + 1;
        float min = pMin.y;
        float max = !firstQuartileMode ? pMax.y :
                pOriginal.y < pMin.y ? pMin.y :
                        pOriginal.y > pMax.y ? pMax.y : pOriginal.y;
        float raw;
        for (int x = start; x <= end; x++) {
            raw = pOriginal.x + gridUnitX * factorX * x;
            canvas.drawLine(raw, min, raw, max, cGridPaint);
        }

        // 横向
        start = firstQuartileMode ? 0 : (int) (cPMin.y / gridUnitY) - 1;
        end = (int) (cPMax.y / gridUnitY) + 1;
        min = !firstQuartileMode ? pMin.x :
                pOriginal.x < pMin.x ? pMin.x :
                        pOriginal.x > pMax.x ? pMax.x : pOriginal.x;
        max = pMax.x;
        for (int y = start; y <= end; y++) {
            raw = pOriginal.y - gridUnitY * factorY * y;
            canvas.drawLine(min, raw, max, raw, cGridPaint);
        }
    }

    private Map<Integer, EasyPoint> pointList = new HashMap<>();
    private Map<Integer, EasyPoint> downPointList = new HashMap<>();
    private EasyPoint cPMinDown = new EasyPoint();
    private EasyPoint cPMaxDown = new EasyPoint();
    private boolean isMoving;
    private boolean isScaling;

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
                    isScaling = false;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (moveMode == MODE_NONE && scaleMode == MODE_NONE)
                        break;

                    if (event.getPointerCount() == 1) { // 单指操作
                        EasyPoint point = pointList.get(pointerId);
                        float x = event.getX(pointerIndex);
                        float y = event.getY(pointerIndex);
                        float deltaX = x - point.x;
                        float deltaY = y - point.y;

                        if (!isMoving && (Math.abs(deltaX) > scaledTouchSlop || Math.abs(deltaY) > scaledTouchSlop))
                            isMoving = true;
                        if (!isScaling && isMoving)
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
                        isScaling = true;
                    }
                    refresh();
                    break;

                case MotionEvent.ACTION_UP:
                    boolean needRefresh = false;
                    Set<Map.Entry<String, EasyCoordinateEntity>> entries = coordinateMap.entrySet();
                    for (Map.Entry<String, EasyCoordinateEntity> entry : entries) {
                        EasyCoordinateEntity entity = entry.getValue();
                        if (null != entity.graph && !isMoving && !isScaling) {
                            entity.graph.onClick(entity.coordinatePointList, entity.rawPointList,
                                    event.getX(), event.getY(), pOriginal, factorX, factorY);
                            needRefresh = true;
                        }
                    }
                    if (needRefresh)
                        refresh();
                    isMoving = false;
                case MotionEvent.ACTION_CANCEL:
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
        float oDeltaX = moveMode == MODE_Y || moveMode == MODE_NONE ? 0.0f : deltaX;
        float oDeltaY = moveMode == MODE_X || moveMode == MODE_NONE ? 0.0f : deltaY;
        float fDeltaX = moveMode == MODE_Y || moveMode == MODE_NONE ? 0.0f : deltaX / factorX;
        float fDeltaY = moveMode == MODE_X || moveMode == MODE_NONE ? 0.0f : deltaY / factorY;
        resetCoordinate(pOriginal.x + oDeltaX, pOriginal.y + oDeltaY,
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
        float deltaMinX = scaleMode == MODE_Y || scaleMode == MODE_NONE ?
                0.0f : (-cPMin.x / (cPMax.x - cPMin.x)) * deltaDistanceX;
        float deltaMaxX = scaleMode == MODE_Y || scaleMode == MODE_NONE ?
                0.0f : deltaDistanceX - deltaMinX;
        float deltaMinY = scaleMode == MODE_X || scaleMode == MODE_NONE ?
                0.0f : (-cPMin.y / (cPMax.y - cPMin.y)) * deltaDistanceY;
        float deltaMaxY = scaleMode == MODE_X || scaleMode == MODE_NONE ?
                0.0f : deltaDistanceY - deltaMinY;
        resetCoordinate(pOriginal.x, pOriginal.y,
                cPMinDown.x + deltaMinX, cPMinDown.y + deltaMinY,
                cPMaxDown.x - deltaMaxX, cPMaxDown.y - deltaMaxY);
    }

    /**
     * 设置数据集合
     *
     * @param coordinateMap 数据集合
     */
    public void setDataList(HashMap<String, EasyCoordinateEntity> coordinateMap) {
        if (null == coordinateMap || coordinateMap.size() <= 0)
            return;

        Set<Map.Entry<String, EasyCoordinateEntity>> entries = coordinateMap.entrySet();
        for (Map.Entry<String, EasyCoordinateEntity> entry : entries) {
            EasyCoordinateEntity entity = entry.getValue();
            if (null == entity || entity.coordinatePointList.size() == 0)
                continue;

            sortPointByX(entity.coordinatePointList);
        }

        this.coordinateMap.putAll(coordinateMap);

        refresh();
    }

    /**
     * 设置数据
     *
     * @param name             String类型的名称，用于标记该图形
     * @param coordinateEntity EasyCoordinateEntity，包括点数据集及图形
     */
    public void setData(String name, EasyCoordinateEntity coordinateEntity) {
        if (null == coordinateEntity || null == coordinateEntity.coordinatePointList ||
                coordinateEntity.coordinatePointList.size() <= 0)
            return;

        sortPointByX(coordinateEntity.coordinatePointList);
        coordinateMap.put(name, coordinateEntity);

        refresh();
    }

    /**
     * 为指定图形添加点数据集，不删除旧的点数据集；当键值name没有对应的图形时，将只存储点数据集，此时graph为空，
     * 需要手动调用{@link EasyCoordinate#setGraph(String, EasyGraph)}方法设置图形。
     *
     * @param name      String类型的名称，用于标记该图形
     * @param pointList 要添加的点数据集
     */
    public void addData(String name, ArrayList<EasyPoint> pointList) {
        if (null == name || null == pointList || pointList.size() <= 0)
            return;

        sortPointByX(pointList);

        EasyCoordinateEntity entity = coordinateMap.get(name);
        if (null == entity)
            coordinateMap.put(name, new EasyCoordinateEntity(pointList, null));
        else
            entity.coordinatePointList.addAll(pointList);

        refresh();
    }

    /**
     * 删除某个图形，将删除该图形的动画对象，若只希望清除数据点，
     * 可以使用{@link EasyCoordinate#clearData(String)}方法进行清除
     *
     * @param name String类型的名称，用于标记该图形
     */
    public void removeData(String name) {
        if (null == name)
            return;

        coordinateMap.remove(name);
        animationMap.remove(name);

        refresh();
    }

    /**
     * 清除某个图形的点数据集，不删除该图形
     *
     * @param name String类型的名称，用于标记该图形
     */
    public void clearData(String name) {
        if (null == name)
            return;

        EasyCoordinateEntity entity = coordinateMap.get(name);
        if (null != entity) {
            entity.coordinatePointList.clear();
            entity.rawPointList.clear();
        }

        refresh();
    }

    /**
     * 设置一个图形，可用于切换某个点数据集的图形
     *
     * @param name  String类型的名称，用于标记该图形
     * @param graph 图形
     */
    public void setGraph(String name, EasyGraph graph) {
        if (null == name)
            return;

        EasyCoordinateEntity entity = coordinateMap.get(name);
        if (null != entity)
            entity.graph = graph;

        refresh();
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

    /**
     * 初始化动画效果
     *
     * @param name         String类型的名称，用于标记需要初始化动画的图形
     * @param enabled      是否开启动画效果
     * @param animDuration 动画执行时长
     */
    public void initAnimation(String name, boolean enabled, long animDuration) {
        if (null == name)
            return;

        final EasyAnimationEntity animationEntity = new EasyAnimationEntity();
        animationEntity.isAnimationEnabled = enabled;
        animationEntity.animDuration = animDuration;
        animationEntity.animUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animationEntity.animatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        };
        animationMap.put(name, animationEntity);
    }

    /**
     * 初始化动画效果，使用默认动画执行时长
     *
     * @param name    String类型的名称，用于标记需要初始化动画的图形
     * @param enabled 是否开启动画效果
     */
    public void initAnimation(String name, boolean enabled) {
        initAnimation(name, enabled, DEFAULT_ANIM_DURATION);
    }

    /**
     * 初始化动画效果，默认关闭动画效果，可通过{@link EasyCoordinate#setAnimationEnabled(String, boolean)}方法开启动画效果
     *
     * @param name String类型的名称，用于标记需要初始化动画的图形
     */
    public void initAnimation(String name) {
        initAnimation(name, false, DEFAULT_ANIM_DURATION);
    }

    /**
     * 设置是否开启动画效果
     *
     * @param name    String类型的名称，用于标记需要播放动画的图形
     * @param enabled 是否开启动画效果
     */
    public void setAnimationEnabled(String name, boolean enabled) {
        if (null == name)
            return;

        EasyAnimationEntity animationEntity = animationMap.get(name);
        if (null == animationEntity || !animationEntity.isAnimationEnabled)
            return;

        animationEntity.isAnimationEnabled = enabled;
    }

    /**
     * 开始播放动画
     *
     * @param name String类型的名称，用于标记需要播放动画的图形
     */
    public void startAnimation(String name) {
        if (null == name)
            return;

        EasyAnimationEntity animationEntity = animationMap.get(name);
        if (null == animationEntity || !animationEntity.isAnimationEnabled)
            return;

        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.addUpdateListener(animationEntity.animUpdateListener);
        animator.setDuration(animationEntity.animDuration).start();
    }

    /**
     * 根据x值对坐标点数据集进行升序排序
     *
     * @param pointList 坐标点数据集
     */
    private void sortPointByX(List<EasyPoint> pointList) {
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
    private void sortPointByY(List<EasyPoint> pointList) {
        Collections.sort(pointList, new Comparator<Object>() {
            @Override
            public int compare(Object lhs, Object rhs) {
                EasyPoint l = (EasyPoint) lhs;
                EasyPoint r = (EasyPoint) rhs;
                return l.y > r.y ? 1 : l.y == r.y ? 0 : -1;
            }
        });
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

    private static class EasyAnimationEntity {
        // 是否开启动画效果
        public boolean isAnimationEnabled;
        // 动画执行时长
        public long animDuration;
        // 动画更新监听
        public ValueAnimator.AnimatorUpdateListener animUpdateListener;
        // 动画执行进度
        public float animatorValue;
    }

    /**
     * 坐标系坐标实体类
     */
    public static class EasyCoordinateEntity {
        // 原始坐标数据集，由用户设置
        private ArrayList<EasyPoint> coordinatePointList;
        // 屏幕坐标数据集，由EasyCoordinate进行计算
        private ArrayList<EasyPoint> rawPointList;
        // 图形
        private EasyGraph graph;

        /**
         * 初始化一个坐标实体对象
         *
         * @param coordinatePointList 坐标点数据集
         * @param graph               图形
         */
        public EasyCoordinateEntity(ArrayList<EasyPoint> coordinatePointList, EasyGraph graph) {
            this.coordinatePointList = coordinatePointList;
            this.rawPointList = new ArrayList<>();
            this.graph = graph;
        }

        public ArrayList<EasyPoint> getCoordinatePointList() {
            return coordinatePointList == null ? new ArrayList<EasyPoint>() : coordinatePointList;
        }

        public void setCoordinatePointList(ArrayList<EasyPoint> coordinatePointList) {
            this.coordinatePointList = coordinatePointList;
        }

        public EasyGraph getGraph() {
            return graph;
        }

        public void setGraph(EasyGraph graph) {
            this.graph = graph;
        }
    }
}
