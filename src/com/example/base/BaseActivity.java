package com.example.base;

import net.tsz.afinal.FinalActivity;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.example.util.WindowUtil;

/**
 * Activity的基类，实现基本的自动缩放
 * 
 * @author wangzengyang@gmail.com
 * @since 2013-8-26
 */
public class BaseActivity extends FragmentActivity {
    private View mContentView;

    @Override
    public void setContentView(int layoutResID) {
        mContentView = getLayoutInflater().inflate(layoutResID, null);
        this.setContentView(mContentView);
    }

    @Override
    public void setContentView(View view) {
        resizeView(view);
        super.setContentView(view);
        FinalActivity.initInjectedView(this);
    }

    public View getContentView() {
        return mContentView;
    }

    /**
     * 重新计算View及子View的宽高、边距
     * 
     * @param view
     */
    public void resizeView(View view) {
        WindowUtil.resizeRecursively(view);
    }

    public String getLogTag() {
        return this.getClass().getSimpleName();
    }
}
