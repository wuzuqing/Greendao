package cn.wuzuqing.greendao.utils;

import android.util.Log;

public class LogUtils {

    private static final int D = 2;
    private static final int I = 3;
    private static final int W = 4;
    private static final int E = 5;

    private static boolean DEBUG = true;

    public static void setDEBUG(boolean DEBUG) {
        LogUtils.DEBUG = DEBUG;
    }

    private static final String TAG = "LogUtils";

    private static void log(int level, String msg) {
        if (DEBUG) {
            switch (level) {
                case D:
                    Log.d(TAG, msg);
                    break;
                case I:
                    Log.i(TAG, msg);
                    break;
                case W:
                    Log.w(TAG, msg);
                    break;
                case E:
                    Log.e(TAG, msg);
                    break;
            }
        }
    }

    public static void logd(String msg) {
        log(D, msg);
    }
}
