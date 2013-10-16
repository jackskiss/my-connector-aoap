package com.obigo.baidumusic.standard.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.os.StatFs;

public final class ObigoFile {
    private static String TAG = "ObigoFile";

    private ObigoFile() {
        // not called
    }

    public static boolean saveObject(Context context, String filename, Object object) {
        if (!isAvailable()) {
            return false;
        }
        
        ObjectOutputStream out = null;

        try {
            File file = context.getFileStreamPath(filename);
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    ObiLog.e(TAG, "saveObject: file exist now! ");
                    return false;
                }
            }

            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.close();

        } catch (IOException e) {
            ObiLog.e(TAG, "saveObject error : ", e);
            
            if(out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                    ObiLog.e(TAG, "saveObject error : ", e1);
                }
            }
            
            return false;
        }

        return true;
    }

    public static Object loadObject(Context context, String filename) {
        Object retObj = null;

        ObjectInputStream in = null;
        
        try {
            File file = context.getFileStreamPath(filename);
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    ObiLog.e(TAG, "saveObject: file exist now! ");
                    return null;
                }
            }

            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);

            in = new ObjectInputStream(bis);

            retObj = in.readObject();
        } catch (IOException e) {
            ObiLog.e(TAG, "loadObject error : ", e);
        } catch (ClassNotFoundException e) {
            ObiLog.e(TAG, "loadObject error : ", e);
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                ObiLog.e(TAG, "saveObject error : ", e);
            }
        }

        return retObj;
    }

    private static boolean isAvailable() {
        StatFs st = new StatFs("/data/");
        int free = (st.getAvailableBlocks() * st.getBlockSize())
                / (1024 * 1024);
        int limit = ((st.getBlockCount() * st.getBlockSize()) / (1024 * 1024))
                * (1 / 10);

        ObiLog.d(TAG, "free: " + free + "mb, limit: " + limit + "mb");

        if (free < limit) {
            ObiLog.e(TAG, "Not enough free space.");
            return false;
        }
        return true;
    }
}
