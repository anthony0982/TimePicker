package com.example.base;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;

import com.example.util.LogUtil;
import com.example.util.WindowUtil;

public class BaseApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        LogUtil.setDebugMode(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        WindowUtil.computeScaleRatio();
        WindowUtil.computeWindowRotation();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public static Context getAppContext() {
        return mContext;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    /**
     * 应用是否处于debug模式
     * 
     * @return
     */
    public static boolean isDebugMode() {
        ApplicationInfo info = getAppContext().getApplicationInfo();
        return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
}
