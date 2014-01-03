package com.example.scroll;

import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.example.base.BaseApplication;

/**
 * 滚动状态
 * 
 * @author wangzengyang@gmail.com
 * @since 2013-12-26
 */
public class ScrollState {

    private Scroller mScroller;

    public ScrollState() {
        mScroller = new Scroller(BaseApplication.getAppContext());
    }

    public ScrollState(Interpolator interpolator) {
        mScroller = new Scroller(BaseApplication.getAppContext(), interpolator);
    }

    /**
     * 获得当前滚动位置X坐标<br/>
     * 
     * 坐标系为：<br/>
     * 屏幕左上角为坐标原点，水平向右→为X轴正向，垂直向下↓为Y轴正向
     * 
     * @return
     */
    public int getCurrX() {
        return -mScroller.getCurrX();
    }

    /**
     * 获得当前滚动位置Y坐标<br/>
     * 
     * 坐标系为：<br/>
     * 屏幕左上角为坐标原点，水平向右→为X轴正向，垂直向下↓为Y轴正向
     * 
     * @return
     */
    public int getCurrY() {
        return -mScroller.getCurrY();
    }

    /**
     * 获得滚动重点X坐标<br/>
     * 
     * 坐标系为：<br/>
     * 屏幕左上角为坐标原点，水平向右→为X轴正向，垂直向下↓为Y轴正向
     * 
     * @return
     */
    public int getFinalX() {
        return -mScroller.getFinalX();
    }

    /**
     * 获得滚动重点Y坐标<br/>
     * 
     * 坐标系为：<br/>
     * 屏幕左上角为坐标原点，水平向右→为X轴正向，垂直向下↓为Y轴正向
     * 
     * @return
     */
    public int getFinalY() {
        return -mScroller.getFinalY();
    }

    /**
     * 是否需要继续滚动
     * 
     * @return
     */
    public boolean shouldScroll() {
        return mScroller.computeScrollOffset();
    }

    /**
     * 设置滚动参数，默认滚动时间为250ms
     * 
     * 坐标系为：<br/>
     * 屏幕左上角为坐标原点，水平向右→为X轴正向，垂直向下↓为Y轴正向
     * 
     * @param dx
     *            滚动水平距离
     * @param dy
     *            滚动垂直距离
     */
    public void setScrollParams(int dx, int dy) {
        mScroller.startScroll(mScroller.getCurrX(), mScroller.getCurrY(), -dx, -dy);
    }

    /**
     * 设置滚动参数
     * 
     * 坐标系为：<br/>
     * 屏幕左上角为坐标原点，水平向右→为X轴正向，垂直向下↓为Y轴正向
     * 
     * @param dx
     *            滚动水平距离
     * @param dy
     *            滚动垂直距离
     * @param duration
     *            滚动时间，单位毫秒
     */
    public void setScrollParams(int dx, int dy, int duration) {
        mScroller.startScroll(mScroller.getCurrX(), mScroller.getCurrY(), -dx, -dy, duration);
    }

    /**
     * 设置滚动参数
     * 
     * 坐标系为：<br/>
     * 屏幕左上角为坐标原点，水平向右→为X轴正向，垂直向下↓为Y轴正向
     * 
     * @param dx
     *            滚动水平距离
     * @param dy
     *            滚动垂直距离
     * @param duration
     *            滚动时间，单位毫秒
     */
    public void setScrollParams(int fromX, int fromY, int dx, int dy, int duration) {
        mScroller.startScroll(fromX, fromY, -dx, -dy, duration);
    }

    /**
     * 设置滚动状态为停止，即停止滚动
     */
    public void setStoped() {
        mScroller.forceFinished(true);
    }
}
