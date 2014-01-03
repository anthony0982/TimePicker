package com.example.wheel.adapter;

import com.example.util.TextUtil;

/**
 * 数字滚轮适配器
 * 
 * @author wangzengyang@gmail.com
 * @since 2013-12-26
 */
public class NumberWheelAdapter extends WheelAdapter {
    private int startNumber;
    private int endNumber;
    private int interval;
    private String label;

    public NumberWheelAdapter(int startNumber, int endNumber, int interval, String label) {
        this.startNumber = startNumber;
        this.endNumber = endNumber;
        this.interval = interval;
        this.label = label;
    }

    @Override
    public int getCount() {
        return (endNumber - startNumber) / interval;
    }

    @Override
    public String getItem(int index) {
        int number = startNumber + index * interval;
        if (TextUtil.isEmpty(label))
            return String.valueOf(number);
        return number + label;
    }

    /**
     * 设置起始数值
     * 
     * @param startNumber
     */
    public void setStartItem(int startItem) {
        this.startNumber = startItem * this.interval;
        this.notifyChanged();
    }

    @Override
    public int getValue(int index) {
        return index * this.interval + this.startNumber;
    }

    /**
     * 获取当前起始数字
     * 
     * @return
     */
    @Override
    public int getStartValue() {
        return this.startNumber;
    }

    /**
     * 获取当前结束数字
     * 
     * @return
     */
    @Override
    public int getEndValue() {
        return this.endNumber;
    }

    /**
     * 获取数字间隔
     * 
     * @return
     */
    @Override
    public int getInterval() {
        return this.interval;
    }

    @Override
    public int getValueIndex(int value) {
        return (value - this.startNumber) / this.interval;
    }

    @Override
    public void setStartValue(int value) {
        this.startNumber = value;
    }
}
