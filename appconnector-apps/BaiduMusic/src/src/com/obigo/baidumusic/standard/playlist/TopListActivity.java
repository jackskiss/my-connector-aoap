package com.obigo.baidumusic.standard.playlist;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.baidu.music.model.Music;
import com.baidu.music.model.Topic;
import com.obigo.baidumusic.standard.R;
import com.obigo.baidumusic.standard.player.ListDownloader;
import com.obigo.baidumusic.standard.player.ListDownloader.ListDownloadListener;
import com.obigo.baidumusic.standard.player.PlayerListener;
import com.obigo.baidumusic.standard.player.PlayerService;
import com.obigo.baidumusic.standard.util.ObiLog;
import com.obigo.baidumusic.standard.util.Preference;
import com.obigo.baidumusic.standard.view.ObigoDialog;

public class TopListActivity extends PlayListActivity implements
        PlayerListener, ListDownloadListener {
    private final String TAG = "TopListActivity";

    private SongItemAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            getPlayer().setOnPlayerListener(this);
        } catch (Exception e) {
            ObiLog.e(TAG, "onCreate() error : ", e);
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
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
    public void getPlaylist() {
        showLoading();
        isLoadingList = true;
        ListDownloader downloader = ListDownloader.getInstance(this);
        downloader.requestList(getListType(), this);
        
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

    @Override
    public void onPreparePlayer(Music music, int position) {

        if (!isRegulation()) {
            setListPosition(position);
        }

        setDetail(music);
        this.playAniShow(false);
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
        setProgressTime();
        this.playAniShow(false);
    }

    private void setDetail(Music music) {
        setTitle(music.mTitle);
        setArtist(music.mArtist);

        requestAlbumArt(music);

        setProgressTime();

        this.favBtnStatusSwitch(music);
    }

    @Override
    public void onUpdateProgress(int duration, int position) {
        setProgressTime();
    }

    @Override
    public void onUpdateLoading(int duration, int position) {
    }

    @Override
    public void onDownloadMusicList(int type, boolean isSeamless,
            List<Music> list) {
    	
    	isLoadingList = false;
    	
    	if (list == null || list.size() == 0) {
        	hideLoading();
        	showErrorPopup(R.string.warning, R.string.please_try_again);
        }

        if (getListType() == type) {

            try {
                if (!isRegulation()) {
                    mAdapter = new SongItemAdapter(this,
                            R.layout.playlist_item, list, getListView());
                    getListView().setAdapter(mAdapter);
                }

                if (isSeamless) {
                    Music music = getPlayer().getCurrentMusic();

                    if (!isRegulation()) {
                        getListView().setSelection(
                                getPlayer().getCurrentPosition());
                    }

                    setDetail(music);

                    if (getPlayer().getPlayerStatus() == PlayerService.STATUS_PLAYING) {
                        this.playAniShow(true);
                    } else {
                        this.playAniShow(false);
                    }
                } else {
                    getPlayer().setPlayList(getListType(), list);
                    play();
                }

            } catch (Exception e) {
                ObiLog.e(TAG, "onDownloadMusicList error : ", e);
            }
        }

        hideLoading();
    }

    @Override
    public void onError() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDownloadChannelList(int type, List<?> list,
            int selectedChannel) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDownloadTheme(int type, Topic theme) {
        // TODO Auto-generated method stub

    }
}
