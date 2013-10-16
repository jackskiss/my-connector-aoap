package com.obigo.baidumusic.standard;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.hkmc.api.ui.HkmcOSD;
import com.obigo.baidumusic.standard.player.PlayerService;
import com.obigo.baidumusic.standard.util.NetworkChecker;
import com.obigo.baidumusic.standard.util.ObiLog;

public class ConnReceiver extends BroadcastReceiver{
    public static final String TAG = "ConnReceiver";
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_3G = 0;
    public static final int NETWORK_WIMAX = 6;
    public static final int NETWORK_NONE = -1;

    private static BaseActivity mActivity;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

            if (!NetworkChecker.isConnected(context) && mActivity != null) {
                NetworkChecker.showError(mActivity);
                try {
                    PlayerService player = MusicApplication.getPlayer();
                    
                    if (player.getPlayerStatus() == PlayerService.STATUS_PLAYING) {
                        player.pause();
                    }
                } catch (Exception e) {
                    ObiLog.e(TAG, "getPlayer error");
                }

            } else if ( (NetworkChecker.isConnected(context)) && mActivity != null) {
                try {
                    PlayerService player = MusicApplication.getPlayer();
                    
                    if (player.getPlayerStatus() == PlayerService.STATUS_PAUSED) {
                        player.resume();
                    }
                } catch (Exception e) {
                    ObiLog.e(TAG, "getPlayer error");
                }
            } else if (!NetworkChecker.isConnected(context)) { // disconnected in background
            	try {
                    PlayerService player = MusicApplication.getPlayer();                    
                    if (player.getPlayerStatus() == PlayerService.STATUS_PLAYING) {
                        player.pause();
                    }
                    PendingIntent contentIntent = PendingIntent.getActivity (context, 0, null, 0);
                	HkmcOSD mOSD = new HkmcOSD(context);
                    mOSD.OneShotOSD(1, context.getResources().getString(R.string.warning_network_connection1), contentIntent);
                } catch (Exception e) {
                    ObiLog.e(TAG, "getPlayer error");
                }
            } else if (NetworkChecker.isConnected(context)) { // connected in background
            	try {
                    PlayerService player = MusicApplication.getPlayer();                    
                    if (player.getPlayerStatus() == PlayerService.STATUS_PAUSED) {
                        player.resume();                        
                    }
                } catch (Exception e) {
                    ObiLog.e(TAG, "getPlayer error");
                }
            }
            
        } 
    }

    public static void setActivity(BaseActivity activity) {
        mActivity = activity;
    }
    
    public static boolean isBackGroundMode() {
        if (mActivity == null)
            return true;
        else
            return false;
    }
    // Return true if application is background
    public static boolean isApplicationInBackground(Context context)  
    { 
        ActivityManager am =  
          (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE); 

        List<RunningTaskInfo> tasks = am.getRunningTasks(1); 

        if (!tasks.isEmpty())  
        { 
            ComponentName topActivity = tasks.get(0).topActivity; 

            if (!topActivity.getPackageName().equals(context.getPackageName())) 
            { 
                return true; 
            } 
        } 

        return false; 
    }
}
