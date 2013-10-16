package com.obigo.baidumusic.standard.playlist;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.music.model.Music;
import com.obigo.baidumusic.standard.MusicApplication;
import com.obigo.baidumusic.standard.R;
import com.obigo.baidumusic.standard.player.FavoriteList;
import com.obigo.baidumusic.standard.player.PlayerService;
import com.obigo.baidumusic.standard.util.AsyncImageLoader;
import com.obigo.baidumusic.standard.util.AsyncImageLoader.ImageCallback;
import com.obigo.baidumusic.standard.util.ObiLog;
import com.obigo.baidumusic.standard.util.Util;
import com.obigo.baidumusic.standard.util.ViewCache;
import com.obigo.baidumusic.standard.view.ObigoDialog;

public class FavoriteItemAdapter extends ArrayAdapter<Music> implements
        OnClickListener {
    private static final String TAG = "FavoriteItemAdapter";

    public static final int MODE_PLAY = 0;
    public static final int MODE_EDIT = 1;

    private int mMode;
    private boolean mDelete;

    private Context mContext;
    private List<Music> mItems;
    private int mLayoutID;
    private LayoutInflater mInflater;

    private AsyncImageLoader asyncImageLoader;
    private ListView mListView;

    public FavoriteItemAdapter(Context context, int textViewResourceId,
            List<Music> items, ListView listView) {
        super(context, textViewResourceId, items);

        mContext = context;
        mItems = items;
        mLayoutID = textViewResourceId;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mMode = MODE_PLAY;
        mListView = listView;
        asyncImageLoader = AsyncImageLoader.getInstance();

        mDelete = false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewCache viewCache;

        if (rowView == null) {
            rowView = mInflater.inflate(mLayoutID, null);
            viewCache = new ViewCache(rowView);
            Button btnDelete = viewCache.getButtonView();
            btnDelete.setOnClickListener(this);
            rowView.setTag(viewCache);
        } else {
            viewCache = (ViewCache) rowView.getTag();
        }
        viewCache.getButtonView().setTag(Integer.valueOf(position));

        Music item = mItems.get(position);

        String title = (item.mTitle == null || item.mTitle.trim().isEmpty()) ? "no title"
                : item.mTitle;
        String artist = (item.mArtist == null || item.mArtist.trim().isEmpty()) ? "no name"
                : item.mArtist;
        TextView textView = viewCache.getTextView();
        textView.setText(String.format("%s - %s", title, artist));
        textView.setSelected(true);

        String imageUrl = TextUtils.isEmpty(item.mPicSmall) ? item.mPicBig
                : item.mPicSmall;
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            imageUrl = Util.imageUrlCheck(imageUrl);

            ImageView imageView = viewCache.getImageView();
            imageView.setTag(imageUrl);

            Drawable cachedImage = asyncImageLoader.loadDrawable(imageUrl,
                    new ImageCallback() {
                        public void imageLoaded(Drawable imageDrawable,
                                String imageUrl) {
                            
                            ImageView imageViewByTag = null;
                            View v = mListView.findViewWithTag(imageUrl);
                            
                            if(v instanceof ImageView) {
                                imageViewByTag = (ImageView)v;
                            }
                            
                            if (imageViewByTag != null) {
                                if(imageDrawable != null) {
                                    imageViewByTag.setImageDrawable(imageDrawable);
                                } else {
                                    imageViewByTag.setImageResource(R.drawable.img_none);
                                }
                            }
                        }
                    });

            if (cachedImage == null) {
                imageView.setImageResource(R.drawable.img_none);
            } else {
                imageView.setImageDrawable(cachedImage);
            }
        } else {
            ImageView imageView = viewCache.getImageView();
            imageView.setTag(imageUrl);
            imageView.setImageResource(R.drawable.img_none);
        }
        try {
            if (MusicApplication.getPlayer().getCurrentPosition() == position) {
                rowView.setBackgroundResource(R.drawable.bg_list_on);
            } else {

                rowView.setBackgroundResource(R.drawable.bg_list_off);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mMode == MODE_PLAY) {
            viewCache.getButtonView().setVisibility(View.GONE);
        } else {
            viewCache.getButtonView().setVisibility(View.VISIBLE);
            viewCache.getButtonView().setEnabled(!mDelete);
        }

        return rowView;
    }

    @SuppressWarnings("static-access")
    @Override
    public void onClick(View view) {

        mDelete = true;
        notifyDataSetChanged();

        final int position = (Integer) view.getTag();

        ObigoDialog
                .builder((FavoriteListActivity) mContext,
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                int currentItemPos = 0;
                                int deleteItemPos = 0;
                                if (v.getId() == R.id.button_left) {
                                    try {
                                        Music item = mItems.get(position);
                                        deleteItemPos = position;

                                        PlayerService player = MusicApplication
                                                .getPlayer();
                                        Music playingItem = player
                                                .getCurrentMusic();
                                        currentItemPos = player.getCurrentPosition();
                                        FavoriteList.getInstance(mContext).removeMusic(
                                                item.mId);
                                        player.removeMusic(position);

                                        if (playingItem != null
                                                && playingItem.mId
                                                        .equals(item.mId)) {
                                            player.stop();

                                            if (mItems.size() > 0) {
                                                player.preparePlay(position);
                                            } else {
                                                ((FavoriteListActivity) mContext)
                                                        .initDetail();
                                            }
                                        }
                                    } catch (Exception e) {
                                        ObiLog.e(TAG, "removeMusic error : ", e);
                                    }
                                }

                                mDelete = false;
                                notifyDataSetChanged();
                                try {
                                    PlayerService player = MusicApplication.getPlayer();

                                    if (currentItemPos > deleteItemPos)
                                        player.setPlayPosition(currentItemPos - 1);
                                    else if (currentItemPos == deleteItemPos) {
                                        player.setPlayPosition(currentItemPos);
                                    }
                                } catch (Exception e) {
                                    ObiLog.e(TAG, "removeMusic error : ", e);
                                }
                                
                                ((FavoriteListActivity) mContext)
                                        .updateFavoriteListUI();
                            }
                        }).setButtons(R.string.btn_yes, R.string.btn_no)
                .setTitleContent(R.string.warning, R.string.delete_one_warning)
                .show();
    }

    /* End Modification 2013-05-07-005 */

    public void upToTop(int pos) {

        Music item = mItems.remove(pos);
        mItems.add(0, item);

        notifyDataSetChanged();
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    public int getMode() {
        return mMode;
    }
}
