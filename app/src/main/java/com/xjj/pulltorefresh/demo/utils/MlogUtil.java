package com.xjj.pulltorefresh.demo.utils;

import android.util.Log;

public class MlogUtil {

    public static boolean debug = true;

    public static String MY_JSON = "xjj_json";
    public static String MY_TEST = "xjj_test";
    public static String MY_LIFECYCLE = "xjj_lifecycle";

    public static void setDebugIsOpen(boolean check){
        MlogUtil.debug = check;
    }

    public static void d(String tag, String msg){
        if(MlogUtil.debug) Log.d(tag, msg);
    }

    public static void e(String tag, String msg){
        if(MlogUtil.debug) Log.e(tag, msg);
    }

}
