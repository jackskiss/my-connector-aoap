package com.obigo.baidumusic.standard.player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.baidu.music.model.Music;
import com.obigo.baidumusic.standard.util.ObigoFile;

public final class FavoriteList {
    // private static final String TAG = "FavoriteList";
    private static final String FILE_NAME = "favorite";
    public static final int MAX_SIZE = 50;

    private LinkedList<Music> mMusicList;
    private Map<String, Music> mMusicMap;

    private static FavoriteList instance = null;
    private Context mContext;

    public static synchronized FavoriteList getInstance(Context context) {
        if (null == instance) {
            instance = new FavoriteList(context);
        }
        return instance;
    }

    // Comparator<Music> c = new Comparator<Music>() {
    //
    // @Override
    // public int compare(Music m1, Music m2) {
    // if(m1.mId.equals(m2.mId)) {
    // return 0;
    // }
    //
    // return -1;
    // }
    //
    // };

    private FavoriteList(Context context) {
        mContext = context.getApplicationContext();
        
        mMusicList = new LinkedList<Music>();
        mMusicMap = new HashMap<String, Music>();
    }

    public int size() {
        return mMusicList.size();
    }

    public boolean addMusic(Music music) {

        if (mMusicList.size() < MAX_SIZE) {
            mMusicList.addFirst(music);
            mMusicMap.put(music.mId, music);
            saveMusicList();
            return true;
        }

        return false;
    }

    public void removeLast() {
        if (mMusicList.size() == MAX_SIZE) {
            Music m = mMusicList.removeLast();
            mMusicMap.remove(m.mId);
            saveMusicList();
        }
    }

    public void upToTop(int index) {
        Music music = mMusicList.remove(index);
        mMusicList.addFirst(music);

        saveMusicList();
    }

    public void removeMusic(String id) {

        Music m = mMusicMap.remove(id);
        mMusicList.remove(m);
        saveMusicList();
    }

    public Music getMusicByIndex(int index) {
        return mMusicList.get(index);
    }

    public Music getMusicById(String id) {
        return mMusicMap.get(id);
    }

    public List<Music> getList() {
        return mMusicList;
    }

    public boolean contains(Music music) {

        if (mMusicMap.containsKey(music.mId)) {
            return true;
        }

        return false;
    }

    public void saveMusicList() {
        ObigoFile.saveObject(mContext, FILE_NAME, mMusicList);
    }

    @SuppressWarnings("unchecked")
    public void loadMusicList() {
        mMusicList.clear();
        mMusicMap.clear();

        Object obj = ObigoFile.loadObject(mContext, FILE_NAME);
        
        // Initialize Favorite
//        if (obj == null) {
//        	ListDownloader downloader = ListDownloader.getInstance(mContext);
//            downloader.requestInitFavorite(ListDownloader.TYPE_THEME);
//            downloader.requestInitFavorite(ListDownloader.TYPE_POPULAR);
//        }
//        

        if (obj != null && (obj instanceof LinkedList<?>)) {
            mMusicList = (LinkedList<Music>) obj;

            for (Music m : mMusicList) {
                mMusicMap.put(m.mId, m);
            }
        }
    }

    public void removeAllMusic() {
        mMusicList.clear();
        mMusicMap.clear();
    }
}