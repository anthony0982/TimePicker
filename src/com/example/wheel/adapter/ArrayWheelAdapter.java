package com.example.wheel.adapter;

/**
 * 数组滚轮适配器
 * 
 * @author wangzengyang@gmail.com
 * @since 2013-12-26
 */
public class ArrayWheelAdapter extends WheelAdapter {
    String[] data;

    public ArrayWheelAdapter(String[] data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return this.data.length;
    }

    @Override
    public String getItem(int index) {
        return data[index];
    }

    @Override
    public int getValue(int index) {
        return index;
    }

    /**
     * 获取当前起始数字
     * 
     * @return
     */
    @Override
    public int getStartValue() {
        return 0;
    }

    /**
     * 获取当前结束数字
     * 
     * @return
     */
    @Override
    public int getEndValue() {
        return this.getCount() - 1;
    }

    /**
     * 获取数字间隔
     * 
     * @return
     */
    @Override
    public int getInterval() {
        return 1;
    }

    @Override
    public int getValueIndex(int value) {
        return 0;
    }

    @Override
    public void setStartValue(int value) {
    }
}
