package com.obigo.baidumusic.standard.search;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;

import com.baidu.music.model.Music;
import com.obigo.baidumusic.standard.MusicApplication;
import com.obigo.baidumusic.standard.R;
import com.obigo.baidumusic.standard.player.ListDownloader;
import com.obigo.baidumusic.standard.player.PlayerListener;
import com.obigo.baidumusic.standard.player.PlayerService;
import com.obigo.baidumusic.standard.playlist.PlayListActivity;
import com.obigo.baidumusic.standard.playlist.SongItemAdapter;
import com.obigo.baidumusic.standard.util.ObiLog;

public class SearchListActivity extends PlayListActivity implements
        PlayerListener {
    private final String TAG = "SearchListActivity";

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
    protected void onResume() {
        super.onResume();

        try {
            MusicApplication.getPlayer().requestAudioFocus();
        } catch (Exception e) {
            ObiLog.e(TAG, "onResume::getPlayer error : ", e);
        }
    }

    @Override
    public void getPlaylist() {
        
        ListDownloader.getInstance(this).setType(ListDownloader.TYPE_SEARCH);
        
        try {
            Intent intent = getIntent();

            @SuppressWarnings("unchecked")
            List<Music> items = (ArrayList<Music>) intent
                    .getSerializableExtra("ListObject");

            if (!equalList(items)) {
                getPlayer().stop();
                getPlayer().releasePlayList();

                if (!isRegulation()) {
                    mAdapter = new SongItemAdapter(this,
                            R.layout.playlist_item, items, getListView());
                    getListView().setAdapter(mAdapter);
                }

                getPlayer().setPlayList(getListType(), items);

                play();
            } else {

                Music music = getPlayer().getCurrentMusic();
                items = getPlayer().getPlayList();

                if (!isRegulation()) {
                    mAdapter = new SongItemAdapter(this,
                            R.layout.playlist_item, items, getListView());
                    getListView().setAdapter(mAdapter);
                    getListView()
                            .setSelection(getPlayer().getCurrentPosition());
                }

                setDetail(music);

                if (getPlayer().getPlayerStatus() == PlayerService.STATUS_PLAYING) {
                    this.playAniShow(true);
                } else {
                    this.playAniShow(false);
                }
            }

            if (items != null && items.size() == 1) {
                if (!isRegulation()) {
                    getShuffleButton().setEnabled(false);
                }
                getPrevButton().setEnabled(false);
                getNextButton().setEnabled(false);
            }

        } catch (Exception e) {
            ObiLog.e(TAG, "getPlaylist : ", e);
        }
    }
    
    private boolean equalList(List<Music> items) {
        
        List<Music> prevItems = null;
        
        if(items == null) {
            return true;
        }
        
        try {
            prevItems = getPlayer().getPlayList();
        } catch (Exception e) {
            ObiLog.e(TAG, "prevItem is NULL");
        }
        
        if(prevItems == null) {
            return false;
        }
        
        if(items.size() != prevItems.size()) {
            return false;
        }
        
        for(int i=0;i<items.size();i++) {
            if(!items.get(i).mId.equals(prevItems.get(i).mId)) {
                return false;
            }
        }
                
        return true;
    }

    @Override
    public void onPreparePlayer(Music music, int position) {
        setListPosition(position);

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
        this.playAniShow(false);
        setProgressTime();
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
}
