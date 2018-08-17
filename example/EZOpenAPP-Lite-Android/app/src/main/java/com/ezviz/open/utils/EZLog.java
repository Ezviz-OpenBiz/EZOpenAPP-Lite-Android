package com.ezviz.open.utils;

import android.util.Log;


/**
 * Description: log类
 * Created by dingwei3
 *
 * @date : 2016/12/9
 */
public class EZLog {

    public static boolean DEBUG = false;

    public static void debugLog(String tag, String content) {
        if (DEBUG) {
            Log.i(tag, content);
        }
    }

    public static void errorLog(String tag, String content) {
        if (DEBUG) {
            Log.e(tag, content);
        }
    }

    public static void infoLog(String tag, String content) {
        if (DEBUG) {
            Log.i(tag, content);
        }
    }

    public static void verboseLog(String tag, String content) {
        if (DEBUG) {
            Log.v(tag, content);
        }
    }

    public static void warnLog(String tag, String content) {
        if (DEBUG) {
            Log.w(tag, content);
        }
    }

    public static void debugLog(String tag, String content, Exception e) {
        if (DEBUG) {
            Log.d(tag, content, e);
        }
    }

    public static void errorLog(String tag, String content, Exception e) {
        if (DEBUG) {
            Log.e(tag, content, e);
        }
    }

    public static void warnLog(String tag, String content, Exception e) {
        if (DEBUG) {
            Log.w(tag, content, e);
        }
    }

    public static void warnLog(String tag, Exception ex) {
        if (DEBUG) {
            Log.w(tag, ex);
        }
    }

    public static void d(String tag, String content) {
        if (DEBUG) {
            Log.i(tag, content);
        }
    }

    public static void e(String tag, String content) {
        if (DEBUG) {
            Log.e(tag, content);
        }
    }

    public static void i(String tag, String content) {
        if (DEBUG) {
            Log.i(tag, content);
        }
    }

    public static void v(String tag, String content) {
        if (DEBUG) {
            Log.v(tag, content);
        }
    }

    public static void w(String tag, String content) {
        if (DEBUG) {
            Log.w(tag, content);
        }
    }

    public static void d(String tag, String content, Exception e) {
        if (DEBUG) {
            Log.d(tag, content, e);
        }
    }

    public static void e(String tag, String content, Exception e) {
        if (DEBUG) {
            Log.e(tag, content, e);
        }
    }

    public static void w(String tag, String content, Exception e) {
        if (DEBUG) {
            Log.w(tag, content, e);
        }
    }

    public static void w(String tag, Exception ex) {
        if (DEBUG) {
            Log.w(tag, ex);
        }
    }
}


