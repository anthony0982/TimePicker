package com.example.util;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.example.base.BaseApplication;

/**
 * 文本工具类
 * 
 * @author wangzengyang@gmail.com
 */
public class TextUtil {
    /**
     * Returns true if the string is null or 0-length.
     * 
     * @param str
     *            the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        str = str.trim();
        return str.length() == 0 || str.equals("null");
    }

    /**
     * 去掉文件名称中的非法字符
     * 
     * @param str
     * @return
     */
    public static String escapeFileName(String str) {
        if (str == null) {
            return null;
        }
        /** 非法字符包括：/\:*?"<>| */
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '/' || c == '\\' || c == ':' || c == '*' || c == '?' || c == '"' || c == '<' || c == '>' || c == '|') {
                continue;
            } else {
                builder.append(c);
            }
        }

        return builder.toString();
    }

    /**
     * 从url获取当前图片的id，如果url以ignoreTag开头则直接返回该url；如果ignoreTag为空，则不会判断ignoreTag
     * 
     * @param ignoreTag
     * @param url
     * @return
     */
    public static String getIdFromUrl(String url, String ignoreTag) {
        if (TextUtils.isEmpty(url) || (!TextUtils.isEmpty(ignoreTag)) && url.startsWith(ignoreTag))
            return url;
        int lastIndex = url.lastIndexOf(".jpg");
        if (lastIndex < 0)
            lastIndex = url.length() - 1;
        int beginIndex = url.lastIndexOf("/") + 1;
        int slashIndex = url.lastIndexOf("%2F") + 3;
        int finalSlashIndex = url.lastIndexOf("%252F") + 5;
        beginIndex = Math.max(Math.max(beginIndex, slashIndex), finalSlashIndex);

        return url.substring(beginIndex, lastIndex);
    }

    public static String getIdFromUrl(String url) {
        return getIdFromUrl(url, null);
    }

    public static String trim(String str) {
        if (isEmpty(str))
            return null;
        return str.trim();
    }

    /**
     * 从字符串资源文件读取字符串
     * 
     * @param resId
     * @return
     */
    public static String getString(int resId) {
        return BaseApplication.getAppContext().getResources().getString(resId);
    }

    public static CharSequence getString(int resIdX, int resIdY) {
        return getString(resIdX, getString(resIdY));
    }

    /**
     * 从字符串资源文件读取字符串
     * 
     * @param resId
     * @param formatArgs
     * @return
     */
    public static String getString(int resId, Object... formatArgs) {
        return BaseApplication.getAppContext().getResources().getString(resId, formatArgs);
    }

    /**
     * 比较两个字符串是否相同
     * 
     * @param first
     * @param second
     * @return
     */
    public static boolean equals(String first, String second) {
        if (isEmpty(first) || isEmpty(second))
            return false;
        return first.equals(second);
    }

    /** 简单判断坐标经纬度是否合法 */
    public static boolean isCoordinateEmpty(String l) {
        if (l == null) {
            return true;
        }
        l = l.trim();
        return l.length() == 0 || l.equals("null") || l.equals("0");
    }

    /**
     * 清理密码<br>
     * 将密码字符串中的中文、空格去掉
     * 
     * @param password
     * @return
     */
    public static String cleanPassword(String password) {
        if (isEmpty(password))
            return "";
        return password.replaceAll("[^\\x00-\\xff]*|\\s*", "");
    }

    /**
     * 将密码输入框中的全角字符、空格过滤掉
     * 
     * @param editText
     * @param textWatcher
     */
    public static void cleanPasswordEditText(final EditText editText, final TextWatcher textWatcher) {
        Object tag = editText.getTag();
        if (tag != null) {
            int selectionTag = 0;
            try {
                selectionTag = (Integer) tag;
            } catch (ClassCastException e) {
                return;
            }
            editText.setSelection(selectionTag);
            editText.setTag(null);
            return;
        }
        String password = editText.getText().toString();
        int selection = editText.getSelectionStart();
        int preLength = password.length();
        password = TextUtil.cleanPassword(password);
        int cleanedLength = password.length();
        selection = selection - (preLength - cleanedLength);
        if (selection < 0)
            selection = 0;

        editText.setTag(selection);
        editText.setText(password);
    }

    /**
     * 为EditText 设置密码过滤器
     * 
     * @param editText
     */
    public static void setPasswordFilter(EditText editText) {
        InputFilter lengthfilter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                return cleanPassword(source.toString());
            }
        };
        editText.setFilters(new InputFilter[] { lengthfilter });
    }

    public static int length(String phone) {
        return phone == null ? 0 : phone.length();
    }

    public static String getIdString(View v) {
        return String.valueOf(v.getId());
    }

    public static String[] getStringArray(int arrayResId) {
        return BaseApplication.getAppContext().getResources().getStringArray(arrayResId);
    }
}
