package com.oodles.apprtcandroidoodles.util;

import android.util.Log;

import com.oodles.apprtcandroidoodles.BuildConfig;


/**
 * Created by piyush on 6/7/16.
 */
public class Logger {

    public static final boolean DEBUG_MODE = BuildConfig.DEBUG;

    public static void LogInfo(String TAG, String msg) {
        if (DEBUG_MODE && validateArguments(TAG,msg)) {
            Log.i(TAG, msg);
        }
    }

    public static void LogWarrning(String TAG, String msg) {
        if (DEBUG_MODE && validateArguments(TAG,msg)) {
            Log.w(TAG, msg);
        }
    }

    public static void LogDebug(String TAG, String msg) {
        if (DEBUG_MODE && validateArguments(TAG,msg)) {
            Log.d(TAG, msg);
        }
    }

    public static void LogVerbose(String TAG, String msg) {
        if (DEBUG_MODE && validateArguments(TAG, msg)) {
            Log.v(TAG, msg);
        }
    }

    public static void LogError(String TAG, String msg) {
        if (DEBUG_MODE && validateArguments(TAG,msg)) {
            Log.e(TAG, msg);
        }
    }
    private static boolean validateArguments(String TAG, String msg){
        return (TAG != null && !TAG.isEmpty()) && (msg != null && !msg.isEmpty());
    }
}