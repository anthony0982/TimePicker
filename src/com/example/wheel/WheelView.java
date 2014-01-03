package com.example.wheel;

import java.util.LinkedList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

import com.example.scroll.ScrollableView;
import com.example.scroll.Step;
import com.example.timepicker.R;
import com.example.util.WindowUtil;
import com.example.wheel.adapter.WheelAdapter;

/**
 * 滚轮
 * 
 * @author wangzengyang@gmail.com
 * @since 2013-12-26
 */
public class WheelView extends ScrollableView implements OnClickListener {
    private float lineSplitHeight;
    private Paint textPaintFirst;
    private Paint textPaintSecond;
    private Paint textPaintThird;
    private Paint linePaint;
    private float textBaseY;
    private float secondRectTop;
    private float secondRectBottom;
    private float thirdRectTop;
    private float thirdRectBottom;
    private float textSize;
    private float itemHeight;
    private int textGravity;
    private WheelAdapter adapter;

    private float contentHeight;

    private float lastEventY;

    private float scrollY;

    private VelocityTracker velocityTracker;

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.wheel);
        int textColorFirst = a.getColor(R.styleable.wheel_textColorFirst, -1);
        int textColorSecond = a.getColor(R.styleable.wheel_textColorSecond, -1);
        int textColorThird = a.getColor(R.styleable.wheel_textColorThird, -1);
        this.textSize = a.getDimension(R.styleable.wheel_textSize, -1);
        this.textSize = WindowUtil.computeScaledSize(textSize);
        int lineColor = a.getColor(R.styleable.wheel_splitLineColor, -1);
        this.lineSplitHeight = a.getDimension(R.styleable.wheel_lineSplitHeight, -1);
        this.lineSplitHeight = WindowUtil.computeScaledSize(this.lineSplitHeight);
        this.itemHeight = this.textSize + this.lineSplitHeight;
        this.textGravity = a.getInt(R.styleable.wheel_textGravity, -1);
        a.recycle();

        this.textPaintFirst = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        this.textPaintFirst.setTextSize(textSize);
        this.textPaintFirst.setColor(textColorFirst);
        this.textPaintSecond = new TextPaint(textPaintFirst);
        this.textPaintThird = new TextPaint(textPaintFirst);
        this.textPaintSecond.setColor(textColorSecond);
        this.textPaintThird.setColor(textColorThird);

        this.linePaint = new Paint();
        this.linePaint.setColor(lineColor);
    }

    @Override
    protected void onInit() {
        super.onInit();
        setInterpolator(new DecelerateInterpolator());
        setOnClickListener(this);
        velocityTracker = VelocityTracker.obtain();
    }

    public void setAdapter(WheelAdapter adapter) {
        this.adapter = adapter;
        this.render();
        this.adapter.setWheelView(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.adapter == null || this.adapter.getCount() <= 0)
            return;
        drawSplitLine(canvas);
        canvas.save();
        drawText(canvas, this.textPaintFirst);
        canvas.restore();
        canvas.save();
        Rect secondRect = new Rect(0, (int) this.secondRectTop, getMeasuredWidth(), (int) this.secondRectBottom);
        canvas.clipRect(secondRect);
        drawText(canvas, this.textPaintSecond);
        canvas.restore();
        canvas.save();

        Rect thirdRect = new Rect(0, (int) this.thirdRectTop, getMeasuredWidth(), (int) this.thirdRectBottom);
        canvas.clipRect(thirdRect);
        drawText(canvas, this.textPaintThird);
        canvas.restore();
    }

    /**
     * 画分割线
     * 
     * @param canvas
     */
    private void drawSplitLine(Canvas canvas) {
        canvas.drawLine(0, this.thirdRectTop, getMeasuredWidth(), this.thirdRectTop, linePaint);
        canvas.drawLine(0, this.thirdRectBottom, getMeasuredWidth(), this.thirdRectBottom, linePaint);
    }

    /**
     * 画文本
     * 
     * @param canvas
     * @param paint
     */
    private void drawText(Canvas canvas, Paint paint) {
        float maxWidth = this.adapter.getMaxWidth(paint);
        int count = this.adapter.getCount();
        int px = getMeasuredWidth();
        float textWidth = 0f;
        float x = 0f;
        float y = 0f;
        String text = "";
        for (int index = 0; index < count; index++) {
            text = this.adapter.getItem(index);
            textWidth = paint.measureText(text);
            x = computeTextX(maxWidth, px, textWidth);
            y = this.textBaseY + scrollY;
            canvas.drawText(text, x, y, paint);
            canvas.translate(0, itemHeight);
        }
    }

    private float computeTextX(float maxWidth, int px, float textWidth) {
        if (this.textGravity == 0)
            return px / 2 - textWidth / 2 + getPaddingLeft() - getPaddingRight();
        return px / 2 - maxWidth / 2 + maxWidth - textWidth + getPaddingLeft() - getPaddingRight();
    }

    public void computeTextBaseY() {
        if (this.adapter == null || this.adapter.getCount() <= 0)
            return;
        int count = this.adapter.getCount();
        this.contentHeight = count * this.textSize + (count - 1) * this.lineSplitHeight;
        this.textBaseY = -textPaintFirst.getFontMetrics().top + this.getMeasuredHeight() / 2 - this.textSize / 2;
    }

    private void computeCenterRect() {
        if (this.adapter == null || this.adapter.getCount() <= 0)
            return;
        this.thirdRectTop = getMeasuredHeight() / 2 - this.textSize / 2;
        this.thirdRectBottom = this.thirdRectTop + this.textSize + this.lineSplitHeight / 2;
        this.secondRectTop = this.thirdRectTop - this.itemHeight;
        this.secondRectBottom = this.thirdRectBottom + this.itemHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        computeTextBaseY();
        computeCenterRect();
    }

    @Override
    public void onClick(View v) {
        LinkedList<Step> stepList = new LinkedList<Step>();
        Step step1 = Step.createDistanceStep(200, 200, 2000);
        Step step2 = Step.createDistanceStep(-200, -200);
        Step step3 = Step.createDistanceStep(200, -200);
        Step step4 = Step.createDistanceStep(0, 0);
        stepList.add(step1);
        stepList.add(step2);
        stepList.add(step3);
        stepList.add(step4);
        move(stepList);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int action = event.getAction();
        velocityTracker.addMovement(event);
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            stopScroll();
            break;
        case MotionEvent.ACTION_MOVE:
            int dy = (int) (event.getY() - lastEventY);
            scrollY += dy;
            makeSureScrollVisible();
            render();
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_OUTSIDE:
            fling();
            break;
        }
        lastEventY = event.getY();
        return true;
    }

    private int getCurrentY() {
        return (int) -scrollY;
    }

    @SuppressWarnings("deprecation")
    private void fling() {
        velocityTracker.computeCurrentVelocity(1000, ViewConfiguration.getMaximumFlingVelocity());
        float velocityY = velocityTracker.getYVelocity();
        int currentY = getCurrentY();
        int duration = Step.DEFAULT_DURATION;
        int distanceY = (int) (velocityY / 1000 * duration);
        int direction = velocityY > 0 ? -1 : 1;
        int maxDistance = (int) (velocityY > 0 ? Math.abs(this.scrollY) : this.contentHeight - Math.abs(this.scrollY));
        distanceY = direction * Math.min(Math.abs(distanceY), maxDistance);
        if (Math.abs(velocityY) < ViewConfiguration.getMinimumFlingVelocity()) {
            scrollToRightPosition();
            return;
        }
        smoothScrollBy(0, currentY, 0, distanceY, duration);
        scrollToRightPositionDelayed(duration);
    }

    private void scrollToRightPositionDelayed(int duration) {
        postDelayed(new Runnable() {

            @Override
            public void run() {
                scrollToRightPosition();
            }
        }, duration);
    }

    private void scrollToRightPosition() {
        int index = getCurrentItemIndex();
        int position = (int) getItemPosition(index);
        smoothScrollTo(0, -(int) scrollY, 0, position, 250);
    }

    @Override
    public void doScrollTo(int x, int y) {
        scrollY = y;
        makeSureScrollVisible();
    }

    private void makeSureScrollVisible() {
        if (scrollY > this.lineSplitHeight)
            scrollY = this.lineSplitHeight;
        else if (scrollY < -(this.contentHeight))
            scrollY = -(this.contentHeight);
    }

    /**
     * 获取当前选择的条目索引
     * 
     * @return
     */
    public int getCurrentItemIndex() {
        int position = 0;
        if (scrollY > 0) {
            position = 0;
            return position;
        }
        position = (int) (Math.abs(scrollY) / itemHeight);
        int reminer = (int) (Math.abs(scrollY) % itemHeight);
        if (reminer > itemHeight / 2)
            position++;
        if (position > this.adapter.getCount() - 1)
            position = this.adapter.getCount() - 1;
        return position;
    }

    public String getCurrentItemString() {
        return this.adapter.getItem(getCurrentItemIndex());
    }

    public int getCurrentValue() {
        return this.adapter.getValue(getCurrentItemIndex());
    }

    /**
     * 获取指定条目在视图中的位置
     * 
     * @param itemIndex
     * @return
     */
    private float getItemPosition(int itemIndex) {
        return this.itemHeight * itemIndex;
    }

    public void select(int index) {
        this.scrollY = -getItemPosition(index);
        render();
    }

    public void setCurrentValue(int value) {
        int index = this.adapter.getValueIndex(value);
        select(index);
    }

    public void setStartValue(int value) {
        this.adapter.setStartValue(value);
        this.adapter.notifyChanged();
    }
}
