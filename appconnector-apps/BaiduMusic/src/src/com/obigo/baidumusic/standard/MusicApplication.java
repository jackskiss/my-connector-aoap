package com.obigo.baidumusic.standard;

import java.io.File;
import java.io.RandomAccessFile;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.baidu.music.manager.ImageManager;
import com.baidu.music.oauth.OAuthHelper;
import com.baidu.music.oauth.OAuthHelper.CredentialsListener;
import com.baidu.music.oauth.OAuthToken;
import com.baidu.utils.LogUtil;
import com.hkmc.api.Const;
import com.hkmc.api.Driving;
import com.obigo.baidumusic.standard.player.FavoriteList;
import com.obigo.baidumusic.standard.player.PlayerService;
import com.obigo.baidumusic.standard.player.PlayerService.PlayerBinder;
import com.obigo.baidumusic.standard.util.ObiLog;
import com.obigo.baidumusic.standard.util.Preference;
import com.obigo.weblink.RemoteWebLinkServer;

public class MusicApplication extends Application {
    private static String TAG = "MusicApplication";

    private static final String CLIENT_ID = "Evf1RCPtQN5jSGs3OaGIgPmk";
    private static final String CLIENT_SECRET = "7dOnD3D1FnMA50e81gkLV94cxGIIWN25";
    private static final String SCOPE = "music_media_basic,music_musicdata_basic,music_search_basic,music_media_premium";

    private static final String PLAYER_SERVICE = "com.obigo.baidumusic.standard.PLAYER_SERVICE";

//    private static Context mContext;
    private static Boolean mLogEnable;

    private static PlayerService mService;
    private static MusicServiceConnection mListener;

    public interface RegulationListener {
        void onChangedRegulationMode(boolean isRegulation);
    }

    private static Driving mDriving;
    private static RegulationListener mRegulationListener;

    private BroadcastReceiver mRegulationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction()
                    .equals(Const.DRIVING_ACTION_ENTER_REGULATION)) {
                ObiLog.d(TAG, "Enter Regulation mode");

                if (mRegulationListener != null) {
                    mRegulationListener.onChangedRegulationMode(true);
                }
            } else if (intent.getAction().equals(
                    Const.DRIVING_ACTION_EXIT_REGULATION)) {
                ObiLog.d(TAG, "Exit Regulation mode");

                if (mRegulationListener != null) {
                    mRegulationListener.onChangedRegulationMode(false);
                }
            }
        }
    };

    public static void setOnRegulationListener(RegulationListener listener) {
        mRegulationListener = listener;
    }

    @Override
    public void onCreate() {
//        MusicApplication.mContext = getApplicationContext();

        /* authentication */
        OAuthHelper.getClientCredentialsTokenAsync(getApplicationContext(),
                CLIENT_ID, CLIENT_SECRET, SCOPE, new CredentialsListener() {

                    @Override
                    public void onGetCredentialsFinish(int status,
                            OAuthToken token) {
                        ObiLog.d(TAG,
                                "getClientCredentialsTokenAsync status : "
                                        + status);

                        if (token != null) {
                            ObiLog.d(TAG, "getClientCredentialsTokenAsync "
                                    + " token : " + token.toString());
                        }
                    }
                });

        /* album art cache setting */
        String cachePath = getCacheDir() + "/image/";
        int cacheSize = 1 * 1024 * 1024;
        ImageManager.init(this, ImageManager.POSTFIX_JPG, cachePath, cacheSize);

        /* log setting */
        File file = new File(ObiLog.LOG_PATH);

        if (file.exists()) {
            mLogEnable = true;
            LogUtil.setDebugMode(true);

            // delete previous log data
            RandomAccessFile logfile;
            try {
                logfile = new RandomAccessFile(file, "rw");
                logfile.setLength(0);
            } catch (Exception e) {
                ObiLog.e(TAG, "logfile error : ", e);
            }

            Log.d(TAG, "Baidu Music Log enable");
        } else {
            mLogEnable = false;
            LogUtil.setDebugMode(false);
            Log.d(TAG, "Baidu Music Log disable");
        }

        /* regist regulation receiver */
        if (!Preference.getDebugMode())
            mDriving = new Driving(getApplicationContext());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.DRIVING_ACTION_ENTER_REGULATION);
        intentFilter.addAction(Const.DRIVING_ACTION_EXIT_REGULATION);

        registerReceiver(mRegulationReceiver, intentFilter);

        FavoriteList.getInstance(getApplicationContext()).loadMusicList();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        unregisterReceiver(mRegulationReceiver);

        unbindService(mConnection);
        ObiLog.d(TAG, "unbind music service");
    }

//    public static Context getContext() {
//        return MusicApplication.mContext;
//    }
    
    public void bindMusicService() {
        /* player start */
        if(mService != null) {
            if (mListener != null) {
                mListener.onServiceConnected();
            }
        } else {
            boolean b = bindService(new Intent(PLAYER_SERVICE), mConnection, BIND_AUTO_CREATE);
            ObiLog.e(TAG, "bind music service : " + b);
        }
    }

    public static boolean enableLog() {
        return mLogEnable;
    }

    public static PlayerService getPlayer() throws Exception {
        if (mService != null) {
            return mService;
        } else {
            ObiLog.e(TAG, "Player Service is NULL");
            throw new Exception("Player Service is NULL.");
        }
    }

    public interface MusicServiceConnection {
        void onServiceConnected();
    }

    public static void setListener(MusicServiceConnection connection) {
        mListener = connection;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.e("test", "onServiceConnected");
            
            if(service instanceof PlayerBinder) {
                
                Log.e("test", "PlayerBinder");
                
                PlayerBinder binder = (PlayerBinder) service;
                mService = binder.getService();
            
                if (mListener != null) {
                    Log.e("test", "onServiceConnected");
                    mListener.onServiceConnected();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }

    };

    public static boolean isRegulation() {
        if (Preference.getDebugMode())
            return false;
        else {
            try {
                return (mDriving.getRegulationStatus() > 0) ? true : false;
            } catch (RemoteException e) {
                return false;
            }
        }
    }
    
    private Activity mCurrentActivity = null;
    public Activity getCurrentActivity(){
          return mCurrentActivity;
    }
    
    public void setCurrentActivity(Activity activity){
          this.mCurrentActivity = activity;
    }
    
    private static RemoteWebLinkServer mServerWL = null;
    
    public static RemoteWebLinkServer getServerWL() {
    	return mServerWL;
    }
    
    public static void setServerWL(RemoteWebLinkServer serverWL) {
    	mServerWL = serverWL;
    }
}
