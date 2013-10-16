package com.obigo.baidumusic.standard.playlist;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.baidu.music.model.Music;
import com.baidu.music.model.Topic;
import com.obigo.baidumusic.standard.MusicApplication;
import com.obigo.baidumusic.standard.R;
import com.obigo.baidumusic.standard.player.FavoriteList;
import com.obigo.baidumusic.standard.player.ListDownloader;
import com.obigo.baidumusic.standard.player.ListDownloader.ListDownloadListener;
import com.obigo.baidumusic.standard.player.PlayerListener;
import com.obigo.baidumusic.standard.player.PlayerService;
import com.obigo.baidumusic.standard.playlist.PlayListActivity.ViewChangeListener;
import com.obigo.baidumusic.standard.search.SearchActivity;
import com.obigo.baidumusic.standard.util.ObiLog;
import com.obigo.baidumusic.standard.view.MenuButton;
import com.obigo.baidumusic.standard.view.ObigoDialog;

public class FavoriteListActivity extends PlayListActivity implements
        PlayerListener, ListDownloadListener, ViewChangeListener,
        com.obigo.baidumusic.standard.view.MenuButton.OnCheckedChangeListener,
        OnDismissListener {

    private final String TAG = "FavoriteListActivity";

    private FavoriteItemAdapter mAdapter;

    private MenuButton mBtnMenu;
    private PopupWindow mMenu;

    private Button mBtnDelete, mBtnDeleteAll, mBtnTop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setOnViewChangeListener(this);
        try {
            getPlayer().setOnPlayerListener(this);
        } catch (Exception e) {
            ObiLog.e(TAG, "getPlayer error : ", e);
        }

        View popupView = View.inflate(this, R.layout.popup_favorite, null);

        View v1 = popupView.findViewById(R.id.delete);
        if(v1 instanceof Button) {
            mBtnDelete = (Button)v1;
            mBtnDelete.setOnClickListener(this);
        }

        View v2 = popupView.findViewById(R.id.delete_all);
        if(v2 instanceof Button) {
            mBtnDeleteAll = (Button)v2;
            mBtnDeleteAll.setOnClickListener(this);
        }
        
        (popupView.findViewById(R.id.search)).setOnClickListener(this);
        
        View v3 = popupView.findViewById(R.id.top);
        if(v3 instanceof Button) {
            mBtnTop = (Button)v3;
            mBtnTop.setOnClickListener(this);
        }

        mMenu = new PopupWindow(popupView, 190, 176, true);
        mMenu.setOutsideTouchable(true);
        mMenu.setBackgroundDrawable(new BitmapDrawable());
        mMenu.setOnDismissListener(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewChanged(boolean isRegulation) {
        if (!isRegulation) {
            try {
                mBtnMenu = findMenuButton(R.id.menu_button);
                mBtnMenu.setOnCheckedChangeListener(this);
                mBtnMenu.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                ObiLog.e(TAG, "findMenuButton error");
            }
        }
        
        getFavoriteButton().setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        FavoriteList.getInstance(this).saveMusicList();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void getPlaylist() {
        ListDownloader downloader = ListDownloader.getInstance(this);
        downloader.requestList(getListType(), this);
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

    @Override
    public void onUpdateProgress(int duration, int position) {
        setProgressTime();
    }

    @Override
    public void onUpdateLoading(int duration, int position) {
    }

    private void setDetail(Music music) {
        setTitle(music.mTitle);
        setArtist(music.mArtist);

        requestAlbumArt(music);

        setProgressTime();
    }

    @SuppressWarnings("static-access")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.search:
            startActivity(new Intent(FavoriteListActivity.this,
                    SearchActivity.class));
            finish();
            break;
        case R.id.delete:
            mMenu.dismiss();

            try {
                if (getPlayer().getPlayerStatus() == PlayerService.STATUS_PLAYING) {
                    ObigoDialog
                            .builder(FavoriteListActivity.this, null)
                            .setButton(R.string.btn_ok)
                            .setTitleContent(R.string.warning,
                                    R.string.edit_warning).show();
                } else {
                    showDeleteBtn(true);
                }
            } catch (Exception e) {
                ObiLog.e(TAG, "getPlayer error : ", e);
                showDeleteBtn(true);
            }
            break;
        case R.id.delete_all:
            mMenu.dismiss();

            try {
                if (getPlayer().getPlayerStatus() == PlayerService.STATUS_PLAYING) {
                    ObigoDialog
                            .builder(FavoriteListActivity.this, null)
                            .setButton(R.string.btn_ok)
                            .setTitleContent(R.string.warning,
                                    R.string.edit_warning).show();

                    return;
                }
            } catch (Exception e) {
                ObiLog.e(TAG, "getPlayer error : ", e);
                return;
            }

            ObigoDialog
                    .builder(FavoriteListActivity.this,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (v.getId() == R.id.button_left) {

                                        try {
                                            PlayerService player = MusicApplication
                                                    .getPlayer();
                                            player.stop();
                                            player.releasePlayList();
                                        } catch (Exception e) {
                                            ObiLog.e(TAG, "removeMusic error : ", e);
                                        }

                                        FavoriteList.getInstance(FavoriteListActivity.this)
                                                .removeAllMusic();
                                        mAdapter.notifyDataSetChanged();

                                        showDeleteBtn(false);
                                        updateFavoriteListUI();
                                    }
                                }
                            })
                    .setButtons(R.string.btn_yes, R.string.btn_no)
                    .setTitleContent(R.string.warning,
                            R.string.delete_all_warning).show();

            break;
        case R.id.top:
            mMenu.dismiss();

            try {
                int pos = getPlayer().getCurrentPosition();

                if (pos > 0) {
                    FavoriteList.getInstance(this).upToTop(pos);
                    mAdapter.notifyDataSetChanged();
                    getListView().setSelection(0);
                    getPlayer().setPlayPosition(0);
                }
            } catch (Exception e) {
                ObiLog.e(TAG, "onClick error : ", e);
            }
            break;
        default:
            super.onClick(view);
            break;
        }
    }

    public void showDeleteBtn(boolean iShow) {
        getShuffleButton().setEnabled(!iShow);
        getPrevButton().setEnabled(!iShow);
        getPlayButton().setEnabled(!iShow);
        getNextButton().setEnabled(!iShow);
        getLoopButton().setEnabled(!iShow);

        if (mAdapter != null) {
            mAdapter.setMode((iShow) ? FavoriteItemAdapter.MODE_EDIT
                    : FavoriteItemAdapter.MODE_PLAY);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void updateFavoriteListUI() {
        // int contentSize = (mAdapter == null) ? 0 : mAdapter.getCount();
        int contentSize;
        try {
            contentSize = getPlayer().getPlayList().size();
        } catch (Exception e) {
            contentSize = 0;
        }

        
        findViewById(R.id.no_content).setVisibility(
                (contentSize > 0) ? View.GONE : View.VISIBLE);
        
        if (contentSize == 0) {
        	this.finish();
        }
        
        findViewById(R.id.content).setVisibility(
                (contentSize > 0) ? View.VISIBLE : View.GONE);

        if(mAdapter != null && mAdapter.getMode() == FavoriteItemAdapter.MODE_EDIT) {
            if (!isRegulation()) {
                getShuffleButton().setEnabled(false);
                getLoopButton().setEnabled(false);
            }

            getPrevButton().setEnabled(false);
            getPlayButton().setEnabled(false);
            getNextButton().setEnabled(false);

        } else {
            if (!isRegulation()) {
                getShuffleButton().setEnabled(contentSize > 1);
                getLoopButton().setEnabled(contentSize > 0);
            }

            getPrevButton().setEnabled(contentSize > 1);
            getPlayButton().setEnabled(contentSize > 0);
            getNextButton().setEnabled(contentSize > 1);

        }
        
        mBtnTop.setEnabled(contentSize > 1);
        mBtnDelete.setEnabled(contentSize > 0);
        mBtnDeleteAll.setEnabled(contentSize > 0);
    }

    public void initDetail() {
        setTitle("");
        setArtist("");

        setProgressTime();
    }

    @Override
    public void onItemClick(AdapterView<?> parentView, View clickedView,
            int position, long id) {
        if (mAdapter.getMode() == FavoriteItemAdapter.MODE_PLAY) {
            super.onItemClick(parentView, clickedView, position, id);
        }
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

    @Override
    public void onDownloadMusicList(int type, boolean isSeamless, List<Music> list) {
        if (getListType() == type) {
            try {
                if (list != null && list.size() > 0) {
    
                    findViewById(R.id.content).setVisibility(View.VISIBLE);
                    findViewById(R.id.no_content).setVisibility(View.GONE);
                    
                    if (!isRegulation()) {
                        mAdapter = new FavoriteItemAdapter(this,
                                R.layout.favorite_item, list, getListView());
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
                        getPlayer().stop();
                        getPlayer().releasePlayList();
                        getPlayer().setPlayList(getListType(), list);
                        play();
                    }
                } else {
                    findViewById(R.id.content).setVisibility(View.GONE);
                    findViewById(R.id.no_content).setVisibility(View.VISIBLE);
    
                    getPlayer().stop();
                    getPlayer().releasePlayList();
                    getPlayer().setPlayList(getListType(), list);
                }
            } catch (Exception e) {
                ObiLog.e(TAG, "onDownloadMusicList error : ", e);
            }
        }

        updateFavoriteListUI();
    }

    @Override
    public void onError() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCheckedChanged(boolean isChecked) {
        if (isChecked) {
            if(mAdapter == null || mAdapter.getMode() != FavoriteItemAdapter.MODE_EDIT) {
                mMenu.showAtLocation(findViewById(R.id.header), Gravity.RIGHT | Gravity.TOP, 0, 108);
            } else {
                mBtnMenu.setChecked(false);
            }
            showDeleteBtn(false);
            updateFavoriteListUI();
        }
    }

    @Override
    public void onDismiss() {
        mBtnMenu.setChecked(false);
    }
}
