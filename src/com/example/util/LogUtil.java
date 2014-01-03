package com.example.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * 日志工具类
 * 
 * @author wangzengyang@gmail.com
 */
public final class LogUtil {

    private static final String LOG_PATH = Environment.getExternalStorageState() + "/Didi";
    private static LogUtil sInstance;
    private static boolean debug;

    private LogUtil() {
    }

    public static synchronized final LogUtil getInstance() {
        if (sInstance == null) {
            sInstance = new LogUtil();
        }
        return sInstance;
    }

    public static void setDebugMode(boolean mode) {
        debug = mode;
    }

    public static void v(String tag, String msg) {
        if (debug) {
            Log.v(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (debug) {
            Log.w(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (debug) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (debug) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (debug) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (debug) {
            Log.e(tag, msg, tr);
        }
    }

    private static HashMap<String, Integer> actionMap = new HashMap<String, Integer>();

    public static void a(Class<?> clazz, String method, MotionEvent event, String log) {
        a(clazz, method, event, log, true);
    }

    public static void a(Class<?> clazz, String method, MotionEvent event, String log, boolean ignoreDuplicate) {
        if (!debug || event == null)
            return;
        int action = event.getAction();
        String calssName = clazz == null ? "" : clazz.getSimpleName();
        String key = calssName + method;
        Integer i = actionMap.get(key);
        int value = -10;
        if (i != null)
            value = i;
        if (action != value)
            actionMap.put(key, action);
        if (ignoreDuplicate && action == value)
            return;
        StringBuilder sb = new StringBuilder();
        sb.append("class : " + calssName);
        sb.append(TextUtil.isEmpty(calssName) ? "" : ", method : " + method);
        sb.append(", action : " + getAction(action));
        sb.append(TextUtil.isEmpty(log) ? "" : ", log : " + log);
        Log.d("ActionTracker", sb.toString());
    }

    public static void a(Class<?> clazz, String method, MotionEvent event) {
        a(clazz, method, event, "");
    }

    public static void k(Class<?> clazz, String method, int keyCode, KeyEvent event) {
        k(clazz, method, keyCode, event, "");
    }

    public static void k(Class<?> clazz, String method, int keyCode, KeyEvent event, String log) {
        if (!debug) {
            return;
        }

        String calssName = clazz == null ? "" : clazz.getSimpleName();
        StringBuilder sb = new StringBuilder();
        sb.append("class : " + calssName);
        sb.append(TextUtil.isEmpty(calssName) ? "" : ", method : " + method);
        sb.append(", keycode : " + keyCode);
        if (event != null) {
            int action = event.getAction();
            sb.append(", action : " + getAction(action));
        }
        sb.append(TextUtil.isEmpty(log) ? "" : ", log : " + log);
        Log.d("KeyEventTracker", sb.toString());

    }

    public static String getAction(int action) {
        switch (action) {
        case MotionEvent.ACTION_CANCEL:
            return "ACTION_CANCEL";
        case MotionEvent.ACTION_UP:
            return "ACTION_UP";
        case MotionEvent.ACTION_DOWN:
            return "ACTION_DOWN";
        case MotionEvent.ACTION_MOVE:
            return "ACTION_MOVE";
        case MotionEvent.ACTION_OUTSIDE:
            return "ACTION_OUTSIDE";
            // case MotionEvent.ACTION_SCROLL:
            // return "ACTION_SCROLL";
        }
        return null;
    }

    public static void clearCache() {
        File logFile = new File(LOG_PATH + "/log4pad.txt");
        if (logFile.exists()) {
            logFile.delete();
        }
    }

    public static void writeToCache(String filter, String msg) {
        if (msg.contains(filter)) {
            FileOutputStream fos = null;
            try {
                File logFile = new File(LOG_PATH);
                if (!logFile.exists()) {
                    logFile.mkdirs();
                }
                fos = new FileOutputStream(logFile + "/log4pad.txt", true);
                fos.write(msg.getBytes());
                fos.write("\n\r".getBytes());
                fos.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fos = null;
                }
            }
        }
    }

    public static void trace() {
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : traces) {
            LogUtil.d("Trace", stackTraceElement.toString());
        }
    }
}
