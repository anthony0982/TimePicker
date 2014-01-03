package com.example.scroll;


/**
 * 动作步
 * 
 * @author wangzengyang@gmail.com
 * @since 2013-12-26
 */
public class Step {
    /** 默认步长：250毫秒 */
    public static final int DEFAULT_DURATION = 250;
    /** X轴距离 */
    private int distanceX;
    /** Y轴距离 */
    private int distanceY;
    /** 目标点X轴位置 */
    private int finalX = Integer.MAX_VALUE;
    /** 目标点Y轴位置 */
    private int finalY;
    /** 动作时间 */
    private int duration = DEFAULT_DURATION;

    public static Step createDistanceStep(int distanceX, int distanceY) {
        Step step = new Step();
        step.setDistanceX(distanceX);
        step.setDistanceY(distanceY);
        return step;
    }

    public static Step createDistanceStep(int distanceX, int distanceY, int duration) {
        Step step = new Step();
        step.setDistanceX(distanceX);
        step.setDistanceY(distanceY);
        return step;
    }

    public static Step createFinalStep(int finalX, int finalY) {
        Step step = new Step();
        step.setFinalX(finalX);
        step.setFinalY(finalY);
        return step;
    }

    public static Step createFinalStep(int finalX, int finalY, int duration) {
        Step step = new Step();
        step.setFinalX(finalX);
        step.setFinalY(finalY);
        step.setDuration(duration);
        return step;
    }

    public int getDistanceX() {
        return distanceX;
    }

    public void setDistanceX(int distanceX) {
        this.distanceX = distanceX;
    }

    public int getDistanceY() {
        return distanceY;
    }

    public void setDistanceY(int distanceY) {
        this.distanceY = distanceY;
    }

    public int getFinalX() {
        return finalX;
    }

    public void setFinalX(int finalX) {
        this.finalX = finalX;
    }

    public int getFinalY() {
        return finalY;
    }

    public void setFinalY(int finalY) {
        this.finalY = finalY;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isFinal() {
        return this.finalX != Integer.MAX_VALUE;
    }

    @Override
    public String toString() {
        return "Step [distanceX="
                + distanceX + ", distanceY=" + distanceY + ", finalX=" + finalX + ", finalY=" + finalY + ", duration=" + duration + "]";
    }
}
