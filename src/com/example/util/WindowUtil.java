package com.example.util;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.base.BaseApplication;

/**
 * Window及UI缩放工具
 * 
 * @author wangzengyang@gmail.com
 */
@SuppressWarnings("unused")
public class WindowUtil {
    /** UI设计的竖向高度,单位：px */
    private static final int UI_DESIGN_PORTRAIT_SIZE = 1800;
    /** UI设计的横向高度,单位：px */
    private static final int UI_DESIGN_LANDSCAPE_SIZE = 1080;

    /** 自动缩放严格模式标志 */
    private static final String AUTO_RESIZE_STRICT_TAG = "strict_mode";
    private static final String TAG = WindowUtil.class.getSimpleName();
    /** 状态栏高度 */
    public static int STATUS_BAR_HEIGHT;
    /** 缩放比例:水平 */
    public static float SCALE_RATIO_HORIZONTAL;
    /** 缩放比例:垂直 */
    public static float SCALE_RATIO_VERTICAL;
    /** 缩放比例 */
    public static float SCALE_RATIO;
    /** 屏幕旋转度 */
    public static int WINDOW_ROTATION;

    private static float SCREEN_DENSITY = 1.0F;
    static {
        computeScaleRatio();
        computeScreenDensity();
        computeWindowRotation();
    }

    /**
     * View按UI设计大小等比缩放，重新计算view的宽高、边距、文字大小
     * 
     * @param view
     * @return
     */
    public static boolean resize(View view) {
        return resize(view, SCALE_RATIO_HORIZONTAL, SCALE_RATIO);
    }

    /**
     * 递归重新计算View及其子View的宽高
     * 
     * @param view
     * @return
     */
    public static boolean resizeRecursively(View view) {
        return resizeRecursively(view, SCALE_RATIO, SCALE_RATIO);
    }

    /**
     * 重新计算view的宽高、边距、文本大小<br>
     * （其中宽高、文本大小按照相同缩放系数；内外边距水平方向按照水平比例系数，垂直方向按照垂直比例系数）
     * 
     * @param view
     * @param horizontalRatio
     * @param verticalRatio
     * @return
     */

    public static boolean resize(View view, float horizontalRatio, float verticalRatio) {
        if (view == null)
            return false;
        /* 重新计算宽高 */
        resizeWidthAndHeight(view, horizontalRatio, verticalRatio);
        /* 重新计算内边距 */
        repadding(view);
        /* 重新计算外边距 */
        remargin(view);
        /* 重新计算文本大小 */
        if (view instanceof TextView)
            resizeText((TextView) view);
        return true;
    }

    /**
     * 重新计算view的宽高、边距、文本大小<br>
     * (严格模式)
     * 
     * @param view
     * @param horizontalRatio
     * @param verticalRatio
     * @return
     */

    public static boolean resizeStrictly(View view, float horizontalRatio, float verticalRatio) {
        if (view == null)
            return false;
        /* 重新计算宽高 */
        resizeWidthAndHeight(view, horizontalRatio, verticalRatio);
        /* 重新计算内边距 */
        repadding(view, horizontalRatio, verticalRatio);
        /* 重新计算外边距 */
        remargin(view, horizontalRatio, verticalRatio);
        /* 重新计算文本大小 */
        if (view instanceof TextView)
            resizeText((TextView) view);
        return true;
    }

    /**
     * 重新计算view的宽高
     * 
     * @param view
     * @param horizontalRatio
     * @param verticalRatio
     * @return
     */

    public static boolean resizeWidthAndHeight(View view, float horizontalRatio, float verticalRatio) {
        if (view == null)
            return false;
        Object tag = view.getTag();
        if (tag instanceof String) {
            String tagString = (String) tag;
            if ("ignoreSize".equals(tagString)) {
                return true;
            }
        }
        LayoutParams params = view.getLayoutParams();
        if (params != null) {
            int width = params.width;
            int height = params.height;
            if (params.width != LayoutParams.MATCH_PARENT && params.width != LayoutParams.WRAP_CONTENT) {
                width = (int) (width * horizontalRatio);
                if (width > 1)
                    params.width = width;
            }
            if (params.height != LayoutParams.MATCH_PARENT && params.height != LayoutParams.WRAP_CONTENT) {
                height = (int) (height * verticalRatio);
                if (height > 1)
                    params.height = height;
            }
            view.setLayoutParams(params);
        }

        return true;
    }

    /**
     * 重新计算view的Padding(非严格模式)
     * 
     * @param view
     * @return
     */
    public static boolean repadding(View view) {
        return repadding(view, SCALE_RATIO_HORIZONTAL, SCALE_RATIO_VERTICAL);
    }

    /**
     * 重新计算view的Padding(严格模式)
     * 
     * @param view
     * @return
     */
    public static boolean repadding(View view, float horizontalRatio, float verticalRatio) {
        if (view == null)
            return false;
        view.setPadding(
                (int) (view.getPaddingLeft() * horizontalRatio), (int) (view.getPaddingTop() * verticalRatio),
                (int) (view.getPaddingRight() * horizontalRatio), (int) (view.getPaddingBottom() * verticalRatio));
        return true;
    }

    /**
     * 重新计算view的Margin
     * 
     * @param view
     * @return
     */
    public static void remargin(View view) {
        remargin(view, SCALE_RATIO_HORIZONTAL, SCALE_RATIO_VERTICAL);
    }

    /**
     * 重新计算view的Margin
     * 
     * @param view
     * @return
     */
    public static void remargin(View view, float horizontalRatio, float verticalRatio) {
        MarginLayoutParams marginParams = null;
        try {
            marginParams = (MarginLayoutParams) view.getLayoutParams();
        } catch (ClassCastException e) {
            return;
        }
        if (marginParams == null)
            return;
        int left = (int) (marginParams.leftMargin * horizontalRatio);
        int top = (int) (marginParams.topMargin * verticalRatio);
        int right = (int) (marginParams.rightMargin * horizontalRatio);
        int bottom = (int) (marginParams.bottomMargin * verticalRatio);
        marginParams.setMargins(left, top, right, bottom);
        view.setLayoutParams(marginParams);
    }

    /**
     * 重新计算TextView中文本的大小
     * 
     * @param view
     * @return
     */
    public static boolean resizeText(TextView view) {
        if (view == null)
            return false;
        Object tag = view.getTag();
        if (tag instanceof String) {
            String tagString = (String) tag;
            if ("ignoreSize".equals(tagString)) {
                return true;
            }
        }
        float textSize = view.getTextSize();
        float ratio = SCALE_RATIO;
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize * ratio);
        return true;
    }

    /**
     * 重新计算view的宽高(高度及宽度均按照水平缩放比例)
     * 
     * @param view
     * @return
     */
    public static boolean resizeWithHorizontalRatio(View view) {
        return resize(view, SCALE_RATIO_HORIZONTAL, SCALE_RATIO_HORIZONTAL);
    }

    /**
     * 重新计算view的宽高(高度按照垂直缩放比例,宽度按照水平缩放比例)
     * 
     * @param view
     * @return
     */
    public static boolean resizeWithRespectiveRatio(View view) {
        return resize(view, SCALE_RATIO_HORIZONTAL, SCALE_RATIO_VERTICAL);
    }

    public static boolean resizeChildrenRecursively(View view) {
        if (view == null)
            return false;

        if (!(view instanceof ViewGroup))
            return true;
        ViewGroup group = ((ViewGroup) view);
        int childCount = group.getChildCount();
        View child = null;
        for (int i = 0; i < childCount; i++) {
            child = group.getChildAt(i);
            resizeRecursively(child);
        }
        return true;
    }

    /**
     * 递归重新计算view的宽高(高度按照垂直缩放比例,宽度按照水平缩放比例)
     * 
     * @param view
     * @return
     */
    public static boolean resizeRecursivelyWithRespectiveRatio(View view) {
        return resizeRecursively(view, SCALE_RATIO_HORIZONTAL, SCALE_RATIO_VERTICAL);
    }

    /**
     * 递归重新计算view的宽高(高度和宽度均按照垂直缩放比例)
     * 
     * @param view
     * @return
     */
    public static boolean resizeRecursivelyWithVerticalRatio(View view) {
        return resizeRecursively(view, SCALE_RATIO_VERTICAL, SCALE_RATIO_VERTICAL);
    }

    /**
     * 递归重新计算view的宽高
     * 
     * @param view
     * @param horizontalRatio
     *            水平缩放比例
     * @param verticalRatio
     *            垂直缩放比例
     * @return
     */
    private static boolean resizeRecursively(View view, float horizontalRatio, float verticalRatio) {
        if (view == null)
            return false;
        /* 是否为严格模式 */
        boolean strictMode = isStrictMode(view);
        /* 如果当前View需要以严格模式缩放，自动将所有子孙View按照严格模式缩放 */
        if (strictMode)
            return resizeStrictRecursively(view, SCALE_RATIO, SCALE_RATIO);
        resize(view, horizontalRatio, verticalRatio);
        if (!(view instanceof ViewGroup))
            return true;
        ViewGroup group = ((ViewGroup) view);
        int childCount = group.getChildCount();
        View child = null;
        for (int i = 0; i < childCount; i++) {
            child = group.getChildAt(i);
            resizeRecursively(child, horizontalRatio, verticalRatio);
        }
        return true;
    }

    /**
     * 递归重新计算view的宽高(严格模式)
     * 
     * @param view
     * @param horizontalRatio
     *            水平缩放比例
     * @param verticalRatio
     *            垂直缩放比例
     * @return
     */
    private static boolean resizeStrictRecursively(View view, float horizontalRatio, float verticalRatio) {
        if (view == null)
            return false;
        resizeStrictly(view, horizontalRatio, verticalRatio);
        if (!(view instanceof ViewGroup))
            return true;
        ViewGroup group = ((ViewGroup) view);
        int childCount = group.getChildCount();
        View child = null;
        for (int i = 0; i < childCount; i++) {
            child = group.getChildAt(i);
            resizeStrictRecursively(child, horizontalRatio, verticalRatio);
        }
        return true;
    }

    /**
     * 是否为严格缩放模式
     * 
     * @param view
     * @return
     */
    private static boolean isStrictMode(View view) {
        boolean strictMode = false;
        Object tag = view.getTag();
        if (tag == null)
            return false;
        String tagString = String.valueOf(tag);
        if (AUTO_RESIZE_STRICT_TAG.equals(tagString))
            strictMode = true;
        return strictMode;
    }

    /**
     * 根据屏幕宽度设置传入View的宽度
     * 
     * @param view
     * @param designedWidthResId
     *            UI设计的高度资源ID
     * @return 是否成功设置
     */
    public static boolean setWidth(View view, int designedWidthResId) {
        float designedWidth = BaseApplication.getAppContext().getResources().getDimension(designedWidthResId);
        LayoutParams params = null;
        if (view instanceof ViewGroup)
            params = view.getLayoutParams();
        if (params == null)
            params = ((View) (view.getParent())).getLayoutParams();
        if (params == null)
            return false;
        params.width = (int) (designedWidth * SCALE_RATIO_HORIZONTAL);
        view.setLayoutParams(params);
        return true;
    }

    /**
     * 根据屏幕宽度设置传入View的高度
     * 
     * @param view
     * @param designedHeightResId
     *            UI设计的高度资源ID(以像素为单位)
     * @return 是否成功设置
     */
    public static boolean setHeight(View view, int designedHeightResId) {
        if (view == null)
            return false;
        float designedHeight = BaseApplication.getAppContext().getResources().getDimension(designedHeightResId);
        LayoutParams params = null;
        if (view instanceof ViewGroup)
            params = view.getLayoutParams();
        if (params == null)
            params = ((View) (view.getParent())).getLayoutParams();
        if (params == null)
            return false;
        params.height = (int) (designedHeight * SCALE_RATIO_VERTICAL);
        view.setLayoutParams(params);
        return true;
    }

    /**
     * 根据屏幕宽高设置传入View的宽高(按照宽度比例等比缩放)
     * 
     * @param view
     * @param designedWidthResId
     *            UI设计的宽度资源ID(以像素为单位)
     * @param designedHeightResId
     *            UI设计的高度资源ID(以像素为单位)
     * @return 是否成功设置
     */
    public static boolean setSize(View view, int designedWidthResId, int designedHeightResId) {
        float designedWidth = BaseApplication.getAppContext().getResources().getDimension(designedWidthResId);
        float designedHeight = BaseApplication.getAppContext().getResources().getDimension(designedHeightResId);
        LayoutParams params = null;
        if (view instanceof ViewGroup)
            params = view.getLayoutParams();
        if (params == null)
            params = ((View) (view.getParent())).getLayoutParams();
        if (params == null)
            return false;
        params.width = (int) (designedWidth * SCALE_RATIO_HORIZONTAL);
        params.height = (int) (designedHeight * SCALE_RATIO_HORIZONTAL);
        view.setLayoutParams(params);
        return true;
    }

    /**
     * 根据屏幕宽度设置传入View的高度
     * 
     * @param view
     * @param height
     *            像素值
     * 
     * @return 是否成功设置
     */
    public static boolean setViewHeight(View view, int height) {
        LayoutParams params = null;
        if (view instanceof ViewGroup)
            params = view.getLayoutParams();
        if (params == null)
            params = ((View) (view.getParent())).getLayoutParams();
        if (params == null)
            return false;
        params.height = height;
        view.setLayoutParams(params);
        return true;
    }

    /**
     * 设置视图宽高
     * 
     * @param view
     * @param width
     * @param height
     * @return
     */
    public static boolean setViewSize(View view, int width, int height) {
        LayoutParams params = null;
        if (view instanceof ViewGroup)
            params = view.getLayoutParams();
        if (params == null)
            params = ((View) (view.getParent())).getLayoutParams();
        if (params == null)
            return false;
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
        return true;
    }

    /**
     * 根据屏幕宽度设置传入TextView的文本大小
     * 
     * @param view
     * @param designedTextSizeResId
     *            UI设计的文本大小资源ID(以像素为单位)
     * @return 是否成功设置
     */
    public static boolean setTextSize(TextView view, int designedTextSizeResId) {
        if (view == null)
            return false;
        float designedSize = BaseApplication.getAppContext().getResources().getDimension(designedTextSizeResId);
        float size = designedSize * SCALE_RATIO_HORIZONTAL;
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        return true;
    }

    /**
     * 设置View的外边距(像素值)
     * 
     * @param view
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @return
     */
    public static boolean setMargin(View view, int left, int top, int right, int bottom) {
        MarginLayoutParams marginParams = null;
        try {
            marginParams = (MarginLayoutParams) view.getLayoutParams();
        } catch (ClassCastException e) {
            return false;
        }
        if (marginParams == null)
            return false;
        marginParams.leftMargin = left;
        marginParams.topMargin = top;
        marginParams.rightMargin = right;
        marginParams.bottomMargin = bottom;
        view.setLayoutParams(marginParams);
        return true;
    }

    /**
     * 设置View的顶部外边距(像素值)
     * 
     * @param view
     * @return
     */
    public static boolean setMarginTop(View view, int marginTop) {
        MarginLayoutParams marginParams = null;
        try {
            marginParams = (MarginLayoutParams) view.getLayoutParams();
        } catch (ClassCastException e) {
            return false;
        }
        if (marginParams == null)
            return false;
        marginParams.topMargin = marginTop;
        view.setLayoutParams(marginParams);
        return true;
    }

    /**
     * 设置View的左侧外边距(像素值)
     * 
     * @param view
     * @return
     */
    public static boolean setMarginLeft(View view, int marginLeft) {
        MarginLayoutParams marginParams = null;
        try {
            marginParams = (MarginLayoutParams) view.getLayoutParams();
        } catch (ClassCastException e) {
            return false;
        }
        if (marginParams == null)
            return false;
        marginParams.leftMargin = marginLeft;
        view.setLayoutParams(marginParams);
        return true;
    }

    /**
     * 设置View的右侧外边距(像素值)
     * 
     * @param view
     * @return
     */
    public static boolean setMarginRight(View view, int marginRight) {
        MarginLayoutParams marginParams = null;
        try {
            marginParams = (MarginLayoutParams) view.getLayoutParams();
        } catch (ClassCastException e) {
            return false;
        }
        if (marginParams == null)
            return false;
        marginParams.rightMargin = marginRight;
        view.setLayoutParams(marginParams);
        return true;
    }

    /**
     * 设置View的底部外边距(像素值)
     * 
     * @param view
     * @return
     */
    public static boolean setMarginBottom(View view, int marginBottom) {
        MarginLayoutParams marginParams = null;
        try {
            marginParams = (MarginLayoutParams) view.getLayoutParams();
        } catch (ClassCastException e) {
            return false;
        }
        if (marginParams == null)
            return false;
        marginParams.bottomMargin = marginBottom;
        view.setLayoutParams(marginParams);
        return true;
    }

    /**
     * 设置View的内边距(像素值)
     * 
     * @param view
     * @return
     */
    public static boolean setPadding(View view, int left, int top, int right, int bottom) {
        if (view == null)
            return false;
        view.setPadding(left, top, right, bottom);
        return true;
    }

    /**
     * 设置View的顶部内边距(像素值)
     * 
     * @param view
     * @return
     */
    public static boolean setPaddingTop(View view, int top) {
        if (view == null)
            return false;
        view.setPadding(view.getPaddingLeft(), top, view.getPaddingRight(), view.getPaddingBottom());
        return true;
    }

    /**
     * 设置View的左侧内边距(像素值)
     * 
     * @param view
     * @return
     */
    public static boolean setPaddingLeft(View view, int paddingLeft) {
        if (view == null)
            return false;
        view.setPadding(paddingLeft, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        return true;
    }

    /**
     * 设置View的右侧内边距(像素值)
     * 
     * @param view
     * @return
     */
    public static boolean setPaddingRight(View view, int paddingRight) {
        if (view == null)
            return false;
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), paddingRight, view.getPaddingBottom());
        return true;
    }

    /**
     * 设置View的底部内边距(像素值)
     * 
     * @param view
     * @return
     */
    public static boolean setPaddingBottom(View view, int paddingBottom) {
        if (view == null)
            return false;
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), paddingBottom);
        return true;
    }

    /**
     * 获取View的宽度(像素值)
     * 
     * @param view
     * @return
     */
    public static int getWidth(View view) {
        LayoutParams params = view.getLayoutParams();
        if (params == null)
            return 0;
        return params.width;
    }

    /**
     * 获取View的高度(像素值)
     * 
     * @param view
     * @return
     */
    public static int getHeight(View view) {
        LayoutParams params = view.getLayoutParams();
        if (params == null)
            return 0;
        return params.height;
    }

    /**
     * 获取高度值(像素)(按照宽度比例缩放)
     * 
     * @return
     */
    public static float getHorizontalScaledDimen(int heightResId) {
        return BaseApplication.getAppContext().getResources().getDimension(heightResId) * SCALE_RATIO_HORIZONTAL;
    }

    /**
     * 获取高度值(像素)(按照高度比例缩放)
     * 
     * @return
     */
    public static float getVerticalScaledDimen(int heightResId) {
        return BaseApplication.getAppContext().getResources().getDimension(heightResId) * SCALE_RATIO_VERTICAL;
    }

    /**
     * 获取屏幕宽度(像素)
     * 
     * @return
     */
    public static int getWindowWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = BaseApplication.getAppContext().getResources().getDisplayMetrics();
        if (dm == null)
            return 0;
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度(像素)
     * 
     * @return
     */
    public static int getWindowHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = BaseApplication.getAppContext().getResources().getDisplayMetrics();
        if (dm == null)
            return 0;
        return dm.heightPixels;
    }

    /**
     * 计算资源文件中定义的尺寸像素值
     * 
     * @param resId
     *            dimen.xml中定义的资源ID
     * @return
     */
    public static float computeDimen(int resId) {
        return BaseApplication.getAppContext().getResources().getDimension(resId);
    }

    /**
     * 计算资源文件中定义的尺寸像素值，并乘以缩放系数ratio
     * 
     * @return
     */
    public static float computeScaledDimen(int resId, float ratio) {
        return computeDimen(resId) * ratio;
    }

    /**
     * 重新计算尺寸像素值，并乘以缩放系数ratio
     * 
     * @return
     */
    public static int computeScaledSize(int size) {
        return (int) (size * SCALE_RATIO);
    }

    /**
     * 重新计算尺寸像素值，并乘以缩放系数ratio
     * 
     * @return
     */
    public static int computeScaledSize(float size) {
        return (int) (size * SCALE_RATIO);
    }

    /**
     * 计算资源文件中定义的尺寸像素值，并乘以垂直缩放系数
     * 
     * @param resId
     *            UI设计的大小资源ID(以像素为单位)
     * @return
     */
    public static float computeScaledDimenByVerticalRatio(int resId) {
        return computeScaledDimen(resId, SCALE_RATIO_VERTICAL);
    }

    /**
     * 计算资源文件中定义的尺寸像素值，并乘以水平缩放系数
     * 
     * @param resId
     *            UI设计的大小资源ID(以像素为单位)
     * @return
     */
    public static float computeScaledDimenByHorizontalRatio(int resId) {
        return computeScaledDimen(resId, SCALE_RATIO_HORIZONTAL);
    }

    /**
     * 计算屏幕密度
     */
    public static void computeScreenDensity() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = BaseApplication.getAppContext().getResources().getDisplayMetrics();
        if (dm == null)
            return;
        SCREEN_DENSITY = dm.density;
    }

    /**
     * 计算UI/字体缩放比例
     */
    public static void computeScaleRatio() {
        int windowWidth = getWindowWidth();
        int windowHeight = getWindowHeight();
        if (windowWidth == 0 || windowHeight == 0)
            return;
        int designedWidth = (windowWidth > windowHeight) ? UI_DESIGN_PORTRAIT_SIZE : UI_DESIGN_LANDSCAPE_SIZE;
        int designedHeight = (windowWidth > windowHeight) ? UI_DESIGN_LANDSCAPE_SIZE : UI_DESIGN_PORTRAIT_SIZE;
        SCALE_RATIO_HORIZONTAL = (float) windowWidth / (float) designedWidth;
        SCALE_RATIO_VERTICAL = (float) windowHeight / (float) designedHeight;

        float ratioDesigned = (float) UI_DESIGN_PORTRAIT_SIZE / (float) UI_DESIGN_LANDSCAPE_SIZE;
        float ratioDevice = (float) windowHeight / (float) windowWidth;
        /* 当设备宽高比例与UI设计的比例相同，或者设备宽高比例比UI设计的比例瘦长时按照宽度等比缩放(主流)，相反，如果比UI设计的比例胖扁时按照高度缩放 */
        SCALE_RATIO = ratioDevice >= ratioDesigned ? SCALE_RATIO_HORIZONTAL : SCALE_RATIO_VERTICAL;
    }

    /**
     * 检查当前屏幕方向是否为横向
     * 
     * @return
     */
    public static boolean isLandscape() {
        return BaseApplication.getAppContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * dip转换px
     * 
     * @param dip
     * @return
     */
    public static int dip2px(float dip) {
        float f = BaseApplication.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (dip * f + 0.5F);
    }

    /**
     * dip转换px
     * 
     * @param dip
     * @return
     */
    public static int px2dip(float px) {
        float f = BaseApplication.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (px / f);
    }

    /**
     * 检查UI事件是否发生在视图view的区域内
     * 
     * @param v
     * @param ev
     * @return
     */
    public static boolean intersects(View v, MotionEvent ev) {
        if (v == null)
            return false;
        Rect rect = new Rect();
        v.getHitRect(rect);
        Rect r = new Rect();
        r.left = r.right = (int) ev.getX();
        r.bottom = r.top = (int) ev.getY();
        return rect.intersects(r.left, r.top, r.right, r.bottom);
    }

    /**
     * 通过反射计算状态栏高度
     * 
     * @return
     */
    public static int getStatusBarHeight() {
        if (STATUS_BAR_HEIGHT != 0)
            return STATUS_BAR_HEIGHT;
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = BaseApplication.getAppContext().getResources().getDimensionPixelSize(x);
            STATUS_BAR_HEIGHT = statusBarHeight;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return STATUS_BAR_HEIGHT;
    }

    /**
     * 根据Activity获取状态栏高度
     * 
     * @param activityo
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    public static void computeWindowRotation() {
        WindowManager windowManager = (WindowManager) BaseApplication.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        WINDOW_ROTATION = windowManager.getDefaultDisplay().getRotation();
        LogUtil.d(TAG, "computeWindowRotation rotation : " + WINDOW_ROTATION);
    }

    public static int getWindowRotation() {
        return WINDOW_ROTATION;
    }
}
