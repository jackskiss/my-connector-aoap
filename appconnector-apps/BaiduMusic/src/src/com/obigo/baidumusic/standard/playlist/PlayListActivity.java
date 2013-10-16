package com.obigo.baidumusic.standard.playlist;

import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.music.model.Album;
import com.baidu.music.model.AlbumList;
import com.baidu.music.model.Music;
import com.baidu.music.onlinedata.AlbumManager.AlbumListener;
import com.baidu.music.onlinedata.OnlineManagerEngine;
import com.baidu.utils.TextUtil;
import com.obigo.baidumusic.standard.BaseActivity;
import com.obigo.baidumusic.standard.MusicApplication;
import com.obigo.baidumusic.standard.MusicApplication.RegulationListener;
import com.obigo.baidumusic.standard.R;
import com.obigo.baidumusic.standard.player.FavoriteList;
import com.obigo.baidumusic.standard.player.ListDownloader;
import com.obigo.baidumusic.standard.player.PlayerService;
import com.obigo.baidumusic.standard.util.AsyncImageLoader;
import com.obigo.baidumusic.standard.util.AsyncImageLoader.ImageCallback;
import com.obigo.baidumusic.standard.util.ObiLog;
import com.obigo.baidumusic.standard.util.Preference;
import com.obigo.baidumusic.standard.util.Util;
import com.obigo.baidumusic.standard.view.CategoryView;
import com.obigo.baidumusic.standard.view.ObigoDialog;

public abstract class PlayListActivity extends BaseActivity implements
        OnItemClickListener, OnClickListener, AlbumListener, ImageCallback {
    private static String TAG = "PlaylistActivity";

    public static final String LIST_TYPE = "list_type";
    public static final String REGULATION = "regulation";

    private int mListType;

    private ListView mListView;
    private Button mBtnShuffle, mBtnPlay, mBtnPrev, mBtnNext, mBtnLoop, mBtnBack,
            mBtnFavorite;
    private TextView mTvHeader, mTvTitle, mTvArtist, mTvTime;
    private ImageView mIvAlbumArt;
    private ProgressBar mImgLoading;
    private CategoryView mCategory;
    
    protected boolean isLoadingList = false;
    
    public static final int BUTTON_DELAY = 500;

    //private ObigoProgressDialog mLoadingDialog = null;

    private AsyncImageLoader mAsyncImageLoader;

    protected int getListType() {
        return mListType;
    }

    protected boolean isRegulation() {
        return MusicApplication.isRegulation();
    }

    protected ListView getListView() {
        return mListView;
    }

    protected Button getFavoriteButton() {
        return mBtnFavorite;
    }

    protected Button getLoopButton() {
        return mBtnLoop;
    }

    protected Button getShuffleButton() {
        return mBtnShuffle;
    }

    protected Button getPrevButton() {
        return mBtnPrev;
    }

    protected Button getNextButton() {
        return mBtnNext;
    }

    protected Button getPlayButton() {
        return mBtnPlay;
    }

    private ViewChangeListener mViewChangeListener = null;

    interface ViewChangeListener {
        void onViewChanged(boolean isRegulation);
    }

    public void setOnViewChangeListener(ViewChangeListener listener) {
        mViewChangeListener = listener;
    }
    
    private boolean mRedraw = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mListType = intent.getIntExtra(LIST_TYPE, ListDownloader.TYPE_FAVORATE);

        mAsyncImageLoader = AsyncImageLoader.getInstance();

        if (isRegulation()) {
            setRegulationView();
        } else {
            setNormalView();
        }
    }

    private void setNormalView() {
        setContentView(R.layout.playlist_activity);
        
        try {
            mListView = findListView(R.id.list);
            mBtnShuffle = findButton(R.id.shuffle);
            mBtnPlay = findButton(R.id.play);
            mBtnPrev = findButton(R.id.prev);
            mBtnNext = findButton(R.id.next);
            mBtnLoop = findButton(R.id.loop);
            mBtnBack = findButton(R.id.back);
            mBtnFavorite = findButton(R.id.favorite);

            mTvHeader = findTextView(R.id.header_title);
            mTvTitle = findTextView(R.id.title);
            mTvArtist = findTextView(R.id.artist);
            mTvTime = findTextView(R.id.time);

            mIvAlbumArt = findImageView(R.id.thumb);
            
            mImgLoading = findProgressBar(R.id.img_loading);
            mImgLoading.setVisibility(View.INVISIBLE);
            
            mBtnPlay.setOnClickListener(this);
            mBtnPrev.setOnClickListener(this);
            mBtnNext.setOnClickListener(this);
            mBtnBack.setOnClickListener(this);
            mBtnShuffle.setOnClickListener(this);
            mBtnLoop.setOnClickListener(this);
            mBtnFavorite.setOnClickListener(this);

            mListView.setOnItemClickListener(this);

            setHeader(mListType);

            setShuffleButton();
            setLoopButton();
        } catch (Exception e) {
            ObiLog.e(TAG, "findView error");
        }

        getPlaylist();

        if (mViewChangeListener != null) {
            mViewChangeListener.onViewChanged(false);
        }
    }

    private void setRegulationView() {
        setContentView(R.layout.regulation_activity);
        
        mRedraw = false;

        try {
            mImgLoading = findProgressBar(R.id.img_loading);
            mImgLoading.setVisibility(View.INVISIBLE);
    
            mBtnPlay = findButton(R.id.play);
            mBtnPrev = findButton(R.id.prev);
            mBtnNext = findButton(R.id.next);
            mBtnBack = findButton(R.id.back);
            mBtnFavorite = findButton(R.id.favorite);
    
            mTvHeader = findTextView(R.id.header_title);
    
            mTvTitle = findTextView(R.id.title);
            mTvArtist = findTextView(R.id.artist);
    
            mIvAlbumArt = findImageView(R.id.thumb);
    
            mCategory = findCategoryView(R.id.category);
            mCategory.setCategory(mListType);
    
            mBtnPlay.setOnClickListener(this);
            mBtnPrev.setOnClickListener(this);
            mBtnNext.setOnClickListener(this);
            mBtnBack.setOnClickListener(this);
            mBtnFavorite.setOnClickListener(this);
        } catch (Exception e) {
            ObiLog.e(TAG, "findView error");
        }

        mTvHeader.setText(R.string.driving);

        getPlaylist();

        if (mViewChangeListener != null) {
            mViewChangeListener.onViewChanged(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        if(mRedraw) {
            if (isRegulation()) {
                setRegulationView();
            } else {
                setNormalView();
            }
            
            mRedraw = false;
        }

        MusicApplication.setOnRegulationListener(new RegulationListener() {

            @Override
            public void onChangedRegulationMode(boolean isRegulation) {
                if (isRegulation) {
                    setRegulationView();
                } else {
                    setNormalView();
                }
            }

        });

        try {
            getPlayer().requestAudioFocus();
        } catch (Exception e) {
            ObiLog.e(TAG, "onResume::getPlayer error : ", e);
        }

        setShuffleButton();
        setLoopButton();
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        mRedraw = true;

        MusicApplication.setOnRegulationListener(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parentView, View clickedView,
            int position, long id) {
        try {
            getPlayer().stop();
            getPlayer().preparePlay(position);
            play();
        } catch (Exception e) {
            ObiLog.e(TAG, "getPlayer error : ", e);
        }
    }

    private void saveWidgetThumbnail(BitmapDrawable drawable) {
        Bitmap bitmap = drawable.getBitmap();
               
        String p = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/baidumusic/" + UUID.randomUUID().toString();
        
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(p);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
            Preference.putString(this, Preference.KEY_THUMBNAIL, p);
            getPlayer().broadcastInfo(false);
        } catch (Exception e) {
            ObiLog.e(TAG, "saveWidgetThumbnail : ", e);
        } finally {
           try {
               out.close();
           } catch (Exception e) {
               ObiLog.e(TAG, "saveWidgetThumbnail : ", e);
           }
        }
    }

    protected void requestAlbumArt(String path) {

        if (path != null && !TextUtil.isEmpty(path.trim())) {
            String prevPath = (String) mIvAlbumArt.getTag();

            path = Util.imageUrlCheck(path);

            if (path.equals(prevPath)) {
                saveWidgetThumbnail((BitmapDrawable) mIvAlbumArt.getDrawable());
                return;
            }

            loadThumbnail(path.trim());
        } else {
            mIvAlbumArt.setImageDrawable(getResources().getDrawable(
                    R.drawable.img_none_big));
        }
    }

    protected void requestAlbumArt(Music music) {
        String imageUrl = TextUtils.isEmpty(music.mPicSmall) ? music.mPicBig
                : music.mPicSmall;

        if (imageUrl != null && !TextUtils.isEmpty(imageUrl.trim())) {

            String prevPath = (String) mIvAlbumArt.getTag();

            imageUrl = Util.imageUrlCheck(imageUrl);

            if (imageUrl.equals(prevPath)) {
                saveWidgetThumbnail((BitmapDrawable) mIvAlbumArt.getDrawable());
                return;
            }

            mIvAlbumArt.setImageDrawable(getResources().getDrawable(
                    R.drawable.img_none_big));

            loadThumbnail(imageUrl.trim());
        } else {
            mIvAlbumArt.setImageDrawable(getResources().getDrawable(
                    R.drawable.img_none_big));

            if (!TextUtils.isEmpty(music.mAlbumId)) {
                try {
                    int id = Integer.parseInt(music.mAlbumId);
                    OnlineManagerEngine.getInstance(getApplicationContext())
                            .getAlbumManager(this)
                            .getAlbumAsync(this, id, this);
                } catch (Exception e) {
                    ObiLog.e(TAG, "requestAlbumArt :", e);
                    mIvAlbumArt.setImageDrawable(getResources().getDrawable(
                            R.drawable.img_none_big));
                }
            } else {
                mIvAlbumArt.setImageDrawable(getResources().getDrawable(
                        R.drawable.img_none_big));
            }
        }
    }

    private void loadThumbnail(String path) {
        mImgLoading.setVisibility(View.VISIBLE);

        mIvAlbumArt.setTag(path);
        // ImageManager.render(path, mIvAlbumArt, ALBUMART_SIZE, ALBUMART_SIZE,
        // 1, true, true);
        // ImageManager.request(path, this, ALBUMART_SIZE, ALBUMART_SIZE, 1,
        // true, true);

        
        Drawable drawable = null;
        
        if(mAsyncImageLoader != null) {
            drawable = mAsyncImageLoader.loadDrawable(path.trim(), this);
        }

        if (drawable == null) {
            mIvAlbumArt.setImageDrawable(getResources().getDrawable(
                    R.drawable.img_none_big));
        } else {
            mIvAlbumArt.setImageDrawable(drawable);
            saveWidgetThumbnail((BitmapDrawable) drawable);
            mImgLoading.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void imageLoaded(Drawable imageDrawable, String imageUrl) {

        if (imageDrawable != null
                && imageUrl.equals((String) mIvAlbumArt.getTag())) {
            mIvAlbumArt.setImageDrawable(imageDrawable);
            
            if(imageDrawable instanceof BitmapDrawable) {
                saveWidgetThumbnail((BitmapDrawable) imageDrawable);
            }
        } else {
            mIvAlbumArt.setImageDrawable(getResources().getDrawable(
                    R.drawable.img_none_big));
        }

        mImgLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setShuffleButton() {
        if (!isRegulation()) {
            try {
                if (getPlayer().getStateShuffle()) {
                    mBtnShuffle
                            .setBackgroundResource(R.drawable.selector_btn_shuffleon);
                } else {
                    mBtnShuffle
                            .setBackgroundResource(R.drawable.selector_btn_shuffleoff);
                }
            } catch (Exception e) {
                ObiLog.e(TAG, "setShuffleButton error : ", e);
            }
        }
    }

    private void setLoopButton() {
        if (!isRegulation()) {
            try {
                int loop = getPlayer().getStateLoop();

                switch (loop) {
//            case PlayerService.LOOP_NONE:
//                mBtnLoop.setBackgroundResource(R.drawable.selector_btn_repeatno);
//                break;
                case PlayerService.LOOP_ONE:
                    mBtnLoop.setBackgroundResource(R.drawable.selector_btn_repeat1);
                    break;
                case PlayerService.LOOP_ALL:
                    mBtnLoop.setBackgroundResource(R.drawable.selector_btn_repeatall);
                    break;

                default:
                    break;
                }
            } catch (Exception e) {
                ObiLog.e(TAG, "setLoopButton error : ", e);
            }
        }
    }

    protected PlayerService getPlayer() throws Exception {
        return MusicApplication.getPlayer();
    }

    protected boolean play() {
        try {
            getPlayer().play();
        } catch (Exception e) {
            ObiLog.e(TAG, "play() error : ", e);
            return false;
        }
        
        return true;
    }

    protected void showLoading() {
        View dialog = findViewById(R.id.loading);
        dialog.setVisibility(View.VISIBLE);
        
        View image = dialog.findViewById(R.id.loadingImageView);
        if (image instanceof ImageView) {
            final ImageView loadingImage = (ImageView) image;

            loadingImage.post(new Runnable() {
                public void run() {
                    AnimationDrawable animationDrawable = (AnimationDrawable)loadingImage.getBackground();
                    animationDrawable.start();
                }
            });
        }
        
//        if (mLoadingDialog == null) {
//            mLoadingDialog = ObigoProgressDialog.createDialog(this);
//            mLoadingDialog.setMessage(getResources()
//                    .getString(R.string.loading));
//            mLoadingDialog.setCancelable(false);
//            mLoadingDialog.getButton().setOnClickListener(this);
//        }
//
//        mLoadingDialog.show();
    }

    protected void hideLoading() {
        View dialog = findViewById(R.id.loading);
        dialog.setVisibility(View.GONE);
//        if (mLoadingDialog != null) {
//            mLoadingDialog.dismiss();
//            mLoadingDialog = null;
//        }
    }

    public abstract void getPlaylist();

    public void setHeader(String title) {
        mTvHeader.setText(title);
    }

    public void setHeader(int listType) {
        int drawableId = 0;
        int stringId = 0;

        switch (listType) {
        case ListDownloader.TYPE_FAVORATE:
            drawableId = R.drawable.ico_favorite;
            stringId = R.string.favorite;
            break;
        case ListDownloader.TYPE_THEME:
            drawableId = R.drawable.ico_theme;
            stringId = R.string.theme;
            break;
        case ListDownloader.TYPE_RADIO:
            drawableId = R.drawable.ico_radio;
            stringId = R.string.radio;
            break;
        case ListDownloader.TYPE_POPULAR:
            drawableId = R.drawable.ico_popular;
            stringId = R.string.popular;
            break;
        case ListDownloader.TYPE_BILLBOARD:
            drawableId = R.drawable.ico_billboard;
            stringId = R.string.billboard;
            break;
        case ListDownloader.TYPE_NEW:
            drawableId = R.drawable.ico_new;
            stringId = R.string.newsong;
            break;
        case ListDownloader.TYPE_SEARCH:
            drawableId = R.drawable.ico_search;
            stringId = R.string.search;
            break;
        default:
            return;
        }

        mTvHeader.setText(stringId);
        mTvHeader.setCompoundDrawablePadding(10);
        mTvHeader.setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0);
    }

    @Override
    public void onClick(View view) {

        PlayerService player = null;
        try {
            player = getPlayer();
        } catch (Exception e) {
            ObiLog.e(TAG, "getPlayer error : ", e);
            return;
        }

        switch (view.getId()) {
        case R.id.play:
            buttonDelay(mBtnPlay);
            int state = player.getPlayerStatus();

            switch (state) {
            case PlayerService.STATUS_STOPPED:
                if(play()) {
                    this.playAniShow(true);
                } else {
                    this.playAniShow(false);
                }
                break;
            case PlayerService.STATUS_PLAYING:
                this.playAniShow(false);
                player.pause();
                break;
            case PlayerService.STATUS_PAUSED:
                if(player.resume()) {
                    this.playAniShow(true);
                } else {
                    this.playAniShow(false);
                }
                break;
            default:
                break;
            }
            break;
        case R.id.prev:
            buttonDelay(mBtnPrev);
            player.movePrev(false);
            break;
        case R.id.next:
            buttonDelay(mBtnNext);
            player.moveNext(false);
            break;
        case R.id.back:
            this.finish();
            break;
        case R.id.shuffle:
            buttonDelay(mBtnShuffle);
            player.setStateShuffle(!player.getStateShuffle());
            setShuffleButton();
            break;
        case R.id.loop:
            buttonDelay(mBtnLoop);
            player.setStateLoop((player.getStateLoop() + 1)
                    % PlayerService.LOOP_MAX);
            setLoopButton();
            break;
        case R.id.favorite:
            if (!isContainFavorite()) {
                if (FavoriteList.getInstance(this).size() == FavoriteList.MAX_SIZE) {
                    ObigoDialog.builder(this, new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (v.getId() == R.id.button_left) {
                                FavoriteList.getInstance(PlayListActivity.this).removeLast();
                                addFavorite();
                            }
                        }
                    });
                    ObigoDialog.setButtons(R.string.btn_yes, R.string.btn_no);
                    Music m = FavoriteList.getInstance(this).getMusicByIndex(
                            FavoriteList.MAX_SIZE - 1);
                    String s = String.format(
                            getResources().getString(
                                    R.string.delete_last_and_add), m.mTitle);
                    ObigoDialog.setTitleContent(
                            getResources().getString(R.string.warning), s);

                    ObigoDialog.show();
                } else {
                    addFavorite();
                }
            } else {
                removeFavorite();
            }
            break;
        default:
            break;
        }

    }
    
    private void buttonDelay(final Button button) {
        button.setEnabled(false);
        Timer t = new Timer();
        t.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setEnabled(true);
                    }
                });
            }
            
        }, BUTTON_DELAY);
    }

    public void playAniShow(boolean isPlay) {
        if (isPlay) {
            mBtnPlay.setBackgroundResource((isRegulation()) ? R.drawable.selector_btn_big_stop
                    : R.drawable.selector_btn_stop);
        } else {
            mBtnPlay.setBackgroundResource((isRegulation()) ? R.drawable.selector_btn_big_play
                    : R.drawable.selector_btn_play);
        }
    }

    public void favBtnStatusSwitch(Music music) {

        if (FavoriteList.getInstance(this).contains(music)) {
            mBtnFavorite
                    .setBackgroundResource((isRegulation()) ? R.drawable.btn_big_favorite_s
                            : R.drawable.btn_favorite_s);
        } else {
            mBtnFavorite
                    .setBackgroundResource((isRegulation()) ? R.drawable.selector_btn_big_favorite
                            : R.drawable.selector_btn_favorite);
        }
    }

    private boolean isContainFavorite() {
        try {
            if (FavoriteList.getInstance(this).contains(
                    getPlayer().getCurrentMusic())) {
                return true;
            }
        } catch (Exception e) {
            ObiLog.e(TAG, "addMusic error : ", e);
        }

        return false;
    }

    private void addFavorite() {

        try {
            if (FavoriteList.getInstance(this).addMusic(
                    getPlayer().getCurrentMusic())) {
                mBtnFavorite
                        .setBackgroundResource((isRegulation()) ? R.drawable.btn_big_favorite_s
                                : R.drawable.btn_favorite_s);

                // ObigoDialog.Builder(this, null);
                // ObigoDialog.setButton(R.string.btn_ok);
                // ObigoDialog.setTitleContent(R.string.information,
                // R.string.added_to_favorite);
                // ObigoDialog.show();
            }
        } catch (Exception e) {
            ObiLog.e(TAG, "addMusic error : ", e);
        }
    }

    private void removeFavorite() {
        try {
            FavoriteList.getInstance(this).removeMusic(
                    getPlayer().getCurrentMusic().mId);
            mBtnFavorite
                    .setBackgroundResource((isRegulation()) ? R.drawable.selector_btn_big_favorite
                            : R.drawable.selector_btn_favorite);
        } catch (Exception e) {
            ObiLog.e(TAG, "removeMusic error : ", e);
        }
    }

    protected void setListPosition(final int position) {
        if (!isRegulation()) {
            ((ArrayAdapter<?>) mListView.getAdapter()).notifyDataSetChanged();

            if (!(position >= mListView.getFirstVisiblePosition() - 1 && position <= mListView
                    .getLastVisiblePosition() + 1)) {
                mListView.setSelection(position);
            } else {
                mListView.smoothScrollToPosition(position);
            }
        }
    }

    protected void setProgressTime() {
        if (isRegulation()) {
            return;
        }
        
        try {
            if(getPlayer().getPlayerStatus() == PlayerService.STATUS_STOPPED) {
                Spanned str = Html.fromHtml("<font color=#ffffff>00:00</font> / <font color=#ffffff>00:00</font>");
                mTvTime.setText(str);
                return;
            }
        } catch (Exception e) {
            ObiLog.e(TAG, "setProgressTime ", e);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isRegulation()) {
                    return;
                }

                long position = 0L;
                long duration = 0L;

                try {
                    position = getPlayer().position();
                    duration = getPlayer().duration();
                } catch (Exception e) {
                    ObiLog.e(TAG, "setProgressTime", e);
                }

                Spanned str;

                if (duration > 0L) {
                    str = Html.fromHtml("<font color=#ffffff>"
                            + Util.formatDuration(position)
                            + "</font> / <font color=#ffffff>"
                            + Util.formatDuration(duration) + "</font>");
                } else {
                    str = Html
                            .fromHtml("<font color=#ffffff>00:00</font> / <font color=#ffffff>00:00</font>");
                }

                mTvTime.setText(str);
            }
        });
    }

    @Override
    public void onGetAlbum(Album album) {
        String imageUrl = TextUtils.isEmpty(album.mPicSmall) ? album.mPicBig
                : album.mPicSmall;

        if (imageUrl != null && !TextUtil.isEmpty(imageUrl.trim())) {

            try {
                Music item = getPlayer().getCurrentMusic();
                item.mPicBig = imageUrl;
            } catch (Exception e) {
                ObiLog.e(TAG, "setProgressTime", e);
            }

            imageUrl = Util.imageUrlCheck(imageUrl);

            loadThumbnail(imageUrl.trim());
        }
    }

    @Override
    public void onGetAlbumList(AlbumList arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetNewAlbumList(AlbumList arg0) {
        // TODO Auto-generated method stub

    }

    public void setTitle(String title) {
        mTvTitle.setText((title == null || title.trim().isEmpty()) ? "no title"
                : title);
        mTvTitle.setSelected(true);
    }

    public void setArtist(String artist) {
        mTvArtist
                .setText((artist == null || artist.trim().isEmpty()) ? "no name"
                        : artist);
        mTvArtist.setSelected(true);
    }

}
