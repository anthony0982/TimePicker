package com.example.base;

import net.tsz.afinal.FinalActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.util.WindowUtil;

/**
 * Fragment的基类，实现基本的自动缩放
 * 
 * @author wangzengyang@gmail.com
 * @since 2013-8-26
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        resizeView(view);
        FinalActivity.initInjectedView(this, view);
    }

    /**
     * 重新计算View宽高、边距
     * 
     * @param view
     */
    public void resizeView(View view) {
        WindowUtil.resizeRecursively(view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public String getLogTag() {
        return this.getClass().getSimpleName();
    }
}
