package com.obigo.baidumusic.standard.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

import com.obigo.baidumusic.standard.MusicApplication;

public final class ObiLog {
    private static boolean LOADING_CHECK = true;

    public static final String LOG_PATH = android.os.Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/baidumusic.log";
    public static final String TIME_LOG_PATH = android.os.Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/baidumusic";

    private static String mTag;
    private static long mStart, mEnd;

    private ObiLog() {
        // not called
    }

    public static void v(String tag, String msg) {
        if (MusicApplication.enableLog()) {
            Log.v(tag, msg);
            writeLog("v", tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (MusicApplication.enableLog()) {
            Log.d(tag, msg);
            writeLog("d", tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (MusicApplication.enableLog()) {
            Log.i(tag, msg);
            writeLog("i", tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (MusicApplication.enableLog()) {
            Log.w(tag, msg);
            writeLog("w", tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (MusicApplication.enableLog()) {
            Log.e(tag, msg);
            writeLog("e", tag, msg);
        }
    }
    
    public static void e(String tag, String msg, Exception e) {
        if (MusicApplication.enableLog()) {
            Log.e(tag, msg);
            writeLog("e", tag, msg);
            e.printStackTrace();
            writeLog("e", tag, e.toString());
        }
    }

    private static void writeLog(String level, String tag, String msg) {
        File logfile = new File(LOG_PATH);

        if (logfile.exists()) {
            BufferedWriter buf = null;
            
            try {
                buf = new BufferedWriter(new FileWriter(logfile, true));
                buf.append(level + " " + tag + ":" + msg);
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                Log.e(tag, "writeLog error : " + e.getMessage());
                
                if(buf != null) {
                    try {
                        buf.close();
                    } catch (IOException e1) {
                        Log.e(tag, "writeLog error : " + e1.getMessage());
                    }
                }
            }
        }
    }

    public static void startLoad(String tag) {
        if (LOADING_CHECK) {
            mTag = tag;
            mStart = System.currentTimeMillis();
        }
    }

    public static void endLoad(String tag) {
        if (LOADING_CHECK && mTag.equals(tag)) {
            mEnd = System.currentTimeMillis();

            SimpleDateFormat formatter1 = new SimpleDateFormat(
                    "MM/dd/yy-HH:mm:ss  ");
            SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm:ss.SSS");

            long duration = mEnd - mStart;

            String log = formatter1.format(new Date(mEnd)) + mTag
                    + " - start : " + formatter2.format(new Date(mStart))
                    + ", end : " + formatter2.format(new Date(mEnd))
                    + ", response : " + duration + "ms\n";

            Log.d("LoadingTest", log);

            File logpath = new File(TIME_LOG_PATH);
                
            if(!logpath.exists()) {
                if (!logpath.mkdir()) {
                    Log.e(mTag, "endLoad: logpath.mkdir() is false.");
                    return;
                }
            }
            
            BufferedWriter buf = null;
            
            try {
                buf = new BufferedWriter(new FileWriter(logpath + "/time.log", true));
                buf.append(log);
                buf.close();
            } catch (IOException e) {
                Log.e(tag, "writeLog error : " + e.getMessage());
                
                if(buf != null) {
                    try {
                        buf.close();
                    } catch (IOException e1) {
                        Log.e(tag, "writeLog error : " + e1.getMessage());
                    }
                }
            }
        }
    }
}
