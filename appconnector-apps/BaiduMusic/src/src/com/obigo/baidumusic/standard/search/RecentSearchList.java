package com.obigo.baidumusic.standard.search;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;

import com.baidu.music.model.Music;
import com.obigo.baidumusic.standard.util.ObigoFile;

public final class RecentSearchList {
    // private static final String TAG = "RecentSearchList";
    private static final String FILE_NAME = "recent_search";

    private LinkedList<Music> mList;

    private static RecentSearchList instance = null;
    private Context mContext;

    public static final int MAX_SIZE = 3;

    public static synchronized RecentSearchList getInstance(Context context) {
        if (null == instance) {
            instance = new RecentSearchList(context);
        }
        return instance;
    }

    private RecentSearchList(Context context) {
        mContext = context.getApplicationContext();
        mList = new LinkedList<Music>();
    }

    List<Music> get() {

        loadList();

        return mList;
    }

    void set(String query) {
        String q = query.trim();

        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).mDescription.equals(q)) {

                if (i == 0) {
                    return;
                } else {
                    Music item = mList.remove(i);
                    mList.addFirst(item);
                    saveList();
                    return;
                }
            }
        }

        Music m = new Music();
        m.mDescription = q;

        mList.addFirst(m);

        if (mList.size() > MAX_SIZE) {
            mList.remove(MAX_SIZE);
        }

        saveList();
    }

    private void saveList() {
        ObigoFile.saveObject(mContext, FILE_NAME, mList);
    }

    @SuppressWarnings("unchecked")
    public void loadList() {
        mList.clear();

        Object obj = ObigoFile.loadObject(mContext, FILE_NAME);

        if (obj != null && (obj instanceof LinkedList<?>)) {
            mList = (LinkedList<Music>) obj;
        }
    }
}
