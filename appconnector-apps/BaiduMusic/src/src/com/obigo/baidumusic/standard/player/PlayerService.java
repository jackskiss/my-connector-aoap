package com.obigo.baidumusic.standard.player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.baidu.music.model.Music;
import com.baidu.music.player.StreamPlayer;
import com.baidu.music.player.StreamPlayer.OnBufferingUpdateListener;
import com.baidu.music.player.StreamPlayer.OnCompletionListener;
import com.baidu.music.player.StreamPlayer.OnErrorListener;
import com.baidu.music.player.StreamPlayer.OnPreparedListener;
import com.hkmc.api.Const;
import com.hkmc.api.notify.ListenerSelect;
import com.hkmc.api.notify.Notify;
import com.hkmc.api.ui.HkmcOSD;
import com.obigo.baidumusic.standard.ConnReceiver;
import com.obigo.baidumusic.standard.MusicApplication;
import com.obigo.baidumusic.standard.R;
import com.obigo.baidumusic.standard.playlist.PlayListActivity;
import com.obigo.baidumusic.standard.util.NetworkChecker;
import com.obigo.baidumusic.standard.util.ObiLog;
import com.obigo.baidumusic.standard.util.Preference;
import com.obigo.baidumusic.standard.util.Util;
import com.obigo.baidumusic.standard.view.ObigoDialog;

public class PlayerService extends Service implements OnPreparedListener,
        OnBufferingUpdateListener, OnCompletionListener, ListenerSelect,
        OnAudioFocusChangeListener, OnErrorListener {
    private static final String TAG = "ObigoPlayer";

//    public static final int LOOP_NONE = 0;
    public static final int LOOP_ONE = 0;
    public static final int LOOP_ALL = 1;
//    public static final int LOOP_MAX = 3;
    public static final int LOOP_MAX = 2;

    public static final int STATUS_STOPPED = 0;
    public static final int STATUS_PLAYING = 1;
    public static final int STATUS_PAUSED = 2;

    private static final String PACKAGE_LAUNCHER = "com.android.launcher";
    private static final String PACKAGE_BAIDUMUSIC = "com.obigo.baidumusic";

    private boolean mShuffle;
    private int mLoop;

    private int mListType;

    private List<Music> mMusicList;
    private List<Integer> mShuffleList;
    private int mPlayPosition, mShufflePosition;
    private int mBufferingPercent;

    private final IBinder mBinder = new PlayerBinder();

    private StreamPlayer mStreamPlayer;
    private int mPlayerStatus = STATUS_STOPPED;

    private AudioManager mAudioManager = null;

    private PlayerListener mListner;

    private Notify mNotify;
    private boolean mRegistNotify;

    private boolean mLossAudioFocus;

    public class PlayerBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    private Timer mTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        ObiLog.i(TAG, "onCreate()");

        mShuffle = Preference.getBoolean(getApplicationContext(), Preference.KEY_SHUFFLE);
        mLoop = Preference.getInt(getApplicationContext(), Preference.KEY_LOOP);

        if (mLoop == Integer.MIN_VALUE) {
//            mLoop = LOOP_NONE;
            mLoop = LOOP_ALL;
        }

        mStreamPlayer = new StreamPlayer(this);
        mStreamPlayer.setAutoSave(false);
        mStreamPlayer.setOnPreparedListener(this);
        mStreamPlayer.setOnBufferingUpdateListener(this);
        mStreamPlayer.setOnCompletionListener(this);
        mStreamPlayer.setOnErrorListener(this);

        mTimer = null;

        if (!Preference.getDebugMode())
            mNotify = new Notify(getApplicationContext());
        mRegistNotify = false;

        mLossAudioFocus = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        ObiLog.i(TAG, "onStartCommand()");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ObiLog.i(TAG, "onDestroy()");

        if (mStreamPlayer != null) {
            mStreamPlayer.stop();
            mStreamPlayer.release();
        }

        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        ObiLog.i(TAG, "onBind()");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        ObiLog.i(TAG, "onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        ObiLog.i(TAG, "onRebind()");
        super.onRebind(intent);
    }

    public void requestAudioFocus() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        }

        if (mAudioManager != null) {
            if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager
                    .requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                            AudioManager.AUDIOFOCUS_GAIN)) {

                if (mLossAudioFocus) {
                    broadcastInfo(false);
                    resume();
                    mLossAudioFocus = false;
                }

                addModeKeyListener();
            }
        }
    }

    public int getListType() {
        return mListType;
    }

    public void setPlayList(int listType, List<Music> list) {

        if (mMusicList != null) {
            mMusicList = null;
        }

        if (mShuffleList != null) {
            mShuffleList = null;
        }
        
        if(list == null) {
            throw new IllegalArgumentException("list is NULL");
        }

        mListType = listType;
        mMusicList = list;
        mShuffleList = new ArrayList<Integer>();

        for (int i = 0; i < mMusicList.size(); i++) {
            mShuffleList.add(Integer.valueOf(i));
        }
        Collections.shuffle(mShuffleList);

        mPlayPosition = 0;
        mShufflePosition = 0;

        mPlayerStatus = STATUS_STOPPED;

        onPrePare(false);
    }

    public void releasePlayList() {
        mListType = ListDownloader.TYPE_UNKNOWN;
        mMusicList = null;
        mShuffleList = null;
        mPlayPosition = 0;
        mShufflePosition = 0;
    }

    public List<Music> getPlayList() {
        return mMusicList;
    }

    public Music getMusic(int pos) throws Exception {
        if (mMusicList == null) {
            ObiLog.e(TAG, "getMusic:MusicList is NULL");
            throw new Exception("MusicList is NULL");
        }

        if (pos < 0 || pos > mMusicList.size() - 1) {
            ObiLog.e(TAG, "getMusic:position overflow");
            throw new Exception("position overflow");
        }

        return mMusicList.get(pos);
    }

    public void removeMusic(int pos) throws Exception {
        if (mMusicList == null) {
            ObiLog.e(TAG, "getMusic:MusicList is NULL");
            throw new Exception("MusicList is NULL");
        }

        // if(pos < 0 || pos > mMusicList.size()-1) {
        // ObiLog.e(TAG, "getMusic:position overflow");
        // throw new Exception("position overflow");
        // }

        // mMusicList.remove(pos);

        mShuffleList = new ArrayList<Integer>();

        for (int i = 0; i < mMusicList.size(); i++) {
            mShuffleList.add(Integer.valueOf(i));
        }
        Collections.shuffle(mShuffleList);
    }

    public Music getCurrentMusic() {
        if (mMusicList == null) {
            return null;
        }

        if (mPlayPosition < 0 || mPlayPosition > mMusicList.size() - 1) {
            return null;
        }

        return mMusicList.get(mPlayPosition);
    }

    public int getCurrentPosition() {
        if (mMusicList == null) {
            return -1;
        }

        if (mPlayPosition < 0 || mPlayPosition > mMusicList.size() - 1) {
            return -1;
        }

        return mPlayPosition;
    }

    public void setStateShuffle(boolean stateShuffle) {
        Preference.putBoolean(getApplicationContext(), Preference.KEY_SHUFFLE, stateShuffle);
        mShuffle = stateShuffle;

        if (mShuffle && mShuffleList != null) {
            Collections.shuffle(mShuffleList);
            mShufflePosition = 0;
        }
    }

    public boolean getStateShuffle() {
        return mShuffle;
    }

    public void setStateLoop(int stateLoop) {
        Preference.putInt(getApplicationContext(), Preference.KEY_LOOP, stateLoop);
        mLoop = stateLoop;
    }

    public int getStateLoop() {
        return mLoop;
    }

    public int getPlayerStatus() {
        return mPlayerStatus;
    }

    public void play() throws Exception {
        
        Activity activity = ((MusicApplication)getApplicationContext()).getCurrentActivity();
        if(activity != null && !NetworkChecker.isConnected(activity)) {
            NetworkChecker.showError(activity);
            throw new Exception("Network fail");
        }
        
        if (mMusicList == null) {
            ObiLog.e(TAG, "play:MusicList is NULL");
            throw new Exception("MusicList is NULL");
        }

        if (mPlayPosition < 0 || mPlayPosition > mMusicList.size() - 1) {
            ObiLog.e(TAG, "play:position overflow");
            throw new Exception("position overflow");
        }

        Music music = mMusicList.get(mPlayPosition);

        ObiLog.startLoad("Play Music (id : " + music.mId.trim() + ")");

        // for cache
        // mStreamPlayer.play(Long.parseLong(music.mId.trim()), false);

        // for streaming
        mStreamPlayer.play(Long.parseLong(music.mId.trim()),
                StreamPlayer.PLAY_TYPE_URI);

        mPlayerStatus = STATUS_PLAYING;

        if (mListner != null) {
            mListner.onStartPlayer();
        }

        startProgressTimer();
    }

    public void pause() {
        mStreamPlayer.pause();
        mPlayerStatus = STATUS_PAUSED;

        if (mListner != null) {
            mListner.onPausePlayer();
        }

        stopProgressTimer();
    }
    
    public boolean resume() {
        
        Activity activity = ((MusicApplication)getApplicationContext()).getCurrentActivity();

        if(activity != null && !NetworkChecker.isConnected(activity)) {
            NetworkChecker.showError(activity);
            return false;
        }

        mStreamPlayer.resume();
        mPlayerStatus = STATUS_PLAYING;

        if (mListner != null) {
            mListner.onResumePlayer();
        }
        startProgressTimer();
        return true;
    }

    public void stop() {
        mStreamPlayer.stop();
        mPlayerStatus = STATUS_STOPPED;

        if (mListner != null) {
            mListner.onStopPlayer();
        }

        stopProgressTimer();
    }

    public int duration() {
        return mStreamPlayer.duration();
    }

    public int position() {
        return mStreamPlayer.position();
    }

    public int buffering() {
        return (mBufferingPercent * mStreamPlayer.duration()) / 100;
    }

    public int seek(int postion) {
        return mStreamPlayer.seek(postion);
    }

    private void playNext() {
        stop();
        broadcastInfo(false);
        
        if (mMusicList == null || mMusicList.size() == 0) {
            stop();
            releasePlayList();
            return;
        }

        /* radio shuffle issue */
        int tempLoop = mLoop;
        boolean tempShuffle = mShuffle;

        if (mListType == ListDownloader.TYPE_RADIO) {
            mLoop = LOOP_ALL;
            mShuffle = false;
        }

        try {
            switch (mLoop) {
//            case LOOP_NONE:
//                if (mShuffle) {
//                    if (mShufflePosition == mShuffleList.size() - 1) {
//                        stop();
//                        return;
//                    } else {
//                        mShufflePosition++;
//                        mPlayPosition = mShuffleList.get(mShufflePosition)
//                                .intValue();
//                    }
//                } else {
//                    if (mPlayPosition == mMusicList.size() - 1) {
//                        stop();
//                        return;
//                    } else {
//                        mPlayPosition++;
//                        mShufflePosition = mShuffleList.indexOf(Integer
//                                .valueOf(mPlayPosition));
//                    }
//                }
//                break;
            case LOOP_ONE:
                break;
            case LOOP_ALL:
                if (mShuffle) {
                    mShufflePosition = (++mShufflePosition)
                            % mShuffleList.size();
                    mPlayPosition = mShuffleList.get(mShufflePosition)
                            .intValue();
                } else {
                    mPlayPosition = (++mPlayPosition) % mMusicList.size();
                    mShufflePosition = mShuffleList.indexOf(Integer
                            .valueOf(mPlayPosition));
                }
                break;
            default:
                break;
            }

            onPrePare(false);

            play();
        } catch (Exception e) {
            ObiLog.e(TAG, "playNext error : ", e);

            stop();
        }

        /* radio shuffle issue */
        if (mListType == ListDownloader.TYPE_RADIO) {
            mLoop = tempLoop;
            mShuffle = tempShuffle;
        }
    }

    public void moveNext(boolean isOSD) {
        stop();
        broadcastInfo(false);
        
        if (mMusicList == null || mMusicList.size() == 0) {
            stop();
            releasePlayList();
            return;
        }

        /* radio shuffle issue */
        int tempLoop = mLoop;
        boolean tempShuffle = mShuffle;

        if (mListType == ListDownloader.TYPE_RADIO) {
            mLoop = LOOP_ALL;
            mShuffle = false;
        }

        if (mShuffle) {
            if (mShufflePosition < 0
                    || mShufflePosition > mShuffleList.size() - 1) {
                mShufflePosition = 0;
            }

            mShufflePosition = (++mShufflePosition) % mShuffleList.size();
            mPlayPosition = mShuffleList.get(mShufflePosition).intValue();
        } else {
            if (mPlayPosition < 0 || mPlayPosition > mMusicList.size() - 1) {
                mPlayPosition = 0;
            }

            mPlayPosition = (++mPlayPosition) % mMusicList.size();
            mShufflePosition = mShuffleList.indexOf(Integer
                    .valueOf(mPlayPosition));
        }

        onPrePare(isOSD);

        try {
            play();
        } catch (Exception e) {
            ObiLog.e(TAG, "moveNext error : ", e);
            stop();
        }

        /* radio shuffle issue */
        if (mListType == ListDownloader.TYPE_RADIO) {
            mLoop = tempLoop;
            mShuffle = tempShuffle;
        }

        ObiLog.d(TAG, String.format(
                "moveNext : shuffle = %s, play_pos = %d, shuffle_pos = %d",
                mShuffle, mPlayPosition, mShufflePosition));
    }

    public void movePrev(boolean isOSD) {
        stop();
        broadcastInfo(false);
        
        if (mMusicList == null || mMusicList.size() == 0) {
            stop();
            releasePlayList();
            return;
        }

        if (mListType == ListDownloader.TYPE_RADIO) {
            return;
        }

        /* radio shuffle issue */
        int tempLoop = mLoop;
        boolean tempShuffle = mShuffle;

        if (mListType == ListDownloader.TYPE_RADIO) {
            mLoop = LOOP_ALL;
            mShuffle = false;
        }

        if (mShuffle) {
            --mShufflePosition;
            if (0 > mShufflePosition) {
                mShufflePosition += mShuffleList.size();
            }
            mPlayPosition = mShuffleList.get(mShufflePosition).intValue();
        } else {
            --mPlayPosition;
            if (0 > mPlayPosition) {
                mPlayPosition += mMusicList.size();
            }
            mShufflePosition = mShuffleList.indexOf(Integer
                    .valueOf(mPlayPosition));
        }

        onPrePare(isOSD);

        try {
            play();
        } catch (Exception e) {
            ObiLog.e(TAG, "movePrev error : ", e);
            stop();
        }

        /* radio shuffle issue */
        if (mListType == ListDownloader.TYPE_RADIO) {
            mLoop = tempLoop;
            mShuffle = tempShuffle;
        }

        ObiLog.d(TAG, String.format(
                "moveNext : shuffle = %s, play_pos = %d, shuffle_pos = %d",
                mShuffle, mPlayPosition, mShufflePosition));
    }

    public void preparePlay(int pos) throws Exception {
        setPlayPosition(pos);

        onPrePare(false);
        ObiLog.d(TAG, String.format(
                "setPlayPosition : play_pos = %d, shuffle_pos = %d",
                mPlayPosition, mShufflePosition));
    }

    private void onPrePare(boolean isOSD) {

        File f = new File(Preference.getString(getApplicationContext(), Preference.KEY_THUMBNAIL));
        if (f.exists() && f.isFile()) {
            if(!f.delete()) {
                ObiLog.e(TAG, "onPrePare:file not deleted.");
            }
            Preference.putString(getApplicationContext(), Preference.KEY_THUMBNAIL, "");
        }

        broadcastInfo(isOSD);

        if (mStreamPlayer.isPlaying()) {
            mStreamPlayer.stop();
        }

        if (mListner != null) {
            mListner.onPreparePlayer(mMusicList.get(mPlayPosition),
                    mPlayPosition);
        }
    }

    public void setPlayPosition(int pos) throws Exception {
        if (mMusicList == null) {
            ObiLog.e(TAG, "setPlayPosition:MusicList is NULL");
            throw new Exception("MusicList is NULL");
        }

        if (mMusicList.size() == 0) {
            pos = 0;
        } else {
            if (pos < 0) {
                pos = 0;
            }

            if (pos > mMusicList.size() - 1) {
                pos = mMusicList.size() - 1;
            }
        }

        mPlayPosition = pos;
        mShufflePosition = mShuffleList.indexOf(Integer.valueOf(mPlayPosition));
    }

    public void setOnPlayerListener(PlayerListener listner) {
        mListner = listner;
    }

    @Override
    public void onCompletion() {
        ObiLog.d(TAG, "onCompletion");

        File f = new File(Preference.getString(getApplicationContext(), Preference.KEY_THUMBNAIL));

        if (f.exists() && f.isFile()) {
            if(!f.delete()) {
                ObiLog.e(TAG, "onPrePare:file not deleted.");
            }
            Preference.putString(getApplicationContext(), Preference.KEY_THUMBNAIL, "");
        }

        playNext();
    }

    @Override
    public void onBufferingEnd() {
        ObiLog.d(TAG, "onBufferingEnd");

        if (mListner != null) {
            mListner.onUpdateLoading(mStreamPlayer.duration(),
                    mStreamPlayer.duration());
        }

        // test code
        // mStreamPlayer.seek(mStreamPlayer.duration()-10000);
    }

    @Override
    public void onBufferingUpdate(int percent) {
        // ObiLog.d(TAG, "" + percent);

        mBufferingPercent = percent;

        if (mListner != null) {
            mListner.onUpdateLoading(mStreamPlayer.duration(),
                    (percent * mStreamPlayer.duration()) / 100);
            // ObiLog.d(TAG, "duration : " + mStreamPlayer.duration() +
            // " percent : " + percent);
        }
    }

    @Override
    public void onPrepared() {
        ObiLog.d(TAG, "onPrepared");
        if (mPlayerStatus == STATUS_PLAYING && !mStreamPlayer.isPlaying()) {

            ObiLog.endLoad("Play Music (id : " + getCurrentMusic().mId.trim()
                    + ")");

            mStreamPlayer.start();

            broadcastInfo(false);
            ObiLog.d(TAG, "player start");
        }
    }

    private void startProgressTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }

        mTimer = new Timer();

        TimerTask mTask = new TimerTask() {

            @Override
            public void run() {
                if (mStreamPlayer != null && mPlayerStatus == STATUS_PLAYING) {
                    int position = mStreamPlayer.position();
                    int duration = mStreamPlayer.duration();

//                    ObiLog.d(TAG, "duration : " + duration + " position : "
//                            + position);

                    if (duration > 0) {

                        if (mListner != null) {
                            mListner.onUpdateProgress(duration, position);
                        }

                        if (mListType != ListDownloader.TYPE_RADIO) {
                            Intent i = new Intent("download3.timeupdate");
                            i.putExtra("time_change",
                                    Util.formatDuration((long) position));
                            i.putExtra("time_bar_change", 1000 * position
                                    / duration);
                            i.putExtra("audio_current_time", (long) position);
                            i.putExtra("audio_total_time", (long) duration);
                            sendBroadcast(i);
                        }
                    }
                }
            }
        };

        mTimer.schedule(mTask, 1000, 1000);
    }

    private void stopProgressTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    public void broadcastInfo(boolean isOSD) {
        Context context = getApplicationContext();
        Music m = getCurrentMusic();

        if (m != null) {

            Intent intent = new Intent();

            if (mListType == ListDownloader.TYPE_RADIO) {
                intent.setAction("download4.widgetupdate");
            } else {
                intent.setAction("download3.widgetupdate");

                intent.putExtra("start_time", (long) mStreamPlayer.position());
                intent.putExtra("end_time", (long) mStreamPlayer.duration());
            }

            intent.putExtra("mode", getResources().getString(R.string.app_name));
            intent.putExtra("filename", m.mArtist);
            intent.putExtra("station_name", m.mTitle);
            intent.putExtra("color", false);
            intent.putExtra("packagename", "com.obigo.baidumusic.standard");
            intent.putExtra("classname",
                    "com.obigo.baidumusic.standard.MainActivity");

            String path = Preference.getString(getApplicationContext(), Preference.KEY_THUMBNAIL);

            if (path != null && path.length() > 0) {
                File thumb = new File(path);

                if (thumb.exists() && thumb.isFile()) {
                    intent.putExtra("artwork_strPath", path);
                }
            }

            sendBroadcast(intent);

            if (isOSD) {
                ActivityManager am = (ActivityManager) context
                        .getSystemService(Context.ACTIVITY_SERVICE);
                List<RunningTaskInfo> Info = am.getRunningTasks(1);
                ComponentName topActivity = Info.get(0).topActivity;
                String packageName = topActivity.getPackageName();

                if (!packageName.equals(PACKAGE_LAUNCHER)
                        && !packageName.startsWith(PACKAGE_BAIDUMUSIC)) {
                    PendingIntent contentIntent = PendingIntent.getActivity(
                            context, 0, null, 0);
                    HkmcOSD mOSD = new HkmcOSD(context);
                    mOSD.OneShotOSD(1, "("+getResources().getString(R.string.app_name)+") " + m.mTitle + " - "
                            + m.mArtist, contentIntent);
                }
            }
        }
    }
    
    private long mBefore;

    @Override
    public void onListenerValueInfoChanged(int arg0, int arg1, Bundle arg2) {
        switch (arg0) {
        case Const.NOTIFY_LISTENER_MODEKEY:
            if (arg1 == Const.NOTIFY_EVENT_MODEKEY_KEY) {
                int keyAction = arg2.getInt(Const.NOTIFY_VALUE_MODE_KEY_ACTION);
                int keyCode = arg2.getInt(Const.NOTIFY_VALUE_MODE_KEY_KEY_CODE);

                if (keyAction == 1 /* KEY_UP */
                        && mPlayerStatus != STATUS_STOPPED) {
                    
                    long time = System.currentTimeMillis();
                    
                    if(time-mBefore < PlayListActivity.BUTTON_DELAY) {
                        return;
                    }
                    mBefore = time;

                    ActivityManager activityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
                    List<RunningTaskInfo> info = activityManager.getRunningTasks(1);
                    String topActivity = info.get(0).topActivity.getShortClassName();
//                    ObiLog.d(TAG, "topActivity: " + topActivity);

                    switch (keyCode) {

                    case Const.KEYCODE_TUNE_DOWN:
                    case Const.KEYCODE_SEEK_DOWN:
                        if (ConnReceiver.isBackGroundMode()) {
                            movePrev(true);
                        } else if (topActivity.equalsIgnoreCase(".MainActivity")) {
                            movePrev(false);
                        } else {
                            moveNext(false);
                        }
                        break;
                    case Const.KEYCODE_TUNE_UP:
                    case Const.KEYCODE_SEEK_UP:
                        if (ConnReceiver.isBackGroundMode())
                            moveNext(true);
                        else if (topActivity.equalsIgnoreCase(".MainActivity"))
                            moveNext(false);
                        else
                            movePrev(false);
                        break;
                    default:
                        break;
                    }
                }
            }
            break;
        default:
            break;
        }

    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
        case AudioManager.AUDIOFOCUS_LOSS:
            if (mStreamPlayer != null) {
                mLossAudioFocus = true;
                pause();
                removeModeKeyListener();
            }
            ObiLog.d(TAG, "AudioFocus: received AUDIOFOCUS_LOSS");
            break;
        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
            ObiLog.d(TAG, "AudioFocus: received AUDIOFOCUS_LOSS_TRANSIENT");
            break;
        case AudioManager.AUDIOFOCUS_GAIN:
            if (mStreamPlayer != null) {
                if (mLossAudioFocus) {
                    resume();
                    mLossAudioFocus = false;
                }

                addModeKeyListener();
            }
            ObiLog.d(TAG, "AudioFocus: received AUDIOFOCUS_GAIN");
            break;
        default:
            ObiLog.e(TAG, "Unknown audio focus change code");
            break;
        }

    }

    @Override
    public boolean onError(int arg0) {
        ObiLog.e(TAG, "onError : " + arg0);
        
        Activity activity = ((MusicApplication)getApplicationContext()).getCurrentActivity();
        int errorMsg;
        
        if (arg0 == 1)
        	errorMsg = R.string.warning_network_connection_fail;
        else if (arg0 == 100)
        	errorMsg = R.string.warning_no_response_from_server;
        else if (arg0 == -1004)
        	errorMsg = R.string.warning_network_connection_fail;
        else if (arg0 == -1007 || arg0 == -1010)
        	errorMsg = R.string.warning_media_format_is_not_supported;
        else if (arg0 == -110)
        	errorMsg = R.string.warning_wait_time_is_expired;
        else
        	errorMsg = R.string.warning_no_response_from_server;
        
        if(activity != null) {
            ObigoDialog.builder(activity, null);
            ObigoDialog.setButton(R.string.btn_ok);            
            ObigoDialog.setTitleContent(R.string.warning, errorMsg);
            ObigoDialog.show();
        }
        
        stop();
        return true;
    }

    private void addModeKeyListener() {
        if (!Preference.getDebugMode()) {
            try {
                if (!mRegistNotify) {
                    mNotify.addListener(Const.NOTIFY_LISTENER_MODEKEY, this);
                    mRegistNotify = true;
                }
            } catch (RemoteException e) {
                ObiLog.e(TAG, "addModeKeyListener error.");
            }
        }
    }

    private void removeModeKeyListener() {
        if (!Preference.getDebugMode()) {
            try {
                mNotify.removeListener(Const.NOTIFY_LISTENER_MODEKEY, this);
                mRegistNotify = false;
            } catch (RemoteException e) {
                ObiLog.e(TAG, "removeModeKeyListener error.");
            }
        }
    }
}
