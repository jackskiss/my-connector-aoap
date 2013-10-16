package com.obigo.baidumusic.standard.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.text.TextUtils;

import com.baidu.music.model.AlbumList;
import com.baidu.music.model.Artist;
import com.baidu.music.model.ArtistList;
import com.baidu.music.model.Channel;
import com.baidu.music.model.Music;
import com.baidu.music.model.MusicList;
import com.baidu.music.model.Radio;
import com.baidu.music.model.RadioList;
import com.baidu.music.model.SearchResult;
import com.baidu.music.model.SearchSuggestion;
import com.baidu.music.model.TopListDescriptionList;
import com.baidu.music.model.Topic;
import com.baidu.music.model.TopicList;
import com.baidu.music.onlinedata.ArtistManager.ArtistListener;
import com.baidu.music.onlinedata.OnlineManagerEngine;
import com.baidu.music.onlinedata.RadioManager.RadioListener;
import com.baidu.music.onlinedata.SearchManager.SearchListener;
import com.baidu.music.onlinedata.TopListManager;
import com.baidu.music.onlinedata.TopListManager.TopListListener;
import com.baidu.music.onlinedata.TopicManager.TopicListener;
import com.baidu.utils.LogUtil;
import com.obigo.baidumusic.standard.util.NetworkChecker;
import com.obigo.baidumusic.standard.util.ObiLog;

public final class ListDownloader implements TopListListener, TopicListener,
        RadioListener, ArtistListener, SearchListener {
    private static final String TAG = "ListDownloader";

    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_FAVORATE = 1;
    public static final int TYPE_THEME = 2;
    public static final int TYPE_RADIO = 3;
    public static final int TYPE_POPULAR = 4;
    public static final int TYPE_BILLBOARD = 5;
    public static final int TYPE_NEW = 6;
    public static final int TYPE_SEARCH = 7;

    public static final int PAGE_SIZE = 100;
    public static final int PAGE_NO = 1;
    public static final int TOPIC_SIZE = 10;
    public static final int ARTIST_NUM = 5;

    /* 5417677 = "T-ara" */
    /* 1497 = "adele" */
    private List<String> mIgnoreChannel = Arrays.asList("5417677", "1497");

    public interface ListDownloadListener {
        void onDownloadChannelList(int type, List<?> list, int selectedChannel);
        void onDownloadTheme(int type, Topic theme);
        void onDownloadMusicList(int type, boolean isSeamless, List<Music> list);
        void onError();
    }
    
    public interface BaiduSearchListener {
        void onDownloadHotArtist(List<Artist> list);
        void onSearchMusic(List<Music> list);
    }

    private Context mContext;
    private OnlineManagerEngine mOnlineManagerEngine;
    private int mType, mPrevType;
    private static ListDownloader mInstance = null;
    private ListDownloadListener mDownloadListener;
    private BaiduSearchListener mSearchListener;

    private List<Channel> mChannelList;
    private List<Music> mMusicList;
    private Topic mTheme;
    private int mSelectedChannel;
    
    private int isInitFavorite = 0;

    private final String[] mTypeString = {"Unknown", "Favorite", "Theme",
            "Radio", "Popular", "Billboard", "New", "Search" };

    public static synchronized ListDownloader getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new ListDownloader(context);
        }

        return mInstance;
    }

    private ListDownloader(Context context) {
        mContext = context.getApplicationContext();
        mOnlineManagerEngine = OnlineManagerEngine.getInstance(mContext);
        mType = TYPE_UNKNOWN;
        mPrevType = TYPE_UNKNOWN;
        mSelectedChannel = -1;
    }

    public void setType(int type) {
        mPrevType = mType;
        mType = type;
    }
    
    // Initialize Favorite by edse
    public int isDownloadingList() {
    	return isInitFavorite;
    }
    
    public void requestInitFavorite(int type) {
    	isInitFavorite++;
    	loadList(type);
    }
    // End

    public void requestList(int type, ListDownloadListener listener) {
        
        if(!NetworkChecker.isConnected(mContext)) {
            return;
        }

        mDownloadListener = listener;

        if (mType == type) { // reuse
            
            if(mType == TYPE_FAVORATE) {
                FavoriteList list = FavoriteList.getInstance(mContext);
                List<Music> items = list.getList();
                
                if(mDownloadListener != null) {
                    mDownloadListener.onDownloadMusicList(mType, true, items);
                }
                return;
            }
            
            if(mMusicList == null) {
                loadList(type);
                return;
            }
            
            if (mType == TYPE_THEME) {
                if (mDownloadListener != null) {
                    mDownloadListener.onDownloadTheme(mType, mTheme);
                }
            } else if (mType == TYPE_RADIO) {
                if (mDownloadListener != null) {
                    mDownloadListener.onDownloadChannelList(type, mChannelList,
                            mSelectedChannel);
                }
            } 

            if (mDownloadListener != null) {
                mDownloadListener.onDownloadMusicList(mType, true, mMusicList);
            }

        } else {
            loadList(type);
        }
    }
    
    private void loadList(int type) {
        mPrevType = mType;
        mType = type;

        ObiLog.startLoad(mTypeString[mType]);

        switch (mType) {
        case TYPE_FAVORATE:
            FavoriteList list = FavoriteList.getInstance(mContext);
            List<Music> items = list.getList();
            
            if(mDownloadListener != null) {
                mDownloadListener.onDownloadMusicList(mType, false, items);
            }
            break;
        case TYPE_THEME:
            mOnlineManagerEngine.getTopicManager(mContext)
                    .getTopicListAsync(mContext, PAGE_NO, TOPIC_SIZE, this);
            break;
        case TYPE_RADIO:
            mOnlineManagerEngine.getRadioManager(mContext)
                    .getRadioListAsync(this);
            break;
        case TYPE_POPULAR:
            mOnlineManagerEngine.getTopListManager(mContext)
                    .getTopListAsync(mContext,
                            TopListManager.EXTRA_TYPE_HITTO_CHINESE_SONGS,
                            PAGE_NO, PAGE_SIZE, this);
            break;
        case TYPE_BILLBOARD:
            mOnlineManagerEngine.getTopListManager(mContext)
                    .getTopListAsync(mContext,
                            TopListManager.EXTRA_TYPE_BILLBOARD_SONGS,
                            PAGE_NO, PAGE_SIZE, this);
            break;
        case TYPE_NEW:
            mOnlineManagerEngine.getTopListManager(mContext)
                    .getTopListAsync(mContext,
                            TopListManager.EXTRA_TYPE_NEW_SONGS, PAGE_NO,
                            PAGE_SIZE, this);
            break;
        case TYPE_SEARCH:
            break;
        default:
            // error case
            break;
        }
    }

    public void requestRadioList(Channel channel) {
        if (mType != TYPE_RADIO) {
            return;
        }

        if (!TextUtils.isEmpty(channel.mChannelId)) {

            ObiLog.startLoad(mTypeString[mType] + "(id : " + channel.mChannelId
                    + ")");

            mOnlineManagerEngine.getRadioManager(mContext)
                    .getPublicChannelAsync(Long.parseLong(channel.mChannelId),
                            PAGE_SIZE, PAGE_NO, this);
        } else if (!TextUtils.isEmpty(channel.mArtistId)) {

            ObiLog.startLoad(mTypeString[mType] + "(id : " + channel.mArtistId
                    + ")");

            mOnlineManagerEngine.getRadioManager(mContext)
                    .getArtistChannelAsync(Long.parseLong(channel.mArtistId),
                            PAGE_SIZE, PAGE_NO, this);
        } else {
            ObiLog.e(TAG, "requestRadioList : error");
        }
    }

    @Override
    public void onGetDescriptionList(TopListDescriptionList arg0) {

    }

    @Override
    public void onGetTopList(MusicList musicList) {
        ObiLog.endLoad(mTypeString[mType]);
        
        if (musicList.getItems() == null) {
    		loadList(TYPE_POPULAR);    		
    		return;
        }

        if (musicList != null && musicList.getItems() != null
                && musicList.getItems().size() > 0) {

            for (int i = 0; i < musicList.getItems().size(); i++) {
                if (musicList.getItems().get(i).mId == null
                        || musicList.getItems().get(i).mId.length() == 0) {
                    musicList.getItems().remove(i);
                    i--;
                }
            }
            mMusicList = musicList.getItems();
        } else {
            mMusicList = null;
        }
        
        if (isInitFavorite > 0) {
        	FavoriteList.getInstance(mContext).addMusic(mMusicList.get(0));
        	FavoriteList.getInstance(mContext).addMusic(mMusicList.get(1));
        	FavoriteList.getInstance(mContext).addMusic(mMusicList.get(2));
        	isInitFavorite--;
        	return;
        }
        
        
        if (mDownloadListener != null) {
            mDownloadListener.onDownloadMusicList(mType, false, mMusicList);
        } else {
            mType = mPrevType;
        }
    }

    @Override
    public void onGetTopicList(TopicList topicList) {
        ObiLog.endLoad(mTypeString[mType]);
        
        if (topicList.getItems() == null) {
    		loadList(TYPE_THEME);
    		return;
        }

        if (topicList != null && topicList.getItems() != null
                && topicList.getItems().size() > 0) {

            Random random = new Random();
            mTheme = topicList.getItems().get(
                    random.nextInt(Math.min(TOPIC_SIZE, topicList.getItems()
                            .size())));

            if (mDownloadListener != null) {
                mDownloadListener.onDownloadTheme(mType, mTheme);
            } else {
                mType = mPrevType;
            }

            ObiLog.startLoad(mTypeString[mType] + "(id : " + mTheme.mCode + ")");

            mOnlineManagerEngine.getTopicManager(mContext).getTopicAsync(
                    mContext, mTheme.mCode, this);
        }
    }

    @Override
    public void onGetTopic(Topic topic) {

        if(topic != null) {
            ObiLog.endLoad(mTypeString[mType] + "(id : " + topic.mCode + ")");
        }

        if (topic != null && topic.mItems != null && topic.mItems.size() > 0) {
            ObiLog.d(TAG, "onGetTopic : " + topic.toString());

            mMusicList = topic.mItems;

        } else {
            mMusicList = null;
        }
        
        if (isInitFavorite > 0) {
        	FavoriteList.getInstance(mContext).addMusic(mMusicList.get(0));
        	FavoriteList.getInstance(mContext).addMusic(mMusicList.get(1));
        	FavoriteList.getInstance(mContext).addMusic(mMusicList.get(2));
        	isInitFavorite--;
        	return;
        }

        if (mDownloadListener != null) {
            mDownloadListener.onDownloadMusicList(mType, false, mMusicList);
        } else {
            mType = mPrevType;
        }
    }

    @Override
    public void onGetRadioList(RadioList radioList) {

        ObiLog.endLoad(mTypeString[mType]);

        ArrayList<Channel> RadioList = new ArrayList<Channel>();

        if (radioList == null) {
            return;
        }

        List<Radio> radioListItems = radioList.getItems();

        if (radioListItems != null) {
            for (Radio item : radioListItems) {
                List<Channel> radioes = item.getItems();
                if (null != radioes && radioes.size() > 0) {
                    RadioList.addAll(radioes);
                }
            }
        }

        if (RadioList.size() > 0) {

            /* mantis issue : 0059186 start */
            for (int i = 0; i < RadioList.size(); i++) {
                for (String id : mIgnoreChannel) {

                    ObiLog.d(TAG, "cId = " + RadioList.get(i).mChannelId
                            + ", aId = " + RadioList.get(i).mArtistId
                            + ", ignoreId = " + id);

                    if (!TextUtils.isEmpty(RadioList.get(i).mChannelId)) {
                        if (RadioList.get(i).mChannelId.equals(id)) {
                            RadioList.remove(i--);
                            ObiLog.e(TAG, "removed channel : case 1");
                            break;
                        }
                    } else if (!TextUtils.isEmpty(RadioList.get(i).mArtistId)) {
                        if (RadioList.get(i).mArtistId.equals(id)) {
                            RadioList.remove(i--);
                            ObiLog.e(TAG, "removed channel : case 2");
                            break;
                        }
                    } else {
                        RadioList.remove(RadioList.get(i--));
                        ObiLog.e(TAG, "removed channel : case 3");
                        break;
                    }
                }
            }
            /* mantis issue : 0059186 end */

            mChannelList = RadioList;

            if (mDownloadListener != null) {
                mDownloadListener.onDownloadChannelList(mType, mChannelList, -1);
            } else {
                mType = mPrevType;
            }
        }
    }

    @Override
    public void onGetArtistChannel(Channel channel) {
        if (channel != null) {
            LogUtil.d(TAG, "RadioArtistChannelData : " + channel.toString());
            ObiLog.endLoad(mTypeString[mType] + "(id : " + channel.mArtistId + ")");
        }

        if (channel != null && channel.mItems != null
                && channel.mItems.size() > 0) {
            mMusicList = channel.mItems;
        } else {
            mMusicList = null;
        }

        if (mDownloadListener != null) {
            mDownloadListener.onDownloadMusicList(mType, false, mMusicList);
        } else {
            mType = mPrevType;
        }
    }

    @Override
    public void onGetPublicChannel(Channel channel) {
        if (channel != null) {
            LogUtil.d(TAG,
                    "onGetPublicChannelListComplete : " + channel.toString());
            ObiLog.endLoad(mTypeString[mType] + "(id : " + channel.mChannelId + ")");
        }

        if (channel != null && channel.mItems != null
                && channel.mItems.size() > 0) {
            mMusicList = channel.mItems;
        } else {
            mMusicList = null;
        }

        if (mDownloadListener != null) {
            mDownloadListener.onDownloadMusicList(mType, false, mMusicList);
        } else {
            mType = mPrevType;
        }
    }

    public void setChannel(int index) {
        mSelectedChannel = index;
    }
    
    public void setOnBaiduSearchListener(BaiduSearchListener listener) {
        mSearchListener = listener;
    }
    
    public void requestHotArtistList() {
        
        if(!NetworkChecker.isConnected(mContext)) {
            return;
        }
        
        mOnlineManagerEngine.getArtistManager(mContext)
            .getHotArtistListAsync(mContext, PAGE_NO, ARTIST_NUM, this);
    }

    @Override
    public void onGetArtist(Artist artist) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onGetArtistAlbumList(AlbumList list) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onGetArtistMusicList(MusicList list) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onGetHotArtistList(ArtistList list) {
        if (null != list && list.getItems() != null
                && list.getItems().size() > 0) {
            
            if(mSearchListener != null) {
                mSearchListener.onDownloadHotArtist(list.getItems());
            }
        }        
    }

    public void searchMusic(String filter) {

        ObiLog.startLoad("Search \'" + filter + "\'");
        
        mOnlineManagerEngine.getSearchManager(mContext)
            .searchMusicAsync(filter, PAGE_NO, PAGE_SIZE, this);
    }

    @Override
    public void onGetSearchSuggestion(SearchSuggestion arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSearchAlbumPicture(String arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSearchArtistAvatar(String arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSearchLyric(String arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSearchMusic(SearchResult searchResult) {

        String query = "";
        
        if (searchResult != null) {
            query = searchResult.mQuery;
        }
        
        ObiLog.endLoad("Search \'" + query + "\'");

        if (searchResult != null && searchResult.mItems != null
                && searchResult.mItems.size() > 0) {
            
            if(mSearchListener != null) {
                mSearchListener.onSearchMusic(searchResult.mItems);
            }
        } else {
            if(mSearchListener != null) {
                mSearchListener.onSearchMusic(null);
            }
        }
    }
}
