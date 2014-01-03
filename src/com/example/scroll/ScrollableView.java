package com.example.scroll;

import java.util.LinkedList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;

import com.example.base.BaseView;
import com.example.util.CollectionUtil;
import com.example.util.LogUtil;

/**
 * 可滚动视图
 * 
 * @author wangzengyang@gmail.com
 * @since 2013-12-26
 */
public class ScrollableView extends BaseView {
    private static final int MSG_SCROLL_STEP = 1;
    private static final int MSG_SCROLL_END = 2;
    /** 滚动状态 */
    protected ScrollState mScrollState;
    /** 动作步列表 */
    private LinkedList<Step> mStepList;

    private ScrollListener mScrollListener;

    private boolean touched;
    private boolean touching;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_SCROLL_STEP:
                actStep();
                break;
            case MSG_SCROLL_END:
                onScrollEnd();
                break;
            }
        };
    };

    public ScrollableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScrollableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollableView(Context context) {
        super(context);
    }

    @Override
    protected void onInit() {
        super.onInit();
        mStepList = new LinkedList<Step>();
        mScrollState = new ScrollState();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            touched = true;
            touching = true;
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_OUTSIDE:
            touching = false;
            break;
        }
        return false;
    }

    /**
     * 设置动画变化率
     * 
     * @param interpolator
     *            变化率算法对象
     */
    protected void setInterpolator(Interpolator interpolator) {
        mScrollState = new ScrollState(interpolator);
    }

    /**
     * 平滑滚动到指定坐标<br/>
     * 
     * 坐标系为：<br/>
     * 屏幕左上角为坐标原点，水平向右→为X轴正向，垂直向下↓为Y轴正向
     * 
     * @param finalX
     *            终点X轴坐标
     * @param finalY
     *            终点Y轴坐标
     * @param duration
     *            时间
     */
    public void smoothScrollTo(int finalX, int finalY, int duration) {
        LogUtil.d(getLogTag(), "smoothScrollTo distanceX : " + finalX);
        LogUtil.d(getLogTag(), "smoothScrollTo getCurrX : " + mScrollState.getCurrX());
        int dx = finalX - mScrollState.getCurrX();
        int dy = finalY - mScrollState.getCurrY();
        smoothScrollBy(dx, dy, duration);
    }

    /**
     * 平滑滚动到指定坐标<br/>
     * 
     * 坐标系为：<br/>
     * 屏幕左上角为坐标原点，水平向右→为X轴正向，垂直向下↓为Y轴正向
     * 
     * @param finalX
     *            终点X轴坐标
     * @param finalY
     *            终点Y轴坐标
     * @param duration
     *            时间
     */
    public void smoothScrollTo(int fromX, int fromY, int finalX, int finalY, int duration) {
        int dx = finalX - fromX;
        int dy = finalY - fromY;
        smoothScrollBy(fromX, fromY, dx, dy, duration);
    }

    public void smoothScrollBy(int fromX, int fromY, int distanceX, int distanceY, int duration) {
        /* 先停止当前的滚动 */
        stopScroll();
        /* 设置滚动参数 */
        mScrollState.setScrollParams(fromX, fromY, -distanceX, -distanceY, duration);
        /* 启动滚动 */
        startScroll();
    }

    /**
     * 平滑滚动到指定坐标<br/>
     * 
     * 坐标系为：<br/>
     * 屏幕左上角为坐标原点，水平向右→为X轴正向，垂直向下↓为Y轴正向
     * 
     * @param finalX
     *            终点X轴坐标
     * @param finalY
     *            终点Y轴坐标
     */
    public void smoothScrollTo(int finalX, int finalY) {
        smoothScrollTo(finalX, finalY, Step.DEFAULT_DURATION);
    }

    /**
     * 调用此方法设置滚动的相对偏移
     * 
     * 坐标系为：<br/>
     * 屏幕左上角为坐标原点，水平向右→为X轴正向，垂直向下↓为Y轴正向
     * 
     * @param distanceX
     *            distanceX>0向右移动
     * @param distanceY
     *            distanceY>0向下移动
     */
    public void smoothScrollBy(int distanceX, int distanceY) {
        /* 先停止当前的滚动 */
        stopScroll();
        /* 设置滚动参数 */
        mScrollState.setScrollParams(distanceX, distanceY, Step.DEFAULT_DURATION);
        /* 启动滚动 */
        startScroll();
    }

    /**
     * 调用此方法设置滚动的相对偏移
     * 
     * 坐标系为：<br/>
     * 屏幕左上角为坐标原点，水平向右→为X轴正向，垂直向下↓为Y轴正向
     * 
     * @param distanceX
     *            distanceX>0向右移动
     * @param distanceY
     *            distanceY>0向下移动
     * @param duration
     *            时间
     */
    public void smoothScrollBy(int distanceX, int distanceY, int duration) {
        /* 先停止当前的滚动 */
        stopScroll();
        /* 设置滚动参数 */
        mScrollState.setScrollParams(distanceX, distanceY, duration);
        /* 启动滚动 */
        startScroll();
    }

    /**
     * 开启滚动<br/>
     * 通过调用invalidate发起重绘实现，这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
     */
    private void startScroll() {
        invalidate();
    }

    /**
     * 停止滚动
     */
    public void stopScroll() {
        /* 通过标记滚动状态为停止来停止滚动计算逻辑 */
        mScrollState.setStoped();
    }

    @Override
    public void computeScroll() {
        mHandler.removeMessages(MSG_SCROLL_END);
        /* 如果已完成滚动，直接返回 */
        if (!mScrollState.shouldScroll()) {
            mHandler.sendEmptyMessageDelayed(MSG_SCROLL_END, 200);
            return;
        }
        /* 滚动一下 */
        action();
        /* 渲染滚动结果 */
        /* 必须调用该方法，否则不一定能看到滚动效果 */
        render();
    }

    private void onScrollEnd() {
        LogUtil.d(getLogTag(), "onScrollEnd");
        if (mScrollListener != null && !touching && touched) {
            LogUtil.d(getLogTag(), "onScrollEnd callback");
            mScrollListener.onScrollEnd(this);
            touched = false;
        }
    }

    /**
     * 做一个滚动动作，滚动到计算出的当前应该在的位置<br/>
     * 这里调用View的scrollTo()完成实际的滚动
     */
    private void action() {
        doScrollTo(mScrollState.getCurrX(), mScrollState.getCurrY());
    }

    /**
     * 渲染滚动结果<br/>
     * 
     * 通过调用重绘实现
     */
    protected void render() {
        postInvalidate();
    }

    public void refresh() {
        render();
    }

    /**
     * 获得当前滚动位置X坐标<br/>
     * 
     * 坐标系为：<br/>
     * 屏幕左上角为坐标原点，水平向右→为X轴正向，垂直向下↓为Y轴正向
     * 
     * @return
     */
    protected int getCurrX() {
        return mScrollState.getCurrX();
    }

    /**
     * 获得当前滚动位置Y坐标<br/>
     * 
     * 坐标系为：<br/>
     * 屏幕左上角为坐标原点，水平向右→为X轴正向，垂直向下↓为Y轴正向
     * 
     * @return
     */
    protected int getCurrY() {
        return mScrollState.getCurrY();
    }

    /**
     * 滚动到指定位置
     * 
     * 坐标系为：<br/>
     * 屏幕左上角为坐标原点，水平向右→为X轴正向，垂直向下↓为Y轴正向
     * 
     * @param x
     *            目标x轴坐标
     * @param y
     *            目标y轴坐标
     */
    public void doScrollTo(int x, int y) {
        /* 翻转坐标系 */
        super.scrollTo(-x, -y);
    }

    /**
     * 滚动指定距离
     * 
     * 坐标系为：<br/>
     * 屏幕左上角为坐标原点，水平向右→为X轴正向，垂直向下↓为Y轴正向
     * 
     * @param x
     *            x轴距离
     * @param y
     *            y轴距离
     */
    public void doScrollBy(int x, int y) {
        /* 翻转坐标系 */
        super.scrollBy(-x, -y);
    }

    /**
     * 移动指定的任意多步
     * 
     * @param stepList
     */
    public void move(LinkedList<Step> stepList) {
        stopCurrentMovement();
        mStepList.clear();
        mStepList.addAll(stepList);
        actStep();
    }

    private void actStep() {
        if (CollectionUtil.isEmpty(mStepList))
            return;
        Step step = mStepList.removeFirst();
        int duration = step.getDuration();
        if (step.isFinal())
            scrollFinalStep(step);
        else
            scrollDistanceStep(step);
        mHandler.sendEmptyMessageDelayed(MSG_SCROLL_STEP, duration);
    }

    private void scrollDistanceStep(Step step) {
        smoothScrollBy(step.getDistanceX(), step.getDistanceY(), step.getDuration());
    }

    private void scrollFinalStep(Step step) {
        smoothScrollTo(step.getFinalX(), step.getFinalY(), step.getDuration());
    }

    private void stopCurrentMovement() {
        stopScroll();
        mHandler.removeMessages(MSG_SCROLL_STEP);
    }

    public void setScrollListener(ScrollListener l) {
        mScrollListener = l;
    }

    public interface ScrollListener {
        void onScrollEnd(View v);
    }
}
