package com.obigo.baidumusic.standard.playlist;

import java.util.Calendar;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;

import com.baidu.music.model.Channel;
import com.baidu.music.model.Music;
import com.baidu.music.model.Topic;
import com.obigo.baidumusic.standard.R;
import com.obigo.baidumusic.standard.player.ListDownloader;
import com.obigo.baidumusic.standard.player.ListDownloader.ListDownloadListener;
import com.obigo.baidumusic.standard.player.PlayerListener;
import com.obigo.baidumusic.standard.player.PlayerService;
import com.obigo.baidumusic.standard.playlist.PlayListActivity.ViewChangeListener;
import com.obigo.baidumusic.standard.util.Holidays;
import com.obigo.baidumusic.standard.util.ObiLog;
import com.obigo.baidumusic.standard.util.Preference;
import com.obigo.baidumusic.standard.view.ObigoDialog;

public class RadioListActivity extends PlayListActivity implements
        PlayerListener, ListDownloadListener, ViewChangeListener {
    private final String TAG = "RadioListActivity";

    private static final String CID_HOLIDAY = "16";
    private static final String CID_MORNING = "29";
    private static final String CID_AFTERNOON = "19";
    private static final String CID_EVENING = "6";

    private static final int HOUR_MORNING = 6;
    private static final int HOUR_AFTERNOON = 12;
    private static final int HOUR_EVENING = 18;

    private RadioItemAdapter mAdapter;
    private ListDownloader mDownloader;
    private String mRadioPicture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mDownloader = ListDownloader.getInstance(this);
        setOnViewChangeListener(this);

        try {
            getPlayer().setOnPlayerListener(this);
        } catch (Exception e) {
            ObiLog.e(TAG, "setOnPlayerListener error : ", e);
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewChanged(boolean isRegulation) {
        if (!isRegulation) {
            getShuffleButton().setEnabled(false);
            getLoopButton().setEnabled(false);
        }

        getPrevButton().setEnabled(false);
    }
    
    private void showErrorPopup(int msg, int a) {
    	ObigoDialog.builder(this, new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.button_left) {
                    finish();
                }
            }
        });
        ObigoDialog.setButton(R.string.btn_ok);
        ObigoDialog.setTitleContent(msg, a);
        ObigoDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void getPlaylist() {
        showLoading();
        isLoadingList = true;
        mDownloader.requestList(getListType(), this);
        
        Handler stopHandler = new Handler() {
        	public void handleMessage(Message msg) {
        		if (isLoadingList == true) {
        			hideLoading();
        			isLoadingList = false;
        			showErrorPopup(R.string.warning, R.string.please_try_again);        			
        		}
        	}
        };        
        stopHandler.sendEmptyMessageDelayed(0, Preference.waitTimeForLoadingList);
    }

    public static int selectDefaultChannel(List<Channel> list) {

        String cid;

        Calendar calendar = Calendar.getInstance();

        if (Holidays.isContain(calendar)) {
            // holiday
            cid = CID_HOLIDAY;
        } else {

            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            if (hour >= HOUR_MORNING && hour < HOUR_AFTERNOON) {
                // morning
                cid = CID_MORNING;
            } else if (hour >= HOUR_AFTERNOON && hour < HOUR_EVENING) {
                // afternoon
                cid = CID_AFTERNOON;
            } else {
                // evening
                cid = CID_EVENING;
            }
        }

        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).mChannelId.equals(cid)) {
                return i;
            }
        }

        return 0;
    }

    @Override
    public void onItemClick(AdapterView<?> parentView, View clickedView,
            int position, long id) {
        Channel channel = mAdapter.getItem(position);
        playChannel(channel);

        mDownloader.setChannel(position);
        mAdapter.setChannelPosition(position);
    }

    private void playChannel(Channel channel) {

        showLoading();

        mRadioPicture = channel.mThumb;
        requestAlbumArt(mRadioPicture);

        mDownloader.requestRadioList(channel);
    }

    @Override
    public void onPreparePlayer(Music music, int position) {
        setTitle(music.mTitle);
        setArtist(music.mArtist);

        requestAlbumArt(mRadioPicture);

        setProgressTime();
        this.playAniShow(false);
        this.favBtnStatusSwitch(music);
    }

    @Override
    public void onStartPlayer() {
        this.playAniShow(true);
    }

    @Override
    public void onPausePlayer() {
        this.playAniShow(false);

    }

    @Override
    public void onResumePlayer() {
        this.playAniShow(true);

    }

    @Override
    public void onStopPlayer() {
        this.playAniShow(false);
        setProgressTime();
    }

    @Override
    public void onUpdateProgress(int duration, int position) {
        setProgressTime();
    }

    @Override
    public void onUpdateLoading(int duration, int position) {
    }

    @Override
    public void onDownloadChannelList(int type, List<?> list, int selectedChannel) {
    	
    	isLoadingList = false;
    	
    	if (list == null || list.size() == 0) {
        	hideLoading();
        	showErrorPopup(R.string.warning, R.string.please_try_again);
        }
    	
        if (getListType() == type) {
            @SuppressWarnings("unchecked")
            List<Channel> channels = (List<Channel>) list;
    
            if (!isRegulation()) {
                mAdapter = new RadioItemAdapter(this, R.layout.playlist_item,
                        channels);
                getListView().setAdapter(mAdapter);
            }
    
            int idx;
    
            if (selectedChannel > 0) {
                idx = selectedChannel;
                Channel channel = channels.get(idx);
                mRadioPicture = channel.mThumb;
                requestAlbumArt(mRadioPicture);
            } else {
                idx = selectDefaultChannel(channels);
                Channel channel = channels.get(idx);
                playChannel(channel);
                mDownloader.setChannel(idx);
            }
    
            if (!isRegulation()) {
                getListView().setSelection(idx);
                mAdapter.setChannelPosition(idx);
            }
        }
    }

    @Override
    public void onDownloadTheme(int type, Topic theme) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDownloadMusicList(int type, boolean isSeamless, List<Music> list) {
        if (getListType() == type) {
            if (!isSeamless) {
                try {
                    getPlayer().setPlayList(ListDownloader.TYPE_RADIO, list);
                    play();
                } catch (Exception e) {
                    ObiLog.e(TAG, "onDownloadMusicList() error : ", e);
                }
            } else {
                try {
                    Music music = getPlayer().getCurrentMusic();
    
                    setTitle(music.mTitle);
                    setArtist(music.mArtist);
                    
                    setProgressTime();
    
                    if (getPlayer().getPlayerStatus() == PlayerService.STATUS_PLAYING) {
                        this.playAniShow(true);
                    } else {
                        this.playAniShow(false);
                    }
                } catch (Exception e) {
                    ObiLog.e(TAG, "onDownloadMusicList() error : ", e);
                }
            }   
        }

        hideLoading();
    }

    @Override
    public void onError() {
        // TODO Auto-generated method stub

    }
}
