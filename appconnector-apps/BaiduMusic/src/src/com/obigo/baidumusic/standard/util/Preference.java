package com.obigo.baidumusic.standard.util;

import android.content.Context;
import android.content.SharedPreferences;

public final class Preference {
    private static String TAG = "Preference";
    private static String mPrefName = "ObigoBaiduMusic";
    private static final boolean isDebugMode = false;

    public static final String KEY_SHUFFLE = "key_shuffle";
    public static final String KEY_LOOP = "key_loop";
    public static final String KEY_RECENT_SEARCH = "key_recent_search";
    public static final String KEY_THUMBNAIL = "key_thumbnail";
    public static final String KEY_LAST_CATEGORY = "key_last_category";
    
    public static final int waitTimeForLoadingList = 10000; // 1000 is 1sec

    private Preference() {
        // not called
    }
    
    public static boolean getDebugMode() {
        return isDebugMode;
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences prefs = context
                .getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);

        editor.apply();
        ObiLog.i(TAG, "putBoolean() success - k:" + key + ", v:" + value);
    }

    public static void putFloat(Context context, String key, float value) {
        SharedPreferences prefs = context
                .getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);

        editor.apply();

        ObiLog.i(TAG, "putFloat() success - k:" + key + ", v:" + value);
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences prefs = context
                .getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);

        editor.apply();
        ObiLog.i(TAG, "putInt() success - k:" + key + ", v:" + value);
    }

    public static void putLong(Context context, String key, long value) {
        SharedPreferences prefs = context
                .getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);

        editor.apply();
        ObiLog.i(TAG, "putLong() success - k:" + key + ", v:" + value);
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences prefs = context
                .getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);

        editor.apply();
        ObiLog.i(TAG, "putString() success - k:" + key + ", v:" + value);
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences prefs = context
                .getSharedPreferences(mPrefName, Context.MODE_PRIVATE);

        try {
            boolean v = prefs.getBoolean(key, false);
            ObiLog.i(TAG, "getBoolean() success - k:" + key + ", v:" + v);
            return v;
        } catch (ClassCastException e) {
            ObiLog.e(TAG, "getBoolean() failed - k:" + key + ", v: false");
            return false;
        }
    }

    public static float getFloat(Context context, String key) {
        SharedPreferences prefs = context
                .getSharedPreferences(mPrefName, Context.MODE_PRIVATE);

        try {
            float v = prefs.getFloat(key, Float.NaN);
            ObiLog.i(TAG, "getFloat() success - k:" + key + ", v:" + v);
            return v;
        } catch (ClassCastException e) {
            ObiLog.e(TAG, "getFloat() failed - k:" + key + ", v: NaN");
            return Float.NaN;
        }
    }

    public static int getInt(Context context, String key) {
        SharedPreferences prefs = context
                .getSharedPreferences(mPrefName, Context.MODE_PRIVATE);

        try {
            int v = prefs.getInt(key, Integer.MIN_VALUE);
            ObiLog.i(TAG, "getInt() success - k:" + key + ", v:" + v);
            return v;
        } catch (ClassCastException e) {
            ObiLog.e(TAG, "getInt() failed - k:" + key + ", v: MIN_VALUE");
            return Integer.MIN_VALUE;
        }
    }

    public static long getLong(Context context, String key) {
        SharedPreferences prefs = context
                .getSharedPreferences(mPrefName, Context.MODE_PRIVATE);

        try {
            long v = prefs.getLong(key, Long.MIN_VALUE);
            ObiLog.i(TAG, "getLong() success - k:" + key + ", v:" + v);
            return v;
        } catch (ClassCastException e) {
            ObiLog.e(TAG, "getLong() failed - k:" + key + ", v: MIN_VALUE");
            return Long.MIN_VALUE;
        }
    }

    public static String getString(Context context, String key) {
        SharedPreferences prefs = context
                .getSharedPreferences(mPrefName, Context.MODE_PRIVATE);

        try {
            String v = prefs.getString(key, "");
            ObiLog.i(TAG, "getString() success - k:" + key + ", v:" + v);
            return v;
        } catch (ClassCastException e) {
            ObiLog.e(TAG, "getString() failed - k:" + key + ", v: ");
            return "";
        }
    }
}
